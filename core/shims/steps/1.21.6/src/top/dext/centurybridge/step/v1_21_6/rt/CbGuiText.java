package top.dext.centurybridge.step.v1_21_6.rt;

/**
 * 1.21.5's GuiGraphics.drawString family on 1.21.6. Two things changed besides the void return:
 * <ul>
 * <li>1.21.5 drew through Font.drawInBatch, which made a colour with no alpha (the top six bits clear) opaque
 *     (Font.adjustColor: ARGB.opaque); 1.21.6's drawString skips a colour whose alpha is 0. A 1.21.5 caller
 *     passing 0xFFFFFF wrote white text, so the colour is adjusted as 1.21.5 did before 1.21.6 draws it;</li>
 * <li>1.21.5 answered drawInBatch's answer: the x where the text ended, (int) (x + its advance), plus one with a
 *     shadow (Font.drawInternal); the advance is the StringSplitter width of the same text (the glyph advances
 *     drawInBatch summed, bold included; bidi reordering does not change it).</li>
 * </ul>
 */
@net.fabricmc.api.Environment(net.fabricmc.api.EnvType.CLIENT)
public final class CbGuiText {

    private CbGuiText() {
    }

    /** Font.adjustColor of 1.21.5 */
    public static int adjust(int color) {
        return (color & 0xFC000000) == 0 ? net.minecraft.class_9848.method_61334(color) : color;
    }

    private static int end(int x, float advance, boolean shadow) {
        return (int) (x + advance) + (shadow ? 1 : 0);
    }

    // drawString(Font, String, x, y, color[, shadow])
    public static int method_51433(net.minecraft.class_332 g, net.minecraft.class_327 font, String text, int x, int y,
            int color, boolean shadow) {
        if (text == null) {
            return 0;
        }
        g.method_51433(font, text, x, y, adjust(color), shadow);
        return end(x, font.method_27527().method_27482(font.method_1726() ? font.method_1721(text) : text), shadow);
    }

    public static int method_25303(net.minecraft.class_332 g, net.minecraft.class_327 font, String text, int x, int y, int color) {
        return method_51433(g, font, text, x, y, color, true);
    }

    // drawString(Font, FormattedCharSequence, x, y, color[, shadow])
    public static int method_51430(net.minecraft.class_332 g, net.minecraft.class_327 font, net.minecraft.class_5481 text,
            int x, int y, int color, boolean shadow) {
        g.method_51430(font, text, x, y, adjust(color), shadow);
        return end(x, font.method_27527().method_30875(text), shadow);
    }

    public static int method_35720(net.minecraft.class_332 g, net.minecraft.class_327 font, net.minecraft.class_5481 text,
            int x, int y, int color) {
        return method_51430(g, font, text, x, y, color, true);
    }

    // drawString(Font, Component, x, y, color[, shadow]): 1.21.5 drew its visual order text
    public static int method_51439(net.minecraft.class_332 g, net.minecraft.class_327 font, net.minecraft.class_2561 text,
            int x, int y, int color, boolean shadow) {
        return method_51430(g, font, text.method_30937(), x, y, color, shadow);
    }

    public static int method_27535(net.minecraft.class_332 g, net.minecraft.class_327 font, net.minecraft.class_2561 text,
            int x, int y, int color) {
        return method_51439(g, font, text, x, y, color, true);
    }

    /** drawStringWithBackdrop: 1.21.5's body (the backdrop on the raw colour, then the shadowed string) */
    public static int method_60649(net.minecraft.class_332 g, net.minecraft.class_327 font, net.minecraft.class_2561 text,
            int x, int y, int width, int color) {
        int opacity = net.minecraft.class_310.method_1551().field_1690.method_19345(0.0F);
        if (opacity != 0) {
            g.method_25294(x - 2, y - 2, x + width + 2, y + 9 + 2, net.minecraft.class_9848.method_61322(opacity, color));
        }
        return method_51439(g, font, text, x, y, color, true);
    }

