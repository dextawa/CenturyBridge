package top.dext.centurybridge.step.v1_21_11.mixin;

import com.mojang.blaze3d.textures.AddressMode;
import com.mojang.blaze3d.textures.FilterMode;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.class_276;
import net.minecraft.class_6364;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import top.dext.centurybridge.step.v1_21_11.rt.CbSamplers;

/**
 * MainTarget.createFrameBuffer(width, height) (class_6364.method_36802, javap -c both): 1.21.10 set the colour and
 * depth textures to NEAREST without mipmaps and CLAMP_TO_EDGE; 1.21.11 creates them the same way and drops the state.
 * Replays 1.21.10's writes into the restored GpuTexture state (rt/CbSamplers, a 1.21.10 bindSampler of these views).
 * Reads and changes nothing 1.21.11 uses; default priority.
 */
@Environment(EnvType.CLIENT)
@Mixin(class_6364.class)
public abstract class CbMainTargetSamplerMixinHooks {

    @Inject(method = "method_36802", at = @At("TAIL"))
    private void cb$v1_21_11$samplerState(int width, int height, CallbackInfo ci) {
        class_276 self = (class_276) (Object) this;
        CbSamplers.vanillaState(self.method_30277(), AddressMode.CLAMP_TO_EDGE, FilterMode.NEAREST);
        CbSamplers.vanillaState(self.method_30278(), AddressMode.CLAMP_TO_EDGE, FilterMode.NEAREST);
    }
}
