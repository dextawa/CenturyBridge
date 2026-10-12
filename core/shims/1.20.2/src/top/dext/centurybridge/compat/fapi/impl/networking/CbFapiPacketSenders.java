package top.dext.centurybridge.compat.fapi.impl.networking;

import net.fabricmc.fabric.api.networking.v1.FabricPacket;
import net.fabricmc.fabric.api.networking.v1.PacketByteBufs;

/**
 * 1.20.2's PacketSender gained an abstract createPacket(FabricPacket), through which its
 * sendPacket(FabricPacket) defaults now send. A 1.20.1 implementation has no such method; 1.20.1's
 * defaults sent a FabricPacket by writing it into a fresh buffer and sending that on the packet type's
 * channel -- through the implementation's own createPacket(channel, buffer). The method synthesized
 * for it does exactly that.
 */
public final class CbFapiPacketSenders {

    private CbFapiPacketSenders() {
    }

    public static net.minecraft.class_2596<?> createPacket(CbLegacyPacketSender sender, FabricPacket packet) {
        net.minecraft.class_2540 buf = PacketByteBufs.create();
        packet.write(buf);
        return sender.createPacket(packet.getType().getId(), buf);
    }
}
