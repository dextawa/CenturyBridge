package top.dext.centurybridge.step.v1_21.mixin;

import net.minecraft.class_1282;
import net.minecraft.class_1297;
import net.minecraft.class_1890;
import net.minecraft.class_3218;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import top.dext.centurybridge.step.v1_21.rt.CbEntityEnchanting;

/**
 * EnchantmentHelper.doPostAttackEffects: 1.21 calls it where 1.20.6 called the hitting entity's
 * doEnchantDamageEffects(attacker, target); a 1.20.6 class that overrides that answers instead
 * (CbEntityEnchanting.legacyPostAttack). Every other call is 1.21's.
 */
@Mixin(class_1890.class)
public abstract class CbEntityPostAttackMixin {

    @Inject(method = "method_60107(Lnet/minecraft/class_3218;Lnet/minecraft/class_1297;Lnet/minecraft/class_1282;)V",
            at = @At("HEAD"), cancellable = true)
    private static void cb$legacyDamageEffects(class_3218 level, class_1297 target, class_1282 source, CallbackInfo ci) {
        if (CbEntityEnchanting.legacyPostAttack(level, target, source)) {
            ci.cancel();
        }
    }
}
