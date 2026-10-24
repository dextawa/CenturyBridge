package top.dext.centurybridge.engine.res;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonPrimitive;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;
import top.dext.centurybridge.res.Json;
import top.dext.centurybridge.res.Release;

/**
 * Vanilla data-tag drift on the data side of a climb step (a mod's data/**.json naming a vanilla tag).
 *
 * <p>W can keep a vanilla tag's id while changing what it holds (1.21.4 widened minecraft:replaceable_by_trees by
 * every small flower), or rename it (26.1: big_dripleaf_placeable is supports_big_dripleaf, same members, the same
 * blocks read it). The step's wiring maps each such vanilla tag to the tag carrying its V meaning on W
 * (core/tools/gen_tag_compat.py; {@code tagMap}: "&lt;registry&gt; minecraft:&lt;tag&gt;" -&gt; {"to": id, "mirror": [ids]}):
 * the layer's compat tag holding V's members (centurybridge:compat/&lt;V_&gt;/&lt;tag&gt;), or the W tag vanilla acts on
 * (a rename; a W tag with exactly V's members that names #minecraft:&lt;tag&gt; itself, as 26.1 substrate_overworld
 * does for 1.21.11 dirt). A tag W removed that the layer restores under its own id maps to itself, and only when
 * part of its readers moved to another W tag with the same members (26.1 cauldron_can_remove_dye of dyeable).
 *
 * <p>Two things happen to a V-era mod's data:
 * <ul>
 * <li>Every reference to a mapped tag -- "#minecraft:dirt" anywhere, a bare id under a key that names a tag (a
 * recipe ingredient's "tag", a loot "minecraft:tag" entry's "name", a matching_block_tag or tag_match "tag") -- is
 * rewritten to the compat id, so the recipe, loot table, worldgen feature, advancement, enchantment or tag file
 * means V's members as it did. The registry a reference names is read from where it stands: a tag file's own
 * registry; a key that names one registry ("blocks", "items", "biomes", "infiniburn", "supported_items", ...); a
 * recipe's ingredients (items). Where nothing says, the reference is rewritten only when the step maps that id in
 * exactly one registry; otherwise it is left and reported.</li>
 * <li>A mod's own file for a mapped vanilla tag is copied into each {@code mirror} id as well (merged with what the
 * jar already has there), so what the mod added to the vanilla tag still reaches the readers: a widened or changed
 * tag's compat tag (which does not name #minecraft:&lt;tag&gt; -- that would bring W's new vanilla members), a renamed
 * tag's successor, a restored tag's partial successors. Narrowed compat tags name #minecraft:&lt;tag&gt; and get the
 * mod's additions through it.</li>
 * </ul>
 */
final class LegacyTags {

    /** keys whose tag references name one registry (data formats of 1.20.1 .. 26.x) */
    private static final Map<String, String> KEY_REGISTRY = new HashMap<>();

    static {
        for (String k : new String[] {"items", "item", "ingredient", "ingredients", "base", "addition", "template",
            "input", "supported_items", "primary_items", "repair_materials"}) {
            KEY_REGISTRY.put(k, "item");
        }
        for (String k : new String[] {"blocks", "block", "replaceable", "can_grow_through", "cannot_replace",
            "root_replaceable", "rottable_blocks", "infiniburn", "immune_blocks", "invalid_blocks", "can_place_on",
            "can_break"}) {
            KEY_REGISTRY.put(k, "block");
        }
        for (String k : new String[] {"biomes", "preferred_biomes", "biome"}) {
            KEY_REGISTRY.put(k, "worldgen/biome");
        }
        KEY_REGISTRY.put("structures", "worldgen/structure");
        KEY_REGISTRY.put("fluids", "fluid");
        KEY_REGISTRY.put("fluid", "fluid");
        KEY_REGISTRY.put("exclusive_set", "enchantment");
        KEY_REGISTRY.put("enchantments", "enchantment");
        KEY_REGISTRY.put("options", "enchantment");
        KEY_REGISTRY.put("entity_types", "entity_type");
        KEY_REGISTRY.put("entity_type", "entity_type");
        KEY_REGISTRY.put("damage_type", "damage_type");
    }

    private LegacyTags() {
    }

    /**
     * @param map "&lt;registry&gt; minecraft:&lt;tag&gt;" -&gt; the id carrying V's members, the step's wiring {@code tagMap}
     *        (empty outside a climb step)
     * @param mirror per key of map, the tags a mod's own file for that vanilla tag is copied into
     */
    static void apply(Map<String, byte[]> entries, Map<String, String> map, Map<String, java.util.List<String>> mirror, String to,
            ResourceTranslator.Outcome out) {
        if (map.isEmpty()) {
            return;
        }
        Map<String, java.util.List<String>> registriesOf = new HashMap<>();
        for (String k : map.keySet()) {
            int sp = k.indexOf(' ');
            registriesOf.computeIfAbsent(k.substring(sp + 1), x -> new java.util.ArrayList<>()).add(k.substring(0, sp));
        }
        Map<String, byte[]> mirrors = new LinkedHashMap<>();
        for (var e : entries.entrySet()) {
            String n = e.getKey();
            if (!n.startsWith("data/") || !n.endsWith(".json")) {
                continue;
            }
            JsonElement j = Json.tryParse(e.getValue());
            if (j == null) {
                continue;
            }
            String tagFile = ResourceTranslator.tagOf(n);
            String fileRegistry = tagFile == null ? null : tagFile.substring(0, tagFile.indexOf(' '));
            String[] parts = n.split("/", 4);
            boolean recipe = parts.length > 2 && (parts[2].equals("recipe") || parts[2].equals("recipes"));
            boolean[] changed = {false};
            JsonElement up = walk(j, null, null, fileRegistry != null ? fileRegistry : recipe ? "item" : null,
                map, registriesOf, n, changed, out);
            if (changed[0]) {
                e.setValue(Json.write(up));
                out.rewritten++;
            }
            for (String id : tagFile == null ? java.util.List.<String>of() : mirror.getOrDefault(tagFile, java.util.List.of())) {
                String ns = id.substring(0, id.indexOf(':'));
                String path = id.substring(id.indexOf(':') + 1);
                String dir = Release.before(to, "1.21") ? pluralDir(fileRegistry) : fileRegistry;
                String target = "data/" + ns + "/tags/" + dir + "/" + path + ".json";
                if (!target.equals(n)) {
                    mirrors.merge(target, e.getValue(), LegacyTags::mergeTag);
                    out.notes.add("mirrored " + n + " into " + id + " (the mod's additions to a vanilla tag W changed or renamed)");
                }
            }
        }
        for (var m : mirrors.entrySet()) {
            byte[] have = entries.get(m.getKey());
            entries.put(m.getKey(), have == null ? m.getValue() : mergeTag(have, m.getValue()));
            out.generated++;
        }
    }

