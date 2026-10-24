package top.dext.centurybridge.step.v1_21_2.rt;

import net.minecraft.class_1729;
import net.minecraft.class_9875;

/**
 * The recipe-book facades' counting of a menu's grid (RecipeBookComponent and its furnace subclass): 1.21.2's
 * StackedItemContents of a menu, and the same counts as a 1.21.1 StackedContents. The rest of 1.21.1's
 * RecipeBookMenu is CbItemRecipeMenus.
 */
public final class CbRestRecipeMenus {

    private CbRestRecipeMenus() {
    }

    /**
     * fillCraftSlotsStackedContents into 1.21.2's StackedItemContents: 1.21.2's method of the menu, which for a
     * 1.21.1 menu class is its own 1.21.1 override poured in (the override hooks and abstractFills of
     * CbItemRecipeMenus)
     */
    public static void fill(class_1729 menu, class_9875 items) {
        menu.method_7654(items);
    }

    /**
     * the counts of 1.21.2's StackedItemContents added to a 1.21.1 StackedContents: each item counted under its
     * stacking index (its raw id), as 1.21.1's accountStack counted it
     */
    public static void counts(class_9875 items, top.dext.centurybridge.step.v1_21_2.facade.StackedContents out) {
        for (it.unimi.dsi.fastutil.objects.Reference2IntMap.Entry<net.minecraft.class_6880<net.minecraft.class_1792>> e
                : ((top.dext.centurybridge.step.v1_21_2.mixin.CbRestStackedItemContentsAccessor) items).cb$raw().field_52504.reference2IntEntrySet()) {
            if (e.getIntValue() > 0) {
                int index = net.minecraft.class_7923.field_41178.method_10206(e.getKey().comp_349());
                out.field_7550.put(index, out.field_7550.get(index) + e.getIntValue());
            }
        }
    }

    /** 1.21.2's StackedItemContents as a 1.21.1 StackedContents of the same counts */
    public static top.dext.centurybridge.step.v1_21_2.facade.StackedContents legacyCounts(class_9875 items) {
        top.dext.centurybridge.step.v1_21_2.facade.StackedContents out = new top.dext.centurybridge.step.v1_21_2.facade.StackedContents();
        counts(items, out);
        return out;
    }
}
