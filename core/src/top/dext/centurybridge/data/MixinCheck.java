package top.dext.centurybridge.data;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;
import org.objectweb.asm.ClassReader;
import org.objectweb.asm.Type;
import org.objectweb.asm.tree.AnnotationNode;
import org.objectweb.asm.tree.ClassNode;
import org.objectweb.asm.tree.FieldNode;
import org.objectweb.asm.tree.MethodNode;

/**
 * Static check of everything Mixin resolves by NAME at PREPARE/apply time --
 * the part javac cannot see. A shim tree that compiles clean can still take
 * the whole game down on the first boot because an {@code @Accessor} names a
 * field that no longer exists, or an {@code @At} selector points at a method
 * whose descriptor moved. The loader reports exactly one such failure per
 * boot; this reports all of them against the real target jar.
 *
 * Checks, per mixin class:
 *   @Accessor / @Invoker   member must be declared on the target class
 *   @Shadow                field/method must be declared on the target
 *   @Overwrite             method must be declared on the target
 *   @Inject & friends      every "method" entry must resolve on the target
 *   @At(target = ...)      the selector's owner must declare the member
 *   non-private statics    Mixin throws InvalidMixinException at apply for a
 *                          class mixin's static method unless it is private,
 *                          synthetic or an @Overwrite
 *   covers                 a mixin declares the exact old shape, with the
 *                          static-ness of the ORIGINAL member (older stubs)
 *
 *   mixin-check <compiledShimDir> <targetJar> [<shims.json> [<inter-to-mojang.tsv> [<stubDir> <targetVersion>]]]
 */
public final class MixinCheck {

    private static final String MIXIN = "Lorg/spongepowered/asm/mixin/Mixin;";
    private static final String SHADOW = "Lorg/spongepowered/asm/mixin/Shadow;";
    private static final String OVERWRITE = "Lorg/spongepowered/asm/mixin/Overwrite;";
    private static final String ACCESSOR = "Lorg/spongepowered/asm/mixin/gen/Accessor;";
    private static final String INVOKER = "Lorg/spongepowered/asm/mixin/gen/Invoker;";
    private static final String INJECT_PKG = "Lorg/spongepowered/asm/mixin/injection/";

    private final Map<String, ClassNode> jar = new HashMap<>();
    /** the packages the game jar has classes in: an owner elsewhere is a library's (brigadier, DFU, ...) */
    private final java.util.Set<String> jarPackages = new java.util.HashSet<>();
    private final List<String> problems = new ArrayList<>();

    /** mixin class -> its target internal names, for the covers check */
    private final Map<String, List<String>> mixinTargets = new HashMap<>();
    private final Map<String, ClassNode> mixinNodes = new HashMap<>();

    public static void run(Path classesDir, Path targetJar) throws IOException {
        run(classesDir, targetJar, null);
    }

    public static void run(Path classesDir, Path targetJar, Path shimsJson) throws IOException {
        run(classesDir, targetJar, shimsJson, null);
    }

    /** inter->mojang table for real-name targets: covers are keyed in the analysis namespace */
    private final Map<String, String> classMap = new HashMap<>();
    private final Map<String, String> memberMap = new HashMap<>();

    public static void run(Path classesDir, Path targetJar, Path shimsJson, Path table) throws IOException {
        run(classesDir, targetJar, shimsJson, table, null, null);
    }

    public static void run(Path classesDir, Path targetJar, Path shimsJson, Path table,
                           Path stubDir, String targetVersion) throws IOException {
        MixinCheck c = new MixinCheck();
        c.index(targetJar);
        if (stubDir != null && targetVersion != null) {
            c.sources = SourceStubs.before(stubDir, targetVersion);
        }
        if (table != null && Files.exists(table)) {
            for (String ln : Files.readAllLines(table)) {
                String[] p = ln.split("\t");
                if (p.length < 3) {
                    continue;
                }
                if (p[0].equals("C")) {
                    c.classMap.put(p[1], p[2]);
                } else {
                    c.memberMap.put(p[1], p[2].substring(p[2].lastIndexOf('.') + 1));
                }
            }
        }
        int mixins = 0;
        try (var stream = Files.walk(classesDir)) {
            for (Path p : (Iterable<Path>) stream.filter(f -> f.toString().endsWith(".class"))::iterator) {
                ClassNode cn = new ClassNode();
                new ClassReader(Files.readAllBytes(p)).accept(cn, ClassReader.SKIP_CODE);
                if (c.check(cn)) {
                    mixins++;
                }
            }
        }
        if (shimsJson != null && top.dext.centurybridge.engine.Wiring.exists(shimsJson)) {
            c.checkCovers(shimsJson);
        }
        c.sources.close();
        System.out.println("mixin-check: " + mixins + " mixin classes against " + targetJar.getFileName()
            + ", " + c.problems.size() + " unresolved targets");
        for (String s : c.problems) {
            System.out.println("    " + s);
        }
        if (!c.problems.isEmpty()) {
            System.exit(1);
        }
    }

