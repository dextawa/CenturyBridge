package top.dext.centurybridge.step.v26_1.mixin;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.OutlineBufferSource;
import net.minecraft.client.renderer.SubmitNodeCollection;
import net.minecraft.client.renderer.block.BlockStateModelSet;
import net.minecraft.client.renderer.feature.BlockFeatureRenderer;
import net.minecraft.client.renderer.state.OptionsRenderState;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import top.dext.centurybridge.step.v26_1.facade.BlockRenderDispatcher;
import top.dext.centurybridge.step.v26_1.rt.CbWallBlockRender;
import top.dext.centurybridge.step.v26_1.rt.CbWallLegacyCalls;
import top.dext.centurybridge.step.v26_1.rt.CbWallLegacySplit;

/**
 * A 1.21.11 subclass overriding BlockFeatureRenderer.render (class_11681.method_72998, restored by
 * CbWallBlockFeatureRendererSplitMixin): vanilla's renderSolid calls it once per frame, with the game's block
 * renderer, in place of both halves, and its renderTranslucent does nothing for it; a renderer without such an
 * override runs 26.1's halves untouched. Default priority: injectors into 26.1 methods.
 */
@Environment(EnvType.CLIENT)
@Mixin(BlockFeatureRenderer.class)
public abstract class CbWallBlockFeatureRendererSplitReadsMixin {

    @Inject(method = "renderSolid", at = @At("HEAD"), cancellable = true)
    private void cb$legacySolid(SubmitNodeCollection collection, MultiBufferSource.BufferSource bufferSource, BlockStateModelSet models,
                                OutlineBufferSource outline, OptionsRenderState options, CallbackInfo ci) {
        if (CbWallLegacySplit.legacy(this, BlockFeatureRenderer.class, "render", SubmitNodeCollection.class,
                MultiBufferSource.BufferSource.class, BlockRenderDispatcher.class, OutlineBufferSource.class)) {
            ci.cancel();
            ((CbWallLegacyCalls.BlockFeatureRendererLegacy) (Object) this).render(collection, bufferSource,
                    CbWallBlockRender.blockRenderer(Minecraft.getInstance()), outline);
        }
    }

    @Inject(method = "renderTranslucent", at = @At("HEAD"), cancellable = true)
    private void cb$legacyTranslucent(SubmitNodeCollection collection, MultiBufferSource.BufferSource bufferSource, BlockStateModelSet models,
                                      OutlineBufferSource outline, MultiBufferSource.BufferSource crumbling, OptionsRenderState options,
                                      CallbackInfo ci) {
        if (CbWallLegacySplit.legacy(this, BlockFeatureRenderer.class, "render", SubmitNodeCollection.class,
                MultiBufferSource.BufferSource.class, BlockRenderDispatcher.class, OutlineBufferSource.class)) {
            ci.cancel();
        }
    }
}
