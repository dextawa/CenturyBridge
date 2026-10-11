package top.dext.centurybridge.engine.res;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import top.dext.centurybridge.res.Json;
import top.dext.centurybridge.res.LegacyItems;
import top.dext.centurybridge.res.LegacyRecipes;
import top.dext.centurybridge.res.Release;
import top.dext.centurybridge.res.VanillaIds;

/**
 * The resource side of a conversion: a mod's data pack and resource pack, upgraded
 * across every boundary between its source release and the target -- the formats
 * the target's loaders read are not the ones the mod was written in, and a file the
 * loader cannot place or parse is skipped without a word in the log.
 *
 * Each step names the release whose format it produces and runs only when the
 * conversion crosses into it. Files are rewritten only where a step changed them;
 * everything a step could not carry is counted and reported.
 */
public final class ResourceTranslator {

    /** what one jar's resources went through */
    public static final class Outcome {
        public int moved;
        public int rewritten;
        public int generated;
        /** loss -> occurrences */
        public final Map<String, Integer> losses = new TreeMap<>();
        public final List<String> notes = new ArrayList<>();

        void lost(String what) {
            losses.merge(what, 1, Integer::sum);
        }

        public boolean changedAnything() {
            return moved + rewritten + generated > 0;
        }
    }

    /**
     * 1.21 renamed the data pack directories to singular nouns; the old paths are
     * not read any more (no fallback), so every recipe, loot table, tag and
     * advancement of an unconverted mod is silently absent from 1.21 on.
     */
    private static final Map<String, String> DIRS_1_21 = new LinkedHashMap<>();

    static {
        DIRS_1_21.put("structures", "structure");
        DIRS_1_21.put("advancements", "advancement");
        DIRS_1_21.put("recipes", "recipe");
        DIRS_1_21.put("loot_tables", "loot_table");
        DIRS_1_21.put("predicates", "predicate");
        DIRS_1_21.put("item_modifiers", "item_modifier");
        DIRS_1_21.put("functions", "function");
        DIRS_1_21.put("tags/functions", "tags/function");
        DIRS_1_21.put("tags/items", "tags/item");
        DIRS_1_21.put("tags/blocks", "tags/block");
        DIRS_1_21.put("tags/entity_types", "tags/entity_type");
        DIRS_1_21.put("tags/fluids", "tags/fluid");
        DIRS_1_21.put("tags/game_events", "tags/game_event");
    }

    public static Outcome apply(Map<String, byte[]> entries, String from, String to) {
        return apply(entries, from, to, java.util.Set.of());
    }

    /**
     * @param runtimeTypes the CenturyBridge resource types the target runtime registers
     *        (shims-<ver>.json "resourceTypes"); a translation needing one the target does
     *        not have falls back to vanilla types and reports what that loses
     */
    public static Outcome apply(Map<String, byte[]> entries, String from, String to, java.util.Set<String> runtimeTypes) {
        Outcome out = new Outcome();
        if (Release.before(from, "1.21") && Release.reaches(to, "1.21")) {
            dataDirs(entries, out);
        }
        recipes(entries, from, to, out);
        lootAndAdvancements(entries, from, to, out);
        worldgen(entries, from, to, out);
        vanillaIds(entries, from, to, out);
        if (Release.before(from, "1.20.5") && Release.reaches(to, "1.20.5")) {
            // item arguments, particle options and NBT paths in commands changed with the
            // component rework; a function file is a command script, not data -- not translated
            long fns = entries.keySet().stream().filter(n -> n.startsWith("data/") && n.endsWith(".mcfunction")).count();
            if (fns > 0) {
                out.lost("function files (.mcfunction) not translated: command syntax changed at 1.20.5 (x" + fns + ")");
            }
        }
        if (Release.before(from, "1.21.4") && Release.reaches(to, "1.21.4")) {
            itemDefinitions(entries, runtimeTypes, out);
        }
        return out;
    }

