package top.dext.centurybridge.data;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.objectweb.asm.ClassReader;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.commons.Remapper;
import org.objectweb.asm.commons.SimpleRemapper;
import org.objectweb.asm.tree.ClassNode;
import org.objectweb.asm.tree.FieldNode;
import org.objectweb.asm.tree.MethodNode;

/**
 * Build gate: every runtime member the wiring (shims-&lt;ver&gt;.json) points a
 * converted call site at must exist in the COMPILED shim tree with exactly
 * the descriptor the converter emits. INVOKESTATIC/GETSTATIC resolve by
 * exact name+descriptor, so "a method of that name exists" proves nothing:
 * a same-name overload with a different arity or a supertype parameter is a
 * NoSuchMethodError the first time a mod reaches it (the chest GUI on the
 * floor; the 4-arg renderBackground shadowing the dead 1-arg cover).
 *
 * The expected descriptor is derived the way the converter derives it -- the
 * key's own types, the receiver prepended for instance-shaped redirects, then
 * the facade {@code classRenames} pass every converted class goes through,
 * then (past the namespace wall) the same inter->mojang table the whole-jar
 * rename applies. Member names on our runtime classes are never renamed by
 * that table, so they compare verbatim.
 *
 * Wiring keys checked: staticRedirects, instanceRedirects, superRedirects, interfaceCallRedirects,
 * fieldRedirects, fieldGetters, staticFieldSetters, instanceFieldGetters, instanceFieldSetters, instanceOfRedirects, checkCastRedirects,
 * ctorRedirects, superCtorAdapts (converter), superCtorReshapes (conv / push / post
 * statics), lookupRt, overrideHooks (wrap / unwrap / guard / frames / bridge,
 * and the subtype relation a narrowing hook casts along), lambdaAdapters (the CB
 * interface declares exactly the old SAM; the adapter's exact descriptor), abstractFills (the
 * static a synthesized implementation calls), classRenames
 * onto shipped facades. Covers are checked by MixinCheck against the mixin
 * classes (they are overloads added to vanilla classes, not statics).
 *
 * A target in a separately released mod the layer depends on (-Dcb.depJars / -Dcb.layerModJson, {@link DepJars}) is
 * judged in that mod's jar by the same rules, public members of public classes only.
 *
 *   wiring-check &lt;compiledShimDir&gt; &lt;shims.json&gt; [&lt;inter-to-mojang.tsv&gt;] [&lt;target stub jar&gt;]
 */
public final class WiringCheck {

    private final Map<String, ClassNode> classes = new HashMap<>();
    /** target stub classes (members only): what superRenames and reshapes must agree with */
    private final Map<String, ClassNode> stub = new HashMap<>();
    /**
     * the interfaces the target's Fabric API injects into vanilla classes (Chain.readInjectedInterfaces), and
     * its modules' classes: a member a vanilla class has only through an injected interface resolves there
     */
    private Map<String, List<String>> injected = Map.of();
    private final Map<String, ClassNode> lib = new HashMap<>();
    /** the target's game libraries (-Dcb.libs), class headers only: subtype judgments of library-keyed wiring */
    private final Map<String, ClassNode> gameLibs = new HashMap<>();
    /**
     * the separately released mods the layer declares in depends (-Dcb.depJars, {@link DepJars}): a redirect or field
     * target that is neither a layer class nor a stub class is looked up here, public class and public member only
     * (another mod's jar: nothing else crosses), with the same exact-descriptor rule
     */
    private Map<String, ClassNode> deps = Map.of();
    private final List<String> problems = new ArrayList<>();
    private Remapper facade = new SimpleRemapper(Map.of());
    private Remapper wall;
    private int checked;

    public static void run(Path classesDir, Path shimsJson, Path table, Path stubJar) throws IOException {
        run(classesDir, shimsJson, table, stubJar, null, null);
    }

    /** older releases' stubs: what a facade stands in for */
    private SourceStubs sources = SourceStubs.none();
    /** member keys whose override the step's generic-drift table lists as damage; null: no table */
    private java.util.Set<String> genericOverrides;

    /**
     * The override rows of the step's generic-drift table (core/segments/damage-generic/V__W.tsv
     * beside the wiring's segments directory; step 1's wiring is shims-1.20.2.json). The table
     * decides which usages of a member drifted harmfully: an override whose answer only widens the
     * way vanilla reads it (OptionsList.getMouseOver: Optional<AbstractWidget> where 1.20.5 reads an
     * Optional<GuiEventListener>) has no row, and needs no converting bridge.
     */
    private static java.util.Set<String> genericOverrideRows(Path wiring) throws IOException {
        Path dir = wiring.toAbsolutePath().getParent();
        String fn = wiring.getFileName().toString();
        String step;
        if (dir.getFileName().toString().equals("steps") && fn.endsWith(".json")) {
            step = fn.substring(0, fn.length() - 5);
            dir = dir.getParent();
        } else if (fn.startsWith("shims-") && fn.endsWith(".json")) {
            String w = fn.substring(6, fn.length() - 5);
            int i = top.dext.centurybridge.engine.Chain.RELEASES.indexOf(w);
            if (i <= 0) {
                return null;
            }
            step = top.dext.centurybridge.engine.Chain.RELEASES.get(i - 1) + "__" + w;
        } else {
            return null;
        }
        Path t = dir.resolve("damage-generic").resolve(step + ".tsv");
        if (!Files.exists(t)) {
            return null;
        }
        java.util.Set<String> out = new java.util.HashSet<>();
        for (String line : Files.readAllLines(t)) {
            String[] f = line.split("	");
            if (f.length > 3 && f[0].equals("override")) {
                out.add(f[1] + "." + f[2] + f[3]);
            }
        }
        return out;
    }

    public static void run(Path classesDir, Path shimsJson, Path table, Path stubJar, Path stubDir, String version)
            throws IOException {
        WiringCheck w = new WiringCheck();
        if (stubDir != null && version != null) {
            w.sources = SourceStubs.before(stubDir, version);
        }
        // the target release's Fabric API (-Dcb.fapi=<its processedMods>): the interfaces it injects into vanilla
        // classes, and its modules' classes, so a target reached through an injected interface is judged there
        String fapi = System.getProperty("cb.fapi");
        if (fapi != null && !fapi.isEmpty()) {
            Path mods = Path.of(fapi);
            w.injected = top.dext.centurybridge.engine.Chain.readInjectedInterfaces(mods);
            try (var s = Files.list(mods)) {
                for (Path jar : (Iterable<Path>) s.filter(f -> f.toString().endsWith(".jar")).sorted()::iterator) {
                    try (var z = new java.util.zip.ZipFile(jar.toFile())) {
                        for (var en : java.util.Collections.list(z.entries())) {
                            if (!en.getName().endsWith(".class") || !en.getName().startsWith("net/fabricmc/")) continue;
                            ClassNode cn = new ClassNode();
                            new ClassReader(z.getInputStream(en).readAllBytes()).accept(cn, ClassReader.SKIP_CODE);
                            w.lib.putIfAbsent(cn.name, cn);
                        }
                    }
                }
            }
        }
        if (stubJar != null && Files.exists(stubJar)) {
            try (var z = new java.util.zip.ZipFile(stubJar.toFile())) {
                for (var en : java.util.Collections.list(z.entries())) {
                    if (!en.getName().endsWith(".class")) continue;
                    ClassNode cn = new ClassNode();
                    new ClassReader(z.getInputStream(en).readAllBytes()).accept(cn, ClassReader.SKIP_CODE);
                    w.stub.put(cn.name, cn);
                }
            }
        }
        try (var stream = Files.walk(classesDir)) {
            for (Path p : (Iterable<Path>) stream.filter(f -> f.toString().endsWith(".class"))::iterator) {
                ClassNode cn = new ClassNode();
                new ClassReader(Files.readAllBytes(p)).accept(cn, ClassReader.SKIP_CODE);
                w.classes.put(cn.name, cn);
            }
        }
        // the target's game libraries (-Dcb.libs=<its libraries dir>: authlib, JOML, fastutil...): class headers only, for
        // the subtype judgments of wiring keyed on a library type (JOML's Vector3f passed as a Vector3fc)
        String libs = System.getProperty("cb.libs");
        if (libs != null && !libs.isEmpty() && Files.isDirectory(Path.of(libs))) {
            List<Path> jars;
            try (var s = Files.walk(Path.of(libs))) {
                jars = s.filter(f -> f.toString().endsWith(".jar") && !f.getFileName().toString().contains("-natives-")).sorted().toList();
            }
            for (Path jar : jars) {
                try (var z = new java.util.zip.ZipFile(jar.toFile())) {
                    for (var en : java.util.Collections.list(z.entries())) {
                        String n = en.getName();
                        if (!n.endsWith(".class") || n.startsWith("META-INF/") || n.startsWith("net/minecraft/")) continue;
                        ClassNode cn = new ClassNode();
                        new ClassReader(z.getInputStream(en).readAllBytes()).accept(cn,
                            ClassReader.SKIP_CODE | ClassReader.SKIP_DEBUG | ClassReader.SKIP_FRAMES);
                        w.gameLibs.putIfAbsent(cn.name, cn);
                    }
                }
            }
        }
        w.deps = DepJars.read(w.problems);
        var root = top.dext.centurybridge.engine.Wiring.read(shimsJson);
        if (table != null && Files.exists(table)) {
            // as the conversion renames (Main.convert): tokens only the wiring's facades declare keep their names
            java.util.Map<String, String> renames = new java.util.HashMap<>();
            if (root.has("classRenames")) {
                root.getAsJsonObject("classRenames").entrySet().forEach(e -> renames.put(e.getKey(), e.getValue().getAsString()));
            }
            JarRemapper.Table t = JarRemapper.loadTable(table).keepFacadeTokens(renames, JarRemapper.Table.facadeMembers(w.classes));
            if (stubJar != null && Files.exists(stubJar)) {
                JarRemapper.loadTarget(t, stubJar); // the conversion's descriptor choice for reused ids
            }
            w.wall = t.remapper();
        }
        w.genericOverrides = genericOverrideRows(shimsJson);
        w.check(root);
        w.checkFacadeSurfaces(root);
        w.checkHookPrecedence(top.dext.centurybridge.engine.Wiring.areas(shimsJson));
        w.sources.close();
        System.out.println("wiring-check: " + w.checked + " wired targets against " + classesDir
            + ", " + w.problems.size() + " dangling");
        for (String s : w.problems) {
            System.out.println("    " + s);
        }
        if (!w.problems.isEmpty()) {
            System.exit(1);
        }
    }

