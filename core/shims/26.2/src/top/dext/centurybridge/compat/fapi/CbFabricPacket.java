package top.dext.centurybridge.compat.fapi;

/**
 * Stand-in for fabric-api's removed FabricPacket (the 1.20.x typed-packet
 * API, replaced by vanilla CustomPacketPayload in 1.20.5). Mods' packet
 * classes implement this after the converter renames the interface; the
 * wire format is the packet's own write() bytes on a raw channel named by
 * its PacketType id -- exactly the 1.20.1 wire bytes.
 */
public interface CbFabricPacket {
    void write(net.minecraft.network.FriendlyByteBuf buf);

    CbPacketType<?> getType();
}
