package top.dext.centurybridge.step.v1_21_2.facade;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import java.util.List;
import java.util.Set;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import top.dext.centurybridge.step.v1_21_2.rt.CbRestRecipeBooks;
import top.dext.centurybridge.step.v1_21_2.rt.CbRestRecipeView;

/**
 * RecipeCollection of 1.21.1 (net/minecraft/class_516; classRenamed -- 1.21.2's class of that name holds
 * RecipeDisplayEntries): a group of recipes the recipe book shows on one button, with which of them the
 * player knows, which fit the grid and which can be crafted. 1.21.1's body over the recipes the server sent
 * (CbRestRecipeSync), asking the book and the recipes as 1.21.1 asked them (CbRestRecipeBooks, CbRestRecipeView).
 */
@Environment(EnvType.CLIENT)
public class RecipeCollection {

    private final net.minecraft.class_5455 registryAccess;
    private final List<net.minecraft.class_8786<?>> recipes;
    private final boolean singleResultItem;
    private final Set<net.minecraft.class_8786<?>> craftable = Sets.newHashSet();
    private final Set<net.minecraft.class_8786<?>> fitsDimensions = Sets.newHashSet();
    private final Set<net.minecraft.class_8786<?>> known = Sets.newHashSet();

    public RecipeCollection(net.minecraft.class_5455 registryAccess, List<net.minecraft.class_8786<?>> recipes) {
        this.registryAccess = registryAccess;
        this.recipes = ImmutableList.copyOf(recipes);
        if (recipes.size() <= 1) {
            this.singleResultItem = true;
        } else {
            this.singleResultItem = allRecipesHaveSameResult(registryAccess, recipes);
        }
    }

    private static boolean allRecipesHaveSameResult(net.minecraft.class_5455 registryAccess, List<net.minecraft.class_8786<?>> recipes) {
        int n = recipes.size();
        net.minecraft.class_1799 first = CbRestRecipeView.resultItem(recipes.get(0).comp_1933(), registryAccess);
        for (int i = 1; i < n; i++) {
            net.minecraft.class_1799 other = CbRestRecipeView.resultItem(recipes.get(i).comp_1933(), registryAccess);
            if (!net.minecraft.class_1799.method_31577(first, other)) {
                return false;
            }
        }
        return true;
    }

    /** registryAccess() */
    public net.minecraft.class_5455 method_48479() {
        return this.registryAccess;
    }

    /** hasKnownRecipes() */
    public boolean method_2652() {
        return !this.known.isEmpty();
    }

    /** updateKnownRecipes(book) */
    public void method_2647(net.minecraft.class_3439 book) {
        for (net.minecraft.class_8786<?> recipe : this.recipes) {
            if (CbRestRecipeBooks.contains(book, recipe)) {
                this.known.add(recipe);
            }
        }
    }

    /** canCraft(stackedContents, width, height, book) */
    public void method_2649(net.minecraft.class_1662<?> stackedContents, int width, int height, net.minecraft.class_3439 book) {
        net.minecraft.class_9875 items = CbRestRecipeBooks.stackedItems(stackedContents);
        for (net.minecraft.class_8786<?> recipe : this.recipes) {
            boolean fits = CbRestRecipeView.fits(recipe.comp_1933(), width, height) && CbRestRecipeBooks.contains(book, recipe);
            if (fits) {
                this.fitsDimensions.add(recipe);
            } else {
                this.fitsDimensions.remove(recipe);
            }
            if (fits && CbRestRecipeView.canCraft(items, recipe.comp_1933())) {
                this.craftable.add(recipe);
            } else {
                this.craftable.remove(recipe);
            }
        }
    }

    /** isCraftable(recipe) */
    public boolean method_2653(net.minecraft.class_8786<?> recipe) {
        return this.craftable.contains(recipe);
    }

    /** hasCraftable() */
    public boolean method_2655() {
        return !this.craftable.isEmpty();
    }

    /** hasFitting() */
    public boolean method_2657() {
        return !this.fitsDimensions.isEmpty();
    }

    /** getRecipes() */
    public List<net.minecraft.class_8786<?>> method_2650() {
        return this.recipes;
    }

    /** getRecipes(craftableOnly) */
    public List<net.minecraft.class_8786<?>> method_2651(boolean craftableOnly) {
        List<net.minecraft.class_8786<?>> out = Lists.newArrayList();
        Set<net.minecraft.class_8786<?>> set = craftableOnly ? this.craftable : this.fitsDimensions;
        for (net.minecraft.class_8786<?> recipe : this.recipes) {
            if (set.contains(recipe)) {
                out.add(recipe);
            }
        }
        return out;
    }

    /** getDisplayRecipes(craftable) */
    public List<net.minecraft.class_8786<?>> method_2648(boolean craftable) {
        List<net.minecraft.class_8786<?>> out = Lists.newArrayList();
        for (net.minecraft.class_8786<?> recipe : this.recipes) {
            if (this.fitsDimensions.contains(recipe) && this.craftable.contains(recipe) == craftable) {
                out.add(recipe);
            }
        }
        return out;
    }

    /** hasSingleResultItem() */
    public boolean method_2656() {
        return this.singleResultItem;
    }
}
