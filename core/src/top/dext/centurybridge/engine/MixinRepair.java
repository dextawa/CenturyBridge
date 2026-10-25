package top.dext.centurybridge.engine;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;
import org.objectweb.asm.ClassReader;
import org.objectweb.asm.ClassWriter;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.Type;
import org.objectweb.asm.tree.AbstractInsnNode;
import org.objectweb.asm.tree.AnnotationNode;
import org.objectweb.asm.tree.ClassNode;
import org.objectweb.asm.tree.FieldInsnNode;
import org.objectweb.asm.tree.IincInsnNode;
import org.objectweb.asm.tree.IntInsnNode;
import org.objectweb.asm.tree.LdcInsnNode;
import org.objectweb.asm.tree.LocalVariableNode;
import org.objectweb.asm.tree.MethodInsnNode;
import org.objectweb.asm.tree.MethodNode;
import org.objectweb.asm.tree.ParameterNode;
import org.objectweb.asm.tree.TypeInsnNode;
import org.objectweb.asm.tree.VarInsnNode;

/**
 * Injector repair against the target's REAL method bodies.
 *
 * Triage judges a mixin from names and the stub: the target method exists, so
 * the injector "survives". But an injector also needs its anchor -- the
 * constant, call, field access or allocation it hooks -- inside that body, and
 * Mixin enforces it ({@code require} >= 1 is a hard crash at apply). Releases
 * routinely move an anchor one call down (1.20.5 split PacketByteBuf.readNbt()
 * into a forwarder plus a static helper holding the 2097152L limit) or change
 * a constructor's descriptor under an {@code <init>*} injector that never
 * looks at the arguments. Both are repairable without touching the mod's
 * logic:
 *
 *   desc-drop  the pinned target descriptor is gone, the handler captures no
 *              target arguments: select by name (all overloads / ctors)
 *   descend    the anchor left the target but sits in exactly one method the
 *              target calls on its own class: retarget there; a static callee
 *              takes a static handler, so an instance handler that never reads
 *              {@code this} is made static
 *   ordinal-remap  an @At / @Constant pinned to the n-th occurrence of its anchor
 *              in a target V and W both declare: the ordinal becomes that of the
 *              occurrence's counterpart in W's body ({@link SiteMatch}, matched by
 *              context, needs {@link #source}); with no unique counterpart it is
 *              left as it stands and reported
 *
 * Whatever stays unrepairable with an effective require >= 1 is reported as
 * fatal with the reason, so the caller strips it instead of shipping a crash.
 * Injectors whose anchor cannot be evaluated (slices, MixinExtras expressions,
 * custom injection points) are left alone: unknown is not evidence of death.
 */
public final class MixinRepair {
    private static final Gson GSON = new Gson();
    private static final String MIXIN = "Lorg/spongepowered/asm/mixin/Mixin;";
    private static final String INJECT = "Lorg/spongepowered/asm/mixin/injection/Inject;";
    private static final String CI = "Lorg/spongepowered/asm/mixin/injection/callback/CallbackInfo;";
    private static final String CIR = "Lorg/spongepowered/asm/mixin/injection/callback/CallbackInfoReturnable;";

    private final ZipFile code;
    private final Map<String, ClassNode> cache = new HashMap<>();
    /** the step's source release (V) code, for ordinal-remap; null: ordinals are not remapped */
    public MixinRepair source;
    /**
     * a descriptor as the converted jar will spell it (the step's classRenames, Chain.renamedDesc): a
     * selector written with a class the step renames names the W method of the renamed shape
     */
    public java.util.function.UnaryOperator<String> renamed = d -> d;

    /** disabled: injectors switched to require=0 because their anchor is gone -- a KNOWN loss, reported */
    public record Outcome(Set<String> fatal, Map<String, String> fatalWhy, List<String> repairs, List<String> disabled) {}

    public MixinRepair(Path codeJar) throws IOException {
        this.code = new ZipFile(codeJar.toFile());
    }

    /** every class of the code jar under its superclass (headers only), built on first use */
    private Map<String, List<String>> subIndex;

    /** the classes of the code jar extending c, directly or not */
    public Set<String> subclasses(String c) {
        if (subIndex == null) {
            subIndex = new HashMap<>();
            for (ZipEntry e : java.util.Collections.list(code.entries())) {
                if (!e.getName().endsWith(".class")) continue;
                try {
                    ClassReader r = new ClassReader(code.getInputStream(e).readAllBytes());
                    if (r.getSuperName() != null) subIndex.computeIfAbsent(r.getSuperName(), k -> new ArrayList<>()).add(r.getClassName());
                } catch (IOException | RuntimeException ex) {
                    // an unreadable entry extends nothing we can name
                }
            }
        }
        Set<String> out = new LinkedHashSet<>();
        java.util.ArrayDeque<String> todo = new java.util.ArrayDeque<>(List.of(aliasOf.getOrDefault(c, c)));
        while (!todo.isEmpty()) {
            for (String k : subIndex.getOrDefault(todo.pop(), List.of())) {
                if (out.add(k)) todo.push(k);
            }
        }
        if (aliasRenames.isEmpty()) return out;
        Set<String> named = new LinkedHashSet<>();
        for (String k : out) named.add(aliasRenames.getOrDefault(k, k));
        return named;
    }

    /** every class of the code jar (bodies, no frames), in jar order; unreadable entries skipped */
    public void forEachClass(java.util.function.Consumer<ClassNode> f) {
        for (ZipEntry e : java.util.Collections.list(code.entries())) {
            if (!e.getName().endsWith(".class")) continue;
            try {
                ClassNode cn = new ClassNode();
                new ClassReader(code.getInputStream(e).readAllBytes()).accept(cn, ClassReader.SKIP_FRAMES);
                f.accept(cn);
            } catch (IOException | RuntimeException ex) {
                // an unreadable entry holds no method to compare
            }
        }
    }

    /** does the code jar declare the class (its entry only, nothing parsed) */
    public boolean declares(String owner) {
        return cache.get(owner) != null || code.getEntry(owner + ".class") != null || layerEntry(owner) != null;
    }

    /**
     * The step's layer jar, for CenturyBridge's own classes (a facade a mixin follows its target's class
     * rename to, Chain.mixinFollow): W's code model only. Null: none at hand.
     */
    private ZipFile layer;

    public void layer(Path jar) throws IOException {
        this.layer = new ZipFile(jar.toFile());
    }

    private ZipEntry layerEntry(String owner) {
        return layer != null && owner.startsWith("top/dext/") ? layer.getEntry(owner + ".class") : null;
    }

    /**
     * V's classes under the names a step's class renames give them where W adds that class in their
     * place (Chain.mixinFollow, read back: new name -> V name): a mixin following its target there
     * reads V's body as the converted mod will name it. Empty on every code model but V's.
     */
    private Map<String, String> aliasOf = Map.of();
    private Map<String, String> aliasRenames = Map.of();

    /**
     * the class renames a facade alias is read through: all the step's (a facade's code names every class as
     * the converted jar does, and a mixin following onto it has all its names renamed, MixinClassRenames)
     */
    private Map<String, String> facadeRenames = Map.of();

    /**
     * take V's class {@code from} as {@code renames.get(from)}, its references renamed the same way; one
     * taken as a facade (CenturyBridge's class) has its references renamed by all of {@code all}
     */
    public void aliases(Map<String, String> renames, Map<String, String> all) {
        Map<String, String> back = new HashMap<>();
        renames.forEach((from, to) -> back.put(to, from));
        this.aliasOf = back;
        this.aliasRenames = Map.copyOf(renames);
        this.facadeRenames = Map.copyOf(all);
        cache.keySet().removeIf(back::containsKey);
    }

    /** the names of the methods the code jar's classes call or take a method reference to (lazily) */
    private Set<String> invokedNames;
    private boolean invokedUnproven;

    /**
     * Whether any class of the code jar calls a method of this name, or takes a method reference to one.
     * By name only (intermediary method names are one per override family), so a same-named method of an
     * unrelated class counts too: an answer of false is the proven one.
     */
    public synchronized boolean invokes(String name) {
        if (invokedNames == null) {
            Set<String> out = new java.util.HashSet<>();
            for (ZipEntry e : java.util.Collections.list(code.entries())) {
                if (!e.getName().endsWith(".class")) continue;
                try {
                    new ClassReader(code.getInputStream(e).readAllBytes()).accept(new org.objectweb.asm.ClassVisitor(Opcodes.ASM9) {
                        @Override
                        public org.objectweb.asm.MethodVisitor visitMethod(int access, String n, String d, String sig, String[] ex) {
                            return new org.objectweb.asm.MethodVisitor(Opcodes.ASM9) {
                                @Override
                                public void visitMethodInsn(int op, String owner, String mn, String md, boolean itf) {
                                    out.add(mn);
                                }

                                @Override
                                public void visitInvokeDynamicInsn(String in, String id, org.objectweb.asm.Handle bsm, Object... args) {
                                    for (Object a : args) {
                                        if (a instanceof org.objectweb.asm.Handle h) out.add(h.getName());
                                    }
                                }

                                @Override
                                public void visitLdcInsn(Object v) {
                                    if (v instanceof org.objectweb.asm.Handle h) out.add(h.getName());
                                }
                            };
                        }
                    }, ClassReader.SKIP_DEBUG | ClassReader.SKIP_FRAMES);
                } catch (IOException ex) {
                    invokedUnproven = true; // an unreadable class may call anything
                }
            }
            invokedNames = out;
        }
        return invokedUnproven || invokedNames.contains(name);
    }

