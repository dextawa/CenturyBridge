package top.dext.centurybridge.data;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.PrintWriter;
import java.net.URI;
import java.nio.file.FileSystem;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;
import java.util.regex.Pattern;
import java.util.stream.Stream;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;
import org.objectweb.asm.ClassReader;
import org.objectweb.asm.Handle;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.Type;
import org.objectweb.asm.tree.AbstractInsnNode;
import org.objectweb.asm.tree.AnnotationNode;
import org.objectweb.asm.tree.ClassNode;
import org.objectweb.asm.tree.FieldInsnNode;
import org.objectweb.asm.tree.FieldNode;
import org.objectweb.asm.tree.InvokeDynamicInsnNode;
import org.objectweb.asm.tree.LdcInsnNode;
import org.objectweb.asm.tree.MethodInsnNode;
import org.objectweb.asm.tree.MethodNode;
import org.objectweb.asm.tree.MultiANewArrayInsnNode;
import org.objectweb.asm.tree.TypeInsnNode;
import org.objectweb.asm.tree.analysis.Analyzer;
import org.objectweb.asm.tree.analysis.AnalyzerException;
import org.objectweb.asm.tree.analysis.BasicValue;
import org.objectweb.asm.tree.analysis.SimpleVerifier;

/**
 * Independent link check of converted jars against the REAL target runtime.
 *
 * The converter's audit grades its own homework: a call site counts as
 * "covered" or "redirected" because the wiring table says so, and a member
 * counts as alive because its intermediary name is absent from the segment
 * death lists (non-intermediary names -- fabric-api, libraries, post-wall
 * mojang names -- are never examined at all). This tool reads none of the
 * engine's tables. It loads the target classpath (game jar, libraries,
 * fabric-api incl. jar-in-jar, the CenturyBridge jar, the JDK), folds every
 * mixin's added members and interfaces into its targets, and resolves every
 * reference in the converted bytecode the way the JVM will:
 *
 *   CLASS      referenced class absent                  NoClassDefFoundError
 *   METHOD     name+desc not found up the hierarchy     NoSuchMethodError
 *   FIELD      name+desc not found                      NoSuchFieldError
 *   ICCE       static-ness / interface-ness mismatch    IncompatibleClassChangeError
 *   LAMBDA     lambda's SAM no longer the interface's   AbstractMethodError
 *              abstract method
 *   ABSTRACT   concrete class misses an abstract member AbstractMethodError
 *   ORPHAN     mod override (intermediary method_N)     silently never called
 *              no longer overrides anything
 *   TYPE       value reaches a class-typed slot it      VerifyError (the whole
 *              does not extend (type flow over the      class is rejected at
 *              real hierarchy)                          link time)
 *   VERIFY     method body fails analysis otherwise     VerifyError
 *
 * Each finding is then joined against the engine's full-audit.tsv (optional):
 *   CLAIMED  engine recorded the symbol as fixed (covered/redirect/rename/hook)
 *            or the dangling ref points into CenturyBridge's own classes
 *   KNOWN    engine reported it (ISSUE/LEDGERED/TOMBSTONE/...)
 *   BLIND    engine never flagged it
 *   EXTDEP   owner outside the platform (another mod's API missing) -- not ours
 *
 *   link-check <outDir> <full-audit.tsv|-> <subjects...> -- <classpath...>
 *
 * subjects: converted jars (or dirs of jars) whose bytecode is checked; they
 * are also on the classpath. classpath: jars, or dirs searched for *.jar.
 */
public final class LinkCheck {

    private static final String MIXIN = "Lorg/spongepowered/asm/mixin/Mixin;";
    private static final String SHADOW = "Lorg/spongepowered/asm/mixin/Shadow;";
    private static final Pattern INTER_METHOD = Pattern.compile("method_\\d+");
    private static final String[] PLATFORM = {
        "net/minecraft/", "com/mojang/", "net/fabricmc/", "org/joml/", "io/netty/",
        "com/google/", "it/unimi/", "org/slf4j/", "org/apache/", "org/lwjgl/",
        "java/", "javax/", "jdk/", "sun/", "top/dext/centurybridge/", "centurybridge/",
        "org/spongepowered/", "org/objectweb/", "com/llamalad7/"};
    private static final Set<String> FIXED_FATES = Set.of(
        "covered", "static-redirect", "instance-redirect", "field-redirect", "super-redirect",
        "method-rename", "field-rename", "lookup-hooked", "override-hooked", "ctor-redirect",
        "class-rename", "facade", "field-getter", "class-renamed-field", "super-ctor-adapt", "super-ctor-reshape");

    /** minimal class model; mixins fold their members into the target's */
    static final class Cls {
        final String name;
        String superName;
        final Set<String> interfaces = new HashSet<>();
        int access;
        final Map<String, Integer> methods = new HashMap<>(); // name+desc -> access
        final Map<String, Integer> fields = new HashMap<>();  // name+desc -> access
        String origin;
        Cls(String name) { this.name = name; }
        boolean isInterface() { return (access & Opcodes.ACC_INTERFACE) != 0; }
    }

