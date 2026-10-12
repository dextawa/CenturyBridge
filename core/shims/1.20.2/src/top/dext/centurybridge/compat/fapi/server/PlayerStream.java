package top.dext.centurybridge.compat.fapi.server;

import java.util.stream.Stream;
import net.fabricmc.fabric.api.networking.v1.PlayerLookup;

/**
 * Fabric API networking v0's PlayerStream (removed with 1.20.2's Fabric API): players of a server, a
 * world, watching a position, entity or block entity, or within a distance -- the 1.20.1 bodies, on
 * 1.20.2's player lists and networking v1's PlayerLookup as 1.20.1's were.
 */
public final class PlayerStream {

    private static final String SERVER_WORLD_ONLY = "Only supported on ServerWorld!";

    private PlayerStream() {
    }

    public static Stream<net.minecraft.class_3222> all(net.minecraft.server.MinecraftServer server) {
        if (server.method_3760() != null) {
            return server.method_3760().method_14571().stream();
        }
        return Stream.empty();
    }

    @SuppressWarnings("unchecked")
    public static Stream<net.minecraft.class_1657> world(net.minecraft.class_1937 world) {
        if (world instanceof net.minecraft.class_3218 serverWorld) {
            return (Stream<net.minecraft.class_1657>) (Stream<? extends net.minecraft.class_1657>) serverWorld.method_18456().stream();
        }
        throw new RuntimeException(SERVER_WORLD_ONLY);
    }

    @SuppressWarnings("unchecked")
    public static Stream<net.minecraft.class_1657> watching(net.minecraft.class_1937 world, net.minecraft.class_1923 pos) {
        if (world instanceof net.minecraft.class_3218 serverWorld) {
            return (Stream<net.minecraft.class_1657>) (Stream<? extends net.minecraft.class_1657>) PlayerLookup.tracking(serverWorld, pos).stream();
        }
        throw new RuntimeException(SERVER_WORLD_ONLY);
    }

    @SuppressWarnings("unchecked")
    public static Stream<net.minecraft.class_1657> watching(net.minecraft.class_1297 entity) {
        return (Stream<net.minecraft.class_1657>) (Stream<? extends net.minecraft.class_1657>) PlayerLookup.tracking(entity).stream();
    }

    public static Stream<net.minecraft.class_1657> watching(net.minecraft.class_2586 blockEntity) {
        return watching(blockEntity.method_10997(), blockEntity.method_11016());
    }

    public static Stream<net.minecraft.class_1657> watching(net.minecraft.class_1937 world, net.minecraft.class_2338 pos) {
        return watching(world, new net.minecraft.class_1923(pos));
    }

    public static Stream<net.minecraft.class_1657> around(net.minecraft.class_1937 world, net.minecraft.class_243 vector, double dist) {
        double distSq = dist * dist;
        return world(world).filter(player -> player.method_5707(vector) <= distSq);
    }

    public static Stream<net.minecraft.class_1657> around(net.minecraft.class_1937 world, net.minecraft.class_2338 pos, double dist) {
        double distSq = dist * dist;
        return world(world).filter(player -> player.method_5649(pos.method_10263(), pos.method_10264(), pos.method_10260()) <= distSq);
    }
}
