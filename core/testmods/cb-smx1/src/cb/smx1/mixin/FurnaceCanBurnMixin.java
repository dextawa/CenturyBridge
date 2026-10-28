package cb.smx1.mixin;

import cb.smx1.Driver;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

/**
 * AbstractFurnaceBlockEntity.canBurn(RegistryAccess, Recipe, NonNullList, int) (method_11192, private static), which
 * 1.20.1's serverTick asks before it lights the furnace. A HEAD injector capturing every argument: on 1.20.2 the method
 * takes the RecipeHolder (mixinTargets with "wrap", CASE 18), so the handler must still be handed the Recipe itself.
 * While the driver ticks its furnace, the handler keeps the recipe it was handed.
 */
@Mixin(net.minecraft.class_2609.class)
public abstract class FurnaceCanBurnMixin {
    @Inject(method = "method_11192(Lnet/minecraft/class_5455;Lnet/minecraft/class_1860;Lnet/minecraft/class_2371;I)Z",
        at = @At("HEAD"), remap = false)
    private static void cbsmx1$canBurn(net.minecraft.class_5455 registries, net.minecraft.class_1860<?> recipe,
                                       net.minecraft.class_2371<net.minecraft.class_1799> items, int maxStack,
                                       CallbackInfoReturnable<Boolean> cir) {
        if (Driver.ON_DRIVER.get()) {
            Driver.count("furnace-canburn-head");
            Driver.burnRecipe = recipe;
        }
    }
}
