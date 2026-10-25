package top.dext.centurybridge.step.v26_1.rt;

import net.minecraft.core.component.DataComponents;
import net.minecraft.network.chat.CommonComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.ItemStackTemplate;

/**
 * 1.21.11 Item members 26.1 reshaped (both final in 1.21.11, so only calls reach them):
 * <ul>
 * <li>getCraftingRemainder() (class_1792.method_7858) answered a new stack of the remainder item, the empty stack for
 *     none; 26.1 answers the remainder's template (null for none): a stack made from it;</li>
 * <li>getName() (class_1792.method_63680) answered the item's ITEM_NAME component, empty when it has none; 26.1 kept
 *     only getName(stack): 1.21.11's body over the item's components.</li>
 * </ul>
 */
public final class CbWallItemMembers {

    private CbWallItemMembers() {
    }

    /** 1.21.11 Item.getCraftingRemainder() */
    public static ItemStack getCraftingRemainder(Item item) {
        ItemStackTemplate remainder = item.getCraftingRemainder();
        return remainder == null ? ItemStack.EMPTY : remainder.create();
    }

    /** 1.21.11 Item.getName() */
    public static Component getName(Item item) {
        return item.components().getOrDefault(DataComponents.ITEM_NAME, CommonComponents.EMPTY);
    }
}
