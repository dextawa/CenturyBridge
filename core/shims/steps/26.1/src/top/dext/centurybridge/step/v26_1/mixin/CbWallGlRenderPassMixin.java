package top.dext.centurybridge.step.v26_1.mixin;

import com.mojang.blaze3d.opengl.GlRenderPass;
import java.util.function.Supplier;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

/**
 * 1.21.11's protected GlRenderPass.pushedDebugGroups: the render pass counted its open debug groups (push adds one,
 * pop takes one away). 26.1 moved that count to the API RenderPass over the GL pass (rt/CbWallGpu); the GL pass
 * keeps 1.21.11's count again here, for a 1.21.11 subclass reading or adjusting it.
 */
@Environment(EnvType.CLIENT)
@Mixin(GlRenderPass.class)
public abstract class CbWallGlRenderPassMixin {

    protected int pushedDebugGroups;

    @Inject(method = "pushDebugGroup", at = @At("HEAD"))
    private void cb$push(Supplier<String> label, CallbackInfo ci) {
        pushedDebugGroups++;
    }

    @Inject(method = "popDebugGroup", at = @At("HEAD"))
    private void cb$pop(CallbackInfo ci) {
        pushedDebugGroups--;
    }
}
