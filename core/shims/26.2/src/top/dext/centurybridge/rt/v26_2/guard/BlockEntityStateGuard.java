package top.dext.centurybridge.rt.v26_2.guard;

import java.util.Collections;
import java.util.IdentityHashMap;
import java.util.Set;

/**
 * Re-entry guard for the synthesized createRenderState of 1.20.1 block entity renderers: the first entry runs
 * the bridge, a second entry on the same object (the bridge deferring to vanilla) goes to
 * super. One guard per hooked method -- a guard shared between methods would send an
 * unrelated hooked call made during the first one (getTextureLocation while submit runs)
 * to super, which for an abstract new method is an AbstractMethodError.
 */
public final class BlockEntityStateGuard {

    private static final ThreadLocal<Set<Object>> IN =
        ThreadLocal.withInitial(() -> Collections.newSetFromMap(new IdentityHashMap<>()));

    public static boolean cbEnter(Object self) {
        return IN.get().add(self);
    }

    public static void cbExit(Object self) {
        IN.get().remove(self);
    }

    private BlockEntityStateGuard() {
    }
}
