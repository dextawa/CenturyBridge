package top.dext.centurybridge.step.v1_20_5.rt;

import net.minecraft.class_2540;

/**
 * 1.20.4's CustomPacketPayload.write(FriendlyByteBuf), on a 1.20.4 mod's own payload class (area
 * s4net). The converter adds this interface to every mod class implementing CustomPacketPayload that
 * declares write (adapterHooks); vanilla payload classes never have it. A payload of such a class is
 * sent as 1.20.4 sent it -- its id, then what its write puts in the buffer (CbNetPayloadCodecBridge) --
 * whatever codec 1.20.5 has for that id.
 */
public interface CbNetLegacyPayload {

    /** was CustomPacketPayload.write(FriendlyByteBuf) */
    void method_53028(class_2540 buf);
}
