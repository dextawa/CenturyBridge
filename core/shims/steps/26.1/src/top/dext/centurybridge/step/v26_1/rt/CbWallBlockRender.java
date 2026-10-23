package top.dext.centurybridge.step.v26_1.rt;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.QuadInstance;
import com.mojang.blaze3d.vertex.VertexConsumer;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.CrashReport;
import net.minecraft.CrashReportCategory;
import net.minecraft.ReportedException;
import net.minecraft.client.Minecraft;
import net.minecraft.client.color.block.BlockColors;
import net.minecraft.client.model.geom.builders.UVPair;
import net.minecraft.util.LightCoordsUtil;
import net.minecraft.client.renderer.block.BlockAndTintGetter;
import net.minecraft.client.renderer.block.BlockQuadOutput;
import net.minecraft.client.renderer.block.FluidRenderer;
import net.minecraft.client.renderer.block.ModelBlockRenderer;
import net.minecraft.client.renderer.block.dispatch.BlockStateModel;
import net.minecraft.client.renderer.block.dispatch.BlockStateModelPart;
import net.minecraft.client.resources.model.geometry.BakedQuad;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.util.ARGB;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.BlockAndLightGetter;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.phys.Vec3;
import org.joml.Matrix4f;
import org.joml.Vector3f;
import org.joml.Vector3fc;
import top.dext.centurybridge.step.v26_1.mixin.CbWallModelBlockRendererAccess;

/**
 * 1.21.11's immediate block tesselation (ModelBlockRenderer, LiquidBlockRenderer, VertexConsumer.putBulkData)
 * on 26.1's.
 *
 * 26.1's ModelBlockRenderer writes quads through a BlockQuadOutput (position, quad, per-vertex colour and
 * light in a QuadInstance) and keeps per-call scratch state in the instance; 1.21.11's wrote to a
 * VertexConsumer with the PoseStack's pose and kept nothing but a thread-local brightness cache, so mods
 * shared one instance across the section-building threads. Each 1.21.11 call therefore runs on a 26.1
 * renderer of this thread with the receiver's BlockColors and the call's checkSides as its cull flag, and
 * goes straight to the per-part tesselation 1.21.11 exposed (26.1's private tesselateAmbientOcclusion /
 * tesselateFlat, same per-face culling, lighting and tinting); the output puts each quad into the
 * consumer with the pose and the call's overlay, as 1.21.11's putQuadData did.
 */
@Environment(EnvType.CLIENT)
public final class CbWallBlockRender {

    private CbWallBlockRender() {
    }

    private record Key(BlockColors colors, boolean cull) {
    }

    private static final ThreadLocal<Map<Key, ModelBlockRenderer>> RENDERERS = ThreadLocal.withInitial(HashMap::new);

    private static ModelBlockRenderer renderer(ModelBlockRenderer receiver, boolean checkSides) {
        BlockColors colors = ((CbWallModelBlockRendererAccess) receiver).cb$blockColors();
        // the ambient-occlusion flag only steers 26.1's tesselateBlock(model); the per-part calls below ignore it
        return RENDERERS.get().computeIfAbsent(new Key(colors, checkSides), k -> new ModelBlockRenderer(false, k.cull(), k.colors()));
    }

    private static BlockQuadOutput output(PoseStack poseStack, VertexConsumer consumer, int overlay) {
        return (x, y, z, quad, instance) -> {
            instance.setOverlayCoords(overlay);
            if (x == 0.0F && y == 0.0F && z == 0.0F) {
                consumer.putBakedQuad(poseStack.last(), quad, instance);
            } else {
                poseStack.pushPose();
                poseStack.translate(x, y, z);
                consumer.putBakedQuad(poseStack.last(), quad, instance);
                poseStack.popPose();
            }
        };
    }

    /** 1.21.11 ModelBlockRenderer(BlockColors): ambient occlusion as the options say, culling (1.21.11 decided both per call) */
    public static ModelBlockRenderer newModelBlockRenderer(BlockColors colors) {
        return new ModelBlockRenderer(ambientOcclusion(), true, colors);
    }

    /** 1.21.11 Minecraft.useAmbientOcclusion() */
    public static boolean ambientOcclusion() {
        return Minecraft.getInstance().options.ambientOcclusion().get();
    }