    /**
     * Override hooks that fold into one new shape (Chain.OverrideHook#shadows): a class declaring both old
     * methods can synthesize the new shape once, so which hook reconnects it must be stated. A pair that
     * can meet in one class -- either is unfiltered, either filter is an interface or a type the source
     * stubs do not know (a library's: Fabric API), or one filter class is under the other -- needs one
     * hook to name the other's old shape in "shadows", unless both come from the same wiring file, whose
     * order its author wrote (the first wins). Two files, no statement: an error, since which loads first
     * is a file-name accident.
     */
    private void checkHookPrecedence(java.util.Map<String, com.google.gson.JsonObject> areas) {
        record Hook(String area, String old, String nw, String onlyUnder, java.util.Set<String> shadows) {
        }
        List<Hook> hooks = new ArrayList<>();
        for (var a : areas.entrySet()) {
            if (!a.getValue().has("overrideHooks")) {
                continue;
            }
            for (var el : a.getValue().getAsJsonArray("overrideHooks")) {
                var o = el.getAsJsonObject();
                java.util.Set<String> sh = new java.util.HashSet<>();
                if (o.has("shadows")) {
                    for (var s : o.getAsJsonArray("shadows")) {
                        sh.add(s.getAsString());
                    }
                }
                hooks.add(new Hook(a.getKey(), o.get("old").getAsString(), o.get("new").getAsString(),
                    o.has("onlyUnder") ? o.get("onlyUnder").getAsString() : null, sh));
            }
        }
        java.util.Set<String> oldShapes = new java.util.HashSet<>();
        for (Hook h : hooks) {
            oldShapes.add(h.old());
        }
        for (Hook h : hooks) {
            for (String s : h.shadows()) {
                if (!oldShapes.contains(s)) {
                    problems.add("overrideHooks " + h.old() + " (" + h.area() + "): shadows " + s + ", which no hook has as its old shape");
                }
            }
        }
        for (int i = 0; i < hooks.size(); i++) {
            for (int j = i + 1; j < hooks.size(); j++) {
                Hook a = hooks.get(i);
                Hook b = hooks.get(j);
                if (!a.nw().equals(b.nw()) || a.old().equals(b.old()) || !canMeet(a.onlyUnder(), b.onlyUnder())) {
                    continue;
                }
                boolean ab = a.shadows().contains(b.old());
                boolean ba = b.shadows().contains(a.old());
                if (ab && ba) {
                    problems.add("overrideHooks " + a.old() + " (" + a.area() + ") and " + b.old() + " (" + b.area()
                        + ") both fold into " + a.nw() + " and each says it shadows the other");
                } else if (!ab && !ba && !a.area().equals(b.area())) {
                    problems.add("overrideHooks " + a.old() + " (" + a.area() + ") and " + b.old() + " (" + b.area()
                        + ") both fold into " + a.nw() + " and can meet in one class: state which wins (\"shadows\")");
                }
                checked++;
            }
        }
    }

    /** whether one class can be under both hook filters (see checkHookPrecedence) */
    private boolean canMeet(String a, String b) {
        if (a == null || b == null || a.equals(b)) {
            return true;
        }
        List<ClassNode> av = sources.versionsOf(a);
        List<ClassNode> bv = sources.versionsOf(b);
        if (av.isEmpty() || bv.isEmpty()) {
            return true;
        }
        if ((av.get(0).access & Opcodes.ACC_INTERFACE) != 0 || (bv.get(0).access & Opcodes.ACC_INTERFACE) != 0) {
            return true;
        }
        return sourceSuperclass(a, b) || sourceSuperclass(b, a);
    }

    /** whether {@code sup} is {@code sub} or one of its superclasses, in the newest source stub knowing each */
    private boolean sourceSuperclass(String sub, String sup) {
        String c = sub;
        for (int guard = 0; c != null && guard < 64; guard++) {
            if (c.equals(sup)) {
                return true;
            }
            List<ClassNode> v = sources.versionsOf(c);
            c = v.isEmpty() ? null : v.get(0).superName;
        }
        return false;
    }

    /**
     * A facade stands in for a class that is gone: every reference a mod had to it is
     * renamed onto the facade, so the facade has to answer every member the dead class
     * declared -- a missing one is a NoSuchMethodError at a mod's call, super() or
     * super.m() (a LeafEntry.Serializer facade without the typed addEntryFields broke every
     * subclass's super call). A mod may come from any release the class lived in, so the
     * contract is every shape it had in any of them: constructors, methods and fields,
     * descriptors through the facade renames, static where the class had it static (the
     * call site's opcode is fixed); methods and fields may come from the facade's own
     * superclasses. Private members count: an access widener opens them -- Fabric API's
     * transitive one for every mod (ModelPredicateProviderRegistry.register), or the mod's
     * own -- and the mod then calls them like any other.
     *
     * Which is also why a facade's own members must all be public, and the facade not
     * final where the class was not: the loader applies access wideners to game classes
     * only, so whatever a mod's widener opened on the dead class does nothing for the
     * facade standing in for it -- a protected MusicDiscItem constructor some mod widened
     * to call it is an IllegalAccessError on the facade.
     */
    private void checkFacadeSurfaces(com.google.gson.JsonObject root) {
        if (sources.isEmpty() || !root.has("classRenames")) {
            return;
        }
        // a gap the facade does not fill must be declared, with its reason, so the
        // converter accounts the references instead of taking them as served
        Map<String, String> declared = new HashMap<>();
        if (root.has("facadeGaps")) {
            for (var g : root.getAsJsonObject("facadeGaps").entrySet()) {
                declared.put(g.getKey(), g.getValue().getAsString());
            }
        }
        java.util.Set<String> used = new java.util.HashSet<>();
        for (var e : root.getAsJsonObject("classRenames").entrySet()) {
            String dead = e.getKey();
            String fac = e.getValue().getAsString();
            if (!fac.startsWith("top/dext/") || !classes.containsKey(fac)) {
                continue;
            }
            List<ClassNode> lives = sources.versionsOf(dead);
            if (lives.isEmpty()) {
                continue;
            }
            List<String> missing = new ArrayList<>();
            List<String> closed = new ArrayList<>();
            ClassNode facNode = classes.get(fac);
            boolean enumFacade = (facNode.access & Opcodes.ACC_ENUM) != 0;
            // (an enum is final unless its constants have bodies -- no mod source can extend one either way)
            if ((facNode.access & Opcodes.ACC_FINAL) != 0
                    && lives.stream().anyMatch(v -> (v.access & (Opcodes.ACC_FINAL | Opcodes.ACC_ENUM)) == 0)) {
                problems.add("facade " + fac + " (for dead " + dead + ") is final; the class was not -- a mod's subclass cannot load");
            }
            java.util.Set<String> seen = new java.util.HashSet<>();
            List<MethodNode> methods = new ArrayList<>();
            List<FieldNode> fields = new ArrayList<>();
            for (ClassNode v : lives) {
                for (MethodNode m : v.methods) {
                    if (seen.add(m.name + m.desc)) {
                        methods.add(m);
                    }
                }
                for (FieldNode f : v.fields) {
                    if (seen.add(f.name + ":" + f.desc)) {
                        fields.add(f);
                    }
                }
            }
            for (MethodNode m : methods) {
                if ((m.access & Opcodes.ACC_SYNTHETIC) != 0 || m.name.equals("<clinit>")) {
                    continue;
                }
                checked++;
                String desc = methodDesc(m.desc);
                String gapKey = dead + "." + m.name + m.desc;
                // past the wall, under the name the conversion gives a reference naming the facade: the token
                // where the facade declares it so, the table's rename otherwise (JarRemapper.Table.keepFacadeTokens).
                // A private member of the class stands in only under its token: a facade's own private member of
                // the table's name is the facade's implementation, not that member given back
                String name = wall != null && !m.name.equals("<init>") && (m.access & Opcodes.ACC_PRIVATE) == 0
                    ? wall.mapMethodName(fac, m.name, m.desc) : m.name;
                MethodNode has = facadeMethod(fac, name, desc, m.name.equals("<init>"));
                if (has != null && has != OBJECT_METHOD && foundIn.startsWith("top/dext/")
                        && (has.access & Opcodes.ACC_PUBLIC) == 0 && !(enumFacade && m.name.equals("<init>"))) {
                    closed.add(m.name + desc);
                }
                if (has == null) {
                    if (declared.containsKey(gapKey)) {
                        used.add(gapKey);
                    } else {
                        missing.add(m.name + desc + privateMark(m.access));
                    }
                } else if (has != OBJECT_METHOD && (has.access & Opcodes.ACC_STATIC) != (m.access & Opcodes.ACC_STATIC)) {
                    problems.add("facade " + fac + " (for dead " + dead + ") declares " + m.name + desc
                        + ((m.access & Opcodes.ACC_STATIC) != 0 ? " as an instance method; the class had it static"
                            : " static; the class had it as an instance method")
                        + " (IncompatibleClassChangeError at every call)");
                }
            }
            for (FieldNode f : fields) {
                if ((f.access & Opcodes.ACC_SYNTHETIC) != 0) {
                    continue;
                }
                checked++;
                String desc = typeDesc(f.desc);
                String gapKey = dead + "." + f.name + ":" + f.desc;
                FieldNode has = facadeField(fac, wall != null && (f.access & Opcodes.ACC_PRIVATE) == 0
                    ? wall.mapFieldName(fac, f.name, f.desc) : f.name, desc);
                if (has != null && foundIn.startsWith("top/dext/") && (has.access & Opcodes.ACC_PUBLIC) == 0) {
                    closed.add(f.name + ":" + desc);
                }
                if (has == null) {
                    if (declared.containsKey(gapKey)) {
                        used.add(gapKey);
                    } else {
                        missing.add(f.name + ":" + desc + privateMark(f.access));
                    }
                } else if ((has.access & Opcodes.ACC_STATIC) != (f.access & Opcodes.ACC_STATIC)) {
                    problems.add("facade " + fac + " (for dead " + dead + ") declares field " + f.name + ":" + desc
                        + ((f.access & Opcodes.ACC_STATIC) != 0 ? " as an instance field; the class had it static"
                            : " static; the class had it as an instance field")
                        + " (IncompatibleClassChangeError at every access)");
                }
            }
            checkFunctional(dead, fac, declared, used);
            if (!closed.isEmpty()) {
                problems.add("facade " + fac + " (for dead " + dead + ") has " + closed.size()
                    + " non-public member(s) standing in for the class's -- access wideners never reach a"
                    + " CenturyBridge class, so they must be public: " + String.join(", ", closed));
            }
            if (!missing.isEmpty()) {
                problems.add("facade " + fac + " (for dead " + dead + ") lacks " + missing.size()
                    + " undeclared member(s) the class had (fill them or list them in facadeGaps): "
                    + String.join(", ", missing));
            }
        }
        for (String k : declared.keySet()) {
            if (!used.contains(k)) {
                problems.add("facadeGaps " + k + ": stale -- the facade provides it now, or it never was a member");
            }
        }
    }

