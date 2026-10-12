package top.dext.centurybridge.rt.v1_20_2;

/**
 * 1.20.1 MeleeAttackGoal.checkAndPerformAttack(target, distanceSqr) and getAttackReachSqr(target)
 * (both protected), answered again by every MeleeAttackGoal through a mixin. 1.20.2's goal checks
 * Mob.isWithinMeleeAttackRange in checkAndPerformAttack(target) and calls neither.
 *
 * A converted subclass's overrides may be protected, which an interface call cannot select, so
 * the bridges use the public trampolines: {@link #cb$legacyAttack} runs 1.20.1's tick step
 * (checkAndPerformAttack with the mob's perceived distance), {@link #cb$legacyAttackReach} calls
 * getAttackReachSqr virtually.
 */
public interface CbLegacyMeleeGoal {

    /** 1.20.1 checkAndPerformAttack(target, distanceSqr) */
    void method_6288(net.minecraft.class_1309 target, double distanceSqr);

    /** 1.20.1 getAttackReachSqr(target) */
    double method_6289(net.minecraft.class_1309 target);

    /** what 1.20.1's tick did: checkAndPerformAttack(target, mob.getPerceivedTargetDistanceSquareForMeleeAttack(target)) */
    void cb$legacyAttack(net.minecraft.class_1309 target);

    /** getAttackReachSqr(target), called virtually */
    double cb$legacyAttackReach(net.minecraft.class_1309 target);
}
