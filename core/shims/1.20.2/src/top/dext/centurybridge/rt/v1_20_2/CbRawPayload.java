package top.dext.centurybridge.rt.v1_20_2;

import net.minecraft.class_2540;
import net.minecraft.class_2960;
import net.minecraft.class_8710;

/**
 * 1.20.1 new CustomPayloadC2SPacket(Identifier, PacketByteBuf): 1.20.2 packets
 * carry a CustomPayload object instead. This one is exactly the old pair -- the
 * channel id plus the bytes the mod already encoded -- written verbatim, which
 * is byte-for-byte what the 1.20.1 packet put on the wire.
 */
public final class CbRawPayload implements class_8710 {

    private final class_2960 id;
    private final class_2540 data;

    public CbRawPayload(class_2960 id, class_2540 data) {
        this.id = id;
        this.data = data;
    }

    @Override
    public void method_53028(class_2540 out) {
        if (data != null) {
            out.writeBytes(data.slice());
        }
    }

    @Override
    public class_2960 comp_1678() {
        return id;
    }
}
