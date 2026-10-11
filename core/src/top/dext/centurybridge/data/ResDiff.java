package top.dext.centurybridge.data;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;
import java.util.zip.ZipFile;

/**
 * What changed in the vanilla data and assets between two releases -- the
 * resource-side counterpart of the stub diff. Both client jars carry the full
 * vanilla data pack and resource pack, so the format a release reads is on
 * record in the files it ships.
 *
 * Reported per resource directory ("recipes", "tags/items", "models/item"):
 * directories that moved (most of their file names reappear under another
 * directory), and for files present on both sides the JSON shape changes --
 * key paths (array positions folded to []) that appeared, disappeared or
 * changed type -- counted over all files, top-level "type" values kept apart.
 * Content tweaks change values, not shapes; what remains is the format.
 */
public final class ResDiff {

    private record Res(String root, String ns, String dir, String rest) {
        String key() {
            return root + "/" + dir;
        }
    }

    public static void run(Path oldJar, Path newJar, int top) throws IOException {
        Map<String, byte[]> a = read(oldJar);
        Map<String, byte[]> b = read(newJar);
        Map<String, Map<String, String>> aDirs = index(a.keySet());
        Map<String, Map<String, String>> bDirs = index(b.keySet());

        // directories that moved: an old directory's file names reappear elsewhere
        Map<String, String> moved = new TreeMap<>();
        for (var e : aDirs.entrySet()) {
            if (bDirs.containsKey(e.getKey()) && overlap(e.getValue().keySet(), bDirs.get(e.getKey()).keySet()) > 0.5) {
                continue;
            }
            String best = null;
            double bestScore = 0;
            for (var f : bDirs.entrySet()) {
                if (f.getKey().equals(e.getKey()) || !f.getKey().startsWith(e.getKey().substring(0, e.getKey().indexOf('/')))) {
                    continue; // a directory moves within its pack (data or assets)
                }
                double s = overlap(e.getValue().keySet(), f.getValue().keySet());
                if (s > bestScore) {
                    bestScore = s;
                    best = f.getKey();
                }
            }
            if (best != null && bestScore >= 0.5) {
                moved.put(e.getKey(), best + String.format(" (%.0f%% of %d names)", bestScore * 100, e.getValue().size()));
            } else if (!bDirs.containsKey(e.getKey())) {
                moved.put(e.getKey(), "(gone; " + e.getValue().size() + " files)");
            }
        }
        System.out.println("== directories moved or gone " + oldJar.getFileName() + " -> " + newJar.getFileName());
        moved.forEach((k, v) -> System.out.println("  " + k + " -> " + v));
        for (var f : bDirs.entrySet()) {
            if (!aDirs.containsKey(f.getKey()) && !moved.values().stream().anyMatch(v -> v.startsWith(f.getKey() + " "))) {
                System.out.println("  (new) " + f.getKey() + " " + f.getValue().size() + " files");
            }
        }

        // shape changes over files present on both sides (after the moves)
        for (var e : aDirs.entrySet()) {
            String dirA = e.getKey();
            String dirB = bDirs.containsKey(dirA) ? dirA
                : moved.containsKey(dirA) && !moved.get(dirA).startsWith("(") ? moved.get(dirA).split(" ")[0] : null;
            if (dirB == null) {
                continue;
            }
            Map<String, String> bFiles = bDirs.get(dirB);
            Map<String, Map<String, Integer>> changes = new TreeMap<>();
            int paired = 0;
            int identical = 0;
            for (var f : e.getValue().entrySet()) {
                if (!f.getKey().endsWith(".json") || !bFiles.containsKey(f.getKey())) {
                    continue;
                }
                JsonElement ja;
                JsonElement jb;
                try {
                    ja = JsonParser.parseString(new String(a.get(f.getValue()), StandardCharsets.UTF_8));
                    jb = JsonParser.parseString(new String(b.get(bFiles.get(f.getKey())), StandardCharsets.UTF_8));
                } catch (RuntimeException ex) {
                    continue;
                }
                paired++;
                if (ja.equals(jb)) {
                    identical++;
                    continue;
                }
                String kind = typeOf(ja);
                Map<String, String> sa = new TreeMap<>();
                Map<String, String> sb = new TreeMap<>();
                shape(ja, "", sa);
                shape(jb, "", sb);
                Map<String, Integer> c = changes.computeIfAbsent(kind, k -> new HashMap<>());
                Set<String> keys = new HashSet<>(sa.keySet());
                keys.addAll(sb.keySet());
                for (String k : keys) {
                    String ta = sa.get(k);
                    String tb = sb.get(k);
                    if (ta == null) {
                        c.merge("+ " + k + " : " + tb, 1, Integer::sum);
                    } else if (tb == null) {
                        c.merge("- " + k + " : " + ta, 1, Integer::sum);
                    } else if (!ta.equals(tb)) {
                        c.merge("~ " + k + " : " + ta + " -> " + tb, 1, Integer::sum);
                    }
                }
            }
            if (paired == identical) {
                continue;
            }
            System.out.println("== " + dirA + (dirB.equals(dirA) ? "" : " -> " + dirB) + ": " + paired + " paired, "
                + identical + " identical");
            for (var k : changes.entrySet()) {
                List<Map.Entry<String, Integer>> l = new ArrayList<>(k.getValue().entrySet());
                l.sort((x, y) -> y.getValue() - x.getValue());
                System.out.println("  [" + k.getKey() + "]");
                for (int i = 0; i < Math.min(top, l.size()); i++) {
                    System.out.println("    " + l.get(i).getValue() + "  " + l.get(i).getKey());
                }
            }
        }
    }

