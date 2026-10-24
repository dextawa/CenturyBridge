package top.dext.centurybridge.step.v1_21_2.rt;

import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerLifecycleEvents;

/**
 * Registrations 1.21.1 had that 1.21.2 dropped (s7item's area): the recipe serializers of CbItemRecipeSerializers;
 * 1.21.1's minecraft:boat entity sub-predicate type (CbItemTriggers); and the running server, which 1.21.1's
 * ServerRecipeBook members consult (CbItemRecipeBook)
 */
public final class CbItemInit implements ModInitializer {
    @Override
    public void onInitialize() {
        CbItemRecipeSerializers.register();
        CbItemTriggers.registerBoatPredicate();
        ServerLifecycleEvents.SERVER_STARTING.register(CbItemRecipeBook::server);
        ServerLifecycleEvents.SERVER_STOPPED.register(CbItemRecipeBook::stopped);
    }
}
