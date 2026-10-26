package top.dext.centurybridge.step.v26_1.rt;

import com.mojang.blaze3d.vertex.PoseStack;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.client.renderer.v1.Renderer;
import net.fabricmc.fabric.api.client.renderer.v1.mesh.MeshView;
import net.fabricmc.fabric.api.client.renderer.v1.mesh.Mesh;
import net.fabricmc.fabric.api.client.renderer.v1.mesh.MutableMesh;
import net.fabricmc.fabric.api.client.renderer.v1.mesh.QuadEmitter;
import net.fabricmc.fabric.api.client.renderer.v1.mesh.QuadView;
import net.fabricmc.fabric.api.client.renderer.v1.model.FabricBlockStateModel;
import net.fabricmc.fabric.api.client.renderer.v1.render.FabricOrderedSubmitNodeCollector;
import net.minecraft.client.Minecraft;
import net.minecraft.client.color.block.BlockTintSource;
import net.minecraft.client.gui.Font;
import net.minecraft.client.model.Model;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.renderer.OrderedSubmitNodeCollector;
import net.minecraft.client.renderer.SubmitNodeCollection;
import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.client.renderer.SubmitNodeStorage;
import net.minecraft.client.renderer.block.BlockAndTintGetter;
import net.minecraft.client.renderer.block.BlockModelRenderState;
import net.minecraft.client.renderer.block.MovingBlockRenderState;
import net.minecraft.client.renderer.block.dispatch.BlockStateModel;
import net.minecraft.client.renderer.block.dispatch.BlockStateModelPart;
import net.minecraft.client.renderer.block.model.BlockDisplayContext;
import net.minecraft.client.renderer.chunk.ChunkSectionLayer;
import net.minecraft.client.renderer.entity.state.EntityRenderState;
import net.minecraft.client.renderer.feature.ModelFeatureRenderer;
import net.minecraft.client.renderer.item.ItemStackRenderState;
import net.minecraft.client.renderer.rendertype.RenderType;
import net.minecraft.client.renderer.special.SpecialModelRenderer;
import net.minecraft.client.renderer.state.level.CameraRenderState;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.util.FormattedCharSequence;
import net.minecraft.util.RandomSource;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.level.BlockAndLightGetter;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.RenderShape;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec3;
import org.joml.Matrix4fc;
import org.joml.Quaternionf;
import top.dext.centurybridge.step.v26_1.compat.fapi.renderer.v1.render.RenderLayerHelper;
import top.dext.centurybridge.step.v26_1.mixin.CbFapiBlockModelRenderStateAccess;
import top.dext.centurybridge.step.v26_1.mixin.CbFapiSubmitNodeCollectionAccess;

import java.lang.invoke.MethodHandle;
import java.lang.invoke.MethodType;
import java.util.EnumMap;
import java.util.List;
import java.util.Map;
import java.util.function.Function;

/**
 * 1.21.11's block submissions on 26.1's submit node collectors.
 * <ul>
 * <li>OrderedSubmitNodeCollector.submitBlock(pose, state, light, overlay, outline): 1.21.11 queued a BlockSubmit in the
 *     collection (drawn as the block's model) and submitted the block's special renderer from its block table (chest,
 *     banner, ...) to the collection's storage. Here the BlockSubmit is listed (getBlockSubmits), 26.1's block model
 *     without its special renderer goes to the receiver, and 1.21.11's block table renderer to the storage
 *     (rt/CbWallSpecialBlocks).</li>
 * <li>OrderedSubmitNodeCollector.submitBlockModel(pose, renderType, model, r, g, b, light, overlay, outline): 1.21.11
 *     rendered the model's quads (seed 42; through Fabric API's renderer: emitQuads with an empty world at the origin as
 *     air) into the render type, tinted quads in r, g, b. Here the quads are emitted into a mesh, submitted through
 *     Fabric API's mesh submission with every tint index coloured r, g, b.</li>
 * <li>Fabric API's FabricRenderCommandQueue.submitBlock / submitBlockStateModel (with a world, position and state):
 *     1.21.11 rendered these as Fabric API's renderBlockAsEntity / FabricBlockModelRenderer.render (rt/CbFapiRender):
 *     emitQuads in that world, each quad into the render type of its chunk layer (the entity block layer, or the
 *     caller's function), tinted quads in the colour given (renderBlockAsEntity: the block's tint 0 without a world).
 *     Here they are one mesh submission per chunk layer; submitBlock also submits the block's special renderer to the
 *     storage, as 1.21.11's collection did.</li>
 * </ul>
 * A receiver that is not one of 26.1's collectors is a mod's own implementation of the 1.21.11 method: the call
 * reaches its override, as 1.21.11's virtual call did.
 */
