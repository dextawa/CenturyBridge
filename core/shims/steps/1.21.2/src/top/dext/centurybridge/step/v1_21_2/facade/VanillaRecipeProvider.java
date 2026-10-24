package top.dext.centurybridge.step.v1_21_2.facade;

import java.util.concurrent.CompletableFuture;
import net.minecraft.class_161;
import net.minecraft.class_174;
import net.minecraft.class_2062;
import net.minecraft.class_2405;
import net.minecraft.class_5797;
import net.minecraft.class_7225;
import net.minecraft.class_7403;
import net.minecraft.class_7784;
import net.minecraft.class_7924;
import net.minecraft.class_8779;
import net.minecraft.class_8790;
import top.dext.centurybridge.step.v1_21_2.rt.CbItemDatagenRecipes;

/**
 * 1.21.1's VanillaRecipeProvider (net/minecraft/class_7803) as the superclass of a 1.21.1 mod's provider (superRenames;
 * dev-time datagen). 1.21.2's VanillaRecipeProvider is an instance-helper provider made by its Runner; 1.21.1's was
 * the data provider. This class extends 1.21.2's (so an instance stands where that type is expected) and is 1.21.1's
 * data provider: run(output, registries) is 1.21.1's -- RecipeProvider's run and the recipes' root advancement
 * ("impossible" criterion, recipes/root) -- and buildRecipes(output) builds the vanilla recipes with a 1.21.2
 * VanillaRecipeProvider bound to that output: the vanilla recipes as the game the data is generated for has them.
 */
public class VanillaRecipeProvider extends net.minecraft.class_7803 implements class_2405, CbItemDatagenRecipes.LegacyProvider {

    /** recipePathProvider */
    public final class_7784.class_7489 field_39378;
    /** advancementPathProvider */
    public final class_7784.class_7489 field_39379;
    /** 1.21.1 RecipeProvider's registries future (1.21.2's class has a field of that name) */
    public final CompletableFuture<class_7225.class_7874> cb$registries;

    public VanillaRecipeProvider(class_7784 output, CompletableFuture<class_7225.class_7874> registries) {
        super(CbItemDatagenRecipes.builtIn(), CbItemDatagenRecipes.nowhere());
        this.field_39378 = output.method_60917(class_7924.field_52178);
        this.field_39379 = output.method_60917(class_7924.field_52177);
        this.cb$registries = registries;
    }

    /** run(output): once the registries are there */
    @Override
    public final CompletableFuture<?> method_10319(class_7403 output) {
        return this.cb$registries.thenCompose(registries -> this.method_56888(output, registries));
    }

    /** run(output, registries): RecipeProvider's run and the recipes' root advancement */
    public CompletableFuture<?> method_56888(class_7403 output, class_7225.class_7874 registries) {
        CompletableFuture<?> recipes = CbItemDatagenRecipes.runLegacy(output, registries, this.field_39378, this::method_10419,
            advancement -> this.method_46206(output, registries, advancement));
        class_8779 root = class_161.class_162.method_51698()
            .method_705("impossible", class_174.field_1184.method_53699(new class_2062.class_2063()))
            .method_695(class_5797.field_39377);
        return CompletableFuture.allOf(recipes, this.method_46206(output, registries, root));
    }

    /** buildAdvancement(output, registries, advancement) */
    public CompletableFuture<?> method_46206(class_7403 output, class_7225.class_7874 registries, class_8779 advancement) {
        return class_2405.method_53496(output, registries, class_161.field_47179, advancement.comp_1920(),
            this.field_39379.method_44107(advancement.comp_1919()));
    }

    /** buildRecipes(output): the vanilla recipes, built by a 1.21.2 VanillaRecipeProvider bound to the output */
    protected void method_10419(class_8790 output) {
        CbItemDatagenRecipes.vanillaRecipes(output);
    }

    /** getName() */
    @Override
    public final String method_10321() {
        return "Recipes";
    }
}
