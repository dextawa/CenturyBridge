package top.dext.centurybridge.res;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import java.util.Set;

/**
 * A recipe file of a vanilla type, upgraded to the shape the target's codec reads.
 * Recipe types a mod registered are parsed by that mod's own serializer -- still
 * the old code, reading the old format -- so only vanilla types are rewritten.
 */
public final class LegacyRecipes {

    private static final Set<String> COOKING = Set.of(
        "minecraft:smelting", "minecraft:blasting", "minecraft:smoking", "minecraft:campfire_cooking");

    /** whether the target's vanilla codec, not a mod's serializer, reads this recipe */
    public static boolean vanillaType(JsonObject recipe) {
        if (!recipe.has("type") || !recipe.get("type").isJsonPrimitive()) {
            return false;
        }
        String t = recipe.get("type").getAsString();
        if (!t.contains(":")) {
            t = "minecraft:" + t;
        }
        return t.startsWith("minecraft:");
    }

    /** the recipe in the target's format; the input is not modified */
    public static JsonObject upgrade(JsonObject in, String target, LegacyItems.Losses losses) {
        JsonObject r = in.deepCopy();
        String type = r.get("type").getAsString();
        if (!type.contains(":")) {
            type = "minecraft:" + type;
        }
        switch (type) {
            case "minecraft:crafting_shaped" -> {
                if (r.has("key")) {
                    JsonObject key = new JsonObject();
                    for (var e : r.getAsJsonObject("key").entrySet()) {
                        key.add(e.getKey(), LegacyItems.ingredient(e.getValue(), target, losses));
                    }
                    r.add("key", key);
                }
                r.add("result", LegacyItems.result(r.get("result"), null, true, target, losses));
            }
            case "minecraft:crafting_shapeless" -> {
                if (r.has("ingredients")) {
                    JsonArray l = new JsonArray();
                    for (JsonElement e : r.getAsJsonArray("ingredients")) {
                        l.add(LegacyItems.ingredient(e, target, losses));
                    }
                    r.add("ingredients", l);
                }
                r.add("result", LegacyItems.result(r.get("result"), null, true, target, losses));
            }
            case "minecraft:stonecutting" -> {
                r.add("ingredient", LegacyItems.ingredient(r.get("ingredient"), target, losses));
                Integer count = r.has("count") ? r.get("count").getAsInt() : null;
                if (Release.reaches(target, "1.20.5") && r.get("result").isJsonPrimitive()) {
                    r.remove("count");
                    r.add("result", LegacyItems.result(r.get("result"), count == null ? 1 : count, true, target, losses));
                }
            }
            case "minecraft:smithing_transform", "minecraft:smithing_trim" -> {
                for (String k : new String[] {"template", "base", "addition"}) {
                    if (r.has(k)) {
                        r.add(k, LegacyItems.ingredient(r.get(k), target, losses));
                    }
                }
                if (r.has("result")) {
                    r.add("result", LegacyItems.result(r.get("result"), null, true, target, losses));
                }
            }
            default -> {
                if (COOKING.contains(type)) {
                    r.add("ingredient", LegacyItems.ingredient(r.get("ingredient"), target, losses));
                    r.add("result", LegacyItems.result(r.get("result"), null, false, target, losses));
                }
                // crafting_special_* and crafting_decorated_pot carry no items
            }
        }
        return r;
    }

    private LegacyRecipes() {
    }
}
