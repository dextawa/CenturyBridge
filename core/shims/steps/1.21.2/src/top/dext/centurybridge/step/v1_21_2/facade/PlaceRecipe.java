package top.dext.centurybridge.step.v1_21_2.facade;

import java.util.Iterator;

/**
 * PlaceRecipe of 1.21.1 (net/minecraft/class_2952, gone in 1.21.2 -- PlaceRecipeHelper took its place;
 * classRenamed): lays a recipe's ingredients out on a crafting grid, handing each to addItemToSlot with its
 * slot index and grid position. 1.21.1's body (a shaped recipe's own width and height, centred in the grid).
 * 1.21.1 handed gaps of a shaped recipe on as Ingredient.EMPTY; 1.21.2 has no empty ingredient, so a gap
 * (a null element) is stepped over without a call -- 1.21.1's own implementations ignored empty ones.
 */
public interface PlaceRecipe<T> {

    /** placeRecipe(width, height, outputSlot, recipe, ingredients, maxAmount) */
    default void method_12816(int width, int height, int outputSlot, net.minecraft.class_8786<?> recipe, Iterator<T> ingredients, int maxAmount) {
        int recipeWidth = width;
        int recipeHeight = height;
        if (recipe.comp_1933() instanceof net.minecraft.class_1869 shaped) {
            recipeWidth = shaped.method_8150();
            recipeHeight = shaped.method_8158();
        }
        int slot = 0;
        for (int y = 0; y < height; y++) {
            if (slot == outputSlot) {
                slot++;
            }
            boolean centreY = (float) recipeHeight < (float) height / 2.0F;
            int offsetY = net.minecraft.class_3532.method_15375((float) height / 2.0F - (float) recipeHeight / 2.0F);
            if (centreY && offsetY > y) {
                slot += width;
                y++;
            }
            for (int x = 0; x < width; x++) {
                if (!ingredients.hasNext()) {
                    return;
                }
                boolean centreX = (float) recipeWidth < (float) width / 2.0F;
                int offsetX = net.minecraft.class_3532.method_15375((float) width / 2.0F - (float) recipeWidth / 2.0F);
                int end = recipeWidth;
                boolean inside = x < recipeWidth;
                if (centreX) {
                    end = offsetX + recipeWidth;
                    inside = offsetX <= x && x < offsetX + recipeWidth;
                }
                if (inside) {
                    T ingredient = ingredients.next();
                    if (ingredient != null) {
                        this.method_12815(ingredient, slot, maxAmount, x, y);
                    }
                } else if (end == x) {
                    slot += width - x;
                    break;
                }
                slot++;
            }
        }
    }

    /** addItemToSlot(ingredient, slot, maxAmount, x, y) */
    void method_12815(T ingredient, int slot, int maxAmount, int x, int y);
}
