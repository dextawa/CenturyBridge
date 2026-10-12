package top.dext.centurybridge.shims.v1_20_2;

import net.minecraft.class_1268;
import net.minecraft.class_1309;
import net.minecraft.class_1314;
import net.minecraft.class_1366;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;
import top.dext.centurybridge.rt.v1_20_2.CbLegacyMeleeGoal;
import top.dext.centurybridge.rt.v1_20_2.CbLegacyMeleeRange;
import top.dext.centurybridge.rt.v1_20_2.CbLegacyMeleeRangeOverride;
import top.dext.centurybridge.rt.v1_20_2.CbMelee;

/**
 * 1.20.1's MeleeAttackGoal attack test back (s1misc melee bridge), 1.20.1 bodies:
 * checkAndPerformAttack(target, distanceSqr) attacks when the distance is within
 * getAttackReachSqr(target) and the cooldown is over; getAttackReachSqr is twice the mob's width,
 * squared, plus the target's width. 1.20.2's goal calls neither: a converted goal overriding one
 * gets 1.20.2's checkAndPerformAttack(target) synthesized to run 1.20.1's tick step
 * ({@link #cb$legacyAttack}); a vanilla goal driving a converted mob that overrides 1.20.1's
 * melee range keeps 1.20.1's goal test in place of the attack-box test.
 */
@Mixin(class_1366.class)
public abstract class CbMeleeAttackGoalLegacy implements CbLegacyMeleeGoal {

    @Shadow
    @Final
    protected class_1314 field_6503;

    @Shadow
    private int field_24667;

    @Shadow
    protected abstract void method_28346();

    @Override
    public void method_6288(class_1309 target, double distanceSqr) {
        double reach = this.method_6289(target);
        if (distanceSqr <= reach && this.field_24667 <= 0) {
            this.method_28346();
            this.field_6503.method_6104(class_1268.field_5808);
            this.field_6503.method_6121(target);
        }
    }

    @Override
    public double method_6289(class_1309 target) {
        return (double) (this.field_6503.method_17681() * 2.0F * this.field_6503.method_17681() * 2.0F + target.method_17681());
    }

    @Override
    public void cb$legacyAttack(class_1309 target) {
        this.method_6288(target, ((CbLegacyMeleeRange) this.field_6503).method_47922(target));
    }

    @Override
    public double cb$legacyAttackReach(class_1309 target) {
        return this.method_6289(target);
    }

    /** canPerformAttack(target): a converted mob with its own 1.20.1 melee range is tested as 1.20.1's goal tested it */
    @Redirect(method = "method_53715", at = @At(value = "INVOKE",
        target = "Lnet/minecraft/class_1314;method_42150(Lnet/minecraft/class_1309;)Z"))
    private boolean cb$inRange(class_1314 mob, class_1309 target) {
        if (mob instanceof CbLegacyMeleeRangeOverride legacy) {
            return CbMelee.goalInRange(this, legacy, target);
        }
        return mob.method_42150(target);
    }
}
