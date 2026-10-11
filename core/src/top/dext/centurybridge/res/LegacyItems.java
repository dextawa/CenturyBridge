package top.dext.centurybridge.res;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.google.gson.JsonPrimitive;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Item stacks, ingredients and item predicates as old data files wrote them, in the
 * shape a target release reads. Each rule names the release that changed the shape
 * and is driven by the shape it finds (an old-shaped value is rewritten, a value
 * already in the new shape passes through), so the same rule serves a static file
 * at conversion and a mod's old JSON handed to a bridge at runtime.
 *
 * Whatever a rule cannot carry over is reported through the {@link Losses} it is
 * given, never dropped silently.
 */
public final class LegacyItems {

    /** what a translation could not express; the caller decides how loud it is */
    public interface Losses {
        void lost(String what);

        Losses NONE = what -> { };
    }

    // ---------------------------------------------------------------- ingredients

    /**
     * An ingredient. 1.21.2: an ingredient is a holder set -- "id", "#tag" or a list
     * of ids -- where it used to be {"item": id} / {"tag": tag} or a list of those.
     * A list mixing items and tags has no vanilla spelling any more; Fabric API's
     * fabric:any custom ingredient (present wherever Fabric API is) expresses it.
     */
    public static JsonElement ingredient(JsonElement in, String target, Losses losses) {
        if (!Release.reaches(target, "1.21.2") || in == null) {
            return in;
        }
        if (in.isJsonPrimitive()) {
            return in;
        }
        if (in.isJsonObject()) {
            JsonObject o = in.getAsJsonObject();
            if (o.has("fabric:type")) {
                return fabricIngredient(o, target, losses);
            }
            if (o.has("item")) {
                return new JsonPrimitive(o.get("item").getAsString());
            }
            if (o.has("tag")) {
                return new JsonPrimitive("#" + o.get("tag").getAsString());
            }
            losses.lost("ingredient of unknown shape " + o);
            return in;
        }
        JsonArray arr = in.getAsJsonArray();
        List<JsonElement> parts = new ArrayList<>();
        boolean allItems = true;
        for (JsonElement e : arr) {
            JsonElement p = ingredient(e, target, losses);
            parts.add(p);
            if (!(p.isJsonPrimitive() && !p.getAsString().startsWith("#"))) {
                allItems = false;
            }
        }
        if (parts.size() == 1) {
            return parts.get(0);
        }
        if (allItems) {
            JsonArray out = new JsonArray();
            parts.forEach(out::add);
            return out;
        }
        JsonObject any = new JsonObject();
        any.addProperty("fabric:type", "fabric:any");
        JsonArray list = new JsonArray();
        parts.forEach(list::add);
        any.add("ingredients", list);
        return any;
    }

    /** Fabric API custom ingredients nest ingredients; their own keys are Fabric's to keep */
    private static JsonElement fabricIngredient(JsonObject o, String target, Losses losses) {
        JsonObject out = o.deepCopy();
        for (String k : List.of("base", "subtracted")) {
            if (out.has(k)) {
                out.add(k, ingredient(out.get(k), target, losses));
            }
        }
        if (out.has("ingredients") && out.get("ingredients").isJsonArray()) {
            JsonArray l = new JsonArray();
            for (JsonElement e : out.getAsJsonArray("ingredients")) {
                l.add(ingredient(e, target, losses));
            }
            out.add("ingredients", l);
        }
        return out;
    }

    // ---------------------------------------------------------------- stacks

    /**
     * A recipe result. 1.20.5: an item stack is {"id", "count", "components"}; it was
     * {"item", "count"} (vanilla recipe serializers never read an "nbt" key, so one
     * present was inert and stays dropped) or, for cooking and stonecutting, a bare
     * id with the count beside it. Crafting, smithing and stonecutting results are
     * counted stacks (the codec writes the count even when it is 1); a cooking
     * result is a single item, as it always was.
     */
    public static JsonElement result(JsonElement in, Integer siblingCount, boolean counted, String target,
            Losses losses) {
        if (!Release.reaches(target, "1.20.5") || in == null) {
            return in;
        }
        JsonObject out = new JsonObject();
        int count;
        String id;
        if (in.isJsonPrimitive()) {
            id = in.getAsString();
            count = siblingCount == null ? 1 : siblingCount;
        } else {
            JsonObject o = in.getAsJsonObject();
            if (o.has("id")) {
                return o; // already a 1.20.5 stack
            }
            id = o.get("item").getAsString();
            count = o.has("count") ? o.get("count").getAsInt() : 1;
        }
        if (counted) {
            out.addProperty("count", count);
        } else if (count != 1) {
            losses.lost("cooking result count " + count + " (a single-item result in 1.20.5)");
        }
        out.addProperty("id", id);
        return out;
    }