    private final Map<String, Cls> index = new HashMap<>();
    private final Map<String, Path> jrt = new HashMap<>();
    private final List<ClassNode> mixins = new ArrayList<>();
    /** mixins some config still lists; triage-stripped ones never load and are neither folded nor checked */
    private final Set<String> listedMixins = new HashSet<>();
    /** subject jar tag -> its class nodes */
    private final Map<String, List<ClassNode>> subjects = new LinkedHashMap<>();
    private final Map<String, String> auditFate = new HashMap<>();
    /**
     * Fates the converter recorded for super(...)/this(...) sites, apart from NEW
     * sites of the same constructor: a factory (ctor-redirect) takes a NEW site and
     * can never take a delegation, so one symbol is routinely "fixed" at its NEW
     * sites and ledgered at its super sites. Joined by symbol alone, every super
     * site of a redirected constructor read as a false CLAIMED.
     */
    private final Map<String, String> superCtorFate = new HashMap<>();

    record Finding(String bucket, String kind, String jar, String where, String ref, String note) {}
    private final List<Finding> findings = new ArrayList<>();

    public static void run(String[] args) throws IOException {
        Path outDir = Path.of(args[1]);
        String audit = args[2];
        List<Path> subj = new ArrayList<>();
        List<Path> cp = new ArrayList<>();
        boolean afterSep = false;
        for (int i = 3; i < args.length; i++) {
            if (args[i].equals("--")) { afterSep = true; continue; }
            (afterSep ? cp : subj).add(Path.of(args[i]));
        }
        LinkCheck lc = new LinkCheck();
        if (!audit.equals("-")) lc.loadAudit(Path.of(audit));
        lc.loadJrt();
        for (Path p : cp) for (Path j : jarsOf(p)) lc.loadJar(j, null, j.getFileName().toString());
        for (Path p : subj) for (Path j : jarsOf(p)) lc.loadJar(j, j.getFileName().toString(), j.getFileName().toString());
        lc.foldMixins();
        lc.checkAll();
        lc.write(outDir);
    }

    private static List<Path> jarsOf(Path p) throws IOException {
        if (Files.isDirectory(p)) {
            try (Stream<Path> s = Files.walk(p)) {
                return s.filter(f -> f.toString().endsWith(".jar")).sorted().toList();
            }
        }
        return List.of(p);
    }

    private void loadAudit(Path tsv) throws IOException {
        for (String line : Files.readAllLines(tsv)) {
            String[] f = line.split("\t", 3);
            if (f.length < 3 || f[0].equals("count")) continue;
            String fate = f[1];
            // the converter spells a delegation site's fate apart: SUPER-CTOR-ISSUE[..],
            // super-ctor-reshape / -adapt, "LEDGERED[side] super-ctor: reason"
            boolean superSite = fate.startsWith("SUPER-CTOR-ISSUE") || fate.startsWith("super-ctor-")
                || fate.matches("LEDGERED\\[[^\\]]*\\] super-ctor:.*");
            int b = fate.indexOf('[');
            if (b > 0) fate = fate.substring(0, b);
            // several fates per symbol are possible (different sites); a KNOWN one wins
            // over a FIXED one only if nothing claimed it fixed -- keep the most "claimed"
            Map<String, String> into = superSite ? superCtorFate : auditFate;
            String prev = into.get(f[2]);
            if (prev == null || FIXED_FATES.contains(fate)) into.put(f[2], fate);
        }
    }

    private void loadJrt() throws IOException {
        FileSystem fs = FileSystems.getFileSystem(URI.create("jrt:/"));
        try (Stream<Path> mods = Files.list(fs.getPath("/modules"))) {
            for (Path m : mods.toList()) {
                try (Stream<Path> s = Files.walk(m)) {
                    s.filter(f -> f.toString().endsWith(".class")).forEach(f -> {
                        String rel = m.relativize(f).toString().replace('\\', '/');
                        jrt.putIfAbsent(rel.substring(0, rel.length() - 6), f);
                    });
                }
            }
        }
    }

    private void loadJar(Path jar, String subjectTag, String origin) throws IOException {
        try (InputStream in = Files.newInputStream(jar)) {
            loadZip(new ZipInputStream(in), subjectTag, origin);
        }
    }

    /** recurses into jar-in-jar: fabric-api modules and mods' bundled libraries */
    private void loadZip(ZipInputStream zin, String subjectTag, String origin) throws IOException {
        ZipEntry e;
        while ((e = zin.getNextEntry()) != null) {
            String n = e.getName();
            if (n.endsWith(".jar")) {
                byte[] b = zin.readAllBytes();
                loadZip(new ZipInputStream(new ByteArrayInputStream(b)), subjectTag, origin + "!" + n);
            } else if (n.endsWith(".json") && !n.contains("/")) {
                collectMixinConfig(zin.readAllBytes());
            } else if (n.endsWith(".class") && !n.startsWith("META-INF/versions/") && !n.endsWith("module-info.class")) {
                byte[] b = zin.readAllBytes();
                ClassNode cn = new ClassNode();
                try {
                    new ClassReader(b).accept(cn, subjectTag != null ? ClassReader.SKIP_FRAMES : ClassReader.SKIP_CODE);
                } catch (RuntimeException ex) {
                    continue;
                }
                if (index.containsKey(cn.name)) continue; // first on the path wins, like the loader
                index.put(cn.name, model(cn, origin));
                if (isMixin(cn)) mixins.add(cn);
                if (subjectTag != null) subjects.computeIfAbsent(subjectTag, k -> new ArrayList<>()).add(cn);
            }
        }
    }

