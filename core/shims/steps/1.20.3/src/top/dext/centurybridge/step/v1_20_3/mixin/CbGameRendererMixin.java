package top.dext.centurybridge.step.v1_20_3.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import top.dext.centurybridge.step.v1_20_3.rt.CbRender;

/** GameRenderer.reloadShaders: 1.20.2's rendertype_translucent_no_crumbling is loaded with the core shaders */
@Mixin(net.minecraft.class_757.class)
public abstract class CbGameRendererMixin {

    @Inject(method = "method_34538", at = @At("TAIL"))
    private void cb$reloadNoCrumbling(net.minecraft.class_5912 provider, CallbackInfo ci) {
        CbRender.cbReloadShaders(provider);
    }
}