    public ClassNode target(String owner) {
        return cache.computeIfAbsent(owner, o -> {
            try {
                ZipEntry e = code.getEntry(o + ".class");
                ZipEntry le = e == null ? layerEntry(o) : null;
                if (le != null) {
                    ClassNode cn = new ClassNode();
                    new ClassReader(layer.getInputStream(le).readAllBytes()).accept(cn, ClassReader.SKIP_FRAMES);
                    return cn;
                }
                if (e == null) {
                    String v = aliasOf.get(o);
                    ZipEntry ve = v == null ? null : code.getEntry(v + ".class");
                    if (ve == null) return null;
                    ClassNode cn = new ClassNode();
                    new ClassReader(code.getInputStream(ve).readAllBytes()).accept(
                        new org.objectweb.asm.commons.ClassRemapper(cn, new org.objectweb.asm.commons.SimpleRemapper(
                            o.startsWith("top/dext/") ? facadeRenames : aliasRenames)),
                        ClassReader.SKIP_FRAMES);
                    return cn;
                }
                ClassNode cn = new ClassNode();
                new ClassReader(code.getInputStream(e).readAllBytes()).accept(cn, ClassReader.SKIP_FRAMES);
                return cn;
            } catch (IOException ex) {
                return null;
            }
        });
    }

    /**
     * mixin-anchors <codeJar> <jar|dir...>: run the check over unconverted jars
     * (nested jars included) -- the native control: mods on the version they
     * were built for must come out with zero "anchor gone".
     */
    public static void audit(String[] args) throws IOException {
        MixinRepair r = new MixinRepair(Path.of(args[1]));
        int fatal = 0, repairs = 0, jars = 0;
        for (int i = 2; i < args.length; i++) {
            Path p = Path.of(args[i]);
            List<Path> list = new ArrayList<>();
            if (java.nio.file.Files.isDirectory(p)) {
                try (var st = java.nio.file.Files.list(p)) { st.filter(f -> f.toString().endsWith(".jar")).sorted().forEach(list::add); }
            } else {
                list.add(p);
            }
            for (Path j : list) {
                jars++;
                int[] c = r.auditJar(java.nio.file.Files.readAllBytes(j), j.getFileName().toString());
                fatal += c[0];
                repairs += c[1];
            }
        }
        System.out.println("mixin-anchors: " + jars + " jars, " + fatal + " anchor-gone, " + repairs + " repairs");
    }

    private int[] auditJar(byte[] jar, String label) throws IOException {
        Map<String, byte[]> entries = new LinkedHashMap<>();
        try (var zin = new java.util.zip.ZipInputStream(new java.io.ByteArrayInputStream(jar))) {
            java.util.zip.ZipEntry e;
            while ((e = zin.getNextEntry()) != null) if (!e.isDirectory()) entries.put(e.getName(), zin.readAllBytes());
        }
        int[] c = {0, 0};
        Outcome o = repair(entries);
        for (String cls : o.fatal()) {
            c[0]++;
            System.out.println("GONE	" + label + "	" + o.fatalWhy().get(cls));
        }
        for (String d : o.disabled()) {
            c[0]++;
            System.out.println("DISABLED	" + label + "	" + d);
        }
        for (String rp : o.repairs()) {
            c[1]++;
            System.out.println("REPAIR	" + label + "	" + rp);
        }
        for (var e : entries.entrySet()) {
            if (e.getKey().startsWith("META-INF/jars/") && e.getKey().endsWith(".jar")) {
                int[] n = auditJar(e.getValue(), label + "!" + e.getKey().substring(14));
                c[0] += n[0];
                c[1] += n[1];
            }
        }
        return c;
    }

    // ------------------------------------------------------------------ entry

    /** rewrites mixin classes / refmaps in {@code entries} in place */
    public Outcome repair(Map<String, byte[]> entries) {
        Set<String> fatal = new LinkedHashSet<>();
        Map<String, String> why = new LinkedHashMap<>();
        List<String> repairs = new ArrayList<>();
        List<String> disabledAll = new ArrayList<>();
        byte[] fmjBytes = entries.get("fabric.mod.json");
        if (fmjBytes == null) return new Outcome(fatal, why, repairs, disabledAll);
        restoredByTarget = restoredOverrides(entries);
        JsonObject fmj;
        try {
            fmj = GSON.fromJson(new String(fmjBytes, StandardCharsets.UTF_8), JsonObject.class);
        } catch (RuntimeException ex) {
            return new Outcome(fatal, why, repairs, disabledAll);
        }
        for (JsonElement ref : fmj.has("mixins") ? fmj.getAsJsonArray("mixins") : new JsonArray()) {
            String cfgName = ref.isJsonObject() ? ref.getAsJsonObject().get("config").getAsString() : ref.getAsString();
            byte[] cfgBytes = entries.get(cfgName);
            if (cfgBytes == null) continue;
            JsonObject cfg;
            try {
                cfg = GSON.fromJson(new String(cfgBytes, StandardCharsets.UTF_8), JsonObject.class);
            } catch (RuntimeException ex) {
                continue;
            }
            String pkg = cfg.has("package") ? cfg.get("package").getAsString().replace('.', '/') : "";
            int defaultRequire = 0;
            if (cfg.has("injectors") && cfg.getAsJsonObject("injectors").has("defaultRequire")) {
                defaultRequire = cfg.getAsJsonObject("injectors").get("defaultRequire").getAsInt();
            }
            String refmapName = cfg.has("refmap") ? cfg.get("refmap").getAsString() : null;
            JsonObject refmapRoot = null;
            if (refmapName != null && entries.get(refmapName) != null) {
                try {
                    refmapRoot = GSON.fromJson(new String(entries.get(refmapName), StandardCharsets.UTF_8), JsonObject.class);
                } catch (RuntimeException ex) {
                    refmapRoot = null;
                }
            }
            boolean refmapDirty = false;
            for (String key : new String[] {"mixins", "client", "server"}) {
                if (!cfg.has(key)) continue;
                for (JsonElement e : cfg.getAsJsonArray(key)) {
                    String cls = (pkg.isEmpty() ? "" : pkg + "/") + e.getAsString().replace('.', '/');
                    byte[] bytes = entries.get(cls + ".class");
                    if (bytes == null) continue;
                    JsonObject table = refmapRoot != null && refmapRoot.has("mappings")
                        && refmapRoot.getAsJsonObject("mappings").has(cls)
                        ? refmapRoot.getAsJsonObject("mappings").getAsJsonObject(cls) : null;
                    MixinResult r = repairMixin(bytes, cls, table, defaultRequire);
                    if (r == null) continue;
                    if (r.bytes != null) entries.put(cls + ".class", r.bytes);
                    refmapDirty |= r.refmapChanged;
                    repairs.addAll(r.repairs);
                    disabledAll.addAll(r.disabled);
                    if (r.fatalWhy != null) {
                        fatal.add(cls);
                        why.put(cls, r.fatalWhy);
                    }
                }
            }
            if (refmapDirty) {
                entries.put(refmapName, GSON.toJson(refmapRoot).getBytes(StandardCharsets.UTF_8));
            }
        }
        return new Outcome(fatal, why, repairs, disabledAll);
    }

    private record MixinResult(byte[] bytes, boolean refmapChanged, List<String> repairs, List<String> disabled,
                               String fatalWhy) {}

    // ------------------------------------------------------------------ per mixin

