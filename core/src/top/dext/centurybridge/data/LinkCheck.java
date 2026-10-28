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
 *   ACCESS     class or member the caller may not reach IllegalAccessError
 *              (private/package/protected, JVM rules
 *              incl. nestmates), or a final field
 *              written from outside its class
 *   FINAL      extends a final class / overrides an     the class fails to load
 *              accessible final method
 *   SEALED     extends/implements a sealed type that    the class fails to load
 *              does not permit it
 *   AWFILE     an access widener the loader rejects     the game does not boot
 *              (format, namespace, missing file)
 *   TOMBSTONE  a call into the converter's lazy-fail    UnsupportedOperationException
 *              stubs (centurybridge/gen/Tombstones*):   when reached (always KNOWN:
 *              the dead reference it replaced           it links, and it is open)
 *
 * Access is judged through the access wideners the game would load with the mod: every
 * classpath mod's (Fabric API's modules, CenturyBridge's) plus the subject's own (and its
 * jar-in-jar mods'), applied as the loader applies them (AwRules).
 *
 * Each finding is then joined against the engine's full-audit.tsv (optional):
 *   CLAIMED  engine recorded the symbol as fixed (covered/redirect/rename/hook)
 *            or the dangling ref points into CenturyBridge's own classes
 *   KNOWN    engine reported it (ISSUE/LEDGERED/TOMBSTONE/...)
 *   BLIND    engine never flagged it
 *   EXTDEP   owner outside the platform (another mod's API missing) -- not ours
 *   DATAGEN  the site is data-generation code (a DataProvider, the fabric-datagen
 *            entrypoint, a "datagen" package): it only runs when someone runs datagen,
 *            never in play -- reported, not counted against the jar
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
        int access;                                           // as read; access wideners are applied per check
        final Map<String, Integer> methods = new HashMap<>(); // name+desc -> access
        final Map<String, Integer> fields = new HashMap<>();  // name+":"+desc -> access
        String origin;
        String nestHost;
        Set<String> permitted;                                // non-null: sealed
        String canonicalCtor;                                 // a record's canonical constructor desc
        Cls(String name) { this.name = name; }
        boolean isInterface() { return (access & Opcodes.ACC_INTERFACE) != 0; }
    }

    /** a resolved member: the class that declares it and its flags as read */
    record Member(String owner, String name, String desc, int access) {}

    /** an access widener some loaded mod declares (fabric.mod.json "accessWidener") */
    record AwFile(String origin, String subjectTag, String modId, String path, String text) {}

    private final Map<String, Cls> index = new HashMap<>();
    private final List<AwFile> awFiles = new ArrayList<>();
    private final Set<String> mixinNames = new HashSet<>();
    private final Map<String, ClassNode> mixinNodes = new HashMap<>();
    /** the runtime's mapping namespace, as an access widener header must name it */
    private String runtimeNamespace;
    /** access wideners in force for the subject being checked: the runtime's + its own */
    private AwRules rules = new AwRules();
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
        lc.injectInterfaces();
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
            String[] f = line.split("\t", 4);
            if (f.length < 3 || f[0].equals("count")) continue;
            // past the wall the converter adds the symbol as the converted jar spells it
            if (f.length == 4) {
                loadAuditRow(f[1], f[3]);
            }
            loadAuditRow(f[1], f[2]);
        }
    }

    private void loadAuditRow(String rawFate, String symbol) {
        // the converter spells a delegation site's fate apart: SUPER-CTOR-ISSUE[..],
        // super-ctor-reshape / -adapt, "LEDGERED[side] super-ctor: reason"
        boolean superSite = rawFate.startsWith("SUPER-CTOR-ISSUE") || rawFate.startsWith("super-ctor-")
            || rawFate.matches("LEDGERED\\[[^\\]]*\\] super-ctor:.*");
        String fate = rawFate;
        int b = fate.indexOf('[');
        if (b > 0) fate = fate.substring(0, b);
        // several fates per symbol are possible (different sites); a KNOWN one wins
        // over a FIXED one only if nothing claimed it fixed -- keep the most "claimed"
        Map<String, String> into = superSite ? superCtorFate : auditFate;
        String prev = into.get(symbol);
        if (prev == null || FIXED_FATES.contains(fate)) into.put(symbol, fate);
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
        byte[] modJson = null;
        Map<String, byte[]> texts = new HashMap<>(); // candidates for the access widener fabric.mod.json names
        while ((e = zin.getNextEntry()) != null) {
            String n = e.getName();
            if (n.endsWith(".jar")) {
                byte[] b = zin.readAllBytes();
                loadZip(new ZipInputStream(new ByteArrayInputStream(b)), subjectTag, origin + "!" + n);
            } else if (n.equals("fabric.mod.json")) {
                modJson = zin.readAllBytes();
            } else if (n.endsWith(".json") && !n.contains("/")) {
                collectMixinConfig(zin.readAllBytes());
            } else if (!n.endsWith("/") && !n.endsWith(".class") && !ASSET.matcher(n).find()) {
                byte[] b = zin.readAllBytes();
                if (b.length < (1 << 20)) texts.put(n, b);
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
                if (isMixin(cn)) { mixins.add(cn); mixinNames.add(cn.name); mixinNodes.put(cn.name, cn); }
                if (subjectTag != null) subjects.computeIfAbsent(subjectTag, k -> new ArrayList<>()).add(cn);
            }
        }
        if (modJson != null) collectModMetadata(modJson, texts, subjectTag, origin);
    }

    private static final Pattern ASSET = Pattern.compile(
        "\\.(png|ogg|json|nbt|mcmeta|wav|ttf|otf|bin|jpg|jpeg|gif|psd|bbmodel|obj|mtl|fsh|vsh|glsl|lang|properties|md|MF)$");

    /** classes the mods name as their fabric-datagen entrypoint */
    private final Set<String> datagenEntry = new HashSet<>();
    private final Map<String, Boolean> datagenCache = new HashMap<>();
    private static final Pattern DATAGEN_PACKAGE = Pattern.compile("(?i)datagen");

    /**
     * Code that only runs when data generation runs: a DataProvider (vanilla's or Fabric
     * API's provider types), the fabric-datagen entrypoint, a class in a "datagen" package --
     * or nested in one of those (lambdas and inner classes of a provider).
     */
    private boolean datagenOnly(String where) {
        String c = where;
        int dot = c.indexOf('.');
        if (dot > 0) c = c.substring(0, dot);
        while (true) {
            if (datagenType(c)) return true;
            int d = c.lastIndexOf('$');
            if (d < 0) return false;
            c = c.substring(0, d);
        }
    }

    private boolean datagenType(String c) {
        Boolean hit = datagenCache.get(c);
        if (hit != null) return hit;
        boolean r = datagenEntry.contains(c) || DATAGEN_PACKAGE.matcher(c).find();
        if (!r && cls(c) != null) {
            for (String sup : supertypes(c)) {
                if (sup.equals("net/minecraft/data/DataProvider") || sup.equals("net/minecraft/class_2405")
                        || sup.startsWith("net/fabricmc/fabric/api/datagen/")) {
                    r = true;
                    break;
                }
            }
        }
        datagenCache.put(c, r);
        return r;
    }

    /** mod ids the classpath brings (Fabric API's modules, CenturyBridge, the loader's builtins) */
    private final Set<String> runtimeIds = new HashSet<>(Set.of("minecraft", "java", "fabricloader"));
    /** subject tag -> the mod ids it brings (its own, its jar-in-jar mods', their "provides") */
    private final Map<String, Set<String>> provides = new HashMap<>();
    /** mod id -> the mod ids it declares in "depends" */
    private final Map<String, Set<String>> modDepends = new HashMap<>();

    /** fabric.mod.json: what the mod is, what it needs loaded with it, its access widener */
    private void collectModMetadata(byte[] modJson, Map<String, byte[]> texts, String subjectTag, String origin) {
        String id;
        String path = null;
        try {
            var r = new com.google.gson.stream.JsonReader(new java.io.StringReader(
                new String(modJson, java.nio.charset.StandardCharsets.UTF_8)));
            r.setLenient(true);
            var o = com.google.gson.JsonParser.parseReader(r).getAsJsonObject();
            id = o.has("id") ? o.get("id").getAsString() : origin;
            Set<String> ids = new HashSet<>(Set.of(id));
            if (o.has("provides") && o.get("provides").isJsonArray())
                for (var el : o.getAsJsonArray("provides")) ids.add(el.getAsString());
            if (subjectTag == null) runtimeIds.addAll(ids);
            else provides.computeIfAbsent(subjectTag, k -> new HashSet<>()).addAll(ids);
            if (o.has("depends") && o.get("depends").isJsonObject())
                modDepends.computeIfAbsent(id, k -> new HashSet<>()).addAll(o.getAsJsonObject("depends").keySet());
            if (o.has("entrypoints") && o.get("entrypoints").isJsonObject()
                    && o.getAsJsonObject("entrypoints").has("fabric-datagen")
                    && o.getAsJsonObject("entrypoints").get("fabric-datagen").isJsonArray()) {
                for (var el : o.getAsJsonObject("entrypoints").getAsJsonArray("fabric-datagen")) {
                    String v = el.isJsonObject() && el.getAsJsonObject().has("value")
                        ? el.getAsJsonObject().get("value").getAsString() : el.isJsonPrimitive() ? el.getAsString() : null;
                    if (v != null) datagenEntry.add((v.contains("::") ? v.substring(0, v.indexOf("::")) : v).replace('.', '/'));
                }
            }
            if (o.has("accessWidener") && o.get("accessWidener").isJsonPrimitive()) path = o.get("accessWidener").getAsString();
        } catch (RuntimeException ex) {
            return; // not a fabric.mod.json the loader reads either
        }
        if (path == null) return;
        // calibration control: -Dcb.linkcheck.skipAw=<substring> leaves out the access wideners of
        // matching jars (e.g. "centurybridge"), to measure what they give back
        String skip = System.getProperty("cb.linkcheck.skipAw");
        if (skip != null && !skip.isEmpty() && origin.contains(skip)) return;
        byte[] b = texts.get(path.startsWith("/") ? path.substring(1) : path);
        awFiles.add(new AwFile(origin, subjectTag, id, path,
            b == null ? null : new String(b, java.nio.charset.StandardCharsets.UTF_8)));
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
        for (FieldNode f : cn.fields) c.fields.put(f.name + ":" + f.desc, f.access);
        c.nestHost = cn.nestHostClass != null ? cn.nestHostClass : cn.name;
        if (cn.permittedSubclasses != null && !cn.permittedSubclasses.isEmpty()) c.permitted = new HashSet<>(cn.permittedSubclasses);
        if (cn.recordComponents != null) {
            StringBuilder d = new StringBuilder("(");
            for (var rc : cn.recordComponents) d.append(rc.descriptor);
            c.canonicalCtor = d.append(")V").toString();
        }
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
                    tc.fields.putIfAbsent(f.name + ":" + f.desc, f.access);
                }
            }
        }
    }

    // ---------------------------------------------------------------- access wideners

    /** the runtime's access wideners: one per classpath mod id, the first seen (as for classes) */
    private final List<AwFile> runtimeAws = new ArrayList<>();
    private final Map<String, List<AwFile>> subjectAws = new HashMap<>();

    /**
     * Interface injection is structural (it changes what resolves where), so it goes into
     * the index once; access flags stay as read and are judged per subject (rules).
     */
    private void injectInterfaces() {
        if (index.containsKey("net/minecraft/class_1297")) runtimeNamespace = "intermediary";
        else if (index.containsKey("net/minecraft/world/entity/Entity")) runtimeNamespace = "official";
        Set<String> runtimeIds = new HashSet<>();
        for (AwFile f : awFiles) {
            if (f.subjectTag() == null && runtimeIds.add(f.modId())) runtimeAws.add(f);
        }
        Map<String, Set<String>> subjectIds = new HashMap<>();
        for (AwFile f : awFiles) {
            if (f.subjectTag() == null || runtimeIds.contains(f.modId())) continue; // the runtime's copy of that mod wins
            if (subjectIds.computeIfAbsent(f.subjectTag(), k -> new HashSet<>()).add(f.modId()))
                subjectAws.computeIfAbsent(f.subjectTag(), k -> new ArrayList<>()).add(f);
        }
        AwRules all = new AwRules();
        for (AwFile f : runtimeAws) {
            String err = f.text() == null ? "Missing classTweaker file " + f.path() + " from mod " + f.modId()
                : all.read(AwRules.lines(f.text()), runtimeNamespace);
            if (err != null) {
                String bucket = f.origin().contains("centurybridge") ? "CLAIMED" : "EXTDEP";
                findings.add(new Finding(bucket, "AWFILE", f.origin(), f.modId(), f.path(), err));
            }
        }
        for (List<AwFile> l : subjectAws.values()) {
            for (AwFile f : l) if (f.text() != null) all.read(AwRules.lines(f.text()), runtimeNamespace);
        }
        int n = 0;
        for (Cls c : index.values()) {
            Set<String> add = all.injectedInterfaces(c.name);
            if (!add.isEmpty() && c.interfaces.addAll(add)) n++;
        }
        System.out.println("access wideners: " + runtimeAws.size() + " runtime, "
            + subjectAws.values().stream().mapToInt(List::size).sum() + " from subjects; namespace " + runtimeNamespace
            + "; interfaces injected into " + n + " classes");
    }

    /** the subject's declared dependencies (transitively) that nothing in this run provides */
    private Set<String> absentDeps = Set.of();
    /** the text of the subject's own access wideners: did it ever name a member */
    private String ownAwText = "";

    /**
     * What the game loads with this subject: the runtime's access wideners, the subject's
     * own (with its jar-in-jar mods'), and those of the mods it declares as dependencies --
     * when the run has them; the rest are remembered (absentDeps), since their access
     * wideners may be what the subject relies on.
     */
    private AwRules rulesFor(String subjectTag) {
        Set<String> withTags = new java.util.LinkedHashSet<>(List.of(subjectTag));
        Set<String> absent = new java.util.TreeSet<>();
        Set<String> have = new HashSet<>(provides.getOrDefault(subjectTag, Set.of()));
        List<String> todo = new ArrayList<>(have);
        Set<String> seen = new HashSet<>();
        while (!todo.isEmpty()) {
            String id = todo.remove(todo.size() - 1);
            if (!seen.add(id)) continue;
            for (String dep : modDepends.getOrDefault(id, Set.of())) {
                if (runtimeIds.contains(dep) || have.contains(dep)) {
                    todo.add(dep);
                    continue;
                }
                String provider = null;
                for (var e : provides.entrySet()) if (e.getValue().contains(dep)) { provider = e.getKey(); break; }
                if (provider == null) { absent.add(dep); continue; }
                withTags.add(provider);
                have.addAll(provides.get(provider));
                todo.addAll(provides.get(provider));
            }
        }
        absentDeps = absent;
        AwRules r = new AwRules();
        for (AwFile f : runtimeAws) if (f.text() != null) r.read(AwRules.lines(f.text()), runtimeNamespace);
        StringBuilder own = new StringBuilder();
        for (String tag : withTags) {
            for (AwFile f : subjectAws.getOrDefault(tag, List.of())) {
                String err = f.text() == null ? "Missing classTweaker file " + f.path() + " from mod " + f.modId()
                    : r.read(AwRules.lines(f.text()), runtimeNamespace);
                if (!tag.equals(subjectTag)) continue; // a dependency's own findings are its own
                if (f.text() != null) own.append(f.text()).append('\n');
                if (err != null) findings.add(new Finding("BLIND", "AWFILE", subjectTag, f.modId(), f.path(), err));
            }
        }
        ownAwText = own.toString();
        return r;
    }

    private int classAcc(Cls c) {
        return rules.classAccess(c.name, c.access);
    }

    /** a member's flags with the access wideners in force */
    private int memberAcc(Member m) {
        Cls o = cls(m.owner());
        int ownerAcc = o == null ? 0 : o.access;
        if (m.desc().startsWith("(")) {
            int a = m.access();
            if (o != null && m.name().equals("<init>") && m.desc().equals(o.canonicalCtor)) {
                a = rules.canonicalConstructorAccess(m.owner(), a);
            }
            return rules.methodAccess(m.owner(), m.name(), m.desc(), a, ownerAcc);
        }
        return rules.fieldAccess(m.owner(), m.name(), m.desc(), m.access(), ownerAcc);
    }

    private static String pkg(String name) {
        int i = name.lastIndexOf('/');
        return i < 0 ? "" : name.substring(0, i);
    }

    private boolean isSubclass(String sub, String sup) {
        for (String c = sub; c != null; ) {
            if (c.equals(sup)) return true;
            Cls k = cls(c);
            c = k == null ? null : k.superName;
        }
        return false;
    }

    private String nestHost(String name) {
        Cls c = cls(name);
        return c == null || c.nestHost == null ? name : c.nestHost;
    }

    /**
     * JVMS 5.4.4 as HotSpot's Reflection::verify_member_access applies it: public; the same
     * class; protected to a subclass (an instance member only through a reference typed as
     * the caller's own hierarchy); not private within the package; private within the nest.
     */
    private boolean memberAccessible(String from, String refOwner, String declOwner, int acc) {
        if ((acc & Opcodes.ACC_PUBLIC) != 0 || from.equals(declOwner)) return true;
        if ((acc & Opcodes.ACC_PROTECTED) != 0) {
            Cls d = cls(from);
            if (d != null && !d.isInterface() && isSubclass(from, declOwner)
                    && ((acc & Opcodes.ACC_STATIC) != 0 || from.equals(refOwner) || declOwner.equals(refOwner)
                        || isSubclass(refOwner, from) || isSubclass(from, refOwner))) {
                return true;
            }
        }
        if ((acc & Opcodes.ACC_PRIVATE) == 0) return samePackage(from, declOwner);
        return nestHost(from).equals(nestHost(declOwner));
    }

    /**
     * JVMS 5.3: same run-time package. A mixin's inner class runs as Mixin's copy of it, one per target, in
     * that target's package (mixinOuter): its own hierarchy, its target's package -- every copy must reach.
     */
    private boolean samePackage(String from, String other) {
        String outer = mixinOuter(from);
        if (outer != null) {
            List<String> targets = mixinTargets(mixinNodes.get(outer)).stream().filter(t -> cls(t) != null).toList();
            if (!targets.isEmpty()) {
                for (String t : targets) if (!pkg(t).equals(pkg(other))) return false;
                return true;
            }
        }
        return pkg(from).equals(pkg(other));
    }

    private static String describe(int acc) {
        if ((acc & Opcodes.ACC_PRIVATE) != 0) return "private";
        if ((acc & Opcodes.ACC_PROTECTED) != 0) return "protected";
        return (acc & Opcodes.ACC_PUBLIC) != 0 ? "public" : "package-private";
    }

    // ---------------------------------------------------------------- resolution

    /** null = resolved; "?" = hierarchy incomplete (a missing class, reported separately) */
    private static final Object UNKNOWN = new Object();

    /**
     * JVM method resolution: the class chain (any access -- a private match is found and
     * then fails the access check), then the superinterfaces, where private and static
     * methods do not count (JVMS 5.4.3.3/5.4.3.4). Returns a Member, null or UNKNOWN.
     */
    private Object findMethod(String owner, String nameDesc) {
        Set<String> seenIfaces = new HashSet<>();
        List<String> ifaces = new ArrayList<>();
        boolean incomplete = false;
        String name = nameDesc.substring(0, nameDesc.indexOf('('));
        String desc = nameDesc.substring(name.length());
        if (name.equals("<init>") || name.equals("<clinit>")) {
            // constructors are not inherited: invokespecial <init> must name a class that declares
            // that exact descriptor (JVMS 6.5), whatever its superclasses have -- Object's ()V included
            Cls k = cls(owner);
            if (k == null) return UNKNOWN;
            Integer acc = k.methods.get(nameDesc);
            return acc != null ? new Member(owner, name, desc, acc) : null;
        }
        for (String c = owner; c != null; ) {
            Cls k = cls(c);
            if (k == null) { incomplete = true; break; }
            Integer acc = k.methods.get(nameDesc);
            if (acc != null) return new Member(c, name, desc, acc);
            ifaces.addAll(k.interfaces);
            c = k.superName;
        }
        while (!ifaces.isEmpty()) {
            String i = ifaces.remove(ifaces.size() - 1);
            if (!seenIfaces.add(i)) continue;
            Cls k = cls(i);
            if (k == null) { incomplete = true; continue; }
            Integer acc = k.methods.get(nameDesc);
            if (acc != null && (acc & (Opcodes.ACC_PRIVATE | Opcodes.ACC_STATIC)) == 0) return new Member(i, name, desc, acc);
            ifaces.addAll(k.interfaces);
        }
        if (!owner.equals("java/lang/Object")) {
            Cls obj = cls("java/lang/Object");
            if (obj != null && obj.methods.containsKey(nameDesc))
                return new Member("java/lang/Object", name, desc, obj.methods.get(nameDesc));
        }
        return incomplete ? UNKNOWN : null;
    }

    private Object findField(String owner, String name, String desc) {
        boolean[] incomplete = {false};
        Member m = lookupField(owner, name, desc, new HashSet<>(), incomplete);
        return m != null ? m : incomplete[0] ? UNKNOWN : null;
    }

    /** JVMS 5.4.3.2: the class itself, then its direct superinterfaces (recursively), then its superclass */
    private Member lookupField(String c, String name, String desc, Set<String> seen, boolean[] incomplete) {
        if (!seen.add(c)) return null;
        Cls k = cls(c);
        if (k == null) { incomplete[0] = true; return null; }
        Integer acc = k.fields.get(name + ":" + desc);
        if (acc != null) return new Member(c, name, desc, acc);
        for (String i : k.interfaces) {
            Member m = lookupField(i, name, desc, seen, incomplete);
            if (m != null) return m;
        }
        return k.superName == null ? null : lookupField(k.superName, name, desc, seen, incomplete);
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
    /** the classes the code being checked runs as: the class itself, or a mixin's targets; null = not judged */
    private List<String> curCallers;
    private String curMethod;
    private int curVersion;
    /** the instruction being checked is a constructor's super(...)/this(...) call */
    private boolean delegationSite;

    private void checkAll() {
        for (var e : subjects.entrySet()) {
            curJar = e.getKey();
            reported = new HashSet<>();
            rules = rulesFor(curJar);
            for (ClassNode cn : e.getValue()) {
                if (cn.name.startsWith("centurybridge/gen/Tombstones")) continue; // lazy-fail by design
                if (!live(cn)) continue; // stripped by triage: never applied
                checkClass(cn);
            }
        }
    }

    private void checkClass(ClassNode cn) {
        boolean mixin = isMixin(cn);
        List<String> runsAs = List.of(cn.name);
        if (mixin) {
            runsAs = mixinTargets(cn).stream().filter(t -> cls(t) != null).toList();
            if (runsAs.isEmpty()) runsAs = null; // no target to judge access from
        }
        curVersion = cn.version & 0xFFFF;
        curCallers = mixin ? null : runsAs; // a mixin's own extends/implements are never loaded as such
        if (cn.superName != null) needClass(cn.superName, cn.name, "extends");
        for (String i : cn.interfaces) needClass(i, cn.name, "implements");
        curCallers = runsAs;
        for (MethodNode m : cn.methods) {
            String where = cn.name + "." + m.name + m.desc;
            curMethod = m.name;
            if (m.instructions != null) {
                Map<String, Integer> pendingNew = new HashMap<>();
                for (AbstractInsnNode in : m.instructions) {
                    delegationSite = isDelegation(in, pendingNew);
                    checkInsn(in, where);
                }
                delegationSite = false;
            }
        }
        curCallers = null;
        if (mixin) return; // members of mixins are MixinCheck's job
        checkInheritance(cn);
        checkTypes(cn);
        if ((cn.access & (Opcodes.ACC_ABSTRACT | Opcodes.ACC_INTERFACE)) == 0) checkAbstracts(cn);
        checkOrphans(cn);
    }

    /**
     * What makes the class itself fail to load: extending a final class, extending or
     * implementing a sealed type that does not permit it, overriding an accessible final
     * method (HotSpot's ClassFileParser::check_final_method_override) -- with the access
     * wideners in force, which is how a bridge gives these back (extendable).
     */
    private void checkInheritance(ClassNode cn) {
        Cls sup = cn.superName == null ? null : cls(cn.superName);
        if (sup != null && (classAcc(sup) & Opcodes.ACC_FINAL) != 0)
            report("FINAL", cn.name, cn.superName, "cannot inherit from final class");
        List<String> direct = new ArrayList<>();
        if (cn.superName != null) direct.add(cn.superName);
        direct.addAll(cn.interfaces);
        for (String t : direct) {
            Cls k = cls(t);
            if (k != null && k.permitted != null && !rules.unsealed(t) && !k.permitted.contains(cn.name))
                report("SEALED", cn.name, t, "not a permitted subclass");
        }
        for (MethodNode m : cn.methods) {
            if ((m.access & (Opcodes.ACC_PRIVATE | Opcodes.ACC_STATIC)) != 0 || m.name.startsWith("<")) continue;
            String nd = m.name + m.desc;
            for (String c = cn.superName; c != null; ) {
                Member sm = declaredUp(c, nd);
                if (sm == null) break;
                int a = memberAcc(sm);
                if ((a & Opcodes.ACC_FINAL) != 0 && (a & (Opcodes.ACC_STATIC | Opcodes.ACC_PRIVATE)) == 0
                        && memberAccessible(cn.name, sm.owner(), sm.owner(), a)) {
                    report("FINAL", cn.name + "." + nd, sm.owner() + "." + nd, "overrides final method");
                    break;
                }
                Cls holder = cls(sm.owner());
                c = holder == null ? null : holder.superName;
            }
        }
    }

    /** the first class from {@code c} up the superclass chain that declares the method */
    private Member declaredUp(String c, String nameDesc) {
        String name = nameDesc.substring(0, nameDesc.indexOf('('));
        for (; c != null; ) {
            Cls k = cls(c);
            if (k == null) return null;
            Integer acc = k.methods.get(nameDesc);
            if (acc != null) return new Member(c, name, nameDesc.substring(name.length()), acc);
            c = k.superName;
        }
        return null;
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
        if (!reported.add("TYPE " + exp + (PER_SITE ? " @" + where : ""))) return;
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
        Cls c = cls(name);
        if (c == null) {
            report("CLASS", where, name, how);
            return false;
        }
        // JVMS 5.4.4: a class is accessible if public or in the caller's run-time package
        if (curCallers != null && !mixinNames.contains(name) && mixinOuter(name) == null && (classAcc(c) & Opcodes.ACC_PUBLIC) == 0) {
            for (String d : curCallers) {
                if (!samePackage(d, name)) {
                    report("ACCESS", where, name, "class not public, caller " + d + " (" + how + ")");
                    break;
                }
            }
        }
        return true;
    }

    /** IllegalAccessError for a resolved member the code may not reach */
    private void checkAccess(String refOwner, Member m, String where) {
        if (curCallers == null || mixinNames.contains(m.owner()) || mixinNames.contains(refOwner)
                || mixinOuter(m.owner()) != null || mixinOuter(refOwner) != null) return;
        int acc = memberAcc(m);
        for (String d : curCallers) {
            if (!memberAccessible(d, refOwner, m.owner(), acc)) {
                String ref = refOwner + "." + m.name() + (m.desc().startsWith("(") ? "" : ":") + m.desc();
                report("ACCESS", where, ref, describe(acc) + " in " + m.owner() + ", caller " + d);
                return;
            }
        }
    }

    private void checkMethodRef(String owner, String name, String desc, int op, boolean itf, String where) {
        if (owner.startsWith("[")) return; // array clone() etc.
        if ((owner.equals("java/lang/invoke/MethodHandle") || owner.equals("java/lang/invoke/VarHandle"))) return;
        if (owner.startsWith("centurybridge/gen/Tombstones")) {
            // a dead reference the converter swapped for a lazy-fail stub: it links, and it throws
            // the moment it is reached -- open, not bridged
            report("TOMBSTONE", where, owner + "." + name + desc, "throws when reached");
            return;
        }
        if (!needClass(owner, where, "owner")) return;
        Object res = findMethod(owner, name + desc);
        if (res == UNKNOWN) return;
        if (res == null) {
            report("METHOD", where, owner + "." + name + desc, "");
            return;
        }
        Member mem = (Member) res;
        checkAccess(owner, mem, where);
        int a = mem.access();
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
        Object res = findField(owner, name, desc);
        if (res == UNKNOWN) return;
        if (res == null) {
            report("FIELD", where, owner + "." + name + ":" + desc, "");
            return;
        }
        Member mem = (Member) res;
        checkAccess(owner, mem, where);
        boolean isStatic = (mem.access() & Opcodes.ACC_STATIC) != 0;
        boolean wantStatic = op == Opcodes.GETSTATIC || op == Opcodes.PUTSTATIC;
        if (isStatic != wantStatic) report("ICCE", where, owner + "." + name + ":" + desc, wantStatic ? "static access to instance field" : "instance access to static field");
        if ((op == Opcodes.PUTFIELD || op == Opcodes.PUTSTATIC) && (memberAcc(mem) & Opcodes.ACC_FINAL) != 0)
            checkFinalWrite(owner, mem, op, where);
    }

    /**
     * putfield/putstatic to a final field: only its own class may, and (class files of Java
     * 9 and later) only in its instance / static initializer -- else IllegalAccessError.
     */
    private void checkFinalWrite(String owner, Member mem, int op, String where) {
        if (curCallers == null || mixinNames.contains(mem.owner()) || mixinNames.contains(owner)) return;
        String init = op == Opcodes.PUTFIELD ? "<init>" : "<clinit>";
        for (String d : curCallers) {
            if (!d.equals(mem.owner()) || (curVersion >= 53 && !init.equals(curMethod))) {
                report("ACCESS", where, owner + "." + mem.name() + ":" + mem.desc(),
                       "final field written " + (d.equals(mem.owner()) ? "outside " + init : "from " + d));
                return;
            }
        }
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
            boolean anyName = false;
            String alt = null;
            String blocked = null;
            if (!hierarchyKnown(cn.name)) continue;
            Object found = null;
            for (String sup : supertypes(cn.name)) {
                Cls k = cls(sup);
                Integer acc = k.methods.get(m.name + m.desc);
                if (acc != null) {
                    // only a method the class can override is overridden (JVMS 5.4.5)
                    int a = memberAcc(new Member(sup, m.name, m.desc, acc));
                    boolean overridable = (a & (Opcodes.ACC_PRIVATE | Opcodes.ACC_STATIC)) == 0
                        && ((a & (Opcodes.ACC_PUBLIC | Opcodes.ACC_PROTECTED)) != 0 || pkg(sup).equals(pkg(cn.name)));
                    if (overridable) { found = k; break; }
                    blocked = sup + " declares it " + ((a & Opcodes.ACC_STATIC) != 0 ? "static" : describe(a));
                    continue;
                }
                for (String md : k.methods.keySet()) {
                    if (md.startsWith(m.name + "(")) { anyName = true; alt = sup + "." + md; }
                }
            }
            if (found == null) {
                report("ORPHAN", cn.name + "." + m.name + m.desc, cn.name + "." + m.name + m.desc,
                       blocked != null ? "not overridable: " + blocked
                       : anyName ? "signature changed: now " + alt : "no supertype declares it");
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

    /**
     * -Dcb.linkcheck.perSite=true: every site of a broken reference is its own finding, not just
     * the first one per jar. A mod jar is counted by its distinct broken symbols; the API probe
     * is one jar holding every usage of every member -- a call, the super call inside an
     * override, a protected call from a subclass of the same member all share one reference, and
     * per-jar deduplication would report the first and hide the rest.
     */
    private static final boolean PER_SITE = Boolean.getBoolean("cb.linkcheck.perSite");

    private void report(String kind, String where, String ref, String note) {
        if (!reported.add(kind + (delegationSite ? " super " : " ") + ref + (PER_SITE ? " @" + where : ""))) return;
        String owner = ref;
        int dot = ref.indexOf('.');
        if (dot > 0) owner = ref.substring(0, dot);
        String bucket;
        if (kind.equals("TOMBSTONE")) {
            findings.add(new Finding("KNOWN", kind, curJar, where, ref, note));
            return;
        }
        String fate = auditFate.get(auditKey(kind, ref));
        if (dot > 0 && !kind.equals("ORPHAN") && !kind.equals("VERIFY")
                && !platform(owner) && !owner.startsWith("top/dext/centurybridge/")
                && inheritedThrough(owner, ref.substring(dot + 1))) {
            // a member reached through a mod class (ExtendedButton.field_22757, a GameData map's IdMapper field)
            // that the class does not declare is inherited: the JVM resolves it up the class's own hierarchy, so
            // the member is the platform ancestor's, not another mod's API -- judged as that class's
            String home = platformHome(owner);
            if (home != null) {
                String homeRef = home + ref.substring(dot);
                if (fate == null) fate = auditFate.get(auditKey(kind, homeRef));
                note = "through " + owner + " " + note;
                owner = home;
            }
        }
        if (delegationSite) {
            // a delegation site is judged by what the converter said about delegation
            // sites; a plain-site fate counts only when it claims nothing (datagen,
            // ledger reasons recorded without the super-ctor spelling)
            String sf = superCtorFate.get(auditKey(kind, ref));
            fate = sf != null ? sf : fate != null && !FIXED_FATES.contains(fate) ? fate : null;
            if (fate != null) note = "super() " + note;
        }
        if (datagenOnly(where)) {
            bucket = "DATAGEN"; // never executes in play; whatever it references is moot there
        } else if (ACCESS_KINDS.contains(kind) && platform(owner) && !owner.startsWith("top/dext/centurybridge/")
                && !absentDeps.isEmpty() && !ownAwText.contains(memberName(ref))) {
            // a vanilla member only an access widener ever opened, and the subject's own never
            // named it: what it relies on is a dependency this run does not have
            bucket = "EXTDEP";
            note = "needs " + String.join(",", absentDeps) + " (absent here; its access widener may open it) " + note;
        } else if (kind.equals("VERIFY") || (kind.equals("ORPHAN") && fate == null)) {
            bucket = "BLIND"; // the audit counts call sites (and orphaned overrides), never method bodies
        } else if (kind.equals("ORPHAN")) {
            bucket = "KNOWN"; // the converter reported this override as orphaned
        } else if (owner.startsWith("top/dext/centurybridge/")) {
            bucket = "CLAIMED"; // the converter pointed the site at a bridge that is not there
        } else if (dot > 0 && subjectOwns(owner) && namesCbType(ref.substring(dot + 1))) {
            // the subject's own member, named with a CenturyBridge type the mod never wrote (an accessor whose
            // descriptor the converter moved onto a facade): the converter's rewrite left the site dangling
            bucket = "CLAIMED";
            note = "descriptor names a CenturyBridge type " + note;
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

    private static final Set<String> ACCESS_KINDS = Set.of("ACCESS", "FINAL", "SEALED");

    /**
     * The nearest platform class above a mod class (its superclasses first, then its interfaces), through
     * the classes this run has: where a member the mod class does not declare is inherited from. Null when
     * the walk leaves the run (an absent dependency's class: then it is that mod's API) or reaches only the
     * JDK (a mod-internal hierarchy).
     */
    private String platformHome(String owner) {
        // the superclass chain first: a class's members are its superclasses' before its interfaces'
        Set<String> seen = new HashSet<>();
        List<String> itfs = new ArrayList<>();
        for (String c = owner; c != null && seen.add(c); ) {
            if (!c.equals(owner) && platform(c)) {
                if (!jdk(c)) return c;
                break;
            }
            Cls k = cls(c);
            if (k == null) return null; // a superclass this run lacks: the member may be that dependency's
            itfs.addAll(k.interfaces);
            c = k.superName;
        }
        List<String> todo = new ArrayList<>(itfs);
        while (!todo.isEmpty()) {
            String c = todo.remove(0);
            if (!seen.add(c)) continue;
            if (platform(c)) {
                if (!jdk(c)) return c;
                continue;
            }
            Cls k = cls(c);
            if (k == null) continue;
            todo.addAll(k.interfaces);
        }
        return null;
    }

    /**
     * the member ("name(desc)" / "name:desc") is one the class this run has does not declare itself, so the
     * reference reaches it by inheritance (a constructor never is inherited)
     */
    private boolean inheritedThrough(String owner, String member) {
        if (member.startsWith("<")) return false;
        Cls k = cls(owner);
        return k != null && !k.methods.containsKey(member) && !k.fields.containsKey(member);
    }

    private static boolean jdk(String c) {
        return c.startsWith("java/") || c.startsWith("javax/") || c.startsWith("jdk/") || c.startsWith("sun/");
    }

    /** a member spelling ("name(desc)" / "name:desc") whose descriptor names a CenturyBridge class */
    private static boolean namesCbType(String member) {
        int d = member.indexOf('(');
        if (d < 0) d = member.indexOf(':');
        return d >= 0 && (member.indexOf("Ltop/dext/centurybridge/", d) >= 0 || member.indexOf("Lcenturybridge/", d) >= 0);
    }

    /**
     * The mixin a class is an inner class of (its name extends the mixin's with "$..."), when it is no mixin
     * itself: Mixin copies such a class for each target into the target's package (its synthetic and
     * anonymous inner classes), and the mixin's code merged into the target reaches that copy -- so the class
     * keeps its own hierarchy but stands in its targets' package (samePackage), and the mixin's code may reach
     * it whatever its access says.
     */
    private String mixinOuter(String name) {
        if (mixinNames.contains(name)) return null;
        for (int i = name.lastIndexOf('$'); i > 0; i = name.lastIndexOf('$', i - 1)) {
            String outer = name.substring(0, i);
            if (mixinNames.contains(outer)) return outer;
        }
        return null;
    }

    /** the member (or class) name a reference names: owner.name(desc) / owner.name:desc / owner */
    private static String memberName(String ref) {
        int dot = ref.indexOf('.');
        if (dot < 0) return ref;
        String rest = ref.substring(dot + 1);
        int end = rest.indexOf('(');
        if (end < 0) end = rest.indexOf(':');
        return end < 0 ? rest : rest.substring(0, end);
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
        // KNOWN is the converter's own "still broken": a hard failure where it is reached,
        // unless it is an orphaned override (behaviour silently gone, nothing thrown)
        Set<String> broken = new HashSet<>();
        Set<String> touched = new HashSet<>();
        for (Finding f : findings) {
            boolean ours = f.bucket.equals("CLAIMED") || f.bucket.equals("BLIND");
            if (ours) dirty.add(f.jar);
            if (ours || f.bucket.equals("KNOWN")) {
                touched.add(f.jar);
                if (!f.kind.equals("ORPHAN")) broken.add(f.jar);
            }
        }
        for (String j : subjects.keySet()) if (!dirty.contains(j)) clean++;
        int unbroken = subjects.size() - broken.size();
        int intact = subjects.size() - touched.size();
        System.out.println("link-check: " + subjects.size() + " subject jars, " + index.size() + " classes indexed, "
                           + mixins.size() + " mixins folded");
        counts.forEach((k, v) -> System.out.println("  " + k + ": " + v + " (distinct per jar)"));
        jarsByBucket.forEach((k, v) -> System.out.println("  jars with " + k + ": " + v.size()));
        System.out.println("  jars with no CLAIMED/BLIND finding: " + clean + "/" + subjects.size());
        System.out.println("  jars with no broken reference (CLAIMED/BLIND/KNOWN, orphaned overrides aside): "
                           + unbroken + "/" + subjects.size());
        System.out.println("  jars fully intact (no CLAIMED/BLIND/KNOWN at all): " + intact + "/" + subjects.size());
        System.out.println("link-check -> " + outDir.resolve("link-findings.tsv") + ", link-refs.tsv");
    }
}