    /**
     * A dead functional interface: mods implemented it with lambdas, which supply exactly
     * its single abstract method, so the facade has to be an interface whose only abstract
     * method is that one -- a facade inheriting the reused name's new abstract methods
     * (ClampedModelPredicateProvider over 1.21.4's NumericProperty) leaves every lambda
     * an AbstractMethodError.
     */
    private void checkFunctional(String dead, String fac, Map<String, String> declared, java.util.Set<String> used) {
        java.util.Set<String> wanted = new java.util.LinkedHashSet<>();
        for (java.util.Set<String> abs : sources.abstractShapes(dead)) {
            if (abs.size() == 1) {
                String shape = abs.iterator().next();
                if (declared.containsKey(dead + "." + shape)) {
                    used.add(dead + "." + shape); // a declared loss: lambdas implementing it are accounted
                    continue;
                }
                int paren = shape.indexOf('(');
                wanted.add(shape.substring(0, paren) + methodDesc(shape.substring(paren)));
            }
        }
        if (wanted.isEmpty()) {
            return;
        }
        ClassNode fcn = classes.get(fac);
        java.util.Set<String> have = (fcn.access & Opcodes.ACC_INTERFACE) != 0
            ? SourceStubs.abstractShapes(fac, c -> classes.containsKey(c) ? classes.get(c) : stub.get(c))
            : null;
        for (String sam : wanted) {
            if (have == null || !have.equals(java.util.Set.of(sam))) {
                problems.add("facade " + fac + " (for dead functional interface " + dead + ", single abstract "
                    + sam + ") " + (have == null ? "is not an interface" : "has abstract methods " + have)
                    + ": a mod lambda implementing it supplies only " + sam + " (AbstractMethodError)");
            }
        }
    }

    /** what every class answers through java/lang/Object (records and enums declare their own) */
    private static final java.util.Set<String> OBJECT_METHODS = java.util.Set.of(
        "toString()Ljava/lang/String;", "equals(Ljava/lang/Object;)Z", "hashCode()I", "clone()Ljava/lang/Object;");
    private static final MethodNode OBJECT_METHOD = new MethodNode();

    /** a gap the gate lists: private ones are reachable only through an access widener */
    private static String privateMark(int access) {
        return (access & Opcodes.ACC_PRIVATE) != 0 ? " [private]" : "";
    }

    /** the class facadeMethod / facadeField last found the member in */
    private String foundIn;

    private MethodNode facadeMethod(String cls, String name, String desc, boolean ctor) {
        if (!ctor && OBJECT_METHODS.contains(name + desc)) {
            return OBJECT_METHOD;
        }
        for (String c = cls; c != null; ) {
            ClassNode cn = classes.containsKey(c) ? classes.get(c) : stub.get(c);
            if (cn == null) {
                return null;
            }
            for (MethodNode m : cn.methods) {
                if (m.name.equals(name) && m.desc.equals(desc)) {
                    foundIn = c;
                    return m;
                }
            }
            if (ctor) {
                return null; // constructors are not inherited
            }
            for (String i : cn.interfaces) {
                ClassNode in = classes.containsKey(i) ? classes.get(i) : stub.get(i);
                if (in != null) {
                    for (MethodNode m : in.methods) {
                        if (m.name.equals(name) && m.desc.equals(desc)) {
                            foundIn = i;
                            return m;
                        }
                    }
                }
            }
            c = cn.superName;
        }
        return null;
    }

    private FieldNode facadeField(String cls, String name, String desc) {
        for (String c = cls; c != null; ) {
            ClassNode cn = classes.containsKey(c) ? classes.get(c) : stub.get(c);
            if (cn == null) {
                return null;
            }
            for (FieldNode f : cn.fields) {
                if (f.name.equals(name) && f.desc.equals(desc)) {
                    foundIn = c;
                    return f;
                }
            }
            c = cn.superName;
        }
        return null;
    }

