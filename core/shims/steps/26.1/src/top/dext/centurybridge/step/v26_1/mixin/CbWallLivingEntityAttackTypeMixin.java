package top.dext.centurybridge.step.v26_1.mixin;

import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import org.spongepowered.asm.mixin.Mixin;
import top.dext.centurybridge.step.v26_1.rt.CbVanillaReads;
import top.dext.centurybridge.step.v26_1.rt.CbWallAttackType;

/**
 * 1.21.11's LivingEntity.canAttackType(EntityType) (class_1309.method_5973): whether this entity may target an
 * entity of that type -- true here; Mob, IronGolem, Breeze and Phantom overrode it. 1.21.11's TargetingConditions.test
 * asked it after canAttack(target); 26.1 dropped it and moved the overrides' type checks into canAttack(target).
 * <p>
 * Vanilla's path asks it again: TargetingConditions.test (CbWallTargetingAttackTypeMixin) and Mob.canAttack, whose
 * type check is this method (CbWallMobAttackTypeMixin); IronGolem's and Breeze's canAttack leave their type checks
 * to it (CbWallIronGolemAttackTypeMixin, CbWallBreezeAttackTypeMixin).
 */
@Mixin(LivingEntity.class)
public abstract class CbWallLivingEntityAttackTypeMixin implements CbWallAttackType {

    /** 1.21.11 LivingEntity.canAttackType (class_1309.method_5973) */
    @CbVanillaReads("CbWallTargetingAttackTypeMixin: TargetingConditions.test asks it after canAttack(target), as 1.21.11")
    @Override
    public boolean canAttackType(EntityType<?> type) {
        return true;
    }
}
