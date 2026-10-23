package top.dext.centurybridge.step.v1_20_5.compat.fapi.networking.v1;

/**
 * Fabric API 0.97.3's (1.20.4) FabricPacket, which 1.20.5's Fabric API removed in favour of vanilla's
 * CustomPayload: a packet object written into the channel of its {@link PacketType}. Mods' packets keep
 * implementing it; the layer's networking (impl/networking) carries it in a {@code CbChannelPayload} of
 * its type's channel, written with {@link #write} exactly where 1.20.4 wrote it.
 */
public interface FabricPacket {

    /** writes the packet's contents to the buffer */
    void write(net.minecraft.class_2540 buf);

    /** the type of this packet: its channel and reader */
    PacketType<?> getType();
}
