package top.dext.centurybridge.step.v1_20_5.rt;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.Channel;
import io.netty.util.Attribute;
import io.netty.util.AttributeKey;
import it.unimi.dsi.fastutil.objects.Object2IntMap;
import java.util.Locale;
import java.util.function.Consumer;
import java.util.function.Function;
import net.minecraft.class_2535;
import net.minecraft.class_2539;
import net.minecraft.class_2540;
import net.minecraft.class_2547;
import net.minecraft.class_2596;
import net.minecraft.class_2598;
import net.minecraft.class_2960;
import net.minecraft.class_8037;
import net.minecraft.class_8039;
import net.minecraft.class_8042;
import net.minecraft.class_8592;
import net.minecraft.class_8703;
import net.minecraft.class_9094;
import net.minecraft.class_9095;
import net.minecraft.class_9097;
import net.minecraft.class_9099;
import net.minecraft.class_9103;
import net.minecraft.class_9127;
import net.minecraft.class_9129;
import net.minecraft.class_9136;
import net.minecraft.class_9145;
import net.minecraft.class_9157;
import top.dext.centurybridge.step.v1_20_5.facade.ConnectionProtocolCodecData;
import top.dext.centurybridge.step.v1_20_5.mixin.CbNetDispatchAccess;

/**
 * 1.20.4's netty protocol state on 1.20.5's (area s4fin).
 *
 * 1.20.4 kept each direction's protocol in a channel attribute holding a ConnectionProtocol.CodecData
 * (Connection.ATTRIBUTE_SERVERBOUND_PROTOCOL / ATTRIBUTE_CLIENTBOUND_PROTOCOL); its decoder, encoder,
 * bundler and flow validator read the attribute for every packet, and a protocol change was setting
 * it (ProtocolSwapHandler on a terminal packet, Connection.setClientboundProtocolAfterHandshake). 1.20.5
 * gives each handler a fixed ProtocolInfo and changes protocol by replacing the handlers
 * (Connection.setupInboundProtocol / setupOutboundProtocol).
 *
 * Here a CodecData is a view of a 1.20.5 ProtocolInfo (facade/ConnectionProtocolCodecData: ids are
 * 1.20.5's wire ids). The two attributes are CenturyBridge's keys, kept equal to the protocols 1.20.5
 * sets up on every connection (CbFinConnectionMixin), so reading them answers what 1.20.4 answered.
 * 1.20.4's handlers, built on an attribute key, are back as subclasses of 1.20.5's that read the
 * attribute as 1.20.4's did (facade/LegacyPacketDecoder ...): in a pipeline a mod builds they follow
 * the attribute; in a vanilla pipeline 1.20.5 replaces them by name on a protocol change, as it
 * replaces its own.
 */
public final class CbFinNet {

    private CbFinNet() {
    }

    /** Connection.ATTRIBUTE_SERVERBOUND_PROTOCOL */
    public static final AttributeKey<ConnectionProtocolCodecData<?>> SERVERBOUND = AttributeKey.valueOf("centurybridge:serverbound_protocol");
    /** Connection.ATTRIBUTE_CLIENTBOUND_PROTOCOL */
    public static final AttributeKey<ConnectionProtocolCodecData<?>> CLIENTBOUND = AttributeKey.valueOf("centurybridge:clientbound_protocol");

    /** Connection.ATTRIBUTE_SERVERBOUND_PROTOCOL */
    public static AttributeKey<ConnectionProtocolCodecData<?>> field_45666() {
        return SERVERBOUND;
    }

    /** Connection.ATTRIBUTE_CLIENTBOUND_PROTOCOL */
    public static AttributeKey<ConnectionProtocolCodecData<?>> field_45667() {
        return CLIENTBOUND;
    }

    /** the attribute of one direction */
    public static AttributeKey<ConnectionProtocolCodecData<?>> key(class_2598 flow) {
        return flow == class_2598.field_11941 ? SERVERBOUND : CLIENTBOUND;
    }

    /** ConnectionProtocol.NOT_REGISTERED: the id of a packet a protocol does not have */
    public static int field_41866() {
        return -1;
    }

    // ------------------------------------------------------------------ the 1.20.5 protocols

    private static volatile class_9127<?>[][] infos;

