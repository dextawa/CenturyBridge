package top.dext.centurybridge.compat.fapi;

/**
 * Stand-in for fabric-api's removed ServerPlayNetworking$PlayChannelHandler
 * (raw-buf C2S receiver). See CbClientPlayHandler.
 */
public interface CbServerPlayHandler {
    void receive(net.minecraft.server.MinecraftServer server,
                 net.minecraft.server.level.ServerPlayer player,
                 net.minecraft.server.network.ServerGamePacketListenerImpl handler,
                 net.minecraft.network.FriendlyByteBuf buf,
                 net.fabricmc.fabric.api.networking.v1.PacketSender responseSender);
}
