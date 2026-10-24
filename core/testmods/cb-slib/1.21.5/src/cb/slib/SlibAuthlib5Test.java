package cb.slib;

import com.mojang.authlib.GameProfile;
import com.mojang.authlib.GameProfileRepository;
import com.mojang.authlib.ProfileLookupCallback;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import net.fabricmc.api.ModInitializer;

/**
 * A 1.21.4-era mod (compiled against authlib 6.0.57) for slib's library bridges of the 1.21.4 -> 1.21.5 step.
 * Each check prints "CB-SLIB ok|FAIL ..."; the last line is "CB-SLIB DONE passed/checks".
 */
public class SlibAuthlib5Test implements ModInitializer {
    static final List<String> FAILS = new ArrayList<>();
    static int checks;

    static void check(boolean ok, String what) {
        checks++;
        System.out.println("CB-SLIB " + (ok ? "ok   " : "FAIL ") + what);
        if (!ok) FAILS.add(what);
    }

    static final UUID ID = UUID.fromString("069a79f4-44e9-4726-a5be-fca90e38aaf5");

    static class MyRepo implements GameProfileRepository {
        @Override
        public void findProfilesByNames(String[] names, ProfileLookupCallback callback) {
            for (String n : names) {
                if (n.equals("Notch")) callback.onProfileLookupSucceeded(new GameProfile(ID, n));
                else callback.onProfileLookupFailed(n, new IllegalArgumentException("unknown"));
            }
        }
    }

    static Optional<?> byName(GameProfileRepository repo, String name) throws ReflectiveOperationException {
        return (Optional<?>) GameProfileRepository.class.getMethod("findProfileByName", String.class).invoke(repo, name);
    }

    @Override
    public void onInitialize() {
        try {
            Optional<?> hit = byName(new MyRepo(), "Notch");
            check(hit.isPresent() && ID.equals(((GameProfile) hit.get()).getId()), "a 6.0.57 repository answers findProfileByName");
            check(byName(new MyRepo(), "Nobody").isEmpty(), "a failed lookup is empty");
            GameProfileRepository lambda = (names, cb) -> cb.onProfileLookupSucceeded(new GameProfile(ID, names[0]));
            Optional<?> l = byName(lambda, "Jeb");
            check(l.isPresent() && "Jeb".equals(((GameProfile) l.get()).getName()), "a lambda repository answers findProfileByName");
        } catch (Throwable e) {
            check(false, "threw " + e);
            e.printStackTrace(System.out);
        }
        System.out.println("CB-SLIB DONE " + (checks - FAILS.size()) + "/" + checks);
    }
}
