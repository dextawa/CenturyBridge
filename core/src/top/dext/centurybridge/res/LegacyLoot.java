package top.dext.centurybridge.res;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonPrimitive;
import java.util.Map;
import java.util.Set;

/**
 * Loot tables, loot conditions and the entity/location/block predicates inside them
 * (shared with advancements), as old data files wrote them, in the shape the target
 * reads. Rules are per boundary and driven by the shape they find, like {@link LegacyItems}.
 *
 * <ul>
 * <li>1.20.2: predicates became codecs; a block state property value must be a string
 * (1.20.1 read any primitive as one: "age": 7, "lit": true).</li>
 * <li>1.20.5: item stacks lost their NBT -- set_nbt / copy_nbt / set_contents became
 * component functions; a loot_table entry names its table under "value"; predicate sets
 * (biome, structure, block, fluid, entity type) became holder sets.</li>
 * <li>1.21: enchantments are data-driven -- looting_enchant, random_chance_with_looting,
 * enchant_randomly's list and enchant_with_levels' treasure flag became enchantment-keyed
 * forms.</li>
 * </ul>
 */
public final class LegacyLoot {

    // ------------------------------------------------------------------ tables

    public static JsonObject table(JsonObject in, String from, String to, LegacyItems.Losses losses) {
        JsonObject t = in.deepCopy();
        if (t.has("functions")) {
            t.add("functions", functions(t.getAsJsonArray("functions"), from, to, losses));
        }
        if (t.has("pools") && t.get("pools").isJsonArray()) {
            for (JsonElement p : t.getAsJsonArray("pools")) {
                if (!p.isJsonObject()) {
                    continue;
                }
                JsonObject pool = p.getAsJsonObject();
                if (pool.has("conditions")) {
                    pool.add("conditions", conditions(pool.getAsJsonArray("conditions"), from, to, losses));
                }
                if (pool.has("functions")) {
                    pool.add("functions", functions(pool.getAsJsonArray("functions"), from, to, losses));
                }
                if (pool.has("entries")) {
                    entries(pool.getAsJsonArray("entries"), from, to, losses);
                }
            }
        }
        return t;
    }

    private static void entries(JsonArray entries, String from, String to, LegacyItems.Losses losses) {
        for (JsonElement e : entries) {
            if (!e.isJsonObject()) {
                continue;
            }
            JsonObject en = e.getAsJsonObject();
            String type = id(en, "type");
            if ("minecraft:loot_table".equals(type) && crosses(from, to, "1.20.5") && en.has("name") && !en.has("value")) {
                en.add("value", en.remove("name"));
            }
            if (en.has("conditions")) {
                en.add("conditions", conditions(en.getAsJsonArray("conditions"), from, to, losses));
            }
            if (en.has("functions")) {
                en.add("functions", functions(en.getAsJsonArray("functions"), from, to, losses));
            }
            if (en.has("children")) {
                entries(en.getAsJsonArray("children"), from, to, losses);
            }
        }
    }

    // ------------------------------------------------------------------ functions

    /** a function list; one old function can become two (set_nbt into components and custom data) */
    public static JsonArray functions(JsonArray in, String from, String to, LegacyItems.Losses losses) {
        JsonArray out = new JsonArray();
        for (JsonElement e : in) {
            if (!e.isJsonObject()) {
                out.add(e);
                continue;
            }
            for (JsonObject f : function(e.getAsJsonObject().deepCopy(), from, to, losses)) {
                out.add(f);
            }
        }
        return out;
    }

