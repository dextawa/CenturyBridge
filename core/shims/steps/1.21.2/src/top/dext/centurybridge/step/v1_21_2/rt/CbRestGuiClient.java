package top.dext.centurybridge.step.v1_21_2.rt;

import java.util.function.Function;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.class_1921;
import net.minecraft.class_2960;

/**
 * Client-side values the s7rest GUI bridges hand to 1.21.2 where 1.21.1 had no such argument.
 */
@Environment(EnvType.CLIENT)
public final class CbRestGuiClient {

    private CbRestGuiClient() {
    }

    /**
     * The render type of a GUI blit when a 1.21.1 GuiGraphics subclass's own blit / blitSprite override calls
     * super outside a draw vanilla started (frameDefaults): 1.21.1 drew every such blit as a textured quad in
     * the GUI's state, which is 1.21.2's RenderType.guiTextured -- the type 1.21.2's own GUI passes for them.
     */
    public static Function<class_2960, class_1921> guiTextured() {
        return class_1921::method_62277;
    }

    /** RecipeButton.TICKS_TO_SWAP: 30 */
    public static int field_32413() {
        return 30;
    }

    /**
     * ReportReason.GENERIC ("I want to report them"): 1.21.2 renamed the constant I_WANT_TO_REPORT_THEM, the
     * same reason with the same text.
     */
    public static net.minecraft.class_7573 field_46063() {
        return net.minecraft.class_7573.field_53035;
    }

    /**
     * ParticleRenderType.PARTICLE_SHEET_LIT: in 1.21.1 the opaque particle sheet without its own shader call
     * (the engine set the particle shader for every sheet), drawn right after PARTICLE_SHEET_OPAQUE in the
     * same state. 1.21.2 dropped it; its particles are the opaque sheet's.
     */
    public static net.minecraft.class_3999 field_17830() {
        return net.minecraft.class_3999.field_17828;
    }

    // ------------------------------------------------------------------ tooltip components (class_5684)

    /** the game's font, which 1.21.1's ClientTooltipComponent.getHeight() did not take and 1.21.2's does */
    public static net.minecraft.class_327 font() {
        return net.minecraft.class_310.method_1551().field_1772;
    }

    /** ClientTooltipComponent.getHeight(): 1.21.2's getHeight(font) with the game's font (vanilla draws tooltips in it) */
    public static int tooltipHeight(net.minecraft.class_5684 component) {
        return component.method_32661(font());
    }

    /**
     * ClientTooltipComponent.renderImage(font, x, y, graphics): 1.21.2 also hands the component the size it
     * is drawn at, which vanilla's tooltip renderer takes from the component's own getWidth / getHeight --
     * the size a 1.21.1 component drew itself at
     */
    public static void renderImage(net.minecraft.class_5684 component, net.minecraft.class_327 font, int x, int y,
            net.minecraft.class_332 graphics) {
        component.method_32666(font, x, y, component.method_32664(font), component.method_32661(font), graphics);
    }

    /** frameDefaults: the width a component entered directly is drawn at (its own getWidth, as vanilla) */
    public static int tooltipWidthDefault(net.minecraft.class_5684 component, net.minecraft.class_327 font, int x, int y,
            net.minecraft.class_332 graphics) {
        return component.method_32664(font);
    }

    /** frameDefaults: the height a component entered directly is drawn at (its own getHeight, as vanilla) */
    public static int tooltipHeightDefault(net.minecraft.class_5684 component, net.minecraft.class_327 font, int x, int y,
            net.minecraft.class_332 graphics) {
        return component.method_32661(font);
    }

    /** TooltipRenderUtil.renderTooltipBackground(graphics, x, y, width, height, z): the default style (null) */
    public static void method_47946(net.minecraft.class_332 graphics, int x, int y, int width, int height, int z) {
        net.minecraft.class_8002.method_47946(graphics, x, y, width, height, z, null);
    }

    // ------------------------------------------------------------------ spectator menu icons (class_537)

    /** SpectatorMenuItem.renderIcon's alpha: 1.21.1's SpectatorGui passed (int) (alpha * 255), 1.21.2 the float */
    public static int iconAlphaInt(float alpha) {
        return (int) (alpha * 255.0F);
    }

    /** a 1.21.1 caller's 0..255 alpha as 1.21.2's 0..1 */
    public static float iconAlphaFloat(int alpha) {
        return alpha / 255.0F;
    }

    // ------------------------------------------------------------------ screens

