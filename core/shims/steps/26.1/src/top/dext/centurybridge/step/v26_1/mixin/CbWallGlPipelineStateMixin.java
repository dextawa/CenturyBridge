package top.dext.centurybridge.step.v26_1.mixin;

import com.mojang.blaze3d.opengl.GlStateManager;
import com.mojang.blaze3d.pipeline.RenderPipeline;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import top.dext.centurybridge.step.v26_1.facade.LogicOp;
import top.dext.centurybridge.step.v26_1.rt.CbWallPipeline;

/**
 * The colour logic operation 1.21.11's GL backend applied with every pipeline (GlCommandEncoder
 * applyPipelineState: NONE disabled GL_COLOR_LOGIC_OP, OR_REVERSE enabled it with glLogicOp(GL_OR_REVERSE)).
 * 26.1 keeps GlStateManager's logic-op state but no pipeline sets it, so it is applied here, after 26.1's
 * own state, for every pipeline: OR_REVERSE only for a pipeline a 1.21.11 mod built with it
 * (rt/CbWallPipeline records it), disabled for all others, as 1.21.11 did.
 */
@Mixin(targets = "com.mojang.blaze3d.opengl.GlCommandEncoder")
public abstract class CbWallGlPipelineStateMixin {

    /** GL_OR_REVERSE */
    private static final int CB$OR_REVERSE = 5387;

    @Inject(method = "applyPipelineState", at = @At("TAIL"))
    private void cb$colorLogic(RenderPipeline pipeline, CallbackInfo ci) {
        if (CbWallPipeline.getColorLogic(pipeline) == LogicOp.OR_REVERSE) {
            GlStateManager._enableColorLogicOp();
            GlStateManager._logicOp(CB$OR_REVERSE);
        } else {
            GlStateManager._disableColorLogicOp();
        }
    }
}