    private void check(com.google.gson.JsonObject root) {
        // retiredClasses (Chain.retiredClasses) take classes out of every jar the step converts: only a
        // CenturyBridge layer's own may go, with a reason
        if (root.has("retiredClasses")) {
            for (var e : root.getAsJsonObject("retiredClasses").entrySet()) {
                checked++;
                if (!e.getKey().startsWith("top/dext/centurybridge/")) {
                    problems.add("retiredClasses " + e.getKey() + ": only CenturyBridge's own classes retire");
                }
                if (!e.getValue().isJsonPrimitive() || e.getValue().getAsString().isBlank()) {
                    problems.add("retiredClasses " + e.getKey() + ": needs the reason it retires at this step");
                }
            }
        }
        if (root.has("classRenames")) {
            Map<String, String> ren = new HashMap<>();
            for (var e : root.getAsJsonObject("classRenames").entrySet()) {
                ren.put(e.getKey(), e.getValue().getAsString());
                String v = e.getValue().getAsString();
                if (v.startsWith("top/dext/") && !classes.containsKey(v)) {
                    problems.add("classRename " + e.getKey() + " -> " + v + " (no such facade class)");
                }
                checked++;
            }
            facade = new SimpleRemapper(ren);
        }
        // superRenames point a mod's extends/implements at a shipped facade class
        // (interface or abstract class). Like classRenames, a key naming a facade
        // that was never compiled would make every converted subclass fail to load
        // with NoClassDefFound -- the build gate must catch the dangling target.
        if (root.has("superRenames")) {
            for (var e : root.getAsJsonObject("superRenames").entrySet()) {
                String v = e.getValue().getAsString();
                if (v.startsWith("top/dext/") && !classes.containsKey(v)) {
                    problems.add("superRename " + e.getKey() + " -> " + v + " (no such facade class)");
                }
                checked++;
                if (!stub.isEmpty()) checkSuperRename(e.getKey(), v);
            }
        }
        // "owner.name(desc)R" -> "cls[#member]": static with the key's descriptor
        obj(root, "staticRedirects", (k, v) -> {
            Sym s = Sym.method(k);
            wantMethod("staticRedirects " + k, v, s.name, methodDesc(s.desc));
        });
        // instance-shaped: receiver becomes argument 0
        for (String map : new String[] {"instanceRedirects", "superRedirects", "interfaceCallRedirects"}) {
            obj(root, map, (k, v) -> {
                Sym s = Sym.method(k);
                wantMethod(map + " " + k, v, s.name, methodDesc("(L" + s.owner + ";" + s.desc.substring(1)));
            });
        }
        // "owner.name:desc" -> static field of that type
        obj(root, "fieldRedirects", (k, v) -> {
            Sym s = Sym.field(k);
            wantField("fieldRedirects " + k, v, s.name, typeDesc(s.desc));
        });
        obj(root, "fieldGetters", (k, v) -> {
            Sym s = Sym.field(k);
            wantMethod("fieldGetters " + k, v, s.name, methodDesc("()" + s.desc));
        });
        obj(root, "staticFieldSetters", (k, v) -> {
            Sym s = Sym.field(k);
            wantMethod("staticFieldSetters " + k, v, s.name, methodDesc("(" + s.desc + ")V"));
        });
        obj(root, "instanceFieldGetters", (k, v) -> {
            Sym s = Sym.field(k);
            wantMethod("instanceFieldGetters " + k, v, s.name, methodDesc("(L" + s.owner + ";)" + s.desc));
        });
        // "owner.name:desc" -> static setter(owner, desc)V: the PUTFIELD's receiver and value
        obj(root, "instanceFieldSetters", (k, v) -> {
            Sym s = Sym.field(k);
            wantMethod("instanceFieldSetters " + k, v, s.name, methodDesc("(L" + s.owner + ";" + s.desc + ")V"));
        });
        obj(root, "instanceOfRedirects", (k, v) ->
            wantMethod("instanceOfRedirects " + k, v, "cbInstanceOf", "(Ljava/lang/Object;)Z"));
        obj(root, "checkCastRedirects", (k, v) ->
            wantMethod("checkCastRedirects " + k, v, "cbCheckCast", "(Ljava/lang/Object;)Ljava/lang/Object;"));
        // "owner.<init>(args)V" -> factory (args)Lowner;
        obj(root, "ctorRedirects", (k, v) -> {
            Sym s = Sym.method(k);
            wantMethod("ctorRedirects " + k, v, "cbNew",
                methodDesc(s.desc.substring(0, s.desc.lastIndexOf(')') + 1) + "L" + s.owner + ";"));
        });
        if (root.has("superCtorAdapts")) {
            for (var e : root.getAsJsonObject("superCtorAdapts").entrySet()) {
                var o = e.getValue().getAsJsonObject();
                wantMethod("superCtorAdapts " + e.getKey(), o.get("conv").getAsString(), "cbConv",
                    methodDesc(o.get("convDesc").getAsString()));
            }
        }
        Map<String, String> superRen = new HashMap<>();
        if (root.has("superRenames")) {
            for (var e : root.getAsJsonObject("superRenames").entrySet()) superRen.put(e.getKey(), e.getValue().getAsString());
        }
        if (root.has("superCtorReshapes")) {
            for (var e : root.getAsJsonObject("superCtorReshapes").entrySet()) {
                var o = e.getValue().getAsJsonObject();
                if (!stub.isEmpty()) checkReshapeTarget(e.getKey(), o.get("desc").getAsString(), superRen);
                checkReshapeOps("superCtorReshapes " + e.getKey(), e.getKey(), o.getAsJsonArray("ops"));
                if (o.has("post")) {
                    // the descriptor is held to the key by the rule Main.convert enforces when the wiring
                    // loads (Chain.CtorReshape.setPost): (Lowner;<the postArgs' old argument types>)V
                    String post = o.get("post").getAsString().trim();
                    int paren = post.indexOf('(');
                    String key = e.getKey();
                    String want = null;
                    if (paren >= 0 && key.contains(".<init>(")) {
                        org.objectweb.asm.Type[] old = org.objectweb.asm.Type.getArgumentTypes(key.substring(key.indexOf('(')));
                        StringBuilder w = new StringBuilder("(L").append(key, 0, key.indexOf(".<init>")).append(';');
                        if (o.has("postArgs")) {
                            for (var a : o.getAsJsonArray("postArgs")) {
                                int i = a.getAsInt();
                                w.append(i >= 0 && i < old.length ? old[i].getDescriptor() : "?" + i);
                            }
                        }
                        want = w.append(")V").toString();
                    }
                    if (paren < 0) {
                        problems.add("superCtorReshapes " + key + ": malformed post '" + post + "'");
                    } else if (want != null && !post.substring(paren).equals(want)) {
                        problems.add("superCtorReshapes " + key + ": post '" + post + "' must have the descriptor " + want);
                    } else {
                        wantMethod("superCtorReshapes " + e.getKey() + " post", post.substring(0, paren), null,
                            methodDesc(post.substring(paren)));
                    }
                }
            }
        }
        // overrideRenames: an adapter interface must declare the new name with the old descriptor
        // (the mod's renamed method implements it; the mixin on the final method calls it)
        if (root.has("overrideRenames")) {
            for (var el : root.getAsJsonArray("overrideRenames")) {
                var o = el.getAsJsonObject();
                checked++;
                if (!o.has("adapter")) {
                    continue;
                }
                String old = o.get("old").getAsString();
                String desc = methodDesc(old.substring(old.indexOf('(')));
                String name = o.get("new").getAsString();
                String adapter = o.get("adapter").getAsString();
                ClassNode an = classes.get(adapter);
                if (an == null || (an.access & Opcodes.ACC_INTERFACE) == 0) {
                    problems.add("overrideRenames " + old + " (no such adapter interface " + adapter + ")");
                } else if (an.methods.stream().noneMatch(m -> m.name.equals(name) && m.desc.equals(desc))) {
                    problems.add("overrideRenames " + old + ": adapter " + adapter + " does not declare " + name + desc);
                }
            }
        }
        // methodRenames / fieldRenames of a Fabric API member (-Dcb.fapi): the renamed site -- the class
        // renames applied, then the wall -- must name a member the target's Fabric API declares on the
        // renamed owner or a supertype, with exactly the converter's descriptor. A rename onto a name the
        // target lacks, or one whose descriptor moved too, links as NoSuchMethodError / NoSuchFieldError.
        if (!lib.isEmpty()) {
            for (String sec : new String[] {"methodRenames", "fieldRenames"}) {
                if (!root.has(sec)) {
                    continue;
                }
                boolean field = sec.equals("fieldRenames");
                for (var e : root.getAsJsonObject(sec).entrySet()) {
                    Sym s = field ? Sym.field(e.getKey()) : Sym.method(e.getKey());
                    if (!s.owner.startsWith("net/fabricmc/")) {
                        continue;
                    }
                    checked++;
                    String owner = targetName(facade.mapType(s.owner));
                    String nn = e.getValue().getAsString();
                    if (field) {
                        String nd = typeDesc(s.desc);
                        if (!libField(owner, nn, nd)) {
                            problems.add(sec + " " + e.getKey() + ": " + owner + "." + nn + ":" + nd + " is no field of the target's Fabric API");
                        }
                    } else {
                        String nd = methodDesc(s.desc);
                        // a Fabric API class the step renames onto one of the layer's own (an API the target's
                        // Fabric API dropped, given back by the layer): the renamed member is the layer's
                        if (classes.containsKey(owner) ? !layerDeclares(owner, nn, nd) : !libMethod(owner, nn, nd)) {
                            problems.add(sec + " " + e.getKey() + ": " + owner + "." + nn + nd
                                + (classes.containsKey(owner) ? " is no method of the layer" : " is no method of the target's Fabric API"));
                        }
                    }
                }
            }
        }
        // mixinTargets: the W method a V method's code now lives in must be declared on the (renamed)
        // class itself -- Mixin looks injector targets up in the target's own member table -- and every
        // argument index must name a W argument of the V argument's (renamed) type or a subtype of it
        if (root.has("mixinTargets") && !stub.isEmpty()) {
            for (var e : root.getAsJsonObject("mixinTargets").entrySet()) {
                checked++;
                Sym s = Sym.method(e.getKey());
                var o = e.getValue().getAsJsonObject();
                String where = "mixinTargets " + e.getKey();
                if (!o.has("to") || !o.has("basis")) {
                    problems.add(where + ": needs \"to\" and \"basis\"");
                    continue;
                }
                String to = o.get("to").getAsString();
                int p = to.indexOf('(');
                if (p <= 0) {
                    problems.add(where + ": \"to\" is not name(desc)");
                    continue;
                }
                String owner = targetName(facade.mapType(s.owner));
                ClassNode cn = classes.containsKey(owner) ? classes.get(owner) : stub.get(owner);
                String toName = to.substring(0, p);
                // past the wall "to" is in the analysis namespace like every key: the name through the wall table
                String nm = wall == null || toName.startsWith("<") ? toName : wall.mapMethodName(s.owner, toName, to.substring(p));
                String nd = methodDesc(to.substring(p));
                if (cn == null || cn.methods.stream().noneMatch(m -> m.name.equals(nm) && m.desc.equals(nd))) {
                    problems.add(where + ": " + owner + " does not itself declare " + nm + nd);
                    continue;
                }
                if (!o.has("args")) {
                    continue;
                }
                var a = o.getAsJsonArray("args");
                org.objectweb.asm.Type[] va = org.objectweb.asm.Type.getArgumentTypes(methodDesc(s.desc));
                org.objectweb.asm.Type[] wa = org.objectweb.asm.Type.getArgumentTypes(nd);
                if (a.size() != va.length) {
                    problems.add(where + ": args has " + a.size() + " entries, the V method " + va.length + " arguments");
                    continue;
                }
                // "narrow": an argument W widened reaches the handler behind an instanceof test of V's type, so V's
                // type must be a subtype of W's (as for override hooks' narrow)
                boolean narrow = o.has("narrow") && o.get("narrow").getAsBoolean();
                boolean narrows = false;
                for (int i = 0; i < va.length; i++) {
                    int j = a.get(i).getAsInt();
                    boolean objects = j >= 0 && j < wa.length && wa[j].getSort() == org.objectweb.asm.Type.OBJECT
                        && va[i].getSort() == org.objectweb.asm.Type.OBJECT;
                    if (j < -1 || j >= wa.length) {
                        problems.add(where + ": args[" + i + "] = " + j + " is no argument of " + nm + nd);
                    } else if (j >= 0 && !wa[j].equals(va[i])
                            && !(objects && resolvesSuper(wa[j].getInternalName(), va[i].getInternalName()))) {
                        if (narrow && objects && resolvesSuper(va[i].getInternalName(), wa[j].getInternalName())) {
                            narrows = true;
                        } else {
                            problems.add(where + ": args[" + i + "]: W argument " + j + " (" + wa[j] + ") is not a " + va[i]
                                + (narrow ? ", nor a supertype it narrows to" : ""));
                        }
                    }
                }
                if (narrow && !narrows) {
                    problems.add(where + ": \"narrow\" but no argument W widened");
                }
            }
        }
        // callReshapes: the reshaped call must resolve on the target (the owner or a supertype
        // declares the new shape), and every converter / supplier it runs must exist
        if (root.has("callReshapes")) {
            for (var e : root.getAsJsonObject("callReshapes").entrySet()) {
                var o = e.getValue().getAsJsonObject();
                String where = "callReshapes " + e.getKey();
                checked++;
                if (!stub.isEmpty()) {
                    Sym s = Sym.method(e.getKey());
                    String owner = targetName(facade.mapType(s.owner));
                    String nd = methodDesc(o.get("desc").getAsString());
                    String name = wall == null ? s.name : wall.mapMethodName(s.owner, s.name, s.desc);
                    if (!resolves(owner, name, nd)) {
                        problems.add(where + ": " + owner + "." + name + nd + " does not resolve on the target");
                    }
                }
                checkReshapeOps(where, e.getKey(), o.getAsJsonArray("ops"));
                if (o.has("ret")) {
                    String r = o.get("ret").getAsString().trim();
                    int sp = r.indexOf(' ');
                    if (r.equals("keep")) {
                        org.objectweb.asm.Type or = org.objectweb.asm.Type.getReturnType(e.getKey().substring(e.getKey().indexOf('(')));
                        org.objectweb.asm.Type nr = org.objectweb.asm.Type.getReturnType(o.get("desc").getAsString());
                        if (!stub.isEmpty() && or.getSort() == org.objectweb.asm.Type.OBJECT && nr.getSort() == org.objectweb.asm.Type.OBJECT) {
                            String from = targetName(facade.mapType(nr.getInternalName()));
                            String to = targetName(facade.mapType(or.getInternalName()));
                            if (!to.equals("java/lang/Object") && !isSubtype(from, to)) {
                                problems.add(where + ": ret keep: " + from + " is not a subtype of " + to + " on the target");
                            }
                        }
                    } else if (!r.equals("pop")) {
                        String rest = sp < 0 ? "" : r.substring(sp + 1).trim();
                        int paren = rest.indexOf('(');
                        if (paren < 0) {
                            problems.add(where + ": malformed ret '" + r + "'");
                        } else {
                            wantMethod(where + " ret", rest.substring(0, paren), null, methodDesc(rest.substring(paren)));
                        }
                    }
                }
            }
        }
        if (root.has("lookupRt")) {
            String desc = root.has("lookupDesc") ? root.get("lookupDesc").getAsString()
                                                  : "()Lnet/minecraft/class_7225$class_7874;";
            wantMethod("lookupRt", root.get("lookupRt").getAsString(), "cbLookup", methodDesc(desc));
        }
        // returnConverters: both converters exist with their exact descriptors, toV and toW undo each other's
        // types (toV: W's answer first, toW: V's answer first, each returning the other), and a basis is given
        if (root.has("returnConverters")) {
            for (var e : root.getAsJsonObject("returnConverters").entrySet()) {
                checked++;
                var o = e.getValue().getAsJsonObject();
                String where = "returnConverters " + e.getKey();
                if (!o.has("toV") || !o.has("toW") || !o.has("basis")) {
                    problems.add(where + ": needs \"toV\", \"toW\" and \"basis\"");
                    continue;
                }
                String tv = o.get("toV").getAsString().trim();
                String tw = o.get("toW").getAsString().trim();
                int pv = tv.indexOf('(');
                int pw = tw.indexOf('(');
                if (pv <= 0 || pw <= 0) {
                    problems.add(where + ": a converter is not OWNER#name(desc)");
                    continue;
                }
                wantMethod(where + " toV", tv.substring(0, pv), null, methodDesc(tv.substring(pv)));
                wantMethod(where + " toW", tw.substring(0, pw), null, methodDesc(tw.substring(pw)));
                org.objectweb.asm.Type[] av = org.objectweb.asm.Type.getArgumentTypes(tv.substring(pv));
                org.objectweb.asm.Type[] aw = org.objectweb.asm.Type.getArgumentTypes(tw.substring(pw));
                org.objectweb.asm.Type rv = org.objectweb.asm.Type.getReturnType(tv.substring(pv));
                org.objectweb.asm.Type rw = org.objectweb.asm.Type.getReturnType(tw.substring(pw));
                int with = o.has("with") ? o.getAsJsonArray("with").size() : 0;
                if (av.length != 1 + with || aw.length != 1 + with || !av[0].equals(rw) || !aw[0].equals(rv)) {
                    problems.add(where + ": toV must be (W answer, with..)V answer and toW (V answer, with..)W answer");
                }
            }
        }
        // splitRenames: X -> facade F with toW (F)X and toV (X)F, both existing with their exact descriptors, and a basis
        if (root.has("splitRenames")) {
            for (var e : root.getAsJsonObject("splitRenames").entrySet()) {
                checked++;
                var o = e.getValue().getAsJsonObject();
                String where = "splitRenames " + e.getKey();
                if (!o.has("facade") || !o.has("toW") || !o.has("toV") || !o.has("basis")) {
                    problems.add(where + ": needs \"facade\", \"toW\", \"toV\" and \"basis\"");
                    continue;
                }
                String f = o.get("facade").getAsString();
                String tw = o.get("toW").getAsString().trim();
                String tv = o.get("toV").getAsString().trim();
                int pw = tw.indexOf('(');
                int pv = tv.indexOf('(');
                if (pv <= 0 || pw <= 0) {
                    problems.add(where + ": a converter is not OWNER#name(desc)");
                    continue;
                }
                wantMethod(where + " toW", tw.substring(0, pw), null, tw.substring(pw));
                wantMethod(where + " toV", tv.substring(0, pv), null, tv.substring(pv));
                String x = e.getKey();
                if (!tw.substring(pw).equals("(L" + f + ";)L" + x + ";") || !tv.substring(pv).equals("(L" + x + ";)L" + f + ";")) {
                    problems.add(where + ": toW must be (L" + f + ";)L" + x + "; and toV (L" + x + ";)L" + f + ";");
                }
            }
        }
        // mixinReceivers: "V>W" with toV (W)V and toW (V)W, both existing with their exact descriptors, and a basis
        if (root.has("mixinReceivers")) {
            for (var e : root.getAsJsonObject("mixinReceivers").entrySet()) {
                checked++;
                var o = e.getValue().getAsJsonObject();
                String where = "mixinReceivers " + e.getKey();
                String[] k = e.getKey().split(">");
                if (k.length != 2 || !o.has("toV") || !o.has("toW") || !o.has("basis")) {
                    problems.add(where + ": key \"V owner>W owner\" with \"toV\", \"toW\" and \"basis\"");
                    continue;
                }
                String tv = o.get("toV").getAsString().trim();
                String tw = o.get("toW").getAsString().trim();
                int pv = tv.indexOf('(');
                int pw = tw.indexOf('(');
                if (pv <= 0 || pw <= 0) {
                    problems.add(where + ": a converter is not OWNER#name(desc)");
                    continue;
                }
                wantMethod(where + " toV", tv.substring(0, pv), null, methodDesc(tv.substring(pv)));
                wantMethod(where + " toW", tw.substring(0, pw), null, methodDesc(tw.substring(pw)));
                if (!tv.substring(pv).equals("(L" + k[1] + ";)L" + k[0] + ";") || !tw.substring(pw).equals("(L" + k[0] + ";)L" + k[1] + ";")) {
                    problems.add(where + ": toV must be (L" + k[1] + ";)L" + k[0] + "; and toW (L" + k[0] + ";)L" + k[1] + ";");
                }
            }
        }
        // convertHooks (the override side of a same-shape reshape): the member is one the target's
        // `under` has with that very descriptor, and every converter exists with its exact descriptor
        java.util.Set<String> convertHooked = new java.util.HashSet<>();
        if (root.has("convertHooks")) {
            for (var el : root.getAsJsonArray("convertHooks")) {
                var o = el.getAsJsonObject();
                String under = o.get("under").getAsString();
                String member = o.get("member").getAsString();
                String where = "convertHooks " + under + "." + member;
                checked++;
                convertHooked.add(member);
                int paren = member.indexOf('(');
                if (paren <= 0) {
                    problems.add(where + ": member must be name(desc)");
                    continue;
                }
                if (!stub.isEmpty()) {
                    String owner = targetName(facade.mapType(under));
                    String name = postWallName(under, member.substring(0, paren), member.substring(paren));
                    if (!resolves(owner, name, methodDesc(member.substring(paren)))) {
                        problems.add(where + ": " + owner + "." + name + " is not a member of the target with that descriptor");
                    }
                }
                java.util.List<String> convs = new java.util.ArrayList<>();
                if (o.has("args")) {
                    for (var a : o.getAsJsonArray("args")) {
                        if (!a.isJsonNull()) {
                            String r = a.getAsString().trim();
                            int p = r.indexOf('(');
                            wantMethod(where + " arg", r.substring(0, p), null, methodDesc(r.substring(p)));
                            convs.add(r);
                        }
                    }
                }
                if (o.has("ret") && !o.get("ret").isJsonNull()) {
                    String r = o.get("ret").getAsString().trim();
                    int p = r.indexOf('(');
                    wantMethod(where + " ret", r.substring(0, p), null, methodDesc(r.substring(p)));
                    convs.add(r);
                }
                // a converter taking the mod object first gets `this` of a class under `under`
                for (String r : convs) {
                    org.objectweb.asm.Type[] cp = org.objectweb.asm.Type.getArgumentTypes(r.substring(r.indexOf('(')));
                    if (cp.length == 2 && !stub.isEmpty() && cp[0].getSort() == org.objectweb.asm.Type.OBJECT) {
                        String recv = targetName(facade.mapType(cp[0].getInternalName()));
                        String self = targetName(facade.mapType(under));
                        if (!recv.equals("java/lang/Object") && !isSubtype(self, recv)) {
                            problems.add(where + ": converter " + r + " takes a " + recv + ", which " + self + " is not");
                        }
                    }
                }
            }
        }
        // a same-shape call reshape converts calls naming a mod class that overrides the member too
        // (the engine's call side climbs past the mod's declaration): without the override side
        // that mod's own call would hand its body converted values. So a reshape keeping the
        // descriptor of a member a mod can override needs its convertHooks entry
        if (root.has("callReshapes") && !stub.isEmpty()) {
            for (var e : root.getAsJsonObject("callReshapes").entrySet()) {
                String k = e.getKey();
                String od = k.substring(k.indexOf('('));
                var ro = e.getValue().getAsJsonObject();
                java.util.List<String> ops = new java.util.ArrayList<>();
                ro.getAsJsonArray("ops").forEach(x -> ops.add(x.getAsString()));
                top.dext.centurybridge.engine.Chain.CallReshape parsed = top.dext.centurybridge.engine.Chain.parseCallReshape(
                    k, ro.get("desc").getAsString(), ops, ro.has("ret") ? ro.get("ret").getAsString() : null);
                if (!parsed.newDesc.equals(od) || !parsed.converts()) {
                    continue; // a shape change, or the same values passed on unchanged
                }
                Sym s = Sym.method(k);
                ClassNode owner = stub.get(targetName(facade.mapType(s.owner)));
                if (owner == null || (owner.access & Opcodes.ACC_FINAL) != 0) {
                    continue;
                }
                String name = postWallName(s.owner, s.name, od);
                String desc = methodDesc(od);
                MethodNode m = owner.methods.stream().filter(x -> x.name.equals(name) && x.desc.equals(desc)).findFirst().orElse(null);
                if (m != null && (m.access & (Opcodes.ACC_STATIC | Opcodes.ACC_FINAL | Opcodes.ACC_PRIVATE)) == 0
                        && (genericOverrides == null || genericOverrides.contains(k))
                        && !convertHooked.contains(s.name + od)
                        && !(root.has("ledger") && root.getAsJsonObject("ledger").has(k))) {
                    problems.add("callReshapes " + k + ": keeps the descriptor of an overridable member but has no convertHooks"
                        + " entry for its overrides (or a ledger reason)");
                }
            }
        }
        // adapterHooks: the bridge interface must declare every required old shape
        // exactly (the mod's own methods implement it by name+descriptor), and every
        // synthesized new shape calls a static that exists with the exact descriptor
        // abstractFills: the static a synthesized implementation calls, with the exact descriptor
        if (root.has("abstractFills")) {
            for (var el : root.getAsJsonArray("abstractFills")) {
                var o = el.getAsJsonObject();
                String call = o.get("call").getAsString();
                int paren = call.indexOf('(');
                int dot = call.lastIndexOf('.', paren);
                wantMethod("abstractFills " + o.get("under").getAsString() + "." + o.get("method").getAsString(),
                    call.substring(0, dot) + "#" + call.substring(dot + 1, paren), null, methodDesc(call.substring(paren)));
            }
        }
        if (root.has("adapterHooks")) {
            for (var el : root.getAsJsonArray("adapterHooks")) {
                var o = el.getAsJsonObject();
                String adapter = o.get("adapter").getAsString();
                String where = "adapterHooks " + adapter;
                checked++;
                ClassNode an = classes.get(adapter);
                if (an == null || (an.access & Opcodes.ACC_INTERFACE) == 0) {
                    problems.add(where + " (no such bridge interface)");
                } else {
                    String under = o.get("under").getAsString();
                    for (var r : o.getAsJsonArray("requires")) {
                        String shape = r.getAsString();
                        int paren = shape.indexOf('(');
                        String name = postWallName(under, shape.substring(0, paren), shape.substring(paren));
                        String desc = methodDesc(shape.substring(paren));
                        boolean declared = an.methods.stream().anyMatch(m -> m.name.equals(name) && m.desc.equals(desc)
                            && (m.access & Opcodes.ACC_ABSTRACT) != 0);
                        if (!declared) {
                            problems.add(where + " does not declare the required shape " + name + desc);
                        }
                    }
                }
                if (o.has("guard")) {
                    String g = o.get("guard").getAsString();
                    wantMethod(where + " guard", g + "#cbEnter", null, "(Ljava/lang/Object;)Z");
                    wantMethod(where + " guard", g + "#cbExit", null, "(Ljava/lang/Object;)V");
                }
                for (var sy : o.getAsJsonArray("synth")) {
                    String call = sy.getAsJsonObject().get("call").getAsString();
                    int paren = call.indexOf('(');
                    int dot = call.lastIndexOf('.', paren);
                    wantMethod(where + " synth", call.substring(0, dot) + "#" + call.substring(dot + 1, paren),
                        null, methodDesc(call.substring(paren)));
                }
            }
        }
        if (root.has("lambdaAdapters")) {
            // a lambda is spun as the CB interface: it must declare exactly the old SAM (any other
            // abstract method would be left unimplemented), and the adapter must turn it into the
            // vanilla interface with exactly the descriptor the converter emits
            for (var e : root.getAsJsonObject("lambdaAdapters").entrySet()) {
                String where = "lambdaAdapters " + e.getKey();
                var o = e.getValue().getAsJsonObject();
                top.dext.centurybridge.engine.Chain.LambdaAdapter la;
                try {
                    la = top.dext.centurybridge.engine.Chain.parseLambdaAdapter(e.getKey(),
                        o.get("iface").getAsString(), o.get("adapt").getAsString());
                } catch (IllegalArgumentException ex) {
                    problems.add(ex.getMessage());
                    continue;
                }
                checked++;
                ClassNode legacy = classes.get(la.legacy);
                String samDesc = methodDesc(la.samDesc);
                if (legacy == null || (legacy.access & Opcodes.ACC_INTERFACE) == 0) {
                    problems.add(where + ": " + la.legacy + " is not an interface of the bridge");
                } else {
                    List<String> abstracts = new ArrayList<>();
                    for (MethodNode m : legacy.methods) {
                        if ((m.access & Opcodes.ACC_ABSTRACT) != 0) {
                            abstracts.add(m.name + m.desc);
                        }
                    }
                    if (!abstracts.equals(List.of(la.sam + samDesc))) {
                        problems.add(where + ": " + la.legacy + " must declare exactly the old SAM " + la.sam + samDesc
                            + ", declares " + abstracts);
                    }
                }
                wantMethod(where + " adapt", la.adaptOwner + "#" + la.adaptName, null, methodDesc(la.adaptDesc));
            }
        }
        if (root.has("overrideHooks")) {
            for (var el : root.getAsJsonArray("overrideHooks")) {
                var o = el.getAsJsonObject();
                String where = "overrideHooks " + o.get("old").getAsString();
                for (String key : new String[] {"wrap", "unwrap"}) {
                    if (!o.has(key)) {
                        continue;
                    }
                    // "owner.name(desc)R", or (wrap) a list of them
                    List<String> ws = new ArrayList<>();
                    if (o.get(key).isJsonArray()) {
                        o.getAsJsonArray(key).forEach(x -> ws.add(x.getAsString()));
                    } else {
                        ws.add(o.get(key).getAsString());
                    }
                    for (String w : ws) {
                        int paren = w.indexOf('(');
                        int dot = w.lastIndexOf('.', paren);
                        wantMethod(where + " " + key, w.substring(0, dot) + "#" + w.substring(dot + 1, paren),
                            null, methodDesc(w.substring(paren)));
                    }
                }
                if (o.has("guard")) {
                    String g = o.get("guard").getAsString();
                    wantMethod(where + " guard", g + "#cbEnter", null, "(Ljava/lang/Object;)Z");
                    wantMethod(where + " guard", g + "#cbExit", null, "(Ljava/lang/Object;)V");
                }
                if (o.has("frames")) {
                    // carried arguments: the synthesized new shape pushes / pops a frame, a rewritten
                    // super call reads an argument back with the accessor of its type
                    String f = o.get("frames").getAsString();
                    wantMethod(where + " frames", f + "#cbPush", null, "(Ljava/lang/Object;[Ljava/lang/Object;)V");
                    wantMethod(where + " frames", f + "#cbPop", null, "()V");
                    for (String t : new String[] {"Z", "B", "C", "S", "I", "J", "F", "D"}) {
                        wantMethod(where + " frames", f + "#cbArg" + t, null, "(Ljava/lang/Object;I)" + t);
                    }
                    wantMethod(where + " frames", f + "#cbArgL", null, "(Ljava/lang/Object;I)Ljava/lang/Object;");
                    if (root.has("frameDefaults") && root.getAsJsonObject("frameDefaults").has(o.get("old").getAsString())) {
                        // a default answered when no frame holds the object: the frames class reads it
                        // through cbArgOr*, and the static it names must exist exactly
                        for (String t : new String[] {"Z", "B", "C", "S", "I", "J", "F", "D"}) {
                            wantMethod(where + " frames", f + "#cbArgOr" + t, null, "(Ljava/lang/Object;I" + t + ")" + t);
                        }
                        wantMethod(where + " frames", f + "#cbArgOrL", null,
                            "(Ljava/lang/Object;ILjava/lang/Object;)Ljava/lang/Object;");
                        for (var d : root.getAsJsonObject("frameDefaults").getAsJsonObject(o.get("old").getAsString()).entrySet()) {
                            String r = d.getValue().getAsString().trim();
                            if (r.startsWith("zero:")) {
                                continue;
                            }
                            int p = r.indexOf('(');
                            wantMethod(where + " frameDefaults " + d.getKey(), r.substring(0, p), null, methodDesc(r.substring(p)));
                        }
                    }
                }
                if (o.has("narrow") && o.get("narrow").getAsBoolean()) {
                    // a narrowing forward casts vanilla's value to the old type after an instanceof
                    // test: every argument it narrows must be one the new shape could be handed
                    String old = o.get("old").getAsString();
                    String nw = o.get("new").getAsString();
                    org.objectweb.asm.Type[] oa = org.objectweb.asm.Type.getArgumentTypes(old.substring(old.indexOf('(')));
                    org.objectweb.asm.Type[] na = org.objectweb.asm.Type.getArgumentTypes(nw.substring(nw.indexOf('(')));
                    int[] map = new int[oa.length];
                    for (int i = 0; i < oa.length; i++) {
                        map[i] = o.has("args") ? o.getAsJsonArray("args").get(i).getAsInt() : i;
                    }
                    for (int i = 0; i < oa.length; i++) {
                        if (map[i] >= na.length) {
                            problems.add(where + " narrow: argument " + i + " maps past the new shape");
                            continue;
                        }
                        org.objectweb.asm.Type from = oa[i];
                        org.objectweb.asm.Type to = na[map[i]];
                        if (from.equals(to)) {
                            continue;
                        }
                        // both sides as the converted class names them: the converter applies the facade
                        // classRenames to the whole class, the synthesized narrowing test included
                        if (from.getSort() != org.objectweb.asm.Type.OBJECT || to.getSort() != org.objectweb.asm.Type.OBJECT
                                || !isSubtype(convertedName(from.getInternalName()), convertedName(to.getInternalName()))) {
                            problems.add(where + " narrow: " + from.getDescriptor() + " is not a subtype of "
                                + to.getDescriptor() + " on the target");
                        }
                        checked++;
                    }
                }
                if (o.has("bridge")) {
                    // bridge mode: the static the synthesized new shape calls, and the adapter
                    // interface (declaring the old shape) the bridge reaches the override by
                    String b = o.get("bridge").getAsString();
                    int paren = b.indexOf('(');
                    int dot = b.lastIndexOf('.', paren);
                    wantMethod(where + " bridge", b.substring(0, dot) + "#" + b.substring(dot + 1, paren),
                        null, methodDesc(b.substring(paren)));
                    String adapter = o.has("adapter") ? o.get("adapter").getAsString() : null;
                    ClassNode an = adapter == null ? null : classes.get(adapter);
                    String old = o.get("old").getAsString();
                    int op = old.indexOf('(');
                    // the mod's override carries the name the namespace wall gives it
                    String oname = postWallName(o.has("onlyUnder") ? o.get("onlyUnder").getAsString() : null,
                        old.substring(0, op), old.substring(op));
                    String odesc = methodDesc(old.substring(op));
                    if (an == null || (an.access & Opcodes.ACC_INTERFACE) == 0) {
                        problems.add(where + " bridge without an adapter interface");
                    } else if (an.methods.stream().noneMatch(m -> m.name.equals(oname) && m.desc.equals(odesc))) {
                        problems.add(where + " adapter " + adapter + " does not declare the old shape " + oname + odesc);
                    }
                }
                if (o.has("superBridge")) {
                    // super calls through a bridge: the static, and the interface the class gets with its
                    // accessors -- it must declare cb$super$<shape> for every accessor shape, nothing else
                    String b = o.get("superBridge").getAsString();
                    int paren = b.indexOf('(');
                    int dot = b.lastIndexOf('.', paren);
                    wantMethod(where + " superBridge", b.substring(0, dot) + "#" + b.substring(dot + 1, paren),
                        null, methodDesc(b.substring(paren)));
                    String access = o.has("superAccess") ? o.get("superAccess").getAsString() : null;
                    ClassNode sn = access == null ? null : classes.get(access);
                    if (sn == null || (sn.access & Opcodes.ACC_INTERFACE) == 0) {
                        problems.add(where + " superBridge without a superAccess interface");
                    } else {
                        java.util.Set<String> want = new java.util.HashSet<>();
                        if (o.has("superAccessors")) {
                            for (var a : o.getAsJsonArray("superAccessors")) {
                                String s = a.getAsString();
                                int p = s.indexOf('(');
                                want.add(top.dext.centurybridge.engine.Chain.SUPER_ACCESSOR_PREFIX + s.substring(0, p)
                                    + methodDesc(s.substring(p)));
                            }
                        }
                        java.util.Set<String> have = new java.util.HashSet<>();
                        for (var m : sn.methods) {
                            if ((m.access & Opcodes.ACC_ABSTRACT) != 0) {
                                have.add(m.name + m.desc);
                            }
                        }
                        if (!have.equals(want)) {
                            problems.add(where + " superAccess " + access + " declares " + have + ", the accessors are " + want);
                        }
                        checked++;
                    }
                }
            }
        }
    }

