package top.dext.centurybridge.step.v1_21_2.rt;

import java.util.Map;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.class_10297;
import net.minecraft.class_10298;
import net.minecraft.class_1662;
import net.minecraft.class_299;
import net.minecraft.class_3439;
import net.minecraft.class_3441;
import net.minecraft.class_5321;
import net.minecraft.class_8786;
import net.minecraft.class_9875;
import top.dext.centurybridge.step.v1_21_2.mixin.CbRestClientRecipeBookAccessor;

/**
 * 1.21.1's recipe-book questions about recipes (RecipeHolders) on 1.21.2's recipe books, which know recipes by
 * key (server) or by display id (client): whether a book contains a recipe, whether the client highlights it.
 * On the client a recipe is known when one of its displays is known (the server sends a recipe's displays
 * into the book together), highlighted when one of them is ({@link CbRestRecipesClient}'s table).
 */
@Environment(EnvType.CLIENT)
public final class CbRestRecipeBooks {

    private CbRestRecipeBooks() {
    }

    /** RecipeBook.contains(recipe) */
    public static boolean contains(class_3439 book, class_8786<?> recipe) {
        if (recipe == null) {
            return false;
        }
        if (book instanceof class_3441 server) {
            return server.method_64593(recipe.comp_1932());
        }
        if (book instanceof class_299 client) {
            Map<class_10298, class_10297> known = ((CbRestClientRecipeBookAccessor) client).cb$known();
            for (int id : CbRestRecipesClient.displayIds(recipe)) {
                if (known.containsKey(new class_10298(id))) {
                    return true;
                }
            }
        }
        return false;
    }

    /** RecipeBook.contains(id) */
    public static boolean contains(class_3439 book, net.minecraft.class_2960 id) {
        if (book instanceof class_3441 server) {
            return server.method_64593(class_5321.method_29179(net.minecraft.class_7924.field_52178, id));
        }
        return contains(book, CbRestRecipesClient.byId(id));
    }

    /** RecipeBook.willHighlight(recipe) */
    public static boolean willHighlight(class_3439 book, class_8786<?> recipe) {
        if (book instanceof class_299 client && recipe != null) {
            for (int id : CbRestRecipesClient.displayIds(recipe)) {
                if (client.method_64855(new class_10298(id))) {
                    return true;
                }
            }
        }
        return false;
    }

    /** RecipeBook.removeHighlight(recipe), on the client book */
    public static void removeHighlight(class_3439 book, class_8786<?> recipe) {
        if (book instanceof class_299 client && recipe != null) {
            for (int id : CbRestRecipesClient.displayIds(recipe)) {
                client.method_64856(new class_10298(id));
            }
        }
    }

    /** RecipeBook.isFiltering(menu): the filter setting of the menu's book type */
    public static boolean isFiltering(class_3439 book, net.minecraft.class_1729 menu) {
        return book.method_30176(menu.method_30264());
    }

    /** a 1.21.1 StackedContents as 1.21.2's StackedItemContents reading the same counts (CbRestRecipeMenus) */
    public static class_9875 stackedItems(class_1662<?> raw) {
        return CbRestRecipeMenus.stackedItems(raw);
    }
}
