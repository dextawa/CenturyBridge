package top.dext.centurybridge.step.v1_20_5.compat.fapi.registry;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import net.fabricmc.fabric.api.registry.FabricBrewingRecipeRegistryBuilder;

/**
 * Fabric API 0.97.3's (1.20.4) FabricBrewingRecipeRegistry, removed in 1.20.5. 1.20.4 added a recipe to
 * vanilla's global brewing lists, after vanilla's own, the moment it was registered; 1.20.5 builds the
 * brewing recipes per world from a builder (BrewingRecipeRegistry.create), and Fabric API's BUILD event adds
 * to each builder after vanilla's own. A registered recipe is kept and added to every builder from one BUILD
 * listener, in registration order -- the same recipes, in the same place among vanilla's. The checks and
 * messages are 1.20.4's.
 */
public final class FabricBrewingRecipeRegistry {

    private static final List<java.util.function.Consumer<net.minecraft.class_1845.class_9665>> RECIPES = new ArrayList<>();
    private static boolean listening;

    private FabricBrewingRecipeRegistry() {
    }

    public static void registerItemRecipe(net.minecraft.class_1812 input, net.minecraft.class_1856 ingredient, net.minecraft.class_1812 output) {
        Objects.requireNonNull(input, "Input cannot be null!");
        Objects.requireNonNull(ingredient, "Ingredient cannot be null!");
        Objects.requireNonNull(output, "Output cannot be null!");
        add(builder -> ((FabricBrewingRecipeRegistryBuilder) (Object) builder).registerItemRecipe(input, ingredient, output));
    }

    public static void registerPotionRecipe(net.minecraft.class_1842 input, net.minecraft.class_1856 ingredient, net.minecraft.class_1842 output) {
        Objects.requireNonNull(input, "Input cannot be null!");
        Objects.requireNonNull(ingredient, "Ingredient cannot be null!");
        Objects.requireNonNull(output, "Output cannot be null");
        add(builder -> ((FabricBrewingRecipeRegistryBuilder) (Object) builder).registerPotionRecipe(
            net.minecraft.class_7923.field_41179.method_47983(input), ingredient, net.minecraft.class_7923.field_41179.method_47983(output)));
    }

    private static synchronized void add(java.util.function.Consumer<net.minecraft.class_1845.class_9665> recipe) {
        RECIPES.add(recipe);
        if (!listening) {
            listening = true;
            FabricBrewingRecipeRegistryBuilder.BUILD.register(FabricBrewingRecipeRegistry::build);
        }
    }

    private static synchronized void build(net.minecraft.class_1845.class_9665 builder) {
        for (var recipe : RECIPES) {
            recipe.accept(builder);
        }
    }
}
