package top.dext.centurybridge.step.v1_21_4.mixin;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import top.dext.centurybridge.step.v1_21_4.compat.fapi.interaction.CbFapiPick;

/** While a 1.21.3 mod listens to Fabric API's client pick-block events, the client picks as 1.21.3's did (CbFapiPick) */
@Environment(EnvType.CLIENT)
@Mixin(net.minecraft.class_310.class)
public abstract class CbFapiPickMixin {

    @Inject(method = "method_1511", at = @At("HEAD"), cancellable = true)
    private void cb$legacyPick(CallbackInfo ci) {
        if (CbFapiPick.listened()) {
            CbFapiPick.pick((net.minecraft.class_310) (Object) this);
            ci.cancel();
        }
    }
}