    /** the top-level "type" of a data file, or its kind of shape */
    private static String typeOf(JsonElement e) {
        if (e.isJsonObject() && e.getAsJsonObject().has("type") && e.getAsJsonObject().get("type").isJsonPrimitive()) {
            return e.getAsJsonObject().get("type").getAsString();
        }
        if (e.isJsonObject() && e.getAsJsonObject().has("criteria")) {
            return "advancement";
        }
        return "*";
    }

    /** key path -> value kind, array positions folded */
    private static void shape(JsonElement e, String path, Map<String, String> out) {
        if (e.isJsonObject()) {
            JsonObject o = e.getAsJsonObject();
            out.merge(path.isEmpty() ? "$" : path, "obj", ResDiff::join);
            for (var en : o.entrySet()) {
                // keys that are names rather than fields (criteria names, pattern keys) fold to *
                String k = path.endsWith("criteria") || path.endsWith("key") || path.endsWith("textures")
                    || path.endsWith("variants") ? "*" : en.getKey();
                shape(en.getValue(), path + "." + k, out);
            }
        } else if (e.isJsonArray()) {
            JsonArray arr = e.getAsJsonArray();
            out.merge(path, "arr", ResDiff::join);
            for (JsonElement x : arr) {
                shape(x, path + "[]", out);
            }
        } else if (e.isJsonNull()) {
            out.merge(path, "null", ResDiff::join);
        } else {
            var p = e.getAsJsonPrimitive();
            out.merge(path, p.isBoolean() ? "bool" : p.isNumber() ? "num" : "str", ResDiff::join);
        }
    }

    private static String join(String x, String y) {
        if (x.contains(y)) {
            return x;
        }
        List<String> l = new ArrayList<>(List.of(x.split("\\|")));
        l.add(y);
        l.sort(null);
        return String.join("|", l);
    }

    /** "data/recipes" / "assets/models/item" -> (name below it -> entry) */
    private static Map<String, Map<String, String>> index(Set<String> names) {
        Map<String, Map<String, String>> dirs = new TreeMap<>();
        for (String n : names) {
            Res r = parse(n);
            if (r != null) {
                dirs.computeIfAbsent(r.key(), k -> new HashMap<>()).put(r.ns() + ":" + r.rest(), n);
            }
        }
        return dirs;
    }

    /**
     * data/<ns>/<dir>/<rest>: the directory is one level, two under tags/ and
     * worldgen/ (and models/, textures/ for assets), so tags/items and worldgen/biome
     * are told apart.
     */
    private static Res parse(String n) {
        String[] p = n.split("/");
        if (p.length < 4 || !(p[0].equals("data") || p[0].equals("assets")) || p[2].equals("datapacks")) {
            return null; // (built-in feature packs nest a data/ tree of their own)
        }
        int depth = p[2].equals("tags") || p[2].equals("worldgen") || p[2].equals("models")
            || p[2].equals("textures") ? 2 : 1;
        if (p.length < 2 + depth + 1) {
            return null;
        }
        String dir = String.join("/", java.util.Arrays.copyOfRange(p, 2, 2 + depth));
        String rest = String.join("/", java.util.Arrays.copyOfRange(p, 2 + depth, p.length));
        return new Res(p[0], p[1], dir, rest);
    }

    private static double overlap(Set<String> x, Set<String> y) {
        if (x.isEmpty()) {
            return 0;
        }
        int n = 0;
        for (String s : x) {
            if (y.contains(s)) {
                n++;
            }
        }
        return (double) n / x.size();
    }

    private static Map<String, byte[]> read(Path jar) throws IOException {
        Map<String, byte[]> m = new HashMap<>();
        try (ZipFile z = new ZipFile(jar.toFile())) {
            for (var en : java.util.Collections.list(z.entries())) {
                String n = en.getName();
                if (!en.isDirectory() && (n.startsWith("data/") || n.startsWith("assets/"))) {
                    m.put(n, z.getInputStream(en).readAllBytes());
                }
            }
        }
        return m;
    }

    private ResDiff() {
    }
}
