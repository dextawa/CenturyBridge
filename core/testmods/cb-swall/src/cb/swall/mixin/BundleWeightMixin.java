package cb.swall.mixin;

import org.apache.commons.lang3.math.Fraction;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

/**
 * 1.21.11's BundleContents.getWeight(stack) (class_9276.method_57423), as 1.21.11-era mods mixed it: stone weighs half a
 * bundle. On 26.1 the selector follows getWeight(ItemInstance) (callReshapes) and the answer converts (returnConverters).
 */
@Mixin(net.minecraft.class_9276.class)
public abstract class BundleWeightMixin {
    @Inject(method = "method_57423(Lnet/minecraft/class_1799;)Lorg/apache/commons/lang3/math/Fraction;", at = @At("RETURN"),
        cancellable = true, remap = false)
    private static void cbswall$heavyStone(net.minecraft.class_1799 stack, CallbackInfoReturnable<Fraction> cir) {
        if (stack.method_7909() == net.minecraft.class_1802.field_20391) {
            cir.setReturnValue(Fraction.getFraction(1, 2));
        }
    }
}
