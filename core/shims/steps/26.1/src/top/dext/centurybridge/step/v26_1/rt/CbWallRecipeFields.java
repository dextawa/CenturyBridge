package top.dext.centurybridge.step.v26_1.rt;

import net.minecraft.world.item.crafting.CraftingBookCategory;
import net.minecraft.world.item.crafting.NormalCraftingRecipe;

/**
 * 1.21.11 ShapedRecipe / ShapelessRecipe / TransmuteRecipe group, category and (shaped) showNotification fields
 * (class_1869 / class_1867 / class_10223, opened by access wideners; each constructor stored its arguments there).
 * 26.1 (javap): all three extend NormalCraftingRecipe, whose constructor keeps the same values in commonInfo
 * (showNotification) and bookInfo (category, group) and answers them through its final group() / category() /
 * showNotification(). The fields were final in 1.21.11, so there are no writes to answer.
 */
public final class CbWallRecipeFields {
    private CbWallRecipeFields() {
    }

    public static String group(NormalCraftingRecipe r) {
        return r.group();
    }

    public static CraftingBookCategory category(NormalCraftingRecipe r) {
        return r.category();
    }

    public static boolean showNotification(NormalCraftingRecipe r) {
        return r.showNotification();
    }
}
