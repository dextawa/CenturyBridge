package top.dext.centurybridge.step.v26_2.mixin;

import net.minecraft.client.gui.render.GuiRenderer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import top.dext.centurybridge.step.v26_2.rt.CbS26RenderBuffers;

/**
 * 26.1.2's GuiRenderer.prepare began by ending the batch of the source it was given (the game's RenderBuffers main
 * source, rt/CbS26RenderBuffers) before preparing picture-in-picture, item and text elements; 26.2's prepare does the
 * same preparation (javap of both).
 */
@Mixin(GuiRenderer.class)
public abstract class CbS26GuiFlushMixin {

    @Inject(method = "prepare()V", at = @At("HEAD"))
    private void cbS26$flush(CallbackInfo ci) {
        CbS26RenderBuffers.game().bufferSource().endBatch();
    }
}
