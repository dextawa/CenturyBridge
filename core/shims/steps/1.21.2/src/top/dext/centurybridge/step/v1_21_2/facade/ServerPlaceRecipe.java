package top.dext.centurybridge.step.v1_21_2.facade;

import it.unimi.dsi.fastutil.ints.IntArrayList;
import it.unimi.dsi.fastutil.ints.IntList;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import net.minecraft.class_1661;
import net.minecraft.class_1729;
import net.minecraft.class_1735;
import net.minecraft.class_1799;
import net.minecraft.class_1860;
import net.minecraft.class_3222;
import net.minecraft.class_8786;
import net.minecraft.class_9695;
import top.dext.centurybridge.step.v1_21_2.rt.CbEntityStacked;
import top.dext.centurybridge.step.v1_21_2.rt.CbEntityTail;
import top.dext.centurybridge.step.v1_21_2.rt.CbItemRecipeMenus;

/**
 * 1.21.1 ServerPlaceRecipe (net/minecraft/class_2955): moves a clicked recipe's ingredients from the player's
 * inventory into a recipe book menu's grid. 1.21.2 kept the name for a private placer behind a static entry point
 * taking the grid's slot lists; the 1.21.1 class -- built on a RecipeBookMenu, with protected members a subclass
 * overrides -- is this one. Every body is 1.21.1's: the menu is asked through 1.21.1's RecipeBookMenu members
 * (CbItemRecipeMenus: a menu's own 1.21.1 method, else its 1.21.1 body), the counts are s7entity's 1.21.1
 * StackedContents, the inventory is filled through 1.21.1's Inventory.fillStackedContents (CbEntityStacked) and
 * searched with 1.21.1's findSlotMatchingUnusedItem (CbEntityTail); the ghost recipe sent when the inventory cannot
 * craft the recipe carries the recipe's first display (CbItemRecipeMenus.sendGhostRecipe). PlaceRecipe is raw here
 * so that addItemToSlot(Object, ..) forwards to addItemToSlot(Integer, ..) under its own name, as in 1.21.1. 1.21.1's
 * protected and private members are public (an access widener never reaches a CenturyBridge class).
 */
@SuppressWarnings({"rawtypes", "unchecked"})
public class ServerPlaceRecipe<I extends class_9695, R extends class_1860<I>> implements PlaceRecipe {
    /** ITEM_NOT_FOUND: what moveItemToGrid answers when the inventory has no such item */
    public static final int field_51523 = -1;
    public final StackedContents field_13347 = new StackedContents();
    public class_1661 field_13350;
    public class_1729 field_13348;

    public ServerPlaceRecipe(class_1729 menu) {
        this.field_13348 = menu;
    }

    /** recipeClicked(player, recipe, placeAll) */
    public void method_12826(class_3222 player, class_8786<R> recipe, boolean placeAll) {
        if (recipe == null || !player.method_14253().method_64593(recipe.comp_1932())) {
            return;
        }
        this.field_13350 = player.method_31548();
        if (!this.method_12825() && !player.method_7337()) {
            return;
        }
        this.field_13347.method_7409();
        CbEntityStacked.fillStackedContents(player.method_31548(), this.field_13347);
        CbItemRecipeMenus.fillCraftSlotsStackedContents(this.field_13348, this.field_13347);
        if (this.field_13347.method_7402(recipe.comp_1933(), null)) {
            this.method_12821(recipe, placeAll);
        } else {
            this.method_12822();
            CbItemRecipeMenus.sendGhostRecipe(player, recipe);
        }
        player.method_31548().method_5431();
    }

    /** clearGrid(): every grid slot the menu moves to the inventory goes back to it, then the menu clears */
    public void method_12822() {
        for (int i = 0; i < CbItemRecipeMenus.getSize(this.field_13348); ++i) {
            if (CbItemRecipeMenus.shouldMoveToInventory(this.field_13348, i)) {
                class_1799 stack = this.field_13348.method_7611(i).method_7677().method_7972();
                this.field_13350.method_32338(stack, false);
                this.field_13348.method_7611(i).method_7673(stack);
            }
        }
        CbItemRecipeMenus.clearCraftingContent(this.field_13348);
    }