    /**
     * 1.21.4 renders an item through its item model definition, assets/<ns>/items/<id>.json;
     * an item without one is the missing model. Before, models/item/<id>.json was the
     * model itself, its "overrides" switched models on predicate values, and the item's
     * registered colour provider tinted every quad with a tint index. Each mod item model
     * gets the definition 1.20.1 implied: the model, the overrides as CenturyBridge's
     * legacy_overrides model (1.20.1 ModelOverrideList semantics over the predicates mods
     * register, see the 1.21.4 runtime), and one legacy_color tint per tint index the
     * mod's models use (the registered colour provider, or none, exactly as before).
     *
     * Models under the minecraft namespace are resource-pack overrides of vanilla items,
     * which keep vanilla's own definitions; their overrides no longer switch anything.
     */
    private static void itemDefinitions(Map<String, byte[]> entries, java.util.Set<String> runtimeTypes, Outcome out) {
        boolean overridesType = runtimeTypes.contains("centurybridge:legacy_overrides");
        boolean colorType = runtimeTypes.contains("centurybridge:legacy_color");
        int maxTint = 0;
        for (var e : entries.entrySet()) {
            String n = e.getKey();
            if (n.startsWith("assets/") && n.contains("/models/") && n.endsWith(".json")) {
                maxTint = Math.max(maxTint, highestTint(Json.tryParse(e.getValue())));
            }
        }
        Map<String, byte[]> add = new LinkedHashMap<>();
        for (var e : entries.entrySet()) {
            String n = e.getKey();
            if (!n.startsWith("assets/") || !n.endsWith(".json")) {
                continue;
            }
            int ns = n.indexOf('/', 7);
            if (ns < 0 || !n.startsWith("/models/item/", ns)) {
                continue;
            }
            String namespace = n.substring(7, ns);
            String path = n.substring(ns + "/models/item/".length(), n.length() - ".json".length());
            JsonElement j = Json.tryParse(e.getValue());
            if (j == null || !j.isJsonObject()) {
                continue;
            }
            JsonObject model = j.getAsJsonObject();
            if (namespace.equals("minecraft")) {
                if (model.has("overrides")) {
                    out.lost("item model overrides on vanilla item minecraft:" + path
                        + " (vanilla's 1.21.4 definition is used; they switch nothing)");
                }
                continue;
            }
            String def = "assets/" + namespace + "/items/" + path + ".json";
            if (entries.containsKey(def)) {
                continue; // the mod ships one (written for 1.21.4)
            }
            JsonObject m = new JsonObject();
            boolean overrides = model.has("overrides") && model.get("overrides").isJsonArray()
                && model.getAsJsonArray("overrides").size() > 0;
            if (overrides && !overridesType) {
                out.lost("item model overrides of " + namespace + ":" + path
                    + " (this target's runtime has no legacy_overrides model: the base model only)");
                overrides = false;
            }
            m.addProperty("type", overrides ? "centurybridge:legacy_overrides" : "minecraft:model");
            m.addProperty("model", namespace + ":item/" + path);
            if (overrides) {
                m.add("overrides", model.get("overrides").deepCopy());
            }
            if (colorType) {
                com.google.gson.JsonArray tints = new com.google.gson.JsonArray();
                for (int i = 0; i <= maxTint; i++) {
                    JsonObject t = new JsonObject();
                    t.addProperty("type", "centurybridge:legacy_color");
                    t.addProperty("index", i);
                    tints.add(t);
                }
                m.add("tints", tints);
            }
            JsonObject root = new JsonObject();
            root.add("model", m);
            add.put(def, Json.write(root));
        }
        entries.putAll(add);
        out.generated += add.size();
    }

    /** the highest tint index a model can ask for: a face's tintindex, an item/generated layer, or its vanilla parent's */
    private static int highestTint(JsonElement j) {
        if (j == null || !j.isJsonObject()) {
            return 0;
        }
        int max = 0;
        JsonObject o = j.getAsJsonObject();
        if (o.has("elements") && o.get("elements").isJsonArray()) {
            for (JsonElement el : o.getAsJsonArray("elements")) {
                if (el.isJsonObject() && el.getAsJsonObject().has("faces")) {
                    for (var f : el.getAsJsonObject().getAsJsonObject("faces").entrySet()) {
                        if (f.getValue().isJsonObject() && f.getValue().getAsJsonObject().has("tintindex")) {
                            max = Math.max(max, f.getValue().getAsJsonObject().get("tintindex").getAsInt());
                        }
                    }
                }
            }
        }
        if (o.has("textures") && o.get("textures").isJsonObject()) {
            for (String k : o.getAsJsonObject("textures").keySet()) {
                if (k.matches("layer\\d+")) {
                    max = Math.max(max, Integer.parseInt(k.substring(5)));
                }
            }
        }
        // a vanilla parent draws its own tinted layers (template_spawn_egg: two)
        if (o.has("parent") && o.get("parent").isJsonPrimitive()) {
            String p = o.get("parent").getAsString();
            if (!p.contains(":") || p.startsWith("minecraft:")) {
                max = Math.max(max, top.dext.centurybridge.res.VanillaItemLayers.highest(p.substring(p.indexOf(':') + 1)));
            }
        }
        return max;
    }

