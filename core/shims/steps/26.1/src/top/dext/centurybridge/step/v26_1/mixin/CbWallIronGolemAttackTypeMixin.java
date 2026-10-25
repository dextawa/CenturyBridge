package top.dext.centurybridge.step.v26_1.mixin;

import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.animal.golem.IronGolem;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;
import top.dext.centurybridge.step.v26_1.rt.CbVanillaReads;

/**
 * 1.21.11's IronGolem.canAttackType (class_1439.method_5973): a player-made golem never targets players, no golem
 * targets creepers, otherwise Mob's answer. 26.1's IronGolem.canAttack(target) makes those two checks itself before
 * Mob.canAttack; they are left to this method instead (Mob.canAttack asks it, CbWallMobAttackTypeMixin), so IronGolem's
 * canAttack is 1.21.11's -- the inherited one -- and a subclass or a mod's injector here decides the types.
 */
@Mixin(IronGolem.class)
public abstract class CbWallIronGolemAttackTypeMixin extends CbWallMobAttackTypeMixin {

    @Shadow
    public abstract boolean isPlayerCreated();

    /** 1.21.11 IronGolem.canAttackType (class_1439.method_5973) */
    @CbVanillaReads("CbWallMobAttackTypeMixin: the inherited Mob.canAttack asks it")
    @Override
    public boolean canAttackType(EntityType<?> type) {
        if (this.isPlayerCreated() && type == EntityType.PLAYER) {
            return false;
        }
        return type != EntityType.CREEPER && super.canAttackType(type);
    }

    @Redirect(method = "canAttack(Lnet/minecraft/world/entity/LivingEntity;)Z", at = @At(value = "INVOKE",
        target = "Lnet/minecraft/world/entity/LivingEntity;is(Ljava/lang/Object;)Z"))
    private boolean cb$typeLeftToCanAttackType(LivingEntity target, Object type) {
        return false; // the PLAYER / CREEPER checks are canAttackType's, asked by Mob.canAttack
    }
}