    private static java.util.List<JsonObject> function(JsonObject f, String from, String to, LegacyItems.Losses losses) {
        if (f.has("conditions")) {
            f.add("conditions", conditions(f.getAsJsonArray("conditions"), from, to, losses));
        }
        String fn = id(f, "function");
        if (fn == null) {
            return java.util.List.of(f);
        }
        if (crosses(from, to, "1.20.5")) {
            switch (fn) {
                case "minecraft:set_nbt" -> {
                    Map<String, Object> nbt = LegacyItems.nbtOf(f.get("tag"), losses);
                    java.util.List<JsonObject> out = new java.util.ArrayList<>();
                    if (nbt != null) {
                        JsonObject comps = LegacyItems.components(nbt, to, losses);
                        JsonElement custom = comps.remove("minecraft:custom_data");
                        if (comps.size() > 0) {
                            JsonObject sc = new JsonObject();
                            sc.addProperty("function", "minecraft:set_components");
                            sc.add("components", comps);
                            copyConditions(f, sc);
                            out.add(sc);
                        }
                        if (custom != null) {
                            JsonObject cd = new JsonObject();
                            cd.addProperty("function", "minecraft:set_custom_data");
                            cd.add("tag", custom);
                            copyConditions(f, cd);
                            out.add(cd);
                        }
                    }
                    return out;
                }
                case "minecraft:copy_nbt" -> {
                    JsonObject c = new JsonObject();
                    c.addProperty("function", "minecraft:copy_custom_data");
                    if (f.has("source")) {
                        c.add("source", f.get("source"));
                    }
                    JsonArray ops = new JsonArray();
                    if (f.has("ops")) {
                        for (JsonElement op : f.getAsJsonArray("ops")) {
                            JsonObject o = op.getAsJsonObject();
                            String target = o.has("target") ? o.get("target").getAsString() : "";
                            if (target.startsWith("BlockEntityTag") || target.startsWith("display")) {
                                losses.lost("copy_nbt into " + target + " (a component since 1.20.5, not custom data)");
                                continue;
                            }
                            ops.add(o);
                        }
                    }
                    c.add("ops", ops);
                    copyConditions(f, c);
                    return ops.isEmpty() ? java.util.List.of() : java.util.List.of(c);
                }
                case "minecraft:set_contents" -> {
                    if (f.has("type") && !f.has("component")) {
                        f.remove("type");
                        f.addProperty("component", "minecraft:container");
                    }
                    if (f.has("entries")) {
                        entries(f.getAsJsonArray("entries"), from, to, losses);
                    }
                }
                default -> { }
            }
        }
        if (crosses(from, to, "1.21")) {
            switch (fn) {
                case "minecraft:looting_enchant" -> {
                    f.addProperty("function", "minecraft:enchanted_count_increase");
                    f.addProperty("enchantment", "minecraft:looting");
                }
                case "minecraft:enchant_randomly" -> {
                    if (f.has("enchantments")) {
                        JsonElement l = f.remove("enchantments");
                        f.add("options", l.isJsonArray() && l.getAsJsonArray().size() == 1 ? l.getAsJsonArray().get(0) : l);
                    }
                }
                case "minecraft:enchant_with_levels" -> {
                    JsonElement treasure = f.remove("treasure");
                    if (treasure != null && treasure.getAsBoolean()) {
                        f.addProperty("options", "#minecraft:on_random_loot");
                    }
                }
                default -> { }
            }
        }
        return java.util.List.of(f);
    }

    private static void copyConditions(JsonObject from, JsonObject to) {
        if (from.has("conditions")) {
            to.add("conditions", from.get("conditions").deepCopy());
        }
    }

    // ------------------------------------------------------------------ conditions

    public static JsonArray conditions(JsonArray in, String from, String to, LegacyItems.Losses losses) {
        JsonArray out = new JsonArray();
        for (JsonElement e : in) {
            out.add(e.isJsonObject() ? condition(e.getAsJsonObject().deepCopy(), from, to, losses) : e);
        }
        return out;
    }

