package top.dext.centurybridge.step.v26_2.mixin;

import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.client.renderer.RenderBuffers;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import top.dext.centurybridge.step.v26_2.rt.CbS26RenderBuffers;

/**
 * Where 26.1.2's GameRenderer ended the main source of RenderBuffers (rt/CbS26RenderBuffers), at the same place in
 * 26.2 (javap of both): renderItemInHand and renderLevel each rendered their features (renderAllFeatures), then
 * ended the batch.
 */
@Mixin(GameRenderer.class)
public abstract class CbS26GameFlushMixin {
    @Shadow
    @Final
    private RenderBuffers renderBuffers;

    @Inject(method = "renderItemInHand", at = @At(value = "INVOKE",
        target = "Lnet/minecraft/client/renderer/feature/FeatureRenderDispatcher;renderAllFeatures(Lnet/minecraft/client/renderer/SubmitNodeStorage;)V",
        shift = At.Shift.AFTER))
    private void cbS26$afterHand(CallbackInfo ci) {
        CbS26RenderBuffers.of(this.renderBuffers).bufferSource().endBatch();
    }

    @Inject(method = "renderLevel", at = @At(value = "INVOKE",
        target = "Lnet/minecraft/client/renderer/feature/FeatureRenderDispatcher;renderAllFeatures(Lnet/minecraft/client/renderer/SubmitNodeStorage;)V",
        shift = At.Shift.AFTER))
    private void cbS26$afterLevel(CallbackInfo ci) {
        CbS26RenderBuffers.of(this.renderBuffers).bufferSource().endBatch();
    }
}
