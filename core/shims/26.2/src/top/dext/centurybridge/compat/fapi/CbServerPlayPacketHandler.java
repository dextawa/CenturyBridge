package top.dext.centurybridge.compat.fapi;

/** Stand-in for fabric-api's removed ServerPlayNetworking$PlayPacketHandler (see CbFabricPacket). */
public interface CbServerPlayPacketHandler<T extends CbFabricPacket> {
    void receive(T packet,
                 net.minecraft.server.level.ServerPlayer player,
                 net.fabricmc.fabric.api.networking.v1.PacketSender responseSender);
}
