package top.dext.centurybridge.step.v1_20_5.rt;

import com.mojang.datafixers.util.Pair;
import it.unimi.dsi.fastutil.objects.Object2IntMap;
import java.net.SocketAddress;
import java.time.Duration;
import java.util.ArrayList;
import java.util.List;
import jdk.jfr.consumer.RecordedEvent;
import net.minecraft.class_2539;
import net.minecraft.class_2598;
import net.minecraft.class_2960;
import net.minecraft.class_6509;
import net.minecraft.class_6611;
import net.minecraft.class_6767;
import net.minecraft.class_6769;
import net.minecraft.class_9095;
import net.minecraft.class_9097;
import net.minecraft.class_9099;
import net.minecraft.class_9103;
import net.minecraft.class_9127;
import net.minecraft.class_9129;
import net.minecraft.class_9136;
import net.minecraft.class_9145;
import net.minecraft.class_9157;
import top.dext.centurybridge.step.v1_20_5.facade.JfrPacketIdentification;
import top.dext.centurybridge.step.v1_20_5.mixin.CbNetDispatchAccess;

/**
 * JFR packet profiling between 1.20.4 and 1.20.5 (area s4fin).
 *
 * 1.20.4 named a packet by (protocol, numeric id): JvmProfiler.onPacketReceived/Sent took the id
 * ConnectionProtocol gave the packet, PacketEvent recorded it as an int, and the stats parser
 * keyed packets by (direction, protocol id, int id). 1.20.5 names a packet by its PacketType
 * (direction + resource id) and records the id as a string. The numeric id a packet has on 1.20.5
 * is its wire id: its index in the protocol codec's id table (IdDispatchCodec), the number 1.20.5
 * writes in front of it, which is what a 1.20.4 mod asking the running game for a packet's id
 * gets. Both directions go through that table here.
 *
 * Direction: 1.20.4's profiler calls and events carried none. Its own stats parser read
 * "minecraft.PacketSent" as clientbound and "minecraft.PacketReceived" as serverbound (the
 * server's view), and the bridges take the same convention where 1.20.5 needs a direction.
 *
 * A number with no packet in the table (an id 1.20.5's protocol does not have) is kept as the
 * type centurybridge:id/&lt;n&gt;, read back as n: 1.20.4 recorded any int it was given.
 */
public final class CbFinJfr {

    private CbFinJfr() {
    }

    private static final String UNKNOWN_NAMESPACE = "centurybridge";
    private static final String UNKNOWN_PREFIX = "id/";

    // ------------------------------------------------------------------ the 1.20.5 id tables

    private static volatile class_9127<?>[][] infos;

    /** every protocol of one direction (the game protocol bound lazily: ids do not depend on registries) */
    private static class_9127<?>[] infos(class_2598 flow) {
        class_9127<?>[][] all = infos;
        if (all == null) {
            class_9127<?> gameServerbound = class_9095.field_48172.bind(
                buf -> class_9129.method_56350(CbRegistries.currentAccess()).apply(buf));
            class_9127<?> gameClientbound = class_9095.field_48173.bind(
                buf -> class_9129.method_56350(CbRegistries.currentAccess()).apply(buf));
            all = new class_9127<?>[][] {
                {gameServerbound, class_9157.field_48698, class_9099.field_48247, class_9103.field_48263, class_9097.field_48231},
                {gameClientbound, class_9157.field_48699, class_9099.field_48248, class_9103.field_48264},
            };
            infos = all;
        }
        return flow == class_2598.field_11941 ? all[0] : all[1];
    }

    /** the wire-id table of a protocol in one direction, or null (no such protocol, or not a dispatch codec) */
    private static Object2IntMap<Object> table(class_2539 protocol, class_2598 flow) {
        if (protocol == null || flow == null) {
            return null;
        }
        for (class_9127<?> info : infos(flow)) {
            if (info.comp_2234() == protocol && info.comp_2236() instanceof class_9136<?, ?, ?> dispatch) {
                return ((CbNetDispatchAccess) dispatch).cb$toId();
            }
        }
        return null;
    }

    /** the packet type with this wire id, or centurybridge:id/&lt;id&gt; when the protocol has none */
    public static class_9145<?> cbType(class_2539 protocol, class_2598 flow, int id) {
        Object2IntMap<Object> table = table(protocol, flow);
        if (table != null) {
            for (Object2IntMap.Entry<Object> e : table.object2IntEntrySet()) {
                if (e.getIntValue() == id && e.getKey() instanceof class_9145<?> type) {
                    return type;
                }
            }
        }
        return new class_9145<>(flow, new class_2960(UNKNOWN_NAMESPACE, UNKNOWN_PREFIX + id));
    }