    public static JsonObject condition(JsonObject c, String from, String to, LegacyItems.Losses losses) {
        String cond = id(c, "condition");
        if (cond == null) {
            return c;
        }
        switch (cond) {
            case "minecraft:any_of", "minecraft:all_of", "minecraft:alternative" -> {
                if (c.has("terms")) {
                    c.add("terms", conditions(c.getAsJsonArray("terms"), from, to, losses));
                }
            }
            case "minecraft:inverted" -> {
                if (c.has("term") && c.get("term").isJsonObject()) {
                    c.add("term", condition(c.getAsJsonObject("term"), from, to, losses));
                }
            }
            case "minecraft:match_tool" -> {
                if (c.has("predicate") && c.get("predicate").isJsonObject()) {
                    c.add("predicate", itemPredicate(c.getAsJsonObject("predicate"), from, to, losses));
                }
            }
            case "minecraft:entity_properties" -> {
                if (c.has("predicate") && c.get("predicate").isJsonObject()) {
                    c.add("predicate", entityPredicate(c.getAsJsonObject("predicate"), from, to, losses));
                }
            }
            case "minecraft:damage_source_properties" -> {
                if (c.has("predicate") && c.get("predicate").isJsonObject()) {
                    c.add("predicate", damageSourcePredicate(c.getAsJsonObject("predicate"), from, to, losses));
                }
            }
            case "minecraft:location_check" -> {
                if (c.has("predicate") && c.get("predicate").isJsonObject()) {
                    c.add("predicate", locationPredicate(c.getAsJsonObject("predicate"), from, to, losses));
                }
            }
            case "minecraft:block_state_property" -> {
                if (c.has("properties") && c.get("properties").isJsonObject()) {
                    c.add("properties", statePredicate(c.getAsJsonObject("properties"), from, to));
                }
            }
            case "minecraft:random_chance_with_looting" -> {
                if (crosses(from, to, "1.21")) {
                    float chance = c.has("chance") ? c.get("chance").getAsFloat() : 0;
                    float mult = c.has("looting_multiplier") ? c.get("looting_multiplier").getAsFloat() : 0;
                    JsonObject r = new JsonObject();
                    r.addProperty("condition", "minecraft:random_chance_with_enchanted_bonus");
                    r.addProperty("enchantment", "minecraft:looting");
                    r.addProperty("unenchanted_chance", chance);
                    JsonObject lin = new JsonObject();
                    lin.addProperty("type", "minecraft:linear");
                    lin.addProperty("base", chance + mult);
                    lin.addProperty("per_level_above_first", mult);
                    r.add("enchanted_chance", lin);
                    return r;
                }
            }
            default -> { }
        }
        return c;
    }

    // ------------------------------------------------------------------ predicates

    /** an item predicate, through every boundary between the two releases */
    public static JsonObject itemPredicate(JsonObject p, String from, String to, LegacyItems.Losses losses) {
        JsonObject out = crosses(from, to, "1.20.5") ? LegacyItems.itemPredicate(p, to, losses) : p.deepCopy();
        if (crosses(from, to, "1.21") && out.has("predicates")) {
            JsonObject preds = out.getAsJsonObject("predicates");
            for (String k : new String[] {"minecraft:enchantments", "minecraft:stored_enchantments"}) {
                if (preds.has(k) && preds.get(k).isJsonArray()) {
                    for (JsonElement en : preds.getAsJsonArray(k)) {
                        JsonObject eo = en.getAsJsonObject();
                        if (eo.has("enchantment")) {
                            eo.add("enchantments", eo.remove("enchantment"));
                        }
                    }
                }
            }
        }
        return out;
    }

    private static final Set<String> EQUIPMENT = Set.of("head", "chest", "legs", "feet", "mainhand", "offhand", "body");

