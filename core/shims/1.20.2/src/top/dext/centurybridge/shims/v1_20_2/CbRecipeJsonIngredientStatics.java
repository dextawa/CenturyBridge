package top.dext.centurybridge.shims.v1_20_2;

import com.google.gson.JsonElement;
import com.google.gson.JsonSyntaxException;
import net.minecraft.class_1856;
import org.spongepowered.asm.mixin.Mixin;
import top.dext.centurybridge.rt.v1_20_2.CbRecipeJson;
import top.dext.centurybridge.rt.v1_20_2.CbVanillaReads;

/**
 * Ingredient's 1.20.1 fromJson(json, canBeEmpty) / fromJson(json), gone from 1.20.2, restored on Ingredient as private
 * statics (Mixin merges a class mixin's statics only when private; mods' plain calls keep their staticRedirects).
 * 1.20.1's own checks stand (null; an object or an array; an empty array only where allowed); the entries are then
 * decoded by 1.20.2's ingredient codec (CbRecipeJson.ingredient): 1.20.1's valueFromJson / fromValues work on
 * Ingredient.Value, a type package-private on 1.20.2 that no layer source can name, and the codec is also what reads
 * Fabric API's custom ingredients. The route therefore carries the answer and the checks around it, not anchors inside
 * 1.20.1's per-entry code (carries).
 */
@Mixin(value = class_1856.class, priority = CbVanillaReads.PRIORITY)
public abstract class CbRecipeJsonIngredientStatics {

    /** 1.20.1 Ingredient.fromJson(json, canBeEmpty) */
    @CbVanillaReads(value = "CbRecipeJsonVanillaMixin: the restored recipe readers and ShapedRecipe.keyFromJson call it; RecipeManager.fromJson "
        + "consults those readers against their unmixed copies; 1.20.1's per-entry valueFromJson / fromValues stand as 1.20.2's codec",
        carries = {"Inject@HEAD", "Inject@RETURN", "Inject@TAIL", "ModifyReturnValue@RETURN"})
    private static class_1856 method_8102(JsonElement json, boolean canBeEmpty) {
        if (json == null || json.isJsonNull()) {
            throw new JsonSyntaxException("Item cannot be null");
        }
        if (json.isJsonArray() && json.getAsJsonArray().size() == 0 && !canBeEmpty) {
            throw new JsonSyntaxException("Item array cannot be empty, at least one item must be defined");
        }
        if (!json.isJsonObject() && !json.isJsonArray()) {
            throw new JsonSyntaxException("Expected item to be object or array of objects");
        }
        return CbRecipeJson.ingredient(json, canBeEmpty);
    }

    /** 1.20.1 Ingredient.fromJson(json): fromJson(json, true) */
    @CbVanillaReads("CbRecipeJsonVanillaMixin: the restored smithing readers call it; RecipeManager.fromJson consults them against their unmixed copies")
    private static class_1856 method_52177(JsonElement json) {
        return method_8102(json, true);
    }
}
