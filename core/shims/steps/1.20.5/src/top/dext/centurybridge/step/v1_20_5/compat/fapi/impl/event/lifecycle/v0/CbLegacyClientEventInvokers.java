package top.dext.centurybridge.step.v1_20_5.compat.fapi.impl.event.lifecycle.v0;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import top.dext.centurybridge.step.v1_20_5.compat.fapi.event.client.ClientTickCallback;
import top.dext.centurybridge.step.v1_20_5.compat.fapi.event.client.ItemTooltipCallback;
import top.dext.centurybridge.step.v1_20_5.compat.fapi.event.world.WorldTickCallback;

/**
 * 1.20.4's fabric-events-lifecycle-v0 LegacyClientEventInvokers (that module's client entrypoint, run from
 * this layer's, compat/fapi/CbFapiClientInit): the ticks on lifecycle v1's client events, the tooltip on the
 * 1.20.4 item API's client ItemTooltipCallback, as there (this layer's copy, itself fired from 1.20.5's:
 * impl/client/item/CbFapiItemClient).
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

    /** the tooltip event (the 1.20.4 item API's, fired when fabric-item-api-v1 is loaded) */
    public static void initTooltips() {
        top.dext.centurybridge.step.v1_20_5.compat.fapi.client.item.v1.ItemTooltipCallback.EVENT.register(
            (stack, context, lines) -> ItemTooltipCallback.EVENT.invoker().getTooltip(stack, context, lines));
    }
}
