package top.dext.centurybridge.data;

import com.google.gson.JsonElement;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HexFormat;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.zip.ZipFile;
import top.dext.centurybridge.engine.Wiring;
import top.dext.centurybridge.engine.res.ResourceTranslator;
import top.dext.centurybridge.res.Json;

/**
 * The resource translation judged against the game itself: release A's vanilla data
 * and assets, run through the steps from A to B with each climb step's own resourceTypes,
 * tagMap and tagMirror, compared file by file with what release B ships. JSON is compared
 * semantically (defaults and object key order aside), every other resource byte for byte.
 * Differences can be Mojang's content changes or a missing rule; the per-directory report
 * shows where, and ResDiff describes their shapes. CenturyBridge-only outputs and deleted
 * inputs are reported separately, never silently treated as vanilla parity failures.
 */
public final class ResParity {

    public static void run(Path jarA, String verA, Path jarB, String verB, int examples, Path segmentsDir) throws IOException {
        System.out.println("res-parity input " + verA + ": " + jarA.toAbsolutePath() + " sha256=" + sha256(jarA));
        System.out.println("res-parity input " + verB + ": " + jarB.toAbsolutePath() + " sha256=" + sha256(jarB));
        Map<String, byte[]> a = read(jarA);
        Map<String, byte[]> b = read(jarB);
        Map<String, byte[]> before = new LinkedHashMap<>(a);
        ResourceTranslator.Outcome o = applyClimb(a, verA, verB, segmentsDir);
        System.out.println("res-parity " + verA + " -> " + verB + ": moved " + o.moved + ", rewritten " + o.rewritten
            + ", generated " + o.generated);
        o.losses.forEach((k, v) -> System.out.println("  lost x" + v + ": " + k));
        o.notes.forEach(n -> System.out.println("  note: " + n));

        // translated equal/differ, untouched equal/differ, missing in B, dropped by us, expected CB-only, moved originals
        Map<String, int[]> perDir = new TreeMap<>();
        Map<String, List<String>> samples = new TreeMap<>();
        int[] total = new int[8];
        int compared = 0;
        for (String n : new LinkedHashSet<>(java.util.stream.Stream.concat(before.keySet().stream(), a.keySet().stream()).toList())) {
            String dir = dirOf(n);
            int[] c = perDir.computeIfAbsent(dir, k -> new int[8]);
            byte[] value = a.get(n);
            if (value == null) {
                String moved = ResourceTranslator.movedPath(n);
                if (moved != null && a.containsKey(moved) && !before.containsKey(moved)) {
                    c[7]++;
                    total[7]++;
                    continue;
                }
                c[5]++;
                total[5]++;
                addSample(samples, dir, examples, n + "\n        ours: <dropped by the translator>"
                    + (b.containsKey(n) ? "\n        game: " + abbreviateBytes(n, b.get(n)) : ""));
                continue;
            }
            compared++;
            boolean touched = !before.containsKey(n) || before.get(n) != value;
            byte[] other = b.get(n);
            if (other == null) {
                int bucket = expectedCb(n) ? 6 : 4;
                c[bucket]++;
                total[bucket]++;
                continue;
            }
            boolean eq = equal(n, value, other);
            int bucket = (touched ? 0 : 2) + (eq ? 0 : 1);
            c[bucket]++;
            total[bucket]++;
            if (touched && !eq) {
                addSample(samples, dir, examples, n + "\n        ours: " + abbreviateBytes(n, value)
                    + "\n        game: " + abbreviateBytes(n, other));
            }
        }
        System.out.println("  dir: translated equal / translated differ | untouched equal / untouched differ | not in "
            + verB + " | expected CB | dropped | moved originals");
        perDir.forEach((d, c) -> {
            if (c[0] + c[1] + c[3] + c[4] + c[5] + c[6] + c[7] > 0) {
                System.out.printf("  %-34s %6d / %-6d | %6d / %-6d | %d | %d | %d | %d%n",
                    d, c[0], c[1], c[2], c[3], c[4], c[6], c[5], c[7]);
            }
        });
        int losses = o.losses.values().stream().mapToInt(Integer::intValue).sum();
        System.out.printf("  total compared %d (skipped 0): %d / %d | %d / %d | not in %s %d | expected CB %d | dropped %d | moved originals %d | losses %d%n",
            compared, total[0], total[1], total[2], total[3], verB, total[4], total[6], total[5], total[7], losses);
        samples.forEach((d, l) -> {
            System.out.println("== translated but still different: " + d);
            l.forEach(s -> System.out.println("    " + s));
        });
    }

