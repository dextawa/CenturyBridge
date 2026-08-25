package cb.guitest;

/** A 1.20.1 ClickableWidget subclass: exercises the renderWidget override hook. */
public class TestWidget extends net.minecraft.class_339 {
    public TestWidget(int x, int y) {
        super(x, y, 120, 20, net.minecraft.class_2561.method_43470("custom widget"));
    }

    @Override
    protected void method_48579(net.minecraft.class_332 ctx, int mouseX, int mouseY, float delta) {
        boolean hover = mouseX >= this.method_46426() && mouseX < this.method_46426() + this.method_25368()
            && mouseY >= this.method_46427() && mouseY < this.method_46427() + this.method_25364();
        ctx.method_25294(this.method_46426(), this.method_46427(), this.method_46426() + this.method_25368(),
            this.method_46427() + this.method_25364(), hover ? 0xFFAA3333 : 0xFF333388);
        ctx.method_27534(net.minecraft.class_310.method_1551().field_1772, this.method_25369(),
            this.method_46426() + this.method_25368() / 2, this.method_46427() + 6, 0xFFFFFFFF);
    }

    @Override
    protected void method_47399(net.minecraft.class_6382 builder) {
    }
}
