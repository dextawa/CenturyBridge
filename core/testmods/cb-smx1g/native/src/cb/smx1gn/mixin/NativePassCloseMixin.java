package cb.smx1gn.mixin;

import cb.smx1gn.Probe;
import com.mojang.blaze3d.systems.RenderPass;
import com.mojang.blaze3d.systems.RenderPassBackend;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

/** native 26.2 RenderPass.close HEAD: the native-W control (passes close) and the backend whose frees follow */
@Mixin(RenderPass.class)
public class NativePassCloseMixin {
    @Shadow
    @Final
    private RenderPassBackend backend;
    @Shadow
    private boolean isClosed;

    @Inject(method = "close()V", at = @At("HEAD"))
    private void cbSmx1gn$close(CallbackInfo ci) {
        if (!isClosed) {
            Probe.passCloses++;
            Probe.currentBackend = backend;
        }
    }
}
