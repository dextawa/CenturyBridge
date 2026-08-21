package top.dext.centurybridge.compat.fapi;

/**
 * Stand-in for fabric-api's removed ClientPlayNetworking$PlayChannelHandler
 * (raw-buf S2C receiver, deleted in the 1.20.5 typed-payload rewrite). The
 * converter classRenames the old interface here, so mods' lambdas and
 * implementations keep their exact old shape.
 */
public interface CbClientPlayHandler {
    void receive(net.minecraft.client.Minecraft client,
                 net.minecraft.client.multiplayer.ClientPacketListener handler,
                 net.minecraft.network.FriendlyByteBuf buf,
                 net.fabricmc.fabric.api.networking.v1.PacketSender responseSender);
}
