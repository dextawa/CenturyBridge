package top.dext.centurybridge.step.v26_1.facade;

import com.mojang.serialization.MapCodec;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.world.item.crafting.ShapelessRecipe;

/**
 * 1.21.11's ShapelessRecipe.Serializer, which 26.1 replaced by the record ShapelessRecipe.SERIALIZER of the recipe's own codecs:
 * the same codecs (26.1's, of 26.1's recipe objects), answered as 1.21.11's serializer did. A mod's instance
 * is registered and answered as a 26.1 record like any 1.21.11 serializer (rt/CbWallRecipes); the constant
 * RecipeSerializer.* is 26.1's own. Members keep their intermediary names.
 */
public class ShapelessRecipeSerializer implements RecipeSerializer<ShapelessRecipe> {

    /** CODEC (private in 1.21.11; the 1.20.5 layer's access widener opened it and its CbFieldReshapesGen reads it) */
    public static final MapCodec<ShapelessRecipe> field_46104 = ShapelessRecipe.MAP_CODEC;

    /** STREAM_CODEC */
    public static final StreamCodec<RegistryFriendlyByteBuf, ShapelessRecipe> field_48360 = ShapelessRecipe.STREAM_CODEC;

    public ShapelessRecipeSerializer() {
    }

    /** codec() */
    @Override
    public MapCodec<ShapelessRecipe> method_53736() {
        return ShapelessRecipe.MAP_CODEC;
    }

    /** streamCodec() */
    @Override
    public StreamCodec<RegistryFriendlyByteBuf, ShapelessRecipe> method_56104() {
        return ShapelessRecipe.STREAM_CODEC;
    }
}
