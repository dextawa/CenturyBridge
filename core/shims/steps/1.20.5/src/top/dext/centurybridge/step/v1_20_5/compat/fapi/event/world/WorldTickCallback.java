package top.dext.centurybridge.step.v1_20_5.compat.fapi.event.world;

import net.fabricmc.fabric.api.event.Event;
import net.fabricmc.fabric.api.event.EventFactory;

/**
 * Fabric API 0.97.3's (1.20.4) fabric-events-lifecycle-v0 WorldTickCallback: invoked at the end of every
 * world tick, server worlds (lifecycle v1's END_WORLD_TICK) and the client world
 * (ClientTickEvents.END_WORLD_TICK), as 1.20.4's legacy invokers registered it (CbLegacyEventInvokers).
 */
@Deprecated
public interface WorldTickCallback {
    @Deprecated
    Event<WorldTickCallback> EVENT = EventFactory.createArrayBacked(WorldTickCallback.class,
        listeners -> world -> {
            for (WorldTickCallback event : listeners) {
                event.tick(world);
            }
        });

    void tick(net.minecraft.class_1937 world);
}
