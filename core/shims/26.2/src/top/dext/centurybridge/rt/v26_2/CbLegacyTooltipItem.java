package top.dext.centurybridge.rt.v26_2;

/**
 * The 1.20.1 Item.appendTooltip(stack, world, tooltip, context) shape, added to every
 * converted item class that overrides it: the appendHoverText the converter synthesizes
 * reaches the mod's own override through it (see Statics.cbAppendHoverText).
 */
public interface CbLegacyTooltipItem {

    /** appendTooltip(stack, world, tooltip, context) */
    void method_7851(net.minecraft.world.item.ItemStack stack, net.minecraft.world.level.Level world,
            java.util.List<net.minecraft.network.chat.Component> tooltip, net.minecraft.world.item.TooltipFlag context);
}