    /**
     * The name a mod's override of {@code owner.name} has in the converted jar: past the
     * wall the whole-jar rename gives an intermediary method id its mojang name whatever
     * the descriptor (an old-shape readNbt override becomes loadAdditional(CompoundTag)),
     * so an adapter interface must declare that name.
     */
    private String postWallName(String owner, String name, String rawDesc) {
        return wall == null || owner == null ? name : wall.mapMethodName(owner, name, rawDesc);
    }

    /** the converter's view of a descriptor: facade renames, then the namespace wall */
    /**
     * A superRename moves only the extends/implements clause (and super calls) onto
     * the facade; every other reference keeps the original type. That is sound only
     * while the original type survives on the target AND the facade is one of its
     * subtypes. A key that is gone leaves NEW sites, casts and field/method types
     * naming a missing class (classRenames is the tool); a facade that is not a
     * subtype of the surviving type makes the mod's own instances fail verification
     * wherever they meet a slot of that type -- always the case for a final class.
     */
    private void checkSuperRename(String key, String fac) {
        // A key classRenames also sends to a surviving class -- a dead class merged into a vanilla one
        // (1.20.5: DustParticleOptionsBase into ScalableParticleOptionsBase), its mod subclasses keeping
        // the old fields and constructor on a facade below that class -- survives as that class: every
        // reference but the hierarchy names it (the converter's superRename layer runs before its
        // classRename remapper), so it is the type the facade must be a subtype of.
        String merged = facade.map(key);
        String kt = targetName(merged != null && !merged.startsWith("top/dext/") ? merged : key);
        if ("java/lang/Object".equals(kt)) {
            // merged into Object (1.21.2: Equipable, whose 1.21.1 instances were items and blocks alike): every
            // facade is one, and the stub (Minecraft only) does not carry it
            return;
        }
        // a Fabric API class surviving on the target (0.116's FabricRecipeProvider on 0.106's, -Dcb.fapi) is judged
        // like a Minecraft one
        ClassNode kn = stub.containsKey(kt) ? stub.get(kt) : gameLibs.containsKey(kt) ? gameLibs.get(kt) : lib.get(kt);
        if (kn == null && gameLibs.isEmpty() && !top.dext.centurybridge.engine.Chain.minecraftOwned(kt) && !lib.containsKey(kt)) {
            // a game library's class (authlib's PropertyMap) with no -Dcb.libs: the stub (Minecraft only) does not
            // carry it, so only the subtype half is judged, through the facade's own compiled supertypes
            if (classes.containsKey(fac) && !isSubtype(fac, kt)) {
                problems.add("superRename " + key + " -> " + fac + ": not a subtype of the library class " + kt
                    + " -- a mod instance reaching a " + kt + "-typed slot fails verification");
            }
            return;
        }
        if (kn == null) {
            problems.add("superRename " + key + ": the class is gone on the target -- superRenames only move"
                + " extends/implements, so NEW sites, casts and field/method types keep naming it; use classRenames");
        } else if (classes.containsKey(fac) && !isSubtype(fac, kt)) {
            boolean fin = (kn.access & org.objectweb.asm.Opcodes.ACC_FINAL) != 0;
            problems.add("superRename " + key + " -> " + fac + ": not a subtype of the surviving " + kt
                + (fin ? " (final on the target, so no facade can be)" : "")
                + " -- a mod instance reaching a " + kt + "-typed slot fails verification");
        }
    }

