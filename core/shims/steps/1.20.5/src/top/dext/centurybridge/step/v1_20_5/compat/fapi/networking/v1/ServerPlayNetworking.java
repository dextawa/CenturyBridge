package top.dext.centurybridge.step.v1_20_5.compat.fapi.networking.v1;

import java.util.Objects;
import net.fabricmc.fabric.api.networking.v1.PacketSender;
import top.dext.centurybridge.step.v1_20_5.compat.fapi.impl.networking.CbChannelPayload;
import top.dext.centurybridge.step.v1_20_5.compat.fapi.impl.networking.CbChannels;

/**
 * Fabric API 0.97.3's (1.20.4) ServerPlayNetworking members that 1.20.5 dropped or reshaped, on 1.20.5's
 * (staticRedirects, same name and the 1.20.4 descriptor), with its two handler interfaces, which 1.20.5
 * removed (classRenames). The members 1.20.5 kept as they were (getSender, getReceived, getSendable,
 * canSend of a channel, getGlobalReceivers) stay Fabric API's own.
 *
 * <p>A channel or packet type is the payload type of its id ({@link CbChannels}); a 1.20.4 handler is
 * registered with 1.20.5's Fabric API wrapped in a {@link Receiver}, whose 1.20.4 form unregistering gives
 * back as 1.20.4 did (the channel handler from the channel overloads, the packet handler from the type ones,
 * null for the other kind). 1.20.5's Fabric API runs every play handler on the server thread: a 1.20.4
 * channel handler, which ran on the netty thread, is called there with a copy of the payload's bytes, its
 * failures leading to the disconnect 1.20.4's connection made of them; a packet handler ran on the server
 * thread in 1.20.4 too, behind the same open-connection check.
 */
public final class ServerPlayNetworking {

    private ServerPlayNetworking() {
    }

    @FunctionalInterface
    public interface PlayChannelHandler {
        void receive(net.minecraft.server.MinecraftServer server, net.minecraft.class_3222 player, net.minecraft.class_3244 handler,
                     net.minecraft.class_2540 buf, PacketSender responseSender);
    }

    @FunctionalInterface
    public interface PlayPacketHandler<T extends FabricPacket> {
        void receive(T packet, net.minecraft.class_3222 player, PacketSender responseSender);
    }

    /** a 1.20.4 handler as 1.20.5 takes it: the channel or packet handler, and the type of a packet one */
    @SuppressWarnings({"rawtypes", "unchecked"})
    static final class Receiver implements net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking.PlayPayloadHandler {
        final PacketType<?> type;
        final Object actual;

        Receiver(PacketType<?> type, Object actual) {
            this.type = type;
            this.actual = actual;
        }

