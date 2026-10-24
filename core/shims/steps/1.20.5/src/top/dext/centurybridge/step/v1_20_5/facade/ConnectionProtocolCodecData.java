package top.dext.centurybridge.step.v1_20_5.facade;

import it.unimi.dsi.fastutil.objects.Object2IntMap;
import net.minecraft.class_2539;
import net.minecraft.class_2540;
import net.minecraft.class_2547;
import net.minecraft.class_2596;
import net.minecraft.class_2598;
import net.minecraft.class_8039;
import net.minecraft.class_9127;
import top.dext.centurybridge.step.v1_20_5.rt.CbFinNet;

/**
 * ConnectionProtocol.CodecData (gone in 1.20.5): one direction of one protocol, as a view of 1.20.5's
 * ProtocolInfo for it (rt/CbFinNet). Packet ids are 1.20.5's wire ids, the index in the protocol codec's id
 * table; a protocol 1.20.5 does not have (clientbound handshake: 1.20.4's was empty) knows no packet.
 */
public class ConnectionProtocolCodecData<T extends class_2547> implements BundlerInfoProvider {

    /** protocol */
    public final class_2539 field_45672;
    /** flow */
    public final class_2598 field_45673;
    /** the 1.20.5 protocol (null when 1.20.5 has none for this direction) */
    private final class_9127<?> cbInfo;

    public ConnectionProtocolCodecData(class_2539 protocol, class_2598 flow, class_9127<?> info) {
        this.field_45672 = protocol;
        this.field_45673 = flow;
        this.cbInfo = info;
    }

    /** the 1.20.5 protocol behind this view, or null */
    public class_9127<?> cbInfo() {
        return cbInfo;
    }

    /** protocol() */
    public class_2539 method_52922() {
        return field_45672;
    }

    /** flow() */
    public class_2598 method_52926() {
        return field_45673;
    }

    /** packetId(packet): the packet's wire id, or -1 (NOT_REGISTERED) for a packet this protocol does not have */
    public int method_52925(class_2596<?> packet) {
        Object2IntMap<Object> table = CbFinNet.table(cbInfo);
        return table == null ? -1 : table.getOrDefault(packet.method_55846(), -1);
    }

    /** bundlerInfo(): the protocol's bundler, or 1.20.4's empty one */
    @Override
    public class_8039 method_48312() {
        class_8039 bundler = cbInfo == null ? null : cbInfo.comp_2237();
        return bundler != null ? bundler : CbFinNet.EMPTY_BUNDLER;
    }

    /** createPacket(id, buf): the packet with that wire id read from the buffer, or null for an id the protocol does not have */
    public class_2596<?> method_52923(int id, class_2540 buf) {
        Object2IntMap<Object> table = CbFinNet.table(cbInfo);
        if (table == null || !table.containsValue(id)) {
            return null;
        }
        return CbFinNet.cbDecode(cbInfo, id, buf);
    }

    /** isValidPacketType(packet): whether this protocol has the packet's type */
    public boolean method_52927(class_2596<?> packet) {
        Object2IntMap<Object> table = CbFinNet.table(cbInfo);
        return table != null && table.containsKey(packet.method_55846());
    }
}
