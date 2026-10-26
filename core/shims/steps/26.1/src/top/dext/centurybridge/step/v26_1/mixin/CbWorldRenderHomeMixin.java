package top.dext.centurybridge.step.v26_1.mixin;

import com.mojang.blaze3d.buffers.GpuBufferSlice;
import com.mojang.blaze3d.framegraph.FrameGraphBuilder;
import com.mojang.blaze3d.resource.GraphicsResourceAllocator;
import com.mojang.blaze3d.vertex.PoseStack;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.DeltaTracker;
import net.minecraft.client.CloudStatus;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.LevelRenderer;
import net.minecraft.client.renderer.LevelTargetBundle;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.client.renderer.chunk.ChunkSectionsToRender;
import net.minecraft.client.renderer.state.level.BlockOutlineRenderState;
import net.minecraft.client.renderer.state.level.CameraRenderState;
import net.minecraft.client.renderer.state.level.LevelRenderState;
import net.minecraft.world.phys.Vec3;
import org.joml.Matrix4fc;
import org.joml.Vector4f;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import top.dext.centurybridge.step.v26_1.rt.CbWorldRenderHome;
import top.dext.centurybridge.step.v26_1.rt.CbWorldRenderHomeContext;
import top.dext.centurybridge.step.v26_1.rt.CbWorldRenderHomeHolder;

/**
 * Fires the restored 0.136 WorldRenderEvents (facade/WorldRenderEvents, the 26.1 home of the 1.21.9 layer's copy) from
 * 26.1's LevelRenderer at the points of 26.1's frame matching the 1.21.9 layer's CbWorldRenderEventsMixin (javap of
 * 26.1 LevelRenderer):
 * <ul>
 *   <li>START at renderLevel's head; the context takes the frame's DeltaTracker and block-outline flag (renderLevel's
 *       arguments 1 and 2), the main camera, the camera state's view rotation and projection matrices (what 1.21.9's
 *       renderLevel was handed as its position and projection matrices) and cull frustum (what renderLevel hands
 *       addMainPass, the frustum update(camera) culled the terrain with), the main buffer source, the level;</li>
 *   <li>AFTER_SETUP after the main pass pushes "solidTerrain" (1.21.9: "terrain"), before the terrain draws;</li>
 *   <li>the main pass's pose stack as it hands it to submitEntities;</li>
 *   <li>BEFORE_ENTITIES at submitEntities' head; AFTER_ENTITIES after renderTranslucentFeatures (1.21.9 drew every
 *       submitted feature in one renderAllFeatures, which 26.1 splits into solid and translucent);</li>
 *   <li>BEFORE_BLOCK_OUTLINE at renderBlockOutline's head and BLOCK_OUTLINE after its translucency test (the first read
 *       of the camera position), as 1.21.9; a refused BLOCK_OUTLINE skips vanilla's outline. What the listeners drew
 *       into the frame's buffer source is drawn when 26.1's main pass ends that source's batch right after each
 *       renderBlockOutline call (javap: bufferSource.endBatch() follows both calls), so the handler ends nothing itself
 *       (1.21.9's endLastBatch at that point is that same batch end, earlier);</li>
 *   <li>BEFORE_DEBUG_RENDER at finalizeGizmoCollection's head (26.1 draws the debug geometry as gizmos there);</li>
 *   <li>0.136's afterTranslucent frame pass right before the clouds pass, or before the weather pass when no clouds
 *       pass is added (1.21.9 added it just before reading the clouds option), only while AFTER_TRANSLUCENT has a
 *       listener;</li>
 *   <li>LAST at the main pass's return; END at renderLevel's return.</li>
 * </ul>
 * Every injection only adds: it returns at once until a converted mod initialized the restored events, cancels nothing a
 * listener did not refuse, and leaves 26.1's own and other mods' injections at these methods in place.
 */
@Environment(EnvType.CLIENT)
@Mixin(LevelRenderer.class)
public abstract class CbWorldRenderHomeMixin implements CbWorldRenderHomeHolder {

