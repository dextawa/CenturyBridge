package top.dext.centurybridge.rt.v1_20_2;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

/**
 * The 1.20.1 ClientPlayPacketListener.onFeatures(UpdateEnabledFeaturesS2CPacket) of a
 * ClientPlayNetworkHandler subclass that overrides it (adapterHooks). 1.20.1's server sent the enabled
 * features in the play phase, right after the login packet; 1.20.2 sends them in the configuration
 * phase to the configuration listener, which hands them to the play handler's constructor, so vanilla
 * never calls a play handler's onFeatures. The override is called where 1.20.1's server put the packet:
 * once the login packet is handled, with the handler's features (CbNetLegacyPlayFeaturesDelivery).
 */
@Environment(EnvType.CLIENT)
public interface CbNetLegacyPlayFeatures {

    /** onFeatures(packet) */
    void method_45728(net.minecraft.class_7832 packet);
}