    private MixinResult repairMixin(byte[] bytes, String cls, JsonObject refmap, int defaultRequire) {
        ClassNode mx = new ClassNode();
        new ClassReader(bytes).accept(mx, 0);
        List<String> targets = mixinTargets(mx, refmap);
        // vanilla targets, and a facade a mixin followed its target's class rename to (its code is the layer's)
        if (targets.stream().noneMatch(t -> t.startsWith("net/minecraft/") || t.startsWith("top/dext/") && layerEntry(t) != null)) return null;
        boolean classChanged = false;
        boolean refmapChanged = false;
        List<String> repairs = new ArrayList<>();
        List<String> disabled = new ArrayList<>();
        String fatalWhy = null;
        String shortName = cls.substring(cls.lastIndexOf('/') + 1);

        // A class mixin on a target that became an interface dies at PREPARE
        // ("@Mixin target type mismatch"), whatever its injectors say. 1.21.2
        // made Explosion an interface over one implementation (ExplosionImpl):
        // the mixin's members now belong there, so retarget to the single
        // concrete implementor; with none or several there is no faithful target.
        if ((mx.access & Opcodes.ACC_INTERFACE) == 0) {
            for (String t : new ArrayList<>(targets)) {
                ClassNode tc = target(t);
                if (tc == null || (tc.access & Opcodes.ACC_INTERFACE) == 0) continue;
                List<String> impls = implementors(t);
                if (impls.size() == 1) {
                    String impl = impls.get(0);
                    if (retargetMixin(mx, refmap, t, impl)) {
                        classChanged = true;
                        if (refmap != null) refmapChanged = true;
                        targets.set(targets.indexOf(t), impl);
                        repairs.add("interface-retarget " + shortName + ": " + t + " is an interface now -> " + impl);
                    }
                } else {
                    fatalWhy = "target " + t + " became an interface (" + impls.size() + " concrete implementors)";
                }
            }
            if (fatalWhy != null) return new MixinResult(null, false, repairs, disabled, fatalWhy);
        }

        boolean[] dirty = {false, false}; // class, refmap
        for (MethodNode handler : mx.methods) {
            for (AnnotationNode an : annotations(handler)) {
                Map<String, Object> v = values(an);
                List<String> methods = strings(v.get("method"));
                if (methods.isEmpty()) continue;
                pendingDisable = null;
                if (source != null && remapOrdinals(an, refmap, methods, targets, shortName + "." + handler.name, repairs)) {
                    dirty[0] = true;
                    v = values(an);
                }
                if (pendingDisable != null) {
                    dropInjector(handler, an);
                    dirty[0] = true;
                    disabled.add(pendingDisable);
                    pendingDisable = null;
                    continue;
                }
                List<Anchor> anchors = anchors(an, v, refmap); // null = anchor not statically evaluable
                int require = v.get("require") instanceof Integer i && i >= 0 ? i : defaultRequire;
                boolean capturesArgs = capturesTargetArgs(an, handler, anchors);
                String kind = an.desc.substring(an.desc.lastIndexOf('/') + 1, an.desc.length() - 1);

                // Selector lists are ALTERNATIVES (fabric name, forge lambda, SRG name...):
                // Mixin is satisfied when any of them injects. Judge the injector as a
                // whole; repair only when no alternative is live, and apply one repair
                // (two alternatives repaired onto the same method would inject twice).
                boolean live = false;
                String deadWhy = null;
                List<String> fixNotes = new ArrayList<>();
                List<Runnable> fixes = new ArrayList<>();
                for (int mi = 0; mi < methods.size() && !live; mi++) {
                    final int idx = mi;
                    String sel = methods.get(mi);
                    if (sel.startsWith("/")) { live = true; break; } // regex selector: not judged
                    String mapped = refmapLookup(refmap, sel);
                    boolean viaRefmap = !mapped.equals(sel);
                    Sel s = Sel.parse(mapped);
                    List<String> owners = s.owner != null ? List.of(s.owner) : targets;
                    for (String owner : owners) {
                        ClassNode tc = target(owner);
                        if (tc == null) { live = true; break; } // not a class we hold code for: can't judge
                        List<MethodNode> cands = s.match(tc);
                        if (cands.isEmpty() && s.desc != null && !renamed.apply(s.desc).equals(s.desc)) {
                            cands = new Sel(s.owner, s.name, renamed.apply(s.desc), s.wildcard).match(tc);
                        }
                        if (cands.isEmpty()) cands = restored(restoredByTarget, owner, s); // an override MixinBridge put back
                        if (cands.isEmpty()) cands = layerMembers.match(owner, s); // a member the layer restores (LayerMembers)
                        if (cands.isEmpty() && s.desc != null && !renamed.apply(s.desc).equals(s.desc)) {
                            cands = layerMembers.match(owner, new Sel(s.owner, s.name, renamed.apply(s.desc), s.wildcard)); // as the step renames its types
                        }
                        String where = shortName + "." + handler.name + " -> " + owner.substring(owner.lastIndexOf('/') + 1)
                            + "." + s.name + (s.desc != null ? s.desc : "");
                        if (cands.isEmpty()) {
                            Sel byName = new Sel(s.owner, s.name, null, s.wildcard || s.name.equals("<init>"));
                            List<MethodNode> named = byName.match(tc);
                            if (s.desc != null && !named.isEmpty() && !capturesArgs
                                    && (named.size() == 1 || byName.wildcard)
                                    && (anchors == null || anchors.isEmpty()
                                        || named.stream().allMatch(m -> anchorsIn(m, tc, anchors)))) {
                                String repl = (s.owner != null ? "L" + s.owner + ";" : "") + s.name;
                                fixNotes.add("desc-drop " + where + " (" + named.size() + " match by name)");
                                fixes.add(() -> {
                                    if (viaRefmap) { refmapPut(refmap, sel, repl); dirty[1] = true; }
                                    else { setMethodSelector(an, idx, repl); dirty[0] = true; }
                                });
                            } else if (deadWhy == null) {
                                deadWhy = "target gone: @" + kind + " " + where;
                            }
                            continue;
                        }
                        if (anchors == null || anchors.isEmpty() || cands.stream().anyMatch(m -> anchorsIn(m, tc, anchors))) {
                            live = true;
                            break;
                        }
                        if (cands.stream().allMatch(MixinBridge::restoredMark)) {
                            // an override MixinBridge put back is one super call: the anchor stood in the body V's
                            // override had, and following it into the super call would move it before all of that
                            if (deadWhy == null) deadWhy = "anchor gone: @" + kind + " " + where + " " + anchors
                                + " (the target is an override put back as a super call; its anchor stood in V's own body)";
                            continue;
                        }
                        // @Inject marks a POSITION in the unchanged target: its anchor may
                        // follow the call it named -- re-described (drawTexture grew a
                        // Function parameter) or wrapped (tick -> hitOrDeflect -> onCollision)
                        if (an.desc.equals(INJECT) && anchors.size() == 1
                                && (anchors.get(0).kind.equals("INVOKE") || anchors.get(0).kind.equals("FIELD"))) {
                            String moved = movedAnchor(cands, tc, anchors.get(0));
                            if (moved != null) {
                                fixNotes.add("anchor-follow " + where + " " + anchors.get(0) + " => " + moved);
                                fixes.add(() -> {
                                    if (setAtTarget(an, refmap, moved)) { dirty[0] = true; if (refmap != null) dirty[1] = true; }
                                });
                                continue;
                            }
                        }
                        // descend-site (V's code at hand): where V's site of the anchor stands in W, in one
                        // private method only the target's path calls; when it judged and refused, the
                        // looser descend below does not get to guess
                        if (source != null && anchors.size() == 1 && cands.size() == 1 && !capturesArgs) {
                            List<String> dwhy = new ArrayList<>();
                            Descend d = descendBySite(an, handler, owner, tc, cands.get(0), anchors.get(0), dwhy);
                            if (d != null) {
                                String repl = "L" + owner + ";" + d.callee().name + d.callee().desc;
                                fixNotes.add("descend-site " + where + " " + anchors.get(0) + " => " + d.callee().name + d.callee().desc
                                    + (d.ordinal() >= 0 ? " #" + d.ordinal() : "") + (d.makeStatic() ? " (handler made static)" : "")
                                    + (d.toCir() ? " (callback as CallbackInfoReturnable)" : "")
                                    + " (V's site by context; the callee is " + d.how() + ")");
                                fixes.add(() -> {
                                    if (d.makeStatic()) makeStatic(handler);
                                    if (d.toCir()) toReturnable(handler);
                                    setAtOrdinal(an, d.ordinal());
                                    if (viaRefmap) { refmapPut(refmap, sel, repl); dirty[1] = true; }
                                    else setMethodSelector(an, idx, repl);
                                    dirty[0] = true;
                                });
                                continue;
                            }
                            if (!dwhy.isEmpty()) {
                                if (deadWhy == null) deadWhy = "anchor gone: @" + kind + " " + where + " " + anchors + " (" + dwhy.get(0) + ")";
                                continue;
                            }
                        }
                        // descend: the anchor moved into exactly one same-class callee
                        Set<MethodNode> holders = new LinkedHashSet<>();
                        for (MethodNode c : cands) {
                            for (MethodNode callee : callees(c, tc)) if (anchorsIn(callee, tc, anchors)) holders.add(callee);
                        }
                        if (holders.size() == 1 && !capturesArgs) {
                            MethodNode h = holders.iterator().next();
                            boolean calleeStatic = (h.access & Opcodes.ACC_STATIC) != 0;
                            boolean handlerStatic = (handler.access & Opcodes.ACC_STATIC) != 0;
                            if (calleeStatic == handlerStatic || handlerStatic || (calleeStatic && touchesThisFree(handler))) {
                                String repl = "L" + owner + ";" + h.name + h.desc;
                                fixNotes.add("descend " + where + " => " + h.name + h.desc
                                    + (calleeStatic && !handlerStatic ? " (handler made static)" : ""));
                                fixes.add(() -> {
                                    if (calleeStatic && !handlerStatic) makeStatic(handler);
                                    if (viaRefmap) { refmapPut(refmap, sel, repl); dirty[1] = true; }
                                    else setMethodSelector(an, idx, repl);
                                    dirty[0] = true;
                                });
                                continue;
                            }
                        }
                        if (deadWhy == null) {
                            deadWhy = "anchor gone: @" + kind + " " + where + " " + anchors
                                + (holders.size() > 1 ? " (in " + holders.size() + " callees)" : "")
                                + (capturesArgs && holders.size() == 1 ? " (handler captures target args)" : "");
                        }
                    }
                }
                if (live) continue;
                if (!fixes.isEmpty()) {
                    fixes.get(0).run();
                    repairs.add(fixNotes.get(0));
                } else if (deadWhy != null) {
                    // only THIS injector is lost. Removing its annotation (the handler stays
                    // as a plain method) beats require=0: some failures are thrown at apply
                    // regardless of require ("Found 0 candidate variables"). The mixin's
                    // other injectors and interface merges keep working.
                    dropInjector(handler, an);
                    dirty[0] = true;
                    disabled.add(deadWhy);
                }
            }
        }
        classChanged |= dirty[0];
        refmapChanged |= dirty[1];
        byte[] out = null;
        if (classChanged) {
            ClassWriter cw = new ClassWriter(0);
            mx.accept(cw);
            out = cw.toByteArray();
        }
        if (out == null && !refmapChanged && repairs.isEmpty() && fatalWhy == null && disabled.isEmpty()) return null;
        return new MixinResult(out, refmapChanged, repairs, disabled, fatalWhy);
    }

    // ------------------------------------------------------------------ descend by site

    /** sibling: the callee is not reached from the target but is another entry point of the same framework class */
    record Descend(MethodNode callee, int ordinal, boolean toCir, boolean makeStatic, boolean sibling) {
        String how() {
            return sibling ? "another entry point of the same framework class, called from nowhere in its own class"
                : "called from one site";
        }
    }

    /** the descend's handler and annotation changes (the selector is the caller's to set) */
    static void applyDescend(Descend d, MethodNode handler, AnnotationNode an) {
        if (d.makeStatic()) makeStatic(handler);
        if (d.toCir()) toReturnable(handler);
        setAtOrdinal(an, d.ordinal());
    }

    /**
     * descend-site: the anchor's V site (the pinned occurrence, or every occurrence) found by context
     * ({@link SiteMatch#match}) in one method the W target reaches by calls on its own class (depth 2),
     * and nowhere else. The injector moves there only if that changes nothing but the method it sits in:
     * every method on the path is called from exactly one site (within its class when private, in the
     * whole W jar through the class or its subclasses otherwise -- so it runs exactly when V's code
     * ran, once per pass), the handler does not cancel (in the callee that would
     * return from the callee, not the target), and its callback fits the callee (CallbackInfo becomes
     * CallbackInfoReturnable for a callee returning a value -- a subclass, the body is unchanged; the
     * other way, or a different return value type, is refused). Null without reasons when V's body or
     * the site cannot be read (the looser descend may try); null with a reason when refused.
     */
    private Descend descendBySite(AnnotationNode an, MethodNode handler, String owner, ClassNode tc, MethodNode wm, Anchor a,
                                  List<String> why) {
        ClassNode vc = source.target(owner);
        MethodNode vm = null;
        if (vc != null) for (MethodNode m : vc.methods) if (m.name.equals(wm.name) && m.desc.equals(wm.desc)) vm = m;
        if (vm == null) return null;
        return descendBySite(an, handler, tc, vc, vm, wm, a, why);
    }