    private void collectMixinConfig(byte[] b) {
        try {
            var el = com.google.gson.JsonParser.parseString(new String(b, java.nio.charset.StandardCharsets.UTF_8));
            if (!el.isJsonObject()) return;
            var o = el.getAsJsonObject();
            if (!o.has("package")) return;
            String pkg = o.get("package").getAsString().replace('.', '/');
            for (String k : new String[] {"mixins", "client", "server"}) {
                if (o.has(k) && o.get(k).isJsonArray())
                    for (var e : o.getAsJsonArray(k)) listedMixins.add(pkg + "/" + e.getAsString().replace('.', '/'));
            }
        } catch (RuntimeException ex) {
            // not a mixin config
        }
    }

    private boolean live(ClassNode cn) {
        return !isMixin(cn) || listedMixins.contains(cn.name);
    }

    private static Cls model(ClassNode cn, String origin) {
        Cls c = new Cls(cn.name);
        c.superName = cn.superName;
        c.interfaces.addAll(cn.interfaces);
        c.access = cn.access;
        c.origin = origin;
        for (MethodNode m : cn.methods) c.methods.put(m.name + m.desc, m.access);
        for (FieldNode f : cn.fields) c.fields.put(f.name + f.desc, f.access);
        return c;
    }

    private Cls cls(String name) {
        Cls c = index.get(name);
        if (c != null || !jrt.containsKey(name)) return c;
        try {
            ClassNode cn = new ClassNode();
            new ClassReader(Files.readAllBytes(jrt.get(name))).accept(cn, ClassReader.SKIP_CODE);
            c = model(cn, "jrt");
            index.put(name, c);
            return c;
        } catch (IOException ex) {
            return null;
        }
    }

    private static boolean isMixin(ClassNode cn) {
        return ann(cn.invisibleAnnotations, MIXIN) != null || ann(cn.visibleAnnotations, MIXIN) != null;
    }

    private static AnnotationNode ann(List<AnnotationNode> list, String desc) {
        if (list == null) return null;
        for (AnnotationNode a : list) if (a.desc.equals(desc)) return a;
        return null;
    }

    private static List<String> mixinTargets(ClassNode cn) {
        AnnotationNode a = ann(cn.invisibleAnnotations, MIXIN);
        if (a == null) a = ann(cn.visibleAnnotations, MIXIN);
        List<String> out = new ArrayList<>();
        if (a == null || a.values == null) return out;
        for (int i = 0; i + 1 < a.values.size(); i += 2) {
            Object v = a.values.get(i + 1);
            if (!(v instanceof List<?> l)) continue;
            for (Object o : l) {
                if (o instanceof Type t) out.add(t.getInternalName());
                else if (o instanceof String s) out.add(s.replace('.', '/'));
            }
        }
        return out;
    }

    /** a mixin's non-shadow members and its interfaces exist on the target at runtime */
    private void foldMixins() {
        for (ClassNode mx : mixins) {
            if (!live(mx)) continue;
            for (String t : mixinTargets(mx)) {
                Cls tc = cls(t);
                if (tc == null) continue;
                tc.interfaces.addAll(mx.interfaces);
                for (MethodNode m : mx.methods) {
                    if (m.name.equals("<init>") || m.name.equals("<clinit>")) continue;
                    if (ann(m.visibleAnnotations, SHADOW) != null || ann(m.invisibleAnnotations, SHADOW) != null) continue;
                    // accessor/invoker interfaces: abstract on the mixin, implemented on the target
                    tc.methods.putIfAbsent(m.name + m.desc, m.access & ~Opcodes.ACC_ABSTRACT);
                }
                for (FieldNode f : mx.fields) {
                    if (ann(f.visibleAnnotations, SHADOW) != null || ann(f.invisibleAnnotations, SHADOW) != null) continue;
                    tc.fields.putIfAbsent(f.name + f.desc, f.access);
                }
            }
        }
    }

    // ---------------------------------------------------------------- resolution

    /** null = resolved; "?" = hierarchy incomplete (a missing class, reported separately) */
    private static final Object UNKNOWN = new Object();

    /** JVM method resolution: class chain, then all superinterfaces. Returns access or null/UNKNOWN. */
    private Object findMethod(String owner, String nameDesc) {
        Set<String> seenIfaces = new HashSet<>();
        List<String> ifaces = new ArrayList<>();
        boolean incomplete = false;
        for (String c = owner; c != null; ) {
            Cls k = cls(c);
            if (k == null) { incomplete = true; break; }
            Integer acc = k.methods.get(nameDesc);
            if (acc != null) return acc;
            ifaces.addAll(k.interfaces);
            c = k.superName;
        }
        while (!ifaces.isEmpty()) {
            String i = ifaces.remove(ifaces.size() - 1);
            if (!seenIfaces.add(i)) continue;
            Cls k = cls(i);
            if (k == null) { incomplete = true; continue; }
            Integer acc = k.methods.get(nameDesc);
            if (acc != null) return acc;
            ifaces.addAll(k.interfaces);
        }
        if (!owner.equals("java/lang/Object")) {
            Cls obj = cls("java/lang/Object");
            if (obj != null && obj.methods.containsKey(nameDesc)) return obj.methods.get(nameDesc);
        }
        return incomplete ? UNKNOWN : null;
    }

