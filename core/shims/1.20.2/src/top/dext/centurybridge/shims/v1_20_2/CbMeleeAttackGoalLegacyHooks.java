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
 * The injectors into 1.20.2's own methods that go with {@link CbMeleeAttackGoalLegacy} (its restored 1.20.1 members
 * stay there, at {@code CbVanillaReads.PRIORITY}, so a mod's 1.20.1 injectors can anchor inside them).
 */
@Mixin(class_1366.class)
public abstract class CbMeleeAttackGoalLegacyHooks {
    /** canPerformAttack(target): a converted mob with its own 1.20.1 melee range is tested as 1.20.1's goal tested it */
    @Redirect(method = "method_53715", at = @At(value = "INVOKE",
        target = "Lnet/minecraft/class_1314;method_42150(Lnet/minecraft/class_1309;)Z"))
    private boolean cb$inRange(class_1314 mob, class_1309 target) {
        if (mob instanceof CbLegacyMeleeRangeOverride legacy) {
            return CbMelee.goalInRange((CbLegacyMeleeGoal) (Object) this, legacy, target);
        }
        return mob.method_42150(target);
    }
}
