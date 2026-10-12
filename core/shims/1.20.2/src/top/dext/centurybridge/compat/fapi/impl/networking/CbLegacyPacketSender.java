package top.dext.centurybridge.compat.fapi.impl.networking;

/**
 * The part of a 1.20.1 PacketSender implementation the bridge reaches: building a play packet for a
 * channel and its payload. The converter adds this interface to a mod class implementing PacketSender
 * the 1.20.1 way (overrideHooks bridge mode); the class's own createPacket implements it.
 */
public interface CbLegacyPacketSender {

    net.minecraft.class_2596<?> createPacket(net.minecraft.class_2960 channelName, net.minecraft.class_2540 buf);
}
