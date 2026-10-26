package cb.smx4c;

import java.util.concurrent.atomic.AtomicInteger;
import net.minecraft.class_2561;
import net.minecraft.class_332;
import net.minecraft.class_342;
import net.minecraft.class_4185;
import net.minecraft.class_437;

/** The test screen: the list (x 0..200, y 30..150), the text area, button A (clicked() false), button B, an edit box. */
public class TestScreen extends class_437 {
    public TestList list;
    public TestTextArea area;
    public class_4185 buttonA;
    public class_4185 buttonB;
    public class_342 edit;
    public final AtomicInteger pressedA = new AtomicInteger();
    public final AtomicInteger pressedB = new AtomicInteger();

    public TestScreen() {
        super(class_2561.method_43470("cb smx4"));
    }

    @Override
    protected void method_25426() {
        this.list = new TestList(this.field_22787, 200, 120, 30, 18);
        this.method_37063(this.list);
        this.area = new TestTextArea(this.field_22793, 220, 30, 160, 80);
        this.method_37063(this.area);
        this.buttonA = class_4185.method_46430(class_2561.method_43470("A"), b -> this.pressedA.incrementAndGet()).method_46434(220, 130, 60, 20).method_46431();
        this.buttonB = class_4185.method_46430(class_2561.method_43470("B"), b -> this.pressedB.incrementAndGet()).method_46434(300, 130, 60, 20).method_46431();
        this.method_37063(this.buttonA);
        this.method_37063(this.buttonB);
        this.edit = new class_342(this.field_22793, 400, 130, 100, 20, class_2561.method_43470("edit"));
        this.method_37063(this.edit);
        Counters.blockedButton = this.buttonA;
    }

    @Override
    public void method_25394(class_332 g, int mouseX, int mouseY, float pt) {
        super.method_25394(g, mouseX, mouseY, pt);
    }
}
