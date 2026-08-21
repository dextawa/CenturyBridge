package top.dext.centurybridge.compat.fapi;

/**
 * Stand-in for fabric-api's removed ServerPlayNetworking$PlayChannelHandler
 * (raw-buf C2S receiver). See CbClientPlayHandler.
 */
public interface CbServerPlayHandler {
    void receive(net.minecraft.server.MinecraftServer server,
                 net.minecraft.class_3222 player,
                 net.minecraft.class_3244 handler,
                 net.minecraft.class_2540 buf,
                 net.fabricmc.fabric.api.networking.v1.PacketSender responseSender);
}
