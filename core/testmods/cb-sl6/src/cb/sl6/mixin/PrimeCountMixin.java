package cb.sl6.mixin;

import cb.sl6.Count;
import net.minecraft.class_1799;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

/** no locals: counts the prime call itself, the reference for the @Local handlers */
@Mixin(targets = "net/minecraft/class_2357$10")
public abstract class PrimeCountMixin {
    @Inject(method = "method_10135", at = @At(value = "INVOKE",
        target = "Lnet/minecraft/class_2530;method_10738(Lnet/minecraft/class_1937;Lnet/minecraft/class_2338;)V"))
    private void cbsl6$prime(CallbackInfoReturnable<class_1799> cir) {
        Count.hit("primed");
    }
}
