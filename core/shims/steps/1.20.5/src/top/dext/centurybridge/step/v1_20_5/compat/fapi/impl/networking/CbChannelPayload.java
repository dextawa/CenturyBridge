package top.dext.centurybridge.step.v1_20_5.compat.fapi.impl.networking;

import java.util.Objects;
import net.fabricmc.fabric.api.networking.v1.PacketByteBufs;
import top.dext.centurybridge.step.v1_20_5.compat.fapi.networking.v1.FabricPacket;
import top.dext.centurybridge.step.v1_20_5.compat.fapi.networking.v1.PacketType;
import top.dext.centurybridge.step.v1_20_5.rt.CbPayloads;

/**
 * What 1.20.4's Fabric API sent on a channel, as a 1.20.5 custom payload: the bytes of a channel send
 * ({@code send(channel, buf)}, 1.20.4's UntypedPayload) or a {@link FabricPacket} ({@code send(packet)},
 * its TypedPayload). On the wire it is the channel id followed by those bytes, as in 1.20.4 (written by
 * the codec of {@link CbChannels} or, where nothing registered one on this side, CbPayloads' fallback).
 * Over a local connection the object itself arrives, so a typed packet reaches its handler as the same
 * object, as 1.20.4's TypedPayload did. The handlers take it the way 1.20.4's ResolvablePayload resolved:
 * a channel handler gets a copy of the bytes (the packet written out when it arrived typed), a packet
 * handler the packet (read by its type when it arrived as bytes, with 1.20.4's dangling-bytes check).
 */
public final class CbChannelPayload implements CbPayloads.Encodable {

    /**
     * Fabric API's development default: a typed packet is written out at once even over a local
     * connection, so single player behaves like a remote connection. Same property and default as 1.20.4's
     * NetworkingImpl.FORCE_PACKET_SERIALIZATION.
     */
    static final boolean FORCE_PACKET_SERIALIZATION = Boolean.parseBoolean(System.getProperty(
        "fabric-api.networking.force-packet-serialization",
        Boolean.toString(net.fabricmc.loader.api.FabricLoader.getInstance().isDevelopmentEnvironment())));

    private final net.minecraft.class_8710.class_9154<CbChannelPayload> id;
    /** the bytes: the sender's buffer, or what arrived (null when typed) */
    private final net.minecraft.class_2540 data;
    private final FabricPacket packet;

    private CbChannelPayload(net.minecraft.class_8710.class_9154<CbChannelPayload> id, net.minecraft.class_2540 data, FabricPacket packet) {
        this.id = id;
        this.data = data;
        this.packet = packet;
    }

    /** a channel send: the buffer is kept and written when the packet is (1.20.4's UntypedPayload) */
    public static CbChannelPayload raw(net.minecraft.class_2960 channel, net.minecraft.class_2540 buf) {
        return new CbChannelPayload(CbChannels.id(channel), buf, null);
    }

    /** a packet send (1.20.4's TypedPayload, written out at once under force-packet-serialization) */
    public static CbChannelPayload typed(FabricPacket packet) {
        Objects.requireNonNull(packet, "Packet cannot be null");
        Objects.requireNonNull(packet.getType(), "Packet#getType cannot return null");
        CbChannelPayload p = new CbChannelPayload(CbChannels.id(packet.getType().getId()), null, packet);
        return FORCE_PACKET_SERIALIZATION ? new CbChannelPayload(p.id, p.buffer(), null) : p;
    }

    /** a payload that arrived, its bytes read off the packet */
    static CbChannelPayload arrived(net.minecraft.class_8710.class_9154<CbChannelPayload> id, net.minecraft.class_2540 data) {
        return new CbChannelPayload(id, data, null);
    }

    /**
     * Any payload a channel receiver is handed: this class's own, or -- over a local connection, where no
     * codec runs -- another payload the layer writes the 1.20.4 way, turned into its bytes.
     */
    public static CbChannelPayload of(net.minecraft.class_8710 payload) {
        CbChannelPayload p = channelForm(payload);
        if (p == null) {
            throw new IllegalArgumentException("payload " + payload.method_56479().comp_2242() + " (" + payload.getClass().getName()
                + ") has no 1.20.4 channel form: a 1.20.5 payload type is registered for this channel besides the 1.20.4 one");
        }
        return p;
    }

    /** {@link #of}, or null for a payload only its 1.20.5 codec can write (a vanilla or 1.20.5 type) */
    public static CbChannelPayload channelForm(net.minecraft.class_8710 payload) {
        if (payload instanceof CbChannelPayload p) {
            return p;
        }
        if (payload instanceof CbPayloads.Encodable e) {
            net.minecraft.class_2540 buf = PacketByteBufs.create();
            e.cb$write(buf);
            return new CbChannelPayload(CbChannels.id(payload.method_56479().comp_2242()), buf, null);
        }
        return null;
    }

    @Override
    public net.minecraft.class_8710.class_9154<? extends net.minecraft.class_8710> method_56479() {
        return id;
    }

    public net.minecraft.class_2960 channel() {
        return id.comp_2242();
    }

    /** the typed packet when sent as one and not written out yet */
    public FabricPacket typedPacket() {
        return packet;
    }

    /**
     * The payload's bytes, a fresh buffer each call (1.20.4's UntypedPayload.buffer() / TypedPayload.resolve(null)):
     * a typed packet written out, else a copy of the bytes, keeping the registry access of a play-phase buffer.
     */
    public net.minecraft.class_2540 buffer() {
        if (packet != null) {
            net.minecraft.class_2540 buf = PacketByteBufs.create();
            packet.write(buf);
            return buf;
        }
        return CbChannels.copy(data);
    }

    /** the packet for a handler of its type (1.20.4's ResolvablePayload.resolve(type)) */
    @SuppressWarnings("unchecked")
    public <T extends FabricPacket> T packet(PacketType<T> type) {
        if (packet != null) {
            return (T) packet;
        }
        net.minecraft.class_2540 copy = CbChannels.copy(data);
        T read = type.read(copy);
        int dangling = copy.readableBytes();
        if (dangling > 0) {
            throw new IllegalStateException("Found " + dangling + " extra bytes when reading packet " + channel());
        }
        return read;
    }

    /** written after the id: the bytes, or the packet's own write (1.20.4's UntypedPayload / TypedPayload.write) */
    @Override
    public void cb$write(net.minecraft.class_2540 buf) {
        if (packet != null) {
            packet.write(buf);
        } else {
            buf.writeBytes(data.copy());
        }
    }
}
