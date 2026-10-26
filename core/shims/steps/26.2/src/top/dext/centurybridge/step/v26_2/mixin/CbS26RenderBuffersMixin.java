package top.dext.centurybridge.step.v26_2.mixin;

import net.minecraft.client.renderer.RenderBuffers;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import top.dext.centurybridge.step.v26_2.facade.MultiBufferSource;
import top.dext.centurybridge.step.v26_2.rt.CbS26RenderBuffers;

/**
 * 26.1.2's RenderBuffers.bufferSource() / crumblingBufferSource(), restored (rt/CbS26RenderBuffers): built with the
 * render buffers; a 26.1.2 call links to them. Priority 1, below every mod's.
 */
@Mixin(value = RenderBuffers.class, priority = 1)
public abstract class CbS26RenderBuffersMixin implements CbS26RenderBuffers.Holder {
    @Unique
    private MultiBufferSource.BufferSource cbS26$bufferSource;
    @Unique
    private MultiBufferSource.BufferSource cbS26$crumblingBufferSource;
    @Unique
    private top.dext.centurybridge.step.v26_2.facade.OutlineBufferSource cbS26$outlineBufferSource;

    @Inject(method = "<init>(I)V", at = @At("TAIL"))
    private void cbS26$sources(int threads, CallbackInfo ci) {
        this.cbS26$bufferSource = CbS26RenderBuffers.newBufferSource();
        this.cbS26$crumblingBufferSource = CbS26RenderBuffers.newCrumblingBufferSource();
        this.cbS26$outlineBufferSource = new top.dext.centurybridge.step.v26_2.facade.OutlineBufferSource();
    }

    @Override
    public MultiBufferSource.BufferSource bufferSource() {
        return this.cbS26$bufferSource;
    }

    @Override
    public MultiBufferSource.BufferSource crumblingBufferSource() {
        return this.cbS26$crumblingBufferSource;
    }

    @Override
    public top.dext.centurybridge.step.v26_2.facade.OutlineBufferSource outlineBufferSource() {
        return this.cbS26$outlineBufferSource;
    }
}
