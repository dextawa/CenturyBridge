package cb.smx1gn.mixin;

import cb.smx1gn.Probe;
import com.mojang.blaze3d.buffers.GpuBuffer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

/** native 26.2 GlBuffer.Direct.close HEAD (both BufferStorage kinds make Direct, javap): an open -> closed transition */
@Mixin(targets = "com.mojang.blaze3d.opengl.GlBuffer$Direct")
public class NativeBufferCloseMixin {
    @Shadow
    private boolean closed;

    @Inject(method = "close()V", at = @At("HEAD"))
    private void cbSmx1gn$close(CallbackInfo ci) {
        if (!closed) {
            Probe.freeing((GpuBuffer) (Object) this);
        }
    }
}
