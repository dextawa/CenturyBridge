package top.dext.centurybridge.step.v26_1.rt;

import com.mojang.blaze3d.vertex.PoseStack;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.client.renderer.v1.Renderer;
import net.fabricmc.fabric.api.client.renderer.v1.mesh.MutableQuadView;
import net.fabricmc.fabric.api.client.renderer.v1.mesh.QuadEmitter;
import net.fabricmc.fabric.api.client.renderer.v1.model.FabricBlockStateModel;
import net.fabricmc.fabric.api.client.renderer.v1.render.AltModelBlockRenderer;
import net.fabricmc.fabric.api.client.renderer.v1.render.FabricLayerRenderState;
import net.minecraft.CrashReport;
import net.minecraft.CrashReportCategory;
import net.minecraft.ReportedException;
import net.minecraft.client.color.block.BlockColors;
import net.minecraft.client.color.block.BlockTintSource;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.block.ModelBlockRenderer;
import net.minecraft.client.renderer.block.dispatch.BlockStateModel;
import net.minecraft.client.renderer.item.ItemStackRenderState;
import net.minecraft.core.BlockPos;
import net.minecraft.util.ARGB;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.BlockAndLightGetter;
import net.minecraft.world.level.block.RenderShape;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec3;
import top.dext.centurybridge.step.v26_1.compat.fapi.renderer.v1.render.BlockVertexConsumerProvider;
import top.dext.centurybridge.step.v26_1.compat.fapi.renderer.v1.render.FabricBlockModelRenderer;
import top.dext.centurybridge.step.v26_1.compat.fapi.renderer.v1.render.FabricBlockRenderManager;
import top.dext.centurybridge.step.v26_1.compat.fapi.renderer.v1.render.RenderLayerHelper;
import top.dext.centurybridge.step.v26_1.facade.BlockRenderDispatcher;
import top.dext.centurybridge.step.v26_1.mixin.CbWallModelBlockRendererAccess;

import java.lang.invoke.MethodHandle;
import java.lang.invoke.MethodType;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Consumer;

/**
 * 1.21.11's Fabric renderer entry points (Renderer.render / renderBlockAsEntity / getLayerRenderStateEmitter,
 * FabricBlockModelRenderer, FabricBlockRenderManager; Indigo's TerrainLikeRenderContext and SimpleBlockRenderContext)
 * on 26.1's renderer API, which tesselates a block through an AltModelBlockRenderer into a QuadEmitter whose quads
 * write themselves to a VertexConsumer (QuadView.buffer):
 * <ul>
 * <li>terrain-like (Renderer.render(ModelBlockRenderer, ...), FabricBlockModelRenderer.render): 26.1's alt renderer with
 * ambient occlusion as the options say, the call's culling and the receiver's block colours; each quad into the
 * provider's consumer for its chunk layer with the pose and the call's overlay. 26.1's alt renderer adds the
 * state's offset to the quads; 1.21.11 translated the pose stack by it (and left it translated), which is done
 * after the quads are written so the stack ends as 1.21.11 left it;</li>
 * <li>simple (Renderer.render(Pose, ...), the static FabricBlockModelRenderer.render): the model's emitQuads with
 * seed 42 and no culling, tinted quads multiplied by the call's colour (each clamped to 0..1), emissive quads at
 * full light, the others at the brighter of their own and the call's light (Indigo's ColorHelper.maxLight);</li>
 * <li>renderBlockAsEntity: a visible block's model coloured with its tint 0 asked without a world, simple-rendered
 * into the entity sheets (RenderLayerHelper.entityDelegate).</li>
 * </ul>
 * A quad's chunk layer is 26.1's (QuadView.chunkLayer, decided where the quad is made); 1.21.11 asked
 * ItemBlockRenderTypes for quads that named none, which 26.1 no longer has.
 */
@Environment(EnvType.CLIENT)
public final class CbFapiRender {
    private CbFapiRender() {
    }

    /** the quads' destination of the current call on this thread */
    private static final class Sink implements Consumer<MutableQuadView> {
        PoseStack.Pose pose;
        BlockVertexConsumerProvider consumers;
        int overlay;
        boolean simple;
        float red;
        float green;
        float blue;
        int light;

        @Override
        public void accept(MutableQuadView quad) {
            if (simple) {
                if (quad.tintIndex() != -1) {
                    for (int i = 0; i < 4; i++) {
                        quad.color(i, ARGB.scaleRGB(quad.color(i), red, green, blue));
                    }
                }
                for (int i = 0; i < 4; i++) {
                    quad.lightmap(i, quad.emissive() ? 15728880 : maxLight(quad.lightmap(i), light));
                }
            }
            quad.buffer(overlay, pose, consumers.getBuffer(quad.chunkLayer()));
        }
    }

    private record AltKey(boolean ao, boolean cull, BlockColors colors) {
    }

