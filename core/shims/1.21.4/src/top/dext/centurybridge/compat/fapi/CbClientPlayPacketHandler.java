package top.dext.centurybridge.compat.fapi;

/** Stand-in for fabric-api's removed ClientPlayNetworking$PlayPacketHandler (see CbFabricPacket). */
@net.fabricmc.api.Environment(net.fabricmc.api.EnvType.CLIENT)
public interface CbClientPlayPacketHandler<T extends CbFabricPacket> {
    void receive(T packet,
                 net.minecraft.class_746 player,
                 net.fabricmc.fabric.api.networking.v1.PacketSender responseSender);
}
