package top.dext.centurybridge.compat.fapi;

import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.function.Consumer;

import net.fabricmc.fabric.api.registry.FabricBrewingRecipeRegistryBuilder;
import net.minecraft.class_1812;
import net.minecraft.class_1842;
import net.minecraft.class_1856;
import net.minecraft.class_7923;

/**
 * fabric-content-registries-v0's FabricBrewingRecipeRegistry (removed in 1.21.2: brewing recipes are built per
 * server through FabricBrewingRecipeRegistryBuilder.BUILD). The 1.20.1 statics queue their recipes and one
 * BUILD listener replays them into every builder; potions became Holders (wrapAsHolder on BuiltInRegistries.POTION).
 * classRename: net/fabricmc/fabric/api/registry/FabricBrewingRecipeRegistry -> this class.
 */
public final class CbFabricBrewingRecipeRegistry {

    private static final List<Consumer<FabricBrewingRecipeRegistryBuilder>> PENDING = new CopyOnWriteArrayList<>();

    static {
        FabricBrewingRecipeRegistryBuilder.BUILD.register(builder -> {
            FabricBrewingRecipeRegistryBuilder b = (FabricBrewingRecipeRegistryBuilder) (Object) builder;
            for (Consumer<FabricBrewingRecipeRegistryBuilder> c : PENDING) {
                c.accept(b);
            }
        });
    }

    public static void registerItemRecipe(class_1812 input, class_1856 ingredient, class_1812 output) {
        PENDING.add(b -> b.registerItemRecipe(input, ingredient, output));
    }

    public static void registerPotionRecipe(class_1842 input, class_1856 ingredient, class_1842 output) {
        PENDING.add(b -> b.registerPotionRecipe(class_7923.field_41179.method_47983(input), ingredient,
            class_7923.field_41179.method_47983(output)));
    }

    private CbFabricBrewingRecipeRegistry() {
    }
}
