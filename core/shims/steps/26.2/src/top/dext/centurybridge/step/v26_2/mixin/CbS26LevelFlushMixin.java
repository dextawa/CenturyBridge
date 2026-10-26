package top.dext.centurybridge.step.v26_2.mixin;

import net.minecraft.client.renderer.LevelRenderer;
import net.minecraft.client.renderer.RenderBuffers;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import top.dext.centurybridge.step.v26_2.rt.CbS26RenderBuffers;

/**
 * Where 26.1.2's LevelRenderer ended the batches of RenderBuffers' sources (rt/CbS26RenderBuffers), at the same phase
 * of 26.2's passes (javap of both): the main pass after the solid features (26.1.2: renderSolidFeatures, then the
 * main source), after the translucent features (renderTranslucentFeatures, then the main and the crumbling source),
 * after the translucent features drawn over the terrain (renderTranslucentParticles, then the main source); the late
 * debug pass, now the always-on-top pass, ending the main source's last shared batch.
 */
@Mixin(LevelRenderer.class)
public abstract class CbS26LevelFlushMixin {
    @Shadow
    @Final
    private RenderBuffers renderBuffers;

    @Inject(method = "lambda$addMainPass$0", at = @At(value = "INVOKE",
        target = "Lnet/minecraft/client/renderer/feature/FeatureRenderDispatcher$PreparedFrame;executeSolid()V", shift = At.Shift.AFTER))
    private void cbS26$afterSolid(CallbackInfo ci) {
        CbS26RenderBuffers.of(this.renderBuffers).bufferSource().endBatch();
    }

    @Inject(method = "lambda$addMainPass$0", at = @At(value = "INVOKE",
        target = "Lnet/minecraft/client/renderer/feature/FeatureRenderDispatcher$PreparedFrame;executeTranslucent()V", shift = At.Shift.AFTER))
    private void cbS26$afterTranslucent(CallbackInfo ci) {
        CbS26RenderBuffers.Holder holder = CbS26RenderBuffers.of(this.renderBuffers);
        holder.bufferSource().endBatch();
        holder.crumblingBufferSource().endBatch();
        // 26.1.2's main pass ended the outline batch right after the translucent features
        holder.outlineBufferSource().endOutlineBatch();
    }

    @Inject(method = "lambda$addMainPass$0", at = @At(value = "INVOKE",
        target = "Lnet/minecraft/client/renderer/feature/FeatureRenderDispatcher$PreparedFrame;executeTranslucentAfterTerrain()V", shift = At.Shift.AFTER))
    private void cbS26$afterTerrainTranslucent(CallbackInfo ci) {
        CbS26RenderBuffers.of(this.renderBuffers).bufferSource().endBatch();
    }

    @Inject(method = "lambda$addAlwaysOnTopPass$0", at = @At(value = "INVOKE",
        target = "Lnet/minecraft/client/renderer/feature/FeatureRenderDispatcher$PreparedFrame;executeAlwaysOnTop()V", shift = At.Shift.AFTER))
    private void cbS26$afterAlwaysOnTop(CallbackInfo ci) {
        CbS26RenderBuffers.of(this.renderBuffers).bufferSource().endLastBatch();
    }
}
