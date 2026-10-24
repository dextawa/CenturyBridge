package top.dext.centurybridge.step.v26_1.rt;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.client.Minecraft;
import net.minecraft.core.RegistryAccess;
import net.minecraft.server.MinecraftServer;

/**
 * The registries a 1.21.11 call site would have passed where 26.1 stopped taking them (Recipe.assemble's
 * HolderLookup.Provider was the level's registry access): the running server's, else, on the client, the
 * client level's. Both hold the same registries for the same world.
 */
public final class CbWallRegistries {

    private static final boolean CLIENT = FabricLoader.getInstance().getEnvironmentType() == EnvType.CLIENT;

    private CbWallRegistries() {
    }

    public static RegistryAccess access() {
        MinecraftServer server = CbWallLevelData.SERVER;
        if (server != null) {
            return server.registryAccess();
        }
        if (CLIENT) {
            RegistryAccess client = Client.access();
            if (client != null) {
                return client;
            }
        }
        throw new IllegalStateException("1.21.11 call needing the level's registries with no server or client level");
    }

    /** {@link #access()}, or null while no server or client level is up (an item's default components) */
    public static RegistryAccess accessOrNull() {
        MinecraftServer server = CbWallLevelData.SERVER;
        if (server != null) {
            return server.registryAccess();
        }
        return CLIENT ? Client.access() : null;
    }

    @Environment(EnvType.CLIENT)
    private static final class Client {
        static RegistryAccess access() {
            Minecraft mc = Minecraft.getInstance();
            return mc.level != null ? mc.level.registryAccess() : null;
        }
    }
}
