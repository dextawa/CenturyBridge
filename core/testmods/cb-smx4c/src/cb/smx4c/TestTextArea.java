package cb.smx4c;

import net.minecraft.class_2561;
import net.minecraft.class_327;
import net.minecraft.class_332;
import net.minecraft.class_6382;
import net.minecraft.class_7528;

/** A 1.21.3 AbstractScrollWidget (1.21.4's AbstractTextAreaWidget) with innerPadding() 10. */
public class TestTextArea extends class_7528 {
    final class_327 font;
    public volatile String answers = "not drawn";

    public TestTextArea(class_327 font, int x, int y, int w, int h) {
        super(x, y, w, h, class_2561.method_43470("cb smx4 text area"));
        this.font = font;
    }

    @Override
    protected int method_44381() {
        return 10;
    }

    @Override
    protected int method_44391() {
        return 200;
    }

    @Override
    protected double method_44393() {
        return 9.0;
    }

    @Override
    protected void method_44389(class_332 g, int mouseX, int mouseY, float pt) {
        int x = this.method_46426() + this.method_44381();
        int y = this.method_46427() + this.method_44381();
        g.method_25303(this.font, "padded text", x, y, 0xFFFF00);
        String a = this.method_44381() + " " + this.method_44385() + " " + this.method_44383(this.method_46427() + 5, this.method_46427() + 15)
            + " " + this.method_44383(this.method_46427() - 30, this.method_46427() - 20);
        this.method_52233(g, this.method_46426(), this.method_46427(), this.method_25368(), this.method_25364());
        this.answers = a;
    }

    @Override
    protected void method_47399(class_6382 out) {
    }
}
