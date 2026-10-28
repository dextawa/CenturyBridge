package cb.s9r;

import net.minecraft.class_310;
import net.minecraft.class_4280;

/** a 1.21.8 ObjectSelectionList whose isSelectedItem override says the selected row is index 2 (nothing is getSelected()) */
public class TestList extends class_4280<TestEntry> {
    public TestList(class_310 mc, int width, int height, int y, int itemHeight) {
        super(mc, width, height, y, itemHeight);
        for (int i = 0; i < 4; i++) {
            this.method_25321(new TestEntry(i));
        }
    }

    @Override
    protected boolean method_25332(int index) {
        Counters.IS_SELECTED_CALLS.incrementAndGet();
        Counters.ASKED.add(index);
        return index == 2;
    }
}
