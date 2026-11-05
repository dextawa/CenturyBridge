package cb.sl6.mixin;

import cb.sl6.Count;
import com.llamalad7.mixinextras.sugar.Local;
import net.minecraft.class_1799;
import net.minecraft.class_1937;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

/**
 * Unbindable: the same local picked by ordinal 0. On 1.21 no local is declared Level and an ordinal pick is not
 * retyped: the injector is disabled with a reported loss (no PREPARE crash) and never runs.
 */
@Mixin(targets = "net/minecraft/class_2357$10")
public abstract class OrdinalLevelMixin {
    @Inject(method = "method_10135", at = @At(value = "INVOKE",
        target = "Lnet/minecraft/class_2530;method_10738(Lnet/minecraft/class_1937;Lnet/minecraft/class_2338;)V"))
    private void cbsl6$ordinal(CallbackInfoReturnable<class_1799> cir, @Local(ordinal = 0) class_1937 level) {
        Count.hit("ordinal");
    }
}
