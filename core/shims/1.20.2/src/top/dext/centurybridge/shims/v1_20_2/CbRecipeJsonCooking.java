package top.dext.centurybridge.shims.v1_20_2;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import net.minecraft.class_1799;
import net.minecraft.class_1856;
import net.minecraft.class_1860;
import net.minecraft.class_1874;
import net.minecraft.class_2960;
import net.minecraft.class_3518;
import net.minecraft.class_3957;
import net.minecraft.class_7709;
import net.minecraft.class_7923;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import top.dext.centurybridge.rt.v1_20_2.CbRecipeSerializerSupport;
import top.dext.centurybridge.rt.v1_20_2.CbRecipeJson;
import top.dext.centurybridge.rt.v1_20_2.CbVanillaReads;

/**
 * SimpleCookingSerializer's 1.20.1 read(id, json), 1.20.1's body, and its defaultCookingTime field (1.20.2 keeps the
 * constructor argument in its codec only; CbRecipeJsonCookingHooks sets the field from it). Ingredient.fromJson is
 * answered by CbRecipeJson; the recipe is made by the serializer's factory, which only 1.20.2's codec can call (the
 * factory interface is package-private), so the values read go to it through that codec (CbRecipeJson.cooking; the
 * id kept for getId()). 1.20.2's RecipeManager reads smelting, blasting, smoking and campfire recipes through it
 * (CbRecipeJsonVanillaMixin).
 */
@Mixin(value = class_3957.class, priority = CbVanillaReads.PRIORITY)
public abstract class CbRecipeJsonCooking implements CbRecipeJson.Reader, CbRecipeJson.CookingTime {

    /** 1.20.2's codec: the one place 1.20.2 calls the serializer's (package-private) factory */
    @Shadow
    @Final
    private com.mojang.serialization.Codec<class_1874> field_46105;

    /** 1.20.1 defaultCookingTime */
    private int field_17551;

    /** the constructor's defaultCookingTime: what field_17551 holds unless a mod's accessor wrote it */
    @org.spongepowered.asm.mixin.Unique
    private int cb$constructedCookingTime;

    @CbVanillaReads("CbRecipeJsonVanillaMixin: RecipeManager.fromJson consults it, against its unmixed copy cb$goldenJson, for every cooking recipe the codec decoded")
    public class_1874 method_17736(class_2960 id, JsonObject json) {
        String group = class_3518.method_15253(json, "group", "");
        class_7709 category = class_7709.field_40245.method_47920(class_3518.method_15253(json, "category", null), class_7709.field_40244);
        JsonElement element = class_3518.method_15264(json, "ingredient")
            ? class_3518.method_15261(json, "ingredient") : class_3518.method_15296(json, "ingredient");
        class_1856 ingredient = CbRecipeJson.ingredient(element, false);
        String resultId = class_3518.method_15265(json, "result");
        class_2960 resultKey = new class_2960(resultId);
        class_1799 result = new class_1799(class_7923.field_41178.method_17966(resultKey)
            .orElseThrow(() -> new IllegalStateException("Item: " + resultId + " does not exist")));
        float experience = class_3518.method_15277(json, "experience", 0.0F);
        int cookingTime = class_3518.method_15282(json, "cookingtime", this.field_17551);
        // factory.create(group, category, ingredient, result, experience, cookingTime), run with the id as the one
        // being decoded (a converted 1.20.1 factory takes it from there)
        return CbRecipeSerializerSupport.withId(id, () -> CbRecipeJson.cooking(this.field_46105, group, category, ingredient, result, experience, cookingTime));
    }

    /** the same 1.20.1 body under a CenturyBridge name: what method_17736 answers unmixed (CbRecipeJson.consult) */
    @Override
    public net.minecraft.class_1860<?> cb$goldenJson(class_2960 id, JsonObject json) {
        String group = class_3518.method_15253(json, "group", "");
        class_7709 category = class_7709.field_40245.method_47920(class_3518.method_15253(json, "category", null), class_7709.field_40244);
        JsonElement element = class_3518.method_15264(json, "ingredient")
            ? class_3518.method_15261(json, "ingredient") : class_3518.method_15296(json, "ingredient");
        class_1856 ingredient = CbRecipeJson.ingredient(element, false);
        String resultId = class_3518.method_15265(json, "result");
        class_2960 resultKey = new class_2960(resultId);
        class_1799 result = new class_1799(class_7923.field_41178.method_17966(resultKey)
            .orElseThrow(() -> new IllegalStateException("Item: " + resultId + " does not exist")));
        float experience = class_3518.method_15277(json, "experience", 0.0F);
        int cookingTime = class_3518.method_15282(json, "cookingtime", this.cb$constructedCookingTime);
        // factory.create(group, category, ingredient, result, experience, cookingTime), run with the id as the one
        // being decoded (a converted 1.20.1 factory takes it from there)
        return CbRecipeSerializerSupport.withId(id, () -> CbRecipeJson.cooking(this.field_46105, group, category, ingredient, result, experience, cookingTime));
    }

    @Override
    public class_1860<?> cb$readJson(class_2960 id, JsonObject json) {
        return this.method_17736(id, json);
    }

    @Override
    public void cb$setDefaultCookingTime(int defaultCookingTime) {
        this.field_17551 = defaultCookingTime;
        this.cb$constructedCookingTime = defaultCookingTime;
    }
}
