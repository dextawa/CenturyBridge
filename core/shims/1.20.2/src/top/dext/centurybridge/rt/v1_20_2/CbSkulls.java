package top.dext.centurybridge.rt.v1_20_2;

import com.mojang.authlib.GameProfile;
import java.util.function.Consumer;
import net.minecraft.class_3544;
import top.dext.centurybridge.shims.v1_20_2.CbSkullAccess;

/**
 * 1.20.1 SkullBlockEntity.updateGameprofile(profile, consumer) on 1.20.2 (s1misc).
 *
 * 1.20.1: a profile with a name but no textures, while the profile cache and session service are
 * set up, is looked up by name in the cache, its textures filled in by the session service, the
 * result put in the cache and handed to the consumer on the main thread (the original profile when
 * the name is unknown); any other profile goes to the consumer at once. 1.20.2's
 * fetchGameProfile(name) is that same pipeline, answering a future; the consumer is completed
 * from it.
 *
 * 1.20.1's "cache and session service set up" test is fetchGameProfile's own: 1.20.2 sets both fields together
 * (setup(Services, Executor): profileCache() and sessionService() of the same Services) and clears both together
 * (clear()), and fetchGameProfile answers a completed empty future when the cache is null -- so the consumer gets the
 * original profile at once, on the calling thread, as 1.20.1's else branch gave it (javap class_2631 1.20.2:
 * method_39765, method_39766, method_52580). The fields are not read: they do not survive 1.20.3.
 */
public final class CbSkulls {

    public static void method_11335(GameProfile profile, Consumer<GameProfile> consumer) {
        if (profile != null && !class_3544.method_15438(profile.getName())
                && !profile.getProperties().containsKey("textures")) {
            CbSkullAccess.cb$fetchGameProfile(profile.getName())
                .thenAccept(found -> consumer.accept(found.orElse(profile)));
        } else {
            consumer.accept(profile);
        }
    }

    private CbSkulls() {
    }
}