    /** the reshaped super call lands on the facade (superRenamed owner) or the renamed owner; the ctor must exist there */
    private void checkReshapeTarget(String key, String reshapedDesc, Map<String, String> superRen) {
        Sym s = Sym.method(key);
        String tc = superRen.containsKey(s.owner) ? superRen.get(s.owner) : targetName(facade.mapType(s.owner));
        String nd = methodDesc(reshapedDesc);
        ClassNode tn = classes.containsKey(tc) ? classes.get(tc) : stub.get(tc);
        if (tn == null) {
            if (tc.startsWith("net/minecraft/")) { // library owners (DFU...) are not in the game stub
                problems.add("superCtorReshapes " + key + ": reshaped call targets " + tc + ", which does not exist");
            }
        } else if (tn.methods.stream().noneMatch(m -> m.name.equals("<init>") && m.desc.equals(nd))) {
            problems.add("superCtorReshapes " + key + ": " + tc + ".<init>" + nd + " is not a constructor there");
        }
    }

    /**
     * The reshape ops' runtime references: every converter / supplier static exists with the exact
     * descriptor, and a typed keep ("keep:N Ltype;": the old argument passed where the new shape
     * declares a supertype of it) really names a supertype of old argument N.
     */
    private void checkReshapeOps(String where, String key, com.google.gson.JsonArray ops) {
        org.objectweb.asm.Type[] old = org.objectweb.asm.Type.getArgumentTypes(key.substring(key.indexOf('(')));
        for (var op : ops) {
            String t = op.getAsString().trim();
            int sp = t.indexOf(' ');
            if (sp < 0) {
                continue; // keep / drop
            }
            String head = t.substring(0, sp);
            String rest = t.substring(sp + 1).trim();
            if (head.equals("keep") || head.startsWith("keep:")) {
                if (!head.startsWith("keep:")) {
                    problems.add(where + ": typed op '" + t + "' needs an explicit index (keep:N <type>)");
                    continue;
                }
                int i = Integer.parseInt(head.substring(5));
                org.objectweb.asm.Type want = org.objectweb.asm.Type.getType(rest);
                if (i >= old.length || old[i].getSort() != org.objectweb.asm.Type.OBJECT || want.getSort() != org.objectweb.asm.Type.OBJECT) {
                    problems.add(where + ": typed op '" + t + "' passes no object argument");
                } else if (!stub.isEmpty()) {
                    String from = targetName(facade.mapType(old[i].getInternalName()));
                    String to = targetName(facade.mapType(want.getInternalName()));
                    if (!to.equals("java/lang/Object") && !isSubtype(from, to)) {
                        problems.add(where + ": op '" + t + "': " + from + " is not a subtype of " + to + " on the target");
                    }
                }
                continue;
            }
            int as = rest.indexOf(" as ");   // "conv ... as <type>": the static answers a supertype
            if (as >= 0) {
                rest = rest.substring(0, as).trim();
            }
            int paren = rest.indexOf('(');   // OWNER#name(desc)R
            if (paren < 0) {
                problems.add(where + ": malformed op '" + t + "'");
                continue;
            }
            wantMethod(where + " op '" + t + "'", rest.substring(0, paren), null, methodDesc(rest.substring(paren)));
        }
    }

