package cb.smx1.mixin;

import cb.smx1.Driver;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

/**
 * PathNavigation.isInLiquid() (method_6351, protected; 1.20.2 dropped it and asks Entity.isInLiquid instead). While
 * the driver asks for it, the handler answers true, as a 1.20.1 mod letting a navigation treat its mob as swimming
 * would; GroundPathNavigation.canUpdatePath then answers true for a mob in mid-air.
 */
@Mixin(net.minecraft.class_1408.class)
public abstract class NavigationMixin {
    @Inject(method = "method_6351()Z", at = @At("HEAD"), cancellable = true, remap = false)
    private void cbsmx1$inLiquid(CallbackInfoReturnable<Boolean> cir) {
        if (Driver.forceLiquid) {
            Driver.count("navigation-in-liquid-head");
            cir.setReturnValue(true);
        }
    }
}
