package top.dext.centurybridge.step.v1_21_2.mixin;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import top.dext.centurybridge.step.v1_21_2.rt.CbFapiShaders;

/** after the ShaderManager applied a reload: 1.21.1 Fabric API's CoreShaderRegistrationCallback (CbFapiShaders) */
@Environment(EnvType.CLIENT)
@Mixin(net.minecraft.class_10151.class)
public abstract class CbFapiShaderReloadMixin {

    @Inject(method = "method_62945", at = @At("RETURN"))
    private void cb$legacyCoreShaders(net.minecraft.class_10151.class_10153 prepared, net.minecraft.class_3300 resourceManager, net.minecraft.class_3695 profiler, CallbackInfo ci) {
        CbFapiShaders.reloaded();
    }
}
