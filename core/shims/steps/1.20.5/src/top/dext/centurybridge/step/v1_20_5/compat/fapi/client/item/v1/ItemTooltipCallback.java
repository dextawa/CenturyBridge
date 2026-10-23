package top.dext.centurybridge.step.v1_20_5.compat.fapi.client.item.v1;

import java.util.List;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.event.Event;
import net.fabricmc.fabric.api.event.EventFactory;

/**
 * Fabric API 0.97.3's (1.20.4) client ItemTooltipCallback: getTooltip(stack, context, lines). 1.20.5's takes
 * Item.TooltipContext too (and calls the 1.20.4 context the tooltip type -- the same class_1836), so a 1.20.4
 * listener cannot be registered on 1.20.5's event; a 1.20.4 mod's every reference to the interface names this
 * one (classRenames), whose EVENT is fired from 1.20.5's (impl/client/item/CbFapiItemClient, client entrypoint).
 * The invoker as in 1.20.4 (Fabric API, Apache-2.0).
 */
@Environment(EnvType.CLIENT)
public interface ItemTooltipCallback {

    Event<ItemTooltipCallback> EVENT = EventFactory.createArrayBacked(ItemTooltipCallback.class, callbacks -> (stack, context, lines) -> {
        for (ItemTooltipCallback callback : callbacks) {
            callback.getTooltip(stack, context, lines);
        }
    });

    void getTooltip(net.minecraft.class_1799 stack, net.minecraft.class_1836 context, List<net.minecraft.class_2561> lines);
}
