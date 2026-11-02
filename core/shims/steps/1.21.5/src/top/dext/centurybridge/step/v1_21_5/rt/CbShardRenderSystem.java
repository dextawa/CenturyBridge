package top.dext.centurybridge.step.v1_21_5.rt;

import com.mojang.blaze3d.opengl.GlStateManager;
import com.mojang.blaze3d.systems.RenderSystem;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

/**
 * 1.21.4 RenderSystem state calls that 1.21.5 removed and that 1.21.4's render state shards made (the shard facades
 * call them; converted mods reach them through staticRedirects): enableCull / disableCull, polygonOffset /
 * enablePolygonOffset / disablePolygonOffset, colorMask, enableColorLogicOp / disableColorLogicOp, blendFunc(int, int),
 * blendFuncSeparate(int x4). Bodies as 1.21.4's (javap -c -p 1.21.4 RenderSystem): assertOnRenderThread, then the same
 * GlStateManager call, now 1.21.5's com.mojang.blaze3d.opengl.GlStateManager (it caches the state, so 1.21.5's caches stay
 * true; its command encoder re-applies each pipeline's own state at the next draw, class_10860.method_68356).
 * blendFunc(II) goes through {@link CbGlStateManager#_blendFunc} (1.21.5 has no _blendFunc).
 */
@Environment(EnvType.CLIENT)
public final class CbShardRenderSystem {

    private CbShardRenderSystem() {
    }

    public static void enableCull() {
        RenderSystem.assertOnRenderThread();
        GlStateManager._enableCull();
    }

    public static void disableCull() {
        RenderSystem.assertOnRenderThread();
        GlStateManager._disableCull();
    }

    public static void polygonOffset(float factor, float units) {
        RenderSystem.assertOnRenderThread();
        GlStateManager._polygonOffset(factor, units);
    }

    public static void enablePolygonOffset() {
        RenderSystem.assertOnRenderThread();
        GlStateManager._enablePolygonOffset();
    }

    public static void disablePolygonOffset() {
        RenderSystem.assertOnRenderThread();
        GlStateManager._disablePolygonOffset();
    }

    public static void colorMask(boolean red, boolean green, boolean blue, boolean alpha) {
        RenderSystem.assertOnRenderThread();
        GlStateManager._colorMask(red, green, blue, alpha);
    }

    public static void enableColorLogicOp() {
        RenderSystem.assertOnRenderThread();
        GlStateManager._enableColorLogicOp();
    }

    public static void disableColorLogicOp() {
        RenderSystem.assertOnRenderThread();
        GlStateManager._disableColorLogicOp();
    }

    public static void blendFunc(int src, int dst) {
        RenderSystem.assertOnRenderThread();
        CbGlStateManager._blendFunc(src, dst);
    }

    public static void blendFuncSeparate(int srcRgb, int dstRgb, int srcAlpha, int dstAlpha) {
        RenderSystem.assertOnRenderThread();
        GlStateManager._blendFuncSeparate(srcRgb, dstRgb, srcAlpha, dstAlpha);
    }
}
