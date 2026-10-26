package top.dext.centurybridge.step.v1_21_9.rt;

import io.netty.buffer.ByteBuf;
import net.minecraft.class_2547;
import net.minecraft.class_2596;
import net.minecraft.class_2598;
import net.minecraft.class_2960;
import net.minecraft.class_9145;

/**
 * An already encoded packet on its way to the wire (Fabric API 0.136's PassthroughPacket): the splitter encodes a large
 * payload packet to measure it; one that fits goes on as these bytes, which the encoder writes as they are
 * (CbLargePayloadMixins' encoder hook) instead of encoding the packet a second time.
 */
public record CbPassthroughPacket(ByteBuf buf) implements class_2596<class_2547> {
    private static final class_9145<CbPassthroughPacket> FAKE_TYPE =
        new class_9145<>(class_2598.field_11941, class_2960.method_60655("fabric-networking-api-v1", "passthrough"));

    @Override
    public class_9145<? extends class_2596<class_2547>> method_65080() {
        return FAKE_TYPE;
    }

    @Override
    public void method_65081(class_2547 listener) {
        throw new UnsupportedOperationException("This is not a real packet!");
    }
}