    /** every 1.20.5 protocol of one direction (the game protocol bound lazily: ids do not depend on registries) */
    private static class_9127<?>[] infos(class_2598 flow) {
        class_9127<?>[][] all = infos;
        if (all == null) {
            Function<ByteBuf, class_9129> registries = buf -> class_9129.method_56350(CbRegistries.currentAccess()).apply(buf);
            all = new class_9127<?>[][] {
                {class_9095.field_48172.bind(registries), class_9157.field_48698, class_9099.field_48247, class_9103.field_48263, class_9097.field_48231},
                {class_9095.field_48173.bind(registries), class_9157.field_48699, class_9099.field_48248, class_9103.field_48264},
            };
            infos = all;
        }
        return flow == class_2598.field_11941 ? all[0] : all[1];
    }

    /** the 1.20.5 protocol info of (protocol, direction), or null (1.20.5 has none: clientbound handshake) */
    public static class_9127<?> info(class_2539 protocol, class_2598 flow) {
        for (class_9127<?> info : infos(flow)) {
            if (info.comp_2234() == protocol) {
                return info;
            }
        }
        return null;
    }

    private static final ConnectionProtocolCodecData<?>[][] CODEC_DATA = new ConnectionProtocolCodecData<?>[class_2539.values().length][2];

    /** ConnectionProtocol.codec(flow): the view of 1.20.5's protocol of that direction */
    public static ConnectionProtocolCodecData<?> method_52921(class_2539 protocol, class_2598 flow) {
        int f = flow == class_2598.field_11941 ? 0 : 1;
        ConnectionProtocolCodecData<?> data = CODEC_DATA[protocol.ordinal()][f];
        if (data == null) {
            data = new ConnectionProtocolCodecData<>(protocol, flow, info(protocol, flow));
            CODEC_DATA[protocol.ordinal()][f] = data;
        }
        return data;
    }

    /** the wire-id table of a protocol info, or null */
    public static Object2IntMap<Object> table(class_9127<?> info) {
        return info != null && info.comp_2236() instanceof class_9136<?, ?, ?> dispatch ? ((CbNetDispatchAccess) dispatch).cb$toId() : null;
    }

    // ------------------------------------------------------------------ Connection

    /** Connection.setInitialProtocolAttributes(channel): both directions in the handshake protocol */
    public static void method_52901(Channel channel) {
        channel.attr(SERVERBOUND).set(method_52921(class_2539.field_20590, class_2598.field_11941));
        channel.attr(CLIENTBOUND).set(method_52921(class_2539.field_20590, class_2598.field_11942));
    }

    /**
     * Connection.setClientboundProtocolAfterHandshake(intent): the server's outgoing protocol is the one the
     * client asked for -- 1.20.5's setupOutboundProtocol of that protocol.
     */
    public static void method_52899(class_2535 connection, class_8592 intent) {
        if (connection instanceof LegacyAfterHandshake legacy) {
            legacy.method_52899(intent);
            return;
        }
        superAfterHandshake(connection, intent);
    }

    /** a 1.20.4 Connection subclass declaring setClientboundProtocolAfterHandshake(intent) (adapterHooks) */
    public interface LegacyAfterHandshake {
        void method_52899(class_8592 intent);
    }

    /**
     * 1.20.5's handshake listener sets the clientbound protocol right after the handshake (status or login) where
     * 1.20.4's called setClientboundProtocolAfterHandshake(STATUS / LOGIN): a 1.20.4 subclass's override of it is
     * that call (mixin/CbFinHandshakeMixin), any other connection takes 1.20.5's own setupOutboundProtocol.
     */
    public static void afterHandshake(class_2535 connection, class_9127<?> info) {
        if (connection instanceof LegacyAfterHandshake legacy) {
            legacy.method_52899(info == net.minecraft.class_9103.field_48264 ? class_8592.field_44975 : class_8592.field_44974);
        } else {
            connection.method_56329(info);
        }
    }

