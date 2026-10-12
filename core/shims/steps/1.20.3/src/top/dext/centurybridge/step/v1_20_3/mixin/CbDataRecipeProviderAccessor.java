package top.dext.centurybridge.step.v1_20_3.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;
import top.dext.centurybridge.step.v1_20_3.rt.CbDataRecipes;

/** RecipeProvider's recipe and advancement path providers (package-private), for CbDataRecipeProviderOutputMixin */
@Mixin(net.minecraft.class_2446.class)
public interface CbDataRecipeProviderAccessor extends CbDataRecipes.RecipeProviderPaths {

    @Override
    @Accessor("field_39378")
    net.minecraft.class_7784.class_7489 cb$recipePathProvider();

    @Override
    @Accessor("field_39379")
    net.minecraft.class_7784.class_7489 cb$advancementPathProvider();
}