@Environment(EnvType.CLIENT)
public final class CbFapiSubmitBlocks {
    private CbFapiSubmitBlocks() {
    }

    private static final long SEED = 42L;

    // ---- OrderedSubmitNodeCollector.submitBlock ----

    private static final MethodType SUBMIT_BLOCK = MethodType.methodType(void.class, OrderedSubmitNodeCollector.class, PoseStack.class,
            BlockState.class, int.class, int.class, int.class);
    private static final ClassValue<MethodHandle> SUBMIT_BLOCK_OVERRIDE = overrides("submitBlock", SUBMIT_BLOCK);

    /** a 1.21.11 call of OrderedSubmitNodeCollector.submitBlock(pose, state, light, overlay, outline) */
    public static void submitBlock(OrderedSubmitNodeCollector collector, PoseStack pose, BlockState state, int light, int overlay,
                                   int outline) {
        MethodHandle override = vanilla(collector) ? null : SUBMIT_BLOCK_OVERRIDE.get(collector.getClass());
        if (override == null) {
            submitBlockBase(collector, pose, state, light, overlay, outline);
            return;
        }
        try {
            override.invokeExact(collector, pose, state, light, overlay, outline);
        } catch (Throwable t) {
            throw CbFapiOverrides.rethrow(t);
        }
    }

    /**
     * 1.21.11's SubmitNodeCollection / SubmitNodeStorage body of submitBlock (a super call reaches it; javap
     * class_11788.method_73481): a BlockSubmit queued in the collection (SubmitNodeStorage.submitBlock: order(0)'s),
     * which 1.21.11's BlockFeatureRenderer drew as the block's model, then the block's special renderer from
     * 1.21.11's block table submitted to the collection's storage (ModelManager.specialBlockModelRenderer()
     * .renderByBlock(block, NONE, ...)). The queued submit is listed by getBlockSubmits() (rt/CbWallBlockSubmits); its
     * model is submitted here, 26.1's block model without its special renderer.
     */
    public static void submitBlockBase(OrderedSubmitNodeCollector collector, PoseStack pose, BlockState state, int light, int overlay,
                                       int outline) {
        CbWallBlockSubmits.queued(collector, pose, state, light, overlay, outline);
        submitBlockModelOf(modelTarget(collector), pose, state, light, overlay, outline);
        CbWallSpecialBlocks.superSpecialBlockModelRenderer(Minecraft.getInstance().getModelManager()).method_65535(state.getBlock(),
            ItemDisplayContext.NONE, pose, storage(collector), light, overlay, outline);
    }

    /** what 1.21.11's BlockFeatureRenderer drew for a BlockSubmit: the block's model (26.1's, without its special renderer) */
    public static void submitBlockModelOf(SubmitNodeCollector target, PoseStack pose, BlockState state, int light, int overlay, int outline) {
        BlockModelRenderState renderState = new BlockModelRenderState();
        Minecraft.getInstance().getModelManager().getBlockModelSet().get(state).update(renderState, state, BlockDisplayContext.create(), SEED);
        CbFapiBlockModelRenderStateAccess access = (CbFapiBlockModelRenderStateAccess) (Object) renderState;
        access.cb$setSpecialRenderer(null);
        renderState.submit(pose, target, light, overlay, outline);
    }

    // ---- OrderedSubmitNodeCollector.submitBlockModel ----

    private static final MethodType SUBMIT_MODEL = MethodType.methodType(void.class, OrderedSubmitNodeCollector.class, PoseStack.class,
            RenderType.class, BlockStateModel.class, float.class, float.class, float.class, int.class, int.class, int.class);
    private static final ClassValue<MethodHandle> SUBMIT_MODEL_OVERRIDE = overrides("submitBlockModel", SUBMIT_MODEL);

    /** a 1.21.11 call of OrderedSubmitNodeCollector.submitBlockModel(pose, renderType, model, r, g, b, light, overlay, outline) */
    public static void submitBlockModel(OrderedSubmitNodeCollector collector, PoseStack pose, RenderType renderType, BlockStateModel model,
                                        float red, float green, float blue, int light, int overlay, int outline) {
        MethodHandle override = vanilla(collector) ? null : SUBMIT_MODEL_OVERRIDE.get(collector.getClass());
        if (override == null) {
            submitBlockModelBase(collector, pose, renderType, model, red, green, blue, light, overlay, outline);
            return;
        }
        try {
            override.invokeExact(collector, pose, renderType, model, red, green, blue, light, overlay, outline);
        } catch (Throwable t) {
            throw CbFapiOverrides.rethrow(t);
        }
    }

