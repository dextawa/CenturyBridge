package top.dext.centurybridge.step.v1_21_5.rt;

import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerLifecycleEvents;

/**
 * Registrations and state 1.21.4 had that 1.21.5 dropped: 1.21.4's entity variant sub-predicate types
 * (rt/CbEntityVariants); the running server, whose registries answer 1.21.4's static cat / frog variant registries
 * (rt/CbVariants); 1.21.4's game tests on 1.21.5's registry and runner (rt/CbLegacyGameTests)
 */
public final class CbStepInit implements ModInitializer {
    @Override
    public void onInitialize() {
        CbEntityVariants.register();
        CbLegacyGameTests.init();
        ServerLifecycleEvents.SERVER_STARTING.register(CbVariants::serverStarting);
        ServerLifecycleEvents.SERVER_STOPPED.register(CbVariants::serverStopped);
    }
}
