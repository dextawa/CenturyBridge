package cb.s212.mixin;

import cb.s212.Driver;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

/**
 * 1.21.1 AbstractFurnaceBlockEntity.getTotalCookTime(Level, AbstractFurnaceBlockEntity) (private static; 1.21.2 takes the
 * ServerLevel): sees both arguments and, while the driver asks, answers 7 instead of the recipe's cooking time.
 */
@Mixin(net.minecraft.class_2609.class)
public abstract class FurnaceCookTimeMixin {
    @Inject(method = "method_17029(Lnet/minecraft/class_1937;Lnet/minecraft/class_2609;)I", at = @At("RETURN"), cancellable = true, remap = false)
    private static void cbs212$cookTime(net.minecraft.class_1937 level, net.minecraft.class_2609 be, CallbackInfoReturnable<Integer> cir) {
        Driver.cookTimeAsked++;
        Driver.cookTimeLevel = level;
        Driver.cookTimeBe = be;
        Driver.cookTimeVanilla = cir.getReturnValue();
        if (Driver.cookTimeOverride) {
            cir.setReturnValue(7);
        }
    }
}