    /**
     * 1.21.11 ModelBlockRenderer.tesselateBlock(level, parts, state, pos, poseStack, consumer, checkSides, overlay):
     * ambient occlusion when the options ask for it, the block emits no light and the first part uses it;
     * the pose moved by the state's offset; a crash report naming the block on failure.
     */
    public static void tesselateBlock(ModelBlockRenderer receiver, BlockAndLightGetter level, List<BlockStateModelPart> parts,
                                      BlockState state, BlockPos pos, PoseStack poseStack, VertexConsumer consumer,
                                      boolean checkSides, int overlay) {
        if (parts.isEmpty()) {
            return;
        }
        boolean ao = ambientOcclusion() && state.getLightEmission() == 0 && parts.getFirst().useAmbientOcclusion();
        Vec3 offset = state.getOffset(pos);
        poseStack.translate(offset);
        try {
            if (ao) {
                tesselateWithAO(receiver, level, parts, state, pos, poseStack, consumer, checkSides, overlay);
            } else {
                tesselateWithoutAO(receiver, level, parts, state, pos, poseStack, consumer, checkSides, overlay);
            }
        } catch (Throwable t) {
            CrashReport report = CrashReport.forThrowable(t, "Tesselating block model");
            CrashReportCategory category = report.addCategory("Block model being tesselated");
            CrashReportCategory.populateBlockDetails(category, level, pos, state);
            category.setDetail("Using AO", ao);
            throw new ReportedException(report);
        }
    }

    /** 1.21.11 ModelBlockRenderer.tesselateWithAO(level, parts, state, pos, poseStack, consumer, checkSides, overlay) */
    public static void tesselateWithAO(ModelBlockRenderer receiver, BlockAndLightGetter level, List<BlockStateModelPart> parts,
                                       BlockState state, BlockPos pos, PoseStack poseStack, VertexConsumer consumer,
                                       boolean checkSides, int overlay) {
        ModelBlockRenderer r = renderer(receiver, checkSides);
        try {
            ((CbWallModelBlockRendererAccess) r).cb$tesselateAmbientOcclusion(output(poseStack, consumer, overlay), 0.0F, 0.0F, 0.0F,
                parts, CbWallTintClient.toTint(level), state, pos);
        } finally {
            ((CbWallModelBlockRendererAccess) r).cb$resetTintCache();
        }
    }

    /** 1.21.11 ModelBlockRenderer.tesselateWithoutAO(level, parts, state, pos, poseStack, consumer, checkSides, overlay) */
    public static void tesselateWithoutAO(ModelBlockRenderer receiver, BlockAndLightGetter level, List<BlockStateModelPart> parts,
                                          BlockState state, BlockPos pos, PoseStack poseStack, VertexConsumer consumer,
                                          boolean checkSides, int overlay) {
        ModelBlockRenderer r = renderer(receiver, checkSides);
        try {
            ((CbWallModelBlockRendererAccess) r).cb$tesselateFlat(output(poseStack, consumer, overlay), 0.0F, 0.0F, 0.0F,
                parts, CbWallTintClient.toTint(level), state, pos);
        } finally {
            ((CbWallModelBlockRendererAccess) r).cb$resetTintCache();
        }
    }

    /**
     * 1.21.11 ModelBlockRenderer.renderModel(pose, consumer, model, r, g, b, light, overlay): the parts the model
     * collects with seed 42, every face and the unculled quads, tinted quads coloured r, g, b (each clamped to
     * 0..1), the others white.
     */
    public static void renderModel(PoseStack.Pose pose, VertexConsumer consumer, BlockStateModel model,
                                   float r, float g, float b, int light, int overlay) {
        for (BlockStateModelPart part : CbWallModels.collectParts(model, RandomSource.create(42L))) {
            for (Direction d : Direction.values()) {
                renderQuadList(pose, consumer, r, g, b, part.getQuads(d), light, overlay);
            }
            renderQuadList(pose, consumer, r, g, b, part.getQuads(null), light, overlay);
        }
    }

    private static void renderQuadList(PoseStack.Pose pose, VertexConsumer consumer, float r, float g, float b,
                                       List<BakedQuad> quads, int light, int overlay) {
        for (BakedQuad quad : quads) {
            float qr;
            float qg;
            float qb;
            if (quad.materialInfo().isTinted()) {
                qr = Mth.clamp(r, 0.0F, 1.0F);
                qg = Mth.clamp(g, 0.0F, 1.0F);
                qb = Mth.clamp(b, 0.0F, 1.0F);
            } else {
                qr = 1.0F;
                qg = 1.0F;
                qb = 1.0F;
            }
            putBulkData(consumer, pose, quad, qr, qg, qb, 1.0F, light, overlay);
        }
    }

    /** 1.21.11 ModelBlockRenderer.enableCaching(): the brightness cache moved to BlockModelLighter */
    public static void enableCaching() {
        net.minecraft.client.renderer.block.BlockModelLighter.enableCaching();
    }

    /** 1.21.11 ModelBlockRenderer.clearCache() */
    public static void clearCache() {
        net.minecraft.client.renderer.block.BlockModelLighter.clearCache();
    }

    // ---- VertexConsumer ----

    private static final float[] FULL_BRIGHTNESS = {1.0F, 1.0F, 1.0F, 1.0F};

    /** 1.21.11 VertexConsumer.putBulkData(pose, quad, r, g, b, a, light, overlay) */
    public static void putBulkData(VertexConsumer consumer, PoseStack.Pose pose, BakedQuad quad,
                                   float r, float g, float b, float a, int light, int overlay) {
        putBulkData(consumer, pose, quad, FULL_BRIGHTNESS, r, g, b, a, new int[]{light, light, light, light}, overlay);
    }