    private static final class Context {
        final Sink sink = new Sink();
        final QuadEmitter emitter = Renderer.get().quadEmitter(sink);
        final RandomSource random = RandomSource.create();
        final Map<AltKey, AltModelBlockRenderer> alt = new HashMap<>();
    }

    private static final ThreadLocal<Context> CONTEXT = ThreadLocal.withInitial(Context::new);

    /** 1.21.11 Indigo ColorHelper.maxLight: a zero light yields to the other, otherwise each half's maximum */
    private static int maxLight(int a, int b) {
        if (a == 0) {
            return b;
        }
        if (b == 0) {
            return a;
        }
        return Math.max(a & 0xFFFF, b & 0xFFFF) | Math.max(a & 0xFFFF0000, b & 0xFFFF0000);
    }

    // ---- terrain-like ----

    /** 1.21.11 Renderer.render(ModelBlockRenderer, ...) (Indigo's TerrainLikeRenderContext.bufferModel) */
    public static void renderBlockImpl(ModelBlockRenderer modelRenderer, BlockAndLightGetter blockView, BlockStateModel model, BlockState state,
                                       BlockPos pos, PoseStack matrices, BlockVertexConsumerProvider vertexConsumers, boolean cull, long seed,
                                       int overlay) {
        Context c = CONTEXT.get();
        BlockColors colors = ((CbWallModelBlockRendererAccess) modelRenderer).cb$blockColors();
        AltModelBlockRenderer alt = c.alt.computeIfAbsent(new AltKey(CbWallBlockRender.ambientOcclusion(), cull, colors),
                k -> Renderer.get().altModelBlockRenderer(k.ao(), k.cull(), k.colors()));
        Sink sink = c.sink;
        sink.pose = matrices.last();
        sink.consumers = vertexConsumers;
        sink.overlay = overlay;
        sink.simple = false;
        try {
            alt.tesselateBlock(c.emitter, 0.0F, 0.0F, 0.0F, CbWallTintClient.toTint(blockView), pos, state, model, seed);
        } catch (Throwable t) {
            CrashReport report = CrashReport.forThrowable(t, "Tessellating block model - Indigo Renderer");
            CrashReportCategory category = report.addCategory("Block model being tessellated");
            CrashReportCategory.populateBlockDetails(category, blockView, pos, state);
            throw new ReportedException(report);
        } finally {
            sink.pose = null;
            sink.consumers = null;
        }
        Vec3 offset = state.getOffset(pos);
        matrices.translate(offset.x, offset.y, offset.z);
    }

    // ---- simple ----

    /** 1.21.11 Renderer.render(Pose, ...) (Indigo's SimpleBlockRenderContext.bufferModel) */
    public static void renderModel(PoseStack.Pose matrices, BlockVertexConsumerProvider vertexConsumers, BlockStateModel model, float red,
                                   float green, float blue, int light, int overlay, BlockAndLightGetter blockView, BlockPos pos,
                                   BlockState state) {
        Context c = CONTEXT.get();
        Sink sink = c.sink;
        sink.pose = matrices;
        sink.consumers = vertexConsumers;
        sink.overlay = overlay;
        sink.simple = true;
        sink.red = Mth.clamp(red, 0.0F, 1.0F);
        sink.green = Mth.clamp(green, 0.0F, 1.0F);
        sink.blue = Mth.clamp(blue, 0.0F, 1.0F);
        sink.light = light;
        c.random.setSeed(42L);
        try {
            ((FabricBlockStateModel) (Object) model).emitQuads(c.emitter, CbWallTintClient.toTint(blockView), pos, state, c.random, d -> false);
        } finally {
            sink.pose = null;
            sink.consumers = null;
            sink.simple = false;
        }
    }

    /** 1.21.11 Renderer.renderBlockAsEntity (Indigo's body) */
    public static void renderBlockAsEntityImpl(BlockRenderDispatcher dispatcher, BlockState state, PoseStack matrices,
                                               MultiBufferSource vertexConsumers, int light, int overlay, BlockAndLightGetter blockView,
                                               BlockPos pos) {
        if (state.getRenderShape() == RenderShape.INVISIBLE) {
            return;
        }
        BlockStateModel model = dispatcher.method_3349(state);
        BlockTintSource tint = dispatcher.cb$blockColors().getTintSource(state, 0);
        int color = tint != null ? tint.color(state) : -1;
        float r = (color >> 16 & 255) / 255.0F;
        float g = (color >> 8 & 255) / 255.0F;
        float b = (color & 255) / 255.0F;
        renderModel(matrices.last(), RenderLayerHelper.entityDelegate(vertexConsumers), model, r, g, b, light, overlay, blockView, pos, state);
    }

    // ---- 1.21.11 call sites ----

