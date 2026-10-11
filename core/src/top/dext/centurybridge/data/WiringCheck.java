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
 * Wiring keys checked: staticRedirects, instanceRedirects, superRedirects,
 * fieldRedirects, fieldGetters, instanceFieldGetters, instanceOfRedirects,
 * ctorRedirects, superCtorAdapts (converter), superCtorReshapes (conv / push
 * statics), lookupRt, overrideHooks (wrap / unwrap / guard), classRenames
 * onto shipped facades. Covers are checked by MixinCheck against the mixin
 * classes (they are overloads added to vanilla classes, not statics).
 *
 *   wiring-check &lt;compiledShimDir&gt; &lt;shims.json&gt; [&lt;inter-to-mojang.tsv&gt;] [&lt;target stub jar&gt;]
 */
public final class WiringCheck {

    private final Map<String, ClassNode> classes = new HashMap<>();
    /** target stub classes (members only): what superRenames and reshapes must agree with */
    private final Map<String, ClassNode> stub = new HashMap<>();
    private final List<String> problems = new ArrayList<>();
    private Remapper facade = new SimpleRemapper(Map.of());
    private Remapper wall;
    private int checked;

    public static void run(Path classesDir, Path shimsJson, Path table, Path stubJar) throws IOException {
        run(classesDir, shimsJson, table, stubJar, null, null);
    }

    /** older releases' stubs: what a facade stands in for */
    private SourceStubs sources = SourceStubs.none();