    private static boolean equal(String name, byte[] a, byte[] b) {
        if (!name.endsWith(".json")) {
            return Arrays.equals(a, b);
        }
        JsonElement ja = Json.tryParse(a);
        JsonElement jb = Json.tryParse(b);
        return ja != null && jb != null && dropDefaults(ja.deepCopy()).equals(dropDefaults(jb.deepCopy()));
    }

    private static boolean expectedCb(String name) {
        return name.startsWith("data/centurybridge/") || name.startsWith("assets/centurybridge/");
    }

    private static void addSample(Map<String, List<String>> samples, String dir, int examples, String text) {
        List<String> l = samples.computeIfAbsent(dir, k -> new ArrayList<>());
        if (l.size() < examples) {
            l.add(text);
        }
    }

    /** The exact per-boundary resource inputs Main.convert-climb gives ResourceTranslator. */
    private static ResourceTranslator.Outcome applyClimb(Map<String, byte[]> entries, String from, String to,
            Path segmentsDir) throws IOException {
        ResourceTranslator.Outcome total = new ResourceTranslator.Outcome();
        int a = top.dext.centurybridge.res.Release.index(from);
        int b = top.dext.centurybridge.res.Release.index(to);
        if (a > b) {
            throw new IllegalArgumentException("bad resource parity range " + from + " -> " + to);
        }
        for (int i = a; i < b; i++) {
            String v = top.dext.centurybridge.res.Release.ALL.get(i);
            String w = top.dext.centurybridge.res.Release.ALL.get(i + 1);
            String seg = v + "__" + w;
            Path wiring = v.equals("1.20.1") && w.equals("1.20.2")
                ? segmentsDir.resolve("shims-1.20.2.json") : segmentsDir.resolve("steps").resolve(seg + ".json");
            total.notes.add("wiring " + seg + " = " + wiring.toAbsolutePath());
            var root = Wiring.read(wiring);
            java.util.Set<String> runtimeTypes = new LinkedHashSet<>();
            if (root.has("resourceTypes")) {
                for (var t : root.getAsJsonArray("resourceTypes")) {
                    runtimeTypes.add(t.getAsString());
                }
            }
            Map<String, String> tagMap = new LinkedHashMap<>();
            Map<String, List<String>> tagMirror = new LinkedHashMap<>();
            if (root.has("tagMap")) {
                for (var tm : root.getAsJsonObject("tagMap").entrySet()) {
                    var value = tm.getValue().getAsJsonObject();
                    String target = value.get("to").getAsString();
                    tagMap.put(tm.getKey(), target);
                    List<String> mirrors = new ArrayList<>();
                    if (value.has("mirror") && value.get("mirror").isJsonArray()) {
                        for (var m : value.getAsJsonArray("mirror")) {
                            mirrors.add(m.getAsString());
                        }
                    } else if (value.has("mirror") && value.get("mirror").getAsBoolean()) {
                        mirrors.add(target);
                    }
                    if (!mirrors.isEmpty()) {
                        tagMirror.put(tm.getKey(), mirrors);
                    }
                }
            }
            ResourceTranslator.Outcome o = ResourceTranslator.apply(entries, v, w, runtimeTypes, tagMap, tagMirror);
            total.moved += o.moved;
            total.rewritten += o.rewritten;
            total.generated += o.generated;
            o.losses.forEach((what, n) -> total.losses.merge(seg + ": " + what, n, Integer::sum));
            for (String note : o.notes) {
                total.notes.add(seg + ": " + note);
            }
        }
        return total;
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
        for (String k : new ArrayList<>(o.keySet())) {
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

    private static String abbreviateBytes(String name, byte[] bytes) {
        if (name.endsWith(".json")) {
            JsonElement json = Json.tryParse(bytes);
            if (json != null) {
                return abbreviate(String.valueOf(json));
            }
        }
        if (name.endsWith(".png") || name.endsWith(".jpg") || name.endsWith(".jpeg")
                || name.endsWith(".ogg") || name.endsWith(".nbt") || name.endsWith(".class")) {
            return "<binary " + bytes.length + " bytes>";
        }
        return abbreviate(new String(bytes, StandardCharsets.UTF_8));
    }

    private static String abbreviate(String s) {
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
        // Jar order is conversion order: mirrored tag values must be appended in that same order.
        Map<String, byte[]> m = new LinkedHashMap<>();
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

    private static String sha256(Path jar) throws IOException {
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            try (var in = Files.newInputStream(jar)) {
                byte[] buf = new byte[1024 * 1024];
                for (int n; (n = in.read(buf)) >= 0; ) {
                    digest.update(buf, 0, n);
                }
            }
            return HexFormat.of().formatHex(digest.digest());
        } catch (NoSuchAlgorithmException e) {
            throw new IllegalStateException(e);
        }
    }

    private ResParity() {
    }
}
