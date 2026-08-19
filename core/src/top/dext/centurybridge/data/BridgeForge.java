package top.dext.centurybridge.data;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

/**
 * Turns a kill-list row into a self-contained work order a small model can
 * fill in.
 *
 * The split matters: the model writes ONLY a method body, against signatures
 * and a target-class API surface this class extracts from the real stubs.
 * Everything structural -- package, imports, the @Mixin vs Statics decision,
 * the JSON wiring -- is generated deterministically here, because that is
 * where cheap models fail and where a wrong guess is silent at compile time.
 *
 * Shim kind is forced by JVM and Mixin rules, never by the model:
 *   MIXIN_OVERLOAD  instance method on a concrete class -> add the old
 *                   descriptor back as an overload; the JVM picks by descriptor
 *   STATIC_SHIM     static method (Mixin forbids non-private statics) or a
 *                   method on an interface (Mixin cannot add instance methods)
 *                   -> body lives in Statics, call sites are redirected
 *   FIELD_SHIM      dead static field -> constant rebuilt in Statics
 *   TOMBSTONE       no new-side equivalent exists; fail lazily with a reason
 */
public final class BridgeForge {

    public record Order(String id, String kind, String side, String fate,
                        String owner, String name, String oldDesc, String newDesc,
                        String shimKind, List<String> ownerApi, int corpusRefs) {}

    public static void run(Path openTsv, Path newStub, Path auditTsv, Path outJson)
            throws IOException {
        StubDiff.Index newIdx = StubDiff.index(newStub);

        Map<String, Integer> refs = new java.util.HashMap<>();
        if (auditTsv != null && Files.exists(auditTsv)) {
            for (String line : Files.readAllLines(auditTsv)) {
                String[] p = line.split("\t");
                if (p.length >= 3 && !p[0].equals("count")) {
                    String base = p[2].split("\\(")[0].split(":")[0];
                    try {
                        refs.merge(base, Integer.parseInt(p[0]), Integer::sum);
                    } catch (NumberFormatException ignored) {
                        // header or malformed row
                    }
                }
            }
        }

        List<Order> orders = new ArrayList<>();
        List<String> lines = Files.readAllLines(openTsv);
        for (int i = 1; i < lines.size(); i++) {
            String line = lines.get(i);
            if (line.isBlank()) {
                continue;
            }
            String[] p = line.split("\t", -1);
            String side = p[0];
            String fate = p[1];
            String symbol = p[2];
            String oldDesc = p.length > 3 ? p[3] : "";
            String detail = p.length > 4 ? p[4] : "";

            int dot = symbol.lastIndexOf('.');
            String owner = symbol.substring(0, dot);
            String name = symbol.substring(dot + 1);
            boolean isField = !oldDesc.startsWith("(");

            String shimKind = shimKind(fate, isField, owner, name, oldDesc, newIdx);
            String newDesc = fate.equals("DESC_CHANGED") ? detail
                           : fate.equals("MOVED") ? detail : "";

            List<String> api = apiSurface(newIdx, owner, name);
            // Types that only appear in the NEW signature are exactly the ones a
            // model has never seen and will otherwise invent accessors for, so
            // ship their surface too.
            for (String t : newTypes(oldDesc, newDesc)) {
                if (t.equals(owner) || !newIdx.classes.contains(t)) {
                    continue;
                }
                api.add("-- " + t.substring(t.lastIndexOf('/') + 1) + " --");
                api.addAll(apiSurface(newIdx, t, ""));
            }

            orders.add(new Order(
                owner.replace('/', '.') + "#" + name + oldDesc,
                isField ? "field" : "method",
                side, fate, owner, name, oldDesc, newDesc, shimKind,
                api,
                refs.getOrDefault(symbol, 0)));
        }

        orders.sort((a, b) -> b.corpusRefs() - a.corpusRefs());

        JsonArray arr = new JsonArray();
        for (Order o : orders) {
            JsonObject j = new JsonObject();
            j.addProperty("id", o.id());
            j.addProperty("kind", o.kind());
            j.addProperty("side", o.side());
            j.addProperty("fate", o.fate());
            j.addProperty("owner", o.owner());
            j.addProperty("name", o.name());
            j.addProperty("oldDesc", o.oldDesc());
            j.addProperty("newDesc", o.newDesc());
            j.addProperty("shimKind", o.shimKind());
            j.addProperty("corpusRefs", o.corpusRefs());
            JsonArray api = new JsonArray();
            o.ownerApi().forEach(api::add);
            j.add("ownerApi", api);
            arr.add(j);
        }
        Files.write(outJson, new GsonBuilder().setPrettyPrinting().create().toJson(arr)
            .getBytes(StandardCharsets.UTF_8));

        Map<String, Integer> byKind = new java.util.TreeMap<>();
        orders.forEach(o -> byKind.merge(o.shimKind(), 1, Integer::sum));
        System.out.println("work orders: " + orders.size() + " " + byKind);
        System.out.println("  -> " + outJson);
    }

