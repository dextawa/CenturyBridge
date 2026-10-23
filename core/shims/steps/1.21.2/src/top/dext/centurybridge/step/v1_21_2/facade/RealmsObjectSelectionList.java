package top.dext.centurybridge.step.v1_21_2.facade;

import java.util.Collection;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

/**
 * RealmsObjectSelectionList (class_4904) of 1.21.1, which 1.21.2 dropped (its screens use ObjectSelectionList
 * directly): 1.21.1's class, each body as 1.21.1's on 1.21.2's ObjectSelectionList.
 */
@Environment(EnvType.CLIENT)
public abstract class RealmsObjectSelectionList<E extends net.minecraft.class_4280.class_4281<E>> extends net.minecraft.class_4280<E> {

    public RealmsObjectSelectionList(int width, int height, int top, int itemHeight) {
        super(net.minecraft.class_310.method_1551(), width, height, top, itemHeight);
    }

    /** setSelectedItem(index): -1 clears the selection, otherwise the entry at the index when there are entries */
    public void method_25492(int index) {
        if (index == -1) {
            this.method_25313(null);
        } else if (super.method_25340() != 0) {
            this.method_25313(this.method_25326(index));
        }
    }

    /** selectItem(index) */
    public void method_25489(int index) {
        this.method_25492(index);
    }

    /** getMaxPosition(): 0 */
    @Override
    public int method_25317() {
        return 0;
    }

    /** getScrollbarPosition(): 60% of the width, truncated */
    @Override
    public int method_25322() {
        return (int) (this.field_22758 * 0.6);
    }

    @Override
    public void method_25314(Collection<E> entries) {
        super.method_25314(entries);
    }

    @Override
    public int method_25340() {
        return super.method_25340();
    }

    @Override
    public int method_25337(int index) {
        return super.method_25337(index);
    }

    @Override
    public int method_25342() {
        return super.method_25342();
    }

    /** addEntry(entry) */
    public int method_25491(E entry) {
        return super.method_25321(entry);
    }

    /** clear(): every entry removed */
    public void method_25493() {
        this.method_25339();
    }

    @Override
    public int method_25321(E entry) {
        return this.method_25491(entry);
    }
}
