package top.dext.centurybridge.compat.fapi;

import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

import net.fabricmc.fabric.api.networking.v1.PayloadTypeRegistry;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
/**
 * The raw-channel half of fabric-api's networking v1, rebuilt on the 1.20.5+
 * typed-payload API. Old mods think in (Identifier channel, PacketByteBuf);
 * the new API wants a registered CustomPayload type per channel. Each old
 * channel gets one lazily-registered RawPayload type that just carries bytes,
 * so the old contract survives byte-for-byte.
 *
 * Server side lives here; the client statics are in CbClientNetworking so a
 * dedicated server never loads client classes.
 */
public final class CbNetworking {

    static final Map<net.minecraft.resources.Identifier, net.minecraft.network.protocol.common.custom.CustomPacketPayload.Type<RawPayload>> IDS = new ConcurrentHashMap<>();
    static final Map<net.minecraft.resources.Identifier, net.minecraft.network.codec.StreamCodec<net.minecraft.network.RegistryFriendlyByteBuf, RawPayload>> CODECS = new ConcurrentHashMap<>();
    static final Set<net.minecraft.resources.Identifier> REG_C2S = ConcurrentHashMap.newKeySet();
    static final Set<net.minecraft.resources.Identifier> REG_S2C = ConcurrentHashMap.newKeySet();

    /** One payload class for every legacy channel: an id plus opaque bytes. */
    public static final class RawPayload implements net.minecraft.network.protocol.common.custom.CustomPacketPayload {
        final net.minecraft.resources.Identifier channel;
        final byte[] data;

        public RawPayload(net.minecraft.resources.Identifier channel, byte[] data) {
            this.channel = channel;
            this.data = data;
        }

        @Override
        public net.minecraft.network.protocol.common.custom.CustomPacketPayload.Type<? extends net.minecraft.network.protocol.common.custom.CustomPacketPayload> type() {
            return idOf(channel);
        }
    }

    static net.minecraft.network.protocol.common.custom.CustomPacketPayload.Type<RawPayload> idOf(net.minecraft.resources.Identifier ch) {
        return IDS.computeIfAbsent(ch, net.minecraft.network.protocol.common.custom.CustomPacketPayload.Type::new);
    }

    static net.minecraft.network.codec.StreamCodec<net.minecraft.network.RegistryFriendlyByteBuf, RawPayload> codecOf(net.minecraft.resources.Identifier ch) {
        return CODECS.computeIfAbsent(ch, c -> net.minecraft.network.protocol.common.custom.CustomPacketPayload.codec(
            (payload, buf) -> buf.writeBytes(payload.data),
            buf -> {
                byte[] d = new byte[buf.readableBytes()];
                buf.readBytes(d);
                return new RawPayload(c, d);
            }));
    }

    static void ensureC2S(net.minecraft.resources.Identifier ch) {
        if (REG_C2S.add(ch)) {
            PayloadTypeRegistry.serverboundPlay().register(idOf(ch), codecOf(ch));
        }
    }

    static void ensureS2C(net.minecraft.resources.Identifier ch) {
        if (REG_S2C.add(ch)) {
            PayloadTypeRegistry.clientboundPlay().register(idOf(ch), codecOf(ch));
        }
    }

    static byte[] drain(net.minecraft.network.FriendlyByteBuf buf) {
        byte[] d = new byte[buf.readableBytes()];
        buf.readBytes(d);
        return d;
    }

    static net.minecraft.network.FriendlyByteBuf wrap(RawPayload p) {
        return new net.minecraft.network.FriendlyByteBuf(io.netty.buffer.Unpooled.wrappedBuffer(p.data));
    }

    // ---- old ServerPlayNetworking statics, exact legacy descriptors ----

    public static boolean registerGlobalReceiver(net.minecraft.resources.Identifier channel, CbServerPlayHandler handler) {
        ensureC2S(channel);
        return ServerPlayNetworking.registerGlobalReceiver(idOf(channel),
            (payload, ctx) -> handler.receive(ctx.server(), ctx.player(),
                ctx.player().connection, wrap(payload), ctx.responseSender()));
    }

    public static boolean registerReceiver(net.minecraft.server.network.ServerGamePacketListenerImpl listener,
                                           net.minecraft.resources.Identifier channel, CbServerPlayHandler handler) {
        ensureC2S(channel);
        return ServerPlayNetworking.registerReceiver(listener, idOf(channel),
            (payload, ctx) -> handler.receive(ctx.server(), ctx.player(),
                ctx.player().connection, wrap(payload), ctx.responseSender()));
    }

    public static void send(net.minecraft.server.level.ServerPlayer player, net.minecraft.resources.Identifier channel, net.minecraft.network.FriendlyByteBuf buf) {
        ensureS2C(channel);
        ServerPlayNetworking.send(player, new RawPayload(channel, drain(buf)));
    }

    /** was ServerPlayNetworking.createS2CPacket(Identifier, PacketByteBuf) */
    public static net.minecraft.network.protocol.Packet createS2CPacket(net.minecraft.resources.Identifier channel, net.minecraft.network.FriendlyByteBuf buf) {
        ensureS2C(channel);
        return ServerPlayNetworking.createClientboundPacket(new RawPayload(channel, drain(buf)));
    }

    // ---- old typed-packet API (FabricPacket / PacketType, gone in 1.20.5) ----

    public static void send(net.minecraft.server.level.ServerPlayer player, CbFabricPacket packet) {
        net.minecraft.network.FriendlyByteBuf buf = new net.minecraft.network.FriendlyByteBuf(io.netty.buffer.Unpooled.buffer());
        packet.write(buf);
        send(player, packet.getType().getId(), buf);
    }

    public static <T extends CbFabricPacket> boolean registerGlobalReceiver(CbPacketType<T> type, CbServerPlayPacketHandler<T> handler) {
        ensureC2S(type.getId());
        return ServerPlayNetworking.registerGlobalReceiver(idOf(type.getId()),
            (payload, ctx) -> handler.receive(type.read(wrap(payload)), ctx.player(), ctx.responseSender()));
    }

    // ---- old PacketSender.sendPacket(Identifier, PacketByteBuf) (instance redirect: receiver first) ----

    public static void sendPacket(net.fabricmc.fabric.api.networking.v1.PacketSender self,
                                  net.minecraft.resources.Identifier channel, net.minecraft.network.FriendlyByteBuf buf) {
        // direction unknowable at the call site (response sender exists on both
        // sides); registering the codec both ways is harmless metadata
        ensureC2S(channel);
        ensureS2C(channel);
        self.sendPacket(new RawPayload(channel, drain(buf)));
    }

    /** was PlayerLookup.world(ServerWorld) -- renamed to level() in the mojang-name era */
    public static java.util.Collection<net.minecraft.server.level.ServerPlayer> world(net.minecraft.server.level.ServerLevel level) {
        return net.fabricmc.fabric.api.networking.v1.PlayerLookup.level(level);
    }

    private CbNetworking() {
    }
}