    private Object findField(String owner, String nameDesc) {
        boolean incomplete = false;
        List<String> todo = new ArrayList<>(List.of(owner));
        Set<String> seen = new HashSet<>();
        while (!todo.isEmpty()) {
            String c = todo.remove(0);
            if (!seen.add(c)) continue;
            Cls k = cls(c);
            if (k == null) { incomplete = true; continue; }
            Integer acc = k.fields.get(nameDesc);
            if (acc != null) return acc;
            todo.addAll(k.interfaces);
            if (k.superName != null) todo.add(k.superName);
        }
        return incomplete ? UNKNOWN : null;
    }

    private boolean hierarchyKnown(String name) {
        Set<String> seen = new HashSet<>();
        List<String> todo = new ArrayList<>(List.of(name));
        while (!todo.isEmpty()) {
            String c = todo.remove(0);
            if (!seen.add(c)) continue;
            Cls k = cls(c);
            if (k == null) return false;
            todo.addAll(k.interfaces);
            if (k.superName != null) todo.add(k.superName);
        }
        return true;
    }

    private static boolean platform(String owner) {
        for (String p : PLATFORM) if (owner.startsWith(p)) return true;
        return false;
    }

    // ---------------------------------------------------------------- checks

    private String curJar;
    private Set<String> reported; // per jar: dedupe identical (kind, ref)
    /** the instruction being checked is a constructor's super(...)/this(...) call */
    private boolean delegationSite;

    private void checkAll() {
        for (var e : subjects.entrySet()) {
            curJar = e.getKey();
            reported = new HashSet<>();
            for (ClassNode cn : e.getValue()) {
                if (cn.name.startsWith("centurybridge/gen/Tombstones")) continue; // lazy-fail by design
                if (!live(cn)) continue; // stripped by triage: never applied
                checkClass(cn);
            }
        }
    }

    private void checkClass(ClassNode cn) {
        boolean mixin = isMixin(cn);
        if (cn.superName != null) needClass(cn.superName, cn.name, "extends");
        for (String i : cn.interfaces) needClass(i, cn.name, "implements");
        for (MethodNode m : cn.methods) {
            String where = cn.name + "." + m.name + m.desc;
            if (m.instructions != null) {
                Map<String, Integer> pendingNew = new HashMap<>();
                for (AbstractInsnNode in : m.instructions) {
                    delegationSite = isDelegation(in, pendingNew);
                    checkInsn(in, where);
                }
                delegationSite = false;
            }
        }
        if (mixin) return; // members of mixins are MixinCheck's job
        checkTypes(cn);
        if ((cn.access & (Opcodes.ACC_ABSTRACT | Opcodes.ACC_INTERFACE)) == 0) checkAbstracts(cn);
        checkOrphans(cn);
    }

    /**
     * Type-flow check of every method body against the real hierarchy: the JVM's
     * verifier rejects the whole class (VerifyError) when a value meets a slot of a
     * class type it does not extend. Renaming a mod's hierarchy onto a facade that is
     * not a subtype of the surviving vanilla type produces exactly that, and no
     * member-resolution check can see it. Mixin bodies are skipped: they only acquire
     * their real `this` type once merged into the target.
     */
    private void checkTypes(ClassNode cn) {
        for (MethodNode m : cn.methods) {
            if (m.instructions == null || m.instructions.size() == 0) continue;
            String where = cn.name + "." + m.name + m.desc;
            IndexVerifier v = new IndexVerifier(cn);
            try {
                new Analyzer<>(v).analyze(cn.name, m);
            } catch (AnalyzerException ex) {
                if (v.expected != null) {
                    reportType(where, v.expected, v.actual, ex.getMessage());
                } else {
                    report("VERIFY", where, where, String.valueOf(ex.getMessage()));
                }
            } catch (RuntimeException ex) {
                report("VERIFY", where, where, ex.getClass().getSimpleName() + ": " + ex.getMessage());
            }
        }
    }

    private void reportType(String where, Type expected, Type actual, String message) {
        String exp = expected.getSort() == Type.OBJECT ? expected.getInternalName() : expected.getDescriptor();
        if (!reported.add("TYPE " + exp)) return;
        String bucket;
        if (actual.getSort() == Type.OBJECT && hasFacadeAncestor(actual.getInternalName())) {
            bucket = "CLAIMED"; // the converter moved this value's hierarchy onto a facade the slot rejects
        } else if (expected.getSort() == Type.OBJECT && !platform(exp) && !subjectOwns(exp)) {
            bucket = "EXTDEP";
        } else {
            bucket = "BLIND";
        }
        findings.add(new Finding(bucket, "TYPE", curJar, where, exp,
            "found " + (actual.getSort() == Type.OBJECT ? actual.getInternalName() : actual.getDescriptor())
            + " (" + message + ")"));
    }

    private boolean hasFacadeAncestor(String name) {
        if (name.startsWith("top/dext/centurybridge/")) return true;
        if (cls(name) == null) return false;
        for (String c : supertypes(name)) if (c.startsWith("top/dext/centurybridge/")) return true;
        return false;
    }

