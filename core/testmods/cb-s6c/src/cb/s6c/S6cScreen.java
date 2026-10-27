package cb.s6c;

import com.mojang.blaze3d.systems.RenderSystem;
import java.util.List;
import net.minecraft.class_1041;
import net.minecraft.class_1799;
import net.minecraft.class_1802;
import net.minecraft.class_1921;
import net.minecraft.class_2561;
import net.minecraft.class_332;
import net.minecraft.class_437;
import net.minecraft.class_7529;
import net.minecraft.class_8001;
import net.minecraft.class_8519;

/**
 * The test screen, one mode per screenshot:
 *   1  GuiGraphics.fill(RenderType.endPortal()) over the whole screen; a white "UNDER" string, flush(), then a blue
 *      box drawn over it (the box must cover the string); renderItem(stack, x, y, seed, z); renderTooltip(font,
 *      lines, positioner, x, y); a MultiLineEditBox; SplashRenderer.render with the masks 0xFF000000, 0x80000000, 0
 *   2  renderBlurredBackground() over the world, setTooltipForNextRenderPass("cb") every frame
 *   3  Screen.PANORAMA.render, setTooltipForNextRenderPass("cb") then clearTooltipForNextRenderPass() (no tooltip)
 *   4  scissor: RenderSystem.enableScissor over the GUI rectangle (20,20)-(80,80) in framebuffer pixels (1.21.5's
 *      GuiGraphics.applyScissor formula), a red fill of the whole screen, flush(), RenderSystem.disableScissor(); then
 *      GuiGraphics.enableScissor(100, 20, 160, 80), a green fill of the whole screen, disableScissor(); then an
 *      unclipped blue box (100,100)-(120,120). Red must show only inside the first rectangle, green only inside the
 *      second (S6cClient reads the pixels).
 * The default background is switched off, so the blur and the panorama come only from the calls under test.
 */
public class S6cScreen extends class_437 {
    static final int[] SPLASH_MASKS = {0xFF000000, 0x80000000, 0};
    final int mode;
    class_7529 box;
    volatile int frames;

    S6cScreen(int mode) {
        super(class_2561.method_43470("cb-s6c " + mode));
        this.mode = mode;
    }

    @Override
    protected void method_25426() {
        if (mode == 1) {
            box = new class_7529(field_22793, 10, 130, 170, 60, class_2561.method_43470("placeholder"), class_2561.method_43470("cb box"));
            box.method_44400("multi line\nedit box");
            method_37063(box);
        }
    }

    @Override
    public void method_25420(class_332 g, int mx, int my, float delta) {
    }

    @Override
    public void method_25394(class_332 g, int mx, int my, float delta) {
        if (mode == 1) {
            g.method_51739(class_1921.method_23574(), 0, 0, field_22789, field_22790, 0);
            g.method_51433(field_22793, "UNDER", 200, 20, 0xFFFFFFFF, true);
            g.method_51452();
            g.method_25294(195, 15, 240, 32, 0xFF2040FF);
            g.method_51429(new class_1799(class_1802.field_8477), 200, 50, 7, 0);
            g.method_51436(field_22793, List.of(class_2561.method_43470("cb positioned tooltip").method_30937()),
                class_8001.field_41687, 220, 60);
            for (int i = 0; i < SPLASH_MASKS.length; i++) {
                g.method_51448().method_22903();
                g.method_51448().method_46416(0, 30 * i, 0);
                new class_8519("cb splash " + i).method_51453(g, field_22789, field_22793, SPLASH_MASKS[i]);
                g.method_51448().method_22909();
            }
        } else if (mode == 2) {
            method_57734();
            g.method_51433(field_22793, "mode 2: blurred world, tooltip cb at the mouse", 10, 10, 0xFFFFFFFF, true);
            method_47415(class_2561.method_43470("cb"));
        } else if (mode == 4) {
            class_1041 window = field_22787.method_22683();
            int scale = (int) window.method_4495();
            int h = window.method_4506();
            RenderSystem.enableScissor(20 * scale, h - 80 * scale, 60 * scale, 60 * scale);
            g.method_25294(0, 0, field_22789, field_22790, 0xFFFF0000);
            g.method_51452();
            RenderSystem.disableScissor();
            g.method_44379(100, 20, 160, 80);
            g.method_25294(0, 0, field_22789, field_22790, 0xFF00FF00);
            g.method_44380();
            g.method_25294(100, 100, 120, 120, 0xFF0000FF);
        } else {
            class_437.field_49509.method_3317(g, field_22789, field_22790, 1f, 0f);
            g.method_51433(field_22793, "mode 3: panorama, no tooltip", 10, 10, 0xFFFFFFFF, true);
            method_47415(class_2561.method_43470("cb"));
            method_59840();
        }
        super.method_25394(g, mx, my, delta);
        frames++;
    }
}
