package top.dext.centurybridge.step.v1_20_5.compat.fapi;

import net.fabricmc.api.ModInitializer;
import net.fabricmc.loader.api.FabricLoader;
import top.dext.centurybridge.step.v1_20_5.compat.fapi.impl.event.lifecycle.v0.CbLegacyEventInvokers;
import top.dext.centurybridge.step.v1_20_5.compat.fapi.impl.loot.CbLootEvents;
import top.dext.centurybridge.step.v1_20_5.compat.fapi.impl.resource.conditions.CbLegacyConditions;

/**
 * The main entrypoint of the Fabric API 1.20.4 modules this layer gives back on 1.20.5: what their own
 * entrypoints did at start -- fabric-events-lifecycle-v0's legacy invokers, fabric-resource-conditions-api-v1's
 * 1.20.4 default condition ids that 1.20.5 dropped, and the loot API's per-table sources for its 1.20.4 events.
 * Each runs only when the 1.20.5 module it is built on is there (a server without Fabric API loads this layer
 * too).
 */
public final class CbFapiInit implements ModInitializer {

    @Override
    public void onInitialize() {
        FabricLoader loader = FabricLoader.getInstance();
        if (loader.isModLoaded("fabric-lifecycle-events-v1")) {
            CbLegacyEventInvokers.init();
        }
        if (loader.isModLoaded("fabric-resource-conditions-api-v1")) {
            CbLegacyConditions.registerLegacyDefaults();
        }
        if (loader.isModLoaded("fabric-loot-api-v2")) {
            CbLootEvents.init();
        }
    }
}