    /** whether the type is one {@link #cbType} made for a number with no packet */
    private static int unknownId(class_2960 id) {
        if (UNKNOWN_NAMESPACE.equals(id.method_12836()) && id.method_12832().startsWith(UNKNOWN_PREFIX)) {
            try {
                return Integer.parseInt(id.method_12832().substring(UNKNOWN_PREFIX.length()));
            } catch (NumberFormatException e) {
                return -1;
            }
        }
        return -1;
    }

    /** the type's wire id in the protocol (the number {@link #cbType} came from), or -1 */
    public static int cbWireId(class_2539 protocol, class_9145<?> type) {
        int unknown = unknownId(type.comp_2231());
        if (unknown >= 0) {
            return unknown;
        }
        Object2IntMap<Object> table = table(protocol, type.comp_2230());
        return table != null ? table.getOrDefault(type, -1) : -1;
    }

    private static class_2539 protocolById(String id) {
        for (class_2539 p : class_2539.values()) {
            if (p.method_10785().equals(id)) {
                return p;
            }
        }
        return null;
    }

    private static class_2598 flowById(String id) {
        for (class_2598 f : class_2598.values()) {
            if (f.method_56444().equals(id)) {
                return f;
            }
        }
        return null;
    }

    /** a recorded 1.20.5 packet id (a type id, or a number) as the wire id, or -1 */
    private static int wireId(String protocolId, String direction, String packetId) {
        if (packetId == null) {
            return -1;
        }
        try {
            return Integer.parseInt(packetId);
        } catch (NumberFormatException ignored) {
            // a type id
        }
        class_2960 id = class_2960.method_12829(packetId);
        class_2598 flow = flowById(direction);
        if (id == null || flow == null) {
            return -1;
        }
        return cbWireId(protocolById(protocolId), new class_9145<>(flow, id));
    }

    /** the 1.20.5 packet id string for a 1.20.4 (protocol id, direction, number) */
    private static String packetIdString(String protocolId, class_2598 flow, int id) {
        class_2539 protocol = protocolById(protocolId);
        if (protocol == null || flow == null) {
            return Integer.toString(id);
        }
        return cbType(protocol, flow, id).comp_2231().toString();
    }

    // ------------------------------------------------------------------ JvmProfiler

    /** was JvmProfiler.onPacketReceived(protocol, id, address, bytes): the received packet is serverbound */
    public static void method_38656(class_6611 profiler, class_2539 protocol, int id, SocketAddress address, int bytes) {
        profiler.method_38656(protocol, cbType(protocol, class_2598.field_11941, id), address, bytes);
    }

    /** was JvmProfiler.onPacketSent(protocol, id, address, bytes): the sent packet is clientbound */
    public static void method_38657(class_6611 profiler, class_2539 protocol, int id, SocketAddress address, int bytes) {
        profiler.method_38657(protocol, cbType(protocol, class_2598.field_11942, id), address, bytes);
    }

    /** a 1.20.4 profiler's onPacketReceived (the adapter the override hook adds) */
    public interface CbLegacyPacketReceived {
        void method_38656(class_2539 protocol, int id, SocketAddress address, int bytes);
    }

    /** a 1.20.4 profiler's onPacketSent */
    public interface CbLegacyPacketSent {
        void method_38657(class_2539 protocol, int id, SocketAddress address, int bytes);
    }

    /** 1.20.5 calls onPacketReceived(protocol, type, ..) on a 1.20.4 profiler: its own method with the wire id */
    public static void cbPacketReceived(CbLegacyPacketReceived profiler, class_2539 protocol, class_9145<?> type,
            SocketAddress address, int bytes) {
        profiler.method_38656(protocol, cbWireId(protocol, type), address, bytes);
    }

    /** the same for onPacketSent */
    public static void cbPacketSent(CbLegacyPacketSent profiler, class_2539 protocol, class_9145<?> type,
            SocketAddress address, int bytes) {
        profiler.method_38657(protocol, cbWireId(protocol, type), address, bytes);
    }

    /**
     * onRegionFileRead / onRegionFileWrite, abstract in 1.20.5's JvmProfiler (abstractFills): 1.20.4
     * profiled no region-file IO, so a 1.20.4 profiler was never told of it and records nothing,
     * exactly as on 1.20.4.
     */
    public static void cbRegionFileIo(class_6611 profiler, net.minecraft.class_9240 storage, net.minecraft.class_1923 pos,
            net.minecraft.class_4486 version, int bytes) {
        // nothing: the 1.20.4 profiler had no such event
    }

    // ------------------------------------------------------------------ packet events

    /** PacketReceivedEvent's direction (1.20.4's parser: a received packet is serverbound) */
    public static String cbServerbound() {
        return class_2598.field_11941.method_56444();
    }