        @Override
        public void receive(net.minecraft.class_8710 payload, net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking.Context context) {
            CbChannelPayload p = CbChannelPayload.of(payload);
            net.minecraft.class_3222 player = context.player();
            net.minecraft.class_3244 handler = player.field_13987;
            if (type == null) {
                try {
                    ((PlayChannelHandler) actual).receive(player.field_13995, player, handler, p.buffer(), context.responseSender());
                } catch (Throwable t) {
                    handler.method_52396(CbChannels.handlerFailure(p.channel(), t));
                }
                return;
            }
            FabricPacket packet;
            try {
                packet = p.packet(type);
            } catch (Throwable t) {
                handler.method_52396(CbChannels.handlerFailure(p.channel(), t));
                return;
            }
            if (handler.method_48106()) {
                ((PlayPacketHandler) actual).receive(packet, player, context.responseSender());
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
        return net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking.registerGlobalReceiver(
            (net.minecraft.class_8710.class_9154) CbChannels.playC2S(channelName), new Receiver(null, channelHandler));
    }

    @SuppressWarnings({"rawtypes", "unchecked"})
    public static <T extends FabricPacket> boolean registerGlobalReceiver(PacketType<T> type, PlayPacketHandler<T> handler) {
        CbChannels.assertGlobalRegistrable(type.getId(), handler);
        return net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking.registerGlobalReceiver(
            (net.minecraft.class_8710.class_9154) CbChannels.playC2S(type.getId()), new Receiver(type, handler));
    }

    public static PlayChannelHandler unregisterGlobalReceiver(net.minecraft.class_2960 channelName) {
        return channelHandler(net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking.unregisterGlobalReceiver(channelName));
    }

    public static <T extends FabricPacket> PlayPacketHandler<T> unregisterGlobalReceiver(PacketType<T> type) {
        return packetHandler(net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking.unregisterGlobalReceiver(type.getId()));
    }

    @SuppressWarnings({"rawtypes", "unchecked"})
    public static boolean registerReceiver(net.minecraft.class_3244 networkHandler, net.minecraft.class_2960 channelName,
                                           PlayChannelHandler channelHandler) {
        Objects.requireNonNull(networkHandler, "Network handler cannot be null");
        CbChannels.assertRegistrable(channelName, channelHandler);
        return net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking.registerReceiver(networkHandler,
            (net.minecraft.class_8710.class_9154) CbChannels.playC2S(channelName), new Receiver(null, channelHandler));
    }

    @SuppressWarnings({"rawtypes", "unchecked"})
    public static <T extends FabricPacket> boolean registerReceiver(net.minecraft.class_3244 networkHandler, PacketType<T> type,
                                                                    PlayPacketHandler<T> handler) {
        CbChannels.assertRegistrable(type.getId(), handler);
        return net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking.registerReceiver(networkHandler,
            (net.minecraft.class_8710.class_9154) CbChannels.playC2S(type.getId()), new Receiver(type, handler));
    }

    public static PlayChannelHandler unregisterReceiver(net.minecraft.class_3244 networkHandler, net.minecraft.class_2960 channelName) {
        Objects.requireNonNull(networkHandler, "Network handler cannot be null");
        return channelHandler(net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking.unregisterReceiver(networkHandler, channelName));
    }

    public static <T extends FabricPacket> PlayPacketHandler<T> unregisterReceiver(net.minecraft.class_3244 networkHandler, PacketType<T> type) {
        return packetHandler(net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking.unregisterReceiver(networkHandler, type.getId()));
    }

    public static boolean canSend(net.minecraft.class_3222 player, PacketType<?> type) {
        Objects.requireNonNull(player, "Server player entity cannot be null");
        return canSend(player.field_13987, type);
    }

    public static boolean canSend(net.minecraft.class_3244 handler, PacketType<?> type) {
        Objects.requireNonNull(handler, "Server play network handler cannot be null");
        Objects.requireNonNull(type, "Packet type cannot be null");
        return net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking.canSend(handler, type.getId());
    }

    public static net.minecraft.class_2596<net.minecraft.class_8705> createS2CPacket(net.minecraft.class_2960 channelName,
                                                                                    net.minecraft.class_2540 buf) {
        Objects.requireNonNull(channelName, "Channel cannot be null");
        Objects.requireNonNull(buf, "Buf cannot be null");
        return net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking.createS2CPacket(CbChannelPayload.raw(channelName, buf));
    }

    public static <T extends FabricPacket> net.minecraft.class_2596<net.minecraft.class_8705> createS2CPacket(T packet) {
        return net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking.createS2CPacket(CbChannelPayload.typed(packet));
    }

    public static void send(net.minecraft.class_3222 player, net.minecraft.class_2960 channelName, net.minecraft.class_2540 buf) {
        Objects.requireNonNull(player, "Server player entity cannot be null");
        Objects.requireNonNull(channelName, "Channel name cannot be null");
        Objects.requireNonNull(buf, "Packet byte buf cannot be null");
        player.field_13987.method_14364(createS2CPacket(channelName, buf));
    }

    public static <T extends FabricPacket> void send(net.minecraft.class_3222 player, T packet) {
        Objects.requireNonNull(player, "Server player entity cannot be null");
        Objects.requireNonNull(packet, "Packet cannot be null");
        Objects.requireNonNull(packet.getType(), "Packet#getType cannot return null");
        player.field_13987.method_14364(createS2CPacket(packet));
    }

    /** removed in 1.20.5: the server of a play network handler, as 1.20.4 answered it (its player's) */
    public static net.minecraft.server.MinecraftServer getServer(net.minecraft.class_3244 handler) {
        Objects.requireNonNull(handler, "Network handler cannot be null");
        return handler.field_14140.field_13995;
    }
}
