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
import org.objectweb.asm.tree.TryCatchBlockNode;
import org.objectweb.asm.tree.LabelNode;
import org.objectweb.asm.tree.JumpInsnNode;
import org.objectweb.asm.tree.InsnNode;
import org.objectweb.asm.tree.InsnList;
import org.objectweb.asm.tree.FrameNode;
import org.objectweb.asm.tree.FieldNode;
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

    /** a class's @Mixin annotation (an empty one when it has none) */
    static AnnotationNode mixinAnnotation(ClassNode cn) {
        for (List<AnnotationNode> l : java.util.Arrays.asList(cn.invisibleAnnotations, cn.visibleAnnotations)) {
            for (AnnotationNode a : l == null ? List.<AnnotationNode>of() : l) {
                if (a.desc.equals(MIXIN)) {
                    return a;
                }
            }
        }
        return new AnnotationNode(MIXIN);
    }
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
        index();
        return invokedUnproven || invokedNames.contains(name);
    }

    /** the methods ("owner.name desc") calling a method of this name ("?" for a method reference); null if unproven */
    private Map<String, Set<String>> callersByName;

    public synchronized Set<String> callers(String name) {
        index();
        return invokedUnproven ? null : callersByName.getOrDefault(name, Set.of());
    }

    /** class -> the other classes of the code jar whose code initializes it (lazily, with invokedNames) */
    private Map<String, Set<String>> initializersOf;

    /**
     * Whether code of a class other than {@code owner} itself makes the JVM initialize {@code owner}: a new,
     * a static field access or a static call naming it, or a class extending or implementing it (initializing a
     * class initializes its superclass first, and an interface declaring default methods with its implementor;
     * any implementor counts, so the answer errs towards reached). This is how the code jar's code reaches
     * {@code <clinit>}, which nothing calls by name. True when a class could not be read.
     */
    public synchronized boolean initialized(String owner) {
        index();
        return invokedUnproven || !initializersOf.getOrDefault(owner, Set.of()).isEmpty();
    }

    private void index() {
        if (invokedNames == null) {
            Set<String> out = new java.util.HashSet<>();
            Map<String, Set<String>> by = new HashMap<>();
            Map<String, Set<String>> init = new HashMap<>();
            callersByName = by;
            initializersOf = init;
            for (ZipEntry e : java.util.Collections.list(code.entries())) {
                if (!e.getName().endsWith(".class")) continue;
                try {
                    String cls = e.getName().substring(0, e.getName().length() - 6);
                    java.util.function.Consumer<String> inits = o -> {
                        if (o != null && !o.equals(cls)) init.computeIfAbsent(o, k -> new java.util.HashSet<>()).add(cls);
                    };
                    new ClassReader(code.getInputStream(e).readAllBytes()).accept(new org.objectweb.asm.ClassVisitor(Opcodes.ASM9) {
                        @Override
                        public void visit(int version, int access, String n, String sig, String sup, String[] itfs) {
                            inits.accept(sup);
                            if (itfs != null) {
                                for (String i : itfs) inits.accept(i);
                            }
                        }

                        @Override
                        public org.objectweb.asm.MethodVisitor visitMethod(int access, String n, String d, String sig, String[] ex) {
                            String caller = cls + "." + n + d;
                            return new org.objectweb.asm.MethodVisitor(Opcodes.ASM9) {
                                @Override
                                public void visitTypeInsn(int op, String type) {
                                    if (op == Opcodes.NEW) inits.accept(type);
                                }

                                @Override
                                public void visitFieldInsn(int op, String owner, String fn, String fd) {
                                    if (op == Opcodes.GETSTATIC || op == Opcodes.PUTSTATIC) inits.accept(owner);
                                }

                                @Override
                                public void visitMethodInsn(int op, String owner, String mn, String md, boolean itf) {
                                    out.add(mn);
                                    by.computeIfAbsent(mn, k -> new java.util.HashSet<>()).add(caller);
                                    if (op == Opcodes.INVOKESTATIC) inits.accept(owner);
                                }

                                @Override
                                public void visitInvokeDynamicInsn(String in, String id, org.objectweb.asm.Handle bsm, Object... args) {
                                    for (Object a : args) {
                                        if (a instanceof org.objectweb.asm.Handle h) {
                                            out.add(h.getName());
                                            by.computeIfAbsent(h.getName(), k -> new java.util.HashSet<>()).add("?");
                                        }
                                    }
                                }

                                @Override
                                public void visitLdcInsn(Object v) {
                                    if (v instanceof org.objectweb.asm.Handle h) {
                                        out.add(h.getName());
                                        by.computeIfAbsent(h.getName(), k -> new java.util.HashSet<>()).add("?");
                                    }
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

    /**
     * The step's W (set by Main): the release whose layer restores the members {@link #layerMembers} holds. A kept
     * injector on such a member is written to the jar as {@link #MIXED_DIR}{@code <W>.txt}, one line per member
     * and mixin, three tab-separated columns "{@code <owner>.<name><desc>}", the mixin class, its config section
     * ("mixins" / "client" / "server": a client-section mixin does not apply on a dedicated server), in the layer's own names (past the wall: W's real
     * names) -- the conversion-time signal "a converted mod mixed this restored member" (smx4 (l)). A layer route
     * that cannot compare answers consults the restored member only when a loaded mod's jar lists it (the layer
     * reads every jar's file through its class loader's getResources), and otherwise leaves W's own path exactly
     * as it is. Lines of mixins stripped later in the conversion are dropped ({@link #dropMixed}). A kept injector on a
     * facade's member (the step renames the target class to one of the layer's) is written too, keyed by the facade's
     * name (s6e B1); so is an injector moved onto a layer class's static (s6e B6)
     */
    public String stepTo;
    public static final String MIXED_DIR = "META-INF/centurybridge/mixed/";
    /** restored members kept injectors / overwrites select, by mixin class (one repair() run) */
    private final Map<String, Set<String>> mixedByMixin = new LinkedHashMap<>();
    /** the config section each of those mixins is listed in ("mixins" / "client" / "server") */
    private final Map<String, String> mixedSide = new HashMap<>();

    /** drop the mixed-member lines of mixins the conversion stripped, in every step's file of the jar */
    public static void dropMixed(Map<String, byte[]> entries, Set<String> dead) {
        if (dead.isEmpty()) return;
        for (Map.Entry<String, byte[]> e : new ArrayList<>(entries.entrySet())) {
            if (!e.getKey().startsWith(MIXED_DIR) || !e.getKey().endsWith(".txt")) continue;
            StringBuilder b = new StringBuilder();
            boolean cut = false;
            for (String line : new String(e.getValue(), StandardCharsets.UTF_8).split("\n")) {
                String[] f = line.split("\t");
                if (f.length > 1 && dead.contains(f[1])) {
                    cut = true;
                } else if (!line.isBlank()) {
                    b.append(line).append('\n');
                }
            }
            if (!cut) continue;
            if (b.length() == 0) entries.remove(e.getKey());
            else entries.put(e.getKey(), b.toString().getBytes(StandardCharsets.UTF_8));
        }
    }

    /** writes this run's mixed members (mixins not fatal here) to the step's file, merged with what it holds */
    private void writeMixed(Map<String, byte[]> entries, Set<String> fatal) {
        if (stepTo == null || mixedByMixin.isEmpty()) return;
        String name = MIXED_DIR + stepTo + ".txt";
        Set<String> lines = new java.util.TreeSet<>();
        if (entries.get(name) != null) {
            for (String l : new String(entries.get(name), StandardCharsets.UTF_8).split("\n")) {
                if (!l.isBlank()) lines.add(l);
            }
        }
        for (Map.Entry<String, Set<String>> e : mixedByMixin.entrySet()) {
            if (fatal.contains(e.getKey())) continue;
            for (String k : e.getValue()) lines.add(k + "\t" + e.getKey() + "\t" + mixedSide.getOrDefault(e.getKey(), "mixins"));
        }
        if (lines.isEmpty()) return;
        entries.put(name, (String.join("\n", lines) + "\n").getBytes(StandardCharsets.UTF_8));
    }

    private void mixed(String mixin, String owner, List<MethodNode> cands) {
        for (MethodNode m : cands) {
            mixedByMixin.computeIfAbsent(mixin, k -> new java.util.TreeSet<>()).add(owner + "." + m.name + m.desc);
        }
        if (curKey != null) mixedSide.put(mixin, curKey);
    }

    /**
     * C23: an injector selector naming its method by a bare name (method = "method_4725", how most mods write it),
     * respelled with the descriptor of the V method it named -- this model is V's code. Mixin resolves a bare name with
     * the default quantifier as SINGLE (MemberInfo.configure(SELECT_MEMBER), javap of sponge-mixin 0.17.3): the target
     * class's first method of that name. Spelled, the selector takes every route a spelled one takes (mixinTargets,
     * mixinMoves, follow+W, restored members), and on W it cannot bind to another method that took the name. Only where
     * every target class of the mixin names the same V method; regex and wildcard selectors stay. Run before the step
     * renames anything (the mixin's targets are V's classes). Returns the report notes.
     */
    public List<String> respellBare(Map<String, byte[]> entries) {
        List<String> notes = new ArrayList<>();
        byte[] fmjBytes = entries.get("fabric.mod.json");
        if (fmjBytes == null) return notes;
        JsonObject fmj;
        try {
            fmj = GSON.fromJson(new String(fmjBytes, StandardCharsets.UTF_8), JsonObject.class);
        } catch (RuntimeException ex) {
            return notes;
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
                    ClassNode mx = new ClassNode();
                    new ClassReader(bytes).accept(mx, 0);
                    List<String> targets = mixinTargets(mx, table);
                    boolean changed = false;
                    for (MethodNode h : mx.methods) {
                        for (AnnotationNode an : annotations(h)) {
                            List<String> methods = strings(values(an).get("method"));
                            for (int idx = 0; idx < methods.size(); idx++) {
                                String sel = methods.get(idx);
                                if (sel.startsWith("/")) continue;
                                String mapped = refmapLookup(table, sel);
                                Sel s = Sel.parse(mapped);
                                if (s.desc != null || s.wildcard || s.name.isEmpty()) continue;
                                String desc = null;
                                boolean ok = true;
                                for (String o : s.owner != null ? List.of(s.owner) : targets) {
                                    ClassNode vc = target(o);
                                    MethodNode first = null;
                                    if (vc != null) {
                                        for (MethodNode m : vc.methods) {
                                            if (m.name.equals(s.name)) { first = m; break; }
                                        }
                                    }
                                    if (first == null || desc != null && !desc.equals(first.desc)) { ok = false; break; }
                                    desc = first.desc;
                                }
                                if (!ok || desc == null) continue;
                                String repl = mapped + desc;
                                if (!mapped.equals(sel)) {
                                    refmapPut(table, sel, repl);
                                    refmapDirty = true;
                                } else {
                                    setMethodSelector(an, idx, repl);
                                    changed = true;
                                }
                                notes.add("bare selector spelled as on V: " + cls.substring(cls.lastIndexOf('/') + 1) + "." + h.name + " -> "
                                    + repl + " (the first method of that name, as Mixin resolved it)");
                            }
                        }
                    }
                    if (changed) {
                        ClassWriter cw = new ClassWriter(0);
                        mx.accept(cw);
                        entries.put(cls + ".class", cw.toByteArray());
                    }
                }
            }
            if (refmapDirty) entries.put(refmapName, GSON.toJson(refmapRoot).getBytes(StandardCharsets.UTF_8));
        }
        return notes;
    }

    /** rewrites mixin classes / refmaps in {@code entries} in place */
    public Outcome repair(Map<String, byte[]> entries) {
        mixedByMixin.clear();
        mixedSide.clear();
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
            boolean cfgDirty = false;
            curPkg = pkg;
            for (String key : new String[] {"mixins", "client", "server"}) {
                if (!cfg.has(key)) continue;
                curKey = key;
                crossAllowed = true;
                for (JsonElement e : cfg.getAsJsonArray(key).deepCopy()) {
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
                cfgDirty |= flushCross(entries, cfg);
                crossAllowed = false;
            }
            if (cfgDirty) entries.put(cfgName, GSON.toJson(cfg).getBytes(StandardCharsets.UTF_8));
            if (refmapDirty) {
                entries.put(refmapName, GSON.toJson(refmapRoot).getBytes(StandardCharsets.UTF_8));
            }
        }
        retargetInvokerCalls(entries);
        invokerMoves.clear();
        writeMixed(entries, fatal);
        return new Outcome(fatal, why, repairs, disabledAll);
    }

    private record MixinResult(byte[] bytes, boolean refmapChanged, List<String> repairs, List<String> disabled,
                               String fatalWhy) {}

    // ------------------------------------------------------------------ per mixin

    private MixinResult repairMixin(byte[] bytes, String cls, JsonObject refmap, int defaultRequire) {
        ClassNode mx = new ClassNode();
        new ClassReader(bytes).accept(mx, 0);
        curMixin = mx.name;
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
        // smx4d (6): an anonymous class javac renumbered (ByteBufCodecs$20 -> $22 at 1.21.2, the same methods)
        for (String t : new ArrayList<>(targets)) {
            String to = renumberedAnonymous(mx, refmap, t);
            if (to != null && retargetMixin(mx, refmap, t, to)) {
                classChanged = true;
                if (refmap != null) refmapChanged = true;
                targets.set(targets.indexOf(t), to);
                repairs.add("anonymous-renumbered " + shortName + ": " + t + " -> " + to
                    + " (the one sibling anonymous class declaring every member the mixin names)");
            }
        }
        if ((mx.access & Opcodes.ACC_INTERFACE) != 0 && moveInvokers(mx, refmap, targets, shortName, repairs)) {
            classChanged = true;
        }

        boolean[] dirty = {false, false}; // class, refmap
        for (MethodNode handler : new ArrayList<>(mx.methods)) { // a copy: reshapeCaptured adds a handler
            int marks = handler.invisibleAnnotations == null ? 0 : handler.invisibleAnnotations.size();
            Object[] notBridged = takeNotBridged(handler); // K2: MixinBridge could not carry it ({why, locals})
            if (handler.invisibleAnnotations != null && handler.invisibleAnnotations.size() != marks) dirty[0] = true;
            for (AnnotationNode an : annotations(handler)) {
                Map<String, Object> v = values(an);
                List<String> methods = strings(v.get("method"));
                if (methods.isEmpty()) continue;
                pendingDisable = null;
                boolean remapped = false; // an ordinal moved to V's site's counterpart here: repaired, not left as written
                if (source != null && remapOrdinals(an, refmap, methods, targets, shortName + "." + handler.name, repairs)) {
                    remapped = true;
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
                boolean localsJudged = false; // K1 judged the handler's captured locals against W's frames
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
                        if (!cands.isEmpty() && cands.stream().allMatch(layerMembers::isMember)) {
                            // Mixin refuses an injector into a member another mixin merged at a priority not below
                            // its own (except HEAD / RETURN / TAIL): InvalidInjectionException at apply. Off, open
                            int mp = values(mixinAnnotation(mx)).get("priority") instanceof Integer p ? p : LayerMembers.DEFAULT_PRIORITY;
                            Map<String, Object> vv = v;
                            List<MethodNode> ok = cands.stream().filter(m -> layerMembers.injectable(m, mp, vv)).toList();
                            if (ok.isEmpty()) {
                                if (deadWhy == null) deadWhy = "restored member merged at priority " + layerMembers.priority(cands.get(0))
                                    + ", not below the mod's " + mp + ": @" + kind + " " + shortName + "." + handler.name + " -> "
                                    + s.name + (s.desc != null ? s.desc : "");
                                continue;
                            }
                            cands = ok;
                        }
                        String where = shortName + "." + handler.name + " -> " + owner.substring(owner.lastIndexOf('/') + 1)
                            + "." + s.name + (s.desc != null ? s.desc : "");
                        // the mixinMoves key the selector names: spelled, or a bare name resolved in V as Mixin resolved it there (C23)
                        String moveKey = !cands.isEmpty() ? null : s.desc != null ? owner + "." + s.name + s.desc : bareMoveKey(owner, s);
                        if (moveKey != null && mixinMoves.containsKey(moveKey)) {
                            // the method moved, the same code, to another W class (mixinMoves): onto a mixin of that class
                            List<String[]> self = mixinMoveSelf.get(moveKey);
                            String to = mixinMoves.get(moveKey);
                            String whereK = s.desc != null ? where : where + " (bare: V's " + moveKey.substring(moveKey.lastIndexOf('.', moveKey.indexOf('(')) + 1) + ")";
                            String why0 = movable(mx, handler, an, anchors, to, owner, self);
                            if (why0 == null) {
                                fixNotes.add("move " + whereK + " => " + to + " (mixinMoves: the same code in another class)");
                                fixes.add(() -> {
                                    moveInjector(mx, handler, an, to, owner, self);
                                    dirty[0] = true;
                                });
                            } else if (deadWhy == null) {
                                deadWhy = "target gone: @" + kind + " " + whereK + " (mixinMoves to " + to + ": " + why0 + ")";
                            }
                            continue;
                        }
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
                            } else {
                                String toLayer = s.desc != null && staticMove != null ? staticMove.apply(owner + "." + s.name + s.desc) : null;
                                if (toLayer != null && toLayer.startsWith("top/dext/")) {
                                    // s6e B6, the last resort for a V static W lost: the step staticRedirects it into a layer
                                    // class (a class mixin cannot restore a non-private static), which holds V's code for
                                    // converted mods' calls. The injector moves onto a mixin of that method, and the move is
                                    // recorded in the mixed signal: a layer route sending W's path through the method
                                    // (@CbVanillaReads) consults it only when mixed. Taken only where nothing W runs itself
                                    // holds the code (no restored member, no mixinMoves, no same-name method to drop onto)
                                    String why0 = (handler.access & Opcodes.ACC_STATIC) == 0 ? "an instance handler on a static"
                                        : movable(mx, handler, an, anchors, toLayer, owner, null);
                                    if (why0 == null) {
                                        fixNotes.add("move " + where + " => " + toLayer + " (staticRedirects: V's static held by a layer class)");
                                        fixes.add(() -> {
                                            moveInjector(mx, handler, an, toLayer, owner, null);
                                            int d = toLayer.indexOf('.');
                                            ClassNode lc = target(toLayer.substring(0, d));
                                            mixed(crossMixinName(toLayer.substring(0, d)), toLayer.substring(0, d),
                                                new Sel(null, toLayer.substring(d + 1, toLayer.indexOf('(')), toLayer.substring(toLayer.indexOf('(')), false).match(lc));
                                            dirty[0] = true;
                                        });
                                    } else if (deadWhy == null) {
                                        deadWhy = "target gone: @" + kind + " " + where + " (staticRedirects to " + toLayer + ": " + why0 + ")";
                                    }
                                } else if (deadWhy == null) {
                                    deadWhy = "target gone: @" + kind + " " + where;
                                }
                            }
                            continue;
                        }
                        if (anchors == null || anchors.isEmpty() || cands.stream().anyMatch(m -> anchorsIn(m, tc, anchors))) {
                            // the handler's captured target arguments must be what the target takes now (smx1 CASE 14)
                            List<MethodNode> hit = anchors == null || anchors.isEmpty() ? cands
                                : cands.stream().filter(m -> anchorsIn(m, tc, anchors)).toList();
                            CapturedFit fit = capturedFit(an, handler, anchors, hit, owner);
                            if (fit.why() != null) {
                                if (deadWhy == null) deadWhy = "captured args: @" + kind + " " + where + " (" + fit.why() + ")";
                                continue;
                            }
                            if (fit.changes()) {
                                fixNotes.add("captured-args " + where + " " + fit.note()
                                    + " (the target's arguments changed; a wrapper handler takes W's and calls the mod's handler with V's)");
                                fixes.add(() -> {
                                    reshapeCaptured(mx, handler, fit);
                                    dirty[0] = true;
                                });
                                continue;
                            }
                            // smixin K1: captured locals (LocalCapture after the CallbackInfo, MixinExtras @Local) judged
                            // against V's and W's frames at the injection point (MixinLocals)
                            ClassNode vtc = source != null ? source.target(owner) : null;
                            List<MethodNode> vms = vtc != null ? s.match(vtc) : List.of();
                            MixinLocals.Verdict lv = vms.size() == 1
                                ? MixinLocals.judge(this, source, an, handler, hit, tc, vtc, vms.get(0), refmap) : null;
                            if (lv != null && lv.why() != null) {
                                if (deadWhy == null) deadWhy = "captured locals: @" + kind + " " + where + " (" + lv.why() + ")";
                                continue;
                            }
                            if (lv != null && lv.fix() != null) {
                                fixNotes.add("locals-remap " + where + " " + lv.note());
                                fixes.add(() -> {
                                    lv.fix().run();
                                    dirty[0] = true;
                                });
                                continue; // a repaired injector: the fix runs when nothing else keeps it live
                            }
                            if (cands.stream().allMatch(layerMembers::isMember)) {
                                mixed(mx.name, owner, cands); // a converted mod mixes the layer's restored member
                            } else if (owner.startsWith("top/dext/") && layerEntry(owner) != null) {
                                // a member of a facade (a class the step renames to one of the layer's, s6e B1:
                                // TerracottaBlock): keyed by the facade's own name, as the layer's route reads it
                                mixed(mx.name, owner, cands);
                            }
                            localsJudged = lv != null;
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
                        // a restored member's body calls the facade the step renames V's class to (the layer's
                        // 1.21.1 ArmorMaterial for class_1741), where V's body called V's class: the anchor, kept in
                        // V's name for a mixin aimed at vanilla (MixinClassRenames.forVanilla), names that call in
                        // the facade's name at runtime
                        if (anchors.size() == 1 && cands.stream().allMatch(layerMembers::isMember)) {
                            Anchor a0 = anchors.get(0);
                            String fo = a0.owner == null ? null : classRenames.get(a0.owner);
                            if (MixinClassRenames.toLayer(fo) && (a0.kind.equals("INVOKE") || a0.kind.equals("FIELD"))) {
                                String fd = MixinClassRenames.rename(a0.desc, classRenames);
                                Anchor fa = new Anchor(a0.kind, fo, a0.name, fd, a0.constant, a0.ordinal, a0.role);
                                if (cands.stream().anyMatch(m -> anchorIn(m, fa))) {
                                    String value = "L" + fo + ";" + a0.name + (a0.kind.equals("FIELD") ? ":" : "") + fd;
                                    fixNotes.add("anchor-facade " + where + " " + a0 + " => " + value
                                        + " (the restored body calls the facade the step renames " + a0.owner + " to)");
                                    List<MethodNode> fc = cands;
                                    fixes.add(() -> {
                                        if (setAtTarget(an, refmap, value)) { dirty[0] = true; if (refmap != null) dirty[1] = true; }
                                        mixed(mx.name, owner, fc);
                                    });
                                    continue;
                                }
                            }
                            // the restored body reaches another class's restored static through the layer's @Invoker of it
                            // (restored statics are private): V's call of that member is the invoker's call (smx1 CASE 11)
                            String[] inv = a0.owner == null || !a0.kind.equals("INVOKE") ? null
                                : layerMembers.invokerOf(a0.owner, a0.name, a0.desc);
                            if (inv != null) {
                                Anchor ia = new Anchor("INVOKE", inv[0], inv[1], inv[2], a0.constant, a0.ordinal, a0.role);
                                if (cands.stream().anyMatch(m -> anchorIn(m, ia))) {
                                    String value = "L" + inv[0] + ";" + inv[1] + inv[2];
                                    fixNotes.add("anchor-invoker " + where + " " + a0 + " => " + value
                                        + " (the restored body calls " + a0.owner + "." + a0.name + " through the layer's invoker)");
                                    List<MethodNode> fc = cands;
                                    fixes.add(() -> {
                                        if (setAtTarget(an, refmap, value)) { dirty[0] = true; if (refmap != null) dirty[1] = true; }
                                        mixed(mx.name, owner, fc);
                                    });
                                    continue;
                                }
                            }
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
                        if (DEBUG_DESCEND) System.err.println("cb.debugDescend gate " + where + " source=" + (source != null)
                            + " anchors=" + anchors + " cands=" + cands.size() + " capturesArgs=" + capturesArgs);
                        if (source != null && anchors.size() == 1 && cands.size() == 1 && !capturesArgs) {
                            List<String> dwhy = new ArrayList<>();
                            Descend d = descendBySite(an, handler, owner, tc, cands.get(0), anchors.get(0), dwhy);
                            if (d != null) {
                                String repl = "L" + owner + ";" + d.callee().name + d.callee().desc;
                                fixNotes.add("descend-site " + where + " " + anchors.get(0) + " => " + d.callee().name + d.callee().desc
                                    + (d.ordinal() >= 0 ? " #" + d.ordinal() : "") + (d.makeStatic() ? " (handler made static)" : "")
                                    + (d.toCir() ? " (callback as CallbackInfoReturnable)" : "")
                                    + " (V's site by context; the callee is " + d.how() + ")");
                                if (d.crossOwner() != null) {
                                    MethodNode via = depth2Of.get(d.callee());
                                    fixNotes.set(fixNotes.size() - 1, fixNotes.get(fixNotes.size() - 1).replace(" => ", " => "
                                        + d.crossOwner().substring(d.crossOwner().lastIndexOf('/') + 1) + ".")
                                        + (via != null ? " (depth 2, under " + via.name + via.desc + ")" : ""));
                                    fixes.add(() -> {
                                        crossGuard(d, mx, handler, an);
                                        dirty[0] = true;
                                    });
                                    continue;
                                }
                                fixes.add(() -> {
                                    applyDescend(d, handler, an, mx);
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
                if (live) {
                    // K2: MixinBridge left it "not bridged" and nothing here repaired it: it would apply unchanged against
                    // W's changed body (or fail at apply), so it is disabled, never left live -- unless the reason is the
                    // target's locals and K1 judged them against W's frames
                    if (notBridged != null && !remapped && !(Boolean.TRUE.equals(notBridged[1]) && localsJudged)) {
                        dropInjector(handler, an);
                        dirty[0] = true;
                        disabled.add("not bridged: " + notBridged[0] + " (it would apply unchanged against " + stepTo
                            + "'s changed body: disabled, not left live)");
                    }
                    continue;
                }
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
        flushGuards(mx);
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
    record Descend(MethodNode callee, int ordinal, boolean toCir, boolean makeStatic, boolean sibling,
                   MethodInsnNode guardCall, String guardOwner, MethodNode guardIn, AbstractInsnNode site,
                   String crossOwner, boolean onThis) {
        Descend(MethodNode callee, int ordinal, boolean toCir, boolean makeStatic, boolean sibling) {
            this(callee, ordinal, toCir, makeStatic, sibling, null, null, null, null, null, false);
        }

        String how() {
            return sibling ? "another entry point of the same framework class, called from nowhere in its own class"
                : crossOwner != null ? "in " + crossOwner.substring(crossOwner.lastIndexOf('/') + 1) + ", guarded: the handler acts only"
                    + " while the target's one call of it runs" + (onThis ? " on the same instance" : "")
                : guardCall != null ? "shared, so guarded: the handler acts only while the target's one call of it runs"
                : "called from one site";
        }
    }

    // ------------------------------------------------------------------ guarded descend into another class (o, stage 2)

    /** -Dcb.debugDescend: the descend-site decisions on stderr */
    private static final boolean DEBUG_DESCEND = Boolean.getBoolean("cb.debugDescend");

    /** the config being repaired: its package and side key, and the guard mixins / helper made for it */
    private String curPkg = "";
    private String curKey = "mixins";
    private final Map<String, ClassNode> crossMixins = new LinkedHashMap<>();
    private ClassNode crossHelper;
    private boolean crossAllowed;

    private String helperName() {
        return "cb$guards/" + (curPkg.isEmpty() ? "root" : curPkg) + "/Guards";
    }

    /** a public static ThreadLocal of the config's guard helper (a plain class outside the mixin package) */
    private String helperField() {
        if (crossHelper == null) {
            crossHelper = new ClassNode();
            crossHelper.version = Opcodes.V17;
            crossHelper.access = Opcodes.ACC_PUBLIC | Opcodes.ACC_FINAL | Opcodes.ACC_SUPER;
            crossHelper.name = helperName();
            crossHelper.superName = "java/lang/Object";
            MethodNode ci = new MethodNode(Opcodes.ACC_STATIC, "<clinit>", "()V", null, null);
            ci.instructions.add(new InsnNode(Opcodes.RETURN));
            ci.maxStack = 2;
            crossHelper.methods.add(ci);
        }
        String f = "g" + crossHelper.fields.size();
        crossHelper.fields.add(new FieldNode(Opcodes.ACC_PUBLIC | Opcodes.ACC_STATIC | Opcodes.ACC_FINAL, f, "Ljava/lang/ThreadLocal;", null, null));
        InsnList in = new InsnList();
        in.add(new TypeInsnNode(Opcodes.NEW, "java/lang/ThreadLocal"));
        in.add(new InsnNode(Opcodes.DUP));
        in.add(new MethodInsnNode(Opcodes.INVOKESPECIAL, "java/lang/ThreadLocal", "<init>", "()V", false));
        in.add(new FieldInsnNode(Opcodes.PUTSTATIC, crossHelper.name, f, "Ljava/lang/ThreadLocal;"));
        crossHelper.methods.get(0).instructions.insert(in);
        return f;
    }

    private String crossMixinName(String decl) {
        return (curPkg.isEmpty() ? "" : curPkg + "/") + "cb$xguard$" + decl.replace('/', '_').replace('$', '_');
    }

    /** a @Shadow on the guard mixin, {kind 'f'/'m', name, desc, static}, unless it has one of that shape */
    private static void addShadow(ClassNode gc, Object[] sh) {
        String name = (String) sh[1];
        String desc = (String) sh[2];
        boolean st = (Boolean) sh[3];
        AnnotationNode a = new AnnotationNode(SHADOW);
        if ((Character) sh[0] == 'f') {
            if (gc.fields.stream().anyMatch(f -> f.name.equals(name) && f.desc.equals(desc))) return;
            FieldNode f = new FieldNode(Opcodes.ACC_PRIVATE | (st ? Opcodes.ACC_STATIC : 0), name, desc, null, null);
            f.visibleAnnotations = new ArrayList<>(List.of(a));
            gc.fields.add(f);
        } else {
            if (gc.methods.stream().anyMatch(m -> m.name.equals(name) && m.desc.equals(desc))) return;
            MethodNode m = new MethodNode(st ? Opcodes.ACC_PRIVATE | Opcodes.ACC_STATIC | Opcodes.ACC_NATIVE : Opcodes.ACC_PROTECTED | Opcodes.ACC_ABSTRACT,
                name, desc, null, null);
            m.visibleAnnotations = new ArrayList<>(List.of(a));
            gc.methods.add(m);
        }
    }

    private ClassNode crossMixin(String decl, int version) {
        String name = crossMixinName(decl);
        return crossMixins.computeIfAbsent(name, k -> {
            ClassNode gc = new ClassNode();
            gc.version = version;
            gc.access = Opcodes.ACC_PUBLIC | Opcodes.ACC_ABSTRACT | Opcodes.ACC_SUPER;
            gc.name = k;
            gc.superName = "java/lang/Object";
            AnnotationNode mixin = new AnnotationNode(MIXIN);
            mixin.values = new ArrayList<>(List.of("value", new ArrayList<>(List.of(Type.getObjectType(decl)))));
            gc.invisibleAnnotations = new ArrayList<>(List.of(mixin));
            return gc;
        });
    }

    /** the config's guard mixins and helper, written into the jar and listed in the config */
    private boolean flushCross(Map<String, byte[]> entries, JsonObject cfg) {
        boolean any = !crossMixins.isEmpty();
        for (Map.Entry<String, ClassNode> e : crossMixins.entrySet()) {
            ClassWriter cw = new ClassWriter(ClassWriter.COMPUTE_MAXS);
            e.getValue().accept(cw);
            entries.put(e.getKey() + ".class", cw.toByteArray());
            String rel = (curPkg.isEmpty() ? e.getKey() : e.getKey().substring(curPkg.length() + 1)).replace('/', '.');
            if (!cfg.has(curKey)) cfg.add(curKey, new JsonArray());
            cfg.getAsJsonArray(curKey).add(rel);
        }
        if (crossHelper != null) {
            ClassWriter cw = new ClassWriter(ClassWriter.COMPUTE_MAXS);
            crossHelper.accept(cw);
            entries.put(crossHelper.name + ".class", cw.toByteArray());
        }
        crossMixins.clear();
        crossHelper = null;
        return any;
    }

    /** the class declaring name+desc with a body, from owner up its superclasses (W), or null */
    private ClassNode declaringClass(String owner, String name, String desc) {
        for (String c = owner; c != null; ) {
            ClassNode cn = target(c);
            if (cn == null) return null;
            for (MethodNode m : cn.methods) {
                if (m.name.equals(name) && m.desc.equals(desc)) return m.instructions != null && m.instructions.size() > 0 ? cn : null;
            }
            c = cn.superName;
        }
        return null;
    }

    /** declarations by name+desc across the code jar, built once (who overrides a method) */
    private Map<String, List<String>> declIndex;

    /** does a class of the code jar below decl override its non-final, non-private, non-static m */
    private boolean overriddenBelow(String decl, MethodNode m) {
        if ((m.access & (Opcodes.ACC_FINAL | Opcodes.ACC_PRIVATE | Opcodes.ACC_STATIC)) != 0) return false;
        if (declIndex == null) {
            declIndex = new HashMap<>();
            for (ZipEntry e : java.util.Collections.list(code.entries())) {
                if (!e.getName().endsWith(".class")) continue;
                String name = e.getName().substring(0, e.getName().length() - 6);
                boolean had = cache.containsKey(name);
                ClassNode cn = target(name);
                if (cn == null) continue;
                for (MethodNode c : cn.methods) declIndex.computeIfAbsent(c.name + c.desc, k -> new ArrayList<>()).add(cn.name);
                if (!had) cache.remove(name);
            }
        }
        for (String o : declIndex.getOrDefault(m.name + m.desc, List.of())) {
            if (o.equals(decl)) continue;
            for (String c = o; c != null; ) {
                if (c.equals(decl)) return true;
                ClassNode cn = target(c);
                c = cn == null ? null : cn.superName;
            }
        }
        return false;
    }

    /** is the call's receiver the method's own this (aload 0 straight onto the stack) */
    private static boolean receiverIsThis(String owner, MethodNode wm, MethodInsnNode call) {
        if ((wm.access & Opcodes.ACC_STATIC) != 0 || call.getOpcode() == Opcodes.INVOKESTATIC) return false;
        try {
            org.objectweb.asm.tree.analysis.Frame<org.objectweb.asm.tree.analysis.SourceValue>[] f =
                new org.objectweb.asm.tree.analysis.Analyzer<>(new org.objectweb.asm.tree.analysis.SourceInterpreter()).analyze(owner, wm);
            org.objectweb.asm.tree.analysis.Frame<org.objectweb.asm.tree.analysis.SourceValue> fr = f[wm.instructions.indexOf(call)];
            if (fr == null) return false;
            int depth = Type.getArgumentTypes(call.desc).length;
            org.objectweb.asm.tree.analysis.SourceValue v = fr.getStack(fr.getStackSize() - 1 - depth);
            if (v.insns.isEmpty()) return false;
            for (AbstractInsnNode in : v.insns) {
                if (!(in instanceof VarInsnNode vi && vi.getOpcode() == Opcodes.ALOAD && vi.var == 0)) return false;
            }
            return true;
        } catch (org.objectweb.asm.tree.analysis.AnalyzerException ex) {
            return false;
        }
    }

    /** in a constructor, is the call after the super / this constructor call on this */
    private static boolean afterSuper(ClassNode tc, MethodNode wm, MethodInsnNode call) {
        if (!wm.name.equals("<init>")) return true;
        for (AbstractInsnNode in = wm.instructions.getFirst(); in != null; in = in.getNext()) {
            if (in == call) return false;
            if (in instanceof MethodInsnNode mi && mi.getOpcode() == Opcodes.INVOKESPECIAL && mi.name.equals("<init>")
                    && (mi.owner.equals(tc.superName) || mi.owner.equals(tc.name))) return true;
        }
        return false;
    }

    /** does the handler reference its own mixin class (its members, shadows, other handlers) */
    private static boolean usesOwnClass(MethodNode h, String mixin) {
        if (h.instructions == null) return false;
        for (AbstractInsnNode in : h.instructions) {
            if (in instanceof MethodInsnNode mi && mi.owner.equals(mixin)) return true;
            if (in instanceof FieldInsnNode fi && fi.owner.equals(mixin)) return true;
            if (in instanceof TypeInsnNode ti && ti.desc.equals(mixin)) return true;
            if (in instanceof org.objectweb.asm.tree.InvokeDynamicInsnNode id) {
                for (Object b : id.bsmArgs) if (b instanceof org.objectweb.asm.Handle hd && hd.getOwner().equals(mixin)) return true;
            }
        }
        return false;
    }

    /**
     * Guarded descend into another class: V's site stands in a callee W declares on another game class D (a superclass
     * method the target calls on itself -- addStandardInventorySlots; a static helper -- spawnProjectileFromRotation),
     * called directly by the target, once. The injector moves into a new mixin on D (listed in the mod's config), and
     * acts only while the target's call runs: the call is wrapped (@WrapOperation in the mod's mixin, try/finally)
     * setting a per-thread token in a plain helper class of the mod -- the receiver when it is the target's this
     * (the moved handler then also requires that this is that receiver), else a marker. The mod's handler stays in
     * its mixin as a plain method (marked BaseGuarded: the judge follows it); on D the moved injector calls it on the
     * target instance (the receiver is this), or runs a copy of its body when it never reads this or its own mixin.
     * Outside the guarded call it does what the site did without it (guardFallback).
     */
    private void crossGuard(Descend d, ClassNode mx, MethodNode h, AnnotationNode an) {
        if (d.toCir()) toReturnable(h);
        String field = helperField();
        String hc = crossHelper.name;
        ClassNode gc = crossMixin(d.crossOwner(), mx.version);
        String oldName = h.name;
        String gname = mx.name.substring(mx.name.lastIndexOf('/') + 1).replaceAll("[^A-Za-z0-9_]", "_") + "$" + oldName;
        while (true) {
            String g0 = gname;
            if (gc.methods.stream().noneMatch(m -> m.name.equals(g0))) break;
            gname += "_";
        }
        boolean calleeStatic = (d.callee().access & Opcodes.ACC_STATIC) != 0;
        AnnotationNode moved = copyAnnotation(an);
        setSelector(moved, "L" + d.crossOwner() + ";" + d.callee().name + d.callee().desc);
        setAtOrdinal(moved, d.ordinal());
        MethodNode g;
        if (d.onThis()) {
            // the mod's handler: a plain public method of the target, called on it
            String plain = "cb$guarded$" + oldName;
            dropInjector(h, an);
            h.name = plain;
            h.access = (h.access & ~(Opcodes.ACC_PRIVATE | Opcodes.ACC_PROTECTED)) | Opcodes.ACC_PUBLIC;
            g = new MethodNode(Opcodes.ACC_PRIVATE, gname, h.desc, null, null);
            Type[] hp = Type.getArgumentTypes(h.desc);
            g.instructions.add(new VarInsnNode(Opcodes.ALOAD, 0));
            g.instructions.add(new TypeInsnNode(Opcodes.CHECKCAST, d.guardOwner()));
            int slot = 1;
            for (Type t : hp) {
                g.instructions.add(new VarInsnNode(t.getOpcode(Opcodes.ILOAD), slot));
                slot += t.getSize();
            }
            g.instructions.add(new MethodInsnNode(Opcodes.INVOKEVIRTUAL, d.guardOwner(), plain, h.desc, false));
            g.instructions.add(new InsnNode(Type.getReturnType(h.desc).getOpcode(Opcodes.IRETURN)));
            g.maxLocals = slot;
            g.maxStack = slot + 1;
        } else {
            // a copy of the handler's body (it reads neither this nor its own mixin)
            g = new MethodNode(h.access, gname, h.desc, h.signature, h.exceptions == null ? null : h.exceptions.toArray(new String[0]));
            h.accept(g);
            g.name = gname;
            g.visibleAnnotations = null;
            g.invisibleAnnotations = null;
            g.visibleParameterAnnotations = null;
            g.invisibleParameterAnnotations = null;
            g.access = (g.access & ~(Opcodes.ACC_PUBLIC | Opcodes.ACC_PROTECTED)) | Opcodes.ACC_PRIVATE;
            if (calleeStatic && (g.access & Opcodes.ACC_STATIC) == 0) makeStatic(g);
            dropInjector(h, an);
            h.name = "cb$guarded$" + oldName; // the judge follows the mark from the mod's name
        }
        g.visibleAnnotations = new ArrayList<>(List.of(moved));
        // guard prologue on g: token == this (on the instance) / token != null (any)
        boolean gst = (g.access & Opcodes.ACC_STATIC) != 0;
        InsnList pro = new InsnList();
        LabelNode run = new LabelNode();
        pro.add(new FieldInsnNode(Opcodes.GETSTATIC, hc, field, "Ljava/lang/ThreadLocal;"));
        pro.add(new MethodInsnNode(Opcodes.INVOKEVIRTUAL, "java/lang/ThreadLocal", "get", "()Ljava/lang/Object;", false));
        if (d.onThis()) {
            pro.add(new VarInsnNode(Opcodes.ALOAD, 0));
            pro.add(new JumpInsnNode(Opcodes.IF_ACMPEQ, run));
        } else {
            pro.add(new JumpInsnNode(Opcodes.IFNONNULL, run));
        }
        pro.add(fallback(guardFallback(moved, g, d.site()), g, d.site()));
        pro.add(run);
        pro.add(new FrameNode(Opcodes.F_SAME, 0, null, 0, null));
        g.instructions.insert(pro);
        g.maxStack = Math.max(g.maxStack, 8);
        gc.methods.add(g);
        AnnotationNode mark = new AnnotationNode(MixinBridge.GUARDED);
        mark.values = new ArrayList<>(List.of("of", oldName, "mixin", gc.name, "handler", gname));
        h.invisibleAnnotations = h.invisibleAnnotations == null ? new ArrayList<>() : h.invisibleAnnotations;
        h.invisibleAnnotations.add(mark);
        PENDING.computeIfAbsent(mx, k -> new ArrayList<>()).add(tokenWrapper(d, mx, hc, field));
    }

    /** the @WrapOperation on the target's call setting the token around it (try/finally, the previous one restored) */
    private static MethodNode tokenWrapper(Descend d, ClassNode mx, String hc, String field) {
        MethodInsnNode call = d.guardCall();
        boolean callStatic = call.getOpcode() == Opcodes.INVOKESTATIC;
        Type cr = Type.getReturnType(call.desc);
        List<Type> wp = new ArrayList<>();
        if (!callStatic) wp.add(Type.getObjectType(call.owner));
        wp.addAll(List.of(Type.getArgumentTypes(call.desc)));
        wp.add(Type.getType(OPERATION));
        boolean wmStatic = (d.guardIn().access & Opcodes.ACC_STATIC) != 0;
        int n = 0;
        for (MethodNode m : mx.methods) if (m.name.startsWith("cb$tokencall$")) n++;
        for (MethodNode m : PENDING.getOrDefault(mx, List.of())) if (m.name.startsWith("cb$tokencall$")) n++;
        MethodNode w = new MethodNode(Opcodes.ACC_PRIVATE | (wmStatic ? Opcodes.ACC_STATIC : 0), "cb$tokencall$" + n,
            Type.getMethodDescriptor(cr, wp.toArray(new Type[0])), null, null);
        AnnotationNode wa = new AnnotationNode(WRAP_OPERATION);
        AnnotationNode at = new AnnotationNode("Lorg/spongepowered/asm/mixin/injection/At;");
        at.values = new ArrayList<>(List.of("value", "INVOKE", "target", "L" + call.owner + ";" + call.name + call.desc));
        wa.values = new ArrayList<>(List.of("method", new ArrayList<>(List.of("L" + d.guardOwner() + ";" + d.guardIn().name + d.guardIn().desc)),
            "at", new ArrayList<>(List.of(at))));
        w.visibleAnnotations = new ArrayList<>(List.of(wa));
        int base = wmStatic ? 0 : 1;
        int opSlot = base;
        for (Type t : wp.subList(0, wp.size() - 1)) opSlot += t.getSize();
        int prevSlot = opSlot + 1;
        int rSlot = prevSlot + 1;
        InsnList b = w.instructions;
        LabelNode tryStart = new LabelNode(), tryEnd = new LabelNode(), handler = new LabelNode();
        // prev = tl.get(); tl.set(token)
        b.add(new FieldInsnNode(Opcodes.GETSTATIC, hc, field, "Ljava/lang/ThreadLocal;"));
        b.add(new MethodInsnNode(Opcodes.INVOKEVIRTUAL, "java/lang/ThreadLocal", "get", "()Ljava/lang/Object;", false));
        b.add(new VarInsnNode(Opcodes.ASTORE, prevSlot));
        b.add(new FieldInsnNode(Opcodes.GETSTATIC, hc, field, "Ljava/lang/ThreadLocal;"));
        if (d.onThis()) {
            b.add(new VarInsnNode(Opcodes.ALOAD, base)); // the call's receiver
        } else {
            b.add(new FieldInsnNode(Opcodes.GETSTATIC, "java/lang/Boolean", "TRUE", "Ljava/lang/Boolean;"));
        }
        b.add(new MethodInsnNode(Opcodes.INVOKEVIRTUAL, "java/lang/ThreadLocal", "set", "(Ljava/lang/Object;)V", false));
        b.add(tryStart);
        b.add(new VarInsnNode(Opcodes.ALOAD, opSlot));
        b.add(new IntInsnNode(Opcodes.SIPUSH, wp.size() - 1));
        b.add(new TypeInsnNode(Opcodes.ANEWARRAY, "java/lang/Object"));
        int slot = base;
        for (int i = 0; i < wp.size() - 1; i++) {
            Type t = wp.get(i);
            b.add(new InsnNode(Opcodes.DUP));
            b.add(new IntInsnNode(Opcodes.SIPUSH, i));
            b.add(new VarInsnNode(t.getOpcode(Opcodes.ILOAD), slot));
            box(b, t);
            b.add(new InsnNode(Opcodes.AASTORE));
            slot += t.getSize();
        }
        b.add(new MethodInsnNode(Opcodes.INVOKEINTERFACE, OPERATION.substring(1, OPERATION.length() - 1), "call",
            "([Ljava/lang/Object;)Ljava/lang/Object;", true));
        b.add(tryEnd);
        if (cr.getSort() == Type.VOID) {
            b.add(new InsnNode(Opcodes.POP));
        } else {
            unbox(b, cr);
            b.add(new VarInsnNode(cr.getOpcode(Opcodes.ISTORE), rSlot));
        }
        restoreToken(b, hc, field, prevSlot);
        if (cr.getSort() == Type.VOID) {
            b.add(new InsnNode(Opcodes.RETURN));
        } else {
            b.add(new VarInsnNode(cr.getOpcode(Opcodes.ILOAD), rSlot));
            b.add(new InsnNode(cr.getOpcode(Opcodes.IRETURN)));
        }
        List<Object> locals = new ArrayList<>();
        if (!wmStatic) locals.add(mx.name);
        for (Type t : wp) locals.add(frameType(t));
        locals.add("java/lang/Object");
        b.add(handler);
        b.add(new FrameNode(Opcodes.F_FULL, locals.size(), locals.toArray(), 1, new Object[] {"java/lang/Throwable"}));
        b.add(new VarInsnNode(Opcodes.ASTORE, rSlot));
        restoreToken(b, hc, field, prevSlot);
        b.add(new VarInsnNode(Opcodes.ALOAD, rSlot));
        b.add(new InsnNode(Opcodes.ATHROW));
        w.tryCatchBlocks.add(new TryCatchBlockNode(tryStart, tryEnd, handler, null));
        w.maxLocals = rSlot + Math.max(1, cr.getSize());
        w.maxStack = 8;
        return w;
    }

    private static void restoreToken(InsnList b, String hc, String field, int prevSlot) {
        b.add(new FieldInsnNode(Opcodes.GETSTATIC, hc, field, "Ljava/lang/ThreadLocal;"));
        b.add(new VarInsnNode(Opcodes.ALOAD, prevSlot));
        b.add(new MethodInsnNode(Opcodes.INVOKEVIRTUAL, "java/lang/ThreadLocal", "set", "(Ljava/lang/Object;)V", false));
    }

    /**
     * The "owner.name(desc)" of the V method a descriptorless selector named, or null (C23). Mixin resolves a bare
     * name with the default quantifier as SINGLE (MemberInfo.configure(SELECT_MEMBER), javap of sponge-mixin 0.17.3):
     * the first method of the class by that name, in class-file order. So on V the selector meant V's first method of
     * that name; once W no longer declares it, that V method's fate (a mixinMoves entry) is the selector's.
     * A wildcard selector names every match: not resolved here.
     */
    private String bareMoveKey(String owner, Sel s) {
        if (source == null || s.wildcard) return null;
        ClassNode vtc = source.target(owner);
        if (vtc == null) return null;
        for (MethodNode m : vtc.methods) {
            if (m.name.equals(s.name)) return owner + "." + m.name + m.desc;
        }
        return null;
    }

    /**
     * K2: MixinBridge's "not bridged" mark for this step on a handler ({why, locals}), or null. Every such mark is taken
     * off (one written for another step is stale: a jar converted again starts clean).
     */
    private Object[] takeNotBridged(MethodNode h) {
        if (h.invisibleAnnotations == null) return null;
        Object[] out = null;
        for (java.util.Iterator<AnnotationNode> it = h.invisibleAnnotations.iterator(); it.hasNext(); ) {
            AnnotationNode a = it.next();
            if (!a.desc.equals(MixinBridge.NOT_BRIDGED)) continue;
            Map<String, Object> v = values(a);
            if (stepTo == null || stepTo.equals(v.get("to"))) out = new Object[] {v.get("why"), v.get("locals")};
            it.remove();
        }
        return out;
    }

    /** why an injector cannot move onto a mixin of the class a mixinMoves entry names, or null */
    private String movable(ClassNode mx, MethodNode h, AnnotationNode an, List<Anchor> anchors, String to, String vOwner,
                           List<String[]> self) {
        if (!crossAllowed) return "not in a config being repaired";
        int dot = to.indexOf('.');
        int paren = to.indexOf('(');
        if (dot <= 0 || paren <= dot) return "not owner.name(desc)";
        ClassNode wc = target(to.substring(0, dot));
        List<MethodNode> wms = wc == null ? List.of() : new Sel(null, to.substring(dot + 1, paren), to.substring(paren), false).match(wc);
        MethodNode wm = wms.size() == 1 ? wms.get(0) : null;
        if (wm == null) return "not a method of the target";
        boolean ws = (wm.access & Opcodes.ACC_STATIC) != 0;
        boolean hs = (h.access & Opcodes.ACC_STATIC) != 0;
        if (ws != hs) return ws ? "a static target with an instance handler" : "an instance target with a static handler";
        // an instance method moved to another class runs on another object: the handler may read its own instance
        // only through the entry's self hops (C17), its own mixin's members only as @Shadows of the W class or V's
        if (!hs && !touchesThisFree(h) || usesOwnClass(h, mx.name)) {
            if (self == null || self.isEmpty() || hs) {
                if (!hs && !touchesThisFree(h)) return "the handler reads this, which is another object in the moved method";
                return "the handler uses its own mixin's members";
            }
            List<String> why = new ArrayList<>();
            if (selfHopCopy(mx, h, to.substring(0, dot), vOwner, self, why, new ArrayList<>()) == null) return why.get(0);
        }
        if (anchors == null) return "an anchor not statically evaluable";
        if (!anchors.isEmpty() && !anchorsIn(wm, wc, anchors)) {
            // s11 C6(a): an INVOKE anchor on another V method that moved too (a RenderType factory overload calling
            // another factory): in the moved code the call names where that one went (moveInjector respells it)
            List<Anchor> tr = movedAnchors(anchors);
            if (tr == null || !anchorsIn(wm, wc, tr)) return "its anchor is not in the moved method " + anchors;
        }
        return null;
    }

    /** the anchors with each INVOKE of a mixinMoves key respelled as its W method, or null when none is one */
    private List<Anchor> movedAnchors(List<Anchor> anchors) {
        List<Anchor> out = new ArrayList<>();
        boolean any = false;
        for (Anchor a : anchors) {
            String to = a.kind.equals("INVOKE") && a.owner != null && a.desc != null ? mixinMoves.get(a.owner + "." + a.name + a.desc) : null;
            if (to != null) {
                int d = to.indexOf('.');
                int p = to.indexOf('(');
                out.add(new Anchor(a.kind, to.substring(0, d), to.substring(d + 1, p), to.substring(p), a.constant, a.ordinal, a.role));
                any = true;
            } else {
                out.add(a);
            }
        }
        return any ? out : null;
    }

    /** every @At target (slices included) naming a mixinMoves key, respelled as its W method (s11 C6(a)) */
    private void respellMovedTargets(AnnotationNode an) {
        if (an.values == null) return;
        for (int i = 0; i + 1 < an.values.size(); i += 2) {
            Object v = an.values.get(i + 1);
            if ("target".equals(an.values.get(i)) && v instanceof String t && t.startsWith("L") && t.indexOf(';') > 0 && t.indexOf('(') > t.indexOf(';')) {
                String key = t.substring(1, t.indexOf(';')) + "." + t.substring(t.indexOf(';') + 1);
                String to = mixinMoves.get(key);
                if (to != null) an.values.set(i + 1, "L" + to.substring(0, to.indexOf('.')) + ";" + to.substring(to.indexOf('.') + 1));
            } else if (v instanceof AnnotationNode a) {
                respellMovedTargets(a);
            } else if (v instanceof List<?> l) {
                for (Object o : l) if (o instanceof AnnotationNode a) respellMovedTargets(a);
            }
        }
    }

    private static final String SHADOW = "Lorg/spongepowered/asm/mixin/Shadow;";

    /**
     * The handler as it runs in the W class's guard mixin (C17): its own mixin's name becomes the guard mixin's, so
     * {@code this} is the W class's instance; a @Shadow member the W class declares stays on {@code this} (a @Shadow
     * of it is declared on the guard mixin, {@code shadows}: {kind, name, desc, static}); a @Shadow member only V's
     * class declares (public: the guard runs in another class) is called on V's instance; every use of {@code this}
     * other than as the receiver of a member the W class declares reads the self hops -- the first a field of the W
     * class (shadowed too), the rest public. Null with the reason when the handler does anything else with its own
     * class (a @Unique member, a cast to it, {@code this} duplicated or stored, a frame holding it on the stack)
     */
    private MethodNode selfHopCopy(ClassNode mx, MethodNode h, String wOwner, String vOwner, List<String[]> hops,
                                   List<String> why, List<Object[]> shadows) {
        String gname = crossMixinName(wOwner);
        ClassNode wc = target(wOwner);
        ClassNode vc = target(vOwner);
        if (wc == null || vc == null) { why.add("no W code for " + wOwner + " / " + vOwner); return null; }
        if (!hops.get(0)[0].equals(wOwner)) { why.add("the first self hop is not a field of " + wOwner); return null; }
        org.objectweb.asm.commons.SimpleRemapper rm = new org.objectweb.asm.commons.SimpleRemapper(mx.name, gname);
        MethodNode c = new MethodNode(h.access, h.name, rm.mapMethodDesc(h.desc), null,
            h.exceptions == null ? null : h.exceptions.toArray(new String[0]));
        h.accept(new org.objectweb.asm.commons.MethodRemapper(c, rm));
        c.desc = rm.mapMethodDesc(h.desc);
        // the mod mixin's @Shadow members: insn name+desc -> the target's name
        Map<String, String> shadowed = new HashMap<>();
        for (FieldNode f : mx.fields) {
            if (hasAnnotation(f.visibleAnnotations, SHADOW) || hasAnnotation(f.invisibleAnnotations, SHADOW)) shadowed.put(f.name + ":" + rm.mapDesc(f.desc), f.name);
        }
        for (MethodNode m : mx.methods) {
            AnnotationNode sh = null;
            for (AnnotationNode a : annotations(m)) if (a.desc.equals(SHADOW)) sh = a;
            if (sh == null) continue;
            String prefix = values(sh).get("prefix") instanceof String p ? p : "shadow$";
            shadowed.put(m.name + rm.mapMethodDesc(m.desc), m.name.startsWith(prefix) ? m.name.substring(prefix.length()) : m.name);
        }
        org.objectweb.asm.tree.analysis.Frame<org.objectweb.asm.tree.analysis.SourceValue>[] fr;
        try {
            fr = new org.objectweb.asm.tree.analysis.Analyzer<>(new org.objectweb.asm.tree.analysis.SourceInterpreter()).analyze(gname, c);
        } catch (org.objectweb.asm.tree.analysis.AnalyzerException ex) {
            why.add("the handler's code cannot be analysed: " + ex.getMessage());
            return null;
        }
        AbstractInsnNode[] insns = c.instructions.toArray();
        Set<AbstractInsnNode> keep = new java.util.HashSet<>();
        Set<AbstractInsnNode> hop = new java.util.HashSet<>();
        for (int i = 0; i < insns.length; i++) {
            AbstractInsnNode in = insns[i];
            if (in instanceof TypeInsnNode ti && ti.desc.equals(gname)) { why.add("the handler casts to its own mixin"); return null; }
            if (fr[i] == null) continue; // unreachable
            if (in.getOpcode() >= Opcodes.DUP && in.getOpcode() <= Opcodes.SWAP || in.getOpcode() == Opcodes.ASTORE) {
                org.objectweb.asm.tree.analysis.SourceValue top = fr[i].getStack(fr[i].getStackSize() - 1);
                if (top.insns.stream().anyMatch(MixinRepair::isThis)) { why.add("the handler duplicates or stores this"); return null; }
            }
            String owner, name, desc;
            boolean field = in instanceof FieldInsnNode;
            if (in instanceof FieldInsnNode fi) { owner = fi.owner; name = fi.name; desc = fi.desc; }
            else if (in instanceof MethodInsnNode mi) { owner = mi.owner; name = mi.name; desc = mi.desc; }
            else continue;
            if (!owner.equals(gname)) continue;
            String real = shadowed.get(field ? name + ":" + desc : name + desc);
            if (real == null) { why.add("the handler uses its own mixin's member " + name + " (not a @Shadow)"); return null; }
            boolean st = in.getOpcode() == Opcodes.GETSTATIC || in.getOpcode() == Opcodes.PUTSTATIC || in.getOpcode() == Opcodes.INVOKESTATIC;
            boolean onW = declares(wc, field, real, desc);
            Integer vAcc = access(vc, field, real, desc);
            int recv;
            if (onW) {
                shadows.add(new Object[] {field ? 'f' : 'm', real, desc, st});
                recv = 1;
            } else if (vAcc != null && (vAcc & Opcodes.ACC_PUBLIC) != 0) {
                if (field) ((FieldInsnNode) in).owner = vOwner; else ((MethodInsnNode) in).owner = vOwner;
                recv = 2;
            } else {
                why.add("@Shadow " + real + " is on neither " + wOwner + " nor (public) on " + vOwner);
                return null;
            }
            if (field) ((FieldInsnNode) in).name = real; else ((MethodInsnNode) in).name = real;
            if (!st) {
                int depth = field ? (in.getOpcode() == Opcodes.PUTFIELD ? 1 : 0) : Type.getArgumentTypes(desc).length;
                org.objectweb.asm.tree.analysis.SourceValue r = fr[i].getStack(fr[i].getStackSize() - 1 - depth);
                for (AbstractInsnNode src : r.insns) if (isThis(src)) (recv == 1 ? keep : hop).add(src);
            }
        }
        for (AbstractInsnNode in : insns) {
            if (in instanceof FrameNode f && f.stack != null && f.stack.contains(gname)) { why.add("a frame holds this on the stack"); return null; }
        }
        for (AbstractInsnNode in : insns) {
            if (!isThis(in) || keep.contains(in) && !hop.contains(in)) continue;
            if (keep.contains(in)) { why.add("one this is the receiver of members of both classes"); return null; }
            InsnList hl = new InsnList();
            for (int k = 0; k < hops.size(); k++) {
                String[] hp = hops.get(k);
                if (k == 0) {
                    shadows.add(new Object[] {'f', hp[1], hp[2], false});
                    hl.add(new FieldInsnNode(Opcodes.GETFIELD, gname, hp[1], hp[2]));
                } else {
                    Integer a = access(target(hp[0]), true, hp[1], hp[2]);
                    if (a == null || (a & Opcodes.ACC_PUBLIC) == 0) { why.add("self hop " + hp[0] + "." + hp[1] + " is not public"); return null; }
                    hl.add(new FieldInsnNode(Opcodes.GETFIELD, hp[0], hp[1], hp[2]));
                }
            }
            c.instructions.insert(in, hl);
        }
        c.maxStack += 1;
        return c;
    }

    private static boolean isThis(AbstractInsnNode n) {
        return n instanceof VarInsnNode v && v.getOpcode() == Opcodes.ALOAD && v.var == 0;
    }

    private static boolean hasAnnotation(List<AnnotationNode> l, String desc) {
        return l != null && l.stream().anyMatch(a -> a.desc.equals(desc));
    }

    private static boolean declares(ClassNode cn, boolean field, String name, String desc) {
        return access(cn, field, name, desc) != null;
    }

    /** the access of a member a class declares itself, or null */
    private static Integer access(ClassNode cn, boolean field, String name, String desc) {
        if (cn == null) return null;
        if (field) {
            for (FieldNode f : cn.fields) if (f.name.equals(name) && f.desc.equals(desc)) return f.access;
        } else {
            for (MethodNode m : cn.methods) if (m.name.equals(name) && m.desc.equals(desc)) return m.access;
        }
        return null;
    }

    /**
     * the injector onto a mixin of the W class (the config's cb$xguard$ mixin of it): a copy of the handler
     * selecting the W method; the mod's handler stays a plain method marked like a guarded one, so MixinJudge
     * judges the copy where it now stands. The copy is spelled in the guard mixin's name (its own class's members
     * are the guard's); with self hops (C17) {@code this} is read as {@link #selfHopCopy} says
     */
    private void moveInjector(ClassNode mx, MethodNode h, AnnotationNode an, String to, String vOwner, List<String[]> self) {
        int dot = to.indexOf('.');
        String wOwner = to.substring(0, dot);
        ClassNode gc = crossMixin(wOwner, mx.version);
        String oldName = h.name;
        String gname = mx.name.substring(mx.name.lastIndexOf('/') + 1).replaceAll("[^A-Za-z0-9_]", "_") + "$" + oldName;
        while (true) {
            String g0 = gname;
            if (gc.methods.stream().noneMatch(m -> m.name.equals(g0))) break;
            gname += "_";
        }
        AnnotationNode moved = copyAnnotation(an);
        setSelector(moved, "L" + wOwner + ";" + to.substring(dot + 1));
        respellMovedTargets(moved);
        List<Object[]> shadows = new ArrayList<>();
        MethodNode g = self != null && !self.isEmpty() && (h.access & Opcodes.ACC_STATIC) == 0
            ? selfHopCopy(mx, h, wOwner, vOwner, self, new ArrayList<>(), shadows) : null;
        if (g == null) {
            org.objectweb.asm.commons.SimpleRemapper rm = new org.objectweb.asm.commons.SimpleRemapper(mx.name, gc.name);
            g = new MethodNode(h.access, gname, rm.mapMethodDesc(h.desc), null, h.exceptions == null ? null : h.exceptions.toArray(new String[0]));
            h.accept(new org.objectweb.asm.commons.MethodRemapper(g, rm));
            g.desc = rm.mapMethodDesc(h.desc);
        }
        g.signature = null;
        for (Object[] sh : shadows) addShadow(gc, sh);
        g.name = gname;
        g.visibleAnnotations = new ArrayList<>(List.of(moved));
        g.invisibleAnnotations = null;
        g.access = (g.access & ~(Opcodes.ACC_PUBLIC | Opcodes.ACC_PROTECTED)) | Opcodes.ACC_PRIVATE;
        gc.methods.add(g);
        dropInjector(h, an);
        h.name = "cb$guarded$" + oldName; // the judge follows the mark from the mod's name
        AnnotationNode mark = new AnnotationNode(MixinBridge.GUARDED);
        mark.values = new ArrayList<>(List.of("of", oldName, "mixin", gc.name, "handler", gname));
        h.invisibleAnnotations = h.invisibleAnnotations == null ? new ArrayList<>() : h.invisibleAnnotations;
        h.invisibleAnnotations.add(mark);
    }

    // ------------------------------------------------------------------ invokers on statics W moved to another class

    private static final String INVOKER = "Lorg/spongepowered/asm/mixin/gen/Invoker;";

    /** "accessor interface.name desc" of an invoker moved by {@link #moveInvokers} -> "W interface.name" (the calls follow) */
    private final Map<String, String> invokerMoves = new LinkedHashMap<>();

    /**
     * A static @Invoker on a static V method that W keeps, the same code, in another class (a mixinMoves entry: 1.20.3
     * moved ShapedRecipe.shrink to ShapedRecipePattern; 1.21.11 RenderType's factories to RenderTypes): the invoker
     * moves onto an accessor interface mixin of the W class, naming the W method, and the jar's calls of it follow
     * ({@link #retargetInvokerCalls}). Left where it was, the target no longer declares the method: the whole
     * accessor is stripped (L3 target-decl) and every call into it runs its {@code throw new AssertionError()} body.
     * Only an invoker whose descriptor is the W method's (the same code: same arguments, same answer) and whose V
     * method the target class no longer declares; an instance invoker stays (its receiver is the V class's object).
     */
    private boolean moveInvokers(ClassNode mx, JsonObject refmap, List<String> targets, String shortName, List<String> repairs) {
        if (mixinMoves.isEmpty() || targets.size() != 1 || !crossAllowed) return false;
        String owner = targets.get(0);
        ClassNode tc = target(owner);
        boolean changed = false;
        for (MethodNode m : new ArrayList<>(mx.methods)) {
            if ((m.access & Opcodes.ACC_STATIC) == 0) continue;
            AnnotationNode inv = annotations(m).stream().filter(a -> INVOKER.equals(a.desc)).findFirst().orElse(null);
            if (inv == null) continue;
            String name = values(inv).get("value") instanceof String s && !s.isEmpty() ? refmapLookup(refmap, s) : invokerName(m.name);
            if (name == null) continue;
            String desc = m.desc;
            if (name.contains("(")) {
                desc = name.substring(name.indexOf('('));
                name = name.substring(0, name.indexOf('('));
                if (!desc.equals(m.desc)) continue;
            }
            String to = mixinMoves.get(owner + "." + name + desc);
            if (to == null) continue;
            String n0 = name, d0 = desc;
            if (tc != null && tc.methods.stream().anyMatch(x -> x.name.equals(n0) && x.desc.equals(d0))) continue;
            int dot = to.indexOf('.');
            String wOwner = to.substring(0, dot);
            String wName = to.substring(dot + 1, to.indexOf('('));
            String wDesc = to.substring(to.indexOf('('));
            ClassNode wc = target(wOwner);
            if (!wDesc.equals(desc) || wc == null || wc.methods.stream().noneMatch(x -> x.name.equals(wName) && x.desc.equals(wDesc)
                    && (x.access & Opcodes.ACC_STATIC) != 0)) {
                continue;
            }
            ClassNode ac = crossInvoker(wOwner, mx.version);
            String gname = m.name;
            while (true) {
                String g0 = gname;
                if (ac.methods.stream().noneMatch(x -> x.name.equals(g0) && x.desc.equals(m.desc))) break;
                gname += "_";
            }
            MethodNode g = new MethodNode(m.access, gname, m.desc, m.signature, m.exceptions == null ? null : m.exceptions.toArray(new String[0]));
            m.accept(g);
            g.name = gname;
            for (AnnotationNode a : annotations(g)) {
                if (!INVOKER.equals(a.desc)) continue;
                a.values = new ArrayList<>(List.of("value", wName));
            }
            ac.methods.add(g);
            mx.methods.remove(m);
            invokerMoves.put(mx.name + "." + m.name + m.desc, ac.name + "." + gname);
            repairs.add("invoker-move " + shortName + "." + m.name + " -> " + owner.substring(owner.lastIndexOf('/') + 1) + "." + name
                + " => " + to + " (mixinMoves: the same code in another class; the jar's calls follow)");
            changed = true;
        }
        return changed;
    }

    /** the member an @Invoker without a value names (Mixin: call/invoke/new/create prefix dropped, first letter lowered) */
    private static String invokerName(String method) {
        for (String p : new String[] {"call", "invoke", "new", "create"}) {
            if (method.startsWith(p) && method.length() > p.length() && Character.isUpperCase(method.charAt(p.length()))) {
                String rest = method.substring(p.length());
                return Character.toLowerCase(rest.charAt(0)) + rest.substring(1);
            }
        }
        return null;
    }

    private ClassNode crossInvoker(String decl, int version) {
        String name = (curPkg.isEmpty() ? "" : curPkg + "/") + "cb$xinvoke$" + decl.replace('/', '_').replace('$', '_');
        return crossMixins.computeIfAbsent(name, k -> {
            ClassNode ac = new ClassNode();
            ac.version = Math.max(version, Opcodes.V1_8);
            ac.access = Opcodes.ACC_PUBLIC | Opcodes.ACC_INTERFACE | Opcodes.ACC_ABSTRACT;
            ac.name = k;
            ac.superName = "java/lang/Object";
            AnnotationNode mixin = new AnnotationNode(MIXIN);
            mixin.values = new ArrayList<>(List.of("value", new ArrayList<>(List.of(Type.getObjectType(decl)))));
            ac.invisibleAnnotations = new ArrayList<>(List.of(mixin));
            return ac;
        });
    }

    /** every class of the jar: a call (or method handle) of a moved invoker names its new interface */
    private void retargetInvokerCalls(Map<String, byte[]> entries) {
        if (invokerMoves.isEmpty()) return;
        for (Map.Entry<String, byte[]> e : entries.entrySet()) {
            if (!e.getKey().endsWith(".class")) continue;
            ClassReader cr = new ClassReader(e.getValue());
            ClassWriter cw = new ClassWriter(cr, 0);
            boolean[] hit = {false};
            java.util.function.UnaryOperator<Object> h = o -> {
                if (o instanceof org.objectweb.asm.Handle hd && hd.getTag() == Opcodes.H_INVOKESTATIC) {
                    String to = invokerMoves.get(hd.getOwner() + "." + hd.getName() + hd.getDesc());
                    if (to != null) {
                        hit[0] = true;
                        return new org.objectweb.asm.Handle(hd.getTag(), to.substring(0, to.indexOf('.')), to.substring(to.indexOf('.') + 1), hd.getDesc(), true);
                    }
                }
                return o;
            };
            cr.accept(new org.objectweb.asm.ClassVisitor(Opcodes.ASM9, cw) {
                @Override
                public org.objectweb.asm.MethodVisitor visitMethod(int acc, String n, String d, String sig, String[] ex) {
                    return new org.objectweb.asm.MethodVisitor(Opcodes.ASM9, super.visitMethod(acc, n, d, sig, ex)) {
                        @Override
                        public void visitMethodInsn(int op, String ow, String nm, String ds, boolean itf) {
                            String to = op == Opcodes.INVOKESTATIC ? invokerMoves.get(ow + "." + nm + ds) : null;
                            if (to != null) {
                                hit[0] = true;
                                super.visitMethodInsn(op, to.substring(0, to.indexOf('.')), to.substring(to.indexOf('.') + 1), ds, true);
                            } else {
                                super.visitMethodInsn(op, ow, nm, ds, itf);
                            }
                        }

                        @Override
                        public void visitLdcInsn(Object v) {
                            super.visitLdcInsn(h.apply(v));
                        }

                        @Override
                        public void visitInvokeDynamicInsn(String nm, String ds, org.objectweb.asm.Handle bsm, Object... args) {
                            Object[] a2 = new Object[args.length];
                            for (int i = 0; i < args.length; i++) a2[i] = h.apply(args[i]);
                            super.visitInvokeDynamicInsn(nm, ds, bsm, a2);
                        }
                    };
                }
            }, 0);
            if (hit[0]) e.setValue(cw.toByteArray());
        }
    }

    private static AnnotationNode copyAnnotation(AnnotationNode an) {
        AnnotationNode c = new AnnotationNode(an.desc);
        an.accept(c);
        return c;
    }

    private static void setSelector(AnnotationNode an, String sel) {
        for (int i = 0; i + 1 < an.values.size(); i += 2) {
            if ("method".equals(an.values.get(i))) an.values.set(i + 1, new ArrayList<>(List.of(sel)));
        }
    }

    /** the descend's handler and annotation changes (the selector is the caller's to set) */
    static void applyDescend(Descend d, MethodNode handler, AnnotationNode an, ClassNode mx) {
        if (d.makeStatic()) makeStatic(handler);
        if (d.toCir()) toReturnable(handler);
        setAtOrdinal(an, d.ordinal());
        if (d.crossOwner() != null) {
            throw new IllegalStateException("a descend into another class goes through crossGuard");
        }
        if (d.guardCall() != null) guard(d, mx, handler, an);
    }

    static final String WRAP_OPERATION = "Lcom/llamalad7/mixinextras/injector/wrapoperation/WrapOperation;";
    static final String OPERATION = "Lcom/llamalad7/mixinextras/injector/wrapoperation/Operation;";
    static final String UNIQUE = "Lorg/spongepowered/asm/mixin/Unique;";
    /** marks a handler a guarded descend moved: "in" the target's method, "call" the guarded call, "wrap" the guard's handler */
    static final String CALL_GUARDED = "Ltop/dext/centurybridge/CallGuarded;";

    /** what the guard's fallback does for an injector kind, or null when it cannot hand back what the site had */
    static String guardFallback(AnnotationNode an, MethodNode h, AbstractInsnNode site) {
        String kind = an.desc.substring(an.desc.lastIndexOf('/') + 1, an.desc.length() - 1);
        Type[] p = Type.getArgumentTypes(h.desc);
        Type r = Type.getReturnType(h.desc);
        switch (kind) {
            case "Inject":
                return r.getSort() == Type.VOID ? "return" : null;
            case "ModifyArg": case "ModifyExpressionValue": case "ModifyConstant": case "ModifyVariable": case "ModifyReturnValue":
                return p.length >= 1 && p[0].equals(r) ? "value" : null;
            case "WrapWithCondition":
                return r.getSort() == Type.BOOLEAN ? "true" : null;
            case "WrapOperation": {
                // (operands..., Operation<R> op, captured target args...): unguarded, the operation runs as it is
                for (Type t : p) if (t.getDescriptor().equals(OPERATION)) return "op";
                return null;
            }
            case "Redirect": {
                if (!(site instanceof MethodInsnNode mi) || mi.name.startsWith("<")) return null;
                Type[] a = Type.getArgumentTypes(mi.desc);
                int n = a.length + (mi.getOpcode() == Opcodes.INVOKESTATIC ? 0 : 1);
                return p.length == n && r.equals(Type.getReturnType(mi.desc)) ? "call" : null;
            }
            default:
                return null;
        }
    }

    /**
     * Guarded descend (o): V's site stands in a callee W shares with other callers. The mod's handler moves into
     * the callee as for any descend, but acts only while the target's one call of the callee runs: a per-thread
     * depth counter (a ThreadLocal int[] the mixin adds, @Unique) that a @WrapOperation on that call raises around
     * the call, try/finally, so an exception cannot leave it raised. Outside it the handler does what the site
     * did without it: returns (@Inject), hands its value back (@Modify*), lets the call run (@WrapWithCondition)
     * or makes the call itself (@Redirect).
     */
    static void guard(Descend d, ClassNode mx, MethodNode h, AnnotationNode an) {
        String mode = guardFallback(an, h, d.site());
        int n = 0;
        for (FieldNode f : mx.fields) if (f.name.startsWith("cb$guard$")) n++;
        // the guard's methods join the mixin after its handlers were walked (flushGuards): callers iterate mx.methods
        String field = "cb$guard$" + n;
        FieldNode fn = new FieldNode(Opcodes.ACC_PRIVATE | Opcodes.ACC_STATIC | Opcodes.ACC_FINAL, field, "Ljava/lang/ThreadLocal;", null, null);
        fn.invisibleAnnotations = new ArrayList<>(List.of(new AnnotationNode(UNIQUE)));
        mx.fields.add(fn);
        MethodNode clinit = null;
        List<MethodNode> pending = PENDING.computeIfAbsent(mx, k -> new ArrayList<>());
        for (MethodNode m : mx.methods) if (m.name.equals("<clinit>")) clinit = m;
        for (MethodNode m : pending) if (m.name.equals("<clinit>")) clinit = m;
        if (clinit == null) {
            clinit = new MethodNode(Opcodes.ACC_STATIC, "<clinit>", "()V", null, null);
            clinit.instructions.add(new InsnNode(Opcodes.RETURN));
            clinit.maxStack = 0;
            pending.add(clinit);
        }
        InsnList ci = new InsnList();
        ci.add(new TypeInsnNode(Opcodes.NEW, "java/lang/ThreadLocal"));
        ci.add(new InsnNode(Opcodes.DUP));
        ci.add(new MethodInsnNode(Opcodes.INVOKESPECIAL, "java/lang/ThreadLocal", "<init>", "()V", false));
        ci.add(new FieldInsnNode(Opcodes.PUTSTATIC, mx.name, field, "Ljava/lang/ThreadLocal;"));
        clinit.instructions.insert(ci);
        clinit.maxStack = Math.max(clinit.maxStack, 2);

        // the handler's prologue: outside the guarded call, what the site did without it
        boolean st = (h.access & Opcodes.ACC_STATIC) != 0;
        Type[] hp = Type.getArgumentTypes(h.desc);
        Type hr = Type.getReturnType(h.desc);
        InsnList pro = new InsnList();
        LabelNode run = new LabelNode();
        LabelNode fall = new LabelNode();
        pro.add(new FieldInsnNode(Opcodes.GETSTATIC, mx.name, field, "Ljava/lang/ThreadLocal;"));
        pro.add(new MethodInsnNode(Opcodes.INVOKEVIRTUAL, "java/lang/ThreadLocal", "get", "()Ljava/lang/Object;", false));
        pro.add(new TypeInsnNode(Opcodes.CHECKCAST, "[I"));
        pro.add(new InsnNode(Opcodes.DUP));
        LabelNode nul = new LabelNode();
        pro.add(new JumpInsnNode(Opcodes.IFNULL, nul));
        pro.add(new InsnNode(Opcodes.ICONST_0));
        pro.add(new InsnNode(Opcodes.IALOAD));
        pro.add(new JumpInsnNode(Opcodes.IFNE, run));
        pro.add(new JumpInsnNode(Opcodes.GOTO, fall));
        pro.add(nul);
        pro.add(new FrameNode(Opcodes.F_SAME1, 0, null, 1, new Object[] {"[I"}));
        pro.add(new InsnNode(Opcodes.POP));
        pro.add(fall);
        pro.add(new FrameNode(Opcodes.F_SAME, 0, null, 0, null));
        pro.add(fallback(mode, h, d.site()));
        pro.add(run);
        pro.add(new FrameNode(Opcodes.F_SAME, 0, null, 0, null));
        h.instructions.insert(pro);
        int argSize = 0;
        for (Type t : hp) argSize += t.getSize();
        h.maxStack = Math.max(h.maxStack, Math.max(6, argSize + 1));

        // the guard: @WrapOperation on the target's one call of the callee
        MethodInsnNode call = d.guardCall();
        boolean callStatic = call.getOpcode() == Opcodes.INVOKESTATIC;
        Type[] ca = Type.getArgumentTypes(call.desc);
        Type cr = Type.getReturnType(call.desc);
        List<Type> wp = new ArrayList<>();
        if (!callStatic) wp.add(Type.getObjectType(call.owner));
        wp.addAll(List.of(ca));
        wp.add(Type.getType(OPERATION));
        String wdesc = Type.getMethodDescriptor(cr, wp.toArray(new Type[0]));
        String wname = "cb$guardcall$" + n;
        MethodNode w = new MethodNode(Opcodes.ACC_PRIVATE, wname, wdesc, null, null);
        // instance or static: a static wrapper works in either kind of method only when the method is static
        boolean wmStatic = (d.guardIn().access & Opcodes.ACC_STATIC) != 0;
        if (wmStatic) w.access |= Opcodes.ACC_STATIC;
        AnnotationNode wa = new AnnotationNode(WRAP_OPERATION);
        AnnotationNode at = new AnnotationNode("Lorg/spongepowered/asm/mixin/injection/At;");
        at.values = new ArrayList<>(List.of("value", "INVOKE", "target", "L" + call.owner + ";" + call.name + call.desc));
        wa.values = new ArrayList<>(List.of("method", new ArrayList<>(List.of("L" + d.guardOwner() + ";" + d.guardIn().name + d.guardIn().desc)),
            "at", new ArrayList<>(List.of(at))));
        w.visibleAnnotations = new ArrayList<>(List.of(wa));
        int base = wmStatic ? 0 : 1;
        int opSlot = base;
        for (Type t : wp.subList(0, wp.size() - 1)) opSlot += t.getSize();
        int cSlot = opSlot + 1;
        int rSlot = cSlot + 1;
        LabelNode tryStart = new LabelNode(), tryEnd = new LabelNode(), handler = new LabelNode();
        InsnList b = w.instructions;
        // int[] c = tl.get(); if (c == null) { c = new int[1]; tl.set(c); }
        b.add(new FieldInsnNode(Opcodes.GETSTATIC, mx.name, field, "Ljava/lang/ThreadLocal;"));
        b.add(new MethodInsnNode(Opcodes.INVOKEVIRTUAL, "java/lang/ThreadLocal", "get", "()Ljava/lang/Object;", false));
        b.add(new TypeInsnNode(Opcodes.CHECKCAST, "[I"));
        b.add(new VarInsnNode(Opcodes.ASTORE, cSlot));
        b.add(new VarInsnNode(Opcodes.ALOAD, cSlot));
        LabelNode have = new LabelNode();
        b.add(new JumpInsnNode(Opcodes.IFNONNULL, have));
        b.add(new InsnNode(Opcodes.ICONST_1));
        b.add(new IntInsnNode(Opcodes.NEWARRAY, Opcodes.T_INT));
        b.add(new VarInsnNode(Opcodes.ASTORE, cSlot));
        b.add(new FieldInsnNode(Opcodes.GETSTATIC, mx.name, field, "Ljava/lang/ThreadLocal;"));
        b.add(new VarInsnNode(Opcodes.ALOAD, cSlot));
        b.add(new MethodInsnNode(Opcodes.INVOKEVIRTUAL, "java/lang/ThreadLocal", "set", "(Ljava/lang/Object;)V", false));
        b.add(have);
        List<Object> locals = new ArrayList<>();
        if (!wmStatic) locals.add(mx.name);
        for (Type t : wp) locals.add(frameType(t));
        List<Object> withC = new ArrayList<>(locals);
        withC.add("[I");
        b.add(new FrameNode(Opcodes.F_FULL, withC.size(), withC.toArray(), 0, new Object[0]));
        // c[0]++
        b.add(new VarInsnNode(Opcodes.ALOAD, cSlot));
        b.add(new InsnNode(Opcodes.ICONST_0));
        b.add(new InsnNode(Opcodes.DUP2));
        b.add(new InsnNode(Opcodes.IALOAD));
        b.add(new InsnNode(Opcodes.ICONST_1));
        b.add(new InsnNode(Opcodes.IADD));
        b.add(new InsnNode(Opcodes.IASTORE));
        b.add(tryStart);
        // op.call(new Object[]{recv, args...})
        b.add(new VarInsnNode(Opcodes.ALOAD, opSlot));
        b.add(new IntInsnNode(Opcodes.SIPUSH, wp.size() - 1));
        b.add(new TypeInsnNode(Opcodes.ANEWARRAY, "java/lang/Object"));
        int slot = base;
        for (int i = 0; i < wp.size() - 1; i++) {
            Type t = wp.get(i);
            b.add(new InsnNode(Opcodes.DUP));
            b.add(new IntInsnNode(Opcodes.SIPUSH, i));
            b.add(new VarInsnNode(t.getOpcode(Opcodes.ILOAD), slot));
            box(b, t);
            b.add(new InsnNode(Opcodes.AASTORE));
            slot += t.getSize();
        }
        b.add(new MethodInsnNode(Opcodes.INVOKEINTERFACE, OPERATION.substring(1, OPERATION.length() - 1), "call",
            "([Ljava/lang/Object;)Ljava/lang/Object;", true));
        b.add(tryEnd);
        if (cr.getSort() == Type.VOID) {
            b.add(new InsnNode(Opcodes.POP));
        } else {
            unbox(b, cr);
            b.add(new VarInsnNode(cr.getOpcode(Opcodes.ISTORE), rSlot));
        }
        decrement(b, cSlot);
        if (cr.getSort() == Type.VOID) {
            b.add(new InsnNode(Opcodes.RETURN));
        } else {
            b.add(new VarInsnNode(cr.getOpcode(Opcodes.ILOAD), rSlot));
            b.add(new InsnNode(cr.getOpcode(Opcodes.IRETURN)));
        }
        b.add(handler);
        b.add(new FrameNode(Opcodes.F_FULL, withC.size(), withC.toArray(), 1, new Object[] {"java/lang/Throwable"}));
        b.add(new VarInsnNode(Opcodes.ASTORE, rSlot));
        decrement(b, cSlot);
        b.add(new VarInsnNode(Opcodes.ALOAD, rSlot));
        b.add(new InsnNode(Opcodes.ATHROW));
        w.tryCatchBlocks.add(new TryCatchBlockNode(tryStart, tryEnd, handler, null));
        w.maxLocals = rSlot + Math.max(1, cr.getSize());
        w.maxStack = 8;
        pending.add(w);
        AnnotationNode mark = new AnnotationNode(CALL_GUARDED);
        mark.values = new ArrayList<>(List.of("in", d.guardOwner() + "." + d.guardIn().name + d.guardIn().desc,
            "call", call.owner + "." + call.name + call.desc, "wrap", wname));
        h.invisibleAnnotations = h.invisibleAnnotations == null ? new ArrayList<>() : h.invisibleAnnotations;
        h.invisibleAnnotations.add(mark);
    }

    /** what the site did without the handler, as the handler's early exit (guardFallback's mode) */
    static InsnList fallback(String mode, MethodNode h, AbstractInsnNode site) {
        InsnList pro = new InsnList();
        boolean st = (h.access & Opcodes.ACC_STATIC) != 0;
        Type[] hp = Type.getArgumentTypes(h.desc);
        Type hr = Type.getReturnType(h.desc);
        int slot0 = st ? 0 : 1;
        switch (mode) {
            case "return" -> pro.add(new InsnNode(Opcodes.RETURN));
            case "value" -> {
                pro.add(new VarInsnNode(hp[0].getOpcode(Opcodes.ILOAD), slot0));
                pro.add(new InsnNode(hr.getOpcode(Opcodes.IRETURN)));
            }
            case "true" -> {
                pro.add(new InsnNode(Opcodes.ICONST_1));
                pro.add(new InsnNode(Opcodes.IRETURN));
            }
            case "op" -> {
                int opi = 0;
                while (!hp[opi].getDescriptor().equals(OPERATION)) opi++;
                int slot = slot0;
                int opSlot = slot0;
                for (int i = 0; i < opi; i++) opSlot += hp[i].getSize();
                pro.add(new VarInsnNode(Opcodes.ALOAD, opSlot));
                pro.add(new IntInsnNode(Opcodes.SIPUSH, opi));
                pro.add(new TypeInsnNode(Opcodes.ANEWARRAY, "java/lang/Object"));
                for (int i = 0; i < opi; i++) {
                    pro.add(new InsnNode(Opcodes.DUP));
                    pro.add(new IntInsnNode(Opcodes.SIPUSH, i));
                    pro.add(new VarInsnNode(hp[i].getOpcode(Opcodes.ILOAD), slot));
                    box(pro, hp[i]);
                    pro.add(new InsnNode(Opcodes.AASTORE));
                    slot += hp[i].getSize();
                }
                pro.add(new MethodInsnNode(Opcodes.INVOKEINTERFACE, OPERATION.substring(1, OPERATION.length() - 1), "call",
                    "([Ljava/lang/Object;)Ljava/lang/Object;", true));
                if (hr.getSort() == Type.VOID) {
                    pro.add(new InsnNode(Opcodes.POP));
                    pro.add(new InsnNode(Opcodes.RETURN));
                } else {
                    unbox(pro, hr);
                    pro.add(new InsnNode(hr.getOpcode(Opcodes.IRETURN)));
                }
            }
            default -> { // call
                MethodInsnNode mi = (MethodInsnNode) site;
                int slot = slot0;
                for (Type t : hp) {
                    pro.add(new VarInsnNode(t.getOpcode(Opcodes.ILOAD), slot));
                    slot += t.getSize();
                }
                pro.add(new MethodInsnNode(mi.getOpcode(), mi.owner, mi.name, mi.desc, mi.itf));
                pro.add(new InsnNode(hr.getOpcode(Opcodes.IRETURN)));
            }
        }
        return pro;
    }

    /** methods guard() made for a mixin, added by flushGuards once its handlers are no longer being walked */
    private static final Map<ClassNode, List<MethodNode>> PENDING =
        java.util.Collections.synchronizedMap(new java.util.IdentityHashMap<>());

    static void flushGuards(ClassNode mx) {
        List<MethodNode> p = PENDING.remove(mx);
        if (p != null) mx.methods.addAll(p);
    }

    private static void decrement(InsnList b, int cSlot) {
        b.add(new VarInsnNode(Opcodes.ALOAD, cSlot));
        b.add(new InsnNode(Opcodes.ICONST_0));
        b.add(new InsnNode(Opcodes.DUP2));
        b.add(new InsnNode(Opcodes.IALOAD));
        b.add(new InsnNode(Opcodes.ICONST_1));
        b.add(new InsnNode(Opcodes.ISUB));
        b.add(new InsnNode(Opcodes.IASTORE));
    }

    private static Object frameType(Type t) {
        return switch (t.getSort()) {
            case Type.BOOLEAN, Type.BYTE, Type.CHAR, Type.SHORT, Type.INT -> Opcodes.INTEGER;
            case Type.FLOAT -> Opcodes.FLOAT;
            case Type.LONG -> Opcodes.LONG;
            case Type.DOUBLE -> Opcodes.DOUBLE;
            default -> t.getSort() == Type.ARRAY ? t.getDescriptor() : t.getInternalName();
        };
    }

    private static final String[][] BOX = {
        {"Z", "java/lang/Boolean", "booleanValue"}, {"B", "java/lang/Byte", "byteValue"}, {"C", "java/lang/Character", "charValue"},
        {"S", "java/lang/Short", "shortValue"}, {"I", "java/lang/Integer", "intValue"}, {"F", "java/lang/Float", "floatValue"},
        {"J", "java/lang/Long", "longValue"}, {"D", "java/lang/Double", "doubleValue"}};

    private static void box(InsnList b, Type t) {
        for (String[] x : BOX) {
            if (x[0].equals(t.getDescriptor())) {
                b.add(new MethodInsnNode(Opcodes.INVOKESTATIC, x[1], "valueOf", "(" + x[0] + ")L" + x[1] + ";", false));
            }
        }
    }

    private static void unbox(InsnList b, Type t) {
        for (String[] x : BOX) {
            if (x[0].equals(t.getDescriptor())) {
                b.add(new TypeInsnNode(Opcodes.CHECKCAST, x[1]));
                b.add(new MethodInsnNode(Opcodes.INVOKEVIRTUAL, x[1], x[2], "()" + x[0], false));
                return;
            }
        }
        b.add(new TypeInsnNode(Opcodes.CHECKCAST, t.getSort() == Type.ARRAY ? t.getDescriptor() : t.getInternalName()));
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
        if (DEBUG_DESCEND) System.err.println("cb.debugDescend site " + vm.name + vm.desc + " " + a + " V occurrences " + vo.size());
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
        // stage 2: a callee W declares on another game class, called by the target directly (the guard decides
        // whether that is the target's call)
        Map<MethodNode, String> crossOf = new java.util.IdentityHashMap<>();
        Map<MethodNode, MethodInsnNode> crossCall = new java.util.IdentityHashMap<>();
        if (crossAllowed && wm.instructions != null) {
            for (AbstractInsnNode in : wm.instructions) {
                if (!(in instanceof MethodInsnNode mi) || mi.owner.equals(tc.name) || mi.name.startsWith("<")
                        || !Chain.minecraftOwned(mi.owner)) continue;
                ClassNode dc = declaringClass(mi.owner, mi.name, mi.desc);
                if (dc == null || dc.name.equals(tc.name)) continue;
                MethodNode dm = null;
                for (MethodNode m : dc.methods) if (m.name.equals(mi.name) && m.desc.equals(mi.desc)) dm = m;
                if (dm == null || crossOf.containsKey(dm)) {
                    if (dm != null) crossCall.put(dm, null); // called twice: no single call to guard
                    continue;
                }
                crossOf.put(dm, dc.name);
                crossCall.put(dm, mi);
                places.add(dm);
            }
        }
        // depth 2 under another class's callee: a method of that callee's own class it calls exactly once (W moved V's
        // code one call further down there). The guard is still the target's one call of the depth-1 callee: the
        // handler acts only while that call runs, and the depth-1 callee reaches this method once in it
        if (crossAllowed && DEPTH2) {
            for (MethodNode dm : new ArrayList<>(crossOf.keySet())) {
                MethodInsnNode c1 = crossCall.get(dm);
                ClassNode dcn = c1 == null ? null : target(crossOf.get(dm));
                if (dcn == null || dm.name.startsWith("<") || overriddenBelow(dcn.name, dm)) continue;
                for (MethodNode c2 : callees(dm, dcn)) {
                    if (places.contains(c2) || crossOf.containsKey(c2) || c2.name.startsWith("<") || callCount(dm, dcn.name, c2) != 1) continue;
                    crossOf.put(c2, dcn.name);
                    crossCall.put(c2, c1);
                    depth2Of.put(c2, dm);
                    places.add(c2);
                }
            }
        }
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
                ClassNode vOwn = crossOf.containsKey(c) ? source.target(crossOf.get(c)) : vc;
                if (j >= 0 && c != wm && vOwn != null && ownSite(vOwn, c, wo.get(j), a)) {
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
        if (crossOf.containsKey(into)) {
            return crossDescend(an, handler, tc, vm, wm, a, into, crossOf.get(into), crossCall.get(into), sites, ords, why);
        }
        MethodInsnNode guardCall = null;
        for (MethodNode m = into; m != wm && !siblings.contains(into); m = parent.get(m)) {
            int calls = (m.access & Opcodes.ACC_PRIVATE) != 0 ? callSites(tc, m) : jarCallSites(tc.name, m);
            if (calls != 1) {
                String g = guardable(an, handler, tc, wm, m, into, parent, a);
                if (g != null) {
                    why.add(m.name + m.desc + " holds V's site but is called from " + calls + " sites; guarded descend skipped: " + g);
                    return null;
                }
                guardCall = callOf(wm, tc, m);
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
        if (guardCall != null) {
            AbstractInsnNode site = wo.get(ordinal >= 0 ? ordinal : 0);
            if (guardFallback(an, handler, site) == null) {
                why.add(into.name + into.desc + " is shared; guarded descend skipped: no fallback for this handler's shape");
                return null;
            }
            return new Descend(into, ordinal, toCir, calleeStatic && !handlerStatic, false, guardCall, tc.name, wm, site, null, false);
        }
        return new Descend(into, ordinal, toCir, calleeStatic && !handlerStatic, siblings.contains(into));
    }

    /** -Dcb.descendDepth2=false: no descend under another class's callee (depth 2 of stage 2) */
    private static final boolean DEPTH2 = !"false".equals(System.getProperty("cb.descendDepth2"));

    /** a depth-2 callee of stage 2 -> the depth-1 callee it stands under (for the repair note) */
    private final Map<MethodNode, MethodNode> depth2Of = new java.util.IdentityHashMap<>();

    /** how many calls of c (owner o) m makes */
    private static int callCount(MethodNode m, String o, MethodNode c) {
        int n = 0;
        if (m.instructions != null) {
            for (AbstractInsnNode in : m.instructions) {
                if (in instanceof MethodInsnNode mi && mi.owner.equals(o) && mi.name.equals(c.name) && mi.desc.equals(c.desc)) n++;
            }
        }
        return n;
    }

    /** the descend into another class's callee (stage 2), or null with the reason counted */
    private Descend crossDescend(AnnotationNode an, MethodNode handler, ClassNode tc, MethodNode vm, MethodNode wm, Anchor a,
                                 MethodNode into, String decl, MethodInsnNode call, List<Integer> sites, int[] ords, List<String> why) {
        String pre = into.name + into.desc + " of " + decl.substring(decl.lastIndexOf('/') + 1) + " holds V's site; guarded descend skipped: ";
        String kind = an.desc.substring(an.desc.lastIndexOf('/') + 1, an.desc.length() - 1);
        String skip = null;
        if (call == null) skip = "the target calls it more than once";
        else if (wm.name.equals("<clinit>")) skip = "static initializer";
        else if (!afterSuper(tc, wm, call)) skip = "the call precedes the constructor's super call";
        else if ((tc.access & Opcodes.ACC_INTERFACE) != 0) skip = "the target class is an interface";
        else if (MixinBridge.capturesLocals(handler)) skip = "the handler captures locals";
        else if (MixinBridge.sugarCount(handler) > 0) skip = "the handler takes sugar parameters";
        else if (cancels(handler)) skip = "the handler cancels";
        else if (overriddenBelow(decl, into)) skip = "a subclass overrides the callee (the call may not run this body)";
        if (skip != null) {
            why.add(pre + skip);
            return null;
        }
        boolean calleeStatic = (into.access & Opcodes.ACC_STATIC) != 0;
        boolean handlerStatic = (handler.access & Opcodes.ACC_STATIC) != 0;
        boolean onThis = !calleeStatic && receiverIsThis(tc.name, wm, call) && !handlerStatic;
        if (onThis) {
            // the on-instance path (superclass method called on this) has no case on the measured steps yet and no
            // real-server proof: not taken until it has one
            why.add(pre + "the callee runs on the target's instance (on-instance guard not yet proven on a server)");
            return null;
        }
        if (!onThis && (!handlerStatic && !touchesThisFree(handler) || usesOwnClass(handler, curMixin))) {
            why.add(pre + "the handler reads this or its own mixin, and the callee does not run on the target's instance");
            return null;
        }
        if (!onThis && !calleeStatic && handlerStatic) {
            why.add(pre + "a static handler for an instance callee");
            return null;
        }
        boolean toCir = false;
        if (an.desc.equals(INJECT)) {
            boolean wantCir = Type.getReturnType(into.desc).getSort() != Type.VOID;
            boolean hasCir = handler.desc.contains(CIR);
            if (hasCir && !Type.getReturnType(into.desc).equals(Type.getReturnType(vm.desc))) {
                why.add(pre + "the handler takes the target's return value");
                return null;
            }
            toCir = wantCir && !hasCir;
        }
        List<AbstractInsnNode> wo = SiteMatch.occurrences(into, in -> matchesAnchor(in, a));
        int ordinal;
        if (sites.size() == 1) {
            ordinal = a.ordinal >= 0 || wo.size() > 1 ? ords[0] : -1;
        } else {
            Set<Integer> got = new java.util.HashSet<>();
            for (int o : ords) got.add(o);
            if (got.size() != wo.size()) {
                why.add(pre + "V's sites are some of the callee's occurrences, not all");
                return null;
            }
            ordinal = -1;
        }
        AbstractInsnNode site = wo.get(ordinal >= 0 ? ordinal : 0);
        if (guardFallback(an, handler, site) == null) {
            why.add(pre + "no fallback for this handler's shape (@" + kind + ")");
            return null;
        }
        return new Descend(into, ordinal, toCir, false, false, call, tc.name, wm, site, decl, onThis);
    }

    /** the mixin a handler belongs to, set while a mixin is repaired (usesOwnClass) */
    private String curMixin = "";

    /** the one call of m in wm (owner tc or a subclass), or null */
    private static MethodInsnNode callOf(MethodNode wm, ClassNode tc, MethodNode m) {
        MethodInsnNode one = null;
        for (AbstractInsnNode in : wm.instructions) {
            if (in instanceof MethodInsnNode mi && mi.name.equals(m.name) && mi.desc.equals(m.desc)) {
                if (one != null) return null;
                one = mi;
            }
        }
        return one;
    }

    /**
     * why a shared callee cannot take a guarded descend (stage 1), or null when it can: the callee is called
     * directly by the target (depth 1), exactly once there, by name (no method reference), neither is a
     * constructor or static initializer, and the handler is one whose fallback hands back what the site had
     */
    private String guardable(AnnotationNode an, MethodNode handler, ClassNode tc, MethodNode wm, MethodNode m, MethodNode into,
                             Map<MethodNode, MethodNode> parent, Anchor a) {
        if (m != into || parent.get(into) != wm) return "the callee is not called by the target directly";
        if (into.name.startsWith("<")) return "the callee is a constructor or static initializer";
        if (wm.name.equals("<clinit>")) return "static initializer";
        MethodInsnNode one = callOf(wm, tc, into);
        if (one != null && !afterSuper(tc, wm, one)) return "the call precedes the constructor's super call";
        if (wm.instructions == null) return "target has no body";
        int n = 0;
        for (AbstractInsnNode in : wm.instructions) {
            if (in instanceof MethodInsnNode mi && mi.name.equals(into.name) && mi.desc.equals(into.desc)) n++;
            if (in instanceof org.objectweb.asm.tree.InvokeDynamicInsnNode id) {
                for (Object b : id.bsmArgs) {
                    if (b instanceof org.objectweb.asm.Handle h && h.getName().equals(into.name) && h.getDesc().equals(into.desc)) {
                        return "the target takes a method reference to the callee";
                    }
                }
            }
        }
        if (n != 1) return "the target calls the callee " + n + " times";
        String kind = an.desc.substring(an.desc.lastIndexOf('/') + 1, an.desc.length() - 1);
        if ((tc.access & Opcodes.ACC_INTERFACE) != 0) return "the target class is an interface";
        if (MixinBridge.capturesLocals(handler)) return "the handler captures locals";
        if (MixinBridge.sugarCount(handler) > 0) return "the handler takes sugar parameters";
        return null;
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
    /** the step's classRenames (W side: a restored body calls the facade a V class is renamed to) */
    public Map<String, String> classRenames = Map.of();
    /**
     * Chain.staticRedirects as a move (s6e B6, set by Main): "V owner.name(desc)" -> "layer class.name(desc as the jar
     * spells it)" of the static the step redirects V's static to, or null
     */
    public java.util.function.Function<String, String> staticMove;
    /** Chain.mixinMoves: "V owner.name(desc)" -> "W owner.name(desc)" of a static W keeps in another class */
    public Map<String, String> mixinMoves = Map.of();
    /** Chain.mixinMoveSelf: per mixinMoves key, the field hops from the W class's instance to V's (C17) */
    public Map<String, List<String[]>> mixinMoveSelf = Map.of();

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

    /**
     * smx4d (6): javac numbers anonymous classes in order (Outer$N), and the number moves when the outer class gains or
     * loses one before it while the members inside keep their names. For a target "Outer$N" (digits) that lacks a
     * member the mixin names with a descriptor (injector selectors, @Shadow / @Accessor / @Invoker by name + the
     * method's own descriptor): the one W class Outer$M (digits) declaring every such member, or null (none, or
     * several: no evidence which). Only exact name + descriptor; a renamed method needs a body follow (not here).
     */
    private String renumberedAnonymous(ClassNode mx, JsonObject refmap, String t) {
        int d = t.lastIndexOf('$');
        if (d < 0 || !t.substring(d + 1).matches("\\d+")) return null;
        List<String> wanted = new ArrayList<>();
        for (MethodNode m : mx.methods) {
            for (AnnotationNode an : annotations(m)) {
                for (String sel : strings(values(an).get("method"))) {
                    Sel sp = Sel.parse(refmapLookup(refmap, sel));
                    if (sp.desc != null && !sp.wildcard && (sp.owner == null || sp.owner.equals(t))) wanted.add(sp.name + sp.desc);
                }
                if (an.desc.equals(SHADOW) && !m.name.startsWith("<")) wanted.add(m.name + m.desc);
            }
        }
        if (wanted.isEmpty()) return null;
        ClassNode tc = target(t);
        if (tc != null && wanted.stream().allMatch(w -> tc.methods.stream().anyMatch(m -> (m.name + m.desc).equals(w)))) return null;
        String outer = t.substring(0, d + 1);
        List<String> hits = new ArrayList<>();
        var en = code.entries();
        while (en.hasMoreElements()) {
            String n = en.nextElement().getName();
            if (!n.startsWith(outer) || !n.endsWith(".class")) continue;
            String c = n.substring(0, n.length() - 6);
            if (c.equals(t) || !c.substring(outer.length()).matches("\\d+")) continue;
            ClassNode cc = target(c);
            if (cc != null && wanted.stream().allMatch(w -> cc.methods.stream().anyMatch(m -> (m.name + m.desc).equals(w)))) hits.add(c);
        }
        return hits.size() == 1 ? hits.get(0) : null;
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
        if (anchors != null && anchors.size() == 1 && anchors.get(0).kind.equals("FIELD") && anchors.get(0).desc != null
                && anchors.get(0).owner != null) {
            // a field access handled itself: (receiver of an instance field) (the value written, for a put), an Operation
            // after them for @WrapOperation; nothing else
            Anchor a = anchors.get(0);
            List<Type> hp = new ArrayList<>(List.of(Type.getArgumentTypes(h.desc)));
            boolean wrap = an.desc.endsWith("/WrapOperation;");
            if (!wrap && !an.desc.endsWith("/Redirect;")) return true;
            if (wrap) {
                if (hp.isEmpty() || !hp.get(hp.size() - 1).getDescriptor().equals(OPERATION)) return true;
                hp.remove(hp.size() - 1);
            }
            if (!hp.isEmpty() && hp.get(0).getDescriptor().equals(Type.getObjectType(a.owner).getDescriptor())) hp.remove(0);
            return !(hp.isEmpty() || hp.size() == 1 && hp.get(0).getDescriptor().equals(a.desc));
        }
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

    /**
     * W -> V converters of the step's wiring (Chain.argConverter: (W type, V type) -> {owner, name, desc}, or null);
     * set by Main. A captured target argument whose type the step changed is handed to the handler through one
     */
    public java.util.function.BiFunction<Type, Type, String[]> converter;

    private static final String COERCE = "Lorg/spongepowered/asm/mixin/injection/Coerce;";

    /**
     * the handler parameters Mixin fills with the target's arguments, {first, end} (end exclusive; the k-th of them
     * is the target's k-th argument), or null when the handler's shape does not say: @Inject before its
     * CallbackInfo(Returnable); the value-modifying injectors after their one value; @ModifyArg(s) after the
     * argument / Args; @Redirect / @WrapWithCondition / @WrapOperation of one INVOKE after the call's operands (and
     * the Operation)
     */
    static int[] capturedRange(AnnotationNode an, MethodNode h, List<Anchor> anchors, List<MethodNode> targets) {
        Type[] hp = Type.getArgumentTypes(h.desc);
        if (an.desc.equals(INJECT)) {
            for (int i = 0; i < hp.length; i++) {
                if (hp[i].getDescriptor().equals(CI) || hp[i].getDescriptor().equals(CIR)) return new int[] {0, i};
            }
            return null;
        }
        if (an.desc.endsWith("/ModifyVariable;") || an.desc.endsWith("/ModifyConstant;") || an.desc.endsWith("/ModifyReturnValue;")
                || an.desc.endsWith("/ModifyExpressionValue;") || an.desc.endsWith("/ModifyArg;") || an.desc.endsWith("/ModifyArgs;")) {
            return new int[] {1, hp.length};
        }
        boolean wrap = an.desc.endsWith("/WrapOperation;");
        if (!wrap && !an.desc.endsWith("/Redirect;") && !an.desc.endsWith("/WrapWithCondition;")) return null;
        if (anchors == null || anchors.size() != 1 || !anchors.get(0).kind.equals("INVOKE") || anchors.get(0).desc == null
                || anchors.get(0).name.equals("<init>")) return null;
        Anchor a = anchors.get(0);
        // the call's receiver is an operand when the call is an instance one -- whatever type the handler gives it
        // (a type match fails when the step moved or renamed the anchor's owner): read off the matched call in W's body
        Boolean isStatic = null;
        for (MethodNode m : targets) {
            for (AbstractInsnNode in : m.instructions) {
                if (in instanceof MethodInsnNode mi && mi.name.equals(a.name) && mi.desc.equals(a.desc)) {
                    boolean s = mi.getOpcode() == Opcodes.INVOKESTATIC;
                    if (isStatic != null && isStatic != s) return null;
                    isStatic = s;
                }
            }
        }
        if (isStatic == null) return null;
        int start = Type.getArgumentTypes(a.desc).length + (isStatic ? 0 : 1) + (wrap ? 1 : 0);
        return start <= hp.length ? new int[] {start, hp.length} : null;
    }

    /**
     * a kept injector's captured arguments against its target(s): why it cannot apply, or the conversions it needs
     * (convs / types: by handler parameter index) and, for an @Inject, the target's trailing arguments the handler
     * never took (Mixin's callback takes all of them or none), accepted and dropped at {@code insertAt}
     */
    record CapturedFit(String why, Map<Integer, String[]> convs, Map<Integer, Type> types, String note, List<Type> extra, int insertAt,
                       int[] pos, Type[] wArgs) {
        CapturedFit(String why, Map<Integer, String[]> convs, Map<Integer, Type> types, String note) {
            this(why, convs, types, note, List.of(), -1, null, null);
        }

        CapturedFit(String why, Map<Integer, String[]> convs, Map<Integer, Type> types, String note, List<Type> extra, int insertAt) {
            this(why, convs, types, note, extra, insertAt, null, null);
        }

        boolean changes() {
            return !convs.isEmpty() || !extra.isEmpty() || pos != null;
        }
    }

    /**
     * Where the step's wiring says a V method's arguments arrive in W's method of the same class (set by Main): for
     * ("owner", "name W-desc"), each {V desc, int[] args (per V argument the W index), String[][] wrap (per V argument a
     * W -> V static or null)} -- the overrideHooks of a same-class change (1.21 LivingEntity.method_20235:
     * (EquipmentSlot) -> (Item, EquipmentSlot), args [1]) and the mixinTargets. The selector of a kept injector still
     * names the method; its handler captured V's arguments.
     */
    public java.util.function.BiFunction<String, String, List<Object[]>> argMaps;

    /**
     * smx1 CASE 14: a handler capturing the target's arguments fails at apply once the step changed one's type (Mixin
     * compares each captured parameter with the target's argument; only an @Coerce parameter may differ). Per
     * captured parameter: the same type (or the type the step's classRenames give it) fits; another reference type
     * the step's wiring converts W -> V ({@link #converter}) is converted in a wrapper handler; anything else -- or
     * more captured parameters than the target now takes -- cannot apply, and the injector is dropped with a WARN
     */
    CapturedFit capturedFit(AnnotationNode an, MethodNode h, List<Anchor> anchors, List<MethodNode> targets, String owner) {
        String argsOnly = argsOnlyFit(an, h, targets);
        if (argsOnly != null) return new CapturedFit(argsOnly, new java.util.TreeMap<>(), new HashMap<>(), "");
        CapturedFit direct = capturedFitInPlace(an, h, anchors, targets);
        if (direct.why() == null || !an.desc.equals(INJECT) || argMaps == null || targets.isEmpty()) return direct;
        CapturedFit mapped = capturedFitMapped(h, targets, owner);
        return mapped != null ? mapped : direct;
    }

    /**
     * smx1 CASE 14 (third instance): an @Inject whose handler captured all of V's arguments, on a method the step
     * reshaped in place with a known argument mapping ({@link #argMaps}): Mixin hands the callback all of W's arguments,
     * a wrapper takes them and calls the mod's handler with V's -- each from the W argument the mapping names, through
     * the mapping's static or the step's converter when the type changed. Null when no mapping fits.
     */
    private CapturedFit capturedFitMapped(MethodNode h, List<MethodNode> targets, String owner) {
        int[] r = capturedRange(new AnnotationNode(INJECT), h, null, targets);
        if (r == null || r[0] >= r[1] || owner == null) return null;
        String wDesc = targets.get(0).desc;
        String name = targets.get(0).name;
        if (targets.stream().anyMatch(m -> !m.desc.equals(wDesc) || !m.name.equals(name))) return null;
        Type[] hp = Type.getArgumentTypes(h.desc);
        Type[] ta = Type.getArgumentTypes(wDesc);
        for (Object[] e : argMaps.apply(owner, name + wDesc)) {
            Type[] va = Type.getArgumentTypes((String) e[0]);
            int[] args = (int[]) e[1];
            String[][] wrap = (String[][]) e[2];
            if (va.length != r[1] - r[0] || args == null || args.length != va.length) continue;
            boolean sameV = true;
            for (int i = 0; sameV && i < va.length; i++) {
                Type p = hp[r[0] + i];
                sameV = p.equals(va[i]) || renamed.apply(va[i].getDescriptor()).equals(p.getDescriptor());
            }
            if (!sameV) continue;
            Map<Integer, String[]> convs = new java.util.TreeMap<>();
            StringBuilder note = new StringBuilder("V arguments from W's " + java.util.Arrays.toString(args));
            boolean ok = true;
            for (int i = 0; ok && i < va.length; i++) {
                int k = r[0] + i;
                if (args[i] < 0 || args[i] >= ta.length) {
                    ok = false;
                    break;
                }
                Type p = hp[k];
                Type t = ta[args[i]];
                if (p.equals(t) || renamed.apply(p.getDescriptor()).equals(t.getDescriptor()) || coerced(h, k)) continue;
                String[] c = wrap != null && i < wrap.length && wrap[i] != null ? wrap[i]
                    : (p.getSort() == Type.OBJECT || p.getSort() == Type.ARRAY) && converter != null ? converter.apply(t, p) : null;
                if (c == null) {
                    ok = false;
                    break;
                }
                convs.put(k, c);
                note.append(", arg ").append(i).append(" by ").append(c[0].substring(c[0].lastIndexOf('/') + 1)).append('.').append(c[1]);
            }
            if (!ok) continue;
            return new CapturedFit(null, convs, new HashMap<>(), note.toString(), List.of(), r[0], args.clone(), ta);
        }
        return null;
    }

    /**
     * A @ModifyVariable(argsOnly = true) with no index or name picks the target argument by the handler's type
     * (LocalVariableDiscriminator over the method's arguments): exactly one argument of that type without an ordinal,
     * more than the ordinal with one. A step that retyped the argument (1.21.2 RecipeManager.fromJson ResourceLocation
     * -> ResourceKey) leaves none, and Mixin fails the whole mixin at apply ("Found 0 candidate variables but exactly 1
     * is required"). Null when it fits on every target, else why not
     */
    private static String argsOnlyFit(AnnotationNode an, MethodNode h, List<MethodNode> targets) {
        if (!an.desc.endsWith("/ModifyVariable;")) return null;
        Map<String, Object> v = values(an);
        if (!Boolean.TRUE.equals(v.get("argsOnly")) || v.get("index") instanceof Integer i && i >= 0
                || v.get("name") != null && !list(v.get("name")).isEmpty()) return null;
        Type[] hp = Type.getArgumentTypes(h.desc);
        if (hp.length == 0) return null;
        Type want = hp[0];
        int ord = v.get("ordinal") instanceof Integer o ? o : -1;
        for (MethodNode m : targets) {
            int n = 0;
            for (Type t : Type.getArgumentTypes(m.desc)) if (t.equals(want)) n++;
            if (ord < 0 ? n != 1 : n <= ord) {
                return "@ModifyVariable(argsOnly) of type " + want.getDescriptor() + (ord < 0 ? "" : " ordinal " + ord) + ": "
                    + m.name + m.desc + " takes " + n + " argument(s) of that type";
            }
        }
        return null;
    }

    private CapturedFit capturedFitInPlace(AnnotationNode an, MethodNode h, List<Anchor> anchors, List<MethodNode> targets) {
        int[] r = capturedRange(an, h, anchors, targets);
        Map<Integer, String[]> convs = new java.util.TreeMap<>();
        Map<Integer, Type> types = new HashMap<>();
        if (r == null || r[0] >= r[1]) return new CapturedFit(null, convs, types, "");
        Type[] hp = Type.getArgumentTypes(h.desc);
        StringBuilder note = new StringBuilder();
        List<Type> extra = null;
        for (MethodNode m : targets) {
            Type[] ta = Type.getArgumentTypes(m.desc);
            if (an.desc.equals(INJECT) && r[1] - r[0] < ta.length) {
                // the target gained trailing arguments: the callback takes them and drops them
                List<Type> ex = List.of(ta).subList(r[1] - r[0], ta.length);
                if (extra != null && !extra.equals(ex)) {
                    return new CapturedFit("the target methods' trailing arguments differ", convs, types, "");
                }
                extra = ex;
            }
            for (int k = r[0]; k < r[1]; k++) {
                int i = k - r[0];
                if (i >= ta.length) {
                    return new CapturedFit("the handler captures " + (r[1] - r[0]) + " target arguments, " + m.name + m.desc
                        + " takes " + ta.length, convs, types, "");
                }
                Type p = hp[k];
                Type t = ta[i];
                if (p.equals(t) || renamed.apply(p.getDescriptor()).equals(t.getDescriptor()) || coerced(h, k)) continue;
                boolean refs = (p.getSort() == Type.OBJECT || p.getSort() == Type.ARRAY) && (t.getSort() == Type.OBJECT || t.getSort() == Type.ARRAY);
                String[] c = refs && converter != null ? converter.apply(t, p) : null;
                if (c != null && (!types.containsKey(k) || types.get(k).equals(t))) {
                    if (!types.containsKey(k)) note.append(" arg ").append(i).append(' ').append(t.getClassName()).append(" -> ")
                        .append(p.getClassName()).append(" by ").append(c[0].substring(c[0].lastIndexOf('/') + 1)).append('.').append(c[1]);
                    convs.put(k, c);
                    types.put(k, t);
                    continue;
                }
                return new CapturedFit("captures " + p.getDescriptor() + " as target argument " + i + ", " + m.name + m.desc
                    + " takes " + t.getDescriptor() + (refs ? "; the step's wiring has no W -> V converter for it" : ""), convs, types, "");
            }
        }
        if (extra != null) {
            note.append(" accepts and drops ").append(extra.size()).append(" trailing target argument(s)");
        }
        return new CapturedFit(null, convs, types, note.toString().trim(), extra == null ? List.of() : extra, r[1]);
    }

    private static boolean coerced(MethodNode h, int k) {
        for (List<AnnotationNode>[] pa : java.util.Arrays.<List<AnnotationNode>[]>asList(h.visibleParameterAnnotations, h.invisibleParameterAnnotations)) {
            if (pa != null && k < pa.length && pa[k] != null && pa[k].stream().anyMatch(a -> a.desc.equals(COERCE))) return true;
        }
        return false;
    }

    /** parameter annotations of the handler as the wrapper's: the inserted trailing target arguments carry none */
    @SuppressWarnings("unchecked")
    private static List<AnnotationNode>[] widen(List<AnnotationNode>[] pa, CapturedFit fit) {
        if (pa == null || fit.extra().isEmpty()) return pa;
        List<AnnotationNode>[] out = new List[pa.length + fit.extra().size()];
        for (int k = 0; k < pa.length; k++) out[k < fit.insertAt() ? k : k + fit.extra().size()] = pa[k];
        return out;
    }

    /**
     * The handler takes the target's new argument types: the mod's handler keeps its body under {@code <name>$cbcap}
     * (a plain private method, no annotations), and a handler of the new shape -- carrying the injector and parameter
     * annotations -- converts each changed argument by the step's W -> V static and calls it with the rest as they came
     */
    void reshapeCaptured(ClassNode mx, MethodNode h, CapturedFit fit) {
        Type[] hp = Type.getArgumentTypes(h.desc);
        if (fit.pos() != null) {
            reshapeMapped(mx, h, fit);
            return;
        }
        List<Type> np = new ArrayList<>(List.of(hp));
        for (Map.Entry<Integer, Type> e : fit.types().entrySet()) np.set(e.getKey(), e.getValue());
        if (!fit.extra().isEmpty()) np.addAll(fit.insertAt(), fit.extra());
        Type ret = Type.getReturnType(h.desc);
        boolean isStatic = (h.access & Opcodes.ACC_STATIC) != 0;
        boolean itf = (mx.access & Opcodes.ACC_INTERFACE) != 0;
        MethodNode w = new MethodNode(h.access, h.name, Type.getMethodDescriptor(ret, np.toArray(new Type[0])), null,
            h.exceptions == null ? null : h.exceptions.toArray(new String[0]));
        w.visibleAnnotations = h.visibleAnnotations;
        w.invisibleAnnotations = h.invisibleAnnotations;
        w.visibleParameterAnnotations = widen(h.visibleParameterAnnotations, fit);
        w.invisibleParameterAnnotations = widen(h.invisibleParameterAnnotations, fit);
        h.visibleAnnotations = null;
        h.invisibleAnnotations = null;
        h.visibleParameterAnnotations = null;
        h.invisibleParameterAnnotations = null;
        h.visibleAnnotableParameterCount = 0;
        h.invisibleAnnotableParameterCount = 0;
        h.name = h.name + "$cbcap";
        // synthetic: kept out of the merged-member indexes (LayerMembers), as a lambda body is
        h.access = (h.access & ~(Opcodes.ACC_PUBLIC | Opcodes.ACC_PROTECTED)) | Opcodes.ACC_PRIVATE | Opcodes.ACC_SYNTHETIC;
        int slot = 0;
        if (!isStatic) {
            w.instructions.add(new VarInsnNode(Opcodes.ALOAD, 0));
            slot = 1;
        }
        int stack = slot;
        for (int k = 0; k < np.size(); k++) {
            Type t = np.get(k);
            boolean dropped = !fit.extra().isEmpty() && k >= fit.insertAt() && k < fit.insertAt() + fit.extra().size();
            if (!dropped) {
                int hk = !fit.extra().isEmpty() && k >= fit.insertAt() ? k - fit.extra().size() : k;
                w.instructions.add(new VarInsnNode(t.getOpcode(Opcodes.ILOAD), slot));
                String[] c = fit.convs().get(hk);
                if (c != null) w.instructions.add(new MethodInsnNode(Opcodes.INVOKESTATIC, c[0], c[1], c[2], false));
                stack += Math.max(t.getSize(), hp[hk].getSize());
            }
            slot += t.getSize();
        }
        w.instructions.add(new MethodInsnNode(isStatic ? Opcodes.INVOKESTATIC : Opcodes.INVOKESPECIAL, mx.name, h.name, h.desc, itf));
        w.instructions.add(new InsnNode(ret.getOpcode(Opcodes.IRETURN)));
        w.maxLocals = slot;
        w.maxStack = Math.max(stack, ret.getSize());
        mx.methods.add(w);
    }

    /**
     * The @Inject handler of a mapped fit: W's arguments replace the captured V ones (fit.insertAt() .. + pos.length),
     * the rest (CallbackInfo, captured locals) stay; the wrapper loads V argument i from W argument pos[i] (converted
     * when fit.convs() says) and calls the mod's handler, kept as {@code <name>$cbcap}
     */
    private void reshapeMapped(ClassNode mx, MethodNode h, CapturedFit fit) {
        Type[] hp = Type.getArgumentTypes(h.desc);
        int r0 = fit.insertAt();
        int n = fit.pos().length;
        int m = fit.wArgs().length;
        List<Type> np = new ArrayList<>();
        for (int k = 0; k < r0; k++) np.add(hp[k]);
        np.addAll(List.of(fit.wArgs()));
        for (int k = r0 + n; k < hp.length; k++) np.add(hp[k]);
        Type ret = Type.getReturnType(h.desc);
        boolean isStatic = (h.access & Opcodes.ACC_STATIC) != 0;
        boolean itf = (mx.access & Opcodes.ACC_INTERFACE) != 0;
        MethodNode w = new MethodNode(h.access, h.name, Type.getMethodDescriptor(ret, np.toArray(new Type[0])), null,
            h.exceptions == null ? null : h.exceptions.toArray(new String[0]));
        w.visibleAnnotations = h.visibleAnnotations;
        w.invisibleAnnotations = h.invisibleAnnotations;
        w.visibleParameterAnnotations = remapParamAnnotations(h.visibleParameterAnnotations, r0, n, m);
        w.invisibleParameterAnnotations = remapParamAnnotations(h.invisibleParameterAnnotations, r0, n, m);
        h.visibleAnnotations = null;
        h.invisibleAnnotations = null;
        h.visibleParameterAnnotations = null;
        h.invisibleParameterAnnotations = null;
        h.visibleAnnotableParameterCount = 0;
        h.invisibleAnnotableParameterCount = 0;
        h.name = h.name + "$cbcap";
        h.access = (h.access & ~(Opcodes.ACC_PUBLIC | Opcodes.ACC_PROTECTED)) | Opcodes.ACC_PRIVATE | Opcodes.ACC_SYNTHETIC;
        int[] slot = new int[np.size()];
        int s0 = isStatic ? 0 : 1;
        for (int k = 0; k < np.size(); k++) {
            slot[k] = s0;
            s0 += np.get(k).getSize();
        }
        if (!isStatic) w.instructions.add(new VarInsnNode(Opcodes.ALOAD, 0));
        int stack = isStatic ? 0 : 1;
        for (int k = 0; k < hp.length; k++) {
            int wk = k < r0 ? k : k < r0 + n ? r0 + fit.pos()[k - r0] : k - n + m;
            Type t = np.get(wk);
            w.instructions.add(new VarInsnNode(t.getOpcode(Opcodes.ILOAD), slot[wk]));
            String[] c = fit.convs().get(k);
            if (c != null) w.instructions.add(new MethodInsnNode(Opcodes.INVOKESTATIC, c[0], c[1], c[2], false));
            stack += Math.max(t.getSize(), hp[k].getSize());
        }
        w.instructions.add(new MethodInsnNode(isStatic ? Opcodes.INVOKESTATIC : Opcodes.INVOKESPECIAL, mx.name, h.name, h.desc, itf));
        w.instructions.add(new InsnNode(ret.getOpcode(Opcodes.IRETURN)));
        w.maxLocals = s0;
        w.maxStack = Math.max(stack, ret.getSize());
        mx.methods.add(w);
    }

    /** parameter annotations after n captured parameters at r0 became m W arguments (those carry none) */
    @SuppressWarnings("unchecked")
    private static List<AnnotationNode>[] remapParamAnnotations(List<AnnotationNode>[] pa, int r0, int n, int m) {
        if (pa == null) return null;
        if (pa.length <= r0) return pa;
        List<AnnotationNode>[] out = new List[Math.max(pa.length, r0 + n) - n + m];
        for (int k = 0; k < pa.length; k++) {
            if (k < r0) out[k] = pa[k];
            else if (k >= r0 + n) out[k - n + m] = pa[k];
        }
        return out;
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
