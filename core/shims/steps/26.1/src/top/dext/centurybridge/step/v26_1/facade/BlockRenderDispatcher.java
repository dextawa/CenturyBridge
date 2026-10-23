package top.dext.centurybridge.step.v26_1.facade;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import java.util.List;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.CrashReport;
import net.minecraft.CrashReportCategory;
import net.minecraft.ReportedException;
import net.minecraft.client.color.block.BlockColors;
import net.minecraft.client.color.block.BlockTintSource;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.block.ModelBlockRenderer;
import net.minecraft.client.renderer.block.dispatch.BlockStateModel;
import net.minecraft.client.renderer.block.dispatch.BlockStateModelPart;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.client.resources.model.geometry.BakedQuad;
import net.minecraft.client.resources.model.sprite.SpriteGetter;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.server.packs.resources.ResourceManagerReloadListener;
import net.minecraft.util.ARGB;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.BlockAndLightGetter;
import net.minecraft.world.level.block.RenderShape;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.FluidState;
import top.dext.centurybridge.step.v26_1.rt.CbWallBlockRender;

/**
 * 1.21.11's BlockRenderDispatcher (net/minecraft/class_776), gone in 26.1: block models come from
 * ModelManager's BlockStateModelSet (its shaper is {@link BlockModelShaper}), blocks tesselate through
 * ModelBlockRenderer into a BlockQuadOutput, fluids through FluidRenderer.
 *
 * Each method keeps 1.21.11's body on those pieces (CbWallBlockRender gives back the 1.21.11 shapes of
 * ModelBlockRenderer and LiquidBlockRenderer it called):
 * <ul>
 * <li>renderBreakingTexture / renderBatched: the model's parts tesselated with the block's seed into the
 *     consumer, with crash reports as 1.21.11 wrote them;</li>
 * <li>renderLiquid: FluidRenderer over the model manager's current fluid models (1.21.11 rebuilt its liquid
 *     renderer from its sprites on every resource reload; 26.1's fluid models are rebuilt by the model
 *     manager, so the reload callback has nothing left to rebuild);</li>
 * <li>renderSingleBlock: renderModel coloured with the block's tint 0 (no level, no position), each quad into
 *     the buffer of its own item render type -- 26.1 has no per-block render type (1.21.11's
 *     ItemBlockRenderTypes is gone); a quad's item render type is what 26.1 itself draws a block with outside
 *     the world.</li>
 * </ul>
 * Member names are 1.21.11's intermediary names; onResourceManagerReload is 26.1's interface method.
 */
@Environment(EnvType.CLIENT)
public class BlockRenderDispatcher implements ResourceManagerReloadListener {

    private final BlockModelShaper cb$blockModelShaper;
    private final SpriteGetter cb$sprites;
    private final BlockColors cb$blockColors;
    private final ModelBlockRenderer cb$modelRenderer;

    /** 1.21.11 BlockRenderDispatcher(BlockModelShaper, MaterialSet, BlockColors) */
    public BlockRenderDispatcher(BlockModelShaper blockModelShaper, SpriteGetter sprites, BlockColors blockColors) {
        this.cb$blockModelShaper = blockModelShaper;
        this.cb$sprites = sprites;
        this.cb$blockColors = blockColors;
        this.cb$modelRenderer = CbWallBlockRender.newModelBlockRenderer(blockColors);
    }

    /** getBlockModelShaper() */
    public BlockModelShaper method_3351() {
        return this.cb$blockModelShaper;
    }

    /** renderBreakingTexture(state, pos, level, poseStack, consumer) */
    public void method_23071(BlockState state, BlockPos pos, BlockAndLightGetter level, PoseStack poseStack, VertexConsumer consumer) {
        if (state.getRenderShape() != RenderShape.MODEL) {
            return;
        }
        BlockStateModel model = this.cb$blockModelShaper.method_3335(state);
        RandomSource random = RandomSource.create();
        random.setSeed(state.getSeed(pos));
        List<BlockStateModelPart> parts = new ObjectArrayList<>();
        model.collectParts(random, parts);
        CbWallBlockRender.tesselateBlock(this.cb$modelRenderer, level, parts, state, pos, poseStack, consumer, true,
            OverlayTexture.NO_OVERLAY);
    }

