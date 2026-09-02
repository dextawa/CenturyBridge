package top.dext.centurybridge.compat.fapi;

import java.util.ArrayList;
import java.util.List;

import net.fabricmc.fabric.api.registry.FabricPotionBrewingBuilder;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.item.alchemy.Potion;
import net.minecraft.world.item.crafting.Ingredient;

/** fabric-content-registries-v0's FabricBrewingRecipeRegistry (1 corpus mod): brewing recipes are built per server since 1.21 (FabricPotionBrewingBuilder.BUILD); registrations are recorded and replayed. */
public final class CbFabricBrewingRecipeRegistry {

    private record PotionRecipe(Potion input, Ingredient ingredient, Potion output) {
    }

    private static final List<PotionRecipe> POTIONS = new ArrayList<>();

    static {
        FabricPotionBrewingBuilder.BUILD.register(builder -> {
            FabricPotionBrewingBuilder fb = (FabricPotionBrewingBuilder) (Object) builder;
            synchronized (POTIONS) {
                for (PotionRecipe r : POTIONS) {
                    fb.registerPotionRecipe(BuiltInRegistries.POTION.wrapAsHolder(r.input), r.ingredient, BuiltInRegistries.POTION.wrapAsHolder(r.output));
                }
            }
        });
    }

    public static void registerPotionRecipe(Potion input, Ingredient ingredient, Potion output) {
        synchronized (POTIONS) {
            POTIONS.add(new PotionRecipe(input, ingredient, output));
        }
    }

    private CbFabricBrewingRecipeRegistry() {
    }
}
