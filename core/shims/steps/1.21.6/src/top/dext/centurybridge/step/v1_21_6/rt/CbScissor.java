package top.dext.centurybridge.step.v1_21_6.rt;

import com.mojang.blaze3d.systems.RenderPass;
import net.minecraft.class_11219;

/**
 * 1.21.5's com.mojang.blaze3d.systems.ScissorState on 1.21.6, where the class is net.minecraft.class_11219 with the
 * same fields and accessors (javap: enable / disable / isEnabled / getX / getY / getWidth / getHeight, the step's
 * classRenames + methodRenames) but no copyFrom, and RenderPass takes the rectangle, not a state.
 */
@net.fabricmc.api.Environment(net.fabricmc.api.EnvType.CLIENT)
public final class CbScissor {

    private CbScissor() {
    }

    /** RenderSystem.SCISSOR_STATE: 1.21.5's render-type-draw scissor, 1.21.6's scissorStateForRenderTypeDraws (javap) */
    public static class_11219 SCISSOR_STATE() {
        return com.mojang.blaze3d.systems.RenderSystem.getScissorStateForRenderTypeDraws();
    }

    /**
     * ScissorState.copyFrom(other): 1.21.5 copied all five fields (javap). 1.21.6's enable(x, y, w, h) sets the
     * rectangle and the flag, disable() only the flag (javap of class_11219), so a disabled state's rectangle is copied
     * too.
     */
    public static void copyFrom(class_11219 self, class_11219 other) {
        self.method_70814(other.method_72092(), other.method_72093(), other.method_72094(), other.method_72095());
        if (!other.method_72091()) {
            self.method_70813();
        }
    }

    /**
     * RenderPass.enableScissor(ScissorState): 1.21.5 copied the state into the pass (GlRenderPass.field_57872.copyFrom,
     * javap), so a disabled state disabled the pass's scissor; 1.21.6 takes the rectangle or disableScissor.
     */
    public static void enableScissor(RenderPass pass, class_11219 state) {
        if (state.method_72091()) {
            pass.enableScissor(state.method_72092(), state.method_72093(), state.method_72094(), state.method_72095());
        } else {
            pass.disableScissor();
        }
    }

    /** the same, called on the GL render pass itself */
    public static void enableScissor(net.minecraft.class_10866 pass, class_11219 state) {
        enableScissor((RenderPass) pass, state);
    }
}
