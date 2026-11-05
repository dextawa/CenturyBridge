package top.dext.centurybridge.step.v26_2.mixin;

import com.mojang.blaze3d.systems.RenderPass;
import com.mojang.blaze3d.systems.RenderPassBackend;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import top.dext.centurybridge.step.v26_2.rt.CbS26GlUniformClose;

/**
 * 26.2 closes a render pass in RenderPass.close() (isClosed, then onFinish; javap -c -p 26.2), and GlRenderPass -- now
 * the pass's backend -- has no close() of its own (26.1.2's had). At RETURN the pass's backend goes to the 1.21.6 layer's
 * close hook (rt/CbS26GlUniformClose), which frees the uniform buffers its legacy view allocated for a GlRenderPass and
 * ignores anything else. Observes the return only; default priority. A second close() answers early, and the hook is
 * idempotent.
 */
@Environment(EnvType.CLIENT)
@Mixin(RenderPass.class)
public abstract class CbS26RenderPassCloseMixin {
    @Shadow
    @Final
    private RenderPassBackend backend;

    @Inject(method = "close()V", at = @At("RETURN"))
    private void cbS26$closeLegacyUniforms(CallbackInfo ci) {
        CbS26GlUniformClose.closed(this.backend);
    }
}
