package top.dext.centurybridge.step.v1_21_9.rt;

import com.mojang.authlib.GameProfile;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import net.fabricmc.api.EnvType;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.class_11755;
import net.minecraft.class_156;
import net.minecraft.class_3544;
import net.minecraft.server.MinecraftServer;

/**
 * SkullBlockEntity.fetchGameProfile(name), a static 1.21.9 dropped with the skull's own profile cache (javap: absent
 * from 1.21.9's class_2631). 1.21.8: an empty profile when the cache was not set up or the name is not a valid player
 * name, else the cache's future, loading the profile by name off-thread. 1.21.9 resolves names through the services'
 * ProfileResolver (Services.profileResolver(), fetchByName, synchronous): the name is resolved through it on the
 * background executor, the client's services on a client, the server's on a dedicated server.
 */
public final class CbSkullProfiles {

    private CbSkullProfiles() {
    }

    /** SkullBlockEntity.fetchGameProfile(name) */
    public static CompletableFuture<Optional<GameProfile>> method_52580(String name) {
        class_11755 resolver = resolver();
        if (resolver == null || !class_3544.method_57179(name)) {
            return CompletableFuture.completedFuture(Optional.empty());
        }
        return CompletableFuture.supplyAsync(() -> resolver.method_73289(name), class_156.method_18349());
    }

    private static class_11755 resolver() {
        if (FabricLoader.getInstance().getEnvironmentType() == EnvType.CLIENT) {
            return CbSkullProfilesClient.resolver();
        }
        MinecraftServer server = CbProfiles.cbServer();
        return server == null ? null : server.method_73550().comp_4624();
    }
}
