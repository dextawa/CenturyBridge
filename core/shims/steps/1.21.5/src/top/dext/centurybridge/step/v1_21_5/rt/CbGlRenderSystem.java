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
}
