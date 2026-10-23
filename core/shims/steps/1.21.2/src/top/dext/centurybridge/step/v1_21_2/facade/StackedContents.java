package top.dext.centurybridge.step.v1_21_2.facade;

import it.unimi.dsi.fastutil.ints.Int2IntMap;
import it.unimi.dsi.fastutil.ints.Int2IntOpenHashMap;
import it.unimi.dsi.fastutil.ints.IntList;
import net.minecraft.class_1792;
import net.minecraft.class_1799;
import net.minecraft.class_1860;
import net.minecraft.class_7923;
import net.minecraft.class_8786;
import net.minecraft.class_9334;
import net.minecraft.class_9875;

/**
 * 1.21.1 StackedContents (net/minecraft/class_1662): the item counts a recipe book matches recipes against, keyed
 * by stacking index (the item's raw registry id). 1.21.2 made StackedContents generic over Holder&lt;Item&gt; and
 * wrapped it in StackedItemContents; the 1.21.1 class is gone under its name. This is 1.21.1's class: the counts
 * live in {@link #field_7550}, as in 1.21.1 (a mod reading or writing the map sees what 1.21.1 kept); a recipe is
 * matched by 1.21.2's StackedItemContents -- 1.21.1's RecipePicker generalised, over the recipe's placement
 * ingredients, which are 1.21.1's non-empty ingredients -- built from those counts at the call, and each picked
 * item reported by its stacking index as 1.21.1's picker did. The package-private members 1.21.1 had are public
 * stand-ins (a CenturyBridge class is out of the access widener's reach).
 */
public class StackedContents {

    /** the counts by stacking index */
    public final Int2IntMap field_7550 = new Int2IntOpenHashMap();

    public StackedContents() {
    }

    /** accountSimpleStack(stack): an undamaged, unenchanted stack without a custom name */
    public void method_7404(class_1799 stack) {
        if (!stack.method_7986() && !stack.method_7942() && !stack.method_57826(class_9334.field_49631)) {
            method_7400(stack);
        }
    }

    /** accountStack(stack): up to its max stack size */
    public void method_7400(class_1799 stack) {
        method_20478(stack, stack.method_7914());
    }

    /** accountStack(stack, maxCount) */
    public void method_20478(class_1799 stack, int maxCount) {
        if (!stack.method_7960()) {
            int index = method_7408(stack);
            int count = Math.min(maxCount, stack.method_7947());
            method_7401(index, count);
        }
    }

    /** getStackingIndex(stack): the item's raw id */
    public static int method_7408(class_1799 stack) {
        return class_7923.field_41178.method_10206(stack.method_7909());
    }

    /** has(index) */
    public boolean method_7410(int index) {
        return this.field_7550.get(index) > 0;
    }

    /** take(index, count): the index when there were enough (the count taken off), else 0 */
    public int method_7411(int index, int count) {
        int have = this.field_7550.get(index);
        if (have >= count) {
            this.field_7550.put(index, have - count);
            return index;
        }
        return 0;
    }

    /** put(index, count) */
    public void method_7401(int index, int count) {
        this.field_7550.put(index, this.field_7550.get(index) + count);
    }

    /** canCraft(recipe, output) */
    public boolean method_7402(class_1860<?> recipe, IntList output) {
        return method_7406(recipe, output, 1);
    }

    /** canCraft(recipe, output, amount) */
    public boolean method_7406(class_1860<?> recipe, IntList output, int amount) {
        return toItemContents().method_61537(recipe, amount, h -> {
            if (output != null) {
                output.add(class_7923.field_41178.method_10206(h.comp_349()));
            }
        });
    }

    /** getBiggestCraftableStack(recipe, output) */
    public int method_7407(class_8786<?> recipe, IntList output) {
        return method_7403(recipe, Integer.MAX_VALUE, output);
    }

    /** getBiggestCraftableStack(recipe, max, output) */
    public int method_7403(class_8786<?> recipe, int max, IntList output) {
        return toItemContents().method_61542(recipe.comp_1933(), max, h -> {
            if (output != null) {
                output.add(class_7923.field_41178.method_10206(h.comp_349()));
            }
        });
    }

    /** getBiggestCraftableStack(recipe, output) of the releases before 1.20.2, which took the recipe itself */
    public int method_7407(class_1860<?> recipe, IntList output) {
        return method_7403(recipe, Integer.MAX_VALUE, output);
    }

    public int method_7403(class_1860<?> recipe, int max, IntList output) {
        return toItemContents().method_61542(recipe, max, h -> {
            if (output != null) {
                output.add(class_7923.field_41178.method_10206(h.comp_349()));
            }
        });
    }

    /** EMPTY: the stacking index of the empty stack (a private constant of the class) */
    public static final int field_30653 = 0;

    /** fromStackingIndex(index) */
    public static class_1799 method_7405(int index) {
        return index == 0 ? class_1799.field_8037 : new class_1799(class_1792.method_7875(index));
    }

    /** clear() */
    public void method_7409() {
        this.field_7550.clear();
    }

    /** these counts as 1.21.2's StackedItemContents */
    public class_9875 toItemContents() {
        class_9875 w = new class_9875();
        for (Int2IntMap.Entry e : this.field_7550.int2IntEntrySet()) {
            if (e.getIntValue() > 0) {
                w.method_61536(new class_1799(class_1792.method_7875(e.getIntKey()), e.getIntValue()), e.getIntValue());
            }
        }
        return w;
    }
}
