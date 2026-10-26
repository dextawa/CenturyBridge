package top.dext.centurybridge.step.v26_1.mixin;

import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.targeting.TargetingConditions;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import top.dext.centurybridge.step.v26_1.rt.CbWallAttackType;

/**
 * 1.21.11's TargetingConditions.test required, for a combat test with an attacker, attacker.canAttack(target) and
 * then attacker.canAttackType(target's type) (CbWallLivingEntityAttackTypeMixin); 26.1 asks canAttack only. 26.1's
 * test runs unchanged; a true answer turns false where the attacker's canAttackType refuses the type and answers
 * otherwise than 1.21.11's vanilla body for its class (a 1.21.11 mod mixed it, or a 1.21.11 subclass overrides it;
 * rt/CbWallAttackType). Unmixed, 26.1's answer stands. Default priority.
 */
@Mixin(TargetingConditions.class)
public abstract class CbWallTargetingAttackTypeMixin {

    @Shadow
    @Final
    private boolean isCombat;

    @Inject(method = "test(Lnet/minecraft/server/level/ServerLevel;Lnet/minecraft/world/entity/LivingEntity;Lnet/minecraft/world/entity/LivingEntity;)Z",
        at = @At("RETURN"), cancellable = true)
    private void cb$attackType(ServerLevel level, LivingEntity attacker, LivingEntity target,
                               CallbackInfoReturnable<Boolean> cir) {
        if (!cir.getReturnValueZ() || !this.isCombat || attacker == null) {
            return;
        }
        EntityType<?> type = target.getType();
        boolean answer = ((CbWallAttackType) attacker).canAttackType(type);
        if (!answer && CbWallAttackType.mixed(attacker, type, answer)) {
            cir.setReturnValue(false);
        }
    }
}
