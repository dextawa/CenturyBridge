package top.dext.centurybridge.step.v26_2.rt;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.client.renderer.SubmitNodeStorage;
import net.minecraft.client.renderer.feature.FeatureRenderDispatcher;
import org.joml.Matrix4fStack;
import org.joml.Matrix4fc;
import com.mojang.blaze3d.systems.RenderSystem;

/**
 * 26.1.2's one submit storage, which 26.2 split per drawing context (javap of both).
 * <p>26.1.2: GameRenderer made one SubmitNodeStorage and gave it to its FeatureRenderDispatcher;
 * GameRenderer.getSubmitNodeStorage(), FeatureRenderDispatcher.getSubmitNodeStorage() and LevelRenderer.submitNodeStorage
 * were that object. The level's main pass submitted into it (submitEntities, submitBlockEntities, particles, destroy
 * progress), drew it (renderSolidFeatures, renderTranslucentFeatures, renderTranslucentParticles) and cleared it
 * (clearSubmitNodes, @537); the hand (GameRenderer.renderItemInHand), the picture-in-picture renderers and
 * GuiItemAtlas.drawToSlot submitted into the same storage and drew it with renderAllFeatures() (draw all, then clear).
 * <p>26.2: LevelRenderer owns its storage, filled by submitFeatures and drained by FeatureRenderDispatcher.prepareFrame at
 * the start of render, before the frame graph runs; the hand and screen effects (GameRenderer.handAndScreenSubmitNodeStorage),
 * each PictureInPictureRenderer and GuiItemAtlas own theirs and draw them with renderAllFeatures(storage). The dispatcher
 * holds one PreparedFrame: preparing a second one while it is open throws ("PreparedFrame already in use").
 * <p>So 26.1.2's storage is, at each moment, the one 26.2 will still draw in the same frame:
 * <ul>
 * <li>from LevelRenderer.render's start until prepareFrame (26.2's submit window, which holds the submits 26.1.2's main
 * pass made before its first draw): the level's storage, drawn by this frame's prepared frame;</li>
 * <li>anywhere else, including the level's frame graph after prepareFrame: this layer's storage. It is drawn where
 * 26.1.2 drew its one storage: by renderAllFeatures() of 26.1.2 code, after each renderAllFeatures(storage) of 26.2
 * vanilla (hand, screen effects, picture-in-picture, item atlas), and, for what the level frame left in it, inside
 * LevelRenderer.render after PreparedFrame.close with the level's model-view matrix and projection and the main
 * target's level depth, before GameRenderer clears the depth for the hand.</li>
 * </ul>
 * Native 26.2 code never sees this layer's storage, so 26.2's own submits and timing are unchanged.
 * <p>Differences from 26.1.2, which drew the storage at the call: renderAllFeatures() while the level frame is open (or
 * before it, in the submit window) cannot draw on 26.2 (a nested prepare throws, and before the frame graph the main pass
 * clears the target); those submits are drawn this frame by the prepared frame (submit window) or after the level's
 * frame graph (frame open), not at the call. See release_notes_known.md.
 */
@Environment(EnvType.CLIENT)
public final class CbS26SubmitStorage {
    private static final int OUTSIDE = 0;
    private static final int LEVEL_SUBMIT = 1;
    private static final int LEVEL_FRAME = 2;

    private static int window = OUTSIDE;
    private static SubmitNodeStorage levelStorage;
    private static SubmitNodeStorage layerStorage;

    private CbS26SubmitStorage() {
    }

    /** this layer's storage (created on first use, on the render thread) */
    private static SubmitNodeStorage layer() {
        SubmitNodeStorage s = layerStorage;
        if (s == null) {
            s = layerStorage = new SubmitNodeStorage();
        }
        return s;
    }

    /** the storage 26.1.2 code means right now (class comment) */
    public static SubmitNodeStorage current() {
        return window == LEVEL_SUBMIT ? levelStorage : layer();
    }

    /** 26.1.2 FeatureRenderDispatcher.getSubmitNodeStorage() */
    public static SubmitNodeStorage getSubmitNodeStorage(FeatureRenderDispatcher dispatcher) {
        return current();
    }

    /** 26.1.2 GameRenderer.getSubmitNodeStorage(): the same storage its dispatcher held */
    public static SubmitNodeStorage getSubmitNodeStorage(GameRenderer gameRenderer) {
        return current();
    }

    /**
     * 26.1.2 FeatureRenderDispatcher.renderAllFeatures(): drew everything in the storage, then cleared it. Outside the
     * level frame 26.2 does the same with renderAllFeatures(storage). Inside it (submit window or open frame) the submits
     * stay where they are and are drawn later in the same frame (class comment).
     */
    public static void renderAllFeatures(FeatureRenderDispatcher dispatcher) {
        if (window != OUTSIDE || dispatcher.preparedFrame.context != null) {
            return;
        }
        SubmitNodeStorage s = layer();
        if (!s.getSubmitsPerOrder().isEmpty()) {
            dispatcher.renderAllFeatures(s);
        }
    }

    /** 26.1.2 FeatureRenderDispatcher.clearSubmitNodes(): storage.clear() */
    public static void clearSubmitNodes(FeatureRenderDispatcher dispatcher) {
        clear(current());
    }

    /**
     * 26.1.2 SubmitNodeStorage.clear(): emptied every node collection of the storage. 26.2's storage keeps a collection
     * only while it holds submits (drainPhases removes drained ones, order(int) makes a new one on the next submit), so
     * dropping the collections is the same empty storage.
     */
    public static void clear(SubmitNodeStorage storage) {
        storage.getSubmitsPerOrder().clear();
    }

    /**
     * After 26.2 vanilla drew one of its own storages with renderAllFeatures(storage) (hand, screen effects,
     * picture-in-picture, item atlas; mixin/CbS26SubmitDrainMixin): 26.1.2 drew its one storage there, so what 26.1.2
     * code put in this layer's storage is drawn there too, in the same target and projection.
     */
    public static void afterVanillaDraw(FeatureRenderDispatcher dispatcher, SubmitNodeStorage drawn) {
        SubmitNodeStorage s = layerStorage;
        if (window != OUTSIDE || s == null || drawn == s || s.getSubmitsPerOrder().isEmpty()) {
            return;
        }
        dispatcher.renderAllFeatures(s);
    }

    /** LevelRenderer.render head (mixin/CbS26SubmitWindowMixin) */
    public static void levelStart(SubmitNodeStorage storage) {
        levelStorage = storage;
        window = LEVEL_SUBMIT;
    }

    /** after LevelRenderer.render's prepareFrame: the level's storage is drained, its frame is open */
    public static void levelFrameOpen() {
        window = LEVEL_FRAME;
    }

    /**
     * After LevelRenderer.render's PreparedFrame.close: what 26.1.2 code left in this layer's storage is drawn now, with
     * the model-view matrix the level frame drew with (render pushes the model-view stack, multiplies it by its
     * Matrix4fc argument and pops it just before the close).
     */
    public static void levelFrameClosed(FeatureRenderDispatcher dispatcher, Matrix4fc modelView) {
        window = OUTSIDE;
        levelStorage = null;
        SubmitNodeStorage s = layerStorage;
        if (s == null || s.getSubmitsPerOrder().isEmpty()) {
            return;
        }
        Matrix4fStack stack = RenderSystem.getModelViewStack();
        stack.pushMatrix();
        stack.mul(modelView);
        try {
            dispatcher.renderAllFeatures(s);
        } finally {
            stack.popMatrix();
        }
    }
}