    /** 1.21.11's SubmitNodeCollection / SubmitNodeStorage body of submitBlockModel (a super call reaches it) */
    public static void submitBlockModelBase(OrderedSubmitNodeCollector collector, PoseStack pose, RenderType renderType, BlockStateModel model,
                                            float red, float green, float blue, int light, int overlay, int outline) {
        MutableMesh mesh = emit(model, BlockAndTintGetter.EMPTY, BlockPos.ZERO, Blocks.AIR.defaultBlockState());
        if (mesh.size() == 0) {
            return;
        }
        int[] tints = tints(mesh, red, green, blue);
        ((FabricOrderedSubmitNodeCollector) (Object) collector).submitBlockModel(pose, renderType, List.of(), mesh.immutableCopy(), tints,
                light, overlay, outline);
    }

    // ---- super calls from mod subclasses of 26.1's collectors ----

    /** super.submitBlock(..) in a mod subclass of SubmitNodeStorage */
    public static void submitBlockBaseStorage(SubmitNodeStorage collector, PoseStack pose, BlockState state, int light, int overlay, int outline) {
        submitBlockBase(collector, pose, state, light, overlay, outline);
    }

    /** super.submitBlock(..) in a mod subclass of SubmitNodeCollection */
    public static void submitBlockBaseCollection(SubmitNodeCollection collector, PoseStack pose, BlockState state, int light, int overlay,
                                                 int outline) {
        submitBlockBase(collector, pose, state, light, overlay, outline);
    }

    /** super.submitBlockModel(..) in a mod subclass of SubmitNodeStorage */
    public static void submitBlockModelBaseStorage(SubmitNodeStorage collector, PoseStack pose, RenderType renderType, BlockStateModel model,
                                                   float red, float green, float blue, int light, int overlay, int outline) {
        submitBlockModelBase(collector, pose, renderType, model, red, green, blue, light, overlay, outline);
    }

    /** super.submitBlockModel(..) in a mod subclass of SubmitNodeCollection */
    public static void submitBlockModelBaseCollection(SubmitNodeCollection collector, PoseStack pose, RenderType renderType,
                                                      BlockStateModel model, float red, float green, float blue, int light, int overlay,
                                                      int outline) {
        submitBlockModelBase(collector, pose, renderType, model, red, green, blue, light, overlay, outline);
    }

    // ---- Fabric API FabricRenderCommandQueue ----

    private static final MethodType Q_SUBMIT_BLOCK = MethodType.methodType(void.class, FabricOrderedSubmitNodeCollector.class, PoseStack.class,
            BlockState.class, int.class, int.class, int.class, BlockAndLightGetter.class, BlockPos.class);
    private static final ClassValue<MethodHandle> Q_SUBMIT_BLOCK_OVERRIDE = overrides("submitBlock", Q_SUBMIT_BLOCK);

    /** a 1.21.11 call of FabricRenderCommandQueue.submitBlock(pose, state, light, overlay, outline, blockView, pos) */
    public static void submitBlock(FabricOrderedSubmitNodeCollector queue, PoseStack pose, BlockState state, int light, int overlay,
                                   int outline, BlockAndLightGetter blockView, BlockPos pos) {
        MethodHandle override = vanilla(queue) ? null : Q_SUBMIT_BLOCK_OVERRIDE.get(queue.getClass());
        if (override == null) {
            submitBlockInWorld(queue, pose, state, light, overlay, outline, blockView, pos);
            return;
        }
        try {
            override.invokeExact(queue, pose, state, light, overlay, outline, blockView, pos);
        } catch (Throwable t) {
            throw CbFapiOverrides.rethrow(t);
        }
    }

    /**
     * FabricRenderCommandQueue.submitBlock's body for a super call: 1.21.11's default (the vanilla submitBlock of the
     * receiver) for a mod's own implementation, Fabric API's collector body for 26.1's collectors
     */
    public static void submitBlockBase(FabricOrderedSubmitNodeCollector queue, PoseStack pose, BlockState state, int light, int overlay,
                                       int outline, BlockAndLightGetter blockView, BlockPos pos) {
        if (vanilla(queue)) {
            submitBlockInWorld(queue, pose, state, light, overlay, outline, blockView, pos);
        } else {
            submitBlock((OrderedSubmitNodeCollector) (Object) queue, pose, state, light, overlay, outline);
        }
    }

