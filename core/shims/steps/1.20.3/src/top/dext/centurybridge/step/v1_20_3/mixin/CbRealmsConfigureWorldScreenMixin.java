package top.dext.centurybridge.step.v1_20_3.mixin;

import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;
import top.dext.centurybridge.step.v1_20_3.rt.CbRealms;

/**
 * RealmsConfigureWorldScreen asked the RealmsMainScreen it came from to refreshServerList() in 1.20.2
 * (an instance method then); 1.20.3 calls the static. A main screen of a 1.20.2 subclass that overrides
 * the method (adapterHooks marks it) is asked again, as 1.20.2 asked it.
 */
@Mixin(net.minecraft.class_4390.class)
public abstract class CbRealmsConfigureWorldScreenMixin {

    /** lastScreen */
    @Shadow
    @Final
    private net.minecraft.class_4325 field_19830;

    @Redirect(method = "method_25157", at = @At(value = "INVOKE", target = "Lnet/minecraft/class_4325;method_53793()V"))
    private void cb$refresh() {
        if (field_19830 instanceof CbRealms.CbLegacyRefresh legacy) {
            legacy.method_53793();
        } else {
            net.minecraft.class_4325.method_53793();
        }
    }
}