    private void index(Path targetJar) throws IOException {
        try (ZipInputStream zin = new ZipInputStream(Files.newInputStream(targetJar))) {
            ZipEntry e;
            while ((e = zin.getNextEntry()) != null) {
                if (!e.getName().endsWith(".class")) {
                    continue;
                }
                ClassNode cn = new ClassNode();
                new ClassReader(zin.readAllBytes()).accept(cn, ClassReader.SKIP_CODE);
                jar.put(cn.name, cn);
                int slash = cn.name.lastIndexOf('/');
                jarPackages.add(slash < 0 ? "" : cn.name.substring(0, slash));
            }
        }
    }

    /** @return true if the class was a mixin (and got checked). */
    private boolean check(ClassNode cn) {
        AnnotationNode mixin = find(cn.invisibleAnnotations, MIXIN);
        if (mixin == null) {
            mixin = find(cn.visibleAnnotations, MIXIN);
        }
        if (mixin == null) {
            return false;
        }
        List<String> targets = new ArrayList<>();
        Object value = get(mixin, "value");
        if (value instanceof List<?> l) {
            for (Object o : l) {
                if (o instanceof Type t) {
                    targets.add(t.getInternalName());
                }
            }
        }
        Object names = get(mixin, "targets");
        if (names instanceof List<?> l) {
            for (Object o : l) {
                targets.add(o.toString().replace('.', '/'));
            }
        }
        String me = cn.name.substring(cn.name.lastIndexOf('/') + 1);
        mixinTargets.put(cn.name, targets);
        mixinNodes.put(cn.name, cn);
        // MixinApplicatorStandard.checkMethodVisibility: a whole-game crash at apply
        if ((cn.access & org.objectweb.asm.Opcodes.ACC_INTERFACE) == 0) {
            for (MethodNode m : cn.methods) {
                if ((m.access & org.objectweb.asm.Opcodes.ACC_STATIC) != 0
                        && (m.access & (org.objectweb.asm.Opcodes.ACC_PRIVATE | org.objectweb.asm.Opcodes.ACC_SYNTHETIC)) == 0
                        && !m.name.equals("<clinit>")
                        && !has(m.visibleAnnotations, m.invisibleAnnotations, OVERWRITE)) {
                    problems.add(me + ": non-private static method " + m.name + m.desc
                        + " (Mixin refuses it at apply; a static shape belongs on Statics behind a staticRedirect)");
                }
            }
        }
        for (String t : targets) {
            ClassNode target = jar.get(t);
            if (target == null) {
                problems.add(me + ": @Mixin target " + t + " not in jar");
                continue;
            }
            // a class mixin on an interface target dies at PREPARE (26.2 turned
            // BlockModel into an interface); only an interface mixin may target one
            if ((target.access & org.objectweb.asm.Opcodes.ACC_INTERFACE) != 0
                    && (cn.access & org.objectweb.asm.Opcodes.ACC_INTERFACE) == 0) {
                problems.add(me + ": class mixin targets interface " + t);
            }
            for (FieldNode f : cn.fields) {
                if (has(f.visibleAnnotations, f.invisibleAnnotations, SHADOW)
                        && !declaresField(target, f.name, f.desc)) {
                    problems.add(me + ": @Shadow field " + f.name + ":" + f.desc + " not declared on " + t);
                }
            }
            for (MethodNode m : cn.methods) {
                checkMethod(me, m, target);
                // A plain bridge overload that matches a method the target already
                // declares does not add an overload: Mixin merges over it, and if
                // the target's is static the JVM throws IncompatibleClassChangeError
                // at every existing call site (26.2: BreezeModel.createBodyLayer).
                if (!m.name.equals("<init>") && !m.name.equals("<clinit>")
                        && (m.access & org.objectweb.asm.Opcodes.ACC_STATIC) == 0
                        && (m.visibleAnnotations == null || m.visibleAnnotations.isEmpty())
                        && (m.invisibleAnnotations == null || m.invisibleAnnotations.isEmpty())) {
                    for (MethodNode tm : target.methods) {
                        if (tm.name.equals(m.name) && tm.desc.equals(m.desc)) {
                            problems.add(me + ": overload " + m.name + m.desc + " already declared on " + target.name
                                + ((tm.access & org.objectweb.asm.Opcodes.ACC_STATIC) != 0 ? " (static: ICCE at call sites)" : " (would be overwritten)"));
                        }
                    }
                }
            }
        }
        return true;
    }

