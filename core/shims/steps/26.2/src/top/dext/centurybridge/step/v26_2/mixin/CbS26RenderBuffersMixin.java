package top.dext.centurybridge.step.v26_2.mixin;

import net.minecraft.client.renderer.RenderBuffers;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import top.dext.centurybridge.step.v26_2.facade.MultiBufferSource;
import top.dext.centurybridge.step.v26_2.rt.CbPrioritySplitClient;
import top.dext.centurybridge.step.v26_2.rt.CbS26RenderBuffers;
import top.dext.centurybridge.step.v26_2.rt.CbVanillaReads;

/**
 * 26.1.2's RenderBuffers.bufferSource() / crumblingBufferSource() / outlineBufferSource(), restored
 * (rt/CbS26RenderBuffers): built with the render buffers (at the end of 26.2's constructor,
 * CbS26RenderBuffersMixinHooks, default priority); a 26.1.2 call links to them. Restored members at
 * {@code CbVanillaReads.PRIORITY}, below every mod's.
 */
@Mixin(value = RenderBuffers.class, priority = CbVanillaReads.PRIORITY)
public abstract class CbS26RenderBuffersMixin implements CbS26RenderBuffers.Holder, CbPrioritySplitClient.RenderBufferSources {
    @Unique
    private MultiBufferSource.BufferSource cbS26$bufferSource;
    @Unique
    private MultiBufferSource.BufferSource cbS26$crumblingBufferSource;
    @Unique
    private top.dext.centurybridge.step.v26_2.facade.OutlineBufferSource cbS26$outlineBufferSource;

    /** the render buffers were constructed (CbS26RenderBuffersMixinHooks) */
    @Override
    public void cb$v26_2$buildSources() {
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