    public static JsonObject entityPredicate(JsonObject p, String from, String to, LegacyItems.Losses losses) {
        JsonObject out = p.deepCopy();
        if (out.has("equipment") && out.get("equipment").isJsonObject()) {
            JsonObject eq = out.getAsJsonObject("equipment");
            for (String slot : EQUIPMENT) {
                if (eq.has(slot) && eq.get(slot).isJsonObject()) {
                    eq.add(slot, itemPredicate(eq.getAsJsonObject(slot), from, to, losses));
                }
            }
        }
        for (String k : new String[] {"location", "stepping_on", "movement_affected_by"}) {
            if (out.has(k) && out.get(k).isJsonObject()) {
                out.add(k, locationPredicate(out.getAsJsonObject(k), from, to, losses));
            }
        }
        if (out.has("type_specific") && out.get("type_specific").isJsonObject()) {
            JsonObject ts = out.getAsJsonObject("type_specific");
            String t = id(ts, "type"); // a registry-keyed dispatch since 1.20.5: written namespaced
            if (t != null && crosses(from, to, "1.20.5")) {
                ts.addProperty("type", t);
            }
            // the player check's looking_at is itself an entity predicate
            if (ts.has("looking_at") && ts.get("looking_at").isJsonObject()) {
                ts.add("looking_at", entityPredicate(ts.getAsJsonObject("looking_at"), from, to, losses));
            }
        }
        for (String k : new String[] {"vehicle", "passenger", "targeted_entity"}) {
            if (out.has(k) && out.get(k).isJsonObject()) {
                out.add(k, entityPredicate(out.getAsJsonObject(k), from, to, losses));
            }
        }
        if (crosses(from, to, "1.21.5") && out.has("type_specific") && out.get("type_specific").isJsonObject()) {
            // the variant checks moved from type_specific into component predicates
            JsonObject ts = out.getAsJsonObject("type_specific");
            String t = id(ts, "type");
            if (("minecraft:cat".equals(t) || "minecraft:frog".equals(t)) && ts.has("variant")) {
                JsonObject comps = out.has("components") ? out.getAsJsonObject("components") : new JsonObject();
                comps.add(t + "/variant", ts.get("variant"));
                out.add("components", comps);
                out.remove("type_specific");
            }
        }
        if (crosses(from, to, "26.2")) {
            out = subPredicateMap(out);
        }
        return out;
    }

    /** a damage source predicate: its entities are entity predicates */
    public static JsonObject damageSourcePredicate(JsonObject p, String from, String to, LegacyItems.Losses losses) {
        JsonObject out = p.deepCopy();
        for (String k : new String[] {"direct_entity", "source_entity"}) {
            if (out.has(k) && out.get(k).isJsonObject()) {
                out.add(k, entityPredicate(out.getAsJsonObject(k), from, to, losses));
            }
        }
        return out;
    }

    /** the entity predicate fields 26.2 keys by sub-predicate id */
    private static final Set<String> ENTITY_FIELDS = Set.of(
        "type", "distance", "location", "stepping_on", "movement_affected_by", "flags", "equipment", "vehicle",
        "passenger", "targeted_entity", "effects", "nbt", "team", "movement", "periodic_tick", "slots",
        "components", "predicates");

    /**
     * 26.2: an entity predicate is a map of sub-predicates keyed by their ids --
     * "type" -> "minecraft:entity_type", every other field namespaced, and the type-specific
     * check keyed by its type ("minecraft:type_specific/player").
     */
    private static JsonObject subPredicateMap(JsonObject p) {
        JsonObject out = new JsonObject();
        for (var e : p.entrySet()) {
            String k = e.getKey();
            if (k.equals("type_specific") && e.getValue().isJsonObject()) {
                JsonObject ts = e.getValue().getAsJsonObject().deepCopy();
                String t = id(ts, "type");
                ts.remove("type");
                String type = t == null ? "unknown" : t.substring(t.indexOf(':') + 1);
                if (type.equals("slime")) {
                    type = "cube_mob"; // 26.2 generalized the slime check to every cube mob
                }
                out.add("minecraft:type_specific/" + type, ts);
            } else if (k.equals("type")) {
                out.add("minecraft:entity_type", e.getValue());
            } else if (ENTITY_FIELDS.contains(k)) {
                out.add("minecraft:" + k, e.getValue());
            } else {
                out.add(k, e.getValue());
            }
        }
        return out;
    }