    /**
     * A "covers" entry tells the converter an old call shape is served by a
     * runtime overload, so the call site is left alone. If no mixin targeting
     * that owner (or an ancestor) declares the name+descriptor, the wiring is
     * lying: the converter reports "covered" and the game throws
     * NoSuchMethodError. Prunes that cut a method but not its cover did this.
     */
    private void checkCovers(Path shimsJson) throws IOException {
        var root = top.dext.centurybridge.engine.Wiring.read(shimsJson);
        if (!root.has("covers")) {
            return;
        }
        // the converter's view of a call site: every class of the descriptor goes
        // through the facade classRenames (a dead class we re-provide, or a vanilla
        // merge target) BEFORE the namespace wall, so a cover whose shape names
        // class_332 is served only by a mixin declaring the GuiGraphics FACADE --
        // vanilla's own render(GuiGraphics,...) is a different descriptor and
        // must not count as serving it
        Map<String, String> renames = new HashMap<>();
        if (root.has("classRenames")) {
            for (var e : root.getAsJsonObject("classRenames").entrySet()) {
                renames.put(e.getKey(), e.getValue().getAsString());
            }
        }
        var facade = new org.objectweb.asm.commons.SimpleRemapper(renames);
        // a call shape the converter itself rewrites (lookupHooks: append the
        // registry lookup, call the new descriptor) never reaches a mixin
        Map<String, String> hooked = new HashMap<>();
        if (root.has("lookupHooks")) {
            for (var e : root.getAsJsonObject("lookupHooks").entrySet()) {
                hooked.put(e.getKey(), e.getValue().getAsJsonArray().get(0).getAsString());
            }
        }
        int missing = 0;
        for (var el : root.getAsJsonArray("covers")) {
            String key = el.getAsString();
            int paren = key.indexOf('(');
            // the owner/name split is the last '.' BEFORE the descriptor: a '/'
            // inside the descriptor's class names is not the end of the owner
            // (searching after the last '/' skipped every cover with a reference
            // parameter -- the gate was passing on nothing)
            int dot = paren < 0 ? -1 : key.lastIndexOf('.', paren);
            if (dot < 0 || paren < 0) {
                continue; // field-shaped cover: not a method overload
            }
            String owner = key.substring(0, dot);
            String name = key.substring(dot + 1, paren);
            if (key.substring(paren).equals(hooked.get(name))) {
                continue; // rewritten by the converter, not served by an overload
            }
            Integer orig = sources.methodAccess(owner, name, key.substring(paren));
            String desc = facade.mapMethodDesc(key.substring(paren));
            if (!classMap.isEmpty()) {
                // real-name target: the bridge declares mojang names for members
                // the table knows, tokens for the rest (see translate_tree.py)
                name = memberMap.getOrDefault(owner + "." + name, name);
            }
            // an owner merged into a surviving vanilla class carries its covers
            // there; one renamed onto a facade we ship is checked by direct target
            owner = renames.getOrDefault(owner, owner);
            if (!classMap.isEmpty()) {
                owner = classMap.getOrDefault(owner, owner);
                desc = java.util.regex.Pattern.compile("L([^;]+);").matcher(desc).replaceAll(
                    mr -> "L" + java.util.regex.Matcher.quoteReplacement(classMap.getOrDefault(mr.group(1), mr.group(1))) + ";");
            }
            // the converter leaves a covered call site alone, opcode included: a
            // static original is called with invokestatic, which no mixin-added
            // (necessarily instance) method answers -- IncompatibleClassChangeError
            if (orig != null && (orig & org.objectweb.asm.Opcodes.ACC_STATIC) != 0) {
                ClassNode native_ = jar.get(owner);
                final String sName = name;
                final String sDesc = desc;
                if (native_ != null && walk(native_, cn -> cn.methods.stream().anyMatch(m -> m.name.equals(sName)
                        && m.desc.equals(sDesc) && (m.access & org.objectweb.asm.Opcodes.ACC_STATIC) != 0))) {
                    continue; // the shape came back on the target as a static: served natively
                }
                List<String> by = new ArrayList<>();
                for (var e : mixinNodes.entrySet()) {
                    for (MethodNode m : e.getValue().methods) {
                        if (m.name.equals(name) && m.desc.equals(desc)) {
                            by.add(e.getKey());
                        }
                    }
                }
                problems.add("cover of a static method (call sites are invokestatic; serve it with a staticRedirect): " + key
                    + (by.isEmpty() ? "" : " [declared by " + String.join(",", by) + " as " + name + desc + "]"));
                missing++;
                continue;
            }
            // a bridge on an ancestor serves subclass call sites (the converter
            // climbs the same way), and an interface mixin's default method
            // serves every implementor (one multi-target mixin covered seven
            // mouseScrolled subclasses through their shared interface) -- so
            // walk superclasses AND interfaces, and match the exact name+desc:
            // a same-name overload of another arity is not this shape
            final String fName = name;
            final String fDesc = desc;
            java.util.function.Predicate<ClassNode> bridged = cn -> {
                for (var e : mixinTargets.entrySet()) {
                    if (!e.getValue().contains(cn.name)) {
                        continue;
                    }
                    for (MethodNode m : mixinNodes.get(e.getKey()).methods) {
                        if (m.name.equals(fName) && m.desc.equals(fDesc)) {
                            return true;
                        }
                    }
                }
                return false;
            };
            boolean served = false;
            ClassNode start = jar.get(owner);
            if (start != null) {
                served = walk(start, bridged);
            } else {
                // owner not in the target jar (a facade or a dead class): only a
                // mixin naming it directly can serve
                for (var e : mixinTargets.entrySet()) {
                    if (e.getValue().contains(owner)) {
                        for (MethodNode m : mixinNodes.get(e.getKey()).methods) {
                            if (m.name.equals(name) && m.desc.equals(desc)) {
                                served = true;
                            }
                        }
                    }
                }
            }
            // the target itself may declare the exact shape (cover kept for a
            // symbol that came back): that serves the call site too
            if (!served) {
                ClassNode t = jar.get(owner);
                served = t != null && hasMethod(t, name, desc);
            }
            if (!served) {
                problems.add("cover without a bridge: " + key);
                missing++;
            }
        }
        if (missing > 0) {
            System.out.println("covers: " + missing + " entries name a shape no mixin provides");
        }
    }

