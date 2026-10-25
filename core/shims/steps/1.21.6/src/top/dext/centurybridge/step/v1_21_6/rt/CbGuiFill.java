package top.dext.centurybridge.step.v1_21_6.rt;

/**
 * 1.21.5's GuiGraphics fills that took a RenderType or a z, on 1.21.6 (see CbGuiTypes for render type -&gt; pipeline).
 * Bodies as 1.21.5's: hLine / vLine swap their ends and fill a one-pixel rectangle; fill(type, .., color) is
 * fill(type, .., z 0, color). A z is dropped: 1.21.6's GUI has no depth, what is drawn later is on top. The
 * gradient goes through 1.21.6's own two-colour fill (innerFill, opened by the access widener), which its
 * fillGradient uses with the GUI pipeline.
 */
@net.fabricmc.api.Environment(net.fabricmc.api.EnvType.CLIENT)
public final class CbGuiFill {

    private CbGuiFill() {
    }

    /** blitSprite(Function, sprite id, texW, texH, u, v, x, y, w, h): 1.21.5 drew it white */
    public static int white() {
        return -1;
    }

    /** fill(RenderType, x1, y1, x2, y2, z, color) */
    public static void method_48196(net.minecraft.class_332 g, net.minecraft.class_1921 type, int x1, int y1, int x2, int y2,
            int z, int color) {
        g.method_48196(CbGuiTypes.pipeline(type), x1, y1, x2, y2, color);
    }

    /** fill(RenderType, x1, y1, x2, y2, color) */
    public static void method_51739(net.minecraft.class_332 g, net.minecraft.class_1921 type, int x1, int y1, int x2, int y2,
            int color) {
        g.method_48196(CbGuiTypes.pipeline(type), x1, y1, x2, y2, color);
    }

    /** fill(x1, y1, x2, y2, z, color) */
    public static void method_51737(net.minecraft.class_332 g, int x1, int y1, int x2, int y2, int z, int color) {
        g.method_25294(x1, y1, x2, y2, color);
    }

    /** hLine(RenderType, x1, x2, y, color) */
    public static void method_51738(net.minecraft.class_332 g, net.minecraft.class_1921 type, int x1, int x2, int y, int color) {
        if (x2 < x1) {
            int t = x1;
            x1 = x2;
            x2 = t;
        }
        method_51739(g, type, x1, y, x2 + 1, y + 1, color);
    }

    /** vLine(RenderType, x, y1, y2, color) */
    public static void method_51742(net.minecraft.class_332 g, net.minecraft.class_1921 type, int x, int y1, int y2, int color) {
        if (y2 < y1) {
            int t = y1;
            y1 = y2;
            y2 = t;
        }
        method_51739(g, type, x, y1 + 1, x + 1, y2, color);
    }

    /** hLine(x1, x2, y, color): 1.21.6's hLine of the GUI pipeline */
    public static void method_25292(net.minecraft.class_332 g, int x1, int x2, int y, int color) {
        g.method_51738(x1, x2, y, color);
    }

    /** vLine(x, y1, y2, color) */
    public static void method_25301(net.minecraft.class_332 g, int x, int y1, int y2, int color) {
        g.method_51742(x, y1, y2, color);
    }

    /** fillGradient(x1, y1, x2, y2, z, from, to) */
    public static void method_33284(net.minecraft.class_332 g, int x1, int y1, int x2, int y2, int z, int from, int to) {
        g.method_25296(x1, y1, x2, y2, from, to);
    }

    /** fillGradient(RenderType, x1, y1, x2, y2, from, to, z) */
    public static void method_51740(net.minecraft.class_332 g, net.minecraft.class_1921 type, int x1, int y1, int x2, int y2,
            int from, int to, int z) {
        g.method_70848(CbGuiTypes.pipeline(type), net.minecraft.class_11231.method_70899(), x1, y1, x2, y2, from,
            Integer.valueOf(to));
    }
}