    /** JVM + Mixin rules decide this, not the model. */
    private static String shimKind(String fate, boolean isField, String owner, String name,
                                   String oldDesc, StubDiff.Index newIdx) {
        if (fate.equals("CLASS_GONE")) {
            return "TOMBSTONE";
        }
        if (isField) {
            return "FIELD_SHIM";
        }
        if (name.equals("<init>")) {
            return "TOMBSTONE"; // constructors cannot be added back by any mechanism
        }
        // a method with no new-side namesake at all has nothing to delegate to
        boolean anyNamesake = newIdx.methods.getOrDefault(owner, Map.of()).containsKey(name);
        if (!anyNamesake && fate.equals("GONE")) {
            return "STATIC_SHIM"; // rebuild from scratch in Statics, redirect call sites
        }
        return "MIXIN_OVERLOAD";
    }

    /** Minecraft types present in the new descriptor but absent from the old one. */
    private static Set<String> newTypes(String oldDesc, String newDesc) {
        Set<String> before = mcTypes(oldDesc);
        Set<String> after = mcTypes(newDesc);
        after.removeAll(before);
        return after;
    }

    private static Set<String> mcTypes(String desc) {
        Set<String> out = new java.util.LinkedHashSet<>();
        java.util.regex.Matcher m = java.util.regex.Pattern
            .compile("L(net/minecraft/[^;]+);").matcher(desc == null ? "" : desc);
        while (m.find()) {
            out.add(m.group(1));
        }
        return out;
    }

    /** The new-side members of this owner, so the model can see what to delegate to. */
    private static List<String> apiSurface(StubDiff.Index idx, String owner, String name) {
        List<String> out = new ArrayList<>();
        Map<String, Set<String>> methods = idx.methods.getOrDefault(owner, Map.of());
        // the same-named survivors first: the delegation target is almost always here
        if (name != null && !name.isEmpty()) {
            for (String desc : new TreeSet<>(methods.getOrDefault(name, Set.of()))) {
                out.add(name + desc);
            }
        }
        for (Map.Entry<String, Set<String>> e : new java.util.TreeMap<>(methods).entrySet()) {
            if (name != null && e.getKey().equals(name)) {
                continue;
            }
            for (String desc : new TreeSet<>(e.getValue())) {
                out.add(e.getKey() + desc);
            }
            if (out.size() > 120) {
                out.add("... (truncated)");
                return out;
            }
        }
        for (Map.Entry<String, Set<String>> e
                : new java.util.TreeMap<>(idx.fields.getOrDefault(owner, Map.of())).entrySet()) {
            for (String desc : new TreeSet<>(e.getValue())) {
                out.add(e.getKey() + ":" + desc);
            }
            if (out.size() > 150) {
                out.add("... (truncated)");
                return out;
            }
        }
        return out;
    }

    private BridgeForge() {
    }
}
