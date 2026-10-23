package top.dext.centurybridge.rt.v1_20_2;

/**
 * The 1.20.1 ClientPlayPacketListener.onFeatures(UpdateEnabledFeaturesS2CPacket) of a class a mod wrote
 * implementing the play listener interface itself (adapterHooks; a ClientPlayNetworkHandler subclass has
 * CbNetLegacyPlayFeatures). 1.20.1's server sent the enabled features in the play phase right after the
 * login packet; 1.20.2's sends them to the configuration listener of the same connection. The listener
 * is handed that packet where 1.20.1's server put it (CbNetListenerFeaturesDelivery).
 */
public interface CbNetLegacyListenerFeatures {

    /** onFeatures(packet) */
    void method_45728(net.minecraft.class_7832 packet);
}
