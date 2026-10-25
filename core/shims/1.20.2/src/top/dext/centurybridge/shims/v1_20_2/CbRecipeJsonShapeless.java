package top.dext.centurybridge.shims.v1_20_2;

import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import net.minecraft.class_1799;
import net.minecraft.class_1856;
import net.minecraft.class_1860;
import net.minecraft.class_1867;
import net.minecraft.class_2371;
import net.minecraft.class_2960;
import net.minecraft.class_3518;
import net.minecraft.class_7710;
import org.spongepowered.asm.mixin.Mixin;
import top.dext.centurybridge.rt.v1_20_2.CbRecipeIds;
import top.dext.centurybridge.rt.v1_20_2.CbRecipeJson;
import top.dext.centurybridge.rt.v1_20_2.CbVanillaReads;

/**
 * ShapelessRecipe.Serializer's 1.20.1 read(id, json) and its private static itemsFromJson, 1.20.1's bodies
 * (itemStackFromJson and Ingredient.fromJson are their classes' restored statics, the recipe built with 1.20.2's
 * constructor, the id kept for getId()). 1.20.2's RecipeManager consults the reader (CbRecipeJsonVanillaMixin).
 */
@Mixin(value = class_1867.class_1868.class, priority = CbVanillaReads.PRIORITY)
public abstract class CbRecipeJsonShapeless implements CbRecipeJson.Reader {

    /** 1.20.1 ShapelessRecipe.Serializer.itemsFromJson(array): the non-empty ingredients */
    @CbVanillaReads("CbRecipeJsonVanillaMixin: the restored shapeless reader calls it; RecipeManager.fromJson consults that reader against its unmixed copy")
    private static class_2371<class_1856> method_8144(com.google.gson.JsonArray array) {
        class_2371<class_1856> list = class_2371.method_10211();
        for (int i = 0; i < array.size(); ++i) {
            class_1856 ingredient = CbRecipeJsonIngredientAccess.cb$fromJson(array.get(i), false);
            if (!ingredient.method_8103()) {
                list.add(ingredient);
            }
        }
        return list;
    }

    @CbVanillaReads("CbRecipeJsonVanillaMixin: RecipeManager.fromJson consults it, against its unmixed copy cb$goldenJson, for every shapeless recipe the codec decoded")
    public class_1867 method_8142(class_2960 id, JsonObject json) {
        String group = class_3518.method_15253(json, "group", "");
        class_7710 category = class_7710.field_40252.method_47920(class_3518.method_15253(json, "category", null), class_7710.field_40251);
        class_2371<class_1856> ingredients = method_8144(class_3518.method_15261(json, "ingredients"));
        if (ingredients.isEmpty()) {
            throw new JsonParseException("No ingredients for shapeless recipe");
        }
        if (ingredients.size() > 9) {
            throw new JsonParseException("Too many ingredients for shapeless recipe");
        }
        class_1799 result = CbRecipeJsonShapedAccess.cb$itemStackFromJson(class_3518.method_15296(json, "result"));
        return CbRecipeIds.remember(new class_1867(group, category, result, ingredients), id);
    }

    /** the same 1.20.1 body under a CenturyBridge name: what method_8142 answers unmixed (CbRecipeJson.consult) */
    @Override
    public net.minecraft.class_1860<?> cb$goldenJson(class_2960 id, JsonObject json) {
        String group = class_3518.method_15253(json, "group", "");
        class_7710 category = class_7710.field_40252.method_47920(class_3518.method_15253(json, "category", null), class_7710.field_40251);
        class_2371<class_1856> ingredients = CbRecipeJson.itemsFromJson(class_3518.method_15261(json, "ingredients"));
        if (ingredients.isEmpty()) {
            throw new JsonParseException("No ingredients for shapeless recipe");
        }
        if (ingredients.size() > 9) {
            throw new JsonParseException("Too many ingredients for shapeless recipe");
        }
        class_1799 result = CbRecipeJson.itemStackFromJson(class_3518.method_15296(json, "result"));
        return new class_1867(group, category, result, ingredients);
    }

    @Override
    public class_1860<?> cb$readJson(class_2960 id, JsonObject json) {
        return this.method_8142(id, json);
    }
}
