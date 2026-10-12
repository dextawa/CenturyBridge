package top.dext.centurybridge.step.v1_20_5.mixin;

import org.spongepowered.asm.mixin.Mixin;

/**
 * 1.20.4 LivingEntity's eye height: getEyeHeight(pose, dimensions) was final there -- 0.2 sleeping,
 * otherwise getStandingEyeHeight(pose, dimensions), which mods override -- and getStandingEyeHeight
 * answered Entity's (the dimensions' eye height in 1.20.5, see mixin/CbRestEntityMixin). 1.20.5 keeps
 * sleeping at 0.2 through its sleeping dimensions.
 */
@Mixin(net.minecraft.class_1309.class)
public abstract class CbRestLivingEntityMixin {

    /** 1.20.4 LivingEntity.getEyeHeight(Pose, EntityDimensions) (final) */
    protected final float method_18378(net.minecraft.class_4050 pose, net.minecraft.class_4048 dimensions) {
        return pose == net.minecraft.class_4050.field_18078 ? 0.2F : this.method_18394(pose, dimensions);
    }

    /** 1.20.4 LivingEntity.getStandingEyeHeight(Pose, EntityDimensions): the dimensions' eye height */
    protected float method_18394(net.minecraft.class_4050 pose, net.minecraft.class_4048 dimensions) {
        return dimensions.comp_2187();
    }
}
