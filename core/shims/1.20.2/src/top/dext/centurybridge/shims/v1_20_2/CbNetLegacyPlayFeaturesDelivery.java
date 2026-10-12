package top.dext.centurybridge.shims.v1_20_2;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import top.dext.centurybridge.rt.v1_20_2.CbNetLegacyPlayFeatures;

/**
 * The enabled features for a 1.20.1 ClientPlayNetworkHandler subclass that overrides onFeatures
 * (CbNetLegacyPlayFeatures): 1.20.1's server sent UpdateEnabledFeaturesS2CPacket in the play phase right
 * after the login packet (PlayerList.placeNewPlayer: login, then features); 1.20.2's goes to the
 * configuration listener only. Once the login packet is handled, the handler's override gets the packet
 * 1.20.1 would have sent next: the features the handler was built with, as their ids.
 */
@Mixin(net.minecraft.class_2678.class)
public abstract class CbNetLegacyPlayFeaturesDelivery {

    @Inject(method = "method_11567", at = @At("TAIL"))
    private void cb$legacyFeatures(net.minecraft.class_2602 listener, CallbackInfo ci) {
        if (listener instanceof CbNetLegacyPlayFeatures legacy && listener instanceof net.minecraft.class_634 handler) {
            legacy.method_45728(new net.minecraft.class_7832(
                net.minecraft.class_7701.field_40180.method_45392(handler.method_45735())));
        }
    }
}
