package top.dext.centurybridge.step.v1_20_5.compat.fapi.impl.networking;

import io.netty.buffer.Unpooled;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import net.fabricmc.fabric.api.networking.v1.PacketByteBufs;
import net.fabricmc.fabric.api.networking.v1.PayloadTypeRegistry;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 1.20.4's Fabric API channels on 1.20.5's payload types. A 1.20.4 channel is an id and the bytes sent on
 * it; 1.20.5's Fabric API only dispatches payloads whose type is registered in its PayloadTypeRegistry for
 * the phase and direction, and refuses a receiver for an id without one ("register the payload type before
 * the handler", as its docs ask of a 1.20.5 mod at initialization). So the receive side of a channel --
 * the registry a receiver needs -- gets a {@link CbChannelPayload} type with a raw codec the first time a
 * receiver for the channel is registered, which a 1.20.4 mod does where a 1.20.5 one registers its types.
 * The send side needs nothing registered: a channel payload sent where no receiver registered its type on
 * this side is written by CbPayloads' fallback (the encoder 1.20.5 reaches for an unregistered id), never by
 * registering from a game thread while netty threads read Fabric API's registry, a plain HashMap.
 *
 * <p>The raw codec reads what 1.20.4's PayloadHelper.readCustom read: every remaining byte, refused above
 * the packet's 1.20.4 (and 1.20.5) limit -- 32767 bytes serverbound, 1048576 clientbound -- with 1.20.4's
 * message. A channel whose id a 1.20.5 payload type already holds (a native mod's, registered first) cannot
 * have its 1.20.4 form: Fabric API's "already registered" is passed on, naming the channel.
 */
public final class CbChannels {

    static final Logger LOGGER = LoggerFactory.getLogger("centurybridge/fabric-networking-api-v1");

    /** CustomPayloadC2SPacket's MAX_PAYLOAD_SIZE (1.20.4 and 1.20.5) */
    public static final int C2S_MAX = 32767;
    /** CustomPayloadS2CPacket's MAX_PAYLOAD_SIZE (1.20.4 and 1.20.5) */
    public static final int S2C_MAX = 1048576;

    private static final net.minecraft.class_2960 REGISTER = new net.minecraft.class_2960("minecraft", "register");
    private static final net.minecraft.class_2960 UNREGISTER = new net.minecraft.class_2960("minecraft", "unregister");

    private static final Map<net.minecraft.class_2960, net.minecraft.class_8710.class_9154<CbChannelPayload>> IDS = new ConcurrentHashMap<>();
    /** per Fabric API registry: the channels this layer registered its raw type for */
    private static final Map<PayloadTypeRegistry<?>, Set<net.minecraft.class_2960>> REGISTERED = new HashMap<>();

    private CbChannels() {
    }

    /** the channel's payload id, one per channel */
    public static net.minecraft.class_8710.class_9154<CbChannelPayload> id(net.minecraft.class_2960 channel) {
        return IDS.computeIfAbsent(channel, net.minecraft.class_8710.class_9154::new);
    }

    /** serverbound play: what a server's receiver needs */
    public static net.minecraft.class_8710.class_9154<CbChannelPayload> playC2S(net.minecraft.class_2960 channel) {
        return receivable(PayloadTypeRegistry.playC2S(), channel, C2S_MAX);
    }

    /** clientbound play: what a client's receiver needs */
    public static net.minecraft.class_8710.class_9154<CbChannelPayload> playS2C(net.minecraft.class_2960 channel) {
        return receivable(PayloadTypeRegistry.playS2C(), channel, S2C_MAX);
    }

    /** serverbound configuration */
    public static net.minecraft.class_8710.class_9154<CbChannelPayload> configurationC2S(net.minecraft.class_2960 channel) {
        return receivable(PayloadTypeRegistry.configurationC2S(), channel, C2S_MAX);
    }

    /** clientbound configuration */
    public static net.minecraft.class_8710.class_9154<CbChannelPayload> configurationS2C(net.minecraft.class_2960 channel) {
        return receivable(PayloadTypeRegistry.configurationS2C(), channel, S2C_MAX);
    }

    private static synchronized net.minecraft.class_8710.class_9154<CbChannelPayload> receivable(
            PayloadTypeRegistry<? extends net.minecraft.class_2540> registry, net.minecraft.class_2960 channel, int maxSize) {
        net.minecraft.class_8710.class_9154<CbChannelPayload> id = id(channel);
        if (REGISTERED.computeIfAbsent(registry, r -> new HashSet<>()).add(channel)) {
            try {
                registry.register(id, new Codec(id, maxSize));
            } catch (IllegalArgumentException e) {
                REGISTERED.get(registry).remove(channel);
                throw new IllegalArgumentException("channel " + channel + " cannot take a 1.20.4 receiver: " + e.getMessage(), e);
            }
        }
        return id;
    }

    /** 1.20.4's GlobalReceiverRegistry checks, in its order: null channel, null handler, a reserved channel */
    public static void assertGlobalRegistrable(net.minecraft.class_2960 channel, Object handler) {
        Objects.requireNonNull(channel, "Channel name cannot be null");
        Objects.requireNonNull(handler, "Channel handler cannot be null");
        if (reserved(channel)) {
            throw new IllegalArgumentException(String.format("Cannot register handler for reserved channel with name \"%s\"", channel));
        }
    }

    /** 1.20.4's AbstractNetworkAddon.registerChannel checks */
    public static void assertRegistrable(net.minecraft.class_2960 channel, Object handler) {
        Objects.requireNonNull(channel, "Channel name cannot be null");
        Objects.requireNonNull(handler, "Packet handler cannot be null");
        if (reserved(channel)) {
            throw new IllegalArgumentException(String.format("Cannot (un)register handler for reserved channel with name \"%s\"", channel));
        }
    }

    /** minecraft:register / minecraft:unregister (1.20.4's NetworkingImpl.isReservedCommonChannel) */
    public static boolean reserved(net.minecraft.class_2960 channel) {
        return channel.equals(REGISTER) || channel.equals(UNREGISTER);
    }

    /** a copy of a payload's bytes, keeping a play-phase buffer's registry access */
    static net.minecraft.class_2540 copy(net.minecraft.class_2540 data) {
        if (data instanceof net.minecraft.class_9129 registryBuf) {
            return new net.minecraft.class_9129(Unpooled.copiedBuffer(data), registryBuf.method_56349());
        }
        return PacketByteBufs.copy(data);
    }

    /**
     * 1.20.4 ran a channel handler on the netty thread, where an exception (logged by Fabric API with this
     * message, then rethrown) reached the connection's exception handler, which disconnects with this
     * reason. The handler now runs on the game thread, from Fabric API 1.20.5's task: the same outcome is
     * produced here.
     */
    public static net.minecraft.class_2561 handlerFailure(net.minecraft.class_2960 channel, Throwable t) {
        LOGGER.error("Encountered exception while handling in channel with name \"{}\"", channel, t);
        return net.minecraft.class_2561.method_43469("disconnect.genericReason", "Internal Exception: " + t);
    }

    /** a channel's raw codec: every remaining byte, up to the direction's limit */
    private record Codec(net.minecraft.class_8710.class_9154<CbChannelPayload> id, int maxSize)
            implements net.minecraft.class_9139<net.minecraft.class_2540, CbChannelPayload> {

        @Override
        public CbChannelPayload decode(net.minecraft.class_2540 buf) {
            int size = buf.readableBytes();
            if (size < 0 || size > maxSize) {
                throw new IllegalArgumentException("Payload may not be larger than " + maxSize + " bytes");
            }
            net.minecraft.class_2540 data = buf instanceof net.minecraft.class_9129 registryBuf
                ? new net.minecraft.class_9129(Unpooled.buffer(size), registryBuf.method_56349()) : PacketByteBufs.create();
            data.writeBytes(buf, size);
            return CbChannelPayload.arrived(id, data);
        }

        @Override
        public void encode(net.minecraft.class_2540 buf, CbChannelPayload payload) {
            payload.cb$write(buf);
        }
    }
}
