package top.dext.centurybridge.step.v1_20_3.rt;

import com.google.gson.JsonElement;
import com.mojang.serialization.Codec;
import com.mojang.serialization.JsonOps;
import java.util.Optional;
import net.minecraft.class_156;
import net.minecraft.class_1740;
import net.minecraft.class_1856;
import net.minecraft.class_2371;
import net.minecraft.class_3542;
import net.minecraft.class_7710;
import net.minecraft.class_8957;

/**
 * Items: the 1.20.2 item API on 1.20.3. Wired from s2world.json.
 */
public final class CbWorldItems {

    /**
     * MobEffectUtil.formatDuration and PotionUtils.addPotionTooltip gained a tick-rate argument in 1.20.3
     * (durations print as ticks / rate). 1.20.2 counted 20 ticks to the second, always: a 1.20.2 caller's
     * tooltip reads as it did.
     */
    public static float legacyTickRate() {
        return 20.0F;
    }

    // ------------------------------------------------------------------ enum codecs

    /**
     * CraftingBookCategory.CODEC / ArmorMaterials.CODEC are typed Codec since 1.20.3 (StringRepresentable.EnumCodec
     * in 1.20.2); the value is the same EnumCodec, built by StringRepresentable.fromEnum in both.
     */
    @SuppressWarnings("unchecked")
    public static class_3542.class_7292<class_7710> craftingBookCategoryCodec() {
        return (class_3542.class_7292<class_7710>) class_7710.field_40252;
    }

    @SuppressWarnings("unchecked")
    public static class_3542.class_7292<class_1740> armorMaterialsCodec() {
        return (class_3542.class_7292<class_1740>) class_1740.field_41941;
    }

    // ------------------------------------------------------------------ ShapedRecipe

    /**
     * ShapedRecipe(group, category, width, height, ingredients, result[, notify]) takes a ShapedRecipePattern
     * since 1.20.3: the same width, height and ingredients. The pattern's key/row data, which only its codec
     * reads, is empty -- a recipe built in code had none in 1.20.2 either.
     */
    public static class_8957 shapedPattern(int width, int height, class_2371<class_1856> ingredients) {
        return new class_8957(width, height, ingredients, Optional.empty());
    }

    // ------------------------------------------------------------------ Ingredient.toJson

    /** 1.20.2's Ingredient.toJson(allowEmpty), line for line (1.20.3 removed it; the codecs are the same) */
    public static JsonElement ingredientToJson(class_1856 ingredient, boolean allowEmpty) {
        Codec<class_1856> codec = allowEmpty ? class_1856.field_46095 : class_1856.field_46096;
        return class_156.method_47526(codec.encodeStart(JsonOps.INSTANCE, ingredient), IllegalStateException::new);
    }

    private CbWorldItems() {
    }
}