    /**
     * descend-site from V's method vm (of V's class vc) to what W's method wm reaches: wm is vm's own
     * W copy, or the W method an injector's selector followed vm to (MixinBridge: rename, reshape,
     * mixinTargets, body follow). The handler is written for vm: a callback it reads the return value
     * through must fit vm's return.
     */
    Descend descendBySite(AnnotationNode an, MethodNode handler, ClassNode tc, ClassNode vc, MethodNode vm, MethodNode wm,
                          Anchor a, List<String> why) {
        if (!a.role.equals("main") || a.kind.equals("LOCAL") || a == POSITIONAL) return null;
        List<AbstractInsnNode> vo = SiteMatch.occurrences(vm, in -> matchesAnchor(in, a));
        if (vo.isEmpty() || a.ordinal >= vo.size()) return null;
        List<Integer> sites = new ArrayList<>();
        if (a.ordinal >= 0) sites.add(a.ordinal);
        else for (int i = 0; i < vo.size(); i++) sites.add(i);
        // what the target reaches by direct calls on its own class, depth 2: callee -> its caller
        Map<MethodNode, MethodNode> parent = new LinkedHashMap<>();
        List<MethodNode> frontier = List.of(wm);
        for (int depth = 1; depth <= 2; depth++) {
            List<MethodNode> next = new ArrayList<>();
            for (MethodNode m : frontier) {
                for (MethodNode c : callees(m, tc)) {
                    if (c != wm && !parent.containsKey(c)) {
                        parent.put(c, m);
                        next.add(c);
                    }
                }
            }
            frontier = next;
        }
        List<MethodNode> places = new ArrayList<>(parent.keySet());
        places.add(0, wm);
        // V's target overrode a framework class's hook (SimplePreparableReloadListener.apply); W split its code over
        // that class's hooks (RecipeManager: parsing in prepare, storing in apply). Another hook of the same
        // framework class, called from nowhere in W's own class, runs once where V's target ran once
        List<MethodNode> siblings = siblingHooks(vc, vm, tc, wm);
        for (MethodNode sb : siblings) {
            if (!places.contains(sb)) {
                places.add(sb);
            }
        }
        MethodNode into = null;
        int[] ords = new int[sites.size()];
        for (int k = 0; k < sites.size(); k++) {
            MethodNode found = null;
            int fj = -1;
            int hits = 0;
            for (MethodNode c : places) {
                List<AbstractInsnNode> wo = SiteMatch.occurrences(c, in -> matchesAnchor(in, a));
                if (wo.isEmpty()) continue;
                // another method than V's own: a context of 3 or more, no alignment (audit: a 1-wide
                // context matched index 0 of another statement's argument array)
                int j = c == wm ? SiteMatch.match(vm, vo, c, wo)[sites.get(k)] : SiteMatch.match(vm, vo, c, wo, 3, false)[sites.get(k)];
                if (j >= 0 && c != wm && ownSite(vc, c, wo.get(j), a)) {
                    j = -1; // V's own copy of that callee already held this site: it is not where the target's went
                }
                if (j >= 0) {
                    hits++;
                    found = c;
                    fj = j;
                }
            }
            if (hits == 0) {
                // V's site has no counterpart in the target or what it calls: the looser descend (any
                // occurrence of the anchor in one callee) would be a guess
                why.add("V's site has no counterpart in the target or the methods it calls");
                return null;
            }
            if (found == wm) return null; // still in the target itself: not a descend
            if (hits > 1) {
                why.add("V's site matches in " + hits + " methods");
                return null;
            }
            if (into != null && into != found) {
                why.add("V's sites stand in different callees now");
                return null;
            }
            into = found;
            ords[k] = fj;
        }
        for (MethodNode m = into; m != wm && !siblings.contains(into); m = parent.get(m)) {
            int calls = (m.access & Opcodes.ACC_PRIVATE) != 0 ? callSites(tc, m) : jarCallSites(tc.name, m);
            if (calls != 1) {
                why.add(m.name + m.desc + " holds V's site but is called from " + calls + " sites");
                return null;
            }
        }
        if (cancels(handler)) {
            why.add("the handler cancels, which in " + into.name + " would return from it, not from the target");
            return null;
        }
        boolean toCir = false;
        if (an.desc.equals(INJECT)) {
            boolean wantCir = Type.getReturnType(into.desc).getSort() != Type.VOID;
            boolean hasCir = handler.desc.contains(CIR);
            if (hasCir && !Type.getReturnType(into.desc).equals(Type.getReturnType(vm.desc))) {
                why.add("the handler takes the target's return value; " + into.name + " returns " + Type.getReturnType(into.desc));
                return null;
            }
            toCir = wantCir && !hasCir;
        }
        boolean calleeStatic = (into.access & Opcodes.ACC_STATIC) != 0;
        boolean handlerStatic = (handler.access & Opcodes.ACC_STATIC) != 0;
        if (!(calleeStatic == handlerStatic || handlerStatic || calleeStatic && touchesThisFree(handler))) {
            why.add(into.name + " is static and the handler reads this");
            return null;
        }
        int ordinal;
        List<AbstractInsnNode> wo = SiteMatch.occurrences(into, in -> matchesAnchor(in, a));
        if (sites.size() == 1) {
            ordinal = a.ordinal >= 0 || wo.size() > 1 ? ords[0] : -1;
        } else {
            Set<Integer> got = new java.util.HashSet<>();
            for (int o : ords) got.add(o);
            if (got.size() != wo.size()) {
                why.add(into.name + " holds the anchor " + wo.size() + " times, V's " + sites.size() + " sites are " + got.size() + " of them");
                return null;
            }
            ordinal = -1;
        }
        return new Descend(into, ordinal, toCir, calleeStatic && !handlerStatic, siblings.contains(into));
    }

    /**
     * Whether W's site in callee c is the counterpart of an occurrence of the anchor in V's own copy of
     * c (same name and descriptor in V's class): then it stood in that method already in V.
     */
    private static boolean ownSite(ClassNode vc, MethodNode c, AbstractInsnNode site, Anchor a) {
        MethodNode vcm = null;
        for (MethodNode m : vc.methods) if (m.name.equals(c.name) && m.desc.equals(c.desc)) vcm = m;
        if (vcm == null) return false;
        List<AbstractInsnNode> vo = SiteMatch.occurrences(vcm, in -> matchesAnchor(in, a));
        if (vo.isEmpty()) return false;
        List<AbstractInsnNode> wo = SiteMatch.occurrences(c, in -> matchesAnchor(in, a));
        int at = wo.indexOf(site);
        for (int j : SiteMatch.match(vcm, vo, c, wo)) if (j == at) return true;
        return false;
    }

    /** calls of m within its class, method references (invokedynamic handles) included */
    /**
     * The other hooks of the framework class V's method vm overrides: methods of W's class tc overriding a method
     * declared by that same class (looked up in W), not vm's W counterpart wm, not called by any method of tc
     * itself, non-static and not synthetic. Empty when vm overrides nothing (V's code model) or nothing qualifies.
     */
    private List<MethodNode> siblingHooks(ClassNode vc, MethodNode vm, ClassNode tc, MethodNode wm) {
        List<MethodNode> out = new ArrayList<>();
        if (source == null || (vm.access & (Opcodes.ACC_STATIC | Opcodes.ACC_PRIVATE)) != 0 || vm.name.startsWith("<")) return out;
        String fw = declaringSuper(source, vc, hookName(vc, vm), vm.desc);
        if (fw == null || !fw.startsWith("net/minecraft/")) return out;
        for (MethodNode m : tc.methods) {
            if (m == wm || m.name.startsWith("<") || (m.access & (Opcodes.ACC_STATIC | Opcodes.ACC_PRIVATE | Opcodes.ACC_SYNTHETIC
                    | Opcodes.ACC_ABSTRACT | Opcodes.ACC_BRIDGE)) != 0) continue;
            if (fw.equals(declaringSuper(this, tc, hookName(tc, m), m.desc)) && callsBesidesBridges(tc, m) == 0) out.add(m);
        }
        return out;
    }

    /** calls of m from the methods of its class, its synthetic bridges left out (they are the hook itself) */
    private static int callsBesidesBridges(ClassNode tc, MethodNode m) {
        int n = 0;
        for (MethodNode c : tc.methods) {
            if ((c.access & (Opcodes.ACC_SYNTHETIC | Opcodes.ACC_BRIDGE)) == (Opcodes.ACC_SYNTHETIC | Opcodes.ACC_BRIDGE) || c.instructions == null) continue;
            for (AbstractInsnNode in : c.instructions) {
                if (in instanceof MethodInsnNode mi && mi.owner.equals(tc.name) && mi.name.equals(m.name) && mi.desc.equals(m.desc)) n++;
                if (in instanceof org.objectweb.asm.tree.InvokeDynamicInsnNode id) {
                    for (Object a : id.bsmArgs) {
                        if (a instanceof org.objectweb.asm.Handle h && h.getOwner().equals(tc.name) && h.getName().equals(m.name)) n++;
                    }
                }
            }
        }
        return n;
    }

