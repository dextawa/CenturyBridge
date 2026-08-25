package cb.guitest;

/** Exercises the 1.20.1 DrawContext API: fill, gradient, text, texture, matrices, a button. */
public class TestScreen extends net.minecraft.class_437 {
    private int clicks;

    public TestScreen() {
        super(net.minecraft.class_2561.method_43470("CenturyBridge GUI test"));
    }

    @Override
    protected void method_25426() {
        super.method_25426();
        this.method_37063(net.minecraft.class_4185.method_46430(
                net.minecraft.class_2561.method_43470("Click me"), b -> clicks++)
            .method_46434(this.field_22789 / 2 - 60, this.field_22790 / 2 + 40, 120, 20).method_46431());
        this.method_37063(new TestWidget(this.field_22789 / 2 - 60, this.field_22790 / 2 + 64));
    }

    @Override
    public void method_25394(net.minecraft.class_332 ctx, int mouseX, int mouseY, float delta) {
        this.method_25420(ctx);
        int w = this.field_22789, h = this.field_22790;
        ctx.method_25294(w / 2 - 110, h / 2 - 70, w / 2 + 110, h / 2 + 70, 0xC0203040);
        ctx.method_25296(w / 2 - 100, h / 2 - 60, w / 2 + 100, h / 2 - 30, 0xFF3355FF, 0xFF55FF33);
        ctx.method_25302(new net.minecraft.class_2960("textures/block/diamond_block.png"),
            w / 2 - 100, h / 2 - 20, 0, 0, 32, 32);
        ctx.method_51448().method_22903();
        ctx.method_51448().method_46416(w / 2 - 60, h / 2 - 20, 0);
        ctx.method_51448().method_22905(2.0f, 2.0f, 1.0f);
        ctx.method_51433(this.field_22793, "CB facade OK", 0, 0, 0xFFFFFF00, true);
        ctx.method_51448().method_22909();
        ctx.method_27534(this.field_22793, net.minecraft.class_2561.method_43470("clicks: " + clicks + "  mouse " + mouseX + "," + mouseY),
            w / 2, h / 2 + 12, 0xFFFFFFFF);
        super.method_25394(ctx, mouseX, mouseY, delta);
    }
}
