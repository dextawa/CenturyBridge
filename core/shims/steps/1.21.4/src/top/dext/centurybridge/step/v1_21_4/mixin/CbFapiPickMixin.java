package top.dext.centurybridge.step.v1_21_4.mixin;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import top.dext.centurybridge.step.v1_21_4.compat.fapi.interaction.CbFapiPick;
import top.dext.centurybridge.step.v1_21_4.rt.CbClientRest;

/**
 * While a 1.21.3 mod listens to Fabric API's client pick-block events, or the game's MultiPlayerGameMode is a 1.21.3 subclass
 * overriding handlePickItem, the client picks as 1.21.3's did (CbFapiPick)
 */
@Environment(EnvType.CLIENT)
@Mixin(net.minecraft.class_310.class)
public abstract class CbFapiPickMixin {

    @Inject(method = "method_1511", at = @At("HEAD"), cancellable = true)
    private void cb$legacyPick(CallbackInfo ci) {
        net.minecraft.class_310 mc = (net.minecraft.class_310) (Object) this;
        if (CbFapiPick.listened() || mc.field_1761 instanceof CbClientRest.CbLegacyPickItem) {
            CbFapiPick.pick(mc);
            ci.cancel();
        }
    }
}
