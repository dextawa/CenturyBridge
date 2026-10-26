package top.dext.centurybridge.step.v26_1.rt;

import com.mojang.blaze3d.framegraph.FrameGraphBuilder;
import com.mojang.blaze3d.framegraph.FramePass;
import com.mojang.blaze3d.vertex.PoseStack;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.Camera;
import net.minecraft.client.DeltaTracker;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.client.renderer.LevelRenderer;
import net.minecraft.client.renderer.LevelTargetBundle;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.culling.Frustum;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.HitResult;
import org.joml.Matrix4f;
import top.dext.centurybridge.step.v26_1.facade.WorldRenderContext;
import top.dext.centurybridge.step.v26_1.facade.WorldRenderEvents;

/**
 * Fires the restored 0.136 WorldRenderEvents (facade/WorldRenderEvents) from 26.1's LevelRenderer
 * (mixin/CbWorldRenderHomeMixin). Nothing here runs before WorldRenderEvents initialized ({@link #cbActivate}): until a
 * converted mod touches the API the mixin returns at once, the frame is 26.1's and Fabric API is never loaded through
 * here.
 */
@Environment(EnvType.CLIENT)
public final class CbWorldRenderHome {

    private CbWorldRenderHome() {
    }

    private static volatile boolean active;

    /** called by WorldRenderEvents' static initializer */
    public static void cbActivate() {
        active = true;
    }

    public static boolean cbActive() {
        return active;
    }

    /** the context of a renderer while it draws a frame and the events are live; null otherwise */
    public static CbWorldRenderHomeContext cbLive(LevelRenderer renderer) {
        if (!active) {
            return null;
        }
        CbWorldRenderHomeContext context = ((CbWorldRenderHomeHolder) renderer).cb$homeContext();
        return context.rendering ? context : null;
    }

    /** WorldRenderContext.getInstance(renderer) */
    public static WorldRenderContext cbContext(LevelRenderer renderer) {
        CbWorldRenderHomeContext context = ((CbWorldRenderHomeHolder) renderer).cb$homeContext();
        if (!context.rendering) {
            throw new IllegalStateException("WorldRenderer is not rendering");
        }
        return context;
    }

    /** renderLevel HEAD: 0.136's prepare, then START */
    public static void cbStart(LevelRenderer renderer, DeltaTracker tickCounter, boolean blockOutlines, Camera camera, GameRenderer gameRenderer,
            Matrix4f positionMatrix, Matrix4f projectionMatrix, MultiBufferSource consumers, boolean advancedTranslucency, ClientLevel world,
            Frustum frustum) {
        CbWorldRenderHomeContext context = ((CbWorldRenderHomeHolder) renderer).cb$homeContext();
        context.prepare(renderer, tickCounter, blockOutlines, camera, gameRenderer, positionMatrix, projectionMatrix, consumers,
            advancedTranslucency, world, frustum);
        context.rendering = true;
        WorldRenderEvents.START.invoker().onStart(context);
    }

    public static void cbAfterSetup(CbWorldRenderHomeContext context) {
        WorldRenderEvents.AFTER_SETUP.invoker().afterSetup(context);
    }

    public static void cbMatrixStack(CbWorldRenderHomeContext context, PoseStack matrixStack) {
        context.setMatrixStack(matrixStack);
    }

    public static void cbBeforeEntities(CbWorldRenderHomeContext context) {
        WorldRenderEvents.BEFORE_ENTITIES.invoker().beforeEntities(context);
    }

    public static void cbAfterEntities(CbWorldRenderHomeContext context) {
        WorldRenderEvents.AFTER_ENTITIES.invoker().afterEntities(context);
    }

    /** block outline HEAD: BEFORE_BLOCK_OUTLINE with the crosshair target, its answer gating the outline */
    public static void cbBeforeBlockOutline(CbWorldRenderHomeContext context, boolean translucent, HitResult hitResult) {
        context.setTranslucentBlockOutline(translucent);
        context.renderBlockOutline = WorldRenderEvents.BEFORE_BLOCK_OUTLINE.invoker().beforeBlockOutline(context, hitResult);
    }

    public static boolean cbRenderBlockOutline(CbWorldRenderHomeContext context) {
        return context.renderBlockOutline;
    }

    /** BLOCK_OUTLINE with the outline's values; false = vanilla's outline is not drawn */
    public static boolean cbBlockOutline(CbWorldRenderHomeContext context, Entity entity, double cameraX, double cameraY, double cameraZ,
            BlockPos blockPos, BlockState blockState) {
        context.prepareBlockOutline(entity, cameraX, cameraY, cameraZ, blockPos, blockState);
        return WorldRenderEvents.BLOCK_OUTLINE.invoker().onBlockOutline(context, context);
    }

    public static void cbBeforeDebugRender(CbWorldRenderHomeContext context) {
        WorldRenderEvents.BEFORE_DEBUG_RENDER.invoker().beforeDebugRender(context);
    }

    /**
     * Before the clouds are added to the frame graph: 0.136's afterTranslucent pass, writing the main target and firing
     * AFTER_TRANSLUCENT when the graph runs it. Added only while a listener is registered.
     */
    public static void cbAfterTranslucentPass(CbWorldRenderHomeContext context, FrameGraphBuilder frameGraph, LevelTargetBundle targets) {
        if (frameGraph == null || targets == null || !WorldRenderEvents.cbAfterTranslucentListened()) {
            return;
        }
        FramePass pass = frameGraph.addPass("afterTranslucent");
        targets.main = pass.readsAndWrites(targets.main);
        pass.executes(() -> WorldRenderEvents.AFTER_TRANSLUCENT.invoker().afterTranslucent(context));
    }

    public static void cbLast(CbWorldRenderHomeContext context) {
        WorldRenderEvents.LAST.invoker().onLast(context);
    }

    /** renderLevel RETURN: END, then the frame is over */
    public static void cbEnd(CbWorldRenderHomeContext context) {
        try {
            WorldRenderEvents.END.invoker().onEnd(context);
        } finally {
            context.rendering = false;
        }
    }
}
