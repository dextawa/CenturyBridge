package top.dext.centurybridge.step.v1_20_5.mixin;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import top.dext.centurybridge.step.v1_20_5.rt.CbClientShaders;

/**
 * The injector into 1.20.5's own GameRenderer.reloadShaders that goes with {@link CbClientGameRendererShadersMixin}
 * (its restored 1.20.4 cycleEffect stays there, at {@code CbVanillaReads.PRIORITY}): the two core shaders 1.20.5
 * dropped are loaded with the rest on every shader reload.
 */
@Environment(EnvType.CLIENT)
@Mixin(net.minecraft.class_757.class)
public abstract class CbClientGameRendererShadersMixinHooks {

    @Inject(method = "method_34538", at = @At("TAIL"))
    private void cb$legacyCoreShaders(net.minecraft.class_5912 resources, CallbackInfo ci) {
        CbClientShaders.reload(resources);
    }
}
