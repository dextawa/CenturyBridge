package top.dext.centurybridge.step.v1_21_2.mixin;

import net.minecraft.class_10289;
import net.minecraft.class_1863;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

/** RecipeManager.recipes (private), set by the 1.21.1 recipe synchronisation (CbRestRecipeSync / CbRestRecipesClient) */
@Mixin(class_1863.class)
public interface CbRestRecipeManagerAccessor {

    @Accessor("field_54638")
    void cb$setRecipes(class_10289 recipes);
}