    /**
     * the name a method overrides its framework hook by: its own, or -- a typed override of a generic hook
     * (RecipeManager.apply(Map, ..)) -- the name of the class's synthetic bridge forwarding to it (apply(Object, ..))
     */
    private static String hookName(ClassNode c, MethodNode m) {
        for (MethodNode b : c.methods) {
            if ((b.access & (Opcodes.ACC_SYNTHETIC | Opcodes.ACC_BRIDGE)) == 0 || b == m || b.instructions == null) continue;
            for (AbstractInsnNode in : b.instructions) {
                if (in instanceof MethodInsnNode mi && mi.owner.equals(c.name) && mi.name.equals(m.name) && mi.desc.equals(m.desc)
                        && !b.name.equals(m.name)) {
                    return b.name;
                }
            }
        }
        return m.name;
    }

    /**
     * the nearest superclass of c (in the given code model) declaring a non-private, non-static method of that
     * intermediary name, or null. By name: an override of a generic hook has another descriptor than the declared
     * one (RecipeManager.apply(Map, ..) for SimplePreparableReloadListener.apply(Object, ..)), and intermediary
     * gives every member of one override family the same name
     */
    private static String declaringSuper(MixinRepair model, ClassNode c, String name, String desc) {
        if (!name.startsWith("method_")) return null;
        for (String s = c.superName; s != null; ) {
            ClassNode sc = model.target(s);
            if (sc == null) return null;
            for (MethodNode m : sc.methods) {
                if (m.name.equals(name) && (m.access & (Opcodes.ACC_PRIVATE | Opcodes.ACC_STATIC)) == 0) return sc.name;
            }
            s = sc.superName;
        }
        return null;
    }

    private static int callSites(ClassNode tc, MethodNode m) {
        int n = 0;
        for (MethodNode c : tc.methods) {
            if (c.instructions == null) continue;
            for (AbstractInsnNode in : c.instructions) {
                if (in instanceof MethodInsnNode mi && mi.owner.equals(tc.name) && mi.name.equals(m.name) && mi.desc.equals(m.desc)) n++;
                if (in instanceof org.objectweb.asm.tree.InvokeDynamicInsnNode id) {
                    for (Object b : id.bsmArgs) {
                        if (b instanceof org.objectweb.asm.Handle h && h.getOwner().equals(tc.name) && h.getName().equals(m.name)
                                && h.getDesc().equals(m.desc)) n++;
                    }
                }
            }
        }
        return n;
    }

    /** call sites of name+desc in every class of the code jar, by owner: built once */
    private Map<String, List<String>> callIndex;

    /**
     * calls anywhere in the W jar that reach a non-private m of class owner: through owner itself or a
     * subclass of it (a call through a supertype that declares it too reaches it virtually: counted as
     * well), method references included
     */
    private int jarCallSites(String owner, MethodNode m) {
        if (callIndex == null) {
            callIndex = new HashMap<>();
            for (ZipEntry e : java.util.Collections.list(code.entries())) {
                if (!e.getName().endsWith(".class")) continue;
                String name = e.getName().substring(0, e.getName().length() - 6);
                boolean had = cache.containsKey(name);
                ClassNode cn = target(name);
                if (cn == null) continue;
                for (MethodNode c : cn.methods) {
                    if (c.instructions == null) continue;
                    for (AbstractInsnNode in : c.instructions) {
                        if (in instanceof MethodInsnNode mi) {
                            callIndex.computeIfAbsent(mi.name + mi.desc, k -> new ArrayList<>()).add(mi.owner);
                        } else if (in instanceof org.objectweb.asm.tree.InvokeDynamicInsnNode id) {
                            for (Object b : id.bsmArgs) {
                                if (b instanceof org.objectweb.asm.Handle h) callIndex.computeIfAbsent(h.getName() + h.getDesc(), k -> new ArrayList<>()).add(h.getOwner());
                            }
                        }
                    }
                }
                if (!had) cache.remove(name); // the index is what is kept, not every class
            }
        }
        int n = 0;
        for (String o : callIndex.getOrDefault(m.name + m.desc, List.of())) {
            if (related(o, owner)) n++;
        }
        return n;
    }

    /** o is owner, a subclass of it, or a supertype of it */
    private boolean related(String o, String owner) {
        if (o.equals(owner)) return true;
        for (String c = o; c != null; ) {
            if (c.equals(owner)) return true;
            ClassNode cn = target(c);
            c = cn == null ? null : cn.superName;
        }
        for (String c = owner; c != null; ) {
            if (c.equals(o)) return true;
            ClassNode cn = target(c);
            c = cn == null ? null : cn.superName;
        }
        return false;
    }

    /** does the handler cancel or set a return value through its callback */
    private static boolean cancels(MethodNode h) {
        if (h.instructions == null) return false;
        String ci = CI.substring(1, CI.length() - 1), cir = CIR.substring(1, CIR.length() - 1);
        for (AbstractInsnNode in : h.instructions) {
            if (in instanceof MethodInsnNode mi && (mi.owner.equals(ci) || mi.owner.equals(cir))
                    && (mi.name.equals("cancel") || mi.name.equals("setReturnValue"))) return true;
        }
        return false;
    }

    /** the handler's CallbackInfo parameter declared as CallbackInfoReturnable (its subclass) */
    private static void toReturnable(MethodNode h) {
        h.desc = h.desc.replace(CI, CIR);
        if (h.localVariables != null) {
            for (LocalVariableNode lv : h.localVariables) if (lv.desc.equals(CI)) lv.desc = CIR;
        }
        if (h.signature != null) h.signature = h.signature.replace(CI, CIR);
    }

    /** the injector's single @At pinned to ordinal (or unpinned for -1) */
    private static void setAtOrdinal(AnnotationNode an, int ordinal) {
        for (Object a : list(raw(an, "at"))) {
            if (!(a instanceof AnnotationNode at)) continue;
            if (at.values == null) at.values = new ArrayList<>();
            int idx = -1;
            for (int i = 0; i + 1 < at.values.size(); i += 2) if ("ordinal".equals(at.values.get(i))) idx = i;
            if (ordinal >= 0) {
                if (idx >= 0) at.values.set(idx + 1, ordinal);
                else { at.values.add("ordinal"); at.values.add(ordinal); }
            } else if (idx >= 0) {
                at.values.remove(idx + 1);
                at.values.remove(idx);
            }
        }
    }

    // ------------------------------------------------------------------ ordinals

    /**
     * ordinal-remap: every @At (slices included) and @Constant of the injector pinned to an ordinal is
     * moved to the ordinal of its V site's counterpart in W. Only for a selector naming one method that
     * V and W both declare on one owner, by name and descriptor or by name alone when each release has
     * exactly one (a target the step moved is not V's body any more). True if an
     * ordinal changed; each change, and each pinned site left without a counterpart, is reported.
     */
    private boolean remapOrdinals(AnnotationNode an, JsonObject refmap, List<String> methods, List<String> targets,
                                  String where, List<String> notes) {
        MethodNode wm = null, vm = null;
        for (String sel : methods) {
            if (sel.startsWith("/")) return false;
            Sel s = Sel.parse(refmapLookup(refmap, sel));
            if (s.wildcard) return false;
            // a selector by name alone (mods write "tick", "<clinit>") names one body when V and W each have one
            List<String> owners = s.owner != null ? List.of(s.owner) : targets;
            for (String owner : owners) {
                ClassNode tc = target(owner), vc = source.target(owner);
                if (tc == null || vc == null) continue;
                List<MethodNode> w = s.match(tc), v = s.match(vc);
                if (w.size() != 1 || v.size() != 1) continue;
                if (wm != null) return false; // several live alternatives: not one body to match
                wm = w.get(0);
                vm = v.get(0);
            }
        }
        if (wm == null) return false;
        List<AnnotationNode> pinned = new ArrayList<>();
        for (Object a : list(raw(an, "at"))) if (a instanceof AnnotationNode at) pinned.add(at);
        for (Object sl : list(raw(an, "slice"))) {
            if (!(sl instanceof AnnotationNode sn)) continue;
            for (String edge : new String[] {"from", "to"}) if (raw(sn, edge) instanceof AnnotationNode at) pinned.add(at);
        }
        boolean changed = false;
        for (AnnotationNode at : pinned) {
            Anchor a = parseAt(values(at), refmap);
            if (a == null || a == POSITIONAL || a.ordinal < 0) continue;
            changed |= remapOne(at, a, vm, wm, where, notes);
        }
        changed |= splitSites(an, refmap, vm, wm, where, notes);
        for (Object c : list(raw(an, "constant"))) {
            if (!(c instanceof AnnotationNode cn)) continue;
            Map<String, Object> cv = values(cn);
            if (!(cv.get("ordinal") instanceof Integer ord) || ord < 0 || cv.containsKey("expandZeroConditions")) continue;
            Object val = null;
            for (String k : new String[] {"intValue", "floatValue", "longValue", "doubleValue", "stringValue", "classValue"}) {
                if (cv.containsKey(k)) val = cv.get(k);
            }
            if (val == null) continue;
            changed |= remapOne(cn, new Anchor("CONSTANT", null, null, null, val, ord), vm, wm, where, notes);
        }
        return changed;
    }

