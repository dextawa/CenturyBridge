package top.dext.centurybridge.step.v26_1.mixin;

import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.monster.Phantom;
import org.spongepowered.asm.mixin.Mixin;
import top.dext.centurybridge.step.v26_1.rt.CbVanillaReads;

/** 1.21.11's Phantom.canAttackType (class_1593.method_5973): every type (it overrode Mob's ghast exception). */
@Mixin(value = Phantom.class, priority = CbVanillaReads.PRIORITY)
public abstract class CbWallPhantomAttackTypeMixin extends CbWallMobAttackTypeMixin {

    /** 1.21.11 Phantom.canAttackType (class_1593.method_5973) */
    @CbVanillaReads("CbWallMobCanAttackMixin / CbWallTargetingAttackTypeMixin: Mob.canAttack and TargetingConditions take its answer where it is mixed")
    @Override
    public boolean canAttackType(EntityType<?> type) {
        return true;
    }
}
