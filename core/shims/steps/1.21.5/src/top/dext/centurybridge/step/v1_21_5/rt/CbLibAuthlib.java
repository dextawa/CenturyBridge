package top.dext.centurybridge.step.v1_21_5.rt;

import com.mojang.authlib.GameProfile;
import com.mojang.authlib.GameProfileRepository;
import com.mojang.authlib.ProfileLookupCallback;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicReference;

/**
 * authlib 6.0.57 (1.21.4) -> 6.0.58 (1.21.5): GameProfileRepository newly demands findProfileByName(name). A 6.0.57
 * repository only answered findProfilesByNames(names, callback), and that is how 1.21.4 looked one name up
 * (GameProfileCache.lookupGameProfile: a one-name array, the profile the callback was handed, empty when none was) -- so a
 * 6.0.57-era mod repository answers 6.0.58's question the same way.
 */
public final class CbLibAuthlib {
    private CbLibAuthlib() {
    }

    /** 6.0.57's repository was a functional interface: a mod's lambda for it, as 6.0.58's two-method repository */
    public static GameProfileRepository fromLambda(Legacy old) {
        return new GameProfileRepository() {
            @Override
            public void findProfilesByNames(String[] names, ProfileLookupCallback callback) {
                old.findProfilesByNames(names, callback);
            }

            @Override
            public Optional<GameProfile> findProfileByName(String name) {
                return CbLibAuthlib.findProfileByName(this, name);
            }
        };
    }

    /** 6.0.57's GameProfileRepository shape: what a mod's lambda for it implements */
    @FunctionalInterface
    public interface Legacy {
        void findProfilesByNames(String[] names, ProfileLookupCallback callback);
    }

    public static Optional<GameProfile> findProfileByName(GameProfileRepository self, String name) {
        AtomicReference<GameProfile> found = new AtomicReference<>();
        self.findProfilesByNames(new String[] {name}, new ProfileLookupCallback() {
            @Override
            public void onProfileLookupSucceeded(GameProfile profile) {
                found.set(profile);
            }

            @Override
            public void onProfileLookupFailed(String profileName, Exception exception) {
                found.set(null);
            }
        });
        return Optional.ofNullable(found.get());
    }
}
