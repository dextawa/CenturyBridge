package top.dext.centurybridge.step.v1_21.mixin;

import net.minecraft.class_1309;
import net.minecraft.class_1665;
import net.minecraft.class_1675;
import net.minecraft.class_1799;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import top.dext.centurybridge.step.v1_21.rt.CbEntityArrows;

/**
 * ProjectileUtil.getMobArrow: 1.20.6 set every mob arrow's enchantment effects from its shooter
 * (setEnchantmentEffectsFromEntity), 1.21 sets its base damage and derives the rest from the weapon. A 1.20.6
 * arrow class that overrides setEnchantmentEffectsFromEntity gets its call again.
 */
@Mixin(class_1675.class)
public abstract class CbEntityMobArrowMixin {

    @Inject(method = "method_18813(Lnet/minecraft/class_1309;Lnet/minecraft/class_1799;FLnet/minecraft/class_1799;)Lnet/minecraft/class_1665;",
            at = @At("RETURN"))
    private static void cb$legacyEnchantmentEffects(class_1309 shooter, class_1799 ammo, float power, class_1799 weapon,
                                                    CallbackInfoReturnable<class_1665> cir) {
        CbEntityArrows.mobArrowMade(cir.getReturnValue(), shooter, power);
    }
}
