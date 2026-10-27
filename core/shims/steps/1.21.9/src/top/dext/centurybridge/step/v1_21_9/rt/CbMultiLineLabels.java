package top.dext.centurybridge.step.v1_21_9.rt;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.class_2583;
import net.minecraft.class_332;
import net.minecraft.class_5489;

/**
 * MultiLineLabel's 1.21.8 render and style-lookup methods. 1.21.9 folded them into one render(graphics, align, x, y,
 * lineHeight, shadow, color) answering the y below the last line, and getStyle(align, x, y, lineHeight, mouseX,
 * mouseY) (javap: the same line loop and the same x per alignment -- 1.21.8 centred at x - width / 2, 1.21.9's CENTER
 * answers that; LEFT answers x). 1.21.8's renderCentered(graphics, x, y) used the font's line height (9) and white.
 */
@Environment(EnvType.CLIENT)
public final class CbMultiLineLabels {

    private CbMultiLineLabels() {
    }

    /** renderCentered(graphics, x, y) */
    public static void method_30888(class_5489 label, class_332 graphics, int x, int y) {
        label.method_73212(graphics, class_5489.class_11735.field_62010, x, y, 9, true, -1);
    }

    /** renderCentered(graphics, x, y, lineHeight, color) */
    public static void method_30889(class_5489 label, class_332 graphics, int x, int y, int lineHeight, int color) {
        label.method_73212(graphics, class_5489.class_11735.field_62010, x, y, lineHeight, true, color);
    }

    /** renderLeftAligned(graphics, x, y, lineHeight, color) */
    public static void method_30893(class_5489 label, class_332 graphics, int x, int y, int lineHeight, int color) {
        label.method_73212(graphics, class_5489.class_11735.field_62009, x, y, lineHeight, true, color);
    }

    /** renderLeftAlignedNoShadow(graphics, x, y, lineHeight, color): the y below the last line */
    public static int method_30896(class_5489 label, class_332 graphics, int x, int y, int lineHeight, int color) {
        return label.method_73212(graphics, class_5489.class_11735.field_62009, x, y, lineHeight, false, color);
    }

    /** getStyleAtCentered(x, y, lineHeight, mouseX, mouseY) */
    public static class_2583 method_72103(class_5489 label, int x, int y, int lineHeight, double mouseX, double mouseY) {
        return label.method_72103(class_5489.class_11735.field_62010, x, y, lineHeight, mouseX, mouseY);
    }

    /** getStyleAtLeftAligned(x, y, lineHeight, mouseX, mouseY) */
    public static class_2583 method_72104(class_5489 label, int x, int y, int lineHeight, double mouseX, double mouseY) {
        return label.method_72103(class_5489.class_11735.field_62009, x, y, lineHeight, mouseX, mouseY);
    }
}
