package top.dext.centurybridge.shims.v1_20_2;

import net.minecraft.class_310;
import net.minecraft.class_7832;
import net.minecraft.class_8673;
import net.minecraft.class_8674;
import net.minecraft.class_8675;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import top.dext.centurybridge.rt.v1_20_2.CbNetListeners;

/**
 * ClientConfigurationPacketListenerImpl.handleEnabledFeatures: remembers, for the connection, the
 * features packet the configuration phase received, which 1.20.1's server sent in the play phase
 * (CbNetListenerFeaturesDelivery hands it to a 1.20.1 play listener after the login packet).
 */
@Mixin(class_8674.class)
public abstract class CbNetListenerConfigFeatures extends class_8673 {

    protected CbNetListenerConfigFeatures(class_310 client, net.minecraft.class_2535 connection, class_8675 cookie) {
        super(client, connection, cookie);
    }

    @Inject(method = "method_52796", at = @At("HEAD"))
    private void cb$configuredFeatures(class_7832 packet, CallbackInfo ci) {
        CbNetListeners.configured(this.field_45589, packet);
    }
}
