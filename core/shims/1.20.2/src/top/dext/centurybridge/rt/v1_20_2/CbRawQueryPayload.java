package top.dext.centurybridge.rt.v1_20_2;

import net.minecraft.class_2540;
import net.minecraft.class_2960;
import net.minecraft.class_8595;

/**
 * 1.20.1 LoginQueryRequestS2CPacket carried (query id, channel, PacketByteBuf); 1.20.2's carries a
 * CustomQueryPayload. This is the old channel and buffer as such a payload, written exactly as the
 * 1.20.1 packet wrote them (the channel, then a copy of the buffer's readable bytes), and holding
 * the very buffer it was given, which 1.20.1's getPayload() handed back.
 */
public final class CbRawQueryPayload implements class_8595 {

    private final class_2960 id;
    private final class_2540 data;

    public CbRawQueryPayload(class_2960 id, class_2540 data) {
        this.id = id;
        this.data = data;
    }

    @Override
    public class_2960 comp_1571() {
        return id;
    }

    @Override
    public void method_52296(class_2540 out) {
        out.writeBytes(data.copy());
    }

    /** the buffer the 1.20.1 packet held */
    public class_2540 data() {
        return data;
    }
}
