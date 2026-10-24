package top.dext.centurybridge.data;

import com.google.gson.GsonBuilder;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.Type;
import org.objectweb.asm.tree.ClassNode;
import org.objectweb.asm.tree.MethodNode;
import top.dext.centurybridge.engine.MixinRepair;
import top.dext.centurybridge.engine.WallInverse;

/**
 * mixinTargets for the members a step reshaped under the same name: a V method W no longer declares with
 * V's descriptor while W's class declares exactly one method of that name. Intermediary names are the
 * member's identity across releases (and the wall table maps by bytecode), so the one same-named W method
 * is the reshaped V method. Emitted only when every V argument has exactly one W argument of its type
 * (or a W subtype of it) and no two share one, the static-ness and constructor-ness agree, and neither is
 * synthetic: an injector then follows with its handler's arguments mapped. Where the arguments do not map
 * one for one MixinRepair's desc-drop already carries the handlers that take none; nothing is emitted.
 * Members the step's wiring already moves (rename / reshape / hook) are left to it.
 *
 *   gen-mixin-targets <V code jar> <W code jar> <step wiring.json> <damage-mixin.tsv> <out.json> [--wall <table.tsv>]
 * reads the step's mixin damage table for the V methods to judge (rows whose target is a method), writes a
 * wiring fragment {"mixinTargets": {...}} and prints how many members it covers and why the rest were not.
 */
public final class GenMixinTargets {
    private GenMixinTargets() {}