    private void checkMethod(String me, MethodNode m, ClassNode target) {
        List<AnnotationNode> all = new ArrayList<>();
        if (m.visibleAnnotations != null) {
            all.addAll(m.visibleAnnotations);
        }
        if (m.invisibleAnnotations != null) {
            all.addAll(m.invisibleAnnotations);
        }
        for (AnnotationNode a : all) {
            switch (a.desc) {
                case SHADOW -> {
                    if (!declaresMethod(target, m.name, m.desc)) {
                        problems.add(me + ": @Shadow method " + m.name + m.desc + " not declared on " + target.name);
                    }
                }
                case OVERWRITE -> {
                    if (!hasMethod(target, m.name, m.desc)) {
                        problems.add(me + ": @Overwrite " + m.name + m.desc + " not declared on " + target.name);
                    }
                }
                case ACCESSOR -> {
                    String name = str(get(a, "value"));
                    boolean setter = Type.getReturnType(m.desc).getSort() == Type.VOID;
                    String fdesc = setter ? Type.getArgumentTypes(m.desc)[0].getDescriptor()
                                          : Type.getReturnType(m.desc).getDescriptor();
                    if (name == null) {
                        name = derive(m.name, "get", "is", "set");
                    }
                    if (name == null || !hasField(target, name, fdesc)) {
                        problems.add(me + ": @Accessor " + m.name + " -> " + target.name + "." + name + ":" + fdesc + " missing");
                    }
                }
                case INVOKER -> {
                    String name = str(get(a, "value"));
                    if (name == null) {
                        name = derive(m.name, "call", "invoke", "new", "create");
                    }
                    // a constructor invoker (@Invoker("<init>"), static, returning the target type)
                    // binds the constructor taking the invoker's arguments
                    String want = "<init>".equals(name)
                        ? m.desc.substring(0, m.desc.lastIndexOf(')') + 1) + "V" : m.desc;
                    if (name == null || !hasMethod(target, name, want)) {
                        problems.add(me + ": @Invoker " + m.name + " -> " + target.name + "." + name + want + " missing");
                    }
                }
                default -> {
                    if (a.desc.startsWith(INJECT_PKG)) {
                        checkInjection(me, m, a, target);
                    }
                }
            }
        }
    }

