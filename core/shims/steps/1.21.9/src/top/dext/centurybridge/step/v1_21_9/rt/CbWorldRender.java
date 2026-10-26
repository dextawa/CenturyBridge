package top.dext.centurybridge.step.v1_21_9.rt;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.class_239;
import net.minecraft.class_4597;
import net.minecraft.class_638;
import net.minecraft.class_757;
import net.minecraft.class_761;
import net.minecraft.class_9779;
import net.minecraft.class_4184;
import net.minecraft.class_9909;
import net.minecraft.class_9916;
import net.minecraft.class_9960;
import org.joml.Matrix4f;
import top.dext.centurybridge.step.v1_21_9.facade.WorldRenderContext;
import top.dext.centurybridge.step.v1_21_9.facade.WorldRenderEvents;

/**
 * Fires the restored WorldRenderEvents (Fabric API 0.136) from 1.21.9's LevelRenderer (CbWorldRenderEventsMixin).
 * Nothing here runs before the restored WorldRenderEvents class initialized ({@link #cbActivate}): until a converted
 * mod touches the API the mixin returns at once, the frame is 1.21.9's and Fabric API is never loaded through here.
 */
@Environment(EnvType.CLIENT)
public final class CbWorldRender {

    private CbWorldRender() {
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
    public static CbWorldRenderContextImpl cbLive(class_761 renderer) {
        if (!active) {
            return null;
        }
        CbWorldRenderContextImpl context = ((CbWorldRenderHolder) renderer).cb$worldRenderContext();
        return context.rendering ? context : null;
    }

    /** WorldRenderContext.getInstance(renderer) */
    public static WorldRenderContext cbContext(class_761 renderer) {
        CbWorldRenderContextImpl context = ((CbWorldRenderHolder) renderer).cb$worldRenderContext();
        if (!context.rendering) {
            throw new IllegalStateException("WorldRenderer is not rendering");
        }
        return context;
    }

    /** renderLevel HEAD: 0.136's prepare, then START */
    public static void cbStart(class_761 renderer, class_9779 tickCounter, boolean blockOutlines, class_4184 camera, class_757 gameRenderer,
            Matrix4f positionMatrix, Matrix4f projectionMatrix, class_4597 consumers, boolean advancedTranslucency, class_638 world) {
        CbWorldRenderContextImpl context = ((CbWorldRenderHolder) renderer).cb$worldRenderContext();
        context.prepare(renderer, tickCounter, blockOutlines, camera, gameRenderer, positionMatrix, projectionMatrix, consumers,
            advancedTranslucency, world);
        context.rendering = true;
        WorldRenderEvents.START.invoker().onStart(context);
    }

    public static void cbAfterSetup(CbWorldRenderContextImpl context) {
        WorldRenderEvents.AFTER_SETUP.invoker().afterSetup(context);
    }

    public static void cbBeforeEntities(CbWorldRenderContextImpl context) {
        WorldRenderEvents.BEFORE_ENTITIES.invoker().beforeEntities(context);
    }

    public static void cbAfterEntities(CbWorldRenderContextImpl context) {
        WorldRenderEvents.AFTER_ENTITIES.invoker().afterEntities(context);
    }

    /** block outline HEAD: BEFORE_BLOCK_OUTLINE with the crosshair target, its answer gating the outline */
    public static void cbBeforeBlockOutline(CbWorldRenderContextImpl context, boolean translucent, class_239 hitResult) {
        context.setTranslucentBlockOutline(translucent);
        context.renderBlockOutline = WorldRenderEvents.BEFORE_BLOCK_OUTLINE.invoker().beforeBlockOutline(context, hitResult);
    }

    public static boolean cbRenderBlockOutline(CbWorldRenderContextImpl context) {
        return context.renderBlockOutline;
    }

    /** BLOCK_OUTLINE with the outline's values; false = vanilla's outline is not drawn */
    public static boolean cbBlockOutline(CbWorldRenderContextImpl context, net.minecraft.class_1297 entity, double cameraX, double cameraY,
            double cameraZ, net.minecraft.class_2338 blockPos, net.minecraft.class_2680 blockState) {
        context.prepareBlockOutline(entity, cameraX, cameraY, cameraZ, blockPos, blockState);
        return WorldRenderEvents.BLOCK_OUTLINE.invoker().onBlockOutline(context, context);
    }

    public static void cbFrustum(CbWorldRenderContextImpl context, net.minecraft.class_4604 frustum) {
        context.setFrustum(frustum);
    }

    public static void cbMatrixStack(CbWorldRenderContextImpl context, net.minecraft.class_4587 matrixStack) {
        context.setMatrixStack(matrixStack);
    }

    public static void cbBeforeDebugRender(CbWorldRenderContextImpl context) {
        WorldRenderEvents.BEFORE_DEBUG_RENDER.invoker().beforeDebugRender(context);
    }

    /**
     * Before the clouds are added to the frame graph: 0.136's afterTranslucent pass, writing the main target and
     * firing AFTER_TRANSLUCENT when the graph runs it. Added only while a listener is registered.
     */
    public static void cbAfterTranslucentPass(CbWorldRenderContextImpl context, class_9909 frameGraph, class_9960 targets) {
        if (frameGraph == null || !WorldRenderEvents.cbAfterTranslucentListened()) {
            return;
        }
        class_9916 pass = frameGraph.method_61911("afterTranslucent");
        targets.field_53091 = pass.method_61933(targets.field_53091);
        pass.method_61929(() -> WorldRenderEvents.AFTER_TRANSLUCENT.invoker().afterTranslucent(context));
    }

    public static void cbLast(CbWorldRenderContextImpl context) {
        WorldRenderEvents.LAST.invoker().onLast(context);
    }

    /** renderLevel RETURN: END, then the frame is over */
    public static void cbEnd(CbWorldRenderContextImpl context) {
        try {
            WorldRenderEvents.END.invoker().onEnd(context);
        } finally {
            context.rendering = false;
        }
    }
}
