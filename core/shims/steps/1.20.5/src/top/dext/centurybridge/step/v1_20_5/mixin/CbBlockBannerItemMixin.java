package top.dext.centurybridge.step.v1_20_5.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import top.dext.centurybridge.step.v1_20_5.rt.CbBlockBanner;

/**
 * fromItem(stack, color) of a banner class overriding 1.20.4's fromItem(stack): 1.20.4's body -- the
 * color, then the override -- where 1.20.5 applies the stack's components itself (CbBlockBanner).
 */
@Mixin(net.minecraft.class_2573.class)
public abstract class CbBlockBannerItemMixin {

    @Inject(method = "method_10913", at = @At("HEAD"), cancellable = true)
    private void cb$legacyFromItem(net.minecraft.class_1799 stack, net.minecraft.class_1767 color, CallbackInfo ci) {
        if ((Object) this instanceof CbBlockBanner.LegacyFromItem legacy) {
            CbBlockBanner.fromItemWithColor(legacy, stack, color);
            ci.cancel();
        }
    }
}
