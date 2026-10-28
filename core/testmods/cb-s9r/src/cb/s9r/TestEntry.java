package cb.s9r;

import net.minecraft.class_2561;
import net.minecraft.class_332;
import net.minecraft.class_4280;

/** a 1.21.8 entry */
public class TestEntry extends class_4280.class_4281<TestEntry> {
    public final int index;

    public TestEntry(int index) {
        this.index = index;
    }

    @Override
    public void method_25343(class_332 graphics, int index, int top, int left, int width, int height, int mouseX, int mouseY,
                             boolean hovered, float delta) {
    }

    @Override
    public class_2561 method_37006() {
        return class_2561.method_43470("entry " + this.index);
    }
}
