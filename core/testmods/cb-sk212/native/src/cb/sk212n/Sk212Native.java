package cb.sk212n;

import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerLifecycleEvents;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Native 1.21.2 counterpart of cb-sk212 (never converted): counts furnace saves whose 1.21.2 recipesUsed holds a key of
 * cb-sk212's probe namespace. Prints "CB-SK212N savesWithProbe N" one tick after the server started.
 */
public class Sk212Native implements ModInitializer {
    public static final int[] SAVES_WITH_PROBE = new int[1];
    private static final Logger LOG = LoggerFactory.getLogger("cb_sk212n");

    @Override
    public void onInitialize() {
        ServerLifecycleEvents.SERVER_STARTED.register(server -> server.execute(() ->
            LOG.info("CB-SK212N savesWithProbe " + SAVES_WITH_PROBE[0])));
    }
}
