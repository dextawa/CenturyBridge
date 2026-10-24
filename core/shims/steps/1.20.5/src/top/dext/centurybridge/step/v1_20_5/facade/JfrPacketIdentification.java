package top.dext.centurybridge.step.v1_20_5.facade;

import java.util.Objects;
import jdk.jfr.consumer.RecordedEvent;
import net.minecraft.class_2598;
import top.dext.centurybridge.step.v1_20_5.rt.CbFinJfr;

/**
 * 1.20.4's IoSummary.PacketIdentification (class_6767$class_6769, a record of direction, protocol id
 * and numeric packet id), gone in 1.20.5 for the top-level PacketIdentification of three strings. A
 * 1.20.4 mod's references are renamed here (classRenames); IoSummary keys convert between the two
 * where mods hand summaries in and read them out ({@link CbFinJfr#cbNewKeys} / {@link CbFinJfr#cbOldKeys}).
 * The number is the packet's 1.20.5 wire id ({@link CbFinJfr}). A plain class with the record's
 * members (its component fields public: a facade's members are reached without access wideners).
 */
public final class JfrPacketIdentification {

    public final class_2598 comp_259;
    public final String comp_260;
    public final int comp_261;

    public JfrPacketIdentification(class_2598 direction, String protocolId, int packetId) {
        this.comp_259 = direction;
        this.comp_260 = protocolId;
        this.comp_261 = packetId;
    }

    /** direction() */
    public class_2598 comp_259() {
        return comp_259;
    }

    /** protocolId() */
    public String comp_260() {
        return comp_260;
    }

    /** packetId() */
    public int comp_261() {
        return comp_261;
    }

    /** packetName(): 1.20.4 gave the packet class's name, or "unknown"; here the 1.20.5 type id of that packet */
    public String method_39445() {
        return CbFinJfr.cbPacketName(this);
    }

    /** from(event) */
    public static JfrPacketIdentification method_39447(RecordedEvent event) {
        return CbFinJfr.cbFromEvent(event);
    }

    @Override
    public boolean equals(Object o) {
        return o instanceof JfrPacketIdentification p && p.comp_259 == comp_259 && Objects.equals(p.comp_260, comp_260)
            && p.comp_261 == comp_261;
    }

    @Override
    public int hashCode() {
        return (Objects.hashCode(comp_259) * 31 + Objects.hashCode(comp_260)) * 31 + comp_261;
    }

    @Override
    public String toString() {
        return "PacketIdentification[direction=" + comp_259 + ", protocolId=" + comp_260 + ", packetId=" + comp_261 + "]";
    }
}
