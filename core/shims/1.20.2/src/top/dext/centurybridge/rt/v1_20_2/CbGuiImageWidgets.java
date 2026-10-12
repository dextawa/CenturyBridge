package top.dext.centurybridge.rt.v1_20_2;

/**
 * 1.20.1 ImageWidget (x, y, width, height, texture): a widget that draws a whole texture file stretched
 * to its size, clickable like any widget. 1.20.2 made ImageWidget abstract with package-private
 * constructors (Sprite / Texture variants, inactive, silent, unfocusable), so neither `new ImageWidget(..)`
 * nor a mod subclass's super(.., texture) exists any more -- and the bridge layer, compiled against the
 * plain stub, cannot subclass it either.
 *
 * Both paths are rebuilt on 1.20.2's own construction: the 1.20.1 texture rides along to the
 * ImageWidget(x, y, width, height) constructor that every ImageWidget runs (a mod subclass's super call
 * is reshaped to it, superCtorReshapes; a NEW site goes through the public Texture variant,
 * ctorRedirects), and CbGuiImageWidgetBridge takes it there and gives the widget its 1.20.1 behaviour.
 * The hand-off is per thread and consumed by the very next ImageWidget constructor to finish: the
 * texture is set while the super call's arguments are evaluated, nothing in between constructs another
 * widget.
 */
@net.fabricmc.api.Environment(net.fabricmc.api.EnvType.CLIENT)
public final class CbGuiImageWidgets {

    private static final ThreadLocal<net.minecraft.class_2960> PENDING = new ThreadLocal<>();

    /** taken by the ImageWidget constructor that finishes next on this thread */
    public static net.minecraft.class_2960 cbTakePending() {
        net.minecraft.class_2960 t = PENDING.get();
        if (t != null) {
            PENDING.remove();
        }
        return t;
    }

    /** superCtorReshapes conv: the old height argument, unchanged, with the old texture handed on */
    public static int cbHeightWithTexture(int height, net.minecraft.class_2960 texture) {
        PENDING.set(texture);
        return height;
    }

    /** superCtorReshapes push: the (0, 0) position 1.20.1's ImageWidget(width, height, texture) delegated with */
    public static int cbZero() {
        return 0;
    }

    /** was net/minecraft/class_8208.<init>(IILnet/minecraft/class_2960;)V */
    public static net.minecraft.class_8208 cbNewclass_8208(int width, int height, net.minecraft.class_2960 texture) {
        return cbNewclass_8208(0, 0, width, height, texture);
    }

    /** was net/minecraft/class_8208.<init>(IIIILnet/minecraft/class_2960;)V */
    public static net.minecraft.class_8208 cbNewclass_8208(int x, int y, int width, int height, net.minecraft.class_2960 texture) {
        PENDING.set(texture);
        net.minecraft.class_8208 w = net.minecraft.class_8208.method_52721(width, height, texture, width, height);
        w.method_46421(x);
        w.method_46419(y);
        return w;
    }

    /** 1.20.1 ImageWidget.renderWidget: the whole texture, stretched to the widget's current size */
    public static void cbRender(net.minecraft.class_339 widget, net.minecraft.class_2960 texture, net.minecraft.class_332 graphics) {
        int w = widget.method_25368();
        int h = widget.method_25364();
        graphics.method_25290(texture, widget.method_46426(), widget.method_46427(), 0.0F, 0.0F, w, h, w, h);
    }

    private CbGuiImageWidgets() {
    }
}
