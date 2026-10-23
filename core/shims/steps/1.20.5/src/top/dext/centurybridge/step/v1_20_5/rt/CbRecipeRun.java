package top.dext.centurybridge.step.v1_20_5.rt;

/**
 * 1.20.4's overridable RecipeProvider.run(DataWriter) on 1.20.5's final one. 1.20.5 made run(writer) final
 * (it waits for the registries, then calls run(writer, registries)), so a 1.20.4 subclass's override -- a vanilla
 * RecipeProvider's, a VanillaRecipeProvider's, or Fabric API's FabricRecipeProvider's -- would not load. Such an
 * override is renamed {@code cb$recipeRun} (overrideRenames under RecipeProvider) and the class implements
 * {@link Legacy}; CbRecipeRunMixin hands the final run(writer) to it, and the override's own super.run(writer)
 * reaches the final body through the guard, which waits for the registries and runs run(writer, registries) --
 * FabricRecipeProvider's generation in 1.20.5 terms, vanilla's own for a vanilla subclass.
 */
public final class CbRecipeRun {

    private CbRecipeRun() {
    }

    /** a 1.20.4 RecipeProvider subclass that overrode run(writer) */
    public interface Legacy {
        java.util.concurrent.CompletableFuture<?> cb$recipeRun(net.minecraft.class_7403 writer);
    }
}