    /**
     * sites-split: an @Inject whose single @At names its anchor without an ordinal fired, on V, at V's
     * occurrences only. On W it must fire at their counterparts (SiteMatch) and never at an occurrence
     * W added. The rule:
     *   every V site matched, W adds sites          one @At per counterpart, pinned by ordinal
     *   some V sites without a unique counterpart   pinned to the counterparts that are known; the V
     *                                               sites left out are reported as lost ("sites-partial")
     *   no V site matched, while W's occurrences    the injector is disabled and reported: firing on all
     *   are not exactly V's                         would hook W-only code, firing on none is the truth
     * Never "fire on all" where W's occurrences are not V's.
     */
    private boolean splitSites(AnnotationNode an, JsonObject refmap, MethodNode vm, MethodNode wm, String where, List<String> notes) {
        if (!an.desc.equals(INJECT) || raw(an, "slice") != null) return false;
        List<Object> ats = list(raw(an, "at"));
        if (ats.size() != 1 || !(ats.get(0) instanceof AnnotationNode at)) return false;
        Anchor a = parseAt(values(at), refmap);
        if (a == null || a == POSITIONAL || a.ordinal >= 0) return false;
        List<AbstractInsnNode> vo = SiteMatch.occurrences(vm, in -> matchesAnchor(in, a));
        List<AbstractInsnNode> wo = SiteMatch.occurrences(wm, in -> matchesAnchor(in, a));
        if (vo.isEmpty() || wo.isEmpty()) return false;
        int[] map = SiteMatch.match(vm, vo, wm, wo);
        List<Integer> kept = new ArrayList<>();
        List<Integer> lost = new ArrayList<>();
        for (int i = 0; i < map.length; i++) {
            if (map[i] >= 0) kept.add(map[i]);
            else lost.add(i);
        }
        if (lost.isEmpty() && wo.size() == vo.size()) return false; // W's occurrences are V's, one for one
        if (kept.isEmpty()) {
            pendingDisable = "sites-unmatched: @Inject " + where + " " + a + ": none of V's " + vo.size()
                + " sites has a unique counterpart among W's " + wo.size() + "; not fired on W's sites";
            return false;
        }
        List<Object> split = new ArrayList<>();
        for (int j : kept) {
            AnnotationNode copy = new AnnotationNode(at.desc);
            copy.values = new ArrayList<>(at.values == null ? List.of() : at.values);
            copy.values.add("ordinal");
            copy.values.add(j);
            split.add(copy);
        }
        for (int i = 0; i + 1 < an.values.size(); i += 2) {
            if ("at".equals(an.values.get(i))) an.values.set(i + 1, split);
        }
        notes.add((lost.isEmpty() ? "sites-split " : "sites-partial ") + where + " " + a + " -> #" + kept + " of W's " + wo.size()
            + " (V's " + vo.size() + " occurrences, by context" + (lost.isEmpty() ? "" : "; V sites " + lost + " have no counterpart and are lost") + ")");
        return true;
    }

    /** set by splitSites when an injector must not fire at all (see there); consumed by repairMixin */
    private String pendingDisable;

    /** the members the step layer's mixins restore on game classes; their code is the layer's (LayerMembers) */
    public LayerMembers layerMembers = LayerMembers.none();

    /** overrides MixinBridge.restoreOverride put back (restoring mixins of the jar being repaired), by target */
    private Map<String, List<MethodNode>> restoredByTarget = Map.of();

    /** the restored override of a target class this selector names, if any */
    static List<MethodNode> restored(Map<String, List<MethodNode>> byTarget, String target, Sel s) {
        for (MethodNode m : byTarget.getOrDefault(target, List.of())) {
            if (m.name.equals(s.name) && (s.desc == null || m.desc.equals(s.desc))) return List.of(m);
        }
        return List.of();
    }

    /** the RESTORED methods of the jar's mixin classes, by target */
    static Map<String, List<MethodNode>> restoredOverrides(Map<String, byte[]> entries) {
        Map<String, List<MethodNode>> out = new HashMap<>();
        for (Map.Entry<String, byte[]> e : entries.entrySet()) {
            if (!e.getKey().endsWith(".class") || !e.getKey().contains("cb$restore$")) continue;
            ClassNode cn = new ClassNode();
            new ClassReader(e.getValue()).accept(cn, ClassReader.SKIP_FRAMES);
            List<String> ts = mixinTargets(cn, null);
            for (MethodNode m : cn.methods) {
                if (!MixinBridge.restoredMark(m)) continue;
                for (String t : ts) out.computeIfAbsent(t, k -> new ArrayList<>()).add(m);
            }
        }
        return out;
    }

    /** an annotation value as stored (nested annotations stay AnnotationNodes) */
    private static Object raw(AnnotationNode an, String key) {
        if (an.values == null) return null;
        for (int i = 0; i + 1 < an.values.size(); i += 2) if (key.equals(an.values.get(i))) return an.values.get(i + 1);
        return null;
    }

    private static boolean remapOne(AnnotationNode holder, Anchor a, MethodNode vm, MethodNode wm, String where, List<String> notes) {
        List<AbstractInsnNode> vo = SiteMatch.occurrences(vm, in -> matchesAnchor(in, a));
        List<AbstractInsnNode> wo = SiteMatch.occurrences(wm, in -> matchesAnchor(in, a));
        if (a.ordinal >= vo.size() || wo.isEmpty()) return false; // not V's site to begin with / nothing to map onto
        int j = SiteMatch.match(vm, vo, wm, wo)[a.ordinal];
        if (j < 0) {
            if (vo.size() != wo.size()) {
                notes.add("ordinal-unmatched " + where + " " + a + ": V's site has no unique counterpart among W's "
                    + wo.size() + " (V " + vo.size() + "), left as it stands");
            }
            return false;
        }
        if (j == a.ordinal) return false;
        for (int i = 0; i + 1 < holder.values.size(); i += 2) {
            if ("ordinal".equals(holder.values.get(i))) {
                holder.values.set(i + 1, j);
                notes.add("ordinal-remap " + where + " " + a + " -> #" + j + " (V " + vo.size() + ", W " + wo.size() + " occurrences, by context)");
                return true;
            }
        }
        return false;
    }

    private Map<String, List<String>> implIndex;

    /** concrete classes in the target jar that directly implement the interface */
    private List<String> implementors(String iface) {
        if (implIndex == null) {
            implIndex = new HashMap<>();
            var en = code.entries();
            while (en.hasMoreElements()) {
                ZipEntry e = en.nextElement();
                if (!e.getName().endsWith(".class")) continue;
                try {
                    ClassReader cr = new ClassReader(code.getInputStream(e).readAllBytes());
                    if ((cr.getAccess() & (Opcodes.ACC_INTERFACE | Opcodes.ACC_ABSTRACT)) != 0) continue;
                    for (String i : cr.getInterfaces()) implIndex.computeIfAbsent(i, k -> new ArrayList<>()).add(cr.getClassName());
                } catch (IOException ex) {
                    // unreadable entry: skip
                }
            }
        }
        return implIndex.getOrDefault(iface, List.of());
    }

    /** replace one @Mixin target (value= Type or targets= string, via refmap if mapped) */
    @SuppressWarnings("unchecked")
    private static boolean retargetMixin(ClassNode mx, JsonObject refmap, String from, String to) {
        boolean done = false;
        for (AnnotationNode a : mx.invisibleAnnotations != null ? mx.invisibleAnnotations : List.<AnnotationNode>of()) {
            if (!a.desc.equals(MIXIN) || a.values == null) continue;
            for (int i = 0; i + 1 < a.values.size(); i += 2) {
                Object v = a.values.get(i + 1);
                if (!(v instanceof List<?> l)) continue;
                List<Object> list = (List<Object>) l;
                for (int j = 0; j < list.size(); j++) {
                    Object o = list.get(j);
                    if (o instanceof Type t && t.getInternalName().equals(from)) {
                        list.set(j, Type.getObjectType(to));
                        done = true;
                    } else if (o instanceof String str) {
                        String mapped = refmapLookup(refmap, str).replace('.', '/');
                        if (mapped.startsWith("L") && mapped.endsWith(";")) mapped = mapped.substring(1, mapped.length() - 1);
                        if (!mapped.equals(from)) continue;
                        if (refmap != null && refmap.has(str)) refmap.addProperty(str, to);
                        else list.set(j, to.replace('/', '.'));
                        done = true;
                    }
                }
            }
        }
        return done;
    }

    // ------------------------------------------------------------------ selectors

    record Sel(String owner, String name, String desc, boolean wildcard) {
        static Sel parse(String spec) {
            String owner = null;
            if (spec.startsWith("L") && spec.contains(";")) {
                int i = spec.indexOf(';');
                owner = spec.substring(1, i);
                spec = spec.substring(i + 1);
            }
            boolean wild = false;
            int p = spec.indexOf('(');
            String name = p >= 0 ? spec.substring(0, p) : spec;
            String desc = p >= 0 ? spec.substring(p) : null;
            if (name.endsWith("*")) { wild = true; name = name.substring(0, name.length() - 1); }
            if (desc != null && desc.endsWith("*")) { wild = true; desc = desc.substring(0, desc.length() - 1); }
            return new Sel(owner, name, desc, wild);
        }

        List<MethodNode> match(ClassNode tc) {
            List<MethodNode> out = new ArrayList<>();
            for (MethodNode m : tc.methods) {
                if (!m.name.equals(name)) continue;
                if (desc != null && !m.desc.equals(desc)) continue;
                out.add(m);
            }
            return out;
        }
    }

    // ------------------------------------------------------------------ anchors

    record Anchor(String kind, String owner, String name, String desc, Object constant, int ordinal, String role) {
        Anchor(String kind, String owner, String name, String desc, Object constant) {
            this(kind, owner, name, desc, constant, -1, "main");
        }

        Anchor(String kind, String owner, String name, String desc, Object constant, int ordinal) {
            this(kind, owner, name, desc, constant, ordinal, "main");
        }

        Anchor role(String r) {
            return new Anchor(kind, owner, name, desc, constant, ordinal, r);
        }

        @Override
        public String toString() {
            return kind + (constant != null ? "=" + constant : " " + (owner != null ? owner + "." : "") + name + (desc != null ? desc : ""))
                + (ordinal >= 0 ? "#" + ordinal : "");
        }
    }

    /** marker for HEAD/RETURN/TAIL/LOAD/STORE/JUMP: a position, nothing to look for */
    static final Anchor POSITIONAL = new Anchor("POS", null, null, null, null);

