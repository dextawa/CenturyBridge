package top.dext.centurybridge.step.v1_21_9.rt;

import com.mojang.authlib.GameProfile;
import com.mojang.authlib.GameProfileRepository;
import com.mojang.authlib.minecraft.MinecraftSessionService;
import com.mojang.authlib.yggdrasil.ServicesKeySet;
import java.util.Optional;
import net.minecraft.class_11560;
import net.minecraft.class_11561;
import net.minecraft.class_11755;
import net.minecraft.class_3312;
import net.minecraft.class_7497;
import net.minecraft.server.MinecraftServer;

/**
 * The user cache, 1.21.8 -> 1.21.9 (javap of both code jars). 1.21.8's GameProfileCache became
 * CachedUserNameToIdResolver behind the UserNameToIdResolver interface; Services holds it as nameToIdCache with a
 * ProfileResolver next to it, and stores NameAndId where 1.21.8 stored GameProfile.
 * <ul>
 * <li>Services.profileCache() answers the cache 1.21.9's Services holds, when it is the vanilla cache class (1.21.8's
 *     Services held only that class; anything else is no GameProfileCache, and 1.21.8's callers null-checked it).</li>
 * <li>new Services(session, keys, repository, cache): 1.21.9's constructor with the ProfileResolver 1.21.9's own
 *     Services.create builds over the same session service and cache (ProfileResolver.Default).</li>
 * <li>MinecraftServer.getProfileCache() is services().profileCache().</li>
 * <li>GameProfileCache.setUsesAuthentication(b) (static: every cache's offline-name rule) is the running server's
 *     resolver's resolveOfflineUsers(!b) (1.21.8's offline lookup answered the offline profile exactly when
 *     usesAuthentication was false; 1.21.9's when resolveOfflineUsers is true). With no server running, 1.21.8's value
 *     was overwritten before any lookup by the server's own setUsesAuthentication at start-up (DedicatedServer's
 *     initServer, the client's integrated server), as 1.21.9 sets resolveOfflineUsers there itself.</li>
 * <li>get(name) / get(uuid) answer Optional&lt;NameAndId&gt; on 1.21.9 (same erased descriptor): {@link #profileOut}
 *     hands 1.21.8 code a GameProfile (CbProfiles.toProfile), {@link #profileIn} turns a 1.21.8 override's answer back.
 *     Both test what they are given (erasure cannot tell who passed the value) and undo each other.</li>
 * </ul>
 * getAsync / setExecutor / clearExecutor are restored on the cache itself (CbProfileCacheRestoredMixin).
 */
public final class CbProfileCache {

    private CbProfileCache() {
    }

    /** Services.profileCache() */
    public static class_3312 comp_840(class_7497 services) {
        class_11561 cache = services.comp_4407();
        return cache instanceof class_3312 c ? c : null;
    }

    /** new Services(sessionService, keySet, repository, profileCache) */
    public static class_7497 services(MinecraftSessionService session, ServicesKeySet keys, GameProfileRepository repository, class_3312 cache) {
        return new class_7497(session, keys, repository, cache, new class_11755.class_11579(session, cache));
    }

    /** MinecraftServer.getProfileCache() */
    public static class_3312 method_3793(MinecraftServer server) {
        return comp_840(server.method_73550());
    }

    /** GameProfileCache.setUsesAuthentication(usesAuthentication) */
    public static void method_14510(boolean usesAuthentication) {
        MinecraftServer server = CbProfiles.cbServer();
        if (server != null) {
            server.method_73550().comp_4407().method_72367(!usesAuthentication);
        }
    }

    /** an Optional of 1.21.9's NameAndId as 1.21.8's Optional of GameProfile; anything else passes */
    @SuppressWarnings({"rawtypes", "unchecked"})
    public static Optional profileOut(Optional value) {
        if (value != null && value.isPresent() && value.get() instanceof class_11560 id) {
            return Optional.of(CbProfiles.toProfile(id));
        }
        return value;
    }

    /** an Optional of 1.21.8's GameProfile as 1.21.9's Optional of NameAndId; anything else passes */
    @SuppressWarnings({"rawtypes", "unchecked"})
    public static Optional profileIn(Optional value) {
        if (value != null && value.isPresent() && value.get() instanceof GameProfile profile) {
            return Optional.of(new class_11560(profile));
        }
        return value;
    }
}