    /**
     * Screen.wrapScreenError(task, errorDescription, screenName): the task run, any throwable reported as a
     * crash with the screen's name in the "Affected screen" category, as 1.21.1
     */
    public static void method_25412(Runnable task, String errorDescription, String screenName) {
        try {
            task.run();
        } catch (Throwable t) {
            net.minecraft.class_128 report = net.minecraft.class_128.method_560(t, errorDescription);
            report.method_562("Affected screen").method_577("Screen name", () -> screenName);
            throw new net.minecraft.class_148(report);
        }
    }

    /** NineSlice(width, height, border): 1.21.1 tiled the centre (1.21.2's stretchInner false) */
    public static boolean tiledCentre() {
        return false;
    }

    /** a 1.21.1 screen overriding renderBlurredBackground(partialTick) (bridge adapter) */
    public interface LegacyBlur {
        void method_57734(float partialTick);
    }

    /** super's 1.21.2 renderBlurredBackground(), from a 1.21.1 screen's super call */
    public interface BlurSuper {
        void cb$super$method_57734();
    }

    /** the partial tick screens render with (GameRenderer hands Screen.render the delta tracker's game-time ticks) */
    static float screenPartialTick() {
        return net.minecraft.class_310.method_1551().method_61966().method_60636();
    }

    /**
     * 1.21.2's renderBlurredBackground() of a screen whose 1.21.1 class overrides renderBlurredBackground(partialTick):
     * its override, with the partial tick 1.21.1's renderBackground passed (the one the screen renders with)
     */
    public static void blur(LegacyBlur screen) {
        screen.method_57734(screenPartialTick());
    }

    /** a 1.21.1 screen's super.renderBlurredBackground(partialTick): 1.21.2's blur, which takes no partial tick */
    public static void superBlur(BlurSuper screen, float partialTick) {
        screen.cb$super$method_57734();
    }

    /** screen.renderBlurredBackground(partialTick) called by a 1.21.1 screen: 1.21.2's, through the screen */
    public static void renderBlurredBackground(net.minecraft.class_437 screen, float partialTick) {
        if (screen instanceof LegacyBlur legacy) {
            legacy.method_57734(partialTick);
        } else {
            ((top.dext.centurybridge.step.v1_21_2.mixin.CbRestScreenInvoker) screen).cb$renderBlurredBackground();
        }
    }

    // ------------------------------------------------------------------ sign and book screens

    /** a 1.21.1 sign edit screen implementing renderSignBackground(graphics, state) (bridge adapter) */
    public interface LegacySignBackground {
        void method_45656(net.minecraft.class_332 graphics, net.minecraft.class_2680 state);
    }

    /**
     * 1.21.2's renderSignBackground(graphics) of a 1.21.1 sign screen: its own, with the state 1.21.1's renderSign
     * passed (the edited sign's block state)
     */
    public static void signBackground(LegacySignBackground screen, net.minecraft.class_332 graphics) {
        net.minecraft.class_2625 sign = ((top.dext.centurybridge.step.v1_21_2.mixin.CbRestSignEditAccessor) screen).cb$sign();
        screen.method_45656(graphics, sign.method_11010());
    }

    /** super's 1.21.2 renderSignBackground(graphics), from a 1.21.1 subclass's super call */
    public interface SignBackgroundSuper {
        void cb$super$method_45656(net.minecraft.class_332 graphics);
    }

    /** a 1.21.1 subclass's super.renderSignBackground(graphics, state): 1.21.2's, which reads the sign's state itself */
    public static void superSignBackground(SignBackgroundSuper screen, net.minecraft.class_332 graphics, net.minecraft.class_2680 state) {
        screen.cb$super$method_45656(graphics);
    }

    /** BookEditScreen's content: 1.21.1's constructor read the stack's writable book content (no pages when it had none) */
    public static net.minecraft.class_9301 bookContent(net.minecraft.class_1799 stack) {
        return stack.method_57825(net.minecraft.class_9334.field_49653, net.minecraft.class_9301.field_49369);
    }

    /**
     * ReportReasonSelectionScreen's report type: 1.21.2 leaves out the reasons a type excludes; 1.21.1 offered every
     * reason, as for USERNAME, which excludes none
     */
    public static net.minecraft.class_8776 everyReason() {
        return net.minecraft.class_8776.field_46066;
    }

    /** AbstractContainerScreen.renderSlotHighlight(graphics, x, y, z): 1.21.1's body, the translucent white overlay */
    public static void method_33285(net.minecraft.class_332 graphics, int x, int y, int z) {
        graphics.method_51740(net.minecraft.class_1921.method_51785(), x, y, x + 16, y + 16, -2130706433, -2130706433, z);
    }
}