    /** null = this injector's anchor cannot be evaluated statically */
    List<Anchor> anchors(AnnotationNode an, Map<String, Object> v, JsonObject refmap) {
        List<Anchor> out = new ArrayList<>();
        // slice boundaries are anchors too: a "from" that no longer occurs leaves an
        // empty slice and Mixin scans 0 targets (porting_lib PlayerMixin.modifyResult:
        // from = 2nd isSprinting() call in attack(), which 1.21.2 has only once)
        for (Object sl : list(v.get("slice"))) {
            if (!(sl instanceof Map<?, ?> sm)) return null;
            for (String edge : new String[] {"from", "to"}) {
                Object e = sm.get(edge);
                if (e == null) continue;
                if (!(e instanceof Map<?, ?> em)) return null;
                Anchor a = parseAt(em, refmap);
                if (a == null) return null;
                if (a != POSITIONAL) out.add(a.role(edge));
            }
        }
        for (Object c : list(v.get("constant"))) {
            if (!(c instanceof Map<?, ?> cm)) return null;
            Object val = null;
            for (String k : new String[] {"intValue", "floatValue", "longValue", "doubleValue", "stringValue", "classValue"}) {
                if (cm.containsKey(k)) val = cm.get(k);
            }
            if (val == null || cm.containsKey("expandZeroConditions")) return null;
            int ord = cm.get("ordinal") instanceof Integer i ? i : -1;
            out.add(new Anchor("CONSTANT", null, null, null, val, ord));
        }
        for (Object a : list(v.get("at"))) {
            if (!(a instanceof Map<?, ?> am)) return null;
            String value = String.valueOf(am.get("value"));
            // @ModifyVariable(index = N) at STORE/LOAD needs that slot touched (in the
            // slice): 1.21.2 reshuffled attack()'s locals and slot 8 left the slice
            if ((value.equals("STORE") || value.equals("LOAD")) && v.get("index") instanceof Integer idx && idx >= 0) {
                int op = am.get("opcode") instanceof Integer o ? o : -1;
                out.add(new Anchor("LOCAL", null, value, null, idx, op));
                continue;
            }
            Anchor x = parseAt(am, refmap);
            if (x == null) return null;
            if (x != POSITIONAL) out.add(x);
        }
        return out;
    }

    /** one @At: POSITIONAL, an Anchor (with ordinal), or null when not evaluable */
    static Anchor parseAt(Map<?, ?> am, JsonObject refmap) {
        String value = String.valueOf(am.get("value"));
        int ord = am.get("ordinal") instanceof Integer i ? i : -1;
        switch (value) {
            case "HEAD", "RETURN", "TAIL", "LOAD", "STORE", "JUMP" -> {
                return POSITIONAL;
            }
            case "INVOKE", "INVOKE_ASSIGN", "INVOKE_STRING", "FIELD", "NEW" -> {
                if (!(am.get("target") instanceof String t)) return null;
                String m = refmapLookup(refmap, t);
                if (value.equals("NEW")) {
                    String o = m.startsWith("L") && m.contains(";") && !m.contains("(") ? m.substring(1, m.indexOf(';'))
                        : m.contains("(") ? null : m;
                    if (o == null) {
                        Sel s = Sel.parse(m);
                        o = s.owner != null ? s.owner : Type.getReturnType(s.desc).getInternalName();
                    }
                    return new Anchor("NEW", o, null, null, null, ord);
                }
                if (value.equals("FIELD")) {
                    if (!m.startsWith("L") || !m.contains(";")) return null;
                    String owner = m.substring(1, m.indexOf(';'));
                    String rest = m.substring(m.indexOf(';') + 1);
                    int c = rest.indexOf(':');
                    return new Anchor("FIELD", owner, c >= 0 ? rest.substring(0, c) : rest,
                        c >= 0 ? rest.substring(c + 1) : null, null, ord);
                }
                Sel s = Sel.parse(m);
                if (s.owner == null) return null;
                return new Anchor("INVOKE", s.owner, s.name, s.desc, null, ord);
            }
            case "CONSTANT" -> {
                Object val = null;
                for (Object o : list(am.get("args"))) {
                    String s = String.valueOf(o);
                    int eq = s.indexOf('=');
                    if (eq < 0) continue;
                    String k = s.substring(0, eq).trim(), x = s.substring(eq + 1).trim();
                    try {
                        switch (k) {
                            case "intValue" -> val = Integer.valueOf(x);
                            case "longValue" -> val = Long.valueOf(x.replaceAll("[lL]$", ""));
                            case "floatValue" -> val = Float.valueOf(x);
                            case "doubleValue" -> val = Double.valueOf(x);
                            case "stringValue" -> val = x;
                            default -> { }
                        }
                    } catch (NumberFormatException ex) {
                        return null;
                    }
                }
                if (val == null) return null;
                return new Anchor("CONSTANT", null, null, null, val, ord);
            }
            default -> {
                return null; // MIXINEXTRAS:EXPRESSION, custom points
            }
        }
    }

    private static String fieldDesc(String spec) {
        int c = spec.indexOf(':');
        return c >= 0 ? spec.substring(c + 1) : null;
    }

    static boolean anchorsIn(MethodNode m, ClassNode owner, List<Anchor> anchors) {
        Anchor from = null, to = null;
        for (Anchor a : anchors) {
            if (a.role.equals("from")) from = a;
            else if (a.role.equals("to")) to = a;
        }
        for (Anchor a : anchors) {
            if (a.kind.equals("LOCAL")) {
                if (!localIn(m, a, from, to)) return false;
            } else if (!anchorIn(m, a)) {
                return false;
            }
        }
        return true;
    }

    /** the slot access within [from, to] (whole body when unsliced); opcode = exact insn when given */
    private static boolean localIn(MethodNode m, Anchor a, Anchor from, Anchor to) {
        if (m.instructions == null) return false;
        AbstractInsnNode[] insns = m.instructions.toArray();
        int lo = 0, hi = insns.length - 1;
        if (from != null) {
            int need = Math.max(from.ordinal, 0) + 1, seen = 0;
            lo = -1;
            for (int i = 0; i < insns.length; i++) {
                if (matchesAnchor(insns[i], from) && ++seen >= need) { lo = i; break; }
            }
            if (lo < 0) return false;
        }
        if (to != null) {
            int need = Math.max(to.ordinal, 0) + 1, seen = 0;
            hi = -1;
            for (int i = 0; i < insns.length; i++) {
                if (matchesAnchor(insns[i], to) && ++seen >= need) { hi = i; break; }
            }
            if (hi < 0) return false;
        }
        int slot = (Integer) a.constant;
        boolean store = a.name.equals("STORE");
        for (int i = lo; i <= hi && i < insns.length; i++) {
            if (!(insns[i] instanceof VarInsnNode vi) || vi.var != slot) continue;
            int op = vi.getOpcode();
            if (a.ordinal >= 0 ? op == a.ordinal
                    : store ? op >= Opcodes.ISTORE && op <= Opcodes.ASTORE : op >= Opcodes.ILOAD && op <= Opcodes.ALOAD) {
                return true;
            }
        }
        return false;
    }

    static boolean anchorIn(MethodNode m, Anchor a) {
        if (m.instructions == null) return false;
        int need = Math.max(a.ordinal, 0) + 1, seen = 0;
        for (AbstractInsnNode in : m.instructions) {
            if (!matchesAnchor(in, a)) continue;
            if (++seen >= need) return true;
        }
        return false;
    }

    static boolean matchesAnchor(AbstractInsnNode in, Anchor a) {
        {
            switch (a.kind) {
                case "INVOKE" -> {
                    // Mixin's target selector compares the owner too
                    if (in instanceof MethodInsnNode mi && mi.name.equals(a.name)
                            && (a.owner == null || mi.owner.equals(a.owner))
                            && (a.desc == null || mi.desc.equals(a.desc))) return true;
                }
                case "FIELD" -> {
                    if (in instanceof FieldInsnNode fi && fi.name.equals(a.name)
                            && (a.owner == null || fi.owner.equals(a.owner))
                            && (a.desc == null || fi.desc.equals(a.desc))) return true;
                }
                case "NEW" -> {
                    if (in instanceof TypeInsnNode ti && ti.getOpcode() == Opcodes.NEW && ti.desc.equals(a.owner)) return true;
                }
                case "CONSTANT" -> {
                    if (constantMatches(in, a.constant)) return true;
                }
                default -> { }
            }
        }
        return false;
    }

    private static boolean constantMatches(AbstractInsnNode in, Object c) {
        int op = in.getOpcode();
        if (in instanceof LdcInsnNode ldc) return ldc.cst.equals(c);
        // @Constant(classValue=X) is matched against instanceof X (and checkcast) as well
        if (c instanceof Type t && in instanceof TypeInsnNode ti
                && (op == Opcodes.INSTANCEOF || op == Opcodes.CHECKCAST)) return ti.desc.equals(t.getInternalName());
        if (c instanceof Integer i) {
            if (op >= Opcodes.ICONST_M1 && op <= Opcodes.ICONST_5) return i == op - Opcodes.ICONST_0;
            if (in instanceof IntInsnNode ii && (op == Opcodes.BIPUSH || op == Opcodes.SIPUSH)) return ii.operand == i;
        }
        // boxed equality, as Mixin's BeforeConstant compares: -0.0f is an LDC, not FCONST_0
        if (c instanceof Long l && (op == Opcodes.LCONST_0 || op == Opcodes.LCONST_1)) return l == op - Opcodes.LCONST_0;
        if (c instanceof Float f && op >= Opcodes.FCONST_0 && op <= Opcodes.FCONST_2) return f.equals((float) (op - Opcodes.FCONST_0));
        if (c instanceof Double d && (op == Opcodes.DCONST_0 || op == Opcodes.DCONST_1)) return d.equals((double) (op - Opcodes.DCONST_0));
        return false;
    }

    /** methods of the same class the body calls (incl. lambdas it creates) */
    private static List<MethodNode> callees(MethodNode m, ClassNode tc) {
        Set<String> keys = new LinkedHashSet<>();
        if (m.instructions != null) {
            for (AbstractInsnNode in : m.instructions) {
                if (in instanceof MethodInsnNode mi && mi.owner.equals(tc.name)) keys.add(mi.name + mi.desc);
            }
        }
        List<MethodNode> out = new ArrayList<>();
        for (MethodNode c : tc.methods) if (keys.contains(c.name + c.desc) && c != m) out.add(c);
        return out;
    }

