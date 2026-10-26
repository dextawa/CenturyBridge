package top.dext.centurybridge.step.v1_21_6.rt;

import io.netty.channel.ChannelFutureListener;
import net.fabricmc.fabric.api.networking.v1.LoginPacketSender;
import net.fabricmc.fabric.api.networking.v1.PacketSender;
import top.dext.centurybridge.step.v1_21_6.facade.PacketSendListener;

/**
 * Fabric API's PacketSender / LoginPacketSender.sendPacket with 1.21.5's PacketSendListener on Fabric API 1.21.6, whose
 * sends take a netty ChannelFutureListener (fabric-networking-api-v1 4.5.0 / 5.0.1, javap: the same members with the
 * listener type changed, the defaults the same bodies). The listener converts as for vanilla's sends ({@link CbPackets});
 * a listener handed to a 1.21.5 implementation has no connection to run a plain netty listener against
 * ({@link CbPackets#fromNetty} with none).
 */
public final class CbFapiPacketSends {

    private CbFapiPacketSends() {
    }

    /** Fabric API 1.21.6's listener as a 1.21.5 implementation's (override hooks' wrap) */
    public static PacketSendListener callbacks(ChannelFutureListener listener) {
        return CbPackets.fromNetty(listener, null);
    }

    /** PacketSender.sendPacket(packet, callbacks) */
    public static void sendPacket(PacketSender sender, net.minecraft.class_2596<?> packet, PacketSendListener callbacks) {
        sender.sendPacket(packet, CbPackets.toNetty(callbacks));
    }

    /** PacketSender.sendPacket(payload, callbacks) */
    public static void sendPacket(PacketSender sender, net.minecraft.class_8710 payload, PacketSendListener callbacks) {
        sender.sendPacket(payload, CbPackets.toNetty(callbacks));
    }

    /** LoginPacketSender.sendPacket(channel, buf, callbacks) */
    public static void sendPacket(LoginPacketSender sender, net.minecraft.class_2960 channel, net.minecraft.class_2540 buf,
                                  PacketSendListener callbacks) {
        sender.sendPacket(channel, buf, CbPackets.toNetty(callbacks));
    }

    /** 1.21.5 PacketSender's default sendPacket(payload, callbacks), reached by a super call */
    public static void superSendPacket(PacketSender sender, net.minecraft.class_8710 payload, PacketSendListener callbacks) {
        sender.sendPacket(sender.createPacket(payload), CbPackets.toNetty(callbacks));
    }

    /** 1.21.5 LoginPacketSender's default sendPacket(channel, buf, callbacks), reached by a super call */
    public static void superSendPacket(LoginPacketSender sender, net.minecraft.class_2960 channel, net.minecraft.class_2540 buf,
                                       PacketSendListener callbacks) {
        java.util.Objects.requireNonNull(channel, "Channel cannot be null");
        java.util.Objects.requireNonNull(buf, "Payload cannot be null");
        sender.sendPacket(sender.createPacket(channel, buf), CbPackets.toNetty(callbacks));
    }
}