    private boolean subjectOwns(String name) {
        for (ClassNode cn : subjects.getOrDefault(curJar, List.of())) if (cn.name.equals(name)) return true;
        return false;
    }

    /** SimpleVerifier answering subtype questions from the index instead of loading classes. */
    private final class IndexVerifier extends SimpleVerifier {
        Type expected;
        Type actual;

        IndexVerifier(ClassNode cn) {
            super(Opcodes.ASM9, Type.getObjectType(cn.name),
                  cn.superName == null ? null : Type.getObjectType(cn.superName),
                  cn.interfaces.stream().map(Type::getObjectType).toList(),
                  (cn.access & Opcodes.ACC_INTERFACE) != 0);
        }

        @Override
        protected boolean isSubTypeOf(BasicValue value, BasicValue expectedValue) {
            Type exp = expectedValue.getType();
            Type typ = value.getType();
            boolean ok;
            if (isReference(exp) && isReference(typ) && !typ.getInternalName().equals("null")) {
                // SimpleVerifier would ask getClass(exp).isInterface() on a miss; the index
                // already answers that (isAssignableFrom relaxes interface slots like the JVM)
                ok = isAssignableFrom(exp, typ);
            } else {
                ok = super.isSubTypeOf(value, expectedValue); // primitives, null: no class lookup
            }
            if (!ok && expected == null) {
                expected = exp;
                actual = typ;
            }
            return ok;
        }

        private boolean isReference(Type t) {
            return t != null && (t.getSort() == Type.OBJECT || t.getSort() == Type.ARRAY);
        }

        @Override
        protected boolean isInterface(Type type) {
            if (type.getSort() != Type.OBJECT) return false;
            Cls c = cls(type.getInternalName());
            return c != null && c.isInterface();
        }

        @Override
        protected Type getSuperClass(Type type) {
            if (type.getSort() != Type.OBJECT) return Type.getObjectType("java/lang/Object");
            Cls c = cls(type.getInternalName());
            return c == null || c.superName == null ? null : Type.getObjectType(c.superName);
        }

        @Override
        protected boolean isAssignableFrom(Type type, Type other) {
            if (type == null || other == null) return false;
            if (type.equals(other)) return true;
            if (type.getSort() == Type.ARRAY) {
                if (other.getSort() != Type.ARRAY) return false;
                if (type.getDimensions() == other.getDimensions()) {
                    Type te = type.getElementType();
                    Type oe = other.getElementType();
                    if (te.getSort() != Type.OBJECT || oe.getSort() != Type.OBJECT) return te.equals(oe);
                    return isAssignableFrom(te, oe);
                }
                if (type.getDimensions() > other.getDimensions()) return false;
                String e = type.getElementType().getInternalName();
                return type.getElementType().getSort() == Type.OBJECT && arrayRoot(e);
            }
            if (type.getSort() != Type.OBJECT) return false;
            String target = type.getInternalName();
            if (target.equals("java/lang/Object")) return true;
            if (other.getSort() == Type.ARRAY) return arrayRoot(target);
            if (other.getSort() != Type.OBJECT) return false;
            Cls t = cls(target);
            if (t == null) return true;        // unknown type: already a CLASS finding
            if (t.isInterface()) return true;  // the verifier does not check interface assignment
            for (String c = other.getInternalName(); c != null; ) {
                if (c.equals(target)) return true;
                Cls k = cls(c);
                if (k == null) return true;    // broken chain: already a CLASS finding
                c = k.superName;
            }
            return false;
        }

        private boolean arrayRoot(String name) {
            return name.equals("java/lang/Object") || name.equals("java/lang/Cloneable")
                   || name.equals("java/io/Serializable");
        }

        @Override
        protected Class<?> getClass(Type type) {
            throw new UnsupportedOperationException("link-check resolves types from its index, never by loading " + type);
        }
    }

    /**
     * An invokespecial <init> that no pending NEW of its type pairs with: the
     * receiver is the object under construction. javac nests NEW/<init> pairs in
     * code order (new A(new B()) is NEW A, NEW B, <init> B, <init> A), so a count
     * per type is enough.
     */
    private static boolean isDelegation(AbstractInsnNode in, Map<String, Integer> pendingNew) {
        if (in.getOpcode() == Opcodes.NEW) {
            pendingNew.merge(((TypeInsnNode) in).desc, 1, Integer::sum);
            return false;
        }
        if (in.getOpcode() != Opcodes.INVOKESPECIAL || !((MethodInsnNode) in).name.equals("<init>")) {
            return false;
        }
        String owner = ((MethodInsnNode) in).owner;
        Integer n = pendingNew.get(owner);
        if (n != null && n > 0) {
            pendingNew.put(owner, n - 1);
            return false;
        }
        return true;
    }

