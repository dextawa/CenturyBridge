package top.dext.centurybridge.step.v1_20_5.compat.fapi.event.server;

import net.fabricmc.fabric.api.event.Event;
import net.fabricmc.fabric.api.event.EventFactory;

/**
 * Fabric API 0.97.3's (1.20.4) fabric-events-lifecycle-v0 ServerStopCallback: invoked as the server
 * stops (lifecycle v1's SERVER_STOPPING, CbLegacyEventInvokers).
 */
@Deprecated
public interface ServerStopCallback {
    @Deprecated
    Event<ServerStopCallback> EVENT = EventFactory.createArrayBacked(ServerStopCallback.class,
        listeners -> server -> {
            for (ServerStopCallback event : listeners) {
                event.onStopServer(server);
            }
        });

    void onStopServer(net.minecraft.server.MinecraftServer server);
}