    /** two tag files as one: values end to end, replace if either replaces */
    private static byte[] mergeTag(byte[] a, byte[] b) {
        JsonElement ja = Json.tryParse(a);
        JsonElement jb = Json.tryParse(b);
        if (!(ja instanceof JsonObject oa) || !(jb instanceof JsonObject ob)) {
            return a;
        }
        JsonObject m = new JsonObject();
        boolean replace = (oa.has("replace") && oa.get("replace").getAsBoolean())
            || (ob.has("replace") && ob.get("replace").getAsBoolean());
        if (replace) {
            m.addProperty("replace", true);
        }
        JsonArray vals = new JsonArray();
        for (JsonObject o : new JsonObject[] {oa, ob}) {
            if (o.has("values")) {
                vals.addAll(o.getAsJsonArray("values"));
            }
        }
        m.add("values", vals);
        return Json.write(m);
    }

    private static String pluralDir(String registry) {
        return switch (registry) {
            case "block" -> "blocks";
            case "item" -> "items";
            case "entity_type" -> "entity_types";
            case "fluid" -> "fluids";
            case "game_event" -> "game_events";
            default -> registry;
        };
    }

    /**
     * @param key the property the value stands under (arrays pass theirs on)
     * @param parent the object holding it (its "type" / "predicate_type" says what a bare "tag" or "name" is)
     * @param inherited the registry the surroundings name (a tag file's, a recipe's items), or null
     */
    private static JsonElement walk(JsonElement e, String key, JsonObject parent, String inherited,
            Map<String, String> map, Map<String, java.util.List<String>> registriesOf, String file, boolean[] changed,
            ResourceTranslator.Outcome out) {
        if (e.isJsonObject()) {
            JsonObject o = e.getAsJsonObject();
            for (var en : o.entrySet()) {
                JsonElement v = walk(en.getValue(), en.getKey(), o, inherited, map, registriesOf, file, changed, out);
                if (v != en.getValue()) {
                    en.setValue(v);
                }
            }
            return o;
        }
        if (e.isJsonArray()) {
            JsonArray a = e.getAsJsonArray();
            for (int i = 0; i < a.size(); i++) {
                JsonElement v = walk(a.get(i), key, parent, inherited, map, registriesOf, file, changed, out);
                if (v != a.get(i)) {
                    a.set(i, v);
                }
            }
            return a;
        }
        if (!e.isJsonPrimitive() || !e.getAsJsonPrimitive().isString() || key == null) {
            return e;
        }
        String s = e.getAsString();
        boolean hash = s.startsWith("#");
        String registry;
        if (hash) {
            registry = KEY_REGISTRY.getOrDefault(key, inherited);
        } else {
            registry = bareTagRegistry(key, parent, inherited);
            if (registry == null) {
                return e;
            }
        }
        String id = hash ? s.substring(1) : s;
        id = id.contains(":") ? id : "minecraft:" + id;
        java.util.List<String> regs = registriesOf.get(id);
        if (regs == null) {
            return e;
        }
        if (registry == null) {
            if (regs.size() != 1) {
                out.lost(file + " names " + s + " where its registry is unknown and " + regs
                    + " all changed it: left as written");
                return e;
            }
            registry = regs.get(0);
        }
        String t = map.get(registry + " " + id);
        if (t == null) {
            return e;
        }
        changed[0] = true;
        return new JsonPrimitive(hash ? "#" + t : t);
    }

    /** the registry of a bare tag id under key, or null when the key does not hold a tag id */
    private static String bareTagRegistry(String key, JsonObject parent, String inherited) {
        String type = parent == null ? null : str(parent, "type");
        String predicate = parent == null ? null : str(parent, "predicate_type");
        if (key.equals("name") && type != null && (type.equals("minecraft:tag") || type.equals("tag"))) {
            return "item"; // a loot table's tag entry
        }
        if (!key.equals("tag")) {
            return null;
        }
        if (type != null && (type.endsWith("matching_block_tag"))) {
            return "block";
        }
        if (predicate != null && predicate.endsWith("tag_match")) {
            return "block";
        }
        if (type != null && type.endsWith("matching_fluids")) {
            return "fluid";
        }
        // a pre-1.21.2 recipe ingredient {"tag": ...}, a pre-1.20.5 item predicate's "tag": the surroundings say
        return inherited;
    }

    private static String str(JsonObject o, String k) {
        JsonElement v = o.get(k);
        return v != null && v.isJsonPrimitive() && v.getAsJsonPrimitive().isString() ? v.getAsString() : null;
    }
}
