package top.dext.centurybridge.step.v26_1.mixin;

import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.level.Level;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import top.dext.centurybridge.step.v26_1.rt.CbWallAttackType;

/**
 * Mob.canAttack(target) (26.1; Phantom inherits it) reads the restored canAttackType (CbWallMobAttackTypeMixin and
 * the overrides) after its own body ran: 26.1's answer stands unless canAttackType answers otherwise than 1.21.11's
 * vanilla body for the mob's class -- then 1.21.11's, LivingEntity.canAttack(target) and canAttackType
 * (rt/CbWallAttackType.canAttack). 26.1's body, its target.is(GHAST) check and whatever a 26.1 mod mixed into them
 * run unchanged. Default priority: an injector into a 26.1 method, not a restored member.
 */
@Mixin(Mob.class)
public abstract class CbWallMobCanAttackMixin extends LivingEntity implements CbWallAttackType.Base {

    protected CbWallMobCanAttackMixin(EntityType<? extends LivingEntity> type, Level level) {
        super(type, level);
    }

    @Override
    public boolean cb$livingCanAttack(LivingEntity target) {
        return super.canAttack(target);
    }

    @Inject(method = "canAttack(Lnet/minecraft/world/entity/LivingEntity;)Z", at = @At("RETURN"), cancellable = true)
    private void cb$attackType(LivingEntity target, CallbackInfoReturnable<Boolean> cir) {
        boolean w = cir.getReturnValueZ();
        boolean answer = CbWallAttackType.canAttack((Mob) (Object) this, target, w);
        if (answer != w) {
            cir.setReturnValue(answer);
        }
    }
}
