package cb.s9;

import com.mojang.authlib.GameProfile;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import net.minecraft.class_3312;
import net.minecraft.server.MinecraftServer;

/** 1.21.8 GameProfileCache: getProfileCache, get(name) / get(uuid) answering GameProfile, getAsync with the server's executor (s9c) */
final class ProfileCacheTest {

    private ProfileCacheTest() {
    }

    static void run(MinecraftServer server) {
        S9TestMod.run("profile cache: getProfileCache / get / getAsync / setUsesAuthentication", () -> {
            class_3312 cache = server.method_3793();
            S9TestMod.check(cache != null, "MinecraftServer.getProfileCache() answers the server's cache, got " + cache);
            UUID id = UUID.fromString("0f1e2d3c-4b5a-6978-8796-a5b4c3d2e1f0");
            cache.method_14508(new GameProfile(id, "CbS9cUser"));
            Optional<GameProfile> byName = cache.method_14515("CbS9cUser");
            S9TestMod.check(byName.isPresent() && byName.get().getId().equals(id) && "CbS9cUser".equals(byName.get().getName()),
                "get(name) answers the added profile as a GameProfile, got " + byName);
            Optional<GameProfile> byId = cache.method_14512(id);
            S9TestMod.check(byId.isPresent() && "CbS9cUser".equals(byId.get().getName()), "get(uuid) answers it as a GameProfile, got " + byId);
            CompletableFuture<Optional<GameProfile>> async = cache.method_37156("CbS9cUser");
            S9TestMod.check(async != null, "getAsync(name) runs (the server set the cache's executor)");
            S9TestMod.check(cache.method_37156("CbS9cUser") == async, "a second getAsync for the same name while the first is pending answers the same future");
            async.thenAccept(p -> System.out.println("CB-S9TEST late getAsync(name) on " + Thread.currentThread().getName() + ": "
                + p.map(GameProfile::getName).orElse("<empty>")));
            class_3312.method_14510(server.method_3828());
            S9TestMod.check(true, "GameProfileCache.setUsesAuthentication(server's online mode) ran");
        });
    }
}
