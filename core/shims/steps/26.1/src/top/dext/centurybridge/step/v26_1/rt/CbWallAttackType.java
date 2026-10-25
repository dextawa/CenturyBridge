package top.dext.centurybridge.step.v26_1.rt;

import net.minecraft.world.entity.EntityType;

/**
 * 1.21.11's LivingEntity.canAttackType(EntityType) (class_1309.method_5973), restored on LivingEntity and the classes
 * that overrode it (mixin/CbWall*AttackTypeMixin), as an interface LivingEntity implements so the layer's own mixins
 * can ask it.
 */
public interface CbWallAttackType {

    boolean canAttackType(EntityType<?> type);
}
