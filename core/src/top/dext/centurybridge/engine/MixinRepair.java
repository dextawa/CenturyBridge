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

    /** disabled: injectors switched to require=0 because their anchor is gone -- a KNOWN loss, reported */
    public record Outcome(Set<String> fatal, Map<String, String> fatalWhy, List<String> repairs, List<String> disabled) {}

    public MixinRepair(Path codeJar) throws IOException {
        this.code = new ZipFile(codeJar.toFile());
    }

    ClassNode target(String owner) {
        return cache.computeIfAbsent(owner, o -> {
            try {
                ZipEntry e = code.getEntry(o + ".class");
                if (e == null) return null;
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
        if (targets.stream().noneMatch(t -> t.startsWith("net/minecraft/"))) return null;
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
                List<Anchor> anchors = anchors(an, v, refmap); // null = anchor not statically evaluable
                int require = v.get("require") instanceof Integer i && i >= 0 ? i : defaultRequire;
                boolean capturesArgs = capturesTargetArgs(an, handler);
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
        if (c instanceof Long l && (op == Opcodes.LCONST_0 || op == Opcodes.LCONST_1)) return l == op - Opcodes.LCONST_0;
        if (c instanceof Float f && op >= Opcodes.FCONST_0 && op <= Opcodes.FCONST_2) return f == op - Opcodes.FCONST_0;
        if (c instanceof Double d && (op == Opcodes.DCONST_0 || op == Opcodes.DCONST_1)) return d == op - Opcodes.DCONST_0;
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
    private static boolean capturesTargetArgs(AnnotationNode an, MethodNode h) {
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

    private static boolean touchesThisFree(MethodNode h) {
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
