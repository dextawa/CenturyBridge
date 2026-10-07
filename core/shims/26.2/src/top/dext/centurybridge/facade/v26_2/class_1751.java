package top.dext.centurybridge.facade.v26_2;

import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;

/** Facade for 1.20.1 net/minecraft/class_1751 (simple Item subclass), deleted on
 *  26.2. Extends real Item; legacy Item hooks given inert defaults. */
public class class_1751 extends Item {
    public class_1751(Item.Properties props) { super(props); }

    /** use (method_7836) -> pass */
    public net.minecraft.world.InteractionResult method_7836(net.minecraft.world.level.Level level,
            net.minecraft.world.entity.player.Player player, net.minecraft.world.InteractionHand hand) {
        return net.minecraft.world.InteractionResult.PASS;
    }
    /** getRarity (method_7862) -> COMMON */
    public net.minecraft.world.item.Rarity method_7862(ItemStack stack) {
        return net.minecraft.world.item.Rarity.COMMON;
    }
}