    /**
     * Loot tables, predicates, item modifiers and advancements -- all read by vanilla codecs
     * whatever mod wrote them (a mod's own loot function or condition type keeps its own
     * fields, which pass through untouched).
     */
    private static void lootAndAdvancements(Map<String, byte[]> entries, String from, String to, Outcome out) {
        for (var e : entries.entrySet()) {
            String n = e.getKey();
            if (!n.startsWith("data/") || !n.endsWith(".json")) {
                continue;
            }
            int ns = n.indexOf('/', 5);
            if (ns < 0) {
                continue;
            }
            int dirEnd = n.indexOf('/', ns + 1);
            if (dirEnd < 0) {
                continue;
            }
            String dir = n.substring(ns + 1, dirEnd);
            String kind = switch (dir) {
                case "loot_table", "loot_tables" -> "loot table";
                case "advancement", "advancements" -> "advancement";
                case "predicate", "predicates" -> "predicate";
                case "item_modifier", "item_modifiers" -> "item modifier";
                default -> null;
            };
            if (kind == null) {
                continue;
            }
            JsonElement j = Json.tryParse(e.getValue());
            if (j == null) {
                continue;
            }
            LegacyItems.Losses losses = what -> out.lost(kind + ": " + what);
            JsonElement up;
            try {
                up = switch (kind) {
                    case "loot table" -> j.isJsonObject() ? top.dext.centurybridge.res.LegacyLoot.table(j.getAsJsonObject(), from, to, losses) : j;
                    case "advancement" -> j.isJsonObject() ? top.dext.centurybridge.res.LegacyAdvancements.upgrade(j.getAsJsonObject(), from, to, losses) : j;
                    case "predicate" -> j.isJsonArray() ? top.dext.centurybridge.res.LegacyLoot.conditions(j.getAsJsonArray(), from, to, losses)
                        : j.isJsonObject() ? top.dext.centurybridge.res.LegacyLoot.condition(j.getAsJsonObject().deepCopy(), from, to, losses) : j;
                    default -> {
                        com.google.gson.JsonArray l = j.isJsonArray() ? j.getAsJsonArray() : wrap(j);
                        com.google.gson.JsonArray f = top.dext.centurybridge.res.LegacyLoot.functions(l, from, to, losses);
                        yield j.isJsonArray() || f.size() != 1 ? f : f.get(0);
                    }
                };
            } catch (RuntimeException ex) {
                out.lost(kind + " left as written (" + ex + "): " + n);
                continue;
            }
            if (!up.equals(j)) {
                e.setValue(Json.write(up));
                out.rewritten++;
            }
        }
    }

    /** worldgen and dimension files: registries that load all-or-nothing (LegacyWorldgen) */
    private static void worldgen(Map<String, byte[]> entries, String from, String to, Outcome out) {
        for (var e : entries.entrySet()) {
            String n = e.getKey();
            if (!n.startsWith("data/") || !n.endsWith(".json")
                    || !(n.contains("/worldgen/") || n.contains("/dimension_type/") || n.contains("/dimension/"))) {
                continue;
            }
            JsonElement j = Json.tryParse(e.getValue());
            if (j == null) {
                continue;
            }
            JsonElement up;
            try {
                up = top.dext.centurybridge.res.LegacyWorldgen.upgrade(j, n, from, to, what -> out.lost("worldgen: " + what));
            } catch (RuntimeException ex) {
                out.lost("worldgen file left as written (" + ex + "): " + n);
                continue;
            }
            if (!up.equals(j)) {
                e.setValue(Json.write(up));
                out.rewritten++;
            }
        }
    }

    private static com.google.gson.JsonArray wrap(JsonElement e) {
        com.google.gson.JsonArray a = new com.google.gson.JsonArray();
        a.add(e);
        return a;
    }

    /** keys whose string value is a registry id even when written without a namespace */
    private static final java.util.Set<String> ID_KEYS = java.util.Set.of(
        "item", "id", "name", "Name", "block", "blocks", "items", "result", "parent", "fluid", "entity_type",
        "enchantment", "enchantments", "recipe", "recipes", "attribute", "values");

