package top.dext.centurybridge.step.v26_1.mixin;

import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.animal.golem.IronGolem;
import net.minecraft.world.entity.monster.breeze.Breeze;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import top.dext.centurybridge.step.v26_1.rt.CbWallAttackType;

/**
 * IronGolem.canAttack / Breeze.canAttack (26.1) make their type checks themselves and may answer before Mob.canAttack
 * runs: they read the restored canAttackType after their own bodies ran, as Mob.canAttack does
 * (CbWallMobCanAttackMixin, rt/CbWallAttackType.canAttack). Unmixed, 26.1's answer stands. Default priority.
 */
@Mixin({IronGolem.class, Breeze.class})
public abstract class CbWallGolemBreezeCanAttackMixin {

    @Inject(method = "canAttack(Lnet/minecraft/world/entity/LivingEntity;)Z", at = @At("RETURN"), cancellable = true)
    private void cb$attackType(LivingEntity target, CallbackInfoReturnable<Boolean> cir) {
        boolean w = cir.getReturnValueZ();
        boolean answer = CbWallAttackType.canAttack((Mob) (Object) this, target, w);
        if (answer != w) {
            cir.setReturnValue(answer);
        }
    }
}
