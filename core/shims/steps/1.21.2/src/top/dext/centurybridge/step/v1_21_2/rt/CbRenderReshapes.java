package top.dext.centurybridge.step.v1_21_2.rt;

import it.unimi.dsi.fastutil.longs.LongOpenHashSet;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

/**
 * Values for the callReshapes of private client members 1.21.2 reshaped (s7render.json): what 1.21.2's shape takes
 * that 1.21.1's callers did not pass, or a 1.21.1 value converted to the form 1.21.2 takes. Mods reach these
 * members through invokers; MixinBridge follows the same entries for selectors and handler arguments.
 */
@Environment(EnvType.CLIENT)
public final class CbRenderReshapes {
    private CbRenderReshapes() {
    }

    /** GameRenderer.getFov(camera, partialTick, useFovSetting): 1.21.1 answered a double, 1.21.2 a float */
    public static double fovDouble(float fov) {
        return fov;
    }

    /**
     * ItemFrameRenderer.getFrameModelResourceLoc / getLightVal: 1.21.1 took the frame and tested
     * frame.getType() == EntityType.GLOW_ITEM_FRAME, 1.21.2 takes that test's answer (the render state's isGlowFrame)
     */
    public static boolean glowFrame(net.minecraft.class_1533 frame) {
        return frame.method_5864() == net.minecraft.class_1299.field_28401;
    }

    /**
     * RenderType's clouds builder: 1.21.1's createClouds(colorOnly) drew with NO_CULL; 1.21.2's takes a second flag
     * that picks CULL when true and NO_CULL when false
     */
    public static boolean cloudsCull() {
        return false;
    }

    /**
     * Font's private drawInternal / renderText: 1.21.2 added a last flag (the depth direction of the background and
     * shadow layers) that its 1.21.1-shaped public drawInBatch passes as true, as 1.21.1 drew
     */
    public static boolean fontDefaultDepth() {
        return true;
    }

    /**
     * LevelRenderer.renderHitOutline: 1.21.2 takes the outline colour; 1.21.1 drew renderShape(.., 0, 0, 0, 0.4f),
     * black at alpha 0.4 = ARGB 0x66000000
     */
    public static int hitOutlineColor() {
        return 0x66000000;
    }

    /**
     * Frustum.cubeInFrustum: 1.21.1 answered FrustumIntersection.testAab (inside or intersecting), 1.21.2 answers
     * intersectAab -- INSIDE (-2), INTERSECT (-1), or the index of the plane the box is outside of (>= 0)
     */
    public static boolean inFrustum(int intersection) {
        return intersection < 0;
    }

    /**
     * SectionOcclusionGraph's private search helpers take the client's loaded empty sections in 1.21.2 and skip
     * them; 1.21.1 skipped none, so the set is empty
     */
    public static LongOpenHashSet noEmptySections() {
        return new LongOpenHashSet();
    }

    /** Window's fullscreen toggle passes 1.21.2's Tracy frame capture on to updateDisplay; 1.21.1 had none (null is skipped) */
    public static net.minecraft.class_10219 noFrameCapture() {
        return null;
    }
}
