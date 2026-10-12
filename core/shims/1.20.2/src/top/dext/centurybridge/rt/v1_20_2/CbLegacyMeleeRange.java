package top.dext.centurybridge.rt.v1_20_2;

/**
 * 1.20.1 Mob melee range (s1misc melee bridge), answered again by every mob: a mixin on Mob
 * implements it with 1.20.1's bodies, a converted subclass's override takes its place by
 * ordinary virtual dispatch. 1.20.2 replaced the squared-distance test with attack boxes
 * (Mob.isWithinMeleeAttackRange / getAttackBoundingBox) and calls neither method.
 */
public interface CbLegacyMeleeRange {

    /** 1.20.1 Mob.getMeleeAttackRangeSqr(target) */
    double method_33191(net.minecraft.class_1309 target);

    /** 1.20.1 Mob.getPerceivedTargetDistanceSquareForMeleeAttack(target) */
    double method_47922(net.minecraft.class_1309 target);
}
