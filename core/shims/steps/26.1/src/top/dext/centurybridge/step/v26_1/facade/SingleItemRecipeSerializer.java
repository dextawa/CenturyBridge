package top.dext.centurybridge.step.v26_1.facade;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.item.crafting.SingleItemRecipe;
import top.dext.centurybridge.step.v26_1.rt.CbWallItemCodecs;
import top.dext.centurybridge.step.v26_1.rt.CbWallRecipeCtors;

/**
 * 1.21.11's SingleItemRecipe.Serializer: a single-item recipe's codecs over the recipe's factory -- {"group" (""
 * when absent), "ingredient", "result" (a strict item stack)}, the same three over the network -- 1.21.11's
 * bodies (its constructor was protected, for subclasses; public here, as access wideners never reach this class). 26.1 dropped it; registered and answered as any
 * 1.21.11 serializer (rt/CbWallRecipes). The values are read back from 26.1's recipe objects.
 */
public class SingleItemRecipeSerializer<T extends SingleItemRecipe> implements RecipeSerializer<T> {

    /** 1.21.11's SingleItemRecipe.Factory */
    public interface Factory<T extends SingleItemRecipe> {
        T create(String group, Ingredient input, ItemStack result);
    }

    private final MapCodec<T> codec;
    private final StreamCodec<RegistryFriendlyByteBuf, T> streamCodec;

    public SingleItemRecipeSerializer(Factory<T> factory) {
        this.codec = RecordCodecBuilder.mapCodec(i -> i.group(
            Codec.STRING.optionalFieldOf("group", "").forGetter(Recipe::group),
            Ingredient.CODEC.fieldOf("ingredient").forGetter(SingleItemRecipe::input),
            CbWallItemCodecs.field_51397().fieldOf("result").forGetter(CbWallRecipeCtors::result)
        ).apply(i, factory::create));
        this.streamCodec = StreamCodec.composite(
            ByteBufCodecs.STRING_UTF8, Recipe::group,
            Ingredient.CONTENTS_STREAM_CODEC, SingleItemRecipe::input,
            ItemStack.STREAM_CODEC, CbWallRecipeCtors::result,
            factory::create);
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
