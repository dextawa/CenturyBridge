package top.dext.centurybridge.step.v1_21_2.facade;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import top.dext.centurybridge.step.v1_21_2.rt.CbRestRecipeBookClient;

/**
 * RecipeUpdateListener of 1.21.1 (net/minecraft/class_518; classRenamed): a screen with a recipe book, told when
 * the recipes changed and asked for its component. 1.21.2's interface of that name replaced the component
 * question by fillGhostRecipe(display); this one extends it, so 1.21.2 tells a 1.21.1 screen of recipe changes
 * as before, and a ghost recipe it sends goes to the screen's component as 1.21.1's client handed it on (the
 * recipe the display belongs to, set up on the open menu's slots). 1.21.2's own recipe-book screens answer for
 * their component too (CbRestRecipeBookScreenMixin).
 */
@Environment(EnvType.CLIENT)
public interface RecipeUpdateListener extends net.minecraft.class_518 {

    /** getRecipeBookComponent() */
    RecipeBookComponent method_2659();

    @Override
    default void method_64862(net.minecraft.class_10295 display) {
        CbRestRecipeBookClient.fillGhostRecipe(this, display);
    }
}
