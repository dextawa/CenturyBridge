package top.dext.centurybridge.step.v1_20_5.compat.fapi.event.client;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.event.Event;
import net.fabricmc.fabric.api.event.EventFactory;

/**
 * Fabric API 0.97.3's (1.20.4) fabric-events-lifecycle-v0 ClientTickCallback: invoked at the end of every
 * client tick (ClientTickEvents.END_CLIENT_TICK, CbLegacyClientEventInvokers).
 */
@Deprecated
@Environment(EnvType.CLIENT)
public interface ClientTickCallback {
    @Deprecated
    Event<ClientTickCallback> EVENT = EventFactory.createArrayBacked(ClientTickCallback.class,
        listeners -> client -> {
            for (ClientTickCallback event : listeners) {
                event.tick(client);
            }
        });

    void tick(net.minecraft.class_310 client);
}