    /** super.setClientboundProtocolAfterHandshake(intent): 1.20.4's body (the protocol of the intent, clientbound) */
    public static void superAfterHandshake(class_2535 connection, class_8592 intent) {
        class_2539 protocol = CbNetPacketWrites.method_52285(intent);
        class_9127<?> info = info(protocol, class_2598.field_11942);
        if (info == null) {
            throw new IllegalStateException("CenturyBridge: 1.20.5 has no clientbound " + protocol.method_10785() + " protocol");
        }
        connection.method_56329(info);
    }

    /** the listener fields of a connection (CbFinConnectionMixin) */
    public interface ConnectionListeners {
        void cb$setListener(class_2547 listener);
    }

    /**
     * Connection.setListener(listener). 1.20.4 changed the protocol through the attribute beforehand and
     * then only replaced the listener (refusing one of another protocol or direction). 1.20.5 changes
     * protocol and listener together: a listener of the protocol the connection receives in replaces the
     * listener alone, as in 1.20.4; one of another protocol sets that protocol up with it, which is what
     * 1.20.4 had done by the time it set such a listener.
     */
    @SuppressWarnings({"unchecked", "rawtypes"})
    public static void method_10763(class_2535 connection, class_2547 listener) {
        if (listener == null) {
            throw new NullPointerException("packetListener");
        }
        class_2598 receiving = connection.method_36121();
        if (listener.method_52895() != receiving) {
            throw new IllegalStateException("Trying to set listener for wrong side: connection is " + receiving + ", but listener is "
                + listener.method_52895());
        }
        class_2547 current = connection.method_10744();
        if (current != null && current.method_52280() == listener.method_52280()) {
            ((ConnectionListeners) connection).cb$setListener(listener);
            return;
        }
        class_9127 info = info(listener.method_52280(), receiving);
        if (info == null) {
            throw new IllegalStateException("CenturyBridge: 1.20.5 has no " + receiving + " " + listener.method_52280().method_10785() + " protocol");
        }
        connection.method_56330(info, listener);
    }

    /** Connection.configureSerialization(pipeline, flow, monitor) of 1.20.4 always framed packets: not memory-only */
    public static boolean cbFramed() {
        return false;
    }

    /** a 1.20.4 Connection subclass's setListener(listener) (the adapter the override hook adds) */
    public interface LegacySetListener {
        void method_10763(class_2547 listener);
    }

    /**
     * 1.20.5 sets a connection's listener only with its protocol (setupInboundProtocol); a 1.20.4 subclass
     * overriding setListener is asked with the listener, and its super call sets that protocol up
     * ({@link #method_10763}, reaching 1.20.5's own through the guard).
     */
    public static void cbSetupInbound(LegacySetListener connection, class_9127<?> info, class_2547 listener) {
        connection.method_10763(listener);
    }

    /** ProtocolSwapHandler.swapProtocolIfNeeded(attribute, packet), 1.20.4's body */
    public static void method_53011(Attribute<ConnectionProtocolCodecData<?>> attribute, class_2596<?> packet) {
        class_2539 next = CbNetPacketWrites.method_52273(packet);
        if (next != null) {
            ConnectionProtocolCodecData<?> current = attribute.get();
            if (next != current.method_52922()) {
                attribute.set(method_52921(next, current.method_52926()));
            }
        }
    }

    // ------------------------------------------------------------------ BundlerInfo

    /** BundlerInfo.EMPTY: bundles nothing (1.20.4's body) */
    public static final class_8039 EMPTY_BUNDLER = new class_8039() {
        @Override
        public void method_48327(class_2596<?> packet, Consumer<class_2596<?>> output) {
            output.accept(packet);
        }

        @Override
        public class_8039.class_8040 method_48326(class_2596<?> packet) {
            return null;
        }
    };

    /** BundlerInfo.EMPTY */
    public static class_8039 field_41879() {
        return EMPTY_BUNDLER;
    }

    /**
     * BundlerInfo.createForPacket(bundleClass, constructor, delimiter): 1.20.5 names the bundle packet by its
     * type -- vanilla's ClientboundBundlePacket is GamePacketTypes.CLIENTBOUND_BUNDLE, a 1.20.4 mod's bundle
     * class the clientbound legacy type CenturyBridge gives that class (CbNetPacketWrites).
     */
    @SuppressWarnings({"unchecked", "rawtypes"})
    public static class_8039 method_48325(Class bundleClass, Function constructor, class_8037 delimiter) {
        class_9145 type = bundleClass == class_8042.class ? class_9094.field_48039
            : new class_9145<>(class_2598.field_11942, new class_2960("centurybridge",
                "legacy/" + bundleClass.getName().toLowerCase(Locale.ROOT).replaceAll("[^a-z0-9/._-]", "_")));
        return class_8039.method_48325(type, constructor, delimiter);
    }

