package top.dext.centurybridge.step.v1_20_5.mixin;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import top.dext.centurybridge.step.v1_20_5.rt.CbClientRender;
import top.dext.centurybridge.step.v1_20_5.rt.Guard;

/**
 * RenderTarget.setFilterMode: 1.20.4's public setFilterMode(int) applied the mode every time; 1.20.5 has
 * setFilterMode(int) (method_58226) and a private (int, boolean force) that skips a mode already set. A
 * 1.20.4 subclass that overrides setFilterMode(int) gets the calls vanilla makes (the private form, which
 * createBuffers forces and method_58226 does not); every other target keeps 1.20.5's behaviour, and
 * cb$forceFilterMode is 1.20.4's unconditional body for 1.20.4 callers (rt/CbClientRender).
 */
@Environment(EnvType.CLIENT)
@Mixin(net.minecraft.class_276.class)
public abstract class CbClientRenderTargetMixin implements CbClientRender.RenderTargetApi {

    @Shadow
    private void method_1232(int mode, boolean force) {
    }

    @Override
    public void cb$forceFilterMode(int mode) {
        this.method_1232(mode, true);
    }

    @Inject(method = "method_1232(IZ)V", at = @At("HEAD"), cancellable = true)
    private void cb$legacyFilterMode(int mode, boolean force, CallbackInfo ci) {
        // the subclass's own super.setFilterMode(mode) comes back here through cb$forceFilterMode: the
        // guard lets that call run 1.20.5's body
        if (this instanceof CbClientRender.CbLegacyFilterMode legacy && Guard.cbEnter(this)) {
            ci.cancel();
            try {
                legacy.method_1232(mode);
            } finally {
                Guard.cbExit(this);
            }
        }
    }
}
