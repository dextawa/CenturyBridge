package top.dext.centurybridge.rt.v1_20_2;

import net.minecraft.class_1309;

/**
 * 1.20.1 melee range on 1.20.2's melee attack (s1misc melee bridge).
 *
 * 1.20.1 decided "in range" by squared distance: Mob.isWithinMeleeAttackRange(target) was
 * getPerceivedTargetDistanceSquareForMeleeAttack(target) <= getMeleeAttackRangeSqr(target) (the
 * brain's melee behaviours), and MeleeAttackGoal compared the mob's perceived distance with its
 * own getAttackReachSqr. 1.20.2 intersects attack boxes instead and calls none of these. The
 * 1.20.1 methods are back on Mob / MeleeAttackGoal (mixins, 1.20.1 bodies); here a converted
 * override is reconnected where 1.20.1 consulted it.
 */
public final class CbMelee {

    /**
     * 1.20.2 isWithinMeleeAttackRange of a converted mob overriding the 1.20.1 range or distance:
     * 1.20.1's own body, so both answer as they did.
     */
    public static boolean isWithinMeleeAttackRange(CbLegacyMeleeRangeOverride mob, class_1309 target) {
        return mob.method_47922(target) <= mob.method_33191(target);
    }

    /**
     * A vanilla MeleeAttackGoal's range test for a converted mob overriding the 1.20.1 range or
     * distance: 1.20.1's goal compared the mob's perceived distance with the goal's own reach
     * (it never asked the mob's getMeleeAttackRangeSqr); 1.20.2's cooldown and line-of-sight
     * tests around it are left as they are.
     */
    public static boolean goalInRange(CbLegacyMeleeGoal goal, CbLegacyMeleeRangeOverride mob, class_1309 target) {
        return mob.method_47922(target) <= goal.cb$legacyAttackReach(target);
    }

    /**
     * 1.20.2 checkAndPerformAttack(target) of a converted goal overriding 1.20.1's
     * checkAndPerformAttack(target, distanceSqr) or getAttackReachSqr: 1.20.1's tick step.
     */
    public static void goalAttack(CbLegacyMeleeGoal goal, class_1309 target) {
        goal.cb$legacyAttack(target);
    }

    private CbMelee() {
    }
}
