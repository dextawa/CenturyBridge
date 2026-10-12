package top.dext.centurybridge.step.v1_20_5.rt;

import java.util.function.Function;
import net.minecraft.class_2540;

/**
 * super(buf) of a 1.20.4 subclass of a packet that 1.20.5 builds from its fields only (area s4net):
 * the converter reshapes the call to the constructor over the fields, each argument computed from
 * the buffer in turn (CbNetPacketReaders.cbSpread*). The first decodes the whole packet once and
 * keeps it; the later ones take their field from that same packet, the last one letting it go. The
 * arguments are computed one after the other, straight-line, before the constructor is invoked, so
 * nothing else runs on the thread in between; the buffer is checked anyway.
 */
public final class CbNetSpread {

    /** {buffer, the packet decoded from it} between the first argument and the last */
    private static final ThreadLocal<Object[]> PENDING = new ThreadLocal<>();

    /** the first argument: the packet decoded from the buffer, kept for the others */
    public static <P> P cbFirst(class_2540 buf, Function<class_2540, P> decode) {
        P packet = decode.apply(buf);
        PENDING.set(new Object[] {buf, packet});
        return packet;
    }

    /** a later argument: the packet the first one decoded from this buffer */
    @SuppressWarnings("unchecked")
    public static <P> P cbNext(class_2540 buf, boolean last) {
        Object[] pending = PENDING.get();
        if (pending == null || pending[0] != buf) {
            throw new IllegalStateException("CenturyBridge: no packet decoded from this buffer for the constructor being called");
        }
        if (last) {
            PENDING.remove();
        }
        return (P) pending[1];
    }

    private CbNetSpread() {
    }
}
