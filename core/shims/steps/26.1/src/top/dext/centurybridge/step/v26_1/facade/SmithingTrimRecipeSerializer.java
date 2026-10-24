package top.dext.centurybridge.step.v26_1.facade;

import com.mojang.serialization.MapCodec;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.world.item.crafting.SmithingTrimRecipe;

/**
 * 1.21.11's SmithingTrimRecipe.Serializer, which 26.1 replaced by the record SmithingTrimRecipe.SERIALIZER of the recipe's own codecs:
 * the same codecs (26.1's, of 26.1's recipe objects), answered as 1.21.11's serializer did. A mod's instance
 * is registered and answered as a 26.1 record like any 1.21.11 serializer (rt/CbWallRecipes); the constant
 * RecipeSerializer.* is 26.1's own. Members keep their intermediary names.
 */
public class SmithingTrimRecipeSerializer implements RecipeSerializer<SmithingTrimRecipe> {

    /** CODEC */
    public static final MapCodec<SmithingTrimRecipe> field_46109 = SmithingTrimRecipe.MAP_CODEC;

    /** STREAM_CODEC */
    public static final StreamCodec<RegistryFriendlyByteBuf, SmithingTrimRecipe> field_48365 = SmithingTrimRecipe.STREAM_CODEC;

    public SmithingTrimRecipeSerializer() {
    }

    /** codec() */
    @Override
    public MapCodec<SmithingTrimRecipe> method_53736() {
        return SmithingTrimRecipe.MAP_CODEC;
    }

    /** streamCodec() */
    @Override
    public StreamCodec<RegistryFriendlyByteBuf, SmithingTrimRecipe> method_56104() {
        return SmithingTrimRecipe.STREAM_CODEC;
    }
}