    /** renderBatched(state, pos, level, poseStack, consumer, checkSides, parts) */
    public void method_3355(BlockState state, BlockPos pos, BlockAndLightGetter level, PoseStack poseStack, VertexConsumer consumer,
                            boolean checkSides, List<BlockStateModelPart> parts) {
        try {
            CbWallBlockRender.tesselateBlock(this.cb$modelRenderer, level, parts, state, pos, poseStack, consumer, checkSides,
                OverlayTexture.NO_OVERLAY);
        } catch (Throwable t) {
            CrashReport report = CrashReport.forThrowable(t, "Tesselating block in world");
            CrashReportCategory category = report.addCategory("Block being tesselated");
            CrashReportCategory.populateBlockDetails(category, level, pos, state);
            throw new ReportedException(report);
        }
    }

    /** renderLiquid(pos, level, consumer, blockState, fluidState) */
    public void method_3352(BlockPos pos, BlockAndLightGetter level, VertexConsumer consumer, BlockState blockState, FluidState fluidState) {
        try {
            CbWallBlockRender.tesselateLiquid(CbWallBlockRender.fluidRenderer(), level, pos, consumer, blockState, fluidState);
        } catch (Throwable t) {
            CrashReport report = CrashReport.forThrowable(t, "Tesselating liquid in world");
            CrashReportCategory category = report.addCategory("Block being tesselated");
            CrashReportCategory.populateBlockDetails(category, level, pos, null);
            throw new ReportedException(report);
        }
    }

    /** getModelRenderer() */
    public ModelBlockRenderer method_3350() {
        return this.cb$modelRenderer;
    }

    /** getBlockModel(state) */
    public BlockStateModel method_3349(BlockState state) {
        return this.cb$blockModelShaper.method_3335(state);
    }

    /** renderSingleBlock(state, poseStack, bufferSource, light, overlay) */
    public void method_3353(BlockState state, PoseStack poseStack, MultiBufferSource bufferSource, int light, int overlay) {
        if (state.getRenderShape() == RenderShape.INVISIBLE) {
            return;
        }
        BlockStateModel model = this.method_3349(state);
        BlockTintSource tint = this.cb$blockColors.getTintSource(state, 0);
        int color = tint != null ? tint.color(state) : -1;
        float r = ARGB.red(color) / 255.0F;
        float g = ARGB.green(color) / 255.0F;
        float b = ARGB.blue(color) / 255.0F;
        PoseStack.Pose pose = poseStack.last();
        for (BlockStateModelPart part : top.dext.centurybridge.step.v26_1.rt.CbWallModels.collectParts(model, RandomSource.create(42L))) {
            for (Direction d : Direction.values()) {
                single(pose, bufferSource, r, g, b, part.getQuads(d), light, overlay);
            }
            single(pose, bufferSource, r, g, b, part.getQuads(null), light, overlay);
        }
    }

    private static void single(PoseStack.Pose pose, MultiBufferSource bufferSource, float r, float g, float b,
                               List<BakedQuad> quads, int light, int overlay) {
        for (BakedQuad quad : quads) {
            boolean tinted = quad.materialInfo().isTinted();
            CbWallBlockRender.putBulkData(bufferSource.getBuffer(quad.materialInfo().itemRenderType()), pose, quad,
                tinted ? Math.clamp(r, 0.0F, 1.0F) : 1.0F, tinted ? Math.clamp(g, 0.0F, 1.0F) : 1.0F,
                tinted ? Math.clamp(b, 0.0F, 1.0F) : 1.0F, 1.0F, light, overlay);
        }
    }

    /** onResourceManagerReload(ResourceManager), 1.21.11 name */
    public void method_14491(ResourceManager resourceManager) {
        this.onResourceManagerReload(resourceManager);
    }

    /**
     * 1.21.11 rebuilt its LiquidBlockRenderer from its sprites here; renderLiquid reads 26.1's fluid models,
     * which the model manager rebuilds on the same reload, so there is no state of this class to rebuild.
     */
    @Override
    public void onResourceManagerReload(ResourceManager resourceManager) {
    }

    /** the sprites the dispatcher was built with (1.21.11 field_61777, read by nothing but its liquid renderer) */
    public SpriteGetter cb$sprites() {
        return this.cb$sprites;
    }
}
