package top.dext.centurybridge.rt.v1_20_2;

import java.util.Collections;
import java.util.IdentityHashMap;
import java.util.Set;

/**
 * Re-entry guard for this step's override hooks: the new shape synthesized in a mod class
 * forwards to the mod's old override once per object and call; if that override's own
 * super call comes back into the synthesized method, it falls through to super instead of
 * forwarding again.
 */
public final class Guard {

    private static final ThreadLocal<Set<Object>> ACTIVE =
        ThreadLocal.withInitial(() -> Collections.newSetFromMap(new IdentityHashMap<>()));

    public static boolean cbEnter(Object self) {
        return ACTIVE.get().add(self);
    }

    public static void cbExit(Object self) {
        ACTIVE.get().remove(self);
    }

    private Guard() {
    }
}