    // ------------------------------------------------------------------ packets by wire id

    // ------------------------------------------------------------------ 1.20.4 handler constructors (ctorRedirects)

    /** new PacketDecoder(protocolKey) */
    public static net.minecraft.class_2543 cbNewDecoder(AttributeKey<ConnectionProtocolCodecData<?>> key) {
        return new top.dext.centurybridge.step.v1_20_5.facade.LegacyPacketDecoder(key);
    }

    /** new PacketEncoder(protocolKey) */
    public static net.minecraft.class_2545 cbNewEncoder(AttributeKey<ConnectionProtocolCodecData<?>> key) {
        return new top.dext.centurybridge.step.v1_20_5.facade.LegacyPacketEncoder(key);
    }

    /** new PacketBundlePacker(bundlerInfoKey) */
    public static net.minecraft.class_8035 cbNewBundlePacker(AttributeKey<? extends top.dext.centurybridge.step.v1_20_5.facade.BundlerInfoProvider> key) {
        return new top.dext.centurybridge.step.v1_20_5.facade.LegacyBundlePacker(key);
    }

    /** new PacketBundleUnpacker(bundlerInfoKey) */
    public static net.minecraft.class_8036 cbNewBundleUnpacker(AttributeKey<? extends top.dext.centurybridge.step.v1_20_5.facade.BundlerInfoProvider> key) {
        return new top.dext.centurybridge.step.v1_20_5.facade.LegacyBundleUnpacker(key);
    }

    /** decode a packet whose wire id was read already: 1.20.5's codec over the id and the rest of the buffer */
    @SuppressWarnings("unchecked")
    public static class_2596<?> cbDecode(class_9127<?> info, int id, class_2540 buf) {
        ByteBuf idBuf = Unpooled.buffer(5);
        class_8703.method_53017(idBuf, id);
        int idLength = idBuf.readableBytes();
        int start = buf.readerIndex();
        ByteBuf whole = Unpooled.wrappedBuffer(idBuf, buf.retainedSlice(start, buf.readableBytes()));
        try {
            Object packet = ((net.minecraft.class_9139<ByteBuf, Object>) (Object) info.comp_2236()).decode(whole);
            buf.readerIndex(start + whole.readerIndex() - idLength);
            return (class_2596<?>) packet;
        } finally {
            whole.release();
        }
    }

    /** the packet's fields as its protocol writes them (1.20.4's Packet.write(buf), CbNetPacketWrites) */
    public static void cbEncodeFields(class_2596<?> packet, class_2540 buf) {
        CbNetPacketWrites.method_11052(packet, buf);
    }

    /**
     * The protocol a 1.20.4 handler (facade/LegacyPacketDecoder, LegacyPacketEncoder) hands 1.20.5's
     * superclass, whose methods it overrides: it reads its protocol from the channel attribute for every
     * packet, as 1.20.4's did, and never asks this one for packets.
     */
    @SuppressWarnings("unchecked")
    public static <T extends class_2547> class_9127<T> cbAttributeProtocol() {
        return (class_9127<T>) ATTRIBUTE_PROTOCOL;
    }

    private static final class_9127<class_2547> ATTRIBUTE_PROTOCOL = new class_9127<>() {
        @Override
        public class_2539 comp_2234() {
            return class_2539.field_20590;
        }

        @Override
        public class_2598 comp_2235() {
            return class_2598.field_11941;
        }

        @Override
        public net.minecraft.class_9139<ByteBuf, class_2596<? super class_2547>> comp_2236() {
            throw new UnsupportedOperationException("CenturyBridge: a 1.20.4 packet handler reads its protocol from the channel attribute");
        }

        @Override
        public class_8039 comp_2237() {
            return null;
        }

        @Override
        public String toString() {
            return "CenturyBridge channel-attribute protocol (1.20.4)";
        }
    };
}
