package top.dext.centurybridge.shims.v1_20_2;

import com.google.gson.JsonObject;
import net.minecraft.class_1856;
import net.minecraft.class_1860;
import net.minecraft.class_2960;
import net.minecraft.class_3518;
import net.minecraft.class_8062;
import org.spongepowered.asm.mixin.Mixin;
import top.dext.centurybridge.rt.v1_20_2.CbRecipeIds;
import top.dext.centurybridge.rt.v1_20_2.CbRecipeJson;
import top.dext.centurybridge.rt.v1_20_2.CbVanillaReads;

/**
 * SmithingTrimRecipe.Serializer's 1.20.1 read(id, json), 1.20.1's body (Ingredient.fromJson(element) answered by
 * CbRecipeJson; the recipe built with 1.20.2's package-private constructor through SmithingTrimRecipeCtor, the id
 * kept for getId()). 1.20.2's RecipeManager reads smithing trim recipes through it (CbRecipeJsonVanillaMixin).
 */
@Mixin(value = class_8062.class_8063.class, priority = CbVanillaReads.PRIORITY)
public abstract class CbRecipeJsonSmithingTrim implements CbRecipeJson.Reader {

    @CbVanillaReads("CbRecipeJsonVanillaMixin: RecipeManager.fromJson consults it, against its unmixed copy cb$goldenJson, for every smithing trim recipe the codec decoded")
    public class_8062 method_48459(class_2960 id, JsonObject json) {
        class_1856 template = CbRecipeJson.ingredient(class_3518.method_52226(json, "template"), true);
        class_1856 base = CbRecipeJson.ingredient(class_3518.method_52226(json, "base"), true);
        class_1856 addition = CbRecipeJson.ingredient(class_3518.method_52226(json, "addition"), true);
        return CbRecipeIds.remember(SmithingTrimRecipeCtor.cb$new(template, base, addition), id);
    }

    /** the same 1.20.1 body under a CenturyBridge name: what method_48459 answers unmixed (CbRecipeJson.consult) */
    @Override
    public net.minecraft.class_1860<?> cb$goldenJson(class_2960 id, JsonObject json) {
        class_1856 template = CbRecipeJson.ingredient(class_3518.method_52226(json, "template"), true);
        class_1856 base = CbRecipeJson.ingredient(class_3518.method_52226(json, "base"), true);
        class_1856 addition = CbRecipeJson.ingredient(class_3518.method_52226(json, "addition"), true);
        return SmithingTrimRecipeCtor.cb$new(template, base, addition);
    }

    @Override
    public class_1860<?> cb$readJson(class_2960 id, JsonObject json) {
        return this.method_48459(id, json);
    }
}