    public static JsonObject locationPredicate(JsonObject p, String from, String to, LegacyItems.Losses losses) {
        JsonObject out = p.deepCopy();
        if (crosses(from, to, "1.20.5")) {
            if (out.has("biome")) {
                out.add("biomes", knownOnly(out.remove("biome"), "worldgen/biome", to, losses));
            }
            if (out.has("structure")) {
                out.add("structures", knownOnly(out.remove("structure"), "worldgen/structure", to, losses));
            }
        }
        if (out.has("block") && out.get("block").isJsonObject()) {
            JsonObject b = out.getAsJsonObject("block");
            if (crosses(from, to, "1.20.5")) {
                holderSet(b, "blocks", "tag", "blocks", losses);
            }
            if (b.has("state") && b.get("state").isJsonObject()) {
                b.add("state", statePredicate(b.getAsJsonObject("state"), from, to));
            }
        }
        if (out.has("fluid") && out.get("fluid").isJsonObject()) {
            JsonObject f = out.getAsJsonObject("fluid");
            if (crosses(from, to, "1.20.5")) {
                if (f.has("fluid")) {
                    f.add("fluids", f.remove("fluid"));
                }
                holderSet(f, "fluids", "tag", "fluids", losses);
            }
            if (f.has("state") && f.get("state").isJsonObject()) {
                f.add("state", statePredicate(f.getAsJsonObject("state"), from, to));
            }
        }
        return out;
    }

    /**
     * A biome or structure named by id: 1.20.1 kept it as a bare key, so a misspelt or
     * since-removed vanilla id simply never matched; 1.20.5 resolves the holder set and a
     * missing element fails the whole file. A vanilla id the target does not ship becomes
     * the empty set, which never matches -- the same answer.
     */
    private static JsonElement knownOnly(JsonElement v, String registry, String to, LegacyItems.Losses losses) {
        if (!v.isJsonPrimitive() || v.getAsString().startsWith("#")) {
            return v;
        }
        String id = v.getAsString().contains(":") ? v.getAsString() : "minecraft:" + v.getAsString();
        if (Boolean.FALSE.equals(VanillaIds.entryShipped(registry, id, to))) {
            losses.lost(registry + " " + id + " does not exist in " + to + " (never matched; now the empty set)");
            return new JsonArray();
        }
        return v;
    }

    /** list-of-ids + separate tag -> one holder set; a single-element list is written as the id */
    private static void holderSet(JsonObject o, String listKey, String tagKey, String outKey, LegacyItems.Losses losses) {
        JsonElement list = o.remove(listKey);
        JsonElement tag = o.remove(tagKey);
        if (list != null && tag != null) {
            losses.lost("predicate with both a " + listKey + " list and a tag (1.20.5 holds one set): the list is kept");
        }
        if (list != null) {
            o.add(outKey, list.isJsonArray() && list.getAsJsonArray().size() == 1 ? list.getAsJsonArray().get(0) : list);
        } else if (tag != null) {
            o.addProperty(outKey, "#" + tag.getAsString());
        }
    }

    /** a block state property predicate: 1.20.2 reads its values as strings or ranges only */
    public static JsonObject statePredicate(JsonObject props, String from, String to) {
        if (!crosses(from, to, "1.20.2")) {
            return props;
        }
        JsonObject out = new JsonObject();
        for (var e : props.entrySet()) {
            JsonElement v = e.getValue();
            if (v.isJsonPrimitive() && !v.getAsJsonPrimitive().isString()) {
                v = new JsonPrimitive(v.getAsString());
            } else if (v.isJsonObject()) {
                JsonObject r = new JsonObject();
                for (var re : v.getAsJsonObject().entrySet()) {
                    JsonElement x = re.getValue();
                    r.add(re.getKey(), x.isJsonPrimitive() && !x.getAsJsonPrimitive().isString()
                        ? new JsonPrimitive(x.getAsString()) : x);
                }
                v = r;
            }
            out.add(e.getKey(), v);
        }
        return out;
    }

    // ------------------------------------------------------------------ helpers

    static boolean crosses(String from, String to, String release) {
        return Release.before(from, release) && Release.reaches(to, release);
    }

    /** a namespaced id field ("set_count" -> "minecraft:set_count"), null when absent */
    static String id(JsonObject o, String key) {
        if (!o.has(key) || !o.get(key).isJsonPrimitive()) {
            return null;
        }
        String s = o.get(key).getAsString();
        return s.contains(":") ? s : "minecraft:" + s;
    }

    private LegacyLoot() {
    }
}
