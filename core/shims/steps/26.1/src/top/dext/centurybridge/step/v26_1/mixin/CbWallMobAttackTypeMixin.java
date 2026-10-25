package top.dext.centurybridge.step.v26_1.mixin;

import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;
import top.dext.centurybridge.step.v26_1.rt.CbVanillaReads;

/**
 * 1.21.11's Mob.canAttackType (class_1308.method_5973): any type but the ghast. 26.1's Mob.canAttack(target) checks
 * target.is(GHAST) itself; that check is this method (a subclass override, or a mod's injector here, decides it as on
 * 1.21.11, where TargetingConditions asked canAttackType).
 */
@Mixin(Mob.class)
public abstract class CbWallMobAttackTypeMixin extends CbWallLivingEntityAttackTypeMixin {

    /** 1.21.11 Mob.canAttackType (class_1308.method_5973) */
    @CbVanillaReads("CbWallMobAttackTypeMixin: Mob.canAttack's type check is this method")
    @Override
    public boolean canAttackType(EntityType<?> type) {
        return type != EntityType.GHAST;
    }

    @Redirect(method = "canAttack(Lnet/minecraft/world/entity/LivingEntity;)Z", at = @At(value = "INVOKE",
        target = "Lnet/minecraft/world/entity/LivingEntity;is(Ljava/lang/Object;)Z"))
    private boolean cb$notAttackType(LivingEntity target, Object ghast) {
        return !this.canAttackType(target.getType());
    }
}
