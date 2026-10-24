package top.dext.centurybridge.step.v26_1.rt;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.component.DataComponentPatch;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;

/**
 * 1.21.11 ItemStack codecs 26.1 dropped (its recipes keep results as item stack templates), 1.21.11's
 * definitions on 26.1's parts:
 * <ul>
 * <li>SINGLE_ITEM_CODEC: {"id", "components" (empty when absent)}, the count always one;</li>
 * <li>STRICT_CODEC (field_51397): CODEC validated strictly (ItemStack.validateStrict, as 1.21.11);</li>
 * <li>STRICT_SINGLE_ITEM_CODEC (field_51398): SINGLE_ITEM_CODEC validated strictly.</li>
 * </ul>
 */
public final class CbWallItemCodecs {

    private CbWallItemCodecs() {
    }

    /** SINGLE_ITEM_CODEC */
    public static final Codec<ItemStack> SINGLE_ITEM_CODEC = Codec.lazyInitialized(() -> RecordCodecBuilder.create(i -> i.group(
        Item.CODEC.fieldOf("id").forGetter(ItemStack::typeHolder),
        DataComponentPatch.CODEC.optionalFieldOf("components", DataComponentPatch.EMPTY).forGetter(ItemStack::getComponentsPatch)
    ).apply(i, (item, patch) -> new ItemStack(item, 1, patch))));

    private static final Codec<ItemStack> STRICT = ItemStack.CODEC.validate(ItemStack::validateStrict);
    private static final Codec<ItemStack> STRICT_SINGLE_ITEM = SINGLE_ITEM_CODEC.validate(ItemStack::validateStrict);

    /** ItemStack.STRICT_CODEC */
    public static Codec<ItemStack> field_51397() {
        return STRICT;
    }

    /** ItemStack.STRICT_SINGLE_ITEM_CODEC */
    public static Codec<ItemStack> field_51398() {
        return STRICT_SINGLE_ITEM;
    }

    /** ItemStack.SINGLE_ITEM_CODEC */
    public static Codec<ItemStack> field_49747() {
        return SINGLE_ITEM_CODEC;
    }
}
