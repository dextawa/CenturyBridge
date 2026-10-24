package top.dext.centurybridge.step.v26_1.facade;

import com.mojang.serialization.MapCodec;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.world.item.crafting.TransmuteRecipe;

/**
 * 1.21.11's TransmuteRecipe.Serializer, which 26.1 replaced by the record TransmuteRecipe.SERIALIZER of the recipe's own codecs:
 * the same codecs (26.1's, of 26.1's recipe objects), answered as 1.21.11's serializer did. A mod's instance
 * is registered and answered as a 26.1 record like any 1.21.11 serializer (rt/CbWallRecipes); the constant
 * RecipeSerializer.* is 26.1's own. Members keep their intermediary names.
 */
public class TransmuteRecipeSerializer implements RecipeSerializer<TransmuteRecipe> {

    /** STREAM_CODEC */
    public static final StreamCodec<RegistryFriendlyByteBuf, TransmuteRecipe> field_54325 = TransmuteRecipe.STREAM_CODEC;

    public TransmuteRecipeSerializer() {
    }

    /** codec() */
    @Override
    public MapCodec<TransmuteRecipe> method_53736() {
        return TransmuteRecipe.MAP_CODEC;
    }

    /** streamCodec() */
    @Override
    public StreamCodec<RegistryFriendlyByteBuf, TransmuteRecipe> method_56104() {
        return TransmuteRecipe.STREAM_CODEC;
    }
}