    private void checkInsn(AbstractInsnNode in, String where) {
        if (in instanceof MethodInsnNode mi) {
            checkMethodRef(mi.owner, mi.name, mi.desc, mi.getOpcode(), mi.itf, where);
        } else if (in instanceof FieldInsnNode fi) {
            checkFieldRef(fi.owner, fi.name, fi.desc, fi.getOpcode(), where);
        } else if (in instanceof TypeInsnNode ti) {
            needType(ti.desc, where);
        } else if (in instanceof MultiANewArrayInsnNode ma) {
            needType(ma.desc, where);
        } else if (in instanceof LdcInsnNode ldc && ldc.cst instanceof Type t && t.getSort() == Type.OBJECT) {
            needClass(t.getInternalName(), where, "ldc");
        } else if (in instanceof InvokeDynamicInsnNode indy) {
            checkHandle(indy.bsm, where);
            for (Object a : indy.bsmArgs) if (a instanceof Handle h) checkHandle(h, where);
            if (indy.bsm.getOwner().equals("java/lang/invoke/LambdaMetafactory")) checkLambda(indy, where);
        }
    }

    private void needType(String desc, String where) {
        Type t = desc.startsWith("[") ? Type.getType(desc) : Type.getObjectType(desc);
        while (t.getSort() == Type.ARRAY) t = t.getElementType();
        if (t.getSort() == Type.OBJECT) needClass(t.getInternalName(), where, "type");
    }

    private boolean needClass(String name, String where, String how) {
        if (name.startsWith("[")) { needType(name, where); return true; }
        if (cls(name) != null) return true;
        report("CLASS", where, name, how);
        return false;
    }

    private void checkMethodRef(String owner, String name, String desc, int op, boolean itf, String where) {
        if (owner.startsWith("[")) return; // array clone() etc.
        if ((owner.equals("java/lang/invoke/MethodHandle") || owner.equals("java/lang/invoke/VarHandle"))) return;
        if (!needClass(owner, where, "owner")) return;
        Object acc = findMethod(owner, name + desc);
        if (acc == UNKNOWN) return;
        if (acc == null) {
            report("METHOD", where, owner + "." + name + desc, "");
            return;
        }
        int a = (Integer) acc;
        boolean isStatic = (a & Opcodes.ACC_STATIC) != 0;
        if (op == Opcodes.INVOKESTATIC && !isStatic) report("ICCE", where, owner + "." + name + desc, "invokestatic on instance method");
        if ((op == Opcodes.INVOKEVIRTUAL || op == Opcodes.INVOKEINTERFACE) && isStatic)
            report("ICCE", where, owner + "." + name + desc, "virtual call on static method");
        Cls k = cls(owner);
        if (op == Opcodes.INVOKEINTERFACE && !k.isInterface()) report("ICCE", where, owner + "." + name + desc, "invokeinterface on class");
        if (op == Opcodes.INVOKEVIRTUAL && k.isInterface()) report("ICCE", where, owner + "." + name + desc, "invokevirtual on interface");
    }

    private void checkFieldRef(String owner, String name, String desc, int op, String where) {
        if (!needClass(owner, where, "owner")) return;
        Object acc = findField(owner, name + desc);
        if (acc == UNKNOWN) return;
        if (acc == null) {
            report("FIELD", where, owner + "." + name + ":" + desc, "");
            return;
        }
        boolean isStatic = ((Integer) acc & Opcodes.ACC_STATIC) != 0;
        boolean wantStatic = op == Opcodes.GETSTATIC || op == Opcodes.PUTSTATIC;
        if (isStatic != wantStatic) report("ICCE", where, owner + "." + name + ":" + desc, wantStatic ? "static access to instance field" : "instance access to static field");
    }

    private void checkHandle(Handle h, String where) {
        int tag = h.getTag();
        if (tag <= Opcodes.H_PUTSTATIC) {
            int op = switch (tag) {
                case Opcodes.H_GETFIELD -> Opcodes.GETFIELD;
                case Opcodes.H_GETSTATIC -> Opcodes.GETSTATIC;
                case Opcodes.H_PUTFIELD -> Opcodes.PUTFIELD;
                default -> Opcodes.PUTSTATIC;
            };
            checkFieldRef(h.getOwner(), h.getName(), h.getDesc(), op, where);
        } else {
            int op = switch (tag) {
                case Opcodes.H_INVOKESTATIC -> Opcodes.INVOKESTATIC;
                case Opcodes.H_INVOKEINTERFACE -> Opcodes.INVOKEINTERFACE;
                case Opcodes.H_INVOKESPECIAL, Opcodes.H_NEWINVOKESPECIAL -> Opcodes.INVOKESPECIAL;
                default -> Opcodes.INVOKEVIRTUAL;
            };
            checkMethodRef(h.getOwner(), h.getName(), h.getDesc(), op, h.isInterface(), where);
        }
    }

    /** the lambda object implements (name, samDesc); every other abstract member of the interface is AME */
    private void checkLambda(InvokeDynamicInsnNode indy, String where) {
        String iface = Type.getReturnType(indy.desc).getInternalName();
        if (indy.bsmArgs.length < 1 || !(indy.bsmArgs[0] instanceof Type sam)) return;
        if (cls(iface) == null || !hierarchyKnown(iface)) return; // CLASS reported elsewhere
        Set<String> provided = new HashSet<>();
        provided.add(indy.name + sam.getDescriptor());
        if (indy.bsm.getName().equals("altMetafactory") && indy.bsmArgs.length > 3 && indy.bsmArgs[3] instanceof Integer flags) {
            int i = 4;
            if ((flags & 2) != 0 && indy.bsmArgs.length > i) { // FLAG_MARKERS
                int n = (Integer) indy.bsmArgs[i++]; i += n;
            }
            if ((flags & 4) != 0 && indy.bsmArgs.length > i) { // FLAG_BRIDGES
                int n = (Integer) indy.bsmArgs[i++];
                for (int j = 0; j < n && i < indy.bsmArgs.length; j++, i++)
                    if (indy.bsmArgs[i] instanceof Type b) provided.add(indy.name + b.getDescriptor());
            }
        }
        for (String abs : abstractsOf(iface, false)) {
            if (!provided.contains(abs)) {
                report("LAMBDA", where, iface + "." + abs, "lambda implements " + indy.name + sam.getDescriptor());
            }
        }
    }

