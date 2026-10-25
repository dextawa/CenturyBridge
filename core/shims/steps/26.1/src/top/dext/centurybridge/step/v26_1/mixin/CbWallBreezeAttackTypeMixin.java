package top.dext.centurybridge.step.v26_1.mixin;

import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.monster.breeze.Breeze;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;
import top.dext.centurybridge.step.v26_1.rt.CbVanillaReads;

/**
 * 1.21.11's Breeze.canAttackType (class_8949.method_5973): players and iron golems only. 26.1's Breeze.canAttack(target)
 * checks those types itself before Monster's canAttack; that check is left to this method (Mob.canAttack asks it,
 * CbWallMobAttackTypeMixin), so Breeze's canAttack is the inherited one, as on 1.21.11.
 */
@Mixin(Breeze.class)
public abstract class CbWallBreezeAttackTypeMixin extends CbWallMobAttackTypeMixin {

    /** 1.21.11 Breeze.canAttackType (class_8949.method_5973) */
    @CbVanillaReads("CbWallMobAttackTypeMixin: the inherited Mob.canAttack asks it")
    @Override
    public boolean canAttackType(EntityType<?> type) {
        return type == EntityType.PLAYER || type == EntityType.IRON_GOLEM;
    }

    @Redirect(method = "canAttack(Lnet/minecraft/world/entity/LivingEntity;)Z", at = @At(value = "INVOKE",
        target = "Lnet/minecraft/world/entity/LivingEntity;is(Ljava/lang/Object;)Z"))
    private boolean cb$typeLeftToCanAttackType(LivingEntity target, Object type) {
        return true; // the PLAYER / IRON_GOLEM check is canAttackType's, asked by Mob.canAttack
    }
}