    // ---- a mod's super.drawString(..) in a GuiGraphics subclass: super's 1.21.6 method, then 1.21.5's answer ----

    public interface SuperString {
        void cb$super$method_51433(net.minecraft.class_327 font, String text, int x, int y, int color, boolean shadow);
    }

    public interface SuperSequence {
        void cb$super$method_51430(net.minecraft.class_327 font, net.minecraft.class_5481 text, int x, int y, int color, boolean shadow);
    }

    public interface SuperComponent {
        void cb$super$method_51439(net.minecraft.class_327 font, net.minecraft.class_2561 text, int x, int y, int color, boolean shadow);
    }

    public interface SuperStringShadow {
        void cb$super$method_25303(net.minecraft.class_327 font, String text, int x, int y, int color);
    }

    public interface SuperSequenceShadow {
        void cb$super$method_35720(net.minecraft.class_327 font, net.minecraft.class_5481 text, int x, int y, int color);
    }

    public interface SuperComponentShadow {
        void cb$super$method_27535(net.minecraft.class_327 font, net.minecraft.class_2561 text, int x, int y, int color);
    }

    public interface SuperBackdrop {
        void cb$super$method_60649(net.minecraft.class_327 font, net.minecraft.class_2561 text, int x, int y, int width, int color);
    }

    public static int superStringShadow(SuperStringShadow g, net.minecraft.class_327 font, String text, int x, int y, int color) {
        if (text == null) {
            return 0;
        }
        g.cb$super$method_25303(font, text, x, y, adjust(color));
        return end(x, font.method_27527().method_27482(font.method_1726() ? font.method_1721(text) : text), true);
    }

    public static int superSequenceShadow(SuperSequenceShadow g, net.minecraft.class_327 font, net.minecraft.class_5481 text,
            int x, int y, int color) {
        g.cb$super$method_35720(font, text, x, y, adjust(color));
        return end(x, font.method_27527().method_30875(text), true);
    }

    public static int superComponentShadow(SuperComponentShadow g, net.minecraft.class_327 font, net.minecraft.class_2561 text,
            int x, int y, int color) {
        g.cb$super$method_27535(font, text, x, y, adjust(color));
        return end(x, font.method_27527().method_30875(text.method_30937()), true);
    }

    /** super's backdrop string: 1.21.6's draws the backdrop from the colour as 1.21.5 did, the text needs the adjusted one */
    public static int superBackdrop(SuperBackdrop g, net.minecraft.class_327 font, net.minecraft.class_2561 text,
            int x, int y, int width, int color) {
        int opacity = net.minecraft.class_310.method_1551().field_1690.method_19345(0.0F);
        if (opacity != 0) {
            ((net.minecraft.class_332) (Object) g).method_25294(x - 2, y - 2, x + width + 2, y + 9 + 2,
                net.minecraft.class_9848.method_61322(opacity, color));
        }
        return method_51439((net.minecraft.class_332) (Object) g, font, text, x, y, color, true);
    }

    public static int superString(SuperString g, net.minecraft.class_327 font, String text, int x, int y, int color, boolean shadow) {
        if (text == null) {
            return 0;
        }
        g.cb$super$method_51433(font, text, x, y, adjust(color), shadow);
        return end(x, font.method_27527().method_27482(font.method_1726() ? font.method_1721(text) : text), shadow);
    }

    public static int superSequence(SuperSequence g, net.minecraft.class_327 font, net.minecraft.class_5481 text, int x, int y,
            int color, boolean shadow) {
        g.cb$super$method_51430(font, text, x, y, adjust(color), shadow);
        return end(x, font.method_27527().method_30875(text), shadow);
    }

    public static int superComponent(SuperComponent g, net.minecraft.class_327 font, net.minecraft.class_2561 text, int x, int y,
            int color, boolean shadow) {
        g.cb$super$method_51439(font, text, x, y, adjust(color), shadow);
        return end(x, font.method_27527().method_30875(text.method_30937()), shadow);
    }
}
