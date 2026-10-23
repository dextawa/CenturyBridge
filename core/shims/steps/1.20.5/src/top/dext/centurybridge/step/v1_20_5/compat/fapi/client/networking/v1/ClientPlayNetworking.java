package top.dext.centurybridge.step.v1_20_5.compat.fapi.client.networking.v1;

import java.util.Objects;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.networking.v1.PacketSender;
import top.dext.centurybridge.step.v1_20_5.compat.fapi.impl.networking.CbChannelPayload;
import top.dext.centurybridge.step.v1_20_5.compat.fapi.impl.networking.CbChannels;
import top.dext.centurybridge.step.v1_20_5.compat.fapi.networking.v1.FabricPacket;
import top.dext.centurybridge.step.v1_20_5.compat.fapi.networking.v1.PacketType;

/**
 * Fabric API 0.97.3's (1.20.4) ClientPlayNetworking members that 1.20.5 dropped or reshaped, on 1.20.5's
 * (staticRedirects), with its two handler interfaces, which 1.20.5 removed (classRenames). As on the server
 * side (networking/v1/ServerPlayNetworking), a channel or packet type is the payload type of its id, and a
 * 1.20.4 handler is registered wrapped: a channel handler, which ran on the netty thread in 1.20.4, is
 * called on the client thread where 1.20.5's Fabric API runs play handlers, with a copy of the bytes and
 * 1.20.4's disconnect for a failure; a packet handler ran on the client thread then as now, behind the same
 * open-connection check.
 */
@Environment(EnvType.CLIENT)
public final class ClientPlayNetworking {

    private ClientPlayNetworking() {
    }

    @Environment(EnvType.CLIENT)
    @FunctionalInterface
    public interface PlayChannelHandler {
        void receive(net.minecraft.class_310 client, net.minecraft.class_634 handler, net.minecraft.class_2540 buf, PacketSender responseSender);
    }

    @Environment(EnvType.CLIENT)
    @FunctionalInterface
    public interface PlayPacketHandler<T extends FabricPacket> {
        void receive(T packet, net.minecraft.class_746 player, PacketSender responseSender);
    }

    @SuppressWarnings({"rawtypes", "unchecked"})
    static final class Receiver implements net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking.PlayPayloadHandler {
        final PacketType<?> type;
        final Object actual;

        Receiver(PacketType<?> type, Object actual) {
            this.type = type;
            this.actual = actual;
        }

        @Override
        public void receive(net.minecraft.class_8710 payload, net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking.Context context) {
            CbChannelPayload p = CbChannelPayload.of(payload);
            net.minecraft.class_310 client = context.client();
            net.minecraft.class_634 handler = client.method_1562();
            if (type == null) {
                try {
                    ((PlayChannelHandler) actual).receive(client, handler, p.buffer(), context.responseSender());
                } catch (Throwable t) {
                    handler.method_48296().method_10747(CbChannels.handlerFailure(p.channel(), t));
                }
                return;
            }
            FabricPacket packet;
            try {
                packet = p.packet(type);
            } catch (Throwable t) {
                handler.method_48296().method_10747(CbChannels.handlerFailure(p.channel(), t));
                return;
            }
            if (handler.method_48296().method_10758()) {
                ((PlayPacketHandler) actual).receive(packet, client.field_1724, context.responseSender());
            }
        }
    }

    static PlayChannelHandler channelHandler(Object h) {
        return h instanceof Receiver r && r.actual instanceof PlayChannelHandler a ? a : null;
    }

    @SuppressWarnings("unchecked")
    static <T extends FabricPacket> PlayPacketHandler<T> packetHandler(Object h) {
        return h instanceof Receiver r && r.actual instanceof PlayPacketHandler a ? (PlayPacketHandler<T>) a : null;
    }