    /** 1.21.11 Fabric API SubmitNodeCollectionMixin.submitBlock with the feature renderer's renderBlockAsEntity of it */
    private static void submitBlockInWorld(FabricOrderedSubmitNodeCollector queue, PoseStack pose, BlockState state, int light, int overlay,
                                           int outline, BlockAndLightGetter blockView, BlockPos pos) {
        OrderedSubmitNodeCollector collector = (OrderedSubmitNodeCollector) (Object) queue;
        if (state.getRenderShape() != RenderShape.INVISIBLE) {
            BlockStateModel model = Minecraft.getInstance().getModelManager().getBlockStateModelSet().get(state);
            BlockTintSource tint = Minecraft.getInstance().getBlockColors().getTintSource(state, 0);
            int color = tint != null ? tint.color(state) : -1;
            submitByLayer(queue, pose, RenderLayerHelper::getEntityBlockLayer, emit(model, CbWallTintClient.toTint(blockView), pos, state),
                    (color >> 16 & 255) / 255.0F, (color >> 8 & 255) / 255.0F, (color & 255) / 255.0F, light, overlay, outline);
        }
        BlockModelRenderState renderState = new BlockModelRenderState();
        Minecraft.getInstance().getModelManager().getBlockModelSet().get(state).update(renderState, state, BlockDisplayContext.create(), SEED);
        CbFapiBlockModelRenderStateAccess access = (CbFapiBlockModelRenderStateAccess) (Object) renderState;
        SpecialModelRenderer<?> special = access.cb$specialRenderer();
        if (special != null) {
            submitSpecial(special, access.cb$specialRendererTransformation(), pose, storage(collector), light, overlay, outline);
        }
    }

    private static final MethodType Q_SUBMIT_MODEL = MethodType.methodType(void.class, FabricOrderedSubmitNodeCollector.class, PoseStack.class,
            Function.class, BlockStateModel.class, float.class, float.class, float.class, int.class, int.class, int.class,
            BlockAndLightGetter.class, BlockPos.class, BlockState.class);
    private static final ClassValue<MethodHandle> Q_SUBMIT_MODEL_OVERRIDE = overrides("submitBlockStateModel", Q_SUBMIT_MODEL);

    /** a 1.21.11 call of FabricRenderCommandQueue.submitBlockStateModel */
    public static void submitBlockStateModel(FabricOrderedSubmitNodeCollector queue, PoseStack pose,
                                             Function<ChunkSectionLayer, RenderType> renderTypes, BlockStateModel model, float red,
                                             float green, float blue, int light, int overlay, int outline, BlockAndLightGetter blockView,
                                             BlockPos pos, BlockState state) {
        MethodHandle override = vanilla(queue) ? null : Q_SUBMIT_MODEL_OVERRIDE.get(queue.getClass());
        if (override == null) {
            submitByLayer(queue, pose, renderTypes, emit(model, CbWallTintClient.toTint(blockView), pos, state), red, green, blue, light,
                    overlay, outline);
            return;
        }
        try {
            override.invokeExact(queue, pose, renderTypes, model, red, green, blue, light, overlay, outline, blockView, pos, state);
        } catch (Throwable t) {
            throw CbFapiOverrides.rethrow(t);
        }
    }

    /**
     * FabricRenderCommandQueue.submitBlockStateModel's body for a super call: 1.21.11's default (the vanilla
     * submitBlockModel of the receiver with the render type of the state's chunk layer) for a mod's own implementation,
     * Fabric API's collector body for 26.1's collectors
     */
    public static void submitBlockStateModelBase(FabricOrderedSubmitNodeCollector queue, PoseStack pose,
                                                 Function<ChunkSectionLayer, RenderType> renderTypes, BlockStateModel model, float red,
                                                 float green, float blue, int light, int overlay, int outline,
                                                 BlockAndLightGetter blockView, BlockPos pos, BlockState state) {
        if (vanilla(queue)) {
            submitByLayer(queue, pose, renderTypes, emit(model, CbWallTintClient.toTint(blockView), pos, state), red, green, blue, light,
                    overlay, outline);
        } else {
            submitBlockModel((OrderedSubmitNodeCollector) (Object) queue, pose, renderTypes.apply(CbFapiBlockLayers.getChunkRenderType(state)),
                    model, red, green, blue, light, overlay, outline);
        }
    }

