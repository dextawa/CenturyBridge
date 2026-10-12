package top.dext.centurybridge.step.v1_21.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;
import top.dext.centurybridge.step.v1_21.rt.CbItemUse;

/**
 * EnchantmentHelper.modifyCrossbowChargingTime's per-enchantment step (its lambda): 1.21 evaluates the charge-time effects
 * with the holder's random. A 1.20.6 getUseDuration / getChargeDuration call names no holder (CbItemUse):
 * then the effects get a random of their own instead of failing on the missing entity. 1.21's own callers
 * always pass the holder, which is used as before.
 */
@Mixin(net.minecraft.class_1890.class)
public abstract class CbItemCrossbowChargeMixin {

    @Redirect(method = "method_60130", at = @At(value = "INVOKE", target = "Lnet/minecraft/class_1309;method_59922()Lnet/minecraft/class_5819;"))
    private static net.minecraft.class_5819 cbItem$chargeRandom(net.minecraft.class_1309 holder) {
        return CbItemUse.chargeRandom(holder);
    }
}
