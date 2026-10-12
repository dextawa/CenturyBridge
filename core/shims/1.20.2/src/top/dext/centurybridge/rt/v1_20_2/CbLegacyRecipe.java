package top.dext.centurybridge.rt.v1_20_2;

/**
 * The 1.20.1 Recipe.getId() shape. 1.20.2 removed it (the id lives in RecipeEntry), so a
 * converted recipe class's own getId() overrides nothing; the converter adds this interface to
 * every recipe class that declares one (adapterHooks), and {@link CbRecipeIds#idOf} -- what a
 * converted recipe.getId() call runs -- asks the recipe itself, as 1.20.1's virtual dispatch did.
 */
public interface CbLegacyRecipe {

    /** getId() */
    net.minecraft.class_2960 method_8114();
}
