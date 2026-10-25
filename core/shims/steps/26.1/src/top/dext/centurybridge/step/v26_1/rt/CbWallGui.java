package top.dext.centurybridge.step.v26_1.rt;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.network.chat.Style;
import top.dext.centurybridge.step.v26_1.mixin.CbWallGuiAccess;

/**
 * 1.21.11's GuiGraphics members that 26.1's GuiGraphicsExtractor no longer has in that shape.
 * <ul>
 * <li>renderDeferredElements() became extractDeferredElements(mouseX, mouseY, partialTick). 1.21.11
 *     read the mouse position the GuiGraphics was built with (its mouseX / mouseY fields, which 26.1
 *     keeps); the partial tick is new and only drives the IME pre-edit overlay 26.1 added. It is
 *     what 26.1's GameRenderer hands Screen.extractRenderStateWithTooltipAndSubtitles, which passes
 *     it on to extractDeferredElements: the DeltaTracker's game-time partial tick.</li>
 * <li>renderComponentHoverEffect(font, style, x, y) was public and returned on a null style; 26.1's
 *     componentHoverEffect is private (the layer's access widener makes it overridable) and its only
 *     caller tests for null first, so the null test stays here.</li>
 * <li>GuiRenderer.incrementFrameNumber() counted the frames 1.21.11's GUI item rendering compared to know which
 *     animated items to draw again; 26.1 moved that frame count into the GUI item atlas, which GuiRenderer.endFrame()
 *     advances (vanilla calls it once a frame where 1.21.11 called incrementFrameNumber);</li>
 * <li>OptionsScreen(lastScreen, options): 26.1 takes whether the options are opened in a world; 1.21.11's screen
 *     decided it from the client level (the world-options / difficulty row shown when a level is loaded).</li>
 * </ul>
 */
@Environment(EnvType.CLIENT)
public final class CbWallGui {

    private CbWallGui() {
    }

    /** 1.21.11 GuiGraphics.renderDeferredElements() */
    public static void renderDeferredElements(GuiGraphicsExtractor g) {
        g.extractDeferredElements(mouseX(g), mouseY(g), partialTick());
    }

    /** 1.21.11 GuiGraphics.renderComponentHoverEffect(Font, Style, int, int) */
    public static void renderComponentHoverEffect(GuiGraphicsExtractor g, Font font, Style style, int x, int y) {
        if (style == null) {
            return;
        }
        ((CbWallGuiAccess) g).cb$componentHoverEffect(font, style, x, y);
    }

    /** frame default of extractDeferredElements' mouseX: the position the GuiGraphics was built with */
    public static int mouseX(GuiGraphicsExtractor g) {
        return ((CbWallGuiAccess) g).cb$mouseX();
    }

    /** frame default of extractDeferredElements' mouseY */
    public static int mouseY(GuiGraphicsExtractor g) {
        return ((CbWallGuiAccess) g).cb$mouseY();
    }

    /** frame default of extractDeferredElements' partialTick: what 26.1's GameRenderer passes the screen */
    public static float partialTick() {
        return Minecraft.getInstance().getDeltaTracker().getGameTimeDeltaTicks();
    }

    /** 1.21.11 GuiRenderer.incrementFrameNumber() */
    public static void incrementFrameNumber(net.minecraft.client.gui.render.GuiRenderer renderer) {
        renderer.endFrame();
    }

    /** 1.21.11 OptionsScreen's in-world test: a client level is loaded */
    public static boolean optionsInWorld() {
        return Minecraft.getInstance().level != null;
    }
}
