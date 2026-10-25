package top.dext.centurybridge.step.v1_21_6.rt;

/**
 * 1.21.5 widget calls whose 1.21.6 counterparts take more arguments; each value is what 1.21.5 used (javap of both):
 * <ul>
 * <li>MultilineTextField.setValue(String): 1.21.6's setValue(String, boolean force) skips a value overflowing its new
 *     line limit unless forced; 1.21.5 had no limit and always set it, so force is true.</li>
 * <li>MultiLineEditBox(font, x, y, w, h, placeholder, message): 1.21.5 drew its text in -2039584 with a shadow, the
 *     cursor in -3092272, with background and decorations; 1.21.6's builder defaults are the same values, which its
 *     constructor now takes.</li>
 * <li>ToastManager(Minecraft): 1.21.6 also takes the options it reads the notification setting from -- the game's own
 *     (Minecraft passes its options).</li>
 * <li>SplashRenderer.render(g, width, font, alphaMask): 1.21.5 drew 0xFFFF00 | alphaMask through drawCenteredString,
 *     whose Font.adjustColor made an alpha below 4 opaque; 1.21.6 takes the alpha as a float and draws
 *     ARGB.color(alpha, 0xFFFFFF00). The float is the mask's alpha byte / 255, opaque where 1.21.5 made it opaque
 *     (the mask's low bits, which no 1.21.5 caller set, only coloured the blue channel and are not carried).</li>
 * </ul>
 */
@net.fabricmc.api.Environment(net.fabricmc.api.EnvType.CLIENT)
public final class CbWidgets {

    private CbWidgets() {
    }

    public static boolean force() {
        return true;
    }

    public static int editTextColor() {
        return -2039584;
    }

    public static boolean editTextShadow() {
        return true;
    }

    public static int editCursorColor() {
        return -3092272;
    }

    public static boolean editBackground() {
        return true;
    }

    public static boolean editDecorations() {
        return true;
    }

    public static net.minecraft.class_315 options(net.minecraft.class_310 minecraft) {
        return minecraft.field_1690;
    }

    public static float splashAlpha(int alphaMask) {
        int alpha = (alphaMask & 0xFC000000) == 0 ? 255 : alphaMask >>> 24;
        return alpha / 255.0F;
    }
}
