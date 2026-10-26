package cb.s203n;

import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerLifecycleEvents;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Native 1.20.3 counterpart of cb-s203 (never converted): its mixins sit on the 1.20.3 methods CenturyBridge's 1.20.3
 * layer routes through (Blocks.leaves, SkullBlockEntity.fetchProfileByName). Prints "CB-S203N ..." after the server
 * started and again after cb-s203 ran (one tick later).
 */
public class S203Native implements ModInitializer {
    public static final int[] LEAVES = new int[1];
    public static final int[] CACHE_READS = new int[1];
    public static final List<String> LOOKUPS = new CopyOnWriteArrayList<>();
    private static final Logger LOG = LoggerFactory.getLogger("cb_s203n");

    @Override
    public void onInitialize() {
        ServerLifecycleEvents.SERVER_STARTED.register(server -> server.execute(() ->
            LOG.info("CB-S203N leaves " + LEAVES[0] + " lookups " + LOOKUPS + " cacheReads " + CACHE_READS[0])));
    }
}