    // ---- helpers ----

    private static ClassValue<MethodHandle> overrides(String name, MethodType type) {
        return new ClassValue<>() {
            @Override
            protected MethodHandle computeValue(Class<?> receiver) {
                return CbFapiOverrides.find(receiver, name, type);
            }
        };
    }

    /** one of 26.1's own collectors (whose 1.21.11 bodies are given back here) */
    private static boolean vanilla(Object collector) {
        return collector instanceof SubmitNodeCollection || collector instanceof SubmitNodeStorage || collector instanceof OrderedView;
    }

    /** the storage 1.21.11 submitted special renderers to: the collection's, or the receiver itself */
    private static SubmitNodeCollector storage(OrderedSubmitNodeCollector collector) {
        if (collector instanceof SubmitNodeCollection collection) {
            return ((CbFapiSubmitNodeCollectionAccess) (Object) collection).cb$submitNodeStorage();
        }
        if (collector instanceof OrderedView view) {
            return view.storage;
        }
        return (SubmitNodeCollector) collector;
    }

    /** where a block render state's model goes: the receiver itself, seen as a SubmitNodeCollector */
    static SubmitNodeCollector modelTarget(OrderedSubmitNodeCollector collector) {
        if (collector instanceof SubmitNodeCollector full) {
            return full;
        }
        return new OrderedView(collector, storage(collector));
    }

    /** BlockModelRenderState.submit's special-renderer half */
    private static void submitSpecial(SpecialModelRenderer<?> special, Matrix4fc transformation, PoseStack pose, SubmitNodeCollector storage,
                                      int light, int overlay, int outline) {
        if (transformation != null) {
            pose.pushPose();
            pose.mulPose(transformation);
            CbFapiBlockModelRenderStateAccess.cb$submitSpecialRenderer(special, pose, storage, light, overlay, outline);
            pose.popPose();
        } else {
            CbFapiBlockModelRenderStateAccess.cb$submitSpecialRenderer(special, pose, storage, light, overlay, outline);
        }
    }

    /** the model's quads as 1.21.11's renderer emitted them (seed 42, no cull) */
    private static MutableMesh emit(BlockStateModel model, BlockAndTintGetter level, BlockPos pos, BlockState state) {
        MutableMesh mesh = Renderer.get().mutableMesh();
        ((FabricBlockStateModel) (Object) model).emitQuads(mesh.emitter(), level, pos, state, RandomSource.create(SEED), d -> false);
        return mesh;
    }

    /** every tint index of the mesh coloured r, g, b (1.21.11 tinted each tinted quad in the colour given) */
    private static int[] tints(MeshView mesh, float red, float green, float blue) {
        int[] max = {-1};
        mesh.forEach(q -> max[0] = Math.max(max[0], q.tintIndex()));
        int[] tints = new int[max[0] + 1];
        int argb = 0xFF000000 | channel(red) << 16 | channel(green) << 8 | channel(blue);
        java.util.Arrays.fill(tints, argb);
        return tints;
    }

    private static int channel(float value) {
        return (int) (Math.max(0.0F, Math.min(1.0F, value)) * 255.0F);
    }

    /** one mesh submission per chunk layer, in the render type the function gives the layer */
    private static void submitByLayer(FabricOrderedSubmitNodeCollector queue, PoseStack pose, Function<ChunkSectionLayer, RenderType> renderTypes,
                                      MutableMesh mesh, float red, float green, float blue, int light, int overlay, int outline) {
        if (mesh.size() == 0) {
            return;
        }
        int[] tints = tints(mesh, red, green, blue);
        Map<ChunkSectionLayer, MutableMesh> byLayer = new EnumMap<>(ChunkSectionLayer.class);
        mesh.forEach(quad -> {
            QuadEmitter emitter = byLayer.computeIfAbsent(quad.chunkLayer(), l -> Renderer.get().mutableMesh()).emitter();
            emitter.copyFrom(quad);
            emitter.emit();
        });
        for (Map.Entry<ChunkSectionLayer, MutableMesh> entry : byLayer.entrySet()) {
            Mesh layerMesh = entry.getValue().immutableCopy();
            queue.submitBlockModel(pose, renderTypes.apply(entry.getKey()), List.of(), layerMesh, tints, light, overlay, outline);
        }
    }

    /** an ordered collection seen as a SubmitNodeCollector: submissions go to it, other orders to its storage */
    private static final class OrderedView implements SubmitNodeCollector, FabricOrderedSubmitNodeCollector {
        private final OrderedSubmitNodeCollector target;
        final SubmitNodeCollector storage;

