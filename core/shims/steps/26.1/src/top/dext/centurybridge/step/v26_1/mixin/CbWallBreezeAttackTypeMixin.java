package top.dext.centurybridge.step.v26_1.mixin;

import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.monster.breeze.Breeze;
import org.spongepowered.asm.mixin.Mixin;
import top.dext.centurybridge.step.v26_1.rt.CbVanillaReads;

/**
 * 1.21.11's Breeze.canAttackType (class_8949.method_5973): players and iron golems only. 26.1's Breeze.canAttack(target)
 * checks those types itself before Monster's canAttack; where this method answers otherwise than that body (a
 * subclass override, or a mod's injector here), canAttack takes its answer (CbWallGolemBreezeCanAttackMixin,
 * rt/CbWallAttackType).
 */
@Mixin(value = Breeze.class, priority = CbVanillaReads.PRIORITY)
public abstract class CbWallBreezeAttackTypeMixin extends CbWallMobAttackTypeMixin {

    /** 1.21.11 Breeze.canAttackType (class_8949.method_5973) */
    @CbVanillaReads("CbWallGolemBreezeCanAttackMixin: Breeze.canAttack takes its answer where it is mixed")
    @Override
    public boolean canAttackType(EntityType<?> type) {
        return type == EntityType.PLAYER || type == EntityType.IRON_GOLEM;
    }
}
