package top.dext.centurybridge.step.v1_20_5.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import top.dext.centurybridge.step.v1_20_5.rt.CbBlockBanner;

/**
 * A placed banner item on the client, for a banner class overriding 1.20.4's fromItem(stack): 1.20.4's
 * AbstractBannerBlock.setPlacedBy called that override on the client for a block entity of the vanilla
 * banner type; 1.20.5 applies the stack's components on placement (applyComponentsFromItemStack), and
 * the override runs after that, where 1.20.4 ran it.
 */
@Mixin(net.minecraft.class_2586.class)
public abstract class CbBlockBannerPlacedMixin {

    @Shadow
    protected net.minecraft.class_1937 field_11863;

    @Inject(method = "method_58683", at = @At("TAIL"))
    private void cb$legacyPlaced(net.minecraft.class_1799 stack, CallbackInfo ci) {
        if ((Object) this instanceof net.minecraft.class_2573 banner && banner instanceof CbBlockBanner.LegacyFromItem legacy
                && this.field_11863 != null && this.field_11863.method_8608()
                && banner.method_11017() == net.minecraft.class_2591.field_11905) {
            legacy.method_38992(stack);
        }
    }
}
