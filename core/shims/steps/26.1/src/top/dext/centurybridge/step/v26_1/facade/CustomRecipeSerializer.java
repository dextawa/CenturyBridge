package top.dext.centurybridge.step.v26_1.facade;

import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.world.item.crafting.CraftingBookCategory;
import net.minecraft.world.item.crafting.CraftingRecipe;

/**
 * 1.21.11's CustomRecipe.Serializer: the serializer of a special recipe whose only datum is its crafting
 * book category -- {"category": ...} (MISC when absent), the category over the network -- each recipe made
 * by the factory from the category. 26.1 dropped it (its special recipes carry their own data); a mod's
 * serializer is registered and answered as the 26.1 record of these codecs like any 1.21.11 serializer
 * (rt/CbWallRecipes). 1.21.11's bodies; members keep their intermediary names.
 */
public class CustomRecipeSerializer<T extends CraftingRecipe> implements RecipeSerializer<T> {

    /** 1.21.11's CustomRecipe.Serializer.Factory */
    public interface Factory<T extends CraftingRecipe> {
        T create(CraftingBookCategory category);
    }

    /** public: 1.21.11 code reads it as a field (the 1.20.5 layer's CbFieldReshapesGen; its own access widener opened the vanilla field) */
    public final MapCodec<T> codec;
    private final StreamCodec<RegistryFriendlyByteBuf, T> streamCodec;

    public CustomRecipeSerializer(Factory<T> factory) {
        this.codec = RecordCodecBuilder.mapCodec(i -> i.group(
            CraftingBookCategory.CODEC.fieldOf("category").orElse(CraftingBookCategory.MISC).forGetter(CraftingRecipe::category)
        ).apply(i, factory::create));
        StreamCodec<io.netty.buffer.ByteBuf, T> byCategory = CraftingBookCategory.STREAM_CODEC.map(factory::create, CraftingRecipe::category);
        this.streamCodec = StreamCodec.of(byCategory::encode, byCategory::decode);
    }

    /** codec() */
    @Override
    public MapCodec<T> method_53736() {
        return this.codec;
    }

    /** streamCodec() */
    @Override
    public StreamCodec<RegistryFriendlyByteBuf, T> method_56104() {
        return this.streamCodec;
    }
}
