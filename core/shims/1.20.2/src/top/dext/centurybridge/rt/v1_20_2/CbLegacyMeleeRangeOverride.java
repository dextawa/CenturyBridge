package top.dext.centurybridge.rt.v1_20_2;

/**
 * Marks a converted mob class that overrides 1.20.1's melee range or perceived target distance:
 * the override hooks add this interface and synthesize 1.20.2's isWithinMeleeAttackRange
 * ({@link CbMelee#isWithinMeleeAttackRange}); a vanilla MeleeAttackGoal driving such a mob keeps
 * 1.20.1's goal test ({@link CbMelee#goalInRange}).
 */
public interface CbLegacyMeleeRangeOverride extends CbLegacyMeleeRange {

    @Override
    double method_33191(net.minecraft.class_1309 target);

    @Override
    double method_47922(net.minecraft.class_1309 target);
}
