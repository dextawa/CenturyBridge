package top.dext.centurybridge.step.v1_21_2.facade;

import java.util.Iterator;
import net.minecraft.class_1860;
import net.minecraft.class_1869;
import net.minecraft.class_3532;
import net.minecraft.class_8786;

/**
 * 1.21.1 PlaceRecipe (net/minecraft/class_2952), gone in 1.21.2 (its grid walk became the private PlaceRecipeHelper):
 * placeRecipe(gridWidth, gridHeight, resultSlot, recipe, ingredients, maxAmount) lays the ingredients out on the grid --
 * a shaped recipe by its own width and height, centred -- handing each to addItemToSlot. The body is 1.21.1's.
 */
public interface PlaceRecipe<T> {

    /** placeRecipe(gridWidth, gridHeight, resultSlot, recipe, ingredients, maxAmount) */
    default void method_12816(int gridWidth, int gridHeight, int resultSlot, class_8786<?> recipe, Iterator<T> ingredients,
            int maxAmount) {
        int width = gridWidth;
        int height = gridHeight;
        class_1860<?> value = recipe.comp_1933();
        if (value instanceof class_1869 shaped) {
            width = shaped.method_8150();
            height = shaped.method_8158();
        }
        int slot = 0;
        for (int y = 0; y < gridHeight; ++y) {
            if (slot == resultSlot) {
                ++slot;
            }
            boolean narrower = (float) height < (float) gridHeight / 2.0F;
            int offset = class_3532.method_15375((float) gridHeight / 2.0F - (float) height / 2.0F);
            if (narrower && offset > y) {
                slot += gridWidth;
                ++y;
            }
            for (int x = 0; x < gridWidth; ++x) {
                if (!ingredients.hasNext()) {
                    return;
                }
                narrower = (float) width < (float) gridWidth / 2.0F;
                offset = class_3532.method_15375((float) gridWidth / 2.0F - (float) width / 2.0F);
                int end = width;
                boolean inside = x < width;
                if (narrower) {
                    end = offset + width;
                    inside = offset <= x && x < offset + width;
                }
                if (inside) {
                    this.method_12815(ingredients.next(), slot, maxAmount, x, y);
                } else if (end == x) {
                    slot += gridWidth - x;
                    break;
                }
                ++slot;
            }
        }
    }

    /** addItemToSlot(ingredient, slot, maxAmount, x, y) */
    void method_12815(T ingredient, int slot, int maxAmount, int x, int y);
}
