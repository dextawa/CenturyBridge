package top.dext.centurybridge.rt.v1_20_2;

import net.minecraft.class_1308;
import net.minecraft.class_1407;
import net.minecraft.class_1408;
import net.minecraft.class_1409;
import net.minecraft.class_1412;
import net.minecraft.class_243;
import top.dext.centurybridge.shims.v1_20_2.CbWaterBoundNavigationAccess;

/**
 * 1.20.1 PathNavigation.isInLiquid() overrides on 1.20.2's navigations (s1misc navigation bridge).
 *
 * 1.20.1's navigations consulted their own isInLiquid() in canUpdatePath (ground, flying and
 * water-bound navigation) and in AmphibiousPathNavigation.canMoveDirectly; 1.20.2 asks the mob
 * instead, so a converted navigation's override went dormant. Its class gets those methods
 * synthesized (guarded override hooks) to run 1.20.1's body of the vanilla navigation it extends,
 * with its own isInLiquid(); a navigation whose 1.20.1 canUpdatePath never asked
 * (AmphibiousPathNavigation) falls through to 1.20.2's.
 */
public final class CbNavigation {

    /** 1.20.2 canUpdatePath() of a converted navigation overriding isInLiquid() */
    public static boolean canUpdatePath(CbLegacyNavigation nav) {
        class_1408 self = (class_1408) nav;
        class_1308 mob = nav.cb$navMob();
        if (self instanceof class_1412 water) {
            return ((CbWaterBoundNavigationAccess) water).cb$allowBreaching() || nav.cb$legacyIsInLiquid();
        }
        if (self instanceof class_1409) {
            return mob.method_24828() || nav.cb$legacyIsInLiquid() || mob.method_5765();
        }
        if (self instanceof class_1407) {
            return self.method_6350() && nav.cb$legacyIsInLiquid() || !mob.method_5765();
        }
        // 1.20.1's canUpdatePath of this navigation did not ask isInLiquid(): 1.20.2's answers
        // (the call comes back into the synthesized method, whose guard falls through to super)
        return nav.cb$canUpdatePath();
    }

    /** 1.20.2 canMoveDirectly(from, to) of a converted AmphibiousPathNavigation overriding isInLiquid() */
    public static boolean amphibiousCanMoveDirectly(CbLegacyNavigation nav, class_243 from, class_243 to) {
        return nav.cb$amphibiousCanMoveDirectly(from, to);
    }

    private CbNavigation() {
    }
}
