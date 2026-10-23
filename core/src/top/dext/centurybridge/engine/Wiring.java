package top.dext.centurybridge.engine;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

/**
 * A wiring table as the engine and its gates read it: one JSON object, completed by the fragments
 * in a sibling directory named after it plus ".d" (core/segments/shims-1.20.2.json.d/*.json,
 * core/segments/steps/1.20.2__1.20.3.json.d/*.json). A bridge area keeps its entries in its own
 * fragment, next to its own code, so independent areas of one step never edit the same file.
 *
 * Fragments merge into the base in file-name order: objects key by key, arrays end to end. The
 * same key from two files is an error unless both say exactly the same thing -- two areas wiring
 * one member two ways is a conflict to resolve, not an order to settle by whichever loads last.
 */
public final class Wiring {

    public static Path fragments(Path base) {
        return base.resolveSibling(base.getFileName() + ".d");
    }

    /** whether a base file or any fragment exists */
    public static boolean exists(Path base) {
        return Files.exists(base) || Files.isDirectory(fragments(base));
    }

    public static JsonObject read(Path base) throws IOException {
        JsonObject root = Files.exists(base) ? parse(base) : new JsonObject();
        for (Path f : fragmentFiles(base)) {
            merge(root, parse(f), base.getFileName() + " + " + fragments(base).getFileName() + "/" + f.getFileName(), "");
        }
        return root;
    }

    /**
     * The wiring's files one by one, in merge order, each with its name (the base file's, then each
     * fragment's "dir/file") -- for a gate that judges entries by the area that wrote them.
     */
    public static java.util.LinkedHashMap<String, JsonObject> areas(Path base) throws IOException {
        java.util.LinkedHashMap<String, JsonObject> out = new java.util.LinkedHashMap<>();
        if (Files.exists(base)) {
            out.put(base.getFileName().toString(), parse(base));
        }
        for (Path f : fragmentFiles(base)) {
            out.put(fragments(base).getFileName() + "/" + f.getFileName(), parse(f));
        }
        return out;
    }

    private static List<Path> fragmentFiles(Path base) throws IOException {
        Path dir = fragments(base);
        if (!Files.isDirectory(dir)) {
            return List.of();
        }
        try (var s = Files.list(dir)) {
            return s.filter(p -> p.getFileName().toString().endsWith(".json")).sorted().toList();
        }
    }

    private static JsonObject parse(Path p) throws IOException {
        JsonElement e = JsonParser.parseString(Files.readString(p, StandardCharsets.UTF_8));
        if (!e.isJsonObject()) {
            throw new IllegalArgumentException(p + ": a wiring table is a JSON object");
        }
        return e.getAsJsonObject();
    }

    private static void merge(JsonObject into, JsonObject from, String where, String path) {
        for (var e : from.entrySet()) {
            String k = e.getKey();
            JsonElement add = e.getValue();
            JsonElement have = into.get(k);
            if (have == null) {
                into.add(k, add.deepCopy());
            } else if (path.isEmpty() && have.isJsonObject() && add.isJsonObject()) {
                // a section: its entries merge one by one
                merge(have.getAsJsonObject(), add.getAsJsonObject(), where, k);
            } else if (path.isEmpty() && have.isJsonArray() && add.isJsonArray()) {
                JsonArray arr = have.getAsJsonArray();
                for (JsonElement x : add.getAsJsonArray()) {
                    arr.add(x.deepCopy());
                }
            } else if (!have.equals(add)) {
                throw new IllegalArgumentException(where + ": " + (path.isEmpty() ? "" : path + " ") + k
                    + " is wired differently in two files: " + have + " vs " + add);
            }
        }
    }

    private Wiring() {
    }
}
