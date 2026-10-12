package top.dext.centurybridge.step.v1_20_3.facade;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import java.util.List;
import top.dext.centurybridge.step.v1_20_3.rt.CbDataRecipes;

/**
 * Facade for 1.20.2 ShapelessRecipeBuilder.Result (class_2450$class_2451), gone in 1.20.3: the
 * FinishedRecipe a shapeless crafting recipe was saved as. The 1.20.2 class whole -- its JSON
 * (category, group unless empty, ingredients, result item and count above one), the shapeless
 * serializer, id and advancement. Members public, as a facade's must be.
 */
public class ShapelessRecipeBuilderResult extends CraftingRecipeBuilder.CraftingResult {

    /** id */
    public final net.minecraft.class_2960 field_11402;
    /** result */
    public final net.minecraft.class_1792 field_11403;
    /** count */
    public final int field_11400;
    /** group */
    public final String field_11399;
    /** ingredients */
    public final List<net.minecraft.class_1856> field_11404;
    /** advancement */
    public final net.minecraft.class_8779 field_11401;

    public ShapelessRecipeBuilderResult(net.minecraft.class_2960 id, net.minecraft.class_1792 result, int count, String group,
            net.minecraft.class_7710 category, List<net.minecraft.class_1856> ingredients, net.minecraft.class_8779 advancement) {
        super(category);
        this.field_11402 = id;
        this.field_11403 = result;
        this.field_11400 = count;
        this.field_11399 = group;
        this.field_11404 = ingredients;
        this.field_11401 = advancement;
    }

    /** serializeRecipeData(json) */
    @Override
    public void method_10416(JsonObject json) {
        super.method_10416(json);
        if (!field_11399.isEmpty()) {
            json.addProperty("group", field_11399);
        }
        JsonArray ingredients = new JsonArray();
        for (net.minecraft.class_1856 ingredient : field_11404) {
            ingredients.add(CbDataRecipes.ingredientJson(ingredient, false));
        }
        json.add("ingredients", ingredients);
        JsonObject result = new JsonObject();
        result.addProperty("item", net.minecraft.class_7923.field_41178.method_10221(field_11403).toString());
        if (field_11400 > 1) {
            result.addProperty("count", field_11400);
        }
        json.add("result", result);
    }

    /** type() */
    @Override
    public net.minecraft.class_1865<?> comp_1231() {
        return net.minecraft.class_1865.field_9031;
    }

    /** id() */
    @Override
    public net.minecraft.class_2960 comp_1230() {
        return field_11402;
    }

    /** advancement() */
    @Override
    public net.minecraft.class_8779 comp_1235() {
        return field_11401;
    }
}
