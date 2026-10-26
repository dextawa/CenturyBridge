package top.dext.centurybridge.step.v1_21_6.rt;

/**
 * Screen / AbstractContainerScreen statics of 1.21.5 that 1.21.6 no longer declares.
 * <ul>
 * <li>Screen.CUBE_MAP and Screen.PANORAMA: 1.21.5 built them in Screen's static initializer (a CubeMap of
 *     textures/gui/title/background/panorama and a PanoramaRenderer of it, javap); 1.21.6 builds the same two in
 *     GameRenderer (field_60579 / field_60580, the title screen and the other menu screens draw that one), so these are
 *     the game renderer's.</li>
 * <li>AbstractContainerScreen.SLOT_ITEM_BLIT_OFFSET: the constant 100 (1.21.5 javap).</li>
 * <li>Screen.method_57734 / ScreenRectangle.transformAxisAligned(Matrix4f): see CbScreenTooltipMixin and
 *     {@link #guiMatrix}.</li>
 * </ul>
 */
@net.fabricmc.api.Environment(net.fabricmc.api.EnvType.CLIENT)
public final class CbScreens {

    private CbScreens() {
    }

    /** Screen.CUBE_MAP */
    public static net.minecraft.class_751 field_49508() {
        return net.minecraft.class_310.method_1551().field_1773.field_60579;
    }

    /** Screen.PANORAMA */
    public static net.minecraft.class_766 field_49509() {
        return net.minecraft.class_310.method_1551().field_1773.method_71649();
    }

    /** AbstractContainerScreen.SLOT_ITEM_BLIT_OFFSET */
    public static int field_32322() {
        return 100;
    }

    /**
     * ScreenRectangle.transformAxisAligned(Matrix4f): 1.21.5 transformed the corners as (x, y, 0) positions, so only the
     * matrix's XY part acts: x' = m00 x + m10 y + m30, y' = m01 x + m11 y + m31 -- 1.21.6's Matrix3x2f of the same
     * entries (1.21.5's identity shortcut gives what the identity gives).
     */
    public static org.joml.Matrix3x2f guiMatrix(org.joml.Matrix4f m) {
        return new org.joml.Matrix3x2f(m.m00(), m.m01(), m.m10(), m.m11(), m.m30(), m.m31());
    }

    /** the GuiGraphics the current screen's renderWithTooltip draws with (CbScreenTooltipMixin records it), else null */
    private static net.minecraft.class_332 screenFrame;

    /** CbScreenTooltipMixin: renderWithTooltip's GuiGraphics at its HEAD, null at its RETURN */
    public static void screenFrame(net.minecraft.class_332 graphics) {
        screenFrame = graphics;
    }

    /**
     * frameDefaults of WidgetTooltipHolder.refreshTooltipForNextRenderPass's GuiGraphics, when a 1.21.5 override entered
     * the old (hovered, focused, rect) shape itself. 1.21.5's body handed the tooltip to the current screen
     * (Minecraft.screen.setTooltipForNextRenderPass, nothing without a screen), which drew it at the mouse at the end of
     * its renderWithTooltip; 1.21.6's hands it to the GuiGraphics it is given, whose renderDeferredTooltip draws it at the
     * end of the same renderWithTooltip. So: that call's GuiGraphics; outside it (no screen rendering) a GuiGraphics over
     * the frame's GUI render state whose deferred tooltip nothing draws -- 1.21.5 drew nothing there either.
     */
    public static net.minecraft.class_332 screenGraphics() {
        net.minecraft.class_332 g = screenFrame;
        if (g != null) {
            return g;
        }
        net.minecraft.class_310 mc = net.minecraft.class_310.method_1551();
        return new net.minecraft.class_332(mc, mc.field_1773.field_59966);
    }

    /**
     * frameDefaults of Screen.renderBlurredBackground's GuiGraphics, when a 1.21.5 override entered the old no-argument
     * shape itself: 1.21.5's body was gameRenderer.processBlurEffect() at once; 1.21.6's marks the frame's GUI render
     * state to blur before the current stratum (GuiGraphics.blurBeforeThisStratum, which processBlurEffect then runs).
     * So the GuiGraphics over the frame's GUI render state. 1.21.6 blurs a frame at most once (GuiRenderState throws
     * "Can only blur once per frame" on a second request); when the frame is already marked, the request is already
     * met, and it gets a GuiGraphics over a render state of its own, which no frame draws.
     */
    public static net.minecraft.class_332 blurGraphics() {
        net.minecraft.class_310 mc = net.minecraft.class_310.method_1551();
        net.minecraft.class_11246 state = mc.field_1773.field_59966;
        if (state.field_60314 != Integer.MAX_VALUE) {
            return new net.minecraft.class_332(mc, new net.minecraft.class_11246());
        }
        net.minecraft.class_332 g = screenFrame;
        return g != null ? g : new net.minecraft.class_332(mc, state);
    }

    /** frameDefaults of WidgetTooltipHolder.refreshTooltipForNextRenderPass's mouse x: GameRenderer's (the scaled cursor) */
    public static int mouseX() {
        net.minecraft.class_310 mc = net.minecraft.class_310.method_1551();
        return (int) mc.field_1729.method_68879(mc.method_22683());
    }

    /** frameDefaults of WidgetTooltipHolder.refreshTooltipForNextRenderPass's mouse y: GameRenderer's (the scaled cursor) */
    public static int mouseY() {
        net.minecraft.class_310 mc = net.minecraft.class_310.method_1551();
        return (int) mc.field_1729.method_68883(mc.method_22683());
    }

    /**
     * frameDefaults of DebugScreenOverlay.render3dCrosshair's Camera: 1.21.5's body drew with the model-view its caller
     * (Gui.renderCrosshair) had rotated by gameRenderer.getMainCamera(); 1.21.6's rotates by the camera it is given, and
     * GameRenderer.renderLevel passes its main camera (field_18765, what getMainCamera answers; javap of both).
     */
    public static net.minecraft.class_4184 mainCamera() {
        return net.minecraft.class_310.method_1551().field_1773.method_19418();
    }
}