    /**
     * An item stack whose NBT the old reader did apply (advancement icons, loot
     * entries): {"item", "count"?, "nbt"?} -> {"id", "count"?, "components"?}.
     */
    public static JsonObject stack(JsonObject o, String target, Losses losses) {
        if (!Release.reaches(target, "1.20.5") || o.has("id")) {
            return o;
        }
        JsonObject out = new JsonObject();
        out.add("id", o.get("item"));
        if (o.has("count")) {
            out.add("count", o.get("count"));
        }
        if (o.has("nbt")) {
            JsonObject comps = components(nbtOf(o.get("nbt"), losses), losses);
            if (comps != null && comps.size() > 0) {
                out.add("components", comps);
            }
        }
        return out;
    }

    // ---------------------------------------------------------------- item NBT -> components

    /** the vanilla NBT keys 1.20.5 turned into components that the rules below do not carry */
    private static final Set<String> VANILLA_UNCARRIED = Set.of(
        "HideFlags", "AttributeModifiers", "Trim", "Fireworks", "Explosion", "BlockStateTag", "CanDestroy",
        "CanPlaceOn", "pages", "title", "author", "generation", "resolved", "filtered_pages", "filtered_title",
        "Items", "ChargedProjectiles", "Charged", "Decorations", "map", "map_scale_direction", "map_to_lock",
        "DebugProperty", "Recipes", "instrument", "effects", "LodestonePos", "LodestoneDimension",
        "LodestoneTracked", "CustomPotionEffects", "custom_potion_effects", "BucketVariantTag", "Patterns",
        "Base", "Bees", "Lock", "LootTable", "LootTableSeed", "Variant", "BlockEntityTag", "EntityTag");

    /**
     * Item NBT as 1.20.5 components (the subset of the game's componentization fix
     * data files use: damage, custom model data, name, lore, colour, enchantments,
     * repair cost, potion, unbreakable, skull owner). Keys the game never owned go
     * to minecraft:custom_data, as the fix put them; vanilla keys this does not
     * carry are reported and left out rather than misfiled there.
     */
    @SuppressWarnings("unchecked")
    public static JsonObject components(Map<String, Object> nbt, Losses losses) {
        if (nbt == null) {
            return null;
        }
        JsonObject c = new JsonObject();
        Map<String, Object> custom = new LinkedHashMap<>();
        for (var e : nbt.entrySet()) {
            String k = e.getKey();
            Object v = e.getValue();
            switch (k) {
                case "Damage" -> {
                    int d = num(v);
                    if (d != 0) {
                        c.addProperty("minecraft:damage", d);
                    }
                }
                case "CustomModelData" -> c.addProperty("minecraft:custom_model_data", num(v));
                case "RepairCost" -> {
                    int r = num(v);
                    if (r != 0) {
                        c.addProperty("minecraft:repair_cost", r);
                    }
                }
                case "Unbreakable" -> {
                    if (num(v) != 0) {
                        c.add("minecraft:unbreakable", new JsonObject());
                    }
                }
                case "Potion" -> {
                    JsonObject p = c.has("minecraft:potion_contents") ? c.getAsJsonObject("minecraft:potion_contents")
                        : new JsonObject();
                    p.addProperty("potion", String.valueOf(v));
                    c.add("minecraft:potion_contents", p);
                }
                case "CustomPotionColor" -> {
                    JsonObject p = c.has("minecraft:potion_contents") ? c.getAsJsonObject("minecraft:potion_contents")
                        : new JsonObject();
                    p.addProperty("custom_color", num(v));
                    c.add("minecraft:potion_contents", p);
                }
                case "Enchantments", "StoredEnchantments" -> {
                    JsonObject levels = new JsonObject();
                    for (Object en : (List<Object>) v) {
                        Map<String, Object> m = (Map<String, Object>) en;
                        levels.addProperty(String.valueOf(m.get("id")), num(m.get("lvl")));
                    }
                    JsonObject ench = new JsonObject();
                    ench.add("levels", levels);
                    c.add(k.equals("Enchantments") ? "minecraft:enchantments" : "minecraft:stored_enchantments", ench);
                }
                case "SkullOwner" -> {
                    JsonObject prof = new JsonObject();
                    if (v instanceof String name) {
                        prof.addProperty("name", name);
                        c.add("minecraft:profile", prof);
                    } else {
                        losses.lost("SkullOwner compound (game profile with properties)");
                    }
                }
                case "display" -> {
                    Map<String, Object> d = (Map<String, Object>) v;
                    for (var de : d.entrySet()) {
                        switch (de.getKey()) {
                            case "Name" -> c.add("minecraft:custom_name", text(String.valueOf(de.getValue())));
                            case "Lore" -> {
                                JsonArray lore = new JsonArray();
                                for (Object line : (List<Object>) de.getValue()) {
                                    lore.add(text(String.valueOf(line)));
                                }
                                c.add("minecraft:lore", lore);
                            }
                            case "color" -> {
                                JsonObject dyed = new JsonObject();
                                dyed.addProperty("rgb", num(de.getValue()));
                                c.add("minecraft:dyed_color", dyed);
                            }
                            case "MapColor" -> c.addProperty("minecraft:map_color", num(de.getValue()));
                            default -> losses.lost("display." + de.getKey());
                        }
                    }
                }
                default -> {
                    if (VANILLA_UNCARRIED.contains(k)) {
                        losses.lost("item NBT " + k + " (a component this translation does not carry)");
                    } else {
                        custom.put(k, v);
                    }
                }
            }
        }
        if (!custom.isEmpty()) {
            c.addProperty("minecraft:custom_data", Snbt.write(custom));
        }
        return c;
    }

