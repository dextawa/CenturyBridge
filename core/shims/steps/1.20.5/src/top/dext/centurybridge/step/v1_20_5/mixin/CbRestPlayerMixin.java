package top.dext.centurybridge.step.v1_20_5.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;
import top.dext.centurybridge.step.v1_20_5.rt.CbRestMobType;

/**
 * 1.20.4 Player declared getStandingEyeHeight(Pose, EntityDimensions) public (its eye height per pose);
 * 1.20.5 keeps those heights in the player's dimensions per pose, which is what the cover answers.
 * <p>
 * Player.attack evaluates the weapon's damage bonus with the attacked entity known
 * (CbRestMobType.cbDamageBonus), so a target whose class declares a 1.20.4 MobType gets its bonus.
 */
@Mixin(net.minecraft.class_1657.class)
public abstract class CbRestPlayerMixin {

    /** 1.20.4 Player.getStandingEyeHeight(Pose, EntityDimensions) (public) */
    public float method_18394(net.minecraft.class_4050 pose, net.minecraft.class_4048 dimensions) {
        return dimensions.comp_2187();
    }

    @Redirect(method = "method_7324", at = @At(value = "INVOKE",
        target = "Lnet/minecraft/class_1890;method_8218(Lnet/minecraft/class_1799;Lnet/minecraft/class_1299;)F"))
    private float cbRest$damageBonus(net.minecraft.class_1799 stack, net.minecraft.class_1299<?> type, net.minecraft.class_1297 target) {
        return CbRestMobType.cbDamageBonus(stack, type, target);
    }
}
