package top.dext.centurybridge.data;

import com.google.gson.JsonElement;
import java.io.IOException;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.zip.ZipFile;
import top.dext.centurybridge.engine.res.ResourceTranslator;
import top.dext.centurybridge.res.Json;

/**
 * The resource translation judged against the game itself: release A's vanilla data
 * and assets, run through the steps from A to B, compared file by file with what
 * release B ships. A file Mojang only reformatted between the two must come out
 * equal (JSON-equal: object key order aside); what still differs is either a
 * content change Mojang made or a rule the translation lacks -- the per-directory
 * report shows which, the shape changes listed as ResDiff lists them.
 */
public final class ResParity {

    public static void run(Path jarA, String verA, Path jarB, String verB, int examples) throws IOException {
        Map<String, byte[]> a = read(jarA);
        Map<String, byte[]> b = read(jarB);
        Map<String, byte[]> before = new HashMap<>(a);
        ResourceTranslator.Outcome o = ResourceTranslator.apply(a, verA, verB);
        System.out.println("res-parity " + verA + " -> " + verB + ": moved " + o.moved + ", rewritten " + o.rewritten
            + ", generated " + o.generated);
        o.losses.forEach((k, v) -> System.out.println("  lost x" + v + ": " + k));

        Map<String, int[]> perDir = new TreeMap<>(); // translated & equal, translated & differ, untouched & equal, untouched & differ, missing in B
        Map<String, List<String>> samples = new TreeMap<>();
        for (var e : a.entrySet()) {
            String n = e.getKey();
            if (!n.endsWith(".json")) {
                continue;
            }
            String dir = dirOf(n);
            int[] c = perDir.computeIfAbsent(dir, k -> new int[5]);
            boolean touched = !before.containsKey(n) || before.get(n) != e.getValue();
            byte[] other = b.get(n);
            if (other == null) {
                c[4]++;
                continue;
            }
            JsonElement ja = Json.tryParse(e.getValue());
            JsonElement jb = Json.tryParse(other);
            // releases differ in which defaults their encoders write (26.x omits count 1,
            // bonus_rolls 0, add false...): both sides are compared with defaults dropped
            boolean eq = ja != null && jb != null && dropDefaults(ja.deepCopy()).equals(dropDefaults(jb.deepCopy()));
            c[(touched ? 0 : 2) + (eq ? 0 : 1)]++;
            if (touched && !eq) {
                List<String> l = samples.computeIfAbsent(dir, k -> new ArrayList<>());
                if (l.size() < examples) {
                    l.add(n + "\n        ours: " + abbreviate(ja) + "\n        game: " + abbreviate(jb));
                }
            }
        }
        System.out.println("  dir: translated equal / translated differ | untouched equal / untouched differ | not in " + verB);
        perDir.forEach((d, c) -> {
            if (c[0] + c[1] + c[3] + c[4] > 0) {
                System.out.printf("  %-34s %6d / %-6d | %6d / %-6d | %d%n", d, c[0], c[1], c[2], c[3], c[4]);
            }
        });
        samples.forEach((d, l) -> {
            System.out.println("== translated but still different: " + d);
            l.forEach(s -> System.out.println("    " + s));
        });
    }

    /** a field holding a value an encoder may leave out as the default: 0, false, 1 for a count, empty */
    private static JsonElement dropDefaults(JsonElement e) {
        if (e.isJsonArray()) {
            com.google.gson.JsonArray a = e.getAsJsonArray();
            for (int i = 0; i < a.size(); i++) {
                a.set(i, dropDefaults(a.get(i)));
            }
            return a;
        }
        if (!e.isJsonObject()) {
            return e;
        }
        com.google.gson.JsonObject o = e.getAsJsonObject();
        for (String k : new java.util.ArrayList<>(o.keySet())) {
            JsonElement v = dropDefaults(o.get(k));
            boolean dflt = v.isJsonPrimitive() && (v.getAsJsonPrimitive().isBoolean() && !v.getAsBoolean()
                    || v.getAsJsonPrimitive().isNumber() && (v.getAsDouble() == 0 || k.equals("count") && v.getAsDouble() == 1))
                || v.isJsonArray() && v.getAsJsonArray().isEmpty()
                || v.isJsonObject() && v.getAsJsonObject().size() == 0;
            if (dflt) {
                o.remove(k);
            } else {
                o.add(k, v);
            }
        }
        return o;
    }

    private static String abbreviate(JsonElement e) {
        String s = String.valueOf(e);
        return s.length() > 400 && !Boolean.getBoolean("cb.parity.full") ? s.substring(0, 400) + "..." : s;
    }

    private static String dirOf(String n) {
        String[] p = n.split("/");
        if (p.length < 4) {
            return n;
        }
        boolean two = p[2].equals("tags") || p[2].equals("worldgen") || p[2].equals("models") || p[2].equals("textures");
        return p[0] + "/" + p[2] + (two && p.length > 4 ? "/" + p[3] : "");
    }

    private static Map<String, byte[]> read(Path jar) throws IOException {
        Map<String, byte[]> m = new HashMap<>();
        try (ZipFile z = new ZipFile(jar.toFile())) {
            for (var en : java.util.Collections.list(z.entries())) {
                String n = en.getName();
                if (!en.isDirectory() && (n.startsWith("data/") || n.startsWith("assets/"))
                        && !n.startsWith("data/minecraft/datapacks/")) {
                    m.put(n, z.getInputStream(en).readAllBytes());
                }
            }
        }
        return m;
    }

    private ResParity() {
    }
}