    private static final MethodType BLOCK_RENDER = MethodType.methodType(void.class, FabricBlockModelRenderer.class, BlockAndLightGetter.class,
            BlockStateModel.class, BlockState.class, BlockPos.class, PoseStack.class, BlockVertexConsumerProvider.class, boolean.class,
            long.class, int.class);
    private static final ClassValue<MethodHandle> BLOCK_RENDER_OVERRIDE = new ClassValue<>() {
        @Override
        protected MethodHandle computeValue(Class<?> type) {
            return CbFapiOverrides.find(type, "render", BLOCK_RENDER);
        }
    };

    /**
     * a 1.21.11 call of FabricBlockModelRenderer.render(view, model, ...) -- on a ModelBlockRenderer, which 26.1's
     * does not implement: a mod subclass's own override answers as 1.21.11's virtual call did, otherwise 1.21.11's
     * default body; any other receiver is a 1.21.11 implementation of the interface and answers itself
     */
    public static void render(FabricBlockModelRenderer receiver, BlockAndLightGetter blockView, BlockStateModel model, BlockState state,
                              BlockPos pos, PoseStack matrices, BlockVertexConsumerProvider vertexConsumers, boolean cull, long seed,
                              int overlay) {
        Object self = receiver;
        if (!(self instanceof ModelBlockRenderer modelRenderer)) {
            receiver.render(blockView, model, state, pos, matrices, vertexConsumers, cull, seed, overlay);
            return;
        }
        MethodHandle override = BLOCK_RENDER_OVERRIDE.get(self.getClass());
        if (override != null) {
            try {
                override.invokeExact(receiver, blockView, model, state, pos, matrices, vertexConsumers, cull, seed, overlay);
            } catch (Throwable t) {
                throw CbFapiOverrides.rethrow(t);
            }
            return;
        }
        renderBlockImpl(modelRenderer, blockView, model, state, pos, matrices, vertexConsumers, cull, seed, overlay);
    }

    /** FabricBlockModelRenderer.render's default body, which a super call reaches */
    public static void renderBase(FabricBlockModelRenderer receiver, BlockAndLightGetter blockView, BlockStateModel model, BlockState state,
                                  BlockPos pos, PoseStack matrices, BlockVertexConsumerProvider vertexConsumers, boolean cull, long seed,
                                  int overlay) {
        renderBlockImpl((ModelBlockRenderer) (Object) receiver, blockView, model, state, pos, matrices, vertexConsumers, cull, seed, overlay);
    }

    /** a 1.21.11 call of Renderer.render(ModelBlockRenderer, ...) on 26.1's renderer */
    public static void render(Renderer renderer, ModelBlockRenderer modelRenderer, BlockAndLightGetter blockView, BlockStateModel model,
                              BlockState state, BlockPos pos, PoseStack matrices, BlockVertexConsumerProvider vertexConsumers, boolean cull,
                              long seed, int overlay) {
        renderBlockImpl(modelRenderer, blockView, model, state, pos, matrices, vertexConsumers, cull, seed, overlay);
    }

    /** a 1.21.11 call of Renderer.render(Pose, ...) on 26.1's renderer */
    public static void render(Renderer renderer, PoseStack.Pose matrices, BlockVertexConsumerProvider vertexConsumers, BlockStateModel model,
                              float red, float green, float blue, int light, int overlay, BlockAndLightGetter blockView, BlockPos pos,
                              BlockState state) {
        renderModel(matrices, vertexConsumers, model, red, green, blue, light, overlay, blockView, pos, state);
    }

    /** a 1.21.11 call of Renderer.renderBlockAsEntity on 26.1's renderer: the dispatcher's own (overridable) member */
    public static void renderBlockAsEntity(Renderer renderer, BlockRenderDispatcher dispatcher, BlockState state, PoseStack matrices,
                                           MultiBufferSource vertexConsumers, int light, int overlay, BlockAndLightGetter blockView,
                                           BlockPos pos) {
        renderBlockAsEntityImpl(dispatcher, state, matrices, vertexConsumers, light, overlay, blockView, pos);
    }

    /** a 1.21.11 call of Renderer.getLayerRenderStateEmitter: 26.1's FabricLayerRenderState.emitter() of the layer */
    public static QuadEmitter getLayerRenderStateEmitter(Renderer renderer, ItemStackRenderState.LayerRenderState layer) {
        return ((FabricLayerRenderState) (Object) layer).emitter();
    }

    /** FabricBlockRenderManager.renderBlockAsEntity's default body, which a super call reaches */
    public static void renderBlockAsEntityBase(FabricBlockRenderManager receiver, BlockState state, PoseStack matrices,
                                               MultiBufferSource vertexConsumers, int light, int overlay, BlockAndLightGetter blockView,
                                               BlockPos pos) {
        renderBlockAsEntityImpl((BlockRenderDispatcher) receiver, state, matrices, vertexConsumers, light, overlay, blockView, pos);
    }
}
