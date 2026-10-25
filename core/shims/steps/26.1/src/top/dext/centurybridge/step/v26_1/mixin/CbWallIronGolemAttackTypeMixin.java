package top.dext.centurybridge.step.v26_1.mixin;

import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.animal.golem.IronGolem;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import top.dext.centurybridge.step.v26_1.rt.CbVanillaReads;

/**
 * 1.21.11's IronGolem.canAttackType (class_1439.method_5973): a player-made golem never targets players, no golem
 * targets creepers, otherwise Mob's answer. 26.1's IronGolem.canAttack(target) makes those two checks itself before
 * Mob.canAttack; where this method answers otherwise than that body (a subclass override, or a mod's injector here),
 * canAttack takes its answer (CbWallGolemBreezeCanAttackMixin, rt/CbWallAttackType).
 */
@Mixin(value = IronGolem.class, priority = CbVanillaReads.PRIORITY)
public abstract class CbWallIronGolemAttackTypeMixin extends CbWallMobAttackTypeMixin {

    @Shadow
    public abstract boolean isPlayerCreated();

    /** 1.21.11 IronGolem.canAttackType (class_1439.method_5973) */
    @CbVanillaReads("CbWallGolemBreezeCanAttackMixin: IronGolem.canAttack takes its answer where it is mixed")
    @Override
    public boolean canAttackType(EntityType<?> type) {
        if (this.isPlayerCreated() && type == EntityType.PLAYER) {
            return false;
        }
        return type != EntityType.CREEPER && super.canAttackType(type);
    }
}