    /** the one call in the targets that now stands where the anchor stood, or null */
    private String movedAnchor(List<MethodNode> cands, ClassNode tc, Anchor a) {
        Set<String> redescribed = new LinkedHashSet<>();
        Set<String> wrappers = new LinkedHashSet<>();
        for (MethodNode m : cands) {
            if (m.instructions == null) continue;
            for (AbstractInsnNode in : m.instructions) {
                if (a.kind.equals("INVOKE") && in instanceof MethodInsnNode mi) {
                    if (mi.name.equals(a.name) && !mi.desc.equals(a.desc)) {
                        redescribed.add("L" + mi.owner + ";" + mi.name + mi.desc);
                    } else if (bodyHas(mi.owner, mi.name, mi.desc, a)) {
                        wrappers.add("L" + mi.owner + ";" + mi.name + mi.desc);
                    }
                } else if (a.kind.equals("FIELD") && in instanceof FieldInsnNode fi
                        && fi.name.equals(a.name) && !fi.desc.equals(a.desc)) {
                    redescribed.add("L" + fi.owner + ";" + fi.name + ":" + fi.desc);
                }
            }
        }
        if (redescribed.size() == 1) return redescribed.iterator().next();
        if (redescribed.isEmpty() && wrappers.size() == 1) return wrappers.iterator().next();
        return null;
    }

    /** does owner.name(desc) -- declared on owner or inherited from a superclass -- contain the anchor? */
    private boolean bodyHas(String owner, String name, String desc, Anchor a) {
        for (String c = owner; c != null; ) {
            ClassNode cn = target(c);
            if (cn == null) return false;
            for (MethodNode m : cn.methods) {
                if (m.name.equals(name) && m.desc.equals(desc)) return anchorIn(m, a);
            }
            c = cn.superName;
        }
        return false;
    }

    /** rewrite the single @At's target (refmap entry if it came from one, else the annotation) */
    @SuppressWarnings("unchecked")
    static boolean setAtTarget(AnnotationNode an, JsonObject refmap, String value) {
        for (int i = 0; i + 1 < an.values.size(); i += 2) {
            if (!"at".equals(an.values.get(i))) continue;
            Object v = an.values.get(i + 1);
            List<Object> ats = v instanceof List<?> l ? (List<Object>) l : List.of(v);
            if (ats.size() != 1 || !(ats.get(0) instanceof AnnotationNode at)) return false;
            for (int j = 0; j + 1 < at.values.size(); j += 2) {
                if ("target".equals(at.values.get(j)) && at.values.get(j + 1) instanceof String key) {
                    if (refmap != null && refmap.has(key)) refmap.addProperty(key, value);
                    else at.values.set(j + 1, value);
                    return true;
                }
            }
        }
        return false;
    }

    // ------------------------------------------------------------------ handler shape

    /**
     * Does the handler take the target method's arguments? Only then does a
     * retarget change what it receives. @Inject: anything before the trailing
     * CallbackInfo(Returnable). Other injectors: more parameters than the
     * anchor's own operands is conservatively treated as capturing.
     */
    static boolean capturesTargetArgs(AnnotationNode an, MethodNode h) {
        Type[] args = Type.getArgumentTypes(h.desc);
        if (an.desc.equals(INJECT)) {
            int ci = -1;
            for (int i = 0; i < args.length; i++) {
                String d = args[i].getDescriptor();
                if (d.equals(CI) || d.equals(CIR)) { ci = i; break; }
            }
            return ci != 0 || args.length > 1; // args before ci, or locals captured after it
        }
        if (an.desc.endsWith("/ModifyConstant;") || an.desc.endsWith("/ModifyReturnValue;")
                || an.desc.endsWith("/ModifyExpressionValue;") || an.desc.endsWith("/ModifyVariable;")) {
            return args.length > 1;
        }
        return true; // Redirect / ModifyArg(s) / Wrap*: operand shape is call-specific; don't move them
    }

    /**
     * As {@link #capturesTargetArgs(AnnotationNode, MethodNode)}, with the injector's one anchor known: a
     * handler of a call it handles itself takes the call's operands (receiver and arguments; an
     * Operation after them for @WrapOperation; the one argument for @ModifyArg, the Args for @ModifyArgs),
     * and only parameters beyond those are the target's -- the same call in another method hands the
     * handler the same operands.
     */
    static boolean capturesTargetArgs(AnnotationNode an, MethodNode h, List<Anchor> anchors) {
        if (!capturesTargetArgs(an, h)) return false;
        if (anchors == null || anchors.size() != 1 || !anchors.get(0).kind.equals("INVOKE") || anchors.get(0).desc == null) return true;
        Anchor a = anchors.get(0);
        if (a.name.equals("<init>")) return true;
        Type[] hp = Type.getArgumentTypes(h.desc);
        int n = Type.getArgumentTypes(a.desc).length;
        String recv = a.owner == null ? null : Type.getObjectType(a.owner).getDescriptor();
        boolean withRecv = hp.length > 0 && recv != null && hp[0].getDescriptor().equals(recv);
        int operands = n + (withRecv ? 1 : 0);
        if (an.desc.endsWith("/Redirect;") || an.desc.endsWith("/WrapWithCondition;")) return hp.length != operands;
        if (an.desc.endsWith("/WrapOperation;")) return hp.length != operands + 1;
        if (an.desc.endsWith("/ModifyArg;") || an.desc.endsWith("/ModifyArgs;")) return hp.length != 1;
        return true;
    }

    static boolean touchesThisFree(MethodNode h) {
        if (h.instructions == null) return true;
        for (AbstractInsnNode in : h.instructions) {
            if (in instanceof VarInsnNode vi && vi.var == 0) return false;
            if (in instanceof IincInsnNode ii && ii.var == 0) return false;
        }
        return true;
    }

    /** instance handler -> static, only if it never touches slot 0 */
    private static boolean makeStatic(MethodNode h) {
        if (h.instructions != null) {
            for (AbstractInsnNode in : h.instructions) {
                if (in instanceof VarInsnNode vi && vi.var == 0) return false;
                if (in instanceof IincInsnNode ii && ii.var == 0) return false;
            }
            for (AbstractInsnNode in : h.instructions) {
                if (in instanceof VarInsnNode vi) vi.var--;
                if (in instanceof IincInsnNode ii) ii.var--;
            }
        }
        if (h.localVariables != null) {
            h.localVariables.removeIf(lv -> lv.index == 0);
            for (LocalVariableNode lv : h.localVariables) lv.index--;
        }
        h.access |= Opcodes.ACC_STATIC;
        h.maxLocals = Math.max(0, h.maxLocals - 1);
        return true;
    }

    // ------------------------------------------------------------------ annotations / refmap

    static List<AnnotationNode> annotations(MethodNode m) {
        List<AnnotationNode> out = new ArrayList<>();
        if (m.visibleAnnotations != null) out.addAll(m.visibleAnnotations);
        if (m.invisibleAnnotations != null) out.addAll(m.invisibleAnnotations);
        return out;
    }

    static Map<String, Object> values(AnnotationNode an) {
        Map<String, Object> out = new LinkedHashMap<>();
        if (an.values == null) return out;
        for (int i = 0; i + 1 < an.values.size(); i += 2) out.put((String) an.values.get(i), unwrap(an.values.get(i + 1)));
        return out;
    }

    private static Object unwrap(Object o) {
        if (o instanceof AnnotationNode sub) return values(sub);
        if (o instanceof List<?> l) {
            List<Object> out = new ArrayList<>();
            for (Object x : l) out.add(unwrap(x));
            return out;
        }
        if (o instanceof String[] e) return e[1]; // enum
        return o;
    }

    static List<Object> list(Object o) {
        if (o == null) return List.of();
        if (o instanceof List<?> l) return new ArrayList<>(l);
        return List.of(o);
    }

    static List<String> strings(Object o) {
        List<String> out = new ArrayList<>();
        for (Object x : list(o)) if (x instanceof String s) out.add(s);
        return out;
    }

    private static void dropInjector(MethodNode h, AnnotationNode an) {
        if (h.visibleAnnotations != null) h.visibleAnnotations.remove(an);
        if (h.invisibleAnnotations != null) h.invisibleAnnotations.remove(an);
    }

    private static void setRequireZero(AnnotationNode an) {
        if (an.values == null) an.values = new ArrayList<>();
        for (int i = 0; i + 1 < an.values.size(); i += 2) {
            if ("require".equals(an.values.get(i))) {
                an.values.set(i + 1, 0);
                return;
            }
        }
        an.values.add("require");
        an.values.add(0);
    }

    @SuppressWarnings("unchecked")
    static void setMethodSelector(AnnotationNode an, int index, String value) {
        for (int i = 0; i + 1 < an.values.size(); i += 2) {
            if ("method".equals(an.values.get(i))) {
                Object v = an.values.get(i + 1);
                if (v instanceof List<?> l) ((List<Object>) l).set(index, value);
                else an.values.set(i + 1, value);
            }
        }
    }

    static List<String> mixinTargets(ClassNode mx, JsonObject refmap) {
        List<String> out = new ArrayList<>();
        for (AnnotationNode a : mx.invisibleAnnotations != null ? mx.invisibleAnnotations : List.<AnnotationNode>of()) {
            if (!a.desc.equals(MIXIN)) continue;
            Map<String, Object> v = values(a);
            for (Object t : list(v.get("value"))) if (t instanceof Type ty) out.add(ty.getInternalName());
            for (Object t : list(v.get("targets"))) {
                if (t instanceof String s) {
                    String m = refmapLookup(refmap, s).replace('.', '/');
                    if (m.startsWith("L") && m.endsWith(";")) m = m.substring(1, m.length() - 1);
                    out.add(m);
                }
            }
        }
        return out;
    }

    static String refmapLookup(JsonObject refmap, String s) {
        if (refmap == null) return s;
        if (refmap.has(s)) return refmap.get(s).getAsString();
        return s;
    }

    static void refmapPut(JsonObject refmap, String key, String value) {
        refmap.addProperty(key, value);
    }
}
