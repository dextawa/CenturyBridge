package top.dext.centurybridge.step.v1_21_2.rt;

import it.unimi.dsi.fastutil.objects.Reference2IntMap;
import net.minecraft.class_1737;
import net.minecraft.class_1792;
import net.minecraft.class_6880;
import net.minecraft.class_9875;
import top.dext.centurybridge.step.v1_21_2.facade.StackedContents;

/**
 * 1.21.1's StackedContentsCompatible.fillStackedContents(StackedContents) on 1.21.2's
 * fillStackedContents(StackedItemContents) -- the furnaces, the crafter, SimpleContainer, the crafting containers and
 * every mod class implementing the interface. 1.21.1's StackedContents is s7entity's facade (counts by item raw id);
 * 1.21.2's StackedItemContents counts item holders in a StackedContents of its own (field opened by the layer's
 * access widener).
 * <ul>
 * <li>A 1.21.1 call fills the facade: a 1.21.1 override fills it itself; any other implementation fills a fresh
 *     StackedItemContents with 1.21.2's body, whose counts are added to the facade by raw id.</li>
 * <li>1.21.2 asking a 1.21.1 override: the override fills a facade, poured into 1.21.2's contents
 *     (CbEntityStacked.pour); its super call is 1.21.2's body read back the same way.</li>
 * </ul>
 */
public final class CbLevelStacked {
    private CbLevelStacked() {
    }

    /** a 1.21.1 StackedContentsCompatible implementation declaring fillStackedContents (the override hook's adapter) */
    public interface LegacyFill {
        void method_7683(StackedContents contents);
    }

    /** the 1.21.2 method, reached past a 1.21.1 override (the engine implements it with a super call) */
    public interface SuperFill {
        void cb$super$method_7683(class_9875 contents);
    }

    /** the counts of 1.21.2's contents added to a 1.21.1 StackedContents, by item raw id */
    public static void readBack(class_9875 from, StackedContents to) {
        for (Reference2IntMap.Entry<class_6880<class_1792>> e : from.field_52506.field_52504.reference2IntEntrySet()) {
            if (e.getIntValue() > 0) {
                to.field_7550.mergeInt(class_1792.method_7880(e.getKey().comp_349()), e.getIntValue(), Integer::sum);
            }
        }
    }

    /** container.fillStackedContents(contents), 1.21.1's shape */
    public static void method_7683(class_1737 self, StackedContents contents) {
        if (self instanceof LegacyFill legacy) {
            legacy.method_7683(contents);
            return;
        }
        class_9875 tmp = new class_9875();
        self.method_7683(tmp);
        readBack(tmp, contents);
    }

    /** the same, called on a SimpleContainer / TransientCraftingContainer / furnace / crafter */
    public static void method_7683(net.minecraft.class_1277 self, StackedContents contents) {
        method_7683((class_1737) self, contents);
    }

    public static void method_7683(net.minecraft.class_1715 self, StackedContents contents) {
        method_7683((class_1737) self, contents);
    }

    public static void method_7683(net.minecraft.class_2609 self, StackedContents contents) {
        method_7683((class_1737) self, contents);
    }

    public static void method_7683(net.minecraft.class_8887 self, StackedContents contents) {
        method_7683((class_1737) self, contents);
    }

    /** 1.21.2 asking a 1.21.1 override */
    public static void fillBridge(LegacyFill self, class_9875 contents) {
        StackedContents old = new StackedContents();
        self.method_7683(old);
        CbEntityStacked.pour(old, contents);
    }

    /** a 1.21.1 StackedContentsCompatible lambda (lambdaAdapters), as 1.21.2's: it fills a facade, poured in */
    public static class_1737 lambda(LegacyFill legacy) {
        return contents -> fillBridge(legacy, contents);
    }

    /** a 1.21.1 override's super.fillStackedContents(contents): 1.21.2's body, read back */
    public static void superFill(SuperFill self, StackedContents contents) {
        class_9875 tmp = new class_9875();
        self.cb$super$method_7683(tmp);
        readBack(tmp, contents);
    }
}
