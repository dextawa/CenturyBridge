package top.dext.centurybridge.rt.v26_2;

/**
 * The 1.20.1 Block.appendTooltip(stack, world, tooltip, context) shape. BlockItem's
 * tooltip called it; 26.2 blocks have no tooltip method, so the converter adds this to
 * every block class that overrides it and ItemTooltipBridge asks it for block items.
 */
public interface CbLegacyTooltipBlock {

    /** appendTooltip(stack, world, tooltip, context) */
    void method_9568(net.minecraft.world.item.ItemStack stack, net.minecraft.world.level.BlockGetter world,
            java.util.List<net.minecraft.network.chat.Component> tooltip, net.minecraft.world.item.TooltipFlag context);
}