    private static final String RENDER_LEVEL = "renderLevel(Lcom/mojang/blaze3d/resource/GraphicsResourceAllocator;Lnet/minecraft/client/DeltaTracker;ZLnet/minecraft/client/renderer/state/level/CameraRenderState;Lorg/joml/Matrix4fc;Lcom/mojang/blaze3d/buffers/GpuBufferSlice;Lorg/joml/Vector4f;ZLnet/minecraft/client/renderer/chunk/ChunkSectionsToRender;)V";
    private static final String MAIN_PASS = "lambda$addMainPass$0(Lcom/mojang/blaze3d/buffers/GpuBufferSlice;Lnet/minecraft/client/renderer/state/level/LevelRenderState;Lnet/minecraft/util/profiling/ProfilerFiller;Lnet/minecraft/client/renderer/chunk/ChunkSectionsToRender;Lcom/mojang/blaze3d/resource/ResourceHandle;Lcom/mojang/blaze3d/resource/ResourceHandle;Lcom/mojang/blaze3d/resource/ResourceHandle;Lcom/mojang/blaze3d/resource/ResourceHandle;Lcom/mojang/blaze3d/resource/ResourceHandle;ZLorg/joml/Matrix4fc;)V";
    private static final String BLOCK_OUTLINE = "renderBlockOutline(Lnet/minecraft/client/renderer/MultiBufferSource$BufferSource;Lcom/mojang/blaze3d/vertex/PoseStack;ZLnet/minecraft/client/renderer/state/level/LevelRenderState;)V";

    @Shadow
    @Final
    private LevelTargetBundle targets;

    @Unique
    private CbWorldRenderHomeContext cb$homeContext;

    /** whether this frame's afterTranslucent pass was added */
    @Unique
    private boolean cb$homeAfterTranslucentAdded;

    @Override
    public CbWorldRenderHomeContext cb$homeContext() {
        CbWorldRenderHomeContext context = this.cb$homeContext;
        if (context == null) {
            context = new CbWorldRenderHomeContext();
            this.cb$homeContext = context;
        }
        return context;
    }

    @Inject(method = RENDER_LEVEL, at = @At("HEAD"))
    private void cb$homeStart(GraphicsResourceAllocator allocator, DeltaTracker deltaTracker, boolean renderBlockOutline, CameraRenderState cameraState,
            Matrix4fc modelView, GpuBufferSlice fog, Vector4f fogColor, boolean renderSky, ChunkSectionsToRender sections, CallbackInfo ci) {
        this.cb$homeAfterTranslucentAdded = false;
        if (!CbWorldRenderHome.cbActive()) {
            return;
        }
        Minecraft minecraft = Minecraft.getInstance();
        CbWorldRenderHome.cbStart((LevelRenderer) (Object) this, deltaTracker, renderBlockOutline, minecraft.gameRenderer.getMainCamera(),
            minecraft.gameRenderer, cameraState.viewRotationMatrix, cameraState.projectionMatrix, minecraft.renderBuffers().bufferSource(),
            Minecraft.useShaderTransparency(), minecraft.level, cameraState.cullFrustum);
    }

    @Inject(method = MAIN_PASS, at = @At(value = "INVOKE_STRING", target = "Lnet/minecraft/util/profiling/ProfilerFiller;push(Ljava/lang/String;)V",
        args = "ldc=solidTerrain", shift = At.Shift.AFTER))
    private void cb$homeAfterSetup(CallbackInfo ci) {
        CbWorldRenderHomeContext context = CbWorldRenderHome.cbLive((LevelRenderer) (Object) this);
        if (context != null) {
            CbWorldRenderHome.cbAfterSetup(context);
        }
    }

    /**
     * BEFORE_ENTITIES, with the main pass's pose stack (the one it hands submitEntities, made just before; no event fires
     * between the two). submitEntities' only caller is the main pass (javap), so its head is the 1.21.9 point
     * "before the entities are submitted"; a method head keeps the injection off the main-pass lambda's locals.
     */
    @Inject(method = "submitEntities(Lcom/mojang/blaze3d/vertex/PoseStack;Lnet/minecraft/client/renderer/state/level/LevelRenderState;Lnet/minecraft/client/renderer/SubmitNodeCollector;)V",
        at = @At("HEAD"))
    private void cb$homeBeforeEntities(PoseStack matrixStack, LevelRenderState state, SubmitNodeCollector collector, CallbackInfo ci) {
        CbWorldRenderHomeContext context = CbWorldRenderHome.cbLive((LevelRenderer) (Object) this);
        if (context != null) {
            CbWorldRenderHome.cbMatrixStack(context, matrixStack);
            CbWorldRenderHome.cbBeforeEntities(context);
        }
    }

    @Inject(method = MAIN_PASS, at = @At(value = "INVOKE",
        target = "Lnet/minecraft/client/renderer/feature/FeatureRenderDispatcher;renderTranslucentFeatures()V", shift = At.Shift.AFTER))
    private void cb$homeAfterEntities(CallbackInfo ci) {
        CbWorldRenderHomeContext context = CbWorldRenderHome.cbLive((LevelRenderer) (Object) this);
        if (context != null) {
            CbWorldRenderHome.cbAfterEntities(context);
        }
    }

