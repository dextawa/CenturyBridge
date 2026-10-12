package top.dext.centurybridge.step.v1_20_5.mixin;

import org.spongepowered.asm.mixin.Mixin;

/**
 * 1.20.4 Player declared getStandingEyeHeight(Pose, EntityDimensions) public (its eye height per pose);
 * 1.20.5 keeps those heights in the player's dimensions per pose, which is what the cover answers.
 */
@Mixin(net.minecraft.class_1657.class)
public abstract class CbRestPlayerMixin {

    /** 1.20.4 Player.getStandingEyeHeight(Pose, EntityDimensions) (public) */
    public float method_18394(net.minecraft.class_4050 pose, net.minecraft.class_4048 dimensions) {
        return dimensions.comp_2187();
    }
}
