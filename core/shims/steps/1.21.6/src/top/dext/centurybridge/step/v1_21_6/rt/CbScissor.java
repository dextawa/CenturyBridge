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

    /*
     * RenderSystem.enableScissor / disableScissor, called by a converted 1.21.5 mod.
     *
     * 1.21.5 (javap): enableScissor(x, y, w, h) set SCISSOR_STATE, which every draw then copied into its pass -- the
     * RenderType draws and GuiGraphics' draws alike, because 1.21.5's GuiGraphics drew through RenderTypes
     * (GuiGraphics.applyScissor itself was flush() then RenderSystem.enableScissor(left * scale, height - bottom *
     * scale, width * scale, height * scale), or disableScissor for no rectangle). The scissor in force was the last of
     * those writes, whoever made it.
     *
     * 1.21.6 (javap): RenderType draws read scissorStateForRenderTypeDraws (enableScissorForRenderTypeDraws /
     * disableScissorForRenderTypeDraws, the same rectangle in framebuffer pixels); GuiGraphics no longer draws -- each
     * element it submits to the frame's GuiRenderState records GuiGraphics.ScissorStack.peek() (GUI units), and
     * GuiRenderer turns that back into framebuffer pixels with 1.21.5's formula (method_70884: left * scale,
     * height - bottom * scale, width * scale, height * scale; Window.getGuiScale() / getHeight()).
     *
     * So the call keeps 1.21.5's render-type half (W's own method, so mixins on it run), and while the frame's GUI is
     * being built -- from GameRenderer.render's GuiRenderState.reset() (CbGuiScissorMixin) -- it also stands as the
     * scissor GuiGraphics' elements record: ScissorStack.peek() answers it until GuiGraphics.enableScissor / disableScissor changes its scissor stack (CbGuiScissorGraphicsMixin)
     * (1.21.5: GuiGraphics.enableScissor / disableScissor re-applied the stack's top, replacing it). With no converted
     * call in the frame peek answers what 1.21.6 answers.
     */

    /** the frame's GUI elements take {@link #guiRect} as their scissor (set only by a converted call) */
    private static boolean guiOverride;

    /** the rectangle a converted RenderSystem.enableScissor made, in GUI units; null after disableScissor */
    private static net.minecraft.class_8030 guiRect;

    /** RenderSystem.enableScissor(x, y, width, height), framebuffer pixels from the bottom left */
    public static void enableScissor(int x, int y, int width, int height) {
        com.mojang.blaze3d.systems.RenderSystem.enableScissorForRenderTypeDraws(x, y, width, height);
        guiRect = guiRectangle(x, y, width, height);
        guiOverride = true;
    }

    /** RenderSystem.disableScissor() */
    public static void disableScissor() {
        com.mojang.blaze3d.systems.RenderSystem.disableScissorForRenderTypeDraws();
        guiRect = null;
        guiOverride = true;
    }

    /**
     * 1.21.5's applyScissor inverted: the GUI rectangle GuiRenderer maps back onto these pixels. A rectangle a 1.21.5
     * mod computed from GUI coordinates (multiples of the scale) comes back exactly; an edge between two GUI pixels
     * widens to the GUI pixel containing it (1.21.6's GUI scissor has GUI-pixel resolution).
     */
    static net.minecraft.class_8030 guiRectangle(int x, int y, int width, int height) {
        net.minecraft.class_1041 window = net.minecraft.class_310.method_1551().method_22683();
        int scale = Math.max(1, window.method_4495());
        int h = window.method_4506();
        int left = Math.floorDiv(x, scale);
        int right = -Math.floorDiv(-(x + Math.max(0, width)), scale);
        int top = Math.floorDiv(h - (y + Math.max(0, height)), scale);
        int bottom = -Math.floorDiv(-(h - y), scale);
        return new net.minecraft.class_8030(left, top, right - left, bottom - top);
    }

    /** CbGuiScissorMixin: the frame's GUI starts being built (GuiRenderState.reset), with 1.21.6's scissor */
    public static void guiFrameStart() {
        guiOverride = false;
        guiRect = null;
    }

    /** CbGuiScissorGraphicsMixin: GuiGraphics.enableScissor / disableScissor (its stack push / pop), whose top is the scissor again */
    public static void guiStackChanged() {
        guiOverride = false;
    }

    /** whether a converted RenderSystem scissor call stands for the frame's GUI elements */
    public static boolean guiOverride() {
        return guiOverride;
    }

    /** that call's rectangle (null: no scissor) */
    public static net.minecraft.class_8030 guiRect() {
        return guiRect;
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
