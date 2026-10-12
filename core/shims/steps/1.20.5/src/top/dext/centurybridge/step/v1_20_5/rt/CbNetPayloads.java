package top.dext.centurybridge.step.v1_20_5.rt;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;
import net.minecraft.class_2540;
import net.minecraft.class_2960;
import net.minecraft.class_8710;
import net.minecraft.class_9139;
import top.dext.centurybridge.step.v1_20_5.mixin.CbNetPayloadCodecAccess;

/**
 * Custom payloads between 1.20.4 and 1.20.5 (area s4net).
 *
 * 1.20.4's CustomPacketPayload wrote itself (write(buf)) under its id (id()); a custom payload packet
 * carried the id and then those bytes. 1.20.5's payload names a Type, and the packet's codec looks the
 * type's codec up by id -- a registered one, or the fallback for ids it does not know (vanilla's
 * DiscardedPayload codec, which writes nothing; Fabric API resolves its registered types there).
 *
 * A 1.20.4 mod's payload class keeps working as written: it gets a type from its id
 * ({@link #cbLegacyPayloadType}), and is encoded the 1.20.4 way, id then write(buf), whatever codec
 * 1.20.5 has for that id (CbNetPayloadCodecBridge) -- a codec registered for the same channel expects
 * its own payload class and would not take the mod's object. Vanilla payload classes have write(buf)
 * and id() back through generated mixins (CbNetPayloadOf*), encoding with their own STREAM_CODEC.
 */
public final class CbNetPayloads {

    /**
     * Packet.type() of a 1.20.4 mod's payload class (abstractFills): the type of its id. The id is the
     * mod's id() -- through CbNetLegacyPayloadId, which the converter adds wherever a class declares it.
     */
    public static class_8710.class_9154<?> cbLegacyPayloadType(class_8710 payload) {
        if (payload instanceof CbNetLegacyPayloadId legacy) {
            return new class_8710.class_9154<>(legacy.comp_1678());
        }
        throw new IllegalStateException("CenturyBridge: " + payload.getClass().getName()
            + " is a 1.20.4 payload class without an id() of its own or from a payload class above it");
    }

    /** whether the payload is a 1.20.4 mod's (it writes itself) */
    public static boolean cbIsLegacy(class_8710 payload) {
        return payload instanceof CbNetLegacyPayload;
    }

    /** was CustomPacketPayload.id(), called through the interface: the id of the payload's type */
    public static class_2960 comp_1678(class_8710 payload) {
        return payload.method_56479().comp_2242();
    }

    // ---------------------------------------------------------------- write through the interface

    /** every payload codec 1.20.5 built (the play phase's both ways, the configuration phase's), as they are made */
    private static final List<Object> DISPATCHERS = new CopyOnWriteArrayList<>();

    /** CbNetPayloadCodecBridge: a payload codec was made (they are the packet classes' static codecs) */
    public static void cbDispatcher(Object dispatcher) {
        DISPATCHERS.add(dispatcher);
    }

    /**
     * was CustomPacketPayload.write(buf), called through the interface: a 1.20.4 mod's payload writes
     * itself; any other is written the way a custom payload packet writes it -- the body its type's
     * codec writes, without the leading id -- by the first payload codec that writes something for
     * its id (one that does not know the id writes nothing for it, as the connection would).
     */
    @SuppressWarnings("unchecked")
    public static void method_53028(class_8710 payload, class_2540 buf) {
        if (payload instanceof CbNetLegacyPayload legacy) {
            legacy.method_53028(buf);
            return;
        }
        class_2960 id = payload.method_56479().comp_2242();
        for (Object dispatcher : DISPATCHERS) {
            class_9139<? super class_2540, ?> codec = ((CbNetPayloadCodecAccess) dispatcher).cb$findCodec(id);
            if (codec == null) {
                continue;
            }
            ByteBuf scratch = Unpooled.buffer();
            try {
                ((class_9139<class_2540, class_8710>) codec).encode(CbNetBufs.cbRegistryBuf(new class_2540(scratch)), payload);
                if (scratch.isReadable()) {
                    buf.writeBytes(scratch);
                    return;
                }
            } finally {
                scratch.release();
            }
        }
    }

    private CbNetPayloads() {
    }
}
