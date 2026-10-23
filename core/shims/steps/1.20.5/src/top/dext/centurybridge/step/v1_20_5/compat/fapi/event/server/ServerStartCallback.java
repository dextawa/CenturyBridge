package top.dext.centurybridge.step.v1_20_5.compat.fapi.event.server;

import net.fabricmc.fabric.api.event.Event;
import net.fabricmc.fabric.api.event.EventFactory;

/**
 * Fabric API 0.97.3's (1.20.4) fabric-events-lifecycle-v0 ServerStartCallback, a module 1.20.5's Fabric
 * API no longer ships: invoked when the server has started (lifecycle v1's SERVER_STARTED,
 * impl/event/lifecycle/v0/CbLegacyEventInvokers, as 1.20.4's LegacyEventInvokers did).
 */
@Deprecated
public interface ServerStartCallback {
    @Deprecated
    Event<ServerStartCallback> EVENT = EventFactory.createArrayBacked(ServerStartCallback.class,
        listeners -> server -> {
            for (ServerStartCallback event : listeners) {
                event.onStartServer(server);
            }
        });

    void onStartServer(net.minecraft.server.MinecraftServer server);
}
