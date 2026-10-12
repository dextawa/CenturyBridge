package top.dext.centurybridge.step.v1_20_3.facade;

import com.google.gson.JsonObject;

/**
 * Facade for 1.20.2 FinishedRecipe (class_2444), gone in 1.20.3: data generation hands recipe
 * objects to the RecipeOutput there, where 1.20.2 handed it this -- an id, the serializer, the
 * unlocking advancement and the recipe's JSON. The 1.20.2 interface whole; serializeRecipe is its
 * 1.20.2 default ("type" from the serializer's registry id, then serializeRecipeData).
 * CbDataRecipes carries it into a 1.20.3 RecipeOutput and back.
 */
public interface FinishedRecipe {

    /** serializeRecipeData(json) */
    void method_10416(JsonObject json);

    /** serializeRecipe() */
    default JsonObject method_17799() {
        JsonObject json = new JsonObject();
        json.addProperty("type", net.minecraft.class_7923.field_41189.method_10221(comp_1231()).toString());
        method_10416(json);
        return json;
    }

    /** id() */
    net.minecraft.class_2960 comp_1230();

    /** type() */
    net.minecraft.class_1865<?> comp_1231();

    /** advancement() */
    net.minecraft.class_8779 comp_1235();
}
