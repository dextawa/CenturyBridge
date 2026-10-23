package top.dext.centurybridge.step.v1_20_5.compat.fapi.networking.v1;

import java.util.Objects;
import net.fabricmc.fabric.api.networking.v1.PacketSender;
import top.dext.centurybridge.step.v1_20_5.compat.fapi.impl.networking.CbChannelPayload;
import top.dext.centurybridge.step.v1_20_5.compat.fapi.impl.networking.CbChannels;

/**
 * Fabric API 0.97.3's (1.20.4) ServerConfigurationNetworking members that 1.20.5 dropped or reshaped, on
 * 1.20.5's (staticRedirects), with its handler interfaces (classRenames: ConfigurationChannelHandler is
 * gone, ConfigurationPacketHandler kept its name with another method, so every 1.20.4 reference to it means
 * this one). As {@link ServerPlayNetworking}, a channel or packet type is the payload type of its id and a
 * handler is registered wrapped. Configuration handlers ran on the netty thread in 1.20.4 and still do in
 * 1.20.5 (Fabric API calls them as the payload is handled), so their failures take the same way as before.
 */
public final class ServerConfigurationNetworking {

    private ServerConfigurationNetworking() {
    }

    @FunctionalInterface
    public interface ConfigurationChannelHandler {
        void receive(net.minecraft.server.MinecraftServer server, net.minecraft.class_8610 handler, net.minecraft.class_2540 buf,
                     PacketSender responseSender);
    }

    @FunctionalInterface
    public interface ConfigurationPacketHandler<T extends FabricPacket> {
        void receive(T packet, net.minecraft.class_8610 networkHandler, PacketSender responseSender);
    }

    @SuppressWarnings({"rawtypes", "unchecked"})
    static final class Receiver implements net.fabricmc.fabric.api.networking.v1.ServerConfigurationNetworking.ConfigurationPacketHandler {
        final PacketType<?> type;
        final Object actual;

        Receiver(PacketType<?> type, Object actual) {
            this.type = type;
            this.actual = actual;
        }

