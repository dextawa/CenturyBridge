package top.dext.centurybridge.step.v1_21_11.mixin;

import com.mojang.blaze3d.textures.AddressMode;
import com.mojang.blaze3d.textures.FilterMode;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.class_276;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import top.dext.centurybridge.step.v1_21_11.rt.CbSamplers;

/**
 * RenderTarget.createBuffers(width, height) (class_276.method_1231, javap -c both): 1.21.10 set the depth texture to
 * NEAREST without mipmaps and CLAMP_TO_EDGE, the colour texture to CLAMP_TO_EDGE and (setFilterMode(NEAREST, true))
 * NEAREST without mipmaps; 1.21.11 creates the same textures and samples them with a sampler of that state where it
 * binds them (blitAndBlendToTexture: SamplerCache.getClampToEdge(NEAREST)). Replays 1.21.10's writes into the restored
 * GpuTexture state (mixin/CbGpuTextureSamplerRestoredMixin), so a 1.21.10 bindSampler of these views samples as 1.21.10
 * did. Reads and changes nothing 1.21.11 uses; default priority.
 */
@Environment(EnvType.CLIENT)
@Mixin(class_276.class)
public abstract class CbRenderTargetSamplerMixinHooks {

    @Inject(method = "method_1231", at = @At("TAIL"))
    private void cb$v1_21_11$samplerState(int width, int height, CallbackInfo ci) {
        class_276 self = (class_276) (Object) this;
        CbSamplers.vanillaState(self.method_30278(), AddressMode.CLAMP_TO_EDGE, FilterMode.NEAREST);
        CbSamplers.vanillaState(self.method_30277(), AddressMode.CLAMP_TO_EDGE, FilterMode.NEAREST);
    }
}