        OrderedView(OrderedSubmitNodeCollector target, SubmitNodeCollector storage) {
            this.target = target;
            this.storage = storage;
        }

        @Override
        public OrderedSubmitNodeCollector order(int order) {
            return this.storage.order(order);
        }

        @Override
        public void submitShadow(PoseStack pose, float radius, List<EntityRenderState.ShadowPiece> pieces) {
            this.target.submitShadow(pose, radius, pieces);
        }

        @Override
        public void submitNameTag(PoseStack pose, Vec3 offset, int y, Component name, boolean seeThrough, int light, double distance,
                                  CameraRenderState camera) {
            this.target.submitNameTag(pose, offset, y, name, seeThrough, light, distance, camera);
        }

        @Override
        public void submitText(PoseStack pose, float x, float y, FormattedCharSequence text, boolean shadow, Font.DisplayMode mode, int light,
                               int color, int background, int outline) {
            this.target.submitText(pose, x, y, text, shadow, mode, light, color, background, outline);
        }

        @Override
        public void submitFlame(PoseStack pose, EntityRenderState state, Quaternionf rotation) {
            this.target.submitFlame(pose, state, rotation);
        }

        @Override
        public void submitLeash(PoseStack pose, EntityRenderState.LeashState leash) {
            this.target.submitLeash(pose, leash);
        }

        @Override
        public <S> void submitModel(Model<? super S> model, S state, PoseStack pose, RenderType renderType, int light, int overlay, int color,
                                    TextureAtlasSprite sprite, int outline, ModelFeatureRenderer.CrumblingOverlay crumbling) {
            this.target.submitModel(model, state, pose, renderType, light, overlay, color, sprite, outline, crumbling);
        }

        @Override
        public void submitModelPart(ModelPart part, PoseStack pose, RenderType renderType, int light, int overlay, TextureAtlasSprite sprite,
                                    boolean sheeted, boolean hasFoil, int color, ModelFeatureRenderer.CrumblingOverlay crumbling, int outline) {
            this.target.submitModelPart(part, pose, renderType, light, overlay, sprite, sheeted, hasFoil, color, crumbling, outline);
        }

        @Override
        public void submitMovingBlock(PoseStack pose, MovingBlockRenderState state) {
            this.target.submitMovingBlock(pose, state);
        }

        @Override
        public void submitBlockModel(PoseStack pose, RenderType renderType, List<BlockStateModelPart> parts, int[] tints, int light, int overlay,
                                     int outline) {
            this.target.submitBlockModel(pose, renderType, parts, tints, light, overlay, outline);
        }

        @Override
        public void submitBreakingBlockModel(PoseStack pose, BlockStateModel model, long seed, int progress) {
            this.target.submitBreakingBlockModel(pose, model, seed, progress);
        }

        @Override
        public void submitItem(PoseStack pose, ItemDisplayContext context, int light, int overlay, int outline, int[] tints, List<net.minecraft.client.resources.model.geometry.BakedQuad> quads,
                               ItemStackRenderState.FoilType foil) {
            this.target.submitItem(pose, context, light, overlay, outline, tints, quads, foil);
        }

        @Override
        public void submitCustomGeometry(PoseStack pose, RenderType renderType, SubmitNodeCollector.CustomGeometryRenderer renderer) {
            this.target.submitCustomGeometry(pose, renderType, renderer);
        }

        @Override
        public void submitParticleGroup(SubmitNodeCollector.ParticleGroupRenderer renderer) {
            this.target.submitParticleGroup(renderer);
        }

        @Override
        public void submitBlockModel(PoseStack pose, RenderType renderType, List<BlockStateModelPart> parts, Mesh mesh, int[] tints, int light,
                                     int overlay, int outline) {
            ((FabricOrderedSubmitNodeCollector) (Object) this.target).submitBlockModel(pose, renderType, parts, mesh, tints, light, overlay, outline);
        }

        @Override
        public void submitItem(PoseStack pose, ItemDisplayContext context, int light, int overlay, int outline, int[] tints,
                               List<net.minecraft.client.resources.model.geometry.BakedQuad> quads, MeshView mesh, ItemStackRenderState.FoilType foil) {
            ((FabricOrderedSubmitNodeCollector) (Object) this.target).submitItem(pose, context, light, overlay, outline, tints, quads, mesh, foil);
        }
    }
}
