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
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import top.dext.centurybridge.step.v26_1.facade.BlockRenderDispatcher;
import top.dext.centurybridge.step.v26_1.rt.CbWallBlockRender;
import top.dext.centurybridge.step.v26_1.rt.CbWallLegacySplit;

/**
 * 1.21.11's BlockFeatureRenderer.render(collection, bufferSource, blockRenderDispatcher, outline)
 * (class_11681.method_72998), which 26.1 split into renderSolid(collection, bufferSource, blockStateModelSet, outline,
 * options) and renderTranslucent(.., crumbling, options). Given back as both halves in turn, with what 26.1's
 * FeatureRenderDispatcher hands them: the model manager's block state model set (where 1.21.11's dispatcher looked
 * the models up), the game's crumbling buffer source and the game render state's options. A subclass overriding it
 * is called once per frame from vanilla's first half, with the game's block renderer (rt/CbWallLegacySplit, as the
 * other split feature renderers).
 *
 * Known difference: the dispatcher argument is not consulted -- the models come from the model manager, which is
 * where the game's own dispatcher (the only one 1.21.11 passed) took them from.
 */
@Environment(EnvType.CLIENT)
@Mixin(BlockFeatureRenderer.class)
public abstract class CbWallBlockFeatureRendererSplitMixin {

    @Shadow
    public abstract void renderSolid(SubmitNodeCollection collection, MultiBufferSource.BufferSource bufferSource,
                                     BlockStateModelSet models, OutlineBufferSource outline, OptionsRenderState options);

    @Shadow
    public abstract void renderTranslucent(SubmitNodeCollection collection, MultiBufferSource.BufferSource bufferSource,
                                           BlockStateModelSet models, OutlineBufferSource outline,
                                           MultiBufferSource.BufferSource crumbling, OptionsRenderState options);

    /** 1.21.11 BlockFeatureRenderer.render (class_11681.method_72998) */
    public void render(SubmitNodeCollection collection, MultiBufferSource.BufferSource bufferSource, BlockRenderDispatcher dispatcher,
                       OutlineBufferSource outline) {
        Minecraft minecraft = Minecraft.getInstance();
        BlockStateModelSet models = minecraft.getModelManager().getBlockStateModelSet();
        OptionsRenderState options = minecraft.gameRenderer.getGameRenderState().optionsRenderState;
        CbWallLegacySplit.enter(this);
        try {
            renderSolid(collection, bufferSource, models, outline, options);
            renderTranslucent(collection, bufferSource, models, outline, minecraft.renderBuffers().crumblingBufferSource(), options);
        } finally {
            CbWallLegacySplit.exit(this);
        }
    }

    @Inject(method = "renderSolid", at = @At("HEAD"), cancellable = true)
    private void cb$legacySolid(SubmitNodeCollection collection, MultiBufferSource.BufferSource bufferSource, BlockStateModelSet models,
                                OutlineBufferSource outline, OptionsRenderState options, CallbackInfo ci) {
        if (CbWallLegacySplit.legacy(this, BlockFeatureRenderer.class, "render", SubmitNodeCollection.class,
                MultiBufferSource.BufferSource.class, BlockRenderDispatcher.class, OutlineBufferSource.class)) {
            ci.cancel();
            render(collection, bufferSource, CbWallBlockRender.blockRenderer(Minecraft.getInstance()), outline);
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