    /** a text component stored as a JSON string in NBT */
    private static JsonElement text(String json) {
        try {
            return JsonParser.parseString(json);
        } catch (RuntimeException e) {
            return new JsonPrimitive(json);
        }
    }

    private static int num(Object v) {
        if (v instanceof Snbt.Num n) {
            return n.value().intValue();
        }
        return Integer.parseInt(String.valueOf(v));
    }

    /** "nbt" was an SNBT string; a few writers put the compound in as JSON */
    public static Map<String, Object> nbtOf(JsonElement e, Losses losses) {
        try {
            if (e.isJsonPrimitive()) {
                return Snbt.compound(e.getAsString());
            }
            return Snbt.compound(e.toString());
        } catch (RuntimeException ex) {
            losses.lost("unreadable item NBT " + e + ": " + ex.getMessage());
            return null;
        }
    }

    // ---------------------------------------------------------------- item predicates

    /**
     * An item predicate (advancement criteria, match_tool, equipment). 1.20.5: "items"
     * is a holder set ("id", "#tag" or a list) where it was a list of ids plus a
     * separate "tag"; nbt, enchantments, stored_enchantments, potion and durability
     * became sub-predicates under "predicates" (nbt matches the custom_data component,
     * so the vanilla keys inside it are matched where 1.20.5 keeps them).
     */
    @SuppressWarnings("unchecked")
    public static JsonObject itemPredicate(JsonObject o, String target, Losses losses) {
        if (!Release.reaches(target, "1.20.5")) {
            return o;
        }
        JsonObject out = new JsonObject();
        JsonObject preds = o.has("predicates") ? o.getAsJsonObject("predicates").deepCopy() : new JsonObject();
        JsonObject comps = o.has("components") ? o.getAsJsonObject("components").deepCopy() : new JsonObject();
        for (var e : o.entrySet()) {
            String k = e.getKey();
            JsonElement v = e.getValue();
            switch (k) {
                case "items" -> {
                    if (o.has("tag")) {
                        losses.lost("item predicate with both items and tag (1.20.5 holds one set)");
                    }
                    if (v.isJsonArray() && v.getAsJsonArray().size() == 1) {
                        out.add("items", v.getAsJsonArray().get(0));
                    } else {
                        out.add("items", v);
                    }
                }
                case "tag" -> {
                    if (!o.has("items")) {
                        out.addProperty("items", "#" + v.getAsString());
                    }
                }
                case "count" -> out.add("count", v);
                case "durability" -> {
                    JsonObject dmg = new JsonObject();
                    dmg.add("durability", v);
                    preds.add("minecraft:damage", dmg);
                }
                case "enchantments", "stored_enchantments" -> {
                    JsonArray l = new JsonArray();
                    for (JsonElement en : v.getAsJsonArray()) {
                        JsonObject eo = en.getAsJsonObject().deepCopy();
                        if (eo.has("enchantment")) {
                            eo.add("enchantments", eo.remove("enchantment"));
                        }
                        l.add(eo);
                    }
                    preds.add(k.equals("enchantments") ? "minecraft:enchantments" : "minecraft:stored_enchantments", l);
                }
                case "potion" -> preds.add("minecraft:potion_contents", v);
                case "nbt" -> {
                    Map<String, Object> nbt = nbtOf(v, losses);
                    if (nbt != null) {
                        JsonObject asComps = components(nbt, losses);
                        // an NBT predicate is a partial match: custom data keeps partial
                        // matching through the custom_data sub-predicate; the vanilla
                        // keys become exact component checks
                        if (asComps.has("minecraft:custom_data")) {
                            preds.add("minecraft:custom_data", asComps.remove("minecraft:custom_data"));
                        }
                        for (var ce : asComps.entrySet()) {
                            comps.add(ce.getKey(), ce.getValue());
                        }
                    }
                }
                case "predicates", "components" -> { }
                default -> out.add(k, v);
            }
        }
        if (comps.size() > 0) {
            out.add("components", comps);
        }
        if (preds.size() > 0) {
            out.add("predicates", preds);
        }
        return out;
    }

    private LegacyItems() {
    }
}