    /** handleRecipeClicked(recipe, placeAll) */
    public void method_12821(class_8786<R> recipe, boolean placeAll) {
        boolean matches = CbItemRecipeMenus.recipeMatches(this.field_13348, recipe);
        int biggest = this.field_13347.method_7407(recipe, (IntList) null);
        if (matches) {
            int cells = CbItemRecipeMenus.getGridHeight(this.field_13348) * CbItemRecipeMenus.getGridWidth(this.field_13348) + 1;
            for (int i = 0; i < cells; ++i) {
                if (i == CbItemRecipeMenus.getResultSlotIndex(this.field_13348)) {
                    continue;
                }
                class_1799 stack = this.field_13348.method_7611(i).method_7677();
                if (!stack.method_7960() && Math.min(biggest, stack.method_7914()) < stack.method_7947() + 1) {
                    return;
                }
            }
        }
        int amount = this.method_12819(placeAll, biggest, matches);
        IntList picked = new IntArrayList();
        if (this.field_13347.method_7406(recipe.comp_1933(), picked, amount)) {
            int capped = amount;
            for (Integer index : picked) {
                class_1799 stack = StackedContents.method_7405(index);
                if (!stack.method_7960()) {
                    int max = stack.method_7914();
                    if (max < capped) {
                        capped = max;
                    }
                }
            }
            amount = capped;
            if (this.field_13347.method_7406(recipe.comp_1933(), picked, amount)) {
                this.method_12822();
                this.method_12816(CbItemRecipeMenus.getGridWidth(this.field_13348),
                    CbItemRecipeMenus.getGridHeight(this.field_13348), CbItemRecipeMenus.getResultSlotIndex(this.field_13348),
                    recipe, picked.iterator(), amount);
            }
        }
    }

    /** addItemToSlot(stacking index, slot, maxAmount, x, y) */
    public void method_59902(Integer item, int slotIndex, int maxAmount, int x, int y) {
        class_1735 slot = this.field_13348.method_7611(slotIndex);
        class_1799 stack = StackedContents.method_7405(item);
        if (stack.method_7960()) {
            return;
        }
        int left = maxAmount;
        while (left > 0) {
            left = this.method_12824(slot, stack, left);
            if (left == -1) {
                return;
            }
        }
    }

    /** getStackSize(placeAll, biggestCraftableStack, recipeMatches) */
    public int method_12819(boolean placeAll, int biggest, boolean matches) {
        int size = 1;
        if (placeAll) {
            size = biggest;
        } else if (matches) {
            size = Integer.MAX_VALUE;
            int cells = CbItemRecipeMenus.getGridWidth(this.field_13348) * CbItemRecipeMenus.getGridHeight(this.field_13348) + 1;
            for (int i = 0; i < cells; ++i) {
                if (i == CbItemRecipeMenus.getResultSlotIndex(this.field_13348)) {
                    continue;
                }
                class_1799 stack = this.field_13348.method_7611(i).method_7677();
                if (!stack.method_7960() && size > stack.method_7947()) {
                    size = stack.method_7947();
                }
            }
            if (size != Integer.MAX_VALUE) {
                ++size;
            }
        }
        return size;
    }

    /** moveItemToGrid(slot, stack, count): what is left to move, or -1 when the inventory has no such item */
    public int method_12824(class_1735 slot, class_1799 stack, int count) {
        int from = CbEntityTail.findSlotMatchingUnusedItem(this.field_13350, stack);
        if (from == -1) {
            return field_51523;
        }
        class_1799 found = this.field_13350.method_5438(from);
        int moved;
        if (count < found.method_7947()) {
            this.field_13350.method_5434(from, count);
            moved = count;
        } else {
            this.field_13350.method_5441(from);
            moved = found.method_7947();
        }
        if (slot.method_7677().method_7960()) {
            slot.method_7673(found.method_46651(moved));
        } else {
            slot.method_7677().method_7933(moved);
        }
        return count - moved;
    }

    /** testClearGrid(): whether every grid stack fits back into the inventory */
    public boolean method_12825() {
        List<class_1799> pending = new ArrayList<>();
        int free = this.method_12823();
        int cells = CbItemRecipeMenus.getGridWidth(this.field_13348) * CbItemRecipeMenus.getGridHeight(this.field_13348) + 1;
        for (int i = 0; i < cells; ++i) {
            if (i == CbItemRecipeMenus.getResultSlotIndex(this.field_13348)) {
                continue;
            }
            class_1799 stack = this.field_13348.method_7611(i).method_7677().method_7972();
            if (stack.method_7960()) {
                continue;
            }
            int room = this.field_13350.method_7390(stack);
            if (room == -1 && pending.size() <= free) {
                for (class_1799 other : pending) {
                    if (class_1799.method_7984(other, stack) && other.method_7947() != other.method_7914()
                            && other.method_7947() + stack.method_7947() <= other.method_7914()) {
                        other.method_7933(stack.method_7947());
                        stack.method_7939(0);
                        break;
                    }
                }
                if (!stack.method_7960()) {
                    if (pending.size() >= free) {
                        return false;
                    }
                    pending.add(stack);
                }
            } else if (room == -1) {
                return false;
            }
        }
        return true;
    }

    /** getAmountOfFreeSlotsInInventory(): the empty stacks among the main items */
    public int method_12823() {
        int free = 0;
        for (class_1799 stack : this.field_13350.field_7547) {
            if (stack.method_7960()) {
                ++free;
            }
        }
        return free;
    }

    /** PlaceRecipe.addItemToSlot, 1.21.1's bridge to addItemToSlot(Integer, ..) */
    @Override
    public void method_12815(Object item, int slot, int maxAmount, int x, int y) {
        this.method_59902((Integer) item, slot, maxAmount, x, y);
    }
}
