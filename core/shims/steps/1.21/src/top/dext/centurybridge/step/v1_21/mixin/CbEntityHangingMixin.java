package top.dext.centurybridge.step.v1_21.mixin;

import net.minecraft.class_1530;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import top.dext.centurybridge.step.v1_21.rt.CbEntityDecorations;
import top.dext.centurybridge.step.v1_21.rt.Guard;

/**
 * HangingEntity.recalculateBoundingBox(), final in 1.21: a 1.20.6 hanging entity that overrides it recalculates
 * with its own (renamed) method; its super call comes back here with the guard held and runs 1.21's body.
 */
@Mixin(class_1530.class)
public abstract class CbEntityHangingMixin {

    @Inject(method = "method_6895()V", at = @At("HEAD"), cancellable = true)
    private void cb$legacyRecalculate(CallbackInfo ci) {
        Object self = this;
        if (self instanceof CbEntityDecorations.LegacyRecalculate legacy && Guard.cbEnter(self)) {
            try {
                legacy.method_6895$cb1_21();
            } finally {
                Guard.cbExit(self);
            }
            ci.cancel();
        }
    }
}