    /** injectors that take exactly one @At (in the Mixin API the runtimes load) */
    private static final java.util.Set<String> SINGLE_AT = java.util.Set.of(
        "Lorg/spongepowered/asm/mixin/injection/Redirect;",
        "Lorg/spongepowered/asm/mixin/injection/ModifyVariable;",
        "Lorg/spongepowered/asm/mixin/injection/ModifyArg;",
        "Lorg/spongepowered/asm/mixin/injection/ModifyArgs;");

    private void checkInjection(String me, MethodNode m, AnnotationNode a, ClassNode target) {
        Object methods = get(a, "method");
        if (methods instanceof List<?> l) {
            for (Object o : l) {
                String sel = o.toString();
                String name = sel.contains("(") ? sel.substring(0, sel.indexOf('(')) : sel;
                String desc = sel.contains("(") ? sel.substring(sel.indexOf('(')) : null;
                boolean ok = desc == null ? hasMethodNamed(target, name) : hasMethod(target, name, desc);
                if (!ok) {
                    problems.add(me + ": " + short_(a.desc) + " " + m.name + " method=\"" + sel + "\" not on " + target.name);
                }
            }
        }
        Object at = get(a, "at");
        // Redirect / ModifyVariable / ModifyArg(s) take ONE @At in sponge-mixin 0.17.3 (Fabric Loader
        // 0.19.3 servers run it) and an array in 0.17.4: compiled against the newer API the value is
        // an array, which the older reads as a ClassCastException before any mixin applies
        if (at instanceof List<?> && SINGLE_AT.contains(a.desc)) {
            problems.add(me + ": " + short_(a.desc) + " " + m.name + " encodes at as an array (compiled against a Mixin API"
                + " newer than the runtimes'; build against the oldest sponge-mixin)");
        }
        List<AnnotationNode> ats = new ArrayList<>();
        if (at instanceof AnnotationNode an) {
            ats.add(an);
        } else if (at instanceof List<?> l) {
            for (Object o : l) {
                if (o instanceof AnnotationNode an) {
                    ats.add(an);
                }
            }
        }
        for (AnnotationNode an : ats) {
            String sel = str(get(an, "target"));
            if (sel == null || sel.isEmpty()) {
                continue;
            }
            // Lowner;name(desc)ret   |   Lowner;name:desc   |   name(desc)ret on the target
            String owner = target.name;
            String rest = sel;
            if (sel.startsWith("L") && sel.contains(";")) {
                owner = sel.substring(1, sel.indexOf(';'));
                rest = sel.substring(sel.indexOf(';') + 1);
            }
            ClassNode oc = jar.get(owner);
            if (oc == null) {
                // an owner in a package of the game is a game class the jar must have; one in another
                // package (com/mojang/brigadier, com/mojang/serialization) is a library's, which Mixin
                // matches by name in the target method's code without loading it
                int slash = owner.lastIndexOf('/');
                if (jarPackages.contains(slash < 0 ? "" : owner.substring(0, slash))) {
                    problems.add(me + ": @At owner " + owner + " not in jar (" + m.name + ")");
                }
                continue;
            }
            boolean ok;
            if (rest.contains("(")) {
                ok = hasMethod(oc, rest.substring(0, rest.indexOf('(')), rest.substring(rest.indexOf('(')));
            } else if (rest.contains(":")) {
                ok = hasField(oc, rest.substring(0, rest.indexOf(':')), rest.substring(rest.indexOf(':') + 1));
            } else {
                ok = hasMethodNamed(oc, rest) || hasFieldNamed(oc, rest);
            }
            if (!ok) {
                problems.add(me + ": @At target \"" + sel + "\" unresolved (" + m.name + ")");
            }
        }
    }

