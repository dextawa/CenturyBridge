package top.dext.centurybridge.step.v1_21_2.rt;

import net.minecraft.class_1714;
import net.minecraft.class_1720;
import net.minecraft.class_1723;
import net.minecraft.class_1729;
import net.minecraft.class_8786;
import net.minecraft.class_9875;

/**
 * 1.21.1's RecipeBookMenu questions on 1.21.2's menus. 1.21.1's RecipeBookMenu declared the crafting grid's
 * width and height, the result slot's index, the number of grid slots, how to count the grid's items and
 * whether a recipe matches; 1.21.2's keeps only the book type, the counting (into a StackedItemContents) and
 * placement. A 1.21.1 menu class answers through its own 1.21.1 methods (the adapters, added by adapterHooks);
 * 1.21.2's recipe-book menus answer as 1.21.1's did -- the player inventory's 2x2 grid (result 0, five slots),
 * the crafting table's 3x3 (result 0, ten slots), a furnace's one input (result 2, three slots).
 */
public final class CbRestRecipeMenus {

    private CbRestRecipeMenus() {
    }

    /** getGridWidth() of a 1.21.1 menu class */
    public interface LegacyGridWidth {
        int method_7653();
    }

    /** getGridHeight() of a 1.21.1 menu class */
    public interface LegacyGridHeight {
        int method_7656();
    }

    /** getResultSlotIndex() of a 1.21.1 menu class */
    public interface LegacyResultSlot {
        int method_7655();
    }

    /** getSize() of a 1.21.1 menu class */
    public interface LegacySize {
        int method_7658();
    }

    /** fillCraftSlotsStackedContents(stackedContents) of a 1.21.1 menu class */
    public interface LegacyFill {
        void method_7654(top.dext.centurybridge.step.v1_21_2.facade.StackedContents stackedContents);
    }

    /** recipeMatches(recipe) of a 1.21.1 menu class */
    public interface LegacyMatches {
        boolean method_7652(class_8786<?> recipe);
    }

    /** getGridWidth() */
    public static int method_7653(class_1729 menu) {
        if (menu instanceof LegacyGridWidth legacy) {
            return legacy.method_7653();
        }
        return menu instanceof class_1723 ? 2 : menu instanceof class_1714 ? 3 : 1;
    }

    /** getGridHeight() */
    public static int method_7656(class_1729 menu) {
        if (menu instanceof LegacyGridHeight legacy) {
            return legacy.method_7656();
        }
        return menu instanceof class_1723 ? 2 : menu instanceof class_1714 ? 3 : 1;
    }

    /** getResultSlotIndex() */
    public static int method_7655(class_1729 menu) {
        if (menu instanceof LegacyResultSlot legacy) {
            return legacy.method_7655();
        }
        return menu instanceof class_1720 ? 2 : 0;
    }

    /** getSize() */
    public static int method_7658(class_1729 menu) {
        if (menu instanceof LegacySize legacy) {
            return legacy.method_7658();
        }
        return menu instanceof class_1723 ? 5 : menu instanceof class_1714 ? 10 : 3;
    }

    /** fillCraftSlotsStackedContents(stackedContents), counting into 1.21.2's StackedItemContents */
    public static void fill(class_1729 menu, class_9875 items) {
        if (menu instanceof LegacyFill legacy) {
            top.dext.centurybridge.step.v1_21_2.facade.StackedContents counts = new top.dext.centurybridge.step.v1_21_2.facade.StackedContents();
            legacy.method_7654(counts);
            CbEntityStacked.pour(counts, items);
            return;
        }
        menu.method_7654(items);
    }

    // ------------------------------------------------------------------ the rest of 1.21.1's RecipeBookMenu

    /** handlePlacement(useMaxItems, recipe, player) of a 1.21.1 menu class that overrides it */
    public interface LegacyPlacement {
        void method_17697(boolean useMaxItems, class_8786<?> recipe, net.minecraft.class_3222 player);
    }

    /** beginPlacingRecipe() of a 1.21.1 menu class that overrides it */
    public interface LegacyBeginPlacing {
        void method_59963();
    }

    /** finishPlacingRecipe(recipe) of a 1.21.1 menu class that overrides it */
    public interface LegacyFinishPlacing {
        void method_59964(class_8786<?> recipe);
    }

    /** clearCraftingContent() of a 1.21.1 menu class */
    public interface LegacyClear {
        void method_7657();
    }

    /** shouldMoveToInventory(slot) of a 1.21.1 menu class */
    public interface LegacyMoveToInventory {
        boolean method_32339(int slot);
    }

    /** fillCraftSlotsStackedContents(stackedContents) of 1.21.1, called on any menu with a 1.21.1 StackedContents */
    public static void method_7654(class_1729 menu, top.dext.centurybridge.step.v1_21_2.facade.StackedContents stackedContents) {
        if (menu instanceof LegacyFill legacy) {
            legacy.method_7654(stackedContents);
            return;
        }
        class_9875 items = new class_9875();
        menu.method_7654(items);
        counts(items, stackedContents);
    }

    /** 1.21.2's fillCraftSlotsStackedContents of a 1.21.1 menu class (abstractFills): its own, into the same counts */
    public static void fillW(class_1729 menu, class_9875 items) {
        fill(menu, items);
    }

