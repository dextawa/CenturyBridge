package top.dext.centurybridge.step.v26_1.mixin;

import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.targeting.TargetingConditions;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;
import top.dext.centurybridge.step.v26_1.rt.CbWallAttackType;

/**
 * 1.21.11's TargetingConditions.test required attacker.canAttack(target) and then attacker.canAttackType(target's
 * type) (CbWallLivingEntityAttackTypeMixin); 26.1 asks canAttack only. The type is asked again after it.
 */
@Mixin(TargetingConditions.class)
public abstract class CbWallTargetingAttackTypeMixin {

    @Redirect(method = "test", at = @At(value = "INVOKE",
        target = "Lnet/minecraft/world/entity/LivingEntity;canAttack(Lnet/minecraft/world/entity/LivingEntity;)Z"))
    private boolean cb$canAttackAndType(LivingEntity attacker, LivingEntity target) {
        return attacker.canAttack(target) && ((CbWallAttackType) attacker).canAttackType(target.getType());
    }
}
