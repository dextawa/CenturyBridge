package cb.smx6.mixin;

import cb.smx6.Driver;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

/**
 * A 1.20.6 mod switching channeling off: ThrownTrident.isChanneling() (class_1685.method_31551) answers false while
 * Driver.cancelChanneling is set. On 1.20.6 that stops onHitEntity's lightning branch.
 */
@Mixin(net.minecraft.class_1685.class)
public abstract class TridentChannelingMixin {
    @Inject(method = "method_31551()Z", at = @At("HEAD"), cancellable = true, remap = false)
    private void cbsmx6$noChanneling(CallbackInfoReturnable<Boolean> cir) {
        Driver.asked++;
        if (Driver.cancelChanneling) {
            cir.setReturnValue(false);
        }
    }
}
