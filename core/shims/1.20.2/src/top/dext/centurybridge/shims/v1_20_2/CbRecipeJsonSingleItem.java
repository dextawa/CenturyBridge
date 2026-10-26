package top.dext.centurybridge.shims.v1_20_2;

import com.google.gson.JsonObject;
import net.minecraft.class_1799;
import net.minecraft.class_1856;
import net.minecraft.class_1860;
import net.minecraft.class_2960;
import net.minecraft.class_3518;
import net.minecraft.class_3972;
import net.minecraft.class_7923;
import org.spongepowered.asm.mixin.Mixin;
import top.dext.centurybridge.rt.v1_20_2.CbRecipeSerializerSupport;
import top.dext.centurybridge.rt.v1_20_2.CbRecipeJson;
import top.dext.centurybridge.rt.v1_20_2.CbVanillaReads;

/**
 * SingleItemRecipe.Serializer's 1.20.1 read(id, json), 1.20.1's body (Ingredient.fromJson answered by CbRecipeJson;
 * the recipe made by the serializer's factory, which only 1.20.2's codec can call -- the factory interface is
 * package-private -- so the values read go to it through that codec, CbRecipeJson.singleItem; the id kept for
 * getId()). 1.20.2's
 * RecipeManager reads stonecutting recipes through it (CbRecipeJsonVanillaMixin).
 */
@Mixin(value = class_3972.class_3973.class, priority = CbVanillaReads.PRIORITY)
public abstract class CbRecipeJsonSingleItem implements CbRecipeJson.Reader {


    @CbVanillaReads("CbRecipeJsonVanillaMixin: RecipeManager.fromJson consults it, against its unmixed copy cb$goldenJson, for every stonecutting recipe the codec decoded")
    public class_3972 method_17881(class_2960 id, JsonObject json) {
        String group = class_3518.method_15253(json, "group", "");
        class_1856 ingredient;
        if (class_3518.method_15264(json, "ingredient")) {
            ingredient = CbRecipeJsonIngredientAccess.cb$fromJson(class_3518.method_15261(json, "ingredient"), false);
        } else {
            ingredient = CbRecipeJsonIngredientAccess.cb$fromJson(class_3518.method_15296(json, "ingredient"), false);
        }
        String resultId = class_3518.method_15265(json, "result");
        int count = class_3518.method_15260(json, "count");
        class_1799 result = new class_1799(class_7923.field_41178.method_10223(new class_2960(resultId)), count);
        // factory.create(group, ingredient, result), run with the id as the one being decoded (a converted 1.20.1
        // factory takes it from there)
        return CbRecipeSerializerSupport.withId(id, () -> CbRecipeJson.singleItem(this.cb$codec(), group, ingredient, result));
    }

    /** the same 1.20.1 body under a CenturyBridge name: what method_17881 answers unmixed (CbRecipeJson.consult) */
    @Override
    public net.minecraft.class_1860<?> cb$goldenJson(class_2960 id, JsonObject json) {
        String group = class_3518.method_15253(json, "group", "");
        class_1856 ingredient;
        if (class_3518.method_15264(json, "ingredient")) {
            ingredient = CbRecipeJson.goldenIngredient(class_3518.method_15261(json, "ingredient"), false);
        } else {
            ingredient = CbRecipeJson.goldenIngredient(class_3518.method_15296(json, "ingredient"), false);
        }
        String resultId = class_3518.method_15265(json, "result");
        int count = class_3518.method_15260(json, "count");
        class_1799 result = new class_1799(class_7923.field_41178.method_10223(new class_2960(resultId)), count);
        // factory.create(group, ingredient, result), run with the id as the one being decoded (a converted 1.20.1
        // factory takes it from there)
        return CbRecipeSerializerSupport.withId(id, () -> CbRecipeJson.singleItem(this.cb$codec(), group, ingredient, result));
    }

    @Override
    public class_1860<?> cb$readJson(class_2960 id, JsonObject json) {
        return this.method_17881(id, json);
    }

    /**
     * the serializer's codec, the one place 1.20.2 calls its (package-private) factory: asked through
     * RecipeSerializer.codec(), a call later steps carry like any mod's (1.20.5's MapCodec answer is converted back by
     * that step's wiring), not a @Shadow of 1.20.2's private codec field, which a later release drops (1.20.5) and
     * Mixin then strips the whole mixin with
     */
    @SuppressWarnings("unchecked")
    @org.spongepowered.asm.mixin.Unique
    private com.mojang.serialization.Codec<class_3972> cb$codec() {
        return ((net.minecraft.class_1865<class_3972>) (Object) this).method_53736();
    }
}
