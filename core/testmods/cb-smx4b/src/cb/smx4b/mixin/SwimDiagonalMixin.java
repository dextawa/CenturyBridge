package cb.smx4b.mixin;

import cb.smx4b.Driver;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

/** 1.20.4 SwimNodeEvaluator.isDiagonalNodeValid(diagonal, a, b): counted; false while Driver.swimNoDiagonal is set */
@Mixin(net.minecraft.class_12.class)
public abstract class SwimDiagonalMixin {
    @Inject(method = "method_38488(Lnet/minecraft/class_9;Lnet/minecraft/class_9;Lnet/minecraft/class_9;)Z",
        at = @At("HEAD"), cancellable = true, remap = false)
    private void cbsmx4b$diagonal(net.minecraft.class_9 diagonal, net.minecraft.class_9 a, net.minecraft.class_9 b,
                                  CallbackInfoReturnable<Boolean> cir) {
        Driver.swimAsked++;
        if (Driver.swimNoDiagonal) {
            cir.setReturnValue(false);
        }
    }
}