    /** whether name+desc is declared on the owner or one of its supertypes (shim tree or target stub) */
    /** whether sub is sup or extends / implements it on the target (stub and compiled classes) */
    private boolean resolvesSuper(String sub, String sup) {
        java.util.Deque<String> todo = new java.util.ArrayDeque<>(List.of(sub));
        java.util.Set<String> seen = new java.util.HashSet<>();
        while (!todo.isEmpty()) {
            String c = todo.pop();
            if (!seen.add(c)) continue;
            if (c.equals(sup) || sup.equals("java/lang/Object")) return true;
            ClassNode n = classes.containsKey(c) ? classes.get(c) : stub.get(c);
            if (n == null) continue;
            if (n.superName != null) todo.push(n.superName);
            todo.addAll(n.interfaces);
        }
        return false;
    }

    private boolean resolves(String owner, String name, String desc) {
        java.util.Deque<String> todo = new java.util.ArrayDeque<>(List.of(owner));
        java.util.Set<String> seen = new java.util.HashSet<>();
        // library types reached through an interface the target's Fabric API injects into a vanilla class: judged
        // by their own members (a library owner named directly stays outside this gate, as before)
        java.util.Set<String> viaInjection = new java.util.HashSet<>();
        while (!todo.isEmpty()) {
            String c = todo.pop();
            if (!seen.add(c)) continue;
            ClassNode n = classes.containsKey(c) ? classes.get(c) : stub.get(c);
            if (n == null && viaInjection.contains(c)) {
                n = lib.get(c);
                if (n == null) {
                    return true; // an injected interface whose module is not here: not this gate's to judge
                }
                for (MethodNode m : n.methods) {
                    if (m.name.equals(name) && m.desc.equals(desc)) return true;
                }
                viaInjection.addAll(n.interfaces);
                todo.addAll(n.interfaces);
                continue;
            }
            if (n == null) {
                if (!c.startsWith("net/minecraft/") && !c.startsWith("com/mojang/blaze3d/")) {
                    return true; // a library owner the stub does not carry: not this gate's to judge
                }
                continue;
            }
            for (MethodNode m : n.methods) {
                if (m.name.equals(name) && m.desc.equals(desc)) return true;
            }
            if (n.superName != null) todo.push(n.superName);
            todo.addAll(n.interfaces);
            for (String i : injected.getOrDefault(c, List.of())) {
                viaInjection.add(i);
                todo.add(i);
            }
        }
        return false;
    }

