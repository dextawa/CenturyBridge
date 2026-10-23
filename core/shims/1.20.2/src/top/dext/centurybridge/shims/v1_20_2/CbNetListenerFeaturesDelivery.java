package top.dext.centurybridge.shims.v1_20_2;

import net.minecraft.class_2678;
import net.minecraft.class_2602;
import net.minecraft.class_634;
import net.minecraft.class_7832;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import top.dext.centurybridge.rt.v1_20_2.CbNetLegacyListenerFeatures;
import top.dext.centurybridge.rt.v1_20_2.CbNetListeners;

/**
 * The enabled features for a 1.20.1 play listener a mod implemented itself (CbNetLegacyListenerFeatures):
 * 1.20.1's server sent UpdateEnabledFeaturesS2CPacket in the play phase right after the login packet
 * (PlayerList.placeNewPlayer: login, then features); 1.20.2's sends it in the configuration phase of
 * the same connection (ServerConfigurationPacketListenerImpl.startConfiguration). Once the login packet
 * is handled, the listener gets the features packet its connection's configuration phase received
 * (CbNetListenerConfigFeatures). ClientPlayNetworkHandler subclasses are served by
 * CbNetLegacyPlayFeaturesDelivery from the handler's own features. Client side: only a client handles the
 * login packet.
 */
@Mixin(class_2678.class)
public abstract class CbNetListenerFeaturesDelivery {

    @Inject(method = "method_11567", at = @At("TAIL"))
    private void cb$listenerFeatures(class_2602 listener, CallbackInfo ci) {
        if (listener instanceof CbNetLegacyListenerFeatures legacy && !(listener instanceof class_634)) {
            class_7832 features = CbNetListeners.configuredFeatures(listener);
            if (features != null) {
                legacy.method_45728(features);
            }
        }
    }
}
