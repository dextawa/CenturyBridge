package top.dext.centurybridge.step.v26_1.rt;

import net.minecraft.advancements.criterion.MinMaxBounds;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.ItemStackTemplate;
import net.minecraft.world.item.crafting.AbstractCookingRecipe;
import net.minecraft.world.item.crafting.CookingBookCategory;
import net.minecraft.world.item.crafting.CraftingBookCategory;
import net.minecraft.world.item.crafting.CraftingRecipe;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.item.crafting.SingleItemRecipe;
import net.minecraft.world.item.crafting.TransmuteRecipe;

/**
 * The arguments 26.1's recipe constructors take, from 1.21.11's (superCtorReshapes of the vanilla recipe classes,
 * for a mod's new and a mod subclass's super(..) alike). 26.1 gathers a recipe's notification flag into
 * Recipe.CommonInfo and its book category and group into a book info record, and keeps results as item stack
 * templates; 1.21.11 took them one by one:
 * <ul>
 * <li>showNotification: 1.21.11's shaped recipe took it (true by default), every other recipe answered
 *     Recipe.showNotification()'s default, true;</li>
 * <li>a result stack: the template of it (1.21.11's recipe codecs required non-empty results);</li>
 * <li>TransmuteRecipe: 1.21.11's took one input and one material and made the result from the input's
 *     components -- 26.1's with exactly one material, the count not added to the result;</li>
 * <li>SingleItemRecipe's group: 26.1 keeps none (stonecutting answers ""); the recipe keeps 1.21.11's
 *     (mixin/CbWallSingleItemRecipeMixin).</li>
 * </ul>
 */
public final class CbWallRecipeCtors {

    private CbWallRecipeCtors() {
    }

    /** a recipe notifying when unlocked: 1.21.11's default */
    public static Recipe.CommonInfo notifying() {
        return new Recipe.CommonInfo(true);
    }

    public static Recipe.CommonInfo commonInfo(boolean showNotification) {
        return new Recipe.CommonInfo(showNotification);
    }

    public static CraftingRecipe.CraftingBookInfo craftingBook(CraftingBookCategory category, String group) {
        return new CraftingRecipe.CraftingBookInfo(category, group);
    }

    public static AbstractCookingRecipe.CookingBookInfo cookingBook(CookingBookCategory category, String group) {
        return new AbstractCookingRecipe.CookingBookInfo(category, group);
    }

    public static ItemStackTemplate template(ItemStack result) {
        return ItemStackTemplate.fromNonEmptyStack(result);
    }

    /** 1.21.11's transmute recipe took exactly one material */
    public static MinMaxBounds.Ints oneMaterial() {
        return TransmuteRecipe.DEFAULT_MATERIAL_COUNT;
    }

    /** ... and made its result without the material count */
    public static boolean materialCountNotInResult() {
        return false;
    }

    /** post of 1.21.11's SingleItemRecipe(group, input, result): the group its group() answers */
    public static void singleItemGroup(SingleItemRecipe recipe, String group) {
        ((CbWallRecipes.SingleItemGroup) recipe).cb$setGroup(group);
    }

    /** post of 1.21.11's StonecutterRecipe(group, input, result) */
    public static void singleItemGroup(net.minecraft.world.item.crafting.StonecutterRecipe recipe, String group) {
        singleItemGroup((SingleItemRecipe) recipe, group);
    }

    /** 1.21.11's SingleItemRecipe result (serializers read it back): the stack of 26.1's template */
    public static ItemStack result(SingleItemRecipe recipe) {
        return ((CbWallRecipes.SingleItemGroup) recipe).cb$resultStack();
    }

    /** a result template as 1.21.11's result stack (a mod's call of result(), its super call) */
    public static ItemStack stack(ItemStackTemplate template) {
        return template.create();
    }
}