    /** abstract name+desc still unimplemented across the type's hierarchy (Object methods excluded) */
    private Set<String> abstractsOf(String type, boolean concreteClass) {
        Set<String> implemented = new HashSet<>();
        Set<String> abstracts = new HashSet<>();
        Cls obj = cls("java/lang/Object");
        if (obj != null) implemented.addAll(obj.methods.keySet());
        List<String> ifaces = new ArrayList<>();
        for (String c = type; c != null; ) {
            Cls k = cls(c);
            if (k == null) return Set.of();
            for (var m : k.methods.entrySet()) {
                if ((m.getValue() & Opcodes.ACC_STATIC) != 0 || m.getKey().startsWith("<")) continue;
                if ((m.getValue() & Opcodes.ACC_ABSTRACT) != 0) {
                    if (!implemented.contains(m.getKey())) abstracts.add(m.getKey());
                } else {
                    implemented.add(m.getKey());
                }
            }
            ifaces.addAll(k.interfaces);
            c = k.isInterface() ? null : k.superName;
        }
        Set<String> seen = new HashSet<>();
        List<Cls> ifaceModels = new ArrayList<>();
        while (!ifaces.isEmpty()) {
            String i = ifaces.remove(0);
            if (!seen.add(i)) continue;
            Cls k = cls(i);
            if (k == null) return Set.of();
            ifaceModels.add(k);
            ifaces.addAll(k.interfaces);
        }
        for (Cls k : ifaceModels) {
            for (var m : k.methods.entrySet()) {
                if ((m.getValue() & (Opcodes.ACC_STATIC | Opcodes.ACC_PRIVATE)) != 0 || m.getKey().startsWith("<")) continue;
                if ((m.getValue() & Opcodes.ACC_ABSTRACT) == 0) implemented.add(m.getKey());
            }
        }
        for (Cls k : ifaceModels) {
            for (var m : k.methods.entrySet()) {
                if ((m.getValue() & Opcodes.ACC_ABSTRACT) != 0 && !m.getKey().startsWith("<")) abstracts.add(m.getKey());
            }
        }
        abstracts.removeAll(implemented);
        return abstracts;
    }

    private void checkAbstracts(ClassNode cn) {
        for (String abs : abstractsOf(cn.name, true)) {
            report("ABSTRACT", cn.name, declarer(cn.name, abs) + "." + abs, "concrete class lacks implementation");
        }
    }

    private String declarer(String type, String nameDesc) {
        List<String> todo = new ArrayList<>(List.of(type));
        Set<String> seen = new HashSet<>();
        while (!todo.isEmpty()) {
            String c = todo.remove(0);
            if (!seen.add(c)) continue;
            Cls k = cls(c);
            if (k == null) continue;
            if (!c.equals(type) && k.methods.containsKey(nameDesc)) return c;
            todo.addAll(k.interfaces);
            if (k.superName != null) todo.add(k.superName);
        }
        return type;
    }

    /**
     * An intermediary method_N declared on a mod class can only be an override of
     * vanilla. If no supertype declares it any more, the game never calls it.
     */
    private void checkOrphans(ClassNode cn) {
        Set<String> selfCalled = new HashSet<>();
        for (MethodNode m : cn.methods) {
            if (m.instructions == null) continue;
            for (AbstractInsnNode in : m.instructions) {
                if (in instanceof MethodInsnNode mi && mi.owner.equals(cn.name)) selfCalled.add(mi.name + mi.desc);
            }
        }
        for (MethodNode m : cn.methods) {
            if ((m.access & (Opcodes.ACC_STATIC | Opcodes.ACC_PRIVATE)) != 0) continue;
            if (!INTER_METHOD.matcher(m.name).matches()) continue;
            if (cn.name.startsWith("top/dext/centurybridge/")) continue; // facade API / Statics helpers, not overrides
            if (selfCalled.contains(m.name + m.desc)) continue; // an override hook forwards to it
            Object found = UNKNOWN;
            boolean anyName = false;
            String alt = null;
            if (!hierarchyKnown(cn.name)) continue;
            found = null;
            for (String sup : supertypes(cn.name)) {
                Cls k = cls(sup);
                if (k.methods.containsKey(m.name + m.desc)) { found = k; break; }
                for (String md : k.methods.keySet()) {
                    if (md.startsWith(m.name + "(")) { anyName = true; alt = sup + "." + md; }
                }
            }
            if (found == null) {
                report("ORPHAN", cn.name + "." + m.name + m.desc, cn.name + "." + m.name + m.desc,
                       anyName ? "signature changed: now " + alt : "no supertype declares it");
            }
        }
    }