    public static void run(String[] args) throws IOException {
        Path vCode = Path.of(args[1]);
        Path wCode = Path.of(args[2]);
        com.google.gson.JsonObject wiring = top.dext.centurybridge.engine.Wiring.read(Path.of(args[3]));
        Path damage = Path.of(args[4]);
        Path out = Path.of(args[5]);
        for (int i = 6; i < args.length; i++) {
            if (args[i].equals("--wall")) {
                wCode = WallInverse.intermediaryView(wCode, vCode, Path.of(args[++i]));
            }
        }
        MixinRepair v = new MixinRepair(vCode);
        MixinRepair w = new MixinRepair(wCode);
        Set<String> wired = new java.util.HashSet<>();
        for (String sec : List.of("methodRenames", "callReshapes", "mixinTargets", "staticRedirects", "instanceRedirects", "superCtorReshapes")) {
            if (wiring.has(sec)) {
                wired.addAll(wiring.getAsJsonObject(sec).keySet());
            }
        }
        if (wiring.has("overrideHooks")) {
            for (var e : wiring.getAsJsonArray("overrideHooks")) {
                wired.add("hook:" + e.getAsJsonObject().get("old").getAsString());
            }
        }
        Set<String> members = new java.util.TreeSet<>();
        List<String> dl = Files.readAllLines(damage, StandardCharsets.UTF_8);
        for (String line : dl.subList(1, dl.size())) {
            String[] r = line.split("\t", -1);
            if (r.length > 2 && r[2].contains("(") && !r[0].startsWith("accessor") && !r[0].startsWith("shadow-field")) {
                members.add(r[1] + "." + r[2]);
            }
        }
        Map<String, JsonObject> outMap = new TreeMap<>();
        Map<String, Integer> why = new TreeMap<>();
        for (String key : members) {
            int dot = key.lastIndexOf('.', key.indexOf('('));
            String owner = key.substring(0, dot);
            String nd = key.substring(dot + 1);
            String name = nd.substring(0, nd.indexOf('('));
            String desc = nd.substring(nd.indexOf('('));
            if (wired.contains(key) || wired.contains("hook:" + nd)) {
                why.merge("wired by the step", 1, Integer::sum);
                continue;
            }
            ClassNode vc = v.target(owner);
            ClassNode wc = w.target(owner);
            if (vc == null || wc == null) {
                why.merge("class gone", 1, Integer::sum);
                continue;
            }
            MethodNode vm = find(vc, name, desc);
            if (vm == null || (vm.access & Opcodes.ACC_SYNTHETIC) != 0) {
                why.merge("V method not readable / synthetic", 1, Integer::sum);
                continue;
            }
            if (find(wc, name, desc) != null) {
                why.merge("W keeps it (anchor rows)", 1, Integer::sum);
                continue;
            }
            List<MethodNode> same = new ArrayList<>();
            for (MethodNode m : wc.methods) {
                if (m.name.equals(name) && (m.access & Opcodes.ACC_SYNTHETIC) == 0) {
                    same.add(m);
                }
            }
            if (same.size() != 1) {
                why.merge(same.isEmpty() ? "no W method of that name" : "several W overloads", 1, Integer::sum);
                continue;
            }
            MethodNode wm = same.get(0);
            if (((wm.access ^ vm.access) & Opcodes.ACC_STATIC) != 0) {
                why.merge("static-ness changed", 1, Integer::sum);
                continue;
            }
            Type[] va = Type.getArgumentTypes(vm.desc);
            Type[] wa = Type.getArgumentTypes(wm.desc);
            int[] from = new int[va.length];
            boolean ok = true;
            Set<Integer> used = new java.util.HashSet<>();
            for (int i = 0; i < va.length && ok; i++) {
                from[i] = -1;
                for (int j = 0; j < wa.length; j++) {
                    if (wa[j].equals(va[i]) || assignable(w, wa[j], va[i])) {
                        from[i] = from[i] == -1 ? j : -2;
                    }
                }
                ok = from[i] >= 0 && used.add(from[i]);
            }
            if (!ok) {
                why.merge("arguments not one for one (desc-drop serves argument-less handlers)", 1, Integer::sum);
                continue;
            }
            JsonObject o = new JsonObject();
            o.addProperty("to", wm.name + wm.desc);
            JsonArray a = new JsonArray();
            for (int f : from) {
                a.add(f);
            }
            o.add("args", a);
            o.addProperty("basis", "gen_mixin_targets: same name, the one method of that name W declares, arguments by type one for one");
            outMap.put(key, o);
            why.merge("emitted", 1, Integer::sum);
        }
        JsonObject root = new JsonObject();
        root.addProperty("_mixinTargets_basis", "Main gen-mixin-targets (V/W bytecode, damage-mixin table); regenerate, never hand-edit");
        JsonObject mt = new JsonObject();
        outMap.forEach(mt::add);
        root.add("mixinTargets", mt);
        Files.writeString(out, new GsonBuilder().setPrettyPrinting().disableHtmlEscaping().create().toJson(root) + "\n");
        System.out.println("gen-mixin-targets: " + members.size() + " V methods in the damage table -> " + out);
        why.forEach((k, n) -> System.out.printf("  %6d  %s%n", n, k));
    }

    private static MethodNode find(ClassNode c, String name, String desc) {
        for (MethodNode m : c.methods) {
            if (m.name.equals(name) && m.desc.equals(desc)) {
                return m;
            }
        }
        return null;
    }

    /** a W value of type sub is a sup on W (superclasses and interfaces) */
    private static boolean assignable(MixinRepair w, Type sub, Type sup) {
        if (sub.getSort() != Type.OBJECT || sup.getSort() != Type.OBJECT) {
            return false;
        }
        if (sup.getInternalName().equals("java/lang/Object")) {
            return true;
        }
        java.util.ArrayDeque<String> todo = new java.util.ArrayDeque<>(List.of(sub.getInternalName()));
        Set<String> seen = new java.util.HashSet<>();
        while (!todo.isEmpty()) {
            String c = todo.poll();
            if (!seen.add(c)) {
                continue;
            }
            if (c.equals(sup.getInternalName())) {
                return true;
            }
            ClassNode cn = w.target(c);
            if (cn == null) {
                continue;
            }
            if (cn.superName != null) {
                todo.add(cn.superName);
            }
            todo.addAll(cn.interfaces);
        }
        return false;
    }
}
