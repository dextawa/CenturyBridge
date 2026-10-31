package cb.s9;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import net.minecraft.class_1303;
import net.minecraft.class_2596;
import net.minecraft.class_3218;
import net.minecraft.class_3231;
import net.minecraft.server.MinecraftServer;

/**
 * 1.21.8 ServerEntity(level, entity, interval, trackDelta, broadcast, broadcastIgnoring) (1.21.9: one Synchronizer):
 * a 1.21.8 mod's ServerEntity is built and its sendChanges sends through the 1.21.8 callback.
 */
final class ServerEntityTest {
    private ServerEntityTest() {
    }

    static void run(MinecraftServer server) {
        S9TestMod.run("server entity: 1.21.8 six-argument constructor", () -> {
            class_3218 level = server.method_30002();
            class_1303 orb = new class_1303(level, 1.25, 70.5, -3.75, 11);
            List<class_2596<?>> sent = new ArrayList<>();
            List<List<UUID>> ignored = new ArrayList<>();
            class_3231 se = new class_3231(level, orb, 1, true, sent::add, (p, skip) -> ignored.add(skip));
            S9TestMod.check(se != null, "new ServerEntity(level, orb, 1, true, broadcast, broadcastIgnoring) links and constructs");
            orb.method_5814(5.0, 70.5, -3.75);
            se.method_18756();
            S9TestMod.check(!sent.isEmpty(), "its sendChanges after a move broadcasts through the 1.21.8 Consumer, got "
                + sent.size() + " packets");
        });
    }
}