    @SuppressWarnings({"rawtypes", "unchecked"})
    public static boolean registerGlobalReceiver(net.minecraft.class_2960 channelName, PlayChannelHandler channelHandler) {
        CbChannels.assertGlobalRegistrable(channelName, channelHandler);
        return net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking.registerGlobalReceiver(
            (net.minecraft.class_8710.class_9154) CbChannels.playS2C(channelName), new Receiver(null, channelHandler));
    }

    @SuppressWarnings({"rawtypes", "unchecked"})
    public static <T extends FabricPacket> boolean registerGlobalReceiver(PacketType<T> type, PlayPacketHandler<T> handler) {
        CbChannels.assertGlobalRegistrable(type.getId(), handler);
        return net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking.registerGlobalReceiver(
            (net.minecraft.class_8710.class_9154) CbChannels.playS2C(type.getId()), new Receiver(type, handler));
    }

    public static PlayChannelHandler unregisterGlobalReceiver(net.minecraft.class_2960 channelName) {
        return channelHandler(net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking.unregisterGlobalReceiver(channelName));
    }

    public static <T extends FabricPacket> PlayPacketHandler<T> unregisterGlobalReceiver(PacketType<T> type) {
        return packetHandler(net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking.unregisterGlobalReceiver(type.getId()));
    }

    @SuppressWarnings({"rawtypes", "unchecked"})
    public static boolean registerReceiver(net.minecraft.class_2960 channelName, PlayChannelHandler channelHandler) {
        CbChannels.assertRegistrable(channelName, channelHandler);
        return net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking.registerReceiver(
            (net.minecraft.class_8710.class_9154) CbChannels.playS2C(channelName), new Receiver(null, channelHandler));
    }

    @SuppressWarnings({"rawtypes", "unchecked"})
    public static <T extends FabricPacket> boolean registerReceiver(PacketType<T> type, PlayPacketHandler<T> handler) {
        CbChannels.assertRegistrable(type.getId(), handler);
        return net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking.registerReceiver(
            (net.minecraft.class_8710.class_9154) CbChannels.playS2C(type.getId()), new Receiver(type, handler));
    }

    public static PlayChannelHandler unregisterReceiver(net.minecraft.class_2960 channelName) {
        return channelHandler(net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking.unregisterReceiver(channelName));
    }

    public static <T extends FabricPacket> PlayPacketHandler<T> unregisterReceiver(PacketType<T> type) {
        return packetHandler(net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking.unregisterReceiver(type.getId()));
    }

    public static boolean canSend(PacketType<?> type) {
        return net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking.canSend(type.getId());
    }

    public static net.minecraft.class_2596<net.minecraft.class_8706> createC2SPacket(net.minecraft.class_2960 channelName,
                                                                                    net.minecraft.class_2540 buf) {
        Objects.requireNonNull(channelName, "Channel name cannot be null");
        Objects.requireNonNull(buf, "Buf cannot be null");
        return net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking.createC2SPacket(CbChannelPayload.raw(channelName, buf));
    }

    public static <T extends FabricPacket> net.minecraft.class_2596<net.minecraft.class_8706> createC2SPacket(T packet) {
        return net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking.createC2SPacket(CbChannelPayload.typed(packet));
    }

    public static void send(net.minecraft.class_2960 channelName, net.minecraft.class_2540 buf) {
        net.minecraft.class_634 handler = net.minecraft.class_310.method_1551().method_1562();
        if (handler != null) {
            handler.method_52787(createC2SPacket(channelName, buf));
            return;
        }
        throw new IllegalStateException("Cannot send packets when not in game!");
    }

    public static <T extends FabricPacket> void send(T packet) {
        Objects.requireNonNull(packet, "Packet cannot be null");
        Objects.requireNonNull(packet.getType(), "Packet#getType cannot return null");
        net.minecraft.class_634 handler = net.minecraft.class_310.method_1551().method_1562();
        if (handler != null) {
            handler.method_52787(createC2SPacket(packet));
            return;
        }
        throw new IllegalStateException("Cannot send packets when not in game!");
    }
}