    /**
     * Vanilla ids renamed since the source release (minecraft:grass -> short_grass, ...) are
     * renamed in every data file -- the game fixes world data, never a data pack; attribute
     * ids lose the category prefix 1.21.2 dropped. A tag file's required reference to a
     * vanilla tag the target no longer ships fails the whole tag: it becomes optional (the
     * tag loads, without that part) and is reported; a reference anywhere else is reported.
     */
    private static void vanillaIds(Map<String, byte[]> entries, String from, String to, Outcome out) {
        java.util.Set<String> ownTags = new java.util.HashSet<>();
        for (String n : entries.keySet()) {
            String t = tagOf(n);
            if (t != null) {
                ownTags.add(t);
            }
        }
        for (var e : entries.entrySet()) {
            String n = e.getKey();
            if (!n.startsWith("data/") || !n.endsWith(".json")) {
                continue;
            }
            JsonElement j = Json.tryParse(e.getValue());
            if (j == null) {
                continue;
            }
            String tagFile = tagOf(n);
            String registry = tagFile == null ? null : tagFile.substring(0, tagFile.indexOf(' '));
            boolean[] changed = {false};
            JsonElement up = walkIds(j, null, from, to, changed);
            if (tagFile != null && up.isJsonObject() && up.getAsJsonObject().has("values")) {
                com.google.gson.JsonArray vals = up.getAsJsonObject().getAsJsonArray("values");
                for (int i = 0; i < vals.size(); i++) {
                    JsonElement v = vals.get(i);
                    String ref = v.isJsonPrimitive() ? v.getAsString()
                        : v.isJsonObject() && v.getAsJsonObject().has("id") ? v.getAsJsonObject().get("id").getAsString() : null;
                    boolean required = !v.isJsonObject() || !v.getAsJsonObject().has("required")
                        || v.getAsJsonObject().get("required").getAsBoolean();
                    if (ref == null || !ref.startsWith("#") || !required) {
                        continue;
                    }
                    String id = ref.substring(1).contains(":") ? ref.substring(1) : "minecraft:" + ref.substring(1);
                    if (VanillaIds.tagGone(registry, id, from, to) && !ownTags.contains(registry + " " + id)) {
                        JsonObject opt = new JsonObject();
                        opt.addProperty("id", ref);
                        opt.addProperty("required", false);
                        vals.set(i, opt);
                        changed[0] = true;
                        out.lost("tag " + n + " named vanilla tag " + ref + ", gone in " + to + " (kept, now optional)");
                    }
                }
            } else if (tagFile == null) {
                goneTagRefs(up, from, to, n, ownTags, out);
            }
            if (changed[0]) {
                e.setValue(Json.write(up));
                out.rewritten++;
            }
        }
    }

    /** "item minecraft:planks" for data/minecraft/tags/item(s)/planks.json; null for any other file */
    private static String tagOf(String n) {
        if (!n.startsWith("data/") || !n.endsWith(".json")) {
            return null;
        }
        String[] p = n.split("/", 5);
        if (p.length < 5 || !p[2].equals("tags")) {
            return null;
        }
        String reg = p[3];
        String rest = p[4];
        if (reg.equals("worldgen")) {
            int s = rest.indexOf('/');
            if (s < 0) {
                return null;
            }
            reg = "worldgen/" + rest.substring(0, s);
            rest = rest.substring(s + 1);
        }
        reg = switch (reg) {
            case "blocks" -> "block";
            case "items" -> "item";
            case "entity_types" -> "entity_type";
            case "fluids" -> "fluid";
            case "game_events" -> "game_event";
            case "functions" -> "function";
            default -> reg;
        };
        return reg + " " + p[1] + ":" + rest.substring(0, rest.length() - 5);
    }

    private static JsonElement walkIds(JsonElement e, String key, String from, String to, boolean[] changed) {
        if (e.isJsonObject()) {
            JsonObject o = e.getAsJsonObject();
            for (var en : o.entrySet()) {
                JsonElement v = walkIds(en.getValue(), en.getKey(), from, to, changed);
                if (v != en.getValue()) {
                    en.setValue(v);
                }
            }
            return o;
        }
        if (e.isJsonArray()) {
            com.google.gson.JsonArray a = e.getAsJsonArray();
            for (int i = 0; i < a.size(); i++) {
                JsonElement v = walkIds(a.get(i), key, from, to, changed);
                if (v != a.get(i)) {
                    a.set(i, v);
                }
            }
            return a;
        }
        if (!e.isJsonPrimitive() || !e.getAsJsonPrimitive().isString()) {
            return e;
        }
        String s = e.getAsString();
        String q = s.contains(":") ? s : key != null && ID_KEYS.contains(key) ? "minecraft:" + s : null;
        if (q == null || q.startsWith("#")) {
            return e;
        }
        String r = "attribute".equals(key) ? VanillaIds.attribute(q, from, to) : VanillaIds.renamed(q, from, to);
        if (r.equals(q)) {
            return e;
        }
        changed[0] = true;
        return new com.google.gson.JsonPrimitive(r);
    }

