package top.dext.centurybridge.step.v1_20_5.compat.fapi.impl.event.lifecycle.v0;

import net.fabricmc.fabric.api.event.lifecycle.v1.ServerLifecycleEvents;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerTickEvents;
import top.dext.centurybridge.step.v1_20_5.compat.fapi.event.server.ServerStartCallback;
import top.dext.centurybridge.step.v1_20_5.compat.fapi.event.server.ServerStopCallback;
import top.dext.centurybridge.step.v1_20_5.compat.fapi.event.server.ServerTickCallback;
import top.dext.centurybridge.step.v1_20_5.compat.fapi.event.world.WorldTickCallback;

/**
 * 1.20.4's fabric-events-lifecycle-v0 LegacyEventInvokers (that module's main entrypoint, run here from this
 * layer's, compat/fapi/CbFapiInit): the deprecated events are invoked by lifecycle v1's, as there.
 */
public final class CbLegacyEventInvokers {

    private CbLegacyEventInvokers() {
    }

    public static void init() {
        ServerLifecycleEvents.SERVER_STARTED.register(server -> ServerStartCallback.EVENT.invoker().onStartServer(server));
        ServerLifecycleEvents.SERVER_STOPPING.register(server -> ServerStopCallback.EVENT.invoker().onStopServer(server));
        ServerTickEvents.END_SERVER_TICK.register(server -> ServerTickCallback.EVENT.invoker().tick(server));
        ServerTickEvents.END_WORLD_TICK.register(world -> WorldTickCallback.EVENT.invoker().tick(world));
    }
}
