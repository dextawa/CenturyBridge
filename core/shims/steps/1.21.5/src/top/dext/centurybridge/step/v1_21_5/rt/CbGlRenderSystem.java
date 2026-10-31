package top.dext.centurybridge.step.v1_21_5.rt;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import top.dext.centurybridge.step.v1_21_5.facade.GlStateManager$class_1030;
import top.dext.centurybridge.step.v1_21_5.facade.GlStateManager$class_4534;
import top.dext.centurybridge.step.v1_21_5.facade.GlStateManager$class_4535;

/**
 * 1.21.4's RenderSystem.blendFunc / blendFuncSeparate / logicOp over GlStateManager's SourceFactor / DestFactor /
 * LogicOp enums (facades GlStateManager$class_4535 / $class_4534 / $class_1030), which 1.21.5 dropped with the enums.
 * 1.21.4's bodies (javap -c -p 1.21.4 RenderSystem): the render-thread check, then GlStateManager with each constant's GL
 * value. They go through 1.21.5's own cached GlStateManager entry points (_blendFuncSeparate, _logicOp; _blendFunc via
 * {@link CbGlStateManager}), so 1.21.5's state caches stay true and its render pipeline re-applies its own blend and
 * logic-op state over them at its next pass exactly as it would over its own calls.
 */
@Environment(EnvType.CLIENT)
public final class CbGlRenderSystem {

    private CbGlRenderSystem() {
    }

    public static void blendFunc(GlStateManager$class_4535 src, GlStateManager$class_4534 dst) {
        com.mojang.blaze3d.systems.RenderSystem.assertOnRenderThread();
        CbGlStateManager._blendFunc(src.value, dst.value);
    }

    public static void blendFuncSeparate(GlStateManager$class_4535 srcRgb, GlStateManager$class_4534 dstRgb, GlStateManager$class_4535 srcAlpha,
            GlStateManager$class_4534 dstAlpha) {
        com.mojang.blaze3d.systems.RenderSystem.assertOnRenderThread();
        com.mojang.blaze3d.opengl.GlStateManager._blendFuncSeparate(srcRgb.value, dstRgb.value, srcAlpha.value, dstAlpha.value);
    }

    public static void logicOp(GlStateManager$class_1030 op) {
        com.mojang.blaze3d.systems.RenderSystem.assertOnRenderThread();
        com.mojang.blaze3d.opengl.GlStateManager._logicOp(op.field_5108);
    }

    // ---- 1.21.4 RenderSystem state calls 1.21.5 removed (javap -c -p 1.21.4 RenderSystem): the same GlStateManager
    // calls, now 1.21.5's com.mojang.blaze3d.opengl.GlStateManager (cached, so 1.21.5's caches stay true); the
    // render-thread check where 1.21.4 had it ----

    public static void enableDepthTest() {
        com.mojang.blaze3d.opengl.GlStateManager._enableDepthTest();
    }

    public static void disableDepthTest() {
        com.mojang.blaze3d.systems.RenderSystem.assertOnRenderThread();
        com.mojang.blaze3d.opengl.GlStateManager._disableDepthTest();
    }

    public static void depthFunc(int func) {
        com.mojang.blaze3d.systems.RenderSystem.assertOnRenderThread();
        com.mojang.blaze3d.opengl.GlStateManager._depthFunc(func);
    }

    public static void depthMask(boolean mask) {
        com.mojang.blaze3d.systems.RenderSystem.assertOnRenderThread();
        com.mojang.blaze3d.opengl.GlStateManager._depthMask(mask);
    }

    public static void enableBlend() {
        com.mojang.blaze3d.systems.RenderSystem.assertOnRenderThread();
        com.mojang.blaze3d.opengl.GlStateManager._enableBlend();
    }

    public static void disableBlend() {
        com.mojang.blaze3d.systems.RenderSystem.assertOnRenderThread();
        com.mojang.blaze3d.opengl.GlStateManager._disableBlend();
    }

    /** blendFuncSeparate(SRC_ALPHA, ONE_MINUS_SRC_ALPHA, ONE, ZERO) (GL 0x302, 0x303, 1, 0) */
    public static void defaultBlendFunc() {
        com.mojang.blaze3d.systems.RenderSystem.assertOnRenderThread();
        com.mojang.blaze3d.opengl.GlStateManager._blendFuncSeparate(0x302, 0x303, 1, 0);
    }

    public static void blendEquation(int mode) {
        com.mojang.blaze3d.systems.RenderSystem.assertOnRenderThread();
        CbGlStateManager._blendEquation(mode);
    }

    public static void activeTexture(int texture) {
        com.mojang.blaze3d.systems.RenderSystem.assertOnRenderThread();
        com.mojang.blaze3d.opengl.GlStateManager._activeTexture(texture);
    }

    public static void texParameter(int target, int name, int value) {
        com.mojang.blaze3d.opengl.GlStateManager._texParameter(target, name, value);
    }

    public static void bindTexture(int texture) {
        com.mojang.blaze3d.opengl.GlStateManager._bindTexture(texture);
    }

    public static void viewport(int x, int y, int w, int h) {
        com.mojang.blaze3d.opengl.GlStateManager._viewport(x, y, w, h);
    }

    public static void clearColor(float r, float g, float b, float a) {
        CbGlStateManager._clearColor(r, g, b, a);
    }

    public static void clear(int mask) {
        com.mojang.blaze3d.opengl.GlStateManager._clear(mask);
    }

    /**
     * isInInit || isOnRenderThread. 1.21.5 has no initialization window: 1.21.4 set isInInit between
     * beginInitialization / finishInitialization in client Main, on the thread initRenderThread had just made the
     * render thread, so there the answer was the render-thread test's; only another thread during that window
     * differs (release_notes_known.md).
     */
    public static boolean isOnRenderThreadOrInit() {
        return com.mojang.blaze3d.systems.RenderSystem.isOnRenderThread();
    }

    /** see isOnRenderThreadOrInit */
    public static void assertOnRenderThreadOrInit() {
        com.mojang.blaze3d.systems.RenderSystem.assertOnRenderThread();
    }
}
