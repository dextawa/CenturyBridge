package top.dext.centurybridge.step.v1_20_5.compat.fapi.impl.networking;

import net.fabricmc.fabric.api.networking.v1.LoginPacketSender;
import net.fabricmc.fabric.api.networking.v1.PacketSender;
import net.fabricmc.fabric.api.networking.v1.ServerLoginConnectionEvents;
import net.fabricmc.fabric.api.networking.v1.ServerLoginNetworking;

/**
 * ServerLoginConnectionEvents.QueryStart.onLoginStart: 1.20.4 handed the listener a PacketSender, 1.20.5 a
 * LoginPacketSender (which is one). A 1.20.4 listener -- lambda (lambdaAdapters, {@link #adapt}) or class
 * (an override hook forwarding the new shape to the old, the login sender passing as a PacketSender) --
 * receives what 1.20.5 passes. A 1.20.4 call of the old shape ({@link #loginSender}, callReshapes) passes
 * its sender on: a login sender as is, any other wrapped as one whose channel packets are built the 1.20.4
 * way for that sender (CbPacketSenders).
 */
public final class CbLoginQueryStart {

    private CbLoginQueryStart() {
    }

    /** the 1.20.4 shape of QueryStart, as the converted lambda implements it */
    @FunctionalInterface
    public interface Legacy {
        void onLoginStart(net.minecraft.class_3248 handler, net.minecraft.server.MinecraftServer server, PacketSender sender,
                          ServerLoginNetworking.LoginSynchronizer synchronizer);
    }

    public static ServerLoginConnectionEvents.QueryStart adapt(Legacy legacy) {
        return legacy::onLoginStart;
    }

    public static LoginPacketSender loginSender(PacketSender sender) {
        if (sender == null || sender instanceof LoginPacketSender) {
            return (LoginPacketSender) sender;
        }
        return new LoginPacketSender() {
            @Override
            public net.minecraft.class_2596<?> createPacket(net.minecraft.class_2960 channelName, net.minecraft.class_2540 buf) {
                return CbPacketSenders.createPacket(sender, channelName, buf);
            }

            @Override
            public net.minecraft.class_2596<?> createPacket(net.minecraft.class_8710 payload) {
                return sender.createPacket(payload);
            }

            @Override
            public void sendPacket(net.minecraft.class_2596<?> packet, net.minecraft.class_7648 callback) {
                sender.sendPacket(packet, callback);
            }

            @Override
            public void disconnect(net.minecraft.class_2561 disconnectReason) {
                sender.disconnect(disconnectReason);
            }
        };
    }
}
