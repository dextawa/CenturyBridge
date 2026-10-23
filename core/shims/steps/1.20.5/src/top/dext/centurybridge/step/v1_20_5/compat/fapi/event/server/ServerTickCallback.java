package top.dext.centurybridge.step.v1_20_5.compat.fapi.event.server;

import net.fabricmc.fabric.api.event.Event;
import net.fabricmc.fabric.api.event.EventFactory;

/**
 * Fabric API 0.97.3's (1.20.4) fabric-events-lifecycle-v0 ServerTickCallback: invoked at the end of
 * every server tick (lifecycle v1's END_SERVER_TICK, CbLegacyEventInvokers).
 */
@Deprecated
public interface ServerTickCallback {
    @Deprecated
    Event<ServerTickCallback> EVENT = EventFactory.createArrayBacked(ServerTickCallback.class,
        listeners -> server -> {
            for (ServerTickCallback event : listeners) {
                event.tick(server);
            }
        });

    void tick(net.minecraft.server.MinecraftServer server);
}