    /** clearCraftingContent() of 1.21.1: a 1.21.1 menu's own; 1.21.2's crafting grids and furnaces as 1.21.1's emptied them */
    public static void method_7657(class_1729 menu) {
        if (menu instanceof LegacyClear legacy) {
            legacy.method_7657();
        } else if (menu instanceof net.minecraft.class_9884 crafting) {
            for (net.minecraft.class_1735 slot : crafting.method_61628()) {
                slot.method_7673(net.minecraft.class_1799.field_8037);
            }
            crafting.method_61627().method_7673(net.minecraft.class_1799.field_8037);
        } else if (menu instanceof class_1720) {
            menu.method_7611(0).method_7673(net.minecraft.class_1799.field_8037);
            menu.method_7611(2).method_7673(net.minecraft.class_1799.field_8037);
        }
    }

    /** recipeMatches(recipe) of 1.21.1: a 1.21.1 menu's own; 1.21.2's crafting grids and furnaces as 1.21.1's asked */
    @SuppressWarnings({"unchecked", "rawtypes"})
    public static boolean method_7652(class_1729 menu, class_8786<?> recipe) {
        if (menu instanceof LegacyMatches legacy) {
            return legacy.method_7652(recipe);
        }
        net.minecraft.class_1860 r = recipe.comp_1933();
        if (menu instanceof net.minecraft.class_9884 crafting) {
            java.util.List<net.minecraft.class_1799> items = new java.util.ArrayList<>();
            for (net.minecraft.class_1735 slot : crafting.method_61628()) {
                items.add(slot.method_7677());
            }
            net.minecraft.class_9694 input = net.minecraft.class_9694.method_59986(method_7653(menu), method_7656(menu), items);
            return r.method_8115(input, ((top.dext.centurybridge.step.v1_21_2.mixin.CbRestCraftingMenuInvoker) crafting).cb$owner().method_37908());
        }
        if (menu instanceof class_1720 furnace) {
            return r.method_8115(new net.minecraft.class_9696(menu.method_7611(0).method_7677()),
                ((top.dext.centurybridge.step.v1_21_2.mixin.CbRestFurnaceMenuAccessor) furnace).cb$level());
        }
        return false;
    }

    /** shouldMoveToInventory(slot) of 1.21.1: a 1.21.1 menu's own; 1.21.2's crafting grids all but the result, furnaces all but the fuel */
    public static boolean method_32339(class_1729 menu, int slot) {
        if (menu instanceof LegacyMoveToInventory legacy) {
            return legacy.method_32339(slot);
        }
        return menu instanceof class_1720 ? slot != 1 : slot != method_7655(menu);
    }

    /**
     * 1.21.2's handlePlacement of a 1.21.1 menu class (abstractFills). A class overriding 1.21.1's handlePlacement
     * places as it did (it sends its own ghost recipe, as 1.21.1's did); otherwise 1.21.1's default --
     * ServerPlaceRecipe on the menu, between beginPlacingRecipe and finishPlacingRecipe -- as 1.21.2's
     * ServerPlaceRecipe, the same placement, over the menu's grid as 1.21.1's numbered it (slots 0 to
     * width*height, the result slot skipped) and clearing the slots 1.21.1's shouldMoveToInventory names.
     */
    @SuppressWarnings({"unchecked", "rawtypes"})
    public static class_1729.class_9885 handlePlacement(class_1729 menu, boolean useMaxItems, boolean creative, class_8786<?> recipe,
                                                        net.minecraft.class_3218 level, net.minecraft.class_1661 inventory) {
        if (menu instanceof LegacyPlacement legacy) {
            legacy.method_17697(useMaxItems, recipe, (net.minecraft.class_3222) inventory.field_7546);
            return class_1729.class_9885.field_52572;
        }
        if (menu instanceof LegacyBeginPlacing begin) {
            begin.method_59963();
        }
        try {
            int width = method_7653(menu);
            int height = method_7656(menu);
            int result = method_7655(menu);
            java.util.List<net.minecraft.class_1735> input = new java.util.ArrayList<>();
            for (int i = 0; i < width * height + 1 && input.size() < width * height; i++) {
                if (i != result) {
                    input.add(menu.method_7611(i));
                }
            }
            java.util.List<net.minecraft.class_1735> clear = new java.util.ArrayList<>();
            for (int i = 0; i < method_7658(menu); i++) {
                if (method_32339(menu, i)) {
                    clear.add(menu.method_7611(i));
                }
            }
            net.minecraft.class_2955.class_9840 access = new net.minecraft.class_2955.class_9840() {
                @Override
                public void method_61237(class_9875 items) {
                    fill(menu, items);
                }

                @Override
                public void method_61236() {
                    method_7657(menu);
                }

                @Override
                public boolean method_61238(class_8786 holder) {
                    return method_7652(menu, holder);
                }
            };
            return net.minecraft.class_2955.method_61232(access, width, height, input, clear, inventory, (class_8786) recipe, useMaxItems, creative);
        } finally {
            if (menu instanceof LegacyFinishPlacing finish) {
                finish.method_59964(recipe);
            }
        }
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
