package top.dext.centurybridge.rt.v1_20_2;

import net.minecraft.class_2540;
import net.minecraft.class_8594;

/**
 * 1.20.1 LoginQueryResponseC2SPacket carried (query id, nullable PacketByteBuf); 1.20.2's carries a
 * nullable CustomQueryAnswerPayload. This is the old buffer as such a payload (a null response stays
 * a null payload), written as the 1.20.1 packet wrote it -- the buffer's readable bytes -- and
 * holding the very buffer it was given, which 1.20.1's getResponse() handed back.
 */
public final class CbRawQueryAnswerPayload implements class_8594 {

    private final class_2540 data;

    public CbRawQueryAnswerPayload(class_2540 data) {
        this.data = data;
    }

    @Override
    public void method_52295(class_2540 out) {
        out.writeBytes(data.slice());
    }

    /** the buffer the 1.20.1 packet held */
    public class_2540 data() {
        return data;
    }
}
