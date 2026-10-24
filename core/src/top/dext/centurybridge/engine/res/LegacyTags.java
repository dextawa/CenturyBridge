package top.dext.centurybridge.engine.res;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonPrimitive;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;
import top.dext.centurybridge.res.Json;
import top.dext.centurybridge.res.Release;

/**
 * Vanilla data-tag drift on the data side of a climb step (a mod's data/**.json naming a vanilla tag).
 *
 * <p>W can keep a vanilla tag's id while changing what it holds (26.1 narrowed minecraft:dirt; 1.21.4 widened
 * minecraft:replaceable_by_trees by every small flower). The step layer ships a tag holding V's members under its
 * own id (core/tools/gen_tag_compat.py: centurybridge:compat/&lt;V_&gt;/&lt;tag&gt;), and the step's wiring maps each
 * drifted vanilla tag to it ({@code tagMap}: "&lt;registry&gt; minecraft:&lt;tag&gt;" -&gt; {"to": id, "mirror": bool}). A tag W
 * removed and the layer restores under its own id needs nothing here and is not in the map.
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
 * <li>A mod's own file for a mapped vanilla tag with {@code mirror} (widened and changed tags, whose compat tag
 * does not name #minecraft:&lt;tag&gt; -- that would bring W's new vanilla members) is copied into the compat tag as
 * well, so what the mod added to the vanilla tag still counts for V-era readers. Narrowed compat tags name
 * #minecraft:&lt;tag&gt; and get the mod's additions through it.</li>
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
     * @param mirror the keys of map whose mod tag files are copied into the target tag
     */
    static void apply(Map<String, byte[]> entries, Map<String, String> map, Set<String> mirror, String to,
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
            String id = tagFile == null || !mirror.contains(tagFile) ? null : map.get(tagFile);
            if (id != null) {
                String ns = id.substring(0, id.indexOf(':'));
                String path = id.substring(id.indexOf(':') + 1);
                String dir = Release.before(to, "1.21") ? pluralDir(fileRegistry) : fileRegistry;
                String target = "data/" + ns + "/tags/" + dir + "/" + path + ".json";
                if (!entries.containsKey(target)) {
                    mirrors.put(target, e.getValue());
                    out.notes.add("mirrored " + n + " into " + id + " (the mod's additions to a vanilla tag W changed)");
                }
            }
        }
        for (var m : mirrors.entrySet()) {
            entries.put(m.getKey(), m.getValue());
            out.generated++;
        }
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