    /** a data file (not a tag) naming a vanilla tag the target does not ship: it cannot be made optional there */
    private static void goneTagRefs(JsonElement e, String from, String to, String file, java.util.Set<String> ownTags,
            Outcome out) {
        if (e.isJsonObject()) {
            for (var en : e.getAsJsonObject().entrySet()) {
                goneTagRefs(en.getValue(), from, to, file, ownTags, out);
            }
        } else if (e.isJsonArray()) {
            for (JsonElement x : e.getAsJsonArray()) {
                goneTagRefs(x, from, to, file, ownTags, out);
            }
        } else if (e.isJsonPrimitive() && e.getAsJsonPrimitive().isString() && e.getAsString().startsWith("#")) {
            String id = e.getAsString().substring(1);
            id = id.contains(":") ? id : "minecraft:" + id;
            for (String reg : new String[] {"item", "block", "fluid", "entity_type"}) {
                if (VanillaIds.tagGone(reg, id, from, to) && !ownTags.contains(reg + " " + id)) {
                    out.lost(file + " names vanilla " + reg + " tag #" + id + ", gone in " + to);
                    return;
                }
            }
        }
    }

    /** data/<ns>/<dir>/... -> its 1.21 name; a file already at the new path wins (it was written for 1.21) */
    private static void dataDirs(Map<String, byte[]> entries, Outcome out) {
        Map<String, String> moves = new LinkedHashMap<>();
        for (String name : entries.keySet()) {
            String moved = movedPath(name);
            if (moved != null) {
                moves.put(name, moved);
            }
        }
        for (var m : moves.entrySet()) {
            byte[] data = entries.remove(m.getKey());
            if (entries.containsKey(m.getValue())) {
                out.notes.add("kept " + m.getValue() + " over the pre-1.21 " + m.getKey());
                continue;
            }
            entries.put(m.getValue(), data);
            out.moved++;
        }
    }

    static String movedPath(String name) {
        if (!name.startsWith("data/")) {
            return null;
        }
        int ns = name.indexOf('/', 5);
        if (ns < 0) {
            return null;
        }
        String rest = name.substring(ns + 1);
        for (var d : DIRS_1_21.entrySet()) {
            if (rest.startsWith(d.getKey() + "/")) {
                return name.substring(0, ns + 1) + d.getValue() + rest.substring(d.getKey().length());
            }
        }
        return null;
    }

    private static void recipes(Map<String, byte[]> entries, String from, String to, Outcome out) {
        if (!Release.reaches(to, "1.20.5")) {
            return;
        }
        for (var e : entries.entrySet()) {
            String name = e.getKey();
            if (!name.startsWith("data/") || !name.endsWith(".json")
                    || !(name.contains("/recipe/") || name.contains("/recipes/"))) {
                continue;
            }
            int ns = name.indexOf('/', 5);
            String dir = name.substring(ns + 1, name.indexOf('/', ns + 1));
            if (!dir.equals("recipe") && !dir.equals("recipes")) {
                continue;
            }
            JsonElement j = Json.tryParse(e.getValue());
            if (j == null || !j.isJsonObject()) {
                continue;
            }
            JsonObject r = j.getAsJsonObject();
            if (!LegacyRecipes.vanillaType(r)) {
                continue;
            }
            LegacyItems.Losses losses = what -> out.lost("recipe: " + what);
            JsonObject up;
            try {
                up = LegacyRecipes.upgrade(r, to, losses);
            } catch (RuntimeException ex) {
                out.lost("recipe left as written (" + ex.getClass().getSimpleName() + "): " + name);
                continue;
            }
            if (!up.equals(r)) {
                e.setValue(Json.write(up));
                out.rewritten++;
            }
        }
    }

    private ResourceTranslator() {
    }
}