    public static void run(Path classesDir, Path shimsJson, Path table, Path stubJar, Path stubDir, String version)
            throws IOException {
        WiringCheck w = new WiringCheck();
        if (stubDir != null && version != null) {
            w.sources = SourceStubs.before(stubDir, version);
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
        if (table != null && Files.exists(table)) {
            w.wall = JarRemapper.loadTable(table).remapper();
        }
        var root = new com.google.gson.Gson().fromJson(Files.readString(shimsJson), com.google.gson.JsonObject.class);
        w.check(root);
        w.checkFacadeSurfaces(root);
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
     * A facade stands in for a class that is gone: every reference a mod had to it is
     * renamed onto the facade, so the facade has to answer every member the dead class
     * declared -- a missing one is a NoSuchMethodError at a mod's call, super() or
     * super.m() (a LeafEntry.Serializer facade without the typed addEntryFields broke every
     * subclass's super call). A mod may come from any release the class lived in, so the
     * contract is every shape it had in any of them: non-private constructors, methods and
     * fields, descriptors through the facade renames; methods and fields may come from the
     * facade's own superclasses.
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
                if ((m.access & (Opcodes.ACC_PRIVATE | Opcodes.ACC_SYNTHETIC)) != 0 || m.name.equals("<clinit>")) {
                    continue;
                }
                checked++;
                String desc = methodDesc(m.desc);
                String gapKey = dead + "." + m.name + m.desc;
                if (!facadeHasMethod(fac, m.name, desc, m.name.equals("<init>"))) {
                    if (declared.containsKey(gapKey)) {
                        used.add(gapKey);
                    } else {
                        missing.add(m.name + desc);
                    }
                }
            }
            for (FieldNode f : fields) {
                if ((f.access & (Opcodes.ACC_PRIVATE | Opcodes.ACC_SYNTHETIC)) != 0) {
                    continue;
                }
                checked++;
                String desc = typeDesc(f.desc);
                String gapKey = dead + "." + f.name + ":" + f.desc;
                if (!facadeHasField(fac, f.name, desc)) {
                    if (declared.containsKey(gapKey)) {
                        used.add(gapKey);
                    } else {
                        missing.add(f.name + ":" + desc);
                    }
                }
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

    /** what every class answers through java/lang/Object (records and enums declare their own) */
    private static final java.util.Set<String> OBJECT_METHODS = java.util.Set.of(
        "toString()Ljava/lang/String;", "equals(Ljava/lang/Object;)Z", "hashCode()I", "clone()Ljava/lang/Object;");

    private boolean facadeHasMethod(String cls, String name, String desc, boolean ctor) {
        if (!ctor && OBJECT_METHODS.contains(name + desc)) {
            return true;
        }
        for (String c = cls; c != null; ) {
            ClassNode cn = classes.containsKey(c) ? classes.get(c) : stub.get(c);
            if (cn == null) {
                return false;
            }
            for (MethodNode m : cn.methods) {
                if (m.name.equals(name) && m.desc.equals(desc)) {
                    return true;
                }
            }
            if (ctor) {
                return false; // constructors are not inherited
            }
            for (String i : cn.interfaces) {
                ClassNode in = classes.containsKey(i) ? classes.get(i) : stub.get(i);
                if (in != null && in.methods.stream().anyMatch(m -> m.name.equals(name) && m.desc.equals(desc))) {
                    return true;
                }
            }
            c = cn.superName;
        }
        return false;
    }

    private boolean facadeHasField(String cls, String name, String desc) {
        for (String c = cls; c != null; ) {
            ClassNode cn = classes.containsKey(c) ? classes.get(c) : stub.get(c);
            if (cn == null) {
                return false;
            }
            for (FieldNode f : cn.fields) {
                if (f.name.equals(name) && f.desc.equals(desc)) {
                    return true;
                }
            }
            c = cn.superName;
        }
        return false;
    }

    private void check(com.google.gson.JsonObject root) {
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
        for (String map : new String[] {"instanceRedirects", "superRedirects"}) {
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
        obj(root, "instanceFieldGetters", (k, v) -> {
            Sym s = Sym.field(k);
            wantMethod("instanceFieldGetters " + k, v, s.name, methodDesc("(L" + s.owner + ";)" + s.desc));
        });
        obj(root, "instanceOfRedirects", (k, v) ->
            wantMethod("instanceOfRedirects " + k, v, "cbInstanceOf", "(Ljava/lang/Object;)Z"));
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
                for (var op : o.getAsJsonArray("ops")) {
                    String t = op.getAsString().trim();
                    int sp = t.indexOf(' ');
                    if (sp < 0) {
                        continue; // keep / drop
                    }
                    String rest = t.substring(sp + 1).trim();   // OWNER#name(desc)R
                    int paren = rest.indexOf('(');
                    if (paren < 0) {
                        problems.add("superCtorReshapes " + e.getKey() + ": malformed op '" + t + "'");
                        continue;
                    }
                    wantMethod("superCtorReshapes " + e.getKey() + " op '" + t + "'",
                        rest.substring(0, paren), null, methodDesc(rest.substring(paren)));
                }
            }
        }
        if (root.has("lookupRt")) {
            String desc = root.has("lookupDesc") ? root.get("lookupDesc").getAsString()
                                                  : "()Lnet/minecraft/class_7225$class_7874;";
            wantMethod("lookupRt", root.get("lookupRt").getAsString(), "cbLookup", methodDesc(desc));
        }
        // adapterHooks: the bridge interface must declare every required old shape
        // exactly (the mod's own methods implement it by name+descriptor), and every
        // synthesized new shape calls a static that exists with the exact descriptor
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
                    for (var r : o.getAsJsonArray("requires")) {
                        String shape = r.getAsString();
                        int paren = shape.indexOf('(');
                        String name = shape.substring(0, paren);
                        String desc = methodDesc(shape.substring(paren));
                        boolean declared = an.methods.stream().anyMatch(m -> m.name.equals(name) && m.desc.equals(desc)
                            && (m.access & Opcodes.ACC_ABSTRACT) != 0);
                        if (!declared) {
                            problems.add(where + " does not declare the required shape " + name + desc);
                        }
                    }
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
        if (root.has("overrideHooks")) {
            for (var el : root.getAsJsonArray("overrideHooks")) {
                var o = el.getAsJsonObject();
                String where = "overrideHooks " + o.get("old").getAsString();
                for (String key : new String[] {"wrap", "unwrap"}) {
                    if (!o.has(key)) {
                        continue;
                    }
                    // "owner.name(desc)R"
                    String w = o.get(key).getAsString();
                    int paren = w.indexOf('(');
                    int dot = w.lastIndexOf('.', paren);
                    wantMethod(where + " " + key, w.substring(0, dot) + "#" + w.substring(dot + 1, paren),
                        null, methodDesc(w.substring(paren)));
                }
                if (o.has("guard")) {
                    String g = o.get("guard").getAsString();
                    wantMethod(where + " guard", g + "#cbEnter", null, "(Ljava/lang/Object;)Z");
                    wantMethod(where + " guard", g + "#cbExit", null, "(Ljava/lang/Object;)V");
                }
            }
        }
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
        String kt = targetName(key);
        ClassNode kn = stub.get(kt);
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

    private String targetName(String internalName) {
        return wall == null ? internalName : wall.mapType(internalName); // map() is null when unmapped
    }

    private boolean isSubtype(String sub, String sup) {
        java.util.Deque<String> todo = new java.util.ArrayDeque<>(List.of(sub));
        java.util.Set<String> seen = new java.util.HashSet<>();
        while (!todo.isEmpty()) {
            String c = todo.pop();
            if (c.equals(sup)) return true;
            if (!seen.add(c)) continue;
            ClassNode n = classes.containsKey(c) ? classes.get(c) : stub.get(c);
            if (n == null) continue;
            if (n.superName != null) todo.push(n.superName);
            todo.addAll(n.interfaces);
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
        if (cn == null) {
            problems.add(where + " -> " + target + " (no such shim class)");
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
        if (cn == null) {
            problems.add(where + " -> " + target + " (no such shim class)");
            return;
        }
        for (FieldNode f : cn.fields) {
            if (f.name.equals(name)) {
                if (!f.desc.equals(desc)) {
                    problems.add(where + " -> " + cls + "." + name + " is " + f.desc + ", call site reads " + desc);
                } else if ((f.access & Opcodes.ACC_STATIC) == 0) {
                    problems.add(where + " -> " + cls + "." + name + " (not static)");
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
