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
 *   wiring-check &lt;compiledShimDir&gt; &lt;shims.json&gt; [&lt;inter-to-mojang.tsv&gt;]
 */
public final class WiringCheck {

    private final Map<String, ClassNode> classes = new HashMap<>();
    private final List<String> problems = new ArrayList<>();
    private Remapper facade = new SimpleRemapper(Map.of());
    private Remapper wall;
    private int checked;

    public static void run(Path classesDir, Path shimsJson, Path table) throws IOException {
        WiringCheck w = new WiringCheck();
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
        System.out.println("wiring-check: " + w.checked + " wired targets against " + classesDir
            + ", " + w.problems.size() + " dangling");
        for (String s : w.problems) {
            System.out.println("    " + s);
        }
        if (!w.problems.isEmpty()) {
            System.exit(1);
        }
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
        if (root.has("superCtorReshapes")) {
            for (var e : root.getAsJsonObject("superCtorReshapes").entrySet()) {
                var o = e.getValue().getAsJsonObject();
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
