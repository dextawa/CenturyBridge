package top.dext.centurybridge.shims.v26_2;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.component.TooltipDisplay;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import top.dext.centurybridge.rt.v26_2.CbLegacyTooltipBlock;
import top.dext.centurybridge.rt.v26_2.Statics;

/**
 * 1.20.1 BlockItem.appendTooltip added its block's tooltip after its own (super) lines;
 * 26.2 BlockItem declares no tooltip and blocks have none. For a converted block that
 * overrides the old Block.appendTooltip, its lines are asked here, where every item's
 * tooltip passes through.
 */
@Mixin(Item.class)
public abstract class ItemTooltipBridge {

    @Inject(method = "appendHoverText", at = @At("TAIL"))
    private void cb$legacyBlockTooltip(ItemStack stack, Item.TooltipContext context, TooltipDisplay display,
            Consumer<Component> tooltip, TooltipFlag flag, CallbackInfo ci) {
        if ((Object) this instanceof BlockItem item && item.getBlock() instanceof CbLegacyTooltipBlock block) {
            List<Component> lines = new ArrayList<>();
            block.method_9568(stack, Statics.cbTooltipLevel(), lines, flag);
            lines.forEach(tooltip);
        }
    }
}
