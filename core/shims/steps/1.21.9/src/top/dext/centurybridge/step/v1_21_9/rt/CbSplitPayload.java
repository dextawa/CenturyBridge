package top.dext.centurybridge.step.v1_21_9.rt;

import io.netty.buffer.ByteBuf;
import net.minecraft.class_2960;
import net.minecraft.class_8710;
import net.minecraft.class_9139;

/**
 * Fabric API 0.136's split payload (FabricSplitPacketPayload, channel fabric:split): one slice of an oversized custom
 * payload packet, written and read as its raw bytes. The same id and wire form as 0.136, so a 1.21.9 peer running the
 * layer and a converted mod understands what the other side splits.
 */
public record CbSplitPayload(ByteBuf byteBuf) implements class_8710 {
    public static final class_8710.class_9154<CbSplitPayload> ID = new class_8710.class_9154<>(class_2960.method_60655("fabric", "split"));
    public static final class_9139<ByteBuf, CbSplitPayload> CODEC = class_9139.method_56437(CbSplitPayload::write, CbSplitPayload::read);

    private static CbSplitPayload read(ByteBuf buf) {
        return new CbSplitPayload(buf.readBytes(buf.readableBytes()));
    }

    private static void write(ByteBuf buf, CbSplitPayload payload) {
        buf.writeBytes(payload.byteBuf());
    }

    @Override
    public class_8710.class_9154<? extends class_8710> method_56479() {
        return ID;
    }
}
