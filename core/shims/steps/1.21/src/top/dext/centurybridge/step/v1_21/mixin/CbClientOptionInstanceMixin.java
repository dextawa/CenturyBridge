package top.dext.centurybridge.step.v1_21.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import top.dext.centurybridge.step.v1_21.rt.CbClientGui;

/**
 * The 1.20.6 view of the menu background blurriness option (CbClientGui.cbBlurrinessView): its value is always
 * 1.21's option in tenths, and setting it sets 1.21's.
 */
@Mixin(net.minecraft.class_7172.class)
public abstract class CbClientOptionInstanceMixin {

    @Inject(method = "method_41753", at = @At("HEAD"), cancellable = true)
    private void cb$viewGet(CallbackInfoReturnable<Object> cir) {
        net.minecraft.class_7172<Integer> steps = CbClientGui.cbViewSteps(this);
        if (steps != null) {
            cir.setReturnValue(CbClientGui.cbBlurriness(steps.method_41753()));
        }
    }

    @Inject(method = "method_41748", at = @At("HEAD"), cancellable = true)
    private void cb$viewSet(Object value, CallbackInfo ci) {
        net.minecraft.class_7172<Integer> steps = CbClientGui.cbViewSteps(this);
        if (steps != null) {
            steps.method_41748(CbClientGui.cbBlurrinessSteps(((Number) value).doubleValue()));
            ci.cancel();
        }
    }
}