        @Override
        public void receive(net.minecraft.class_8710 payload, net.fabricmc.fabric.api.networking.v1.ServerConfigurationNetworking.Context context) {
            CbChannelPayload p = CbChannelPayload.of(payload);
            net.minecraft.class_8610 handler = context.networkHandler();
            if (type == null) {
                ((ConfigurationChannelHandler) actual).receive(
                    net.fabricmc.fabric.api.networking.v1.ServerConfigurationNetworking.getServer(handler), handler, p.buffer(), context.responseSender());
            } else {
                ((ConfigurationPacketHandler) actual).receive(p.packet(type), handler, context.responseSender());
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
        return net.fabricmc.fabric.api.networking.v1.ServerConfigurationNetworking.registerGlobalReceiver(
            (net.minecraft.class_8710.class_9154) CbChannels.configurationC2S(channelName), new Receiver(null, channelHandler));
    }

    @SuppressWarnings({"rawtypes", "unchecked"})
    public static <T extends FabricPacket> boolean registerGlobalReceiver(PacketType<T> type, ConfigurationPacketHandler<T> handler) {
        CbChannels.assertGlobalRegistrable(type.getId(), handler);
        return net.fabricmc.fabric.api.networking.v1.ServerConfigurationNetworking.registerGlobalReceiver(
            (net.minecraft.class_8710.class_9154) CbChannels.configurationC2S(type.getId()), new Receiver(type, handler));
    }

    public static ConfigurationChannelHandler unregisterGlobalReceiver(net.minecraft.class_2960 channelName) {
        return channelHandler(net.fabricmc.fabric.api.networking.v1.ServerConfigurationNetworking.unregisterGlobalReceiver(channelName));
    }

    public static <T extends FabricPacket> ConfigurationPacketHandler<T> unregisterGlobalReceiver(PacketType<T> type) {
        return packetHandler(net.fabricmc.fabric.api.networking.v1.ServerConfigurationNetworking.unregisterGlobalReceiver(type.getId()));
    }

    @SuppressWarnings({"rawtypes", "unchecked"})
    public static boolean registerReceiver(net.minecraft.class_8610 networkHandler, net.minecraft.class_2960 channelName,
                                           ConfigurationChannelHandler channelHandler) {
        Objects.requireNonNull(networkHandler, "Network handler cannot be null");
        CbChannels.assertRegistrable(channelName, channelHandler);
        return net.fabricmc.fabric.api.networking.v1.ServerConfigurationNetworking.registerReceiver(networkHandler,
            (net.minecraft.class_8710.class_9154) CbChannels.configurationC2S(channelName), new Receiver(null, channelHandler));
    }

    @SuppressWarnings({"rawtypes", "unchecked"})
    public static <T extends FabricPacket> boolean registerReceiver(net.minecraft.class_8610 networkHandler, PacketType<T> type,
                                                                    ConfigurationPacketHandler<T> handler) {
        CbChannels.assertRegistrable(type.getId(), handler);
        return net.fabricmc.fabric.api.networking.v1.ServerConfigurationNetworking.registerReceiver(networkHandler,
            (net.minecraft.class_8710.class_9154) CbChannels.configurationC2S(type.getId()), new Receiver(type, handler));
    }

    public static ConfigurationChannelHandler unregisterReceiver(net.minecraft.class_8610 networkHandler, net.minecraft.class_2960 channelName) {
        Objects.requireNonNull(networkHandler, "Network handler cannot be null");
        return channelHandler(net.fabricmc.fabric.api.networking.v1.ServerConfigurationNetworking.unregisterReceiver(networkHandler, channelName));
    }

    public static <T extends FabricPacket> ConfigurationPacketHandler<T> unregisterReceiver(net.minecraft.class_8610 networkHandler,
                                                                                            PacketType<T> type) {
        return packetHandler(net.fabricmc.fabric.api.networking.v1.ServerConfigurationNetworking.unregisterReceiver(networkHandler, type.getId()));
    }

    public static boolean canSend(net.minecraft.class_8610 handler, PacketType<?> type) {
        Objects.requireNonNull(handler, "Server configuration network handler cannot be null");
        Objects.requireNonNull(type, "Packet type cannot be null");
        return net.fabricmc.fabric.api.networking.v1.ServerConfigurationNetworking.canSend(handler, type.getId());
    }

    public static net.minecraft.class_2596<net.minecraft.class_8705> createS2CPacket(net.minecraft.class_2960 channelName,
                                                                                    net.minecraft.class_2540 buf) {
        Objects.requireNonNull(channelName, "Channel cannot be null");
        Objects.requireNonNull(buf, "Buf cannot be null");
        return net.fabricmc.fabric.api.networking.v1.ServerConfigurationNetworking.createS2CPacket(CbChannelPayload.raw(channelName, buf));
    }

    public static <T extends FabricPacket> net.minecraft.class_2596<net.minecraft.class_8705> createS2CPacket(T packet) {
        return net.fabricmc.fabric.api.networking.v1.ServerConfigurationNetworking.createS2CPacket(CbChannelPayload.typed(packet));
    }

    public static void send(net.minecraft.class_8610 handler, net.minecraft.class_2960 channelName, net.minecraft.class_2540 buf) {
        Objects.requireNonNull(handler, "Server configuration entity cannot be null");
        Objects.requireNonNull(channelName, "Channel name cannot be null");
        Objects.requireNonNull(buf, "Packet byte buf cannot be null");
        handler.method_14364(createS2CPacket(channelName, buf));
    }

    public static <T extends FabricPacket> void send(net.minecraft.class_8610 handler, T packet) {
        Objects.requireNonNull(handler, "Server configuration handler cannot be null");
        Objects.requireNonNull(packet, "Packet cannot be null");
        Objects.requireNonNull(packet.getType(), "Packet#getType cannot return null");
        handler.method_14364(createS2CPacket(packet));
    }
}
