package top.dext.centurybridge.step.v1_20_5.compat.fapi.event.client;

import java.util.List;
import net.fabricmc.fabric.api.event.Event;
import net.fabricmc.fabric.api.event.EventFactory;

/**
 * Fabric API 0.97.3's (1.20.4) fabric-events-lifecycle-v0 ItemTooltipCallback, superseded there by the
 * item API's: invoked with each item tooltip as the item API's is (CbLegacyClientEventInvokers registers on
 * 1.20.5's, passing its tooltip type, which 1.20.4 called the tooltip context).
 */
@Deprecated
public interface ItemTooltipCallback {
    @Deprecated
    Event<ItemTooltipCallback> EVENT = EventFactory.createArrayBacked(ItemTooltipCallback.class,
        listeners -> (stack, tooltipContext, lines) -> {
            for (ItemTooltipCallback callback : listeners) {
                callback.getTooltip(stack, tooltipContext, lines);
            }
        });

    void getTooltip(net.minecraft.class_1799 stack, net.minecraft.class_1836 tooltipContext, List<net.minecraft.class_2561> lines);
}
