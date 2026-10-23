package top.dext.centurybridge.step.v1_21.mixin;

import net.minecraft.class_1532;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import top.dext.centurybridge.step.v1_21.rt.CbEntityDecorations;
import top.dext.centurybridge.step.v1_21.rt.Guard;

/**
 * LeashFenceKnotEntity.recalculateBoundingBox(): in 1.20.6 the knot was a hanging entity, so a 1.20.6 subclass's
 * override is renamed like any hanging entity's (overrideRenames, under HangingEntity); 1.21's knot keeps its own
 * method, and this hands the call to the renamed override. Its super call comes back with the guard held.
 */
@Mixin(class_1532.class)
public abstract class CbEntityKnotMixin {

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
