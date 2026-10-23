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
 * Fabric API 0.97.3's (1.20.4) ClientConfigurationNetworking members that 1.20.5 dropped or reshaped, on
 * 1.20.5's (staticRedirects), with its two handler interfaces, which 1.20.5 removed (classRenames).
 * 1.20.5's Fabric API calls configuration handlers on the netty thread, as 1.20.4 called a channel handler;
 * a 1.20.4 packet handler ran on the client thread behind an open-connection check, which the wrapper keeps
 * (the packet read off the bytes where 1.20.4 read it, before scheduling). A channel handler is handed the
 * configuration network handler 1.20.4 passed, which 1.20.5's context no longer carries: the handler the
 * connection is configuring with, and its connection for the open check ({@link #configuring}, called by
 * CbFapiClientConfigurationMixin as a configuration handler is created).
 */
@Environment(EnvType.CLIENT)
public final class ClientConfigurationNetworking {

    /** the client's configuration network handler and its connection, the last one created */
    private record Configuring(net.minecraft.class_8674 handler, net.minecraft.class_2535 connection) {
    }

    private static volatile Configuring current;

    /** CbFapiClientConfigurationMixin: a configuration handler was created for a connection */
    public static void configuring(net.minecraft.class_8674 handler, net.minecraft.class_2535 connection) {
        current = new Configuring(handler, connection);
    }

    private static Configuring current() {
        return Objects.requireNonNull(current, "no configuration network handler was created");
    }

    private ClientConfigurationNetworking() {
    }

    @Environment(EnvType.CLIENT)
    @FunctionalInterface
    public interface ConfigurationChannelHandler {
        void receive(net.minecraft.class_310 client, net.minecraft.class_8674 handler, net.minecraft.class_2540 buf, PacketSender responseSender);
    }

    @Environment(EnvType.CLIENT)
    @FunctionalInterface
    public interface ConfigurationPacketHandler<T extends FabricPacket> {
        void receive(T packet, PacketSender responseSender);
    }

    @SuppressWarnings({"rawtypes", "unchecked"})
    static final class Receiver implements net.fabricmc.fabric.api.client.networking.v1.ClientConfigurationNetworking.ConfigurationPayloadHandler {
        final PacketType<?> type;
        final Object actual;

        Receiver(PacketType<?> type, Object actual) {
            this.type = type;
            this.actual = actual;
        }

        @Override
        public void receive(net.minecraft.class_8710 payload, net.fabricmc.fabric.api.client.networking.v1.ClientConfigurationNetworking.Context context) {
            CbChannelPayload p = CbChannelPayload.of(payload);
            net.minecraft.class_310 client = net.minecraft.class_310.method_1551();
            PacketSender sender = context.responseSender();
            Configuring network = current();
            if (type == null) {
                ((ConfigurationChannelHandler) actual).receive(client, network.handler(), p.buffer(), sender);
                return;
            }
            FabricPacket packet = p.packet(type);
            ConfigurationPacketHandler handler = (ConfigurationPacketHandler) actual;
            if (client.method_18854()) {
                handler.receive(packet, sender);
            } else {
                client.execute(() -> {
                    if (network.connection().method_10758()) {
                        handler.receive(packet, sender);
                    }
                });
            }
        }
    }

    static ConfigurationChannelHandler channelHandler(Object h) {
        return h instanceof Receiver r && r.actual instanceof ConfigurationChannelHandler a ? a : null;
    }

    @SuppressWarnings("unchecked")
    static <T extends FabricPacket> ConfigurationPacketHandler<T> packetHandler(Object h) {
        return h instanceof Receiver r && r.actual instanceof ConfigurationPacketHandler a ? (ConfigurationPacketHandler<T>) a : null;
    }

    @SuppressWarnings({"rawtypes", "unchecked"})
    public static boolean registerGlobalReceiver(net.minecraft.class_2960 channelName, ConfigurationChannelHandler channelHandler) {
        CbChannels.assertGlobalRegistrable(channelName, channelHandler);
        return net.fabricmc.fabric.api.client.networking.v1.ClientConfigurationNetworking.registerGlobalReceiver(
            (net.minecraft.class_8710.class_9154) CbChannels.configurationS2C(channelName), new Receiver(null, channelHandler));
    }

    @SuppressWarnings({"rawtypes", "unchecked"})
    public static <T extends FabricPacket> boolean registerGlobalReceiver(PacketType<T> type, ConfigurationPacketHandler<T> handler) {
        CbChannels.assertGlobalRegistrable(type.getId(), handler);
        return net.fabricmc.fabric.api.client.networking.v1.ClientConfigurationNetworking.registerGlobalReceiver(
            (net.minecraft.class_8710.class_9154) CbChannels.configurationS2C(type.getId()), new Receiver(type, handler));
    }

    public static ConfigurationChannelHandler unregisterGlobalReceiver(net.minecraft.class_2960 channelName) {
        return channelHandler(net.fabricmc.fabric.api.client.networking.v1.ClientConfigurationNetworking.unregisterGlobalReceiver(
            CbChannels.id(channelName)));
    }

    public static <T extends FabricPacket> ConfigurationPacketHandler<T> unregisterGlobalReceiver(PacketType<T> type) {
        return packetHandler(net.fabricmc.fabric.api.client.networking.v1.ClientConfigurationNetworking.unregisterGlobalReceiver(
            CbChannels.id(type.getId())));
    }

    @SuppressWarnings({"rawtypes", "unchecked"})
    public static boolean registerReceiver(net.minecraft.class_2960 channelName, ConfigurationChannelHandler channelHandler) {
        CbChannels.assertRegistrable(channelName, channelHandler);
        return net.fabricmc.fabric.api.client.networking.v1.ClientConfigurationNetworking.registerReceiver(
            (net.minecraft.class_8710.class_9154) CbChannels.configurationS2C(channelName), new Receiver(null, channelHandler));
    }

    @SuppressWarnings({"rawtypes", "unchecked"})
    public static <T extends FabricPacket> boolean registerReceiver(PacketType<T> type, ConfigurationPacketHandler<T> handler) {
        CbChannels.assertRegistrable(type.getId(), handler);
        return net.fabricmc.fabric.api.client.networking.v1.ClientConfigurationNetworking.registerReceiver(
            (net.minecraft.class_8710.class_9154) CbChannels.configurationS2C(type.getId()), new Receiver(type, handler));
    }

    public static ConfigurationChannelHandler unregisterReceiver(net.minecraft.class_2960 channelName) {
        return channelHandler(net.fabricmc.fabric.api.client.networking.v1.ClientConfigurationNetworking.unregisterReceiver(channelName));
    }

    public static <T extends FabricPacket> ConfigurationPacketHandler<T> unregisterReceiver(PacketType<T> type) {
        return packetHandler(net.fabricmc.fabric.api.client.networking.v1.ClientConfigurationNetworking.unregisterReceiver(type.getId()));
    }

    public static boolean canSend(PacketType<?> type) {
        return net.fabricmc.fabric.api.client.networking.v1.ClientConfigurationNetworking.canSend(type.getId());
    }

    /** removed in 1.20.5: the serverbound custom payload packet of a channel send */
    public static net.minecraft.class_2596<net.minecraft.class_8706> createC2SPacket(net.minecraft.class_2960 channelName,
                                                                                    net.minecraft.class_2540 buf) {
        Objects.requireNonNull(channelName, "Channel name cannot be null");
        Objects.requireNonNull(buf, "Buf cannot be null");
        return new net.minecraft.class_2817(CbChannelPayload.raw(channelName, buf));
    }

    /** 1.20.4: the channel's packet through the configuring sender, or "Cannot send packet while not configuring!" */
    public static void send(net.minecraft.class_2960 channelName, net.minecraft.class_2540 buf) {
        Objects.requireNonNull(channelName, "Channel name cannot be null");
        Objects.requireNonNull(buf, "Buf cannot be null");
        net.fabricmc.fabric.api.client.networking.v1.ClientConfigurationNetworking.send(CbChannelPayload.raw(channelName, buf));
    }

    public static <T extends FabricPacket> void send(T packet) {
        Objects.requireNonNull(packet, "Packet cannot be null");
        Objects.requireNonNull(packet.getType(), "Packet#getType cannot return null");
        net.fabricmc.fabric.api.client.networking.v1.ClientConfigurationNetworking.send(CbChannelPayload.typed(packet));
    }
}
