package top.dext.centurybridge.step.v1_21.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import top.dext.centurybridge.step.v1_21.rt.CbRenderShaders;

/**
 * GameRenderer's core shaders as 1.20.6 had them.
 * <ul>
 * <li>getRendertypeArmorGlintShader() / getRendertypeGlintDirectShader(): 1.21 still declares them but no longer
 *     loads their programs, so they answer null -- 1.20.6 answered its rendertype_armor_glint and
 *     rendertype_glint_direct programs, which were 1.20.6's rendertype_glint byte for byte (1.21 dropped a JSON
 *     blend block from every glint program alike); they answer 1.21's glint program.</li>
 * <li>position_color_tex (CbRenderShaders) is made after the core shaders load and closed when they shut down.</li>
 * </ul>
 */
@Mixin(net.minecraft.class_757.class)
public abstract class CbRenderGameRendererMixin {

    @Inject(method = "method_34520", at = @At("HEAD"), cancellable = true)
    private static void cb$armorGlintShader(CallbackInfoReturnable<net.minecraft.class_5944> cir) {
        cir.setReturnValue(net.minecraft.class_757.method_34525());
    }

    @Inject(method = "method_34526", at = @At("HEAD"), cancellable = true)
    private static void cb$glintDirectShader(CallbackInfoReturnable<net.minecraft.class_5944> cir) {
        cir.setReturnValue(net.minecraft.class_757.method_34525());
    }

    @Inject(method = "method_34538", at = @At("RETURN"))
    private void cb$shadersLoaded(net.minecraft.class_5912 provider, CallbackInfo ci) {
        CbRenderShaders.cbShadersLoaded(provider);
    }

    @Inject(method = "method_34537", at = @At("HEAD"))
    private void cb$shadersShutDown(CallbackInfo ci) {
        CbRenderShaders.cbShadersClosed();
    }
}
