package cb.sk203n;

import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerLifecycleEvents;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Native 1.20.3 counterpart of cb-sk203 (never converted): reads IdMapper.tToId as 1.20.3's Reference2IntMap and counts
 * IdMapper.getId calls on cb-sk203's probe keys. Prints "CB-SK203N ..." one tick after the server started (cb-sk203 ran).
 */
public class Sk203Native implements ModInitializer {
    public static final int[] GET_ID = new int[1];
    private static final Logger LOG = LoggerFactory.getLogger("cb_sk203n");

    @Override
    public void onInitialize() {
        ServerLifecycleEvents.SERVER_STARTED.register(server -> server.execute(() -> {
            net.minecraft.class_2680 stone = net.minecraft.class_2246.field_10340.method_9564();
            int id = net.minecraft.class_2248.field_10651.field_11100.getInt(stone);
            LOG.info("CB-SK203N stoneId " + id + " (vanilla " + net.minecraft.class_2248.method_9507(stone) + ") getIdCalls " + GET_ID[0]);
        }));
    }
}
