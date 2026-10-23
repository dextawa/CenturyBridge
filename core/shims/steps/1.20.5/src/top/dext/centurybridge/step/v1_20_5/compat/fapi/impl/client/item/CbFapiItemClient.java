package top.dext.centurybridge.step.v1_20_5.compat.fapi.impl.client.item;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import top.dext.centurybridge.step.v1_20_5.compat.fapi.client.item.v1.ItemTooltipCallback;

/**
 * The 1.20.4 item API's client side on 1.20.5's: 1.20.4's ItemTooltipCallback (this layer's copy) fires from
 * 1.20.5's, with the tooltip type as 1.20.4's tooltip context (the same class) and without the Item.TooltipContext
 * 1.20.4 never had. Registered once, from the client entrypoint, when fabric-item-api-v1 is loaded.
 */
@Environment(EnvType.CLIENT)
public final class CbFapiItemClient {

    private CbFapiItemClient() {
    }

    public static void init() {
        net.fabricmc.fabric.api.client.item.v1.ItemTooltipCallback.EVENT.register(
            (stack, tooltipContext, type, lines) -> ItemTooltipCallback.EVENT.invoker().getTooltip(stack, type, lines));
    }
}
