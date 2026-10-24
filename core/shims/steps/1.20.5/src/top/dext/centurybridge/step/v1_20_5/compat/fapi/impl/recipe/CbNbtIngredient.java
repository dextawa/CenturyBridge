package top.dext.centurybridge.step.v1_20_5.compat.fapi.impl.recipe;

import com.mojang.datafixers.util.Either;
import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import net.fabricmc.fabric.api.recipe.v1.ingredient.CustomIngredient;
import net.fabricmc.fabric.api.recipe.v1.ingredient.CustomIngredientSerializer;
import net.minecraft.class_1799;
import net.minecraft.class_1856;
import net.minecraft.class_1935;
import net.minecraft.class_2487;
import net.minecraft.class_2512;
import net.minecraft.class_2522;
import net.minecraft.class_2960;
import net.minecraft.class_9129;
import net.minecraft.class_9139;
import top.dext.centurybridge.step.v1_20_5.rt.CbItemTags;

/**
 * Fabric API 1.20.4's NBT ingredient (DefaultCustomIngredients.nbt; fabric-recipe-api-v1's NbtIngredient, id
 * fabric:nbt), which 1.20.5's Fabric API replaced by component ingredients. 1.20.4's bodies on 1.20.5: a stack
 * matches when the base ingredient matches it and its 1.20.4 tag (the stack's 1.20.4 NBT view, CbItemTags)
 * equals the ingredient's NBT (strict) or contains it (NbtUtils.compareNbt partial); the matching stacks are the
 * base's with the NBT set. The JSON form is 1.20.4's (base, nbt as SNBT string or compound, strict), so a
 * 1.20.4 data pack's fabric:nbt ingredients load too; the network form is 1.20.4's order (ingredient, NBT,
 * strict) with 1.20.5's ingredient encoding.
 */
public final class CbNbtIngredient implements CustomIngredient {

    public static final Serializer SERIALIZER = new Serializer();

    private final class_1856 base;
    private final class_2487 nbt;
    private final boolean strict;

    public CbNbtIngredient(class_1856 base, class_2487 nbt, boolean strict) {
        if (nbt == null && !strict) {
            throw new IllegalArgumentException("NbtIngredient can only have null NBT in strict mode");
        }
        this.base = base;
        this.nbt = nbt;
        this.strict = strict;
    }

    /** DefaultCustomIngredients.nbt(base, nbt, strict) */
    public static class_1856 nbt(class_1856 base, class_2487 nbt, boolean strict) {
        Objects.requireNonNull(base, "Base ingredient cannot be null");
        return new CbNbtIngredient(base, nbt, strict).toVanilla();
    }

    /** DefaultCustomIngredients.nbt(stack, strict): the stack's item with the stack's 1.20.4 tag */
    public static class_1856 nbt(class_1799 stack, boolean strict) {
        Objects.requireNonNull(stack, "Stack cannot be null");
        return nbt(class_1856.method_8091(new class_1935[] {stack.method_7909()}), CbItemTags.getTag(stack), strict);
    }

    @Override
    public boolean test(class_1799 stack) {
        if (!this.base.method_8093(stack)) {
            return false;
        }
        class_2487 tag = CbItemTags.getTag(stack);
        return this.strict ? Objects.equals(this.nbt, tag) : class_2512.method_10687(this.nbt, tag, true);
    }

    @Override
    public List<class_1799> getMatchingStacks() {
        List<class_1799> stacks = new ArrayList<>(List.of(this.base.method_8105()));
        stacks.replaceAll(stack -> {
            class_1799 copy = stack.method_7972();
            if (this.nbt != null) {
                CbItemTags.setTag(copy, this.nbt.method_10553());
            }
            return copy;
        });
        stacks.removeIf(stack -> !this.base.method_8093(stack));
        return stacks;
    }

    @Override
    public boolean requiresTesting() {
        return true;
    }

    @Override
    public CustomIngredientSerializer<?> getSerializer() {
        return SERIALIZER;
    }

    public static final class Serializer implements CustomIngredientSerializer<CbNbtIngredient> {

        private static final class_2960 ID = new class_2960("fabric", "nbt");
        /** 1.20.4's: an SNBT string or a compound, written as the string */
        private static final Codec<class_2487> NBT_CODEC = Codec.either(Codec.STRING, class_2487.field_25128).flatXmap(
            either -> either.map(s -> {
                try {
                    return DataResult.success(class_2522.method_10718(s));
                } catch (com.mojang.brigadier.exceptions.CommandSyntaxException e) {
                    return DataResult.error(e::getMessage);
                }
            }, DataResult::success),
            tag -> DataResult.success(Either.left(tag.toString())));
        private static final MapCodec<CbNbtIngredient> ALLOW_EMPTY = codec(class_1856.field_46095);
        private static final MapCodec<CbNbtIngredient> DISALLOW_EMPTY = codec(class_1856.field_46096);
        private static final class_9139<class_9129, CbNbtIngredient> PACKET_CODEC = class_9139.method_56437(
            (buf, ingredient) -> {
                class_1856.field_48355.encode(buf, ingredient.base);
                buf.method_10794(ingredient.nbt);
                buf.method_52964(ingredient.strict);
            },
            buf -> new CbNbtIngredient(class_1856.field_48355.decode(buf), buf.method_10798(), buf.readBoolean()));

        private static MapCodec<CbNbtIngredient> codec(Codec<class_1856> ingredient) {
            return RecordCodecBuilder.mapCodec(i -> i.group(
                ingredient.fieldOf("base").forGetter(n -> n.base),
                NBT_CODEC.optionalFieldOf("nbt").forGetter(n -> Optional.ofNullable(n.nbt)),
                Codec.BOOL.optionalFieldOf("strict", false).forGetter(n -> n.strict)
            ).apply(i, (base, nbt, strict) -> new CbNbtIngredient(base, nbt.orElse(null), strict)));
        }

        @Override
        public class_2960 getIdentifier() {
            return ID;
        }

        @Override
        public MapCodec<CbNbtIngredient> getCodec(boolean allowEmpty) {
            return allowEmpty ? ALLOW_EMPTY : DISALLOW_EMPTY;
        }

        @Override
        public class_9139<class_9129, CbNbtIngredient> getPacketCodec() {
            return PACKET_CODEC;
        }
    }
}