    private List<String> supertypes(String name) {
        List<String> out = new ArrayList<>();
        List<String> todo = new ArrayList<>();
        Cls self = cls(name);
        if (self.superName != null) todo.add(self.superName);
        todo.addAll(self.interfaces);
        Set<String> seen = new HashSet<>();
        while (!todo.isEmpty()) {
            String c = todo.remove(0);
            if (!seen.add(c)) continue;
            Cls k = cls(c);
            if (k == null) continue;
            out.add(c);
            if (k.superName != null) todo.add(k.superName);
            todo.addAll(k.interfaces);
        }
        return out;
    }

    // ---------------------------------------------------------------- attribution

    private void report(String kind, String where, String ref, String note) {
        if (!reported.add(kind + (delegationSite ? " super " : " ") + ref)) return;
        String owner = ref;
        int dot = ref.indexOf('.');
        if (dot > 0) owner = ref.substring(0, dot);
        String bucket;
        String fate = auditFate.get(auditKey(kind, ref));
        if (delegationSite) {
            // a delegation site is judged by what the converter said about delegation
            // sites; a plain-site fate counts only when it claims nothing (datagen,
            // ledger reasons recorded without the super-ctor spelling)
            String sf = superCtorFate.get(auditKey(kind, ref));
            fate = sf != null ? sf : fate != null && !FIXED_FATES.contains(fate) ? fate : null;
            if (fate != null) note = "super() " + note;
        }
        if (kind.equals("VERIFY") || (kind.equals("ORPHAN") && fate == null)) {
            bucket = "BLIND"; // the audit counts call sites (and orphaned overrides), never method bodies
        } else if (kind.equals("ORPHAN")) {
            bucket = "KNOWN"; // the converter reported this override as orphaned
        } else if (owner.startsWith("top/dext/centurybridge/")) {
            bucket = "CLAIMED"; // the converter pointed the site at a bridge that is not there
        } else if (!platform(owner) || foreignMinecraft(owner)) {
            bucket = "EXTDEP"; // another mod's API, or multi-loader dead code in mojmap names
        } else if (fate == null) {
            bucket = "BLIND";
        } else if (FIXED_FATES.contains(fate)) {
            bucket = "CLAIMED";
        } else {
            bucket = "KNOWN";
        }
        findings.add(new Finding(bucket, kind, curJar, where, ref, (fate != null ? "audit=" + fate + " " : "") + note));
    }

    private Boolean intermediaryTarget;

    /** mojmap-style net/minecraft names cannot exist in an intermediary-era runtime */
    private boolean foreignMinecraft(String owner) {
        if (intermediaryTarget == null) intermediaryTarget = index.containsKey("net/minecraft/class_1297");
        return intermediaryTarget && owner.startsWith("net/minecraft/") && !owner.contains("class_")
               && cls(owner) == null;
    }

    private static String auditKey(String kind, String ref) {
        return ref; // same owner.name(desc) / owner.name:desc spelling as full-audit.tsv
    }

    private void write(Path outDir) throws IOException {
        Files.createDirectories(outDir);
        try (PrintWriter w = new PrintWriter(Files.newBufferedWriter(outDir.resolve("link-findings.tsv")))) {
            w.println("bucket\tkind\tjar\twhere\tref\tnote");
            for (Finding f : findings) w.println(String.join("\t", f.bucket, f.kind, f.jar, f.where, f.ref, f.note));
        }
        // per (bucket, kind, ref): how many jars hit it
        Map<String, Set<String>> byRef = new TreeMap<>();
        Map<String, Integer> counts = new TreeMap<>();
        Map<String, Set<String>> jarsByBucket = new TreeMap<>();
        for (Finding f : findings) {
            byRef.computeIfAbsent(f.bucket + "\t" + f.kind + "\t" + f.ref, k -> new HashSet<>()).add(f.jar);
            counts.merge(f.bucket + " " + f.kind, 1, Integer::sum);
            jarsByBucket.computeIfAbsent(f.bucket, k -> new HashSet<>()).add(f.jar);
        }
        try (PrintWriter w = new PrintWriter(Files.newBufferedWriter(outDir.resolve("link-refs.tsv")))) {
            w.println("jars\tbucket\tkind\tref");
            byRef.entrySet().stream()
                .sorted((a, b) -> b.getValue().size() - a.getValue().size())
                .forEach(e -> w.println(e.getValue().size() + "\t" + e.getKey()));
        }
        int clean = 0;
        Set<String> dirty = new HashSet<>();
        for (Finding f : findings) if (!f.bucket.equals("EXTDEP") && !f.bucket.equals("KNOWN")) dirty.add(f.jar);
        for (String j : subjects.keySet()) if (!dirty.contains(j)) clean++;
        System.out.println("link-check: " + subjects.size() + " subject jars, " + index.size() + " classes indexed, "
                           + mixins.size() + " mixins folded");
        counts.forEach((k, v) -> System.out.println("  " + k + ": " + v + " (distinct per jar)"));
        jarsByBucket.forEach((k, v) -> System.out.println("  jars with " + k + ": " + v.size()));
        System.out.println("  jars with no CLAIMED/BLIND finding: " + clean + "/" + subjects.size());
        System.out.println("link-check -> " + outDir.resolve("link-findings.tsv") + ", link-refs.tsv");
    }
}
