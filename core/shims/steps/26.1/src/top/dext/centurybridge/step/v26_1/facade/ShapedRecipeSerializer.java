package top.dext.centurybridge.step.v26_1.facade;

import com.mojang.serialization.MapCodec;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.world.item.crafting.ShapedRecipe;

/**
 * 1.21.11's ShapedRecipe.Serializer, which 26.1 replaced by the record ShapedRecipe.SERIALIZER of the recipe's own codecs:
 * the same codecs (26.1's, of 26.1's recipe objects), answered as 1.21.11's serializer did. A mod's instance
 * is registered and answered as a 26.1 record like any 1.21.11 serializer (rt/CbWallRecipes); the constant
 * RecipeSerializer.* is 26.1's own. Members keep their intermediary names.
 */
public class ShapedRecipeSerializer implements RecipeSerializer<ShapedRecipe> {

    /** CODEC */
    public static final MapCodec<ShapedRecipe> field_46102 = ShapedRecipe.MAP_CODEC;

    /** STREAM_CODEC */
    public static final StreamCodec<RegistryFriendlyByteBuf, ShapedRecipe> field_48358 = ShapedRecipe.STREAM_CODEC;

    public ShapedRecipeSerializer() {
    }

    /** codec() */
    @Override
    public MapCodec<ShapedRecipe> method_53736() {
        return ShapedRecipe.MAP_CODEC;
    }

    /** streamCodec() */
    @Override
    public StreamCodec<RegistryFriendlyByteBuf, ShapedRecipe> method_56104() {
        return ShapedRecipe.STREAM_CODEC;
    }
}
