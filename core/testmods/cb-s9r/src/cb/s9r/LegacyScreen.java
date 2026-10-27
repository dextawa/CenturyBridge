package cb.s9r;

import net.minecraft.class_2561;
import net.minecraft.class_4185;
import net.minecraft.class_437;

/** a 1.21.8 screen: the test list and the two buttons */
public class LegacyScreen extends class_437 {
    public TestList list;

    public LegacyScreen() {
        super(class_2561.method_43470("cb-s9r"));
    }

    /** for the native driver: a new screen, as Object (no 1.21.8 type in the reflective signature) */
    public static Object create() {
        return new LegacyScreen();
    }

    @Override
    protected void method_25426() {
        this.list = this.method_37063(new TestList(this.field_22787, this.field_22789, 120, 20, 20));
        this.method_37063(class_4185.method_46430(class_2561.method_43470("keep"), b -> Counters.KEEP_PRESSED.incrementAndGet())
            .method_46434(10, 170, 80, 20).method_46431());
        this.method_37063(class_4185.method_46430(class_2561.method_43470("blocked"), b -> Counters.BLOCKED_PRESSED.incrementAndGet())
            .method_46434(100, 170, 80, 20).method_46431());
    }
}