    /**
     * 1.21.11 VertexConsumer.putBulkData(pose, quad, brightness[4], r, g, b, a, lights[4], overlay): each vertex
     * coloured (a, brightness*r, brightness*g, brightness*b), its light raised to the quad's light emission,
     * position and normal through the pose -- the 1.21.11 body on 26.1's addVertex.
     */
    public static void putBulkData(VertexConsumer consumer, PoseStack.Pose pose, BakedQuad quad, float[] brightness,
                                   float r, float g, float b, float a, int[] lights, int overlay) {
        Vector3fc dir = quad.direction().getUnitVec3f();
        Matrix4f matrix = pose.pose();
        Vector3f normal = pose.transformNormal(dir, new Vector3f());
        int emission = quad.materialInfo().lightEmission();
        for (int i = 0; i < 4; i++) {
            Vector3fc position = quad.position(i);
            long uv = quad.packedUV(i);
            float bri = brightness[i];
            int color = ARGB.colorFromFloat(a, bri * r, bri * g, bri * b);
            int lightCoords = LightCoordsUtil.lightCoordsWithEmission(lights[i], emission);
            Vector3f p = matrix.transformPosition(position, new Vector3f());
            consumer.addVertex(p.x(), p.y(), p.z(), color, UVPair.unpackU(uv), UVPair.unpackV(uv), overlay, lightCoords,
                normal.x(), normal.y(), normal.z());
        }
    }

    /** 1.21.11 VertexConsumer.addVertex(Pose, Vector3f) */
    public static VertexConsumer addVertex(VertexConsumer consumer, PoseStack.Pose pose, Vector3f v) {
        return consumer.addVertex(pose, v.x(), v.y(), v.z());
    }

    /** 1.21.11 VertexConsumer.setNormal(Pose, Vector3f) */
    public static VertexConsumer setNormal(VertexConsumer consumer, PoseStack.Pose pose, Vector3f v) {
        return consumer.setNormal(pose, v.x(), v.y(), v.z());
    }

    // ---- LiquidBlockRenderer ----

    /**
     * 1.21.11 LiquidBlockRenderer.tesselate(level, pos, consumer, blockState, fluidState): 26.1's FluidRenderer
     * with every layer written to the one consumer, as 1.21.11 wrote the fluid to the consumer it was given.
     */
    public static void tesselateLiquid(FluidRenderer renderer, BlockAndLightGetter level, BlockPos pos, VertexConsumer consumer,
                                       BlockState blockState, FluidState fluidState) {
        BlockAndTintGetter tint = CbWallTintClient.toTint(level);
        renderer.tesselate(tint, pos, layer -> consumer, blockState, fluidState);
    }

    /**
     * 1.21.11 LiquidBlockRenderer(MaterialSet): 26.1's FluidRenderer reads its sprites from the fluid models the
     * model manager bakes (water still/flow/overlay, lava still/flow -- the five 1.21.11 looked up in the sprites
     * it was given), so the renderer is built over those; the sprite getter itself has no place in 26.1.
     */
    public static FluidRenderer newLiquidBlockRenderer(net.minecraft.client.resources.model.sprite.SpriteGetter sprites) {
        return fluidRenderer();
    }

    /** a 1.21.11 LiquidBlockRenderer subclass's super(MaterialSet): the model manager's fluid models, see newLiquidBlockRenderer */
    public static net.minecraft.client.renderer.block.FluidStateModelSet fluidModels(net.minecraft.client.resources.model.sprite.SpriteGetter sprites) {
        return Minecraft.getInstance().getModelManager().getFluidStateModelSet();
    }

    /** a 1.21.11 ModelBlockRenderer subclass's culling: 1.21.11 culled per call (checkSides); 26.1 vanilla builds with culling on */
    public static boolean culling() {
        return true;
    }

    /** the fluid renderer over the model manager's current fluid models */
    public static FluidRenderer fluidRenderer() {
        return new FluidRenderer(Minecraft.getInstance().getModelManager().getFluidStateModelSet());
    }

    private static top.dext.centurybridge.step.v26_1.facade.BlockRenderDispatcher dispatcher;

    /** 1.21.11 Minecraft.getBlockRenderer(): one dispatcher over the game's live shaper, sprites and colours */
    public static synchronized top.dext.centurybridge.step.v26_1.facade.BlockRenderDispatcher blockRenderer(Minecraft minecraft) {
        if (dispatcher == null) {
            dispatcher = new top.dext.centurybridge.step.v26_1.facade.BlockRenderDispatcher(
                CbWallModels.getBlockModelShaper(minecraft.getModelManager()), minecraft.getAtlasManager(), minecraft.getBlockColors());
        }
        return dispatcher;
    }
}