    /** whether the target's Fabric API (or the game) declares name+desc on owner or a supertype */
    /** the layer's compiled class {@code owner}, or a supertype the layer compiled, declares name+desc */
    private boolean layerDeclares(String owner, String name, String desc) {
        java.util.ArrayDeque<String> todo = new java.util.ArrayDeque<>(List.of(owner));
        java.util.Set<String> seen = new java.util.HashSet<>();
        while (!todo.isEmpty()) {
            ClassNode cn = classes.get(todo.poll());
            if (cn == null || !seen.add(cn.name)) {
                continue;
            }
            for (org.objectweb.asm.tree.MethodNode m : cn.methods) {
                if (m.name.equals(name) && m.desc.equals(desc)) {
                    return true;
                }
            }
            if (cn.superName != null) {
                todo.add(cn.superName);
            }
            if (cn.interfaces != null) {
                todo.addAll(cn.interfaces);
            }
        }
        return false;
    }

    private boolean libMethod(String owner, String name, String desc) {
        return libMember(owner, n -> n.methods.stream().anyMatch(m -> m.name.equals(name) && m.desc.equals(desc)));
    }

    private boolean libField(String owner, String name, String desc) {
        return libMember(owner, n -> n.fields.stream().anyMatch(f -> f.name.equals(name) && f.desc.equals(desc)));
    }

    private boolean libMember(String owner, java.util.function.Predicate<ClassNode> has) {
        java.util.Deque<String> todo = new java.util.ArrayDeque<>(List.of(owner));
        java.util.Set<String> seen = new java.util.HashSet<>();
        while (!todo.isEmpty()) {
            String c = todo.pop();
            if (!seen.add(c)) continue;
            ClassNode n = lib.containsKey(c) ? lib.get(c) : stub.get(c);
            if (n == null) continue;
            if (has.test(n)) return true;
            if (n.superName != null) todo.push(n.superName);
            todo.addAll(n.interfaces);
        }
        return false;
    }

    /** a type as the converted class names it: through the facade renames, then (a vanilla type) the wall */
    private String convertedName(String internalName) {
        String f = facade.map(internalName); // null when the type is not renamed
        if (f == null) {
            f = internalName;
        }
        return f.startsWith("top/dext/") ? f : targetName(f);
    }

    private String targetName(String internalName) {
        return wall == null ? internalName : wall.mapType(internalName); // map() is null when unmapped
    }

    private boolean isSubtype(String sub, String sup) {
        if (sup.equals("java/lang/Object")) {
            // every class is; the walk below could not prove it for a class whose supertypes leave the
            // stub (PacketByteBuf extends netty's ByteBuf)
            return true;
        }
        java.util.Deque<String> todo = new java.util.ArrayDeque<>(List.of(sub));
        java.util.Set<String> seen = new java.util.HashSet<>();
        while (!todo.isEmpty()) {
            String c = todo.pop();
            if (c.equals(sup)) return true;
            if (!seen.add(c)) continue;
            ClassNode n = classes.containsKey(c) ? classes.get(c) : stub.containsKey(c) ? stub.get(c) : gameLibs.get(c);
            if (n == null) {
                // a JDK type (java.util.List where a reshape passes it as a Collection) is
                // judged by the JDK itself; the game stub does not carry it
                if (c.startsWith("java/") && sup.startsWith("java/")) {
                    try {
                        ClassLoader platform = ClassLoader.getPlatformClassLoader();
                        if (Class.forName(sup.replace('/', '.'), false, platform)
                                .isAssignableFrom(Class.forName(c.replace('/', '.'), false, platform))) {
                            return true;
                        }
                    } catch (ClassNotFoundException e) {
                        // not a JDK class after all: undecided, as before
                    }
                }
                continue;
            }
            if (n.superName != null) todo.push(n.superName);
            todo.addAll(n.interfaces);
            todo.addAll(injected.getOrDefault(c, List.of())); // a vanilla class is its injected interfaces' subtype
        }
        return false;
    }

    private String methodDesc(String desc) {
        String d = facade.mapMethodDesc(desc);
        return wall == null ? d : wall.mapMethodDesc(d);
    }

    private String typeDesc(String desc) {
        String d = facade.mapDesc(desc);
        return wall == null ? d : wall.mapDesc(d);
    }

    /** "cls[#member]": the member name defaults to the call-site name the converter keeps */
    private void wantMethod(String where, String target, String defaultName, String desc) {
        checked++;
        int hash = target.indexOf('#');
        String cls = hash < 0 ? target : target.substring(0, hash);
        String name = hash < 0 ? defaultName : target.substring(hash + 1);
        ClassNode cn = classes.get(cls);
        if (cn == null && stub.containsKey(cls)) {
            cn = stub.get(cls); // a vanilla owner: see wantField
            if (wall != null && hash < 0 && name != null) name = wall.mapMethodName(cls, name, desc);
        }
        boolean dep = false;
        if (cn == null && deps.containsKey(cls)) {
            cn = deps.get(cls); // a dependency mod's class
            dep = true;
        }
        if (cn == null) {
            problems.add(where + " -> " + target + " (no such shim class)");
            return;
        }
        if (dep && (cn.access & Opcodes.ACC_PUBLIC) == 0) {
            problems.add(where + " -> " + cls + " (dependency mod class not public)");
            return;
        }
        if (name == null) {
            problems.add(where + " -> " + target + " (needs cls#member)");
            return;
        }
        List<String> sameName = new ArrayList<>();
        for (MethodNode m : cn.methods) {
            if (!m.name.equals(name)) {
                continue;
            }
            if (m.desc.equals(desc)) {
                if ((m.access & Opcodes.ACC_STATIC) == 0) {
                    problems.add(where + " -> " + cls + "." + name + desc + " (not static)");
                } else if (dep && (m.access & Opcodes.ACC_PUBLIC) == 0) {
                    problems.add(where + " -> " + cls + "." + name + desc + " (dependency mod member not public)");
                }
                return;
            }
            sameName.add(m.desc);
        }
        problems.add(where + " -> " + cls + "." + name + desc + (sameName.isEmpty()
            ? " (member missing)" : " (no such overload; declared: " + String.join(" ", sameName) + ")"));
    }

    private void wantField(String where, String target, String defaultName, String desc) {
        checked++;
        int hash = target.indexOf('#');
        String cls = hash < 0 ? target : target.substring(0, hash);
        String name = hash < 0 ? defaultName : target.substring(hash + 1);
        ClassNode cn = classes.get(cls);
        if (cn == null && stub.containsKey(cls)) {
            // a vanilla owner (a member 26.x moved to another class): the converter keeps the
            // call site's name, which the namespace wall then maps like any other
            cn = stub.get(cls);
            if (wall != null && hash < 0) name = wall.mapFieldName(cls, name, desc);
        }
        boolean dep = false;
        if (cn == null && deps.containsKey(cls)) {
            cn = deps.get(cls); // a dependency mod's class
            dep = true;
        }
        if (cn == null) {
            problems.add(where + " -> " + target + " (no such shim class)");
            return;
        }
        if (dep && (cn.access & Opcodes.ACC_PUBLIC) == 0) {
            problems.add(where + " -> " + cls + " (dependency mod class not public)");
            return;
        }
        for (FieldNode f : cn.fields) {
            if (f.name.equals(name)) {
                if (!f.desc.equals(desc)) {
                    problems.add(where + " -> " + cls + "." + name + " is " + f.desc + ", call site reads " + desc);
                } else if ((f.access & Opcodes.ACC_STATIC) == 0) {
                    problems.add(where + " -> " + cls + "." + name + " (not static)");
                } else if (dep && (f.access & Opcodes.ACC_PUBLIC) == 0) {
                    problems.add(where + " -> " + cls + "." + name + " (dependency mod member not public)");
                }
                return;
            }
        }
        problems.add(where + " -> " + cls + "." + name + ":" + desc + " (field missing)");
    }

    private static void obj(com.google.gson.JsonObject root, String key, java.util.function.BiConsumer<String, String> each) {
        if (!root.has(key)) {
            return;
        }
        for (var e : root.getAsJsonObject(key).entrySet()) {
            each.accept(e.getKey(), e.getValue().getAsString());
        }
    }

    /** a wiring key split into owner / name / desc */
    private record Sym(String owner, String name, String desc) {
        static Sym method(String key) {
            int paren = key.indexOf('(');
            int dot = key.lastIndexOf('.', paren);
            return new Sym(key.substring(0, dot), key.substring(dot + 1, paren), key.substring(paren));
        }

        static Sym field(String key) {
            int colon = key.indexOf(':');
            int dot = key.lastIndexOf('.', colon);
            return new Sym(key.substring(0, dot), key.substring(dot + 1, colon), key.substring(colon + 1));
        }
    }

    private WiringCheck() {
    }
}