    // ---- helpers ----

    private static String derive(String mname, String... prefixes) {
        for (String p : prefixes) {
            if (mname.startsWith(p) && mname.length() > p.length()) {
                String rest = mname.substring(p.length());
                // Mixin keeps an all-uppercase remainder as-is (getCRITERIA -> CRITERIA)
                return rest.equals(rest.toUpperCase()) ? rest
                    : Character.toLowerCase(rest.charAt(0)) + rest.substring(1);
            }
        }
        return null;
    }

    // Lookups climb supertypes. Mixin itself resolves most of these on the
    // target class only, but our intermediary stubs are built by a remapper
    // that does not propagate inherited member names the way the runtime
    // does (MappedRegistry.add is method_10272 at runtime, still "a" in the
    // stub because the tiny file lists it under the interface). Climbing
    // trades a few missed true positives for zero stub-induced false ones.
    private boolean hasField(ClassNode c, String name, String desc) {
        return walk(c, cn -> {
            for (FieldNode f : cn.fields) {
                if (f.name.equals(name) && (desc == null || f.desc.equals(desc))) {
                    return true;
                }
            }
            return false;
        });
    }

    private boolean hasFieldNamed(ClassNode c, String name) {
        return hasField(c, name, null);
    }

    private boolean hasMethod(ClassNode c, String name, String desc) {
        return walk(c, cn -> {
            for (MethodNode m : cn.methods) {
                if (m.name.equals(name) && (desc == null || m.desc.equals(desc))) {
                    return true;
                }
            }
            return false;
        });
    }

    /**
     * A @Shadow resolves on the target class itself: Mixin looks the member up in the
     * target's own field and method lists and rejects the shadow at apply ("not located in
     * the target class"), taking the whole layer down. A member the target only inherits --
     * from a superclass or an interface (Level.getChunkSource, declared on LevelAccessor) --
     * is not found; the shadow belongs on a mixin of the declaring class.
     */
    private static boolean declaresMethod(ClassNode c, String name, String desc) {
        for (MethodNode m : c.methods) {
            if (m.name.equals(name) && m.desc.equals(desc)) {
                return true;
            }
        }
        return false;
    }

    private static boolean declaresField(ClassNode c, String name, String desc) {
        for (FieldNode f : c.fields) {
            if (f.name.equals(name) && f.desc.equals(desc)) {
                return true;
            }
        }
        return false;
    }

    private boolean hasMethodNamed(ClassNode c, String name) {
        return hasMethod(c, name, null);
    }

    /** older releases' stubs: a cover's ORIGINAL member is looked up there */
    private SourceStubs sources = SourceStubs.none();

    private boolean walk(ClassNode c, java.util.function.Predicate<ClassNode> test) {
        java.util.ArrayDeque<ClassNode> todo = new java.util.ArrayDeque<>();
        java.util.Set<String> seen = new java.util.HashSet<>();
        todo.add(c);
        while (!todo.isEmpty()) {
            ClassNode cn = todo.poll();
            if (!seen.add(cn.name)) {
                continue;
            }
            if (test.test(cn)) {
                return true;
            }
            if (cn.superName != null && jar.containsKey(cn.superName)) {
                todo.add(jar.get(cn.superName));
            }
            if (cn.interfaces != null) {
                for (String i : cn.interfaces) {
                    if (jar.containsKey(i)) {
                        todo.add(jar.get(i));
                    }
                }
            }
        }
        return false;
    }

    private static boolean has(List<AnnotationNode> a, List<AnnotationNode> b, String desc) {
        return find(a, desc) != null || find(b, desc) != null;
    }

    private static AnnotationNode find(List<AnnotationNode> list, String desc) {
        if (list == null) {
            return null;
        }
        for (AnnotationNode a : list) {
            if (a.desc.equals(desc)) {
                return a;
            }
        }
        return null;
    }

    private static Object get(AnnotationNode a, String key) {
        if (a.values == null) {
            return null;
        }
        for (int i = 0; i + 1 < a.values.size(); i += 2) {
            if (key.equals(a.values.get(i))) {
                return a.values.get(i + 1);
            }
        }
        return null;
    }

    private static String str(Object o) {
        return o == null ? null : o.toString();
    }

    private static String short_(String desc) {
        return "@" + desc.substring(desc.lastIndexOf('/') + 1, desc.length() - 1);
    }
}
