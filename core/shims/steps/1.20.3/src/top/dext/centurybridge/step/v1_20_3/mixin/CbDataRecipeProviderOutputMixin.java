package top.dext.centurybridge.step.v1_20_3.mixin;

import java.util.List;
import java.util.Set;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import top.dext.centurybridge.step.v1_20_3.facade.FinishedRecipe;
import top.dext.centurybridge.step.v1_20_3.rt.CbAdvJson;
import top.dext.centurybridge.step.v1_20_3.rt.CbDataRecipes;

/**
 * Vanilla RecipeProvider's output (its anonymous RecipeOutput) with 1.20.2's accept(FinishedRecipe),
 * the 1.20.2 body: a duplicate id is an error, the recipe's own JSON is written to the recipe path,
 * its advancement (if any) in 1.20.2's JSON form to the advancement path. CbDataRecipes.accept hands
 * a 1.20.2 FinishedRecipe here instead of decoding it into a recipe object, so vanilla's provider
 * writes exactly the file 1.20.2's did.
 */
@Mixin(targets = "net.minecraft.class_2446$1")
public abstract class CbDataRecipeProviderOutputMixin implements CbDataRecipes.RawRecipeSink {

    @Shadow
    @Final
    Set<net.minecraft.class_2960> field_46145;

    @Shadow
    @Final
    List<java.util.concurrent.CompletableFuture<?>> field_46146;

    @Shadow
    @Final
    net.minecraft.class_7403 field_46147;

    @Shadow
    @Final
    net.minecraft.class_2446 field_46148;

    @Override
    public void cb$acceptFinished(FinishedRecipe recipe) {
        if (!field_46145.add(recipe.comp_1230())) {
            throw new IllegalStateException("Duplicate recipe " + recipe.comp_1230());
        }
        CbDataRecipes.RecipeProviderPaths paths = (CbDataRecipes.RecipeProviderPaths) field_46148;
        field_46146.add(net.minecraft.class_2405.method_10320(field_46147, recipe.method_17799(),
            paths.cb$recipePathProvider().method_44107(recipe.comp_1230())));
        net.minecraft.class_8779 advancement = recipe.comp_1235();
        if (advancement != null) {
            field_46146.add(net.minecraft.class_2405.method_10320(field_46147, CbAdvJson.advancementToJson(advancement.comp_1920()),
                paths.cb$advancementPathProvider().method_44107(advancement.comp_1919())));
        }
    }
}
