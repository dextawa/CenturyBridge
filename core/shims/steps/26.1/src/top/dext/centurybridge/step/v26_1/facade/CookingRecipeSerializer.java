package top.dext.centurybridge.step.v26_1.facade;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.AbstractCookingRecipe;
import net.minecraft.world.item.crafting.CookingBookCategory;
import net.minecraft.world.item.crafting.Ingredient;
import top.dext.centurybridge.step.v26_1.rt.CbWallItemCodecs;
import top.dext.centurybridge.step.v26_1.rt.CbWallRecipeCtors;

/**
 * 1.21.11's AbstractCookingRecipe.Serializer: a cooking recipe's codecs over the recipe's factory -- {"group" (""
 * when absent), "category" (MISC), "ingredient", "result" (a single strict item), "experience" (0), "cookingtime"
 * (the serializer's default)}, the same six over the network -- 1.21.11's bodies. 26.1 dropped it (its cooking
 * recipes carry their own codecs); a mod's serializer is registered and answered as the 26.1 record of these
 * codecs like any 1.21.11 serializer (rt/CbWallRecipes). The values are read back from 26.1's recipe objects.
 */
public class CookingRecipeSerializer<T extends AbstractCookingRecipe> implements RecipeSerializer<T> {

    /** 1.21.11's AbstractCookingRecipe.Factory */
    public interface Factory<T extends AbstractCookingRecipe> {
        T create(String group, CookingBookCategory category, Ingredient input, ItemStack result, float experience, int cookingTime);
    }

    /** public: 1.21.11 code reads it as a field (the 1.20.5 layer's CbFieldReshapesGen; its own access widener opened the vanilla field) */
    public final MapCodec<T> codec;
    private final StreamCodec<RegistryFriendlyByteBuf, T> streamCodec;

    public CookingRecipeSerializer(Factory<T> factory, int defaultCookingTime) {
        this.codec = RecordCodecBuilder.mapCodec(i -> i.group(
            Codec.STRING.optionalFieldOf("group", "").forGetter(AbstractCookingRecipe::group),
            CookingBookCategory.CODEC.fieldOf("category").orElse(CookingBookCategory.MISC).forGetter(AbstractCookingRecipe::category),
            Ingredient.CODEC.fieldOf("ingredient").forGetter(AbstractCookingRecipe::input),
            CbWallItemCodecs.field_51398().fieldOf("result").forGetter(CbWallRecipeCtors::result),
            Codec.FLOAT.fieldOf("experience").orElse(0.0F).forGetter(AbstractCookingRecipe::experience),
            Codec.INT.fieldOf("cookingtime").orElse(defaultCookingTime).forGetter(AbstractCookingRecipe::cookingTime)
        ).apply(i, factory::create));
        this.streamCodec = StreamCodec.composite(
            ByteBufCodecs.STRING_UTF8, AbstractCookingRecipe::group,
            CookingBookCategory.STREAM_CODEC, AbstractCookingRecipe::category,
            Ingredient.CONTENTS_STREAM_CODEC, AbstractCookingRecipe::input,
            ItemStack.STREAM_CODEC, CbWallRecipeCtors::result,
            ByteBufCodecs.FLOAT, AbstractCookingRecipe::experience,
            ByteBufCodecs.INT, AbstractCookingRecipe::cookingTime,
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