    /** PacketSentEvent's direction */
    public static String cbClientbound() {
        return class_2598.field_11942.method_56444();
    }

    /**
     * A PacketEvent of a 1.20.4 mod's own event class: 1.20.4's event had no direction and 1.20.5's
     * parser reads only vanilla's two event types, so it records none (the empty string).
     */
    public static String cbNoDirection() {
        return "";
    }

    /** the received packet's id as 1.20.5 records it: its type id, or the number */
    public static String cbReceivedPacketId(String protocolId, int id) {
        return packetIdString(protocolId, class_2598.field_11941, id);
    }

    /** the sent packet's id as 1.20.5 records it */
    public static String cbSentPacketId(String protocolId, int id) {
        return packetIdString(protocolId, class_2598.field_11942, id);
    }

    /** a 1.20.4 mod's own PacketEvent: no direction to look the number up with, so the number itself */
    public static String cbPacketId(int id) {
        return Integer.toString(id);
    }

    /** read of the 1.20.4 field PacketEvent.packetId (int): the recorded id as the wire id, or -1 */
    public static int packetId(class_6509 event) {
        return wireId(event.protocolId, event.packetDirection, event.packetId);
    }

    // ------------------------------------------------------------------ PacketIdentification / IoSummary

    /** the 1.20.4 identification of a 1.20.5 one */
    public static JfrPacketIdentification cbOld(class_6769 id) {
        class_2598 flow = flowById(id.comp_259());
        return new JfrPacketIdentification(flow, id.comp_260(), wireId(id.comp_260(), id.comp_259(), id.comp_261()));
    }

    /** the 1.20.5 identification of a 1.20.4 one */
    public static class_6769 cbNew(JfrPacketIdentification id) {
        class_2598 flow = id.comp_259();
        return new class_6769(flow != null ? flow.method_56444() : "", id.comp_260(),
            packetIdString(id.comp_260(), flow, id.comp_261()));
    }

    /** the type name 1.20.4's PacketIdentification.packetName() gave ("unknown" when the protocol has no such packet) */
    public static String cbPacketName(JfrPacketIdentification id) {
        class_2539 protocol = protocolById(id.comp_260());
        if (protocol == null || id.comp_259() == null) {
            return "unknown";
        }
        class_9145<?> type = cbType(protocol, id.comp_259(), id.comp_261());
        return unknownId(type.comp_2231()) >= 0 ? "unknown" : type.comp_2231().toString();
    }

    /** 1.20.4's PacketIdentification.from(event), reading a 1.20.5 event */
    public static JfrPacketIdentification cbFromEvent(RecordedEvent event) {
        String protocolId = event.getString("protocolId");
        class_2598 flow = event.hasField("packetDirection") ? flowById(event.getString("packetDirection")) : null;
        if (flow == null) {
            flow = event.getEventType().getName().equals("minecraft.PacketSent") ? class_2598.field_11942 : class_2598.field_11941;
        }
        Object raw = event.getValue("packetId");
        int id = raw instanceof Number n ? n.intValue() : wireId(protocolId, flow.method_56444(), raw == null ? null : raw.toString());
        return new JfrPacketIdentification(flow, protocolId, id);
    }

    /** new IoSummary(duration, pairs) of a 1.20.4 mod: its packet keys as the 1.20.5 identifications vanilla reads */
    public static List<?> cbNewKeys(List<?> pairs) {
        return convertKeys(pairs, true);
    }

    /** IoSummary.largestSizeContributors() read by a 1.20.4 mod: packet keys as the 1.20.4 identification */
    public static List<?> cbOldKeys(List<?> pairs) {
        return convertKeys(pairs, false);
    }

    private static List<?> convertKeys(List<?> pairs, boolean toNew) {
        if (pairs == null) {
            return null;
        }
        boolean any = false;
        for (Object o : pairs) {
            if (o instanceof Pair<?, ?> p && (toNew ? p.getFirst() instanceof JfrPacketIdentification : p.getFirst() instanceof class_6769)) {
                any = true;
                break;
            }
        }
        if (!any) {
            return pairs;
        }
        List<Object> out = new ArrayList<>(pairs.size());
        for (Object o : pairs) {
            if (o instanceof Pair<?, ?> p) {
                Object k = p.getFirst();
                if (toNew && k instanceof JfrPacketIdentification old) {
                    o = Pair.of(cbNew(old), p.getSecond());
                } else if (!toNew && k instanceof class_6769 id) {
                    o = Pair.of(cbOld(id), p.getSecond());
                }
            }
            out.add(o);
        }
        return out;
    }

    /** JfrStatsResult's chunk read/write summaries, which 1.20.4 did not record: empty over the recording */
    public static class_6767<?> cbNoChunkIo(Duration recording) {
        return new class_6767<>(recording, List.of());
    }
}
