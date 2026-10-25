package top.dext.centurybridge.step.v26_1.rt;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import java.util.stream.Stream;
import net.minecraft.core.component.DataComponentPatch;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;

/**
 * 1.21.11 ItemStack statics and members 26.1 dropped, given back with 1.21.11's bodies over 26.1's ItemStack:
 * <ul>
 * <li>SINGLE_ITEM_CODEC (class_1799.field_49747): a stack of one, {"id", "components"}, the components as the
 *     stack's patch, decoded as a count-1 stack;</li>
 * <li>SIMPLE_ITEM_CODEC (class_1799.field_49267): the item alone, decoded as a count-1 stack without a patch;</li>
 * <li>getTags() (class_1799.method_40133): the tags of the stack's item holder.</li>
 * </ul>
 * A mixin cannot add a non-private static to ItemStack, so the codecs are read here (fieldRedirects) and getTags is
 * called here with the stack (staticRedirects).
 */
public final class CbWallItemStacks {

    /** 1.21.11 ItemStack.SINGLE_ITEM_CODEC */
    public static final Codec<ItemStack> SINGLE_ITEM_CODEC = Codec.lazyInitialized(() -> RecordCodecBuilder.create(i -> i.group(
        Item.CODEC.fieldOf("id").forGetter(ItemStack::typeHolder),
        DataComponentPatch.CODEC.optionalFieldOf("components", DataComponentPatch.EMPTY).forGetter(ItemStack::getComponentsPatch)
    ).apply(i, (item, patch) -> new ItemStack(item, 1, patch))));

    /** 1.21.11 ItemStack.SIMPLE_ITEM_CODEC */
    public static final Codec<ItemStack> SIMPLE_ITEM_CODEC = Item.CODEC.xmap(ItemStack::new, ItemStack::typeHolder);

    private CbWallItemStacks() {
    }

    /** 1.21.11 ItemStack.getTags() */
    public static Stream<TagKey<Item>> getTags(ItemStack stack) {
        return stack.typeHolder().tags();
    }
}