    @Inject(method = BLOCK_OUTLINE, at = @At("HEAD"))
    private void cb$homeBeforeBlockOutline(MultiBufferSource.BufferSource consumers, PoseStack matrices, boolean translucent, LevelRenderState state,
            CallbackInfo ci) {
        CbWorldRenderHomeContext context = CbWorldRenderHome.cbLive((LevelRenderer) (Object) this);
        if (context != null) {
            CbWorldRenderHome.cbBeforeBlockOutline(context, translucent, Minecraft.getInstance().hitResult);
        }
    }

    @Inject(method = BLOCK_OUTLINE, at = @At(value = "FIELD",
        target = "Lnet/minecraft/client/renderer/state/level/CameraRenderState;pos:Lnet/minecraft/world/phys/Vec3;", ordinal = 0), cancellable = true)
    private void cb$homeBlockOutline(MultiBufferSource.BufferSource consumers, PoseStack matrices, boolean translucent, LevelRenderState state,
            CallbackInfo ci) {
        CbWorldRenderHomeContext context = CbWorldRenderHome.cbLive((LevelRenderer) (Object) this);
        if (context == null) {
            return;
        }
        if (!CbWorldRenderHome.cbRenderBlockOutline(context)) {
            ci.cancel();
            return;
        }
        BlockOutlineRenderState outline = state.blockOutlineRenderState;
        Vec3 cameraPos = state.cameraRenderState.pos;
        if (!CbWorldRenderHome.cbBlockOutline(context, context.camera().entity(), cameraPos.x, cameraPos.y, cameraPos.z,
                outline.pos(), context.world().getBlockState(outline.pos()))) {
            ci.cancel();
        }
    }

    /** finalizeGizmoCollection's only caller is the main pass (javap): its head is the point before the gizmo draw */
    @Inject(method = "finalizeGizmoCollection()V", at = @At("HEAD"))
    private void cb$homeBeforeDebugRender(CallbackInfo ci) {
        CbWorldRenderHomeContext context = CbWorldRenderHome.cbLive((LevelRenderer) (Object) this);
        if (context != null) {
            CbWorldRenderHome.cbBeforeDebugRender(context);
        }
    }

    /**
     * 0.136's afterTranslucent pass goes into the frame graph right before the clouds pass (1.21.9: before renderLevel
     * read the clouds option, which only decided whether the clouds pass follows). 26.1's renderLevel adds the clouds
     * pass only with clouds on and visible, then always the weather pass, and nothing between the two: the pass is
     * added at the head of whichever of them comes first this frame.
     */
    @Inject(method = "addCloudsPass(Lcom/mojang/blaze3d/framegraph/FrameGraphBuilder;Lnet/minecraft/client/CloudStatus;Lnet/minecraft/world/phys/Vec3;JFIFI)V",
        at = @At("HEAD"))
    private void cb$homeAfterTranslucentClouds(FrameGraphBuilder frameGraph, CloudStatus status, Vec3 cameraPos, long gameTime, float partialTick,
            int color, float height, int range, CallbackInfo ci) {
        this.cb$homeAfterTranslucent(frameGraph);
    }

    @Inject(method = "addWeatherPass(Lcom/mojang/blaze3d/framegraph/FrameGraphBuilder;Lcom/mojang/blaze3d/buffers/GpuBufferSlice;)V", at = @At("HEAD"))
    private void cb$homeAfterTranslucentWeather(FrameGraphBuilder frameGraph, GpuBufferSlice fog, CallbackInfo ci) {
        this.cb$homeAfterTranslucent(frameGraph);
    }

    @Unique
    private void cb$homeAfterTranslucent(FrameGraphBuilder frameGraph) {
        CbWorldRenderHomeContext context = CbWorldRenderHome.cbLive((LevelRenderer) (Object) this);
        if (context != null && !this.cb$homeAfterTranslucentAdded) {
            this.cb$homeAfterTranslucentAdded = true;
            CbWorldRenderHome.cbAfterTranslucentPass(context, frameGraph, this.targets);
        }
    }

    @Inject(method = MAIN_PASS, at = @At("RETURN"))
    private void cb$homeLast(CallbackInfo ci) {
        CbWorldRenderHomeContext context = CbWorldRenderHome.cbLive((LevelRenderer) (Object) this);
        if (context != null) {
            CbWorldRenderHome.cbLast(context);
        }
    }

    @Inject(method = RENDER_LEVEL, at = @At("RETURN"))
    private void cb$homeEnd(CallbackInfo ci) {
        CbWorldRenderHomeContext context = CbWorldRenderHome.cbLive((LevelRenderer) (Object) this);
        if (context != null) {
            CbWorldRenderHome.cbEnd(context);
        }
    }
}
