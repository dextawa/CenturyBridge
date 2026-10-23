package top.dext.centurybridge.step.v1_21.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import top.dext.centurybridge.step.v1_21.rt.CbClientGui;

/**
 * AbstractReportScreen for a 1.20.6 subclass (CbClientGui.cbLegacyUnder): 1.20.6's AbstractReportScreen had no
 * init or repositionElements of its own, so the subclass's super calls (and the methods it inherits) are Screen's
 * -- 1.21's build a header/content/footer layout and only arrange it.
 */
@Mixin(net.minecraft.class_8768.class)
public abstract class CbClientReportScreenMixin extends net.minecraft.class_437 {

    protected CbClientReportScreenMixin(net.minecraft.class_2561 title) {
        super(title);
    }

    @Inject(method = "method_25426", at = @At("HEAD"), cancellable = true)
    private void cb$legacyInit(CallbackInfo ci) {
        if (CbClientGui.cbLegacyUnder(this, net.minecraft.class_8768.class)) {
            super.method_25426();
            ci.cancel();
        }
    }

    @Inject(method = "method_48640", at = @At("HEAD"), cancellable = true)
    private void cb$legacyReposition(CallbackInfo ci) {
        if (CbClientGui.cbLegacyUnder(this, net.minecraft.class_8768.class)) {
            super.method_48640();
            ci.cancel();
        }
    }
}
