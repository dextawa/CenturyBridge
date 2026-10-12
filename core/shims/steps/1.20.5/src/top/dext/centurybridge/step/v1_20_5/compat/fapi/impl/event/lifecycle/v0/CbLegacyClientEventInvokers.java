package top.dext.centurybridge.step.v1_20_5.compat.fapi.impl.event.lifecycle.v0;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import top.dext.centurybridge.step.v1_20_5.compat.fapi.event.client.ClientTickCallback;
import top.dext.centurybridge.step.v1_20_5.compat.fapi.event.client.ItemTooltipCallback;
import top.dext.centurybridge.step.v1_20_5.compat.fapi.event.world.WorldTickCallback;

/**
 * 1.20.4's fabric-events-lifecycle-v0 LegacyClientEventInvokers (that module's client entrypoint, run from
 * this layer's, compat/fapi/CbFapiClientInit): the ticks on lifecycle v1's client events, the tooltip on
 * 1.20.5's item API ItemTooltipCallback, whose Item.TooltipContext argument the 1.20.4 event never had;
 * 1.20.5's tooltip type is the 1.20.4 tooltip context (the same class_1836).
 */
@Environment(EnvType.CLIENT)
public final class CbLegacyClientEventInvokers {

    private CbLegacyClientEventInvokers() {
    }

    /** the tick events (fabric-lifecycle-events-v1) */
    public static void initTicks() {
        ClientTickEvents.END_CLIENT_TICK.register(client -> ClientTickCallback.EVENT.invoker().tick(client));
        ClientTickEvents.END_WORLD_TICK.register(world -> WorldTickCallback.EVENT.invoker().tick(world));
    }

    /** the tooltip event (fabric-item-api-v1) */
    public static void initTooltips() {
        net.fabricmc.fabric.api.client.item.v1.ItemTooltipCallback.EVENT.register(
            (stack, context, type, lines) -> ItemTooltipCallback.EVENT.invoker().getTooltip(stack, type, lines));
    }
}
