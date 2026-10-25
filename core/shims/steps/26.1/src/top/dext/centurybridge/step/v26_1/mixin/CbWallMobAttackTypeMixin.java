package top.dext.centurybridge.step.v26_1.mixin;

import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.Mob;
import org.spongepowered.asm.mixin.Mixin;
import top.dext.centurybridge.step.v26_1.rt.CbVanillaReads;

/**
 * 1.21.11's Mob.canAttackType (class_1308.method_5973): any type but the ghast. 26.1's Mob.canAttack(target) checks
 * target.is(GHAST) itself; where this method answers otherwise than that body (a subclass override, or a mod's
 * injector here), canAttack takes its answer (CbWallMobCanAttackMixin, rt/CbWallAttackType).
 */
@Mixin(value = Mob.class, priority = CbVanillaReads.PRIORITY)
public abstract class CbWallMobAttackTypeMixin extends CbWallLivingEntityAttackTypeMixin {

    /** 1.21.11 Mob.canAttackType (class_1308.method_5973) */
    @CbVanillaReads("CbWallMobCanAttackMixin: Mob.canAttack takes its answer where it is mixed")
    @Override
    public boolean canAttackType(EntityType<?> type) {
        return type != EntityType.GHAST;
    }
}
