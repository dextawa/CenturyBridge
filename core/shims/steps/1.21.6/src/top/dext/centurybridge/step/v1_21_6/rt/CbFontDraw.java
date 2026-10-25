package top.dext.centurybridge.step.v1_21_6.rt;

/**
 * 1.21.5's Font.drawInBatch family on 1.21.6, which answers nothing. 1.21.5 (javap):
 * <ul>
 * <li>drawInBatch(String, ..) shaped bidirectional text, then drawInternal; drawInBatch(Component, ..) was
 *     drawInBatch(Component, .., inverseDepth true) and that drew the component's visual order text;
 *     drawInBatch(FormattedCharSequence, ..) drew the sequence;</li>
 * <li>drawInternal made a colour with no alpha (the top six bits clear) opaque (Font.adjustColor) -- 1.21.6's
 *     drawInBatch takes the colour as given, so an alpha-less colour would draw nothing -- and answered the x where the
 *     text ended, (int) (x + its advance), plus one with a shadow. The advance is the StringSplitter width of the drawn
 *     text (the glyph advances drawInternal summed).</li>
 * </ul>
 * The inverseDepth flag ordered the shadow and the text in depth; 1.21.6's text layers order them themselves (the GUI
 * has no depth), so it is not passed on. Font.ALPHA_CUTOFF (8, the alpha below which a glyph pixel was discarded) is a
 * constant 1.21.6 no longer declares.
 */
@net.fabricmc.api.Environment(net.fabricmc.api.EnvType.CLIENT)
public final class CbFontDraw {

    private CbFontDraw() {
    }

    /** Font.ALPHA_CUTOFF */
    public static int field_35427() {
        return 8;
    }

    private static int end(float x, float advance, boolean shadow) {
        return (int) (x + advance) + (shadow ? 1 : 0);
    }

    private static float width(net.minecraft.class_327 font, String text) {
        return font.method_27527().method_27482(font.method_1726() ? font.method_1721(text) : text);
    }

    /** drawInBatch(String, x, y, color, shadow, pose, buffers, mode, background, light) */
    public static int method_27521(net.minecraft.class_327 font, String text, float x, float y, int color, boolean shadow,
            org.joml.Matrix4f pose, net.minecraft.class_4597 buffers, net.minecraft.class_327.class_6415 mode, int background, int light) {
        font.method_27521(text, x, y, CbGuiText.adjust(color), shadow, pose, buffers, mode, background, light);
        return end(x, width(font, text), shadow);
    }

    /** drawInBatch(Component, x, y, color, shadow, pose, buffers, mode, background, light) */
    public static int method_27522(net.minecraft.class_327 font, net.minecraft.class_2561 text, float x, float y, int color, boolean shadow,
            org.joml.Matrix4f pose, net.minecraft.class_4597 buffers, net.minecraft.class_327.class_6415 mode, int background, int light) {
        return method_22942(font, text.method_30937(), x, y, color, shadow, pose, buffers, mode, background, light);
    }

    /** drawInBatch(Component, x, y, color, shadow, pose, buffers, mode, background, light, inverseDepth) */
    public static int method_30882(net.minecraft.class_327 font, net.minecraft.class_2561 text, float x, float y, int color, boolean shadow,
            org.joml.Matrix4f pose, net.minecraft.class_4597 buffers, net.minecraft.class_327.class_6415 mode, int background, int light,
            boolean inverseDepth) {
        return method_22942(font, text.method_30937(), x, y, color, shadow, pose, buffers, mode, background, light);
    }

    /** drawInBatch(FormattedCharSequence, x, y, color, shadow, pose, buffers, mode, background, light) */
    public static int method_22942(net.minecraft.class_327 font, net.minecraft.class_5481 text, float x, float y, int color, boolean shadow,
            org.joml.Matrix4f pose, net.minecraft.class_4597 buffers, net.minecraft.class_327.class_6415 mode, int background, int light) {
        font.method_22942(text, x, y, CbGuiText.adjust(color), shadow, pose, buffers, mode, background, light);
        return end(x, font.method_27527().method_30875(text), shadow);
    }

    // ---- a mod's super.drawInBatch(..) in a Font subclass: super's 1.21.6 method, then 1.21.5's answer ----

    public interface SuperString {
        void cb$super$method_27521(String text, float x, float y, int color, boolean shadow, org.joml.Matrix4f pose,
            net.minecraft.class_4597 buffers, net.minecraft.class_327.class_6415 mode, int background, int light);
    }

    public interface SuperComponent {
        void cb$super$method_27522(net.minecraft.class_2561 text, float x, float y, int color, boolean shadow, org.joml.Matrix4f pose,
            net.minecraft.class_4597 buffers, net.minecraft.class_327.class_6415 mode, int background, int light);
    }

    public interface SuperSequence {
        void cb$super$method_22942(net.minecraft.class_5481 text, float x, float y, int color, boolean shadow, org.joml.Matrix4f pose,
            net.minecraft.class_4597 buffers, net.minecraft.class_327.class_6415 mode, int background, int light);
    }

    public static int superString(SuperString f, String text, float x, float y, int color, boolean shadow, org.joml.Matrix4f pose,
            net.minecraft.class_4597 buffers, net.minecraft.class_327.class_6415 mode, int background, int light) {
        f.cb$super$method_27521(text, x, y, CbGuiText.adjust(color), shadow, pose, buffers, mode, background, light);
        return end(x, width((net.minecraft.class_327) (Object) f, text), shadow);
    }

    public static int superComponent(SuperComponent f, net.minecraft.class_2561 text, float x, float y, int color, boolean shadow,
            org.joml.Matrix4f pose, net.minecraft.class_4597 buffers, net.minecraft.class_327.class_6415 mode, int background, int light) {
        f.cb$super$method_27522(text, x, y, CbGuiText.adjust(color), shadow, pose, buffers, mode, background, light);
        return end(x, ((net.minecraft.class_327) (Object) f).method_27527().method_30875(text.method_30937()), shadow);
    }

    public static int superSequence(SuperSequence f, net.minecraft.class_5481 text, float x, float y, int color, boolean shadow,
            org.joml.Matrix4f pose, net.minecraft.class_4597 buffers, net.minecraft.class_327.class_6415 mode, int background, int light) {
        f.cb$super$method_22942(text, x, y, CbGuiText.adjust(color), shadow, pose, buffers, mode, background, light);
        return end(x, ((net.minecraft.class_327) (Object) f).method_27527().method_30875(text), shadow);
    }
}
