package top.dext.centurybridge.step.v1_21_2.rt;

import it.unimi.dsi.fastutil.ints.Int2IntMap;
import net.minecraft.class_1661;
import net.minecraft.class_1792;
import net.minecraft.class_1799;
import net.minecraft.class_9875;
import top.dext.centurybridge.step.v1_21_2.facade.StackedContents;

/**
 * 1.21.1's StackedContents (facade/StackedContents) where 1.21.2's vanilla takes StackedItemContents:
 * Inventory.fillStackedContents. Other areas' fill methods (StackedContentsCompatible, the recipe book menus) can
 * use {@link #pour} the same way.
 */
public final class CbEntityStacked {

    /** a mod Inventory subclass's own 1.21.1 fillStackedContents (the override hook's adapter) */
    public interface LegacyFill {
        void method_7387(StackedContents contents);
    }

    /** the counts of a 1.21.1 StackedContents added to 1.21.2's StackedItemContents */
    public static void pour(StackedContents from, class_9875 to) {
        for (Int2IntMap.Entry e : from.field_7550.int2IntEntrySet()) {
            if (e.getIntValue() > 0) {
                to.method_61536(new class_1799(class_1792.method_7875(e.getIntKey()), e.getIntValue()), e.getIntValue());
            }
        }
    }

    /** 1.21.1 Inventory.fillStackedContents(contents), called: the mod class's own, else Inventory's */
    public static void fillStackedContents(class_1661 inventory, StackedContents contents) {
        if (inventory instanceof LegacyFill legacy) {
            legacy.method_7387(contents);
            return;
        }
        superFillStackedContents(inventory, contents);
    }

    /** 1.21.1 Inventory's own body: every stack of the main items, accountSimpleStack */
    public static void superFillStackedContents(class_1661 inventory, StackedContents contents) {
        for (class_1799 stack : inventory.field_7547) {
            contents.method_7404(stack);
        }
    }

    /** 1.21.2's fillStackedContents(StackedItemContents) of a mod Inventory subclass: its 1.21.1 override fills */
    public static void fillBridge(LegacyFill self, class_9875 contents) {
        StackedContents old = new StackedContents();
        self.method_7387(old);
        pour(old, contents);
    }

    private CbEntityStacked() {
    }
}
