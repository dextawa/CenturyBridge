package cb.smx1dn.mixin;

import cb.smx1dn.Probe;
import com.mojang.blaze3d.textures.GpuTextureView;
import net.minecraft.class_10866;
import net.minecraft.class_12137;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

/** native control: 1.21.11 GlRenderPass.bindTexture(name, view, sampler) HEAD; records the sampler the call carried */
@Mixin(class_10866.class)
public class NativeBindTextureMixin {
    @Inject(method = "bindTexture", at = @At("HEAD"))
    private void cbSmx1dn$bind(String name, GpuTextureView view, class_12137 sampler, CallbackInfo ci) {
        if (!"Render thread".equals(Thread.currentThread().getName()) || !name.startsWith("Sampler")) {
            return;
        }
        Probe.bump("bindTexture");
        if (sampler == null) {
            System.getProperties().remove("cbsmx1d.lastSampler");
            System.setProperty("cbsmx1d.lastSamplerState", "null");
        } else {
            System.getProperties().put("cbsmx1d.lastSampler", sampler);
            System.setProperty("cbsmx1d.lastSamplerState", sampler.method_75286() + "/" + sampler.method_75287() + "/"
                + sampler.method_75288() + "/" + sampler.method_75289() + "/" + (sampler.method_76519().isEmpty() ? 1 : 0));
        }
    }
}
