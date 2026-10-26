package top.dext.centurybridge.step.v26_1.rt;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.WeakHashMap;
import net.minecraft.world.level.storage.LevelStorageSource;

/**
 * The level storage accesses open in this process (mixin/CbWallLevelAccessMixin), for a 1.21.11 call that
 * handed over a world's level data without the access 26.1 reads the rest of that world through.
 */
public final class CbWallLevelAccess {

    private CbWallLevelAccess() {
    }

    private static final Map<LevelStorageSource.LevelStorageAccess, Boolean> OPEN = Collections.synchronizedMap(new WeakHashMap<>());

    public static void opened(LevelStorageSource.LevelStorageAccess access) {
        OPEN.put(access, Boolean.TRUE);
    }

    public static void closed(LevelStorageSource.LevelStorageAccess access) {
        OPEN.remove(access);
    }

    /**
     * The one open access of {@code source}: a 1.21.11 caller holding level data read it through an open
     * access (reading needs the access's lock), and a level source opens a world one access at a time.
     * None or several open is a call 1.21.11's data does not tie to one world: refused, with the reason.
     */
    public static LevelStorageSource.LevelStorageAccess onlyOpen(LevelStorageSource source, String what) {
        List<LevelStorageSource.LevelStorageAccess> found = new ArrayList<>();
        synchronized (OPEN) {
            for (LevelStorageSource.LevelStorageAccess access : OPEN.keySet()) {
                if (access.parent() == source) {
                    found.add(access);
                }
            }
        }
        if (found.size() != 1) {
            throw new IllegalStateException(what + ": 26.1 reads the world's generation settings and saved data from the world's"
                + " directory, and 1.21.11's call names the world only through its level data; " + found.size()
                + " worlds of this level source are open, so the data belongs to no single one");
        }
        return found.get(0);
    }

    /**
     * The one open access in this process, for a 1.21.11 call that names neither the access nor its level source
     * (a level.dat tag it read through it). None or several open: refused, with the reason.
     */
    public static LevelStorageSource.LevelStorageAccess onlyOpen(String what) {
        List<LevelStorageSource.LevelStorageAccess> found;
        synchronized (OPEN) {
            found = new ArrayList<>(OPEN.keySet());
        }
        if (found.size() != 1) {
            throw new IllegalStateException(what + ": 26.1 reads the world's generation settings and saved data from the world's"
                + " directory, and 1.21.11's call names the world only through its level data; " + found.size()
                + " worlds are open, so the data belongs to no single one");
        }
        return found.get(0);
    }
}
