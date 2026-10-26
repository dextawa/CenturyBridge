package cb.swall.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

/**
 * 1.21.11's AbstractFurnaceBlockEntity.burn(registryAccess, recipe, input, items, maxStackSize) (class_2609.method_11203),
 * as 1.21.11-era mods mixed it: counts the calls. On 26.1 the layer restores it, and serverTick's smelting goes through it
 * because this mixin mixes it (the converted jar's mixed-member signal).
 */
@Mixin(net.minecraft.class_2609.class)
public abstract class FurnaceBurnMixin {
    @Inject(method = "method_11203(Lnet/minecraft/class_5455;Lnet/minecraft/class_8786;Lnet/minecraft/class_9696;Lnet/minecraft/class_2371;I)Z",
        at = @At("HEAD"), remap = false)
    private static void cbswall$countBurn(net.minecraft.class_5455 registries, net.minecraft.class_8786<?> recipe, net.minecraft.class_9696 input,
                                          net.minecraft.class_2371<net.minecraft.class_1799> items, int maxStackSize, CallbackInfoReturnable<Boolean> cir) {
        cb.swall.SwallTestMod.BURN_CALLS.incrementAndGet();
    }
}
