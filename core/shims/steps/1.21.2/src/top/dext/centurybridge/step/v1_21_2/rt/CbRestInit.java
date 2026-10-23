package top.dext.centurybridge.step.v1_21_2.rt;

import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerLifecycleEvents;

/** s7rest's main entrypoint: the payload types its 1.21.1 APIs send, and the running server they consult */
public final class CbRestInit implements ModInitializer {
    @Override
    public void onInitialize() {
        CbRestRecipeSync.init();
        CbRestRecipeNet.init();
        top.dext.centurybridge.step.v1_21_2.facade.fapi.FabricElytraItem.register();
        ServerLifecycleEvents.SERVER_STARTING.register(server -> CbRestRecipesHolder.server = server);
        ServerLifecycleEvents.SERVER_STOPPED.register(server -> {
            if (CbRestRecipesHolder.server == server) {
                CbRestRecipesHolder.server = null;
            }
        });
    }
}
