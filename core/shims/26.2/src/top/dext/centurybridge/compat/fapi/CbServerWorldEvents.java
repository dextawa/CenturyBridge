package top.dext.centurybridge.compat.fapi;

import net.fabricmc.fabric.api.event.Event;
import net.fabricmc.fabric.api.event.EventFactory;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerLevelEvents;

/**
 * fabric-lifecycle-events-v1's ServerWorldEvents as 1.20.x mods call it (7
 * corpus mods). 26.x: ServerLevelEvents, SAMs onWorldLoad/onWorldUnload became
 * onLevelLoad/onLevelUnload -- a class rename alone would leave 1.20.1 lambdas
 * an AbstractMethodError, so the old-shape events live here and one real
 * listener each forwards into them.
 */
public final class CbServerWorldEvents {

    public static final Event<CbServerWorldLoad> LOAD = EventFactory.createArrayBacked(CbServerWorldLoad.class,
        listeners -> (server, level) -> {
            for (CbServerWorldLoad l : listeners) {
                l.onWorldLoad(server, level);
            }
        });

    public static final Event<CbServerWorldUnload> UNLOAD = EventFactory.createArrayBacked(CbServerWorldUnload.class,
        listeners -> (server, level) -> {
            for (CbServerWorldUnload l : listeners) {
                l.onWorldUnload(server, level);
            }
        });

    static {
        ServerLevelEvents.LOAD.register((server, level) -> LOAD.invoker().onWorldLoad(server, level));
        ServerLevelEvents.UNLOAD.register((server, level) -> UNLOAD.invoker().onWorldUnload(server, level));
    }

    private CbServerWorldEvents() {
    }
}
