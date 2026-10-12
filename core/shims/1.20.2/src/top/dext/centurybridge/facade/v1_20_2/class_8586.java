package top.dext.centurybridge.facade.v1_20_2;

/**
 * 1.20.1 SymlinkWarningScreen (net/minecraft/class_8586), removed in 1.20.2 (its replacement takes a
 * world or pack variant): "World folder contains symbolic links", the explanation with the help link,
 * and Open in Browser / Copy Link / Back buttons in a centred grid; Back and Escape return to the parent
 * screen. The 1.20.1 class on 1.20.2's widgets and layouts, which kept those members; 1.20.2 still ships
 * both translation keys. Every member 1.20.1 had is here, public (a mod's access widener cannot reach a
 * CenturyBridge class). Reached through classRenames.
 *
 * render: 1.20.1 drew the background and then the widgets; 1.20.2's Screen.render does exactly that, so
 * it is called as it is.
 */
@net.fabricmc.api.Environment(net.fabricmc.api.EnvType.CLIENT)
public class class_8586 extends net.minecraft.class_437 {

    private static final String LINK = "https://aka.ms/MinecraftSymLinks";
    /** TITLE */
    public static final net.minecraft.class_2561 field_44964 =
        net.minecraft.class_2561.method_43471("symlink_warning.title").method_27692(net.minecraft.class_124.field_1067);
    /** MESSAGE */
    public static final net.minecraft.class_2561 field_44965 = net.minecraft.class_2561.method_43469("symlink_warning.message", LINK);
    /** callbackScreen */
    public final net.minecraft.class_437 field_44966;
    /** layout */
    public final net.minecraft.class_7845 field_44967;

    public class_8586(net.minecraft.class_437 parent) {
        super(field_44964);
        this.field_44967 = new net.minecraft.class_7845().method_48636(10);
        this.field_44966 = parent;
    }

    /** init */
    @Override
    public void method_25426() {
        super.method_25426();
        this.field_44967.method_46458().method_46467();
        net.minecraft.class_7845.class_7939 rows = this.field_44967.method_47610(1);
        rows.method_47612(new net.minecraft.class_7842(this.field_22785, this.field_22793));
        rows.method_47612(new net.minecraft.class_7940(field_44965, this.field_22793).method_48984(this.field_22789 - 50).method_48981(true));
        net.minecraft.class_7845 buttons = new net.minecraft.class_7845().method_48635(5);
        net.minecraft.class_7845.class_7939 cells = buttons.method_47610(3);
        cells.method_47612(net.minecraft.class_4185.method_46430(net.minecraft.class_5244.field_44968, class_8586::method_52264)
            .method_46437(120, 20).method_46431());
        cells.method_47612(net.minecraft.class_4185.method_46430(net.minecraft.class_5244.field_44969, this::method_52263)
            .method_46437(120, 20).method_46431());
        cells.method_47612(net.minecraft.class_4185.method_46430(net.minecraft.class_5244.field_24339, this::method_52262)
            .method_46437(120, 20).method_46431());
        rows.method_47612(buttons);
        this.method_48640();
        this.field_44967.method_48206(this::method_37063);
    }

    /** repositionElements */
    @Override
    public void method_48640() {
        this.field_44967.method_48222();
        net.minecraft.class_7843.method_48634(this.field_44967, this.method_48202());
    }

    /** render */
    @Override
    public void method_25394(net.minecraft.class_332 graphics, int mouseX, int mouseY, float delta) {
        super.method_25394(graphics, mouseX, mouseY, delta);
    }

    /** getNarrationMessage */
    @Override
    public net.minecraft.class_2561 method_25435() {
        return net.minecraft.class_5244.method_37111(super.method_25435(), field_44965);
    }

    /** onClose */
    @Override
    public void method_25419() {
        this.field_22787.method_1507(this.field_44966);
    }

    /** the Back button */
    public void method_52262(net.minecraft.class_4185 button) {
        this.method_25419();
    }

    /** the Copy Link button */
    public void method_52263(net.minecraft.class_4185 button) {
        this.field_22787.field_1774.method_1455(LINK);
    }

    /** the Open in Browser button */
    public static void method_52264(net.minecraft.class_4185 button) {
        net.minecraft.class_156.method_668().method_670(LINK);
    }
}
