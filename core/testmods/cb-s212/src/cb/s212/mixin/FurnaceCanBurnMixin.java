package cb.s212.mixin;

import cb.s212.Driver;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

/**
 * 1.21.1 AbstractFurnaceBlockEntity.canBurn(RegistryAccess, RecipeHolder, NonNullList, int maxStackSize) (private
 * static; 1.21.2 adds the SingleRecipeInput third): sees its four arguments and may refuse burning.
 */
@Mixin(net.minecraft.class_2609.class)
public abstract class FurnaceCanBurnMixin {
    @Inject(method = "method_11192(Lnet/minecraft/class_5455;Lnet/minecraft/class_8786;Lnet/minecraft/class_2371;I)Z", at = @At("HEAD"), cancellable = true, remap = false)
    private static void cbs212$canBurn(net.minecraft.class_5455 registries, net.minecraft.class_8786<?> recipe,
                                       net.minecraft.class_2371<net.minecraft.class_1799> items, int maxStackSize,
                                       CallbackInfoReturnable<Boolean> cir) {
        Driver.canBurnAsked++;
        Driver.canBurnRegistries = registries;
        Driver.canBurnRecipe = recipe;
        Driver.canBurnItems = items;
        Driver.canBurnMax = maxStackSize;
        if (Driver.refuseBurn) {
            cir.setReturnValue(false);
        }
    }
}
