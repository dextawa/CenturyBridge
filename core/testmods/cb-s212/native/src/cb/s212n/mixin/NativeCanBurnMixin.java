package cb.s212n.mixin;

import cb.s212n.Counts;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

/** 1.21.2 AbstractFurnaceBlockEntity.canBurn(RegistryAccess, RecipeHolder, SingleRecipeInput, NonNullList, int) */
@Mixin(net.minecraft.class_2609.class)
public abstract class NativeCanBurnMixin {
    @Inject(method = "method_11192(Lnet/minecraft/class_5455;Lnet/minecraft/class_8786;Lnet/minecraft/class_9696;Lnet/minecraft/class_2371;I)Z",
            at = @At("HEAD"), remap = false)
    private static void cbs212n$canBurn(net.minecraft.class_5455 registries, net.minecraft.class_8786<?> recipe, net.minecraft.class_9696 input,
                                        net.minecraft.class_2371<net.minecraft.class_1799> items, int maxStackSize, CallbackInfoReturnable<Boolean> cir) {
        Counts.canBurn++;
    }
}
