package top.dext.centurybridge.step.v1_21_2.mixin;

import net.minecraft.class_1542;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import top.dext.centurybridge.step.v1_21_2.rt.CbEntitySmall;

/**
 * 1.21.1's ItemEntity.getVisualRotationYInDegrees() asked the overridable getSpin(0.5): a mod class's own 1.21.1
 * override answers it (1.21.2's body is the same with the static getSpin).
 */
@Mixin(class_1542.class)
public abstract class CbEntitySpinMixin {

    @Inject(method = "method_43078", at = @At("HEAD"), cancellable = true)
    private void cb$visualRotation(CallbackInfoReturnable<Float> cir) {
        if (this instanceof CbEntitySmall.LegacySpin legacy) {
            cir.setReturnValue(180.0F - legacy.method_27314(0.5F) / 6.2831855F * 360.0F);
        }
    }
}
