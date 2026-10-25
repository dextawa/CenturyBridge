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

    /**
     * renderItem(stack, x, y, seed, z): 1.21.5 was renderItem(player, level, stack, x, y, seed, z); 1.21.6's
     * renderItem(stack, x, y, seed) is the same call without z (both pass Minecraft.player and .level, javap). z is
     * dropped: the GUI has no depth.
     */
    public static void method_51429(net.minecraft.class_332 g, net.minecraft.class_1799 stack, int x, int y, int seed, int z) {
        g.method_51428(stack, x, y, seed);
    }

    /** GuiGraphics.MAX_GUI_Z of 1.21.5 (1.21.6's GUI has no depth; the constant is 1.21.5's) */
    public static float field_44931() {
        return 10000.0F;
    }

    /** GuiGraphics.MIN_GUI_Z of 1.21.5 */
    public static float field_44932() {
        return -10000.0F;
    }

    /**
     * renderTooltip(Font, List&lt;FormattedCharSequence&gt;, positioner, x, y): 1.21.5 drew the tooltip at once at z 400,
     * over the rest of the frame; 1.21.6 defers tooltips to the end of the frame for that, and every 1.21.5 tooltip call
     * it kept passes "replace" false (javap: renderTooltip(.., List&lt;Component&gt;, ..) and the others call the deferring
     * method with iconst_0), so the call is 1.21.6's with false.
     */
    public static boolean tooltipKeepsFirst() {
        return false;
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
