package top.dext.centurybridge.step.v1_20_5.compat.fapi;

import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.loader.api.FabricLoader;
import top.dext.centurybridge.step.v1_20_5.compat.fapi.impl.client.item.CbFapiItemClient;
import top.dext.centurybridge.step.v1_20_5.compat.fapi.impl.container.CbScreenProviderRegistryImpl;
import top.dext.centurybridge.step.v1_20_5.compat.fapi.impl.event.lifecycle.v0.CbLegacyClientEventInvokers;

/**
 * The client entrypoint of the Fabric API 1.20.4 modules this layer gives back on 1.20.5: what their own
 * client entrypoints did at start (fabric-events-lifecycle-v0's legacy client invokers, fabric-containers-v0's
 * container screen receiver), and the 1.20.4 item API's tooltip event fired from 1.20.5's. Each runs only when
 * the 1.20.5 module it is built on is there.
 */
@Environment(EnvType.CLIENT)
public final class CbFapiClientInit implements ClientModInitializer {

    @Override
    public void onInitializeClient() {
        FabricLoader loader = FabricLoader.getInstance();
        if (loader.isModLoaded("fabric-lifecycle-events-v1")) {
            CbLegacyClientEventInvokers.initTicks();
        }
        if (loader.isModLoaded("fabric-item-api-v1")) {
            CbFapiItemClient.init();
            CbLegacyClientEventInvokers.initTooltips();
        }
        if (loader.isModLoaded("fabric-networking-api-v1")) {
            CbScreenProviderRegistryImpl.init();
        }
    }
}
