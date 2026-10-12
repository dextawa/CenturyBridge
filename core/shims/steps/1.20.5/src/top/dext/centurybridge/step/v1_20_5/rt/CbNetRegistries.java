package top.dext.centurybridge.step.v1_20_5.rt;

import net.fabricmc.api.EnvType;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.class_5455;
import net.minecraft.class_7923;
import net.minecraft.server.MinecraftServer;

/**
 * The registries a 1.20.4 buffer is read and written with on 1.20.5 (area s4net).
 *
 * A 1.20.4 FriendlyByteBuf carried no registries: items went over the wire as a static-registry id
 * plus NBT, components as NBT, and a packet was a sequence of such fields. 1.20.5 writes items,
 * holders and components through a RegistryFriendlyByteBuf, whose registries the connection supplies
 * (its protocol is bound to them). A buffer a 1.20.4 mod made itself has none, so it gets the
 * registries of the game it is used in -- what the same bytes on that game's connection would be
 * written and read with:
 * <ol>
 * <li>on a running server's own thread, that server's;</li>
 * <li>on a client connected to a world, the registries its connection synchronized;</li>
 * <li>otherwise a running server's (a dedicated server's network and worker threads, an integrated
 *     server used off the client's connection);</li>
 * <li>with no world at all, the static registries alone -- all a 1.20.4 buffer ever relied on, and
 *     enough for anything that names no data-driven entry.</li>
 * </ol>
 */
public final class CbNetRegistries {

    /** the static registries, as {@code RegistryAccess.fromRegistryOfRegistries(BuiltInRegistries.REGISTRY)} */
    private static volatile class_5455.class_6890 builtin;

    private static final boolean CLIENT = FabricLoader.getInstance().getEnvironmentType() == EnvType.CLIENT;

    public static class_5455 cbCurrent() {
        MinecraftServer server = cbRunningServer();
        if (server != null && server.method_18854()) {
            return server.method_30611();
        }
        if (CLIENT) {
            class_5455 connected = CbNetClientRegistries.cbConnection();
            if (connected != null) {
                return connected;
            }
        }
        if (server != null) {
            return server.method_30611();
        }
        class_5455.class_6890 b = builtin;
        if (b == null) {
            b = class_5455.method_40302(class_7923.field_41167);
            builtin = b;
        }
        return b;
    }

    /** the dedicated server, or the client's integrated one, while it runs; null otherwise */
    public static MinecraftServer cbRunningServer() {
        MinecraftServer server = null;
        if (CLIENT) {
            server = CbNetClientRegistries.cbIntegratedServer();
        } else if (FabricLoader.getInstance().getGameInstance() instanceof MinecraftServer dedicated) {
            server = dedicated;
        }
        return server != null && server.method_3806() ? server : null;
    }

    private CbNetRegistries() {
    }
}
