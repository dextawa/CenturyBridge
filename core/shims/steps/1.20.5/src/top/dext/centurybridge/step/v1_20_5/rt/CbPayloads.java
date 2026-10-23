package top.dext.centurybridge.step.v1_20_5.rt;

/**
 * Custom payloads 1.20.4 code sends that 1.20.5 has no registered codec for. 1.20.4 wrote any custom
 * payload as its id followed by whatever the payload wrote; 1.20.5 encodes one through the codec its id is
 * registered with (vanilla's own types, then Fabric API's PayloadTypeRegistry) and falls back to an
 * "unknown payload" codec that only handles UnknownCustomPayload -- a ClassCastException on the netty
 * thread for anything else.
 *
 * <p>A payload this layer knows how to write the 1.20.4 way implements {@link Encodable}. The
 * CbPayloadCodecMixin answers, where vanilla would hand out its unknown-payload codec, a codec whose
 * encoder writes an Encodable payload with its own {@link Encodable#cb$write} and passes everything
 * else -- decoding included -- to vanilla's unknown-payload codec unchanged. So nothing is registered
 * from a game thread while netty threads read Fabric API's registry (a plain HashMap), and every
 * payload type 1.20.5 already knows keeps its own codec.
 */
public final class CbPayloads {

    private CbPayloads() {
    }

    /** a custom payload that writes its own 1.20.4 bytes (the id is written before it by vanilla) */
    public interface Encodable extends net.minecraft.class_8710 {
        void cb$write(net.minecraft.class_2540 buf);
    }

    /** vanilla's unknown-payload codec for an id, with Encodable payloads written the 1.20.4 way */
    @SuppressWarnings({"unchecked", "rawtypes"})
    public static net.minecraft.class_9139 fallback(net.minecraft.class_9139 unknown) {
        return new Fallback(unknown);
    }

    @SuppressWarnings({"unchecked", "rawtypes"})
    private record Fallback(net.minecraft.class_9139 unknown) implements net.minecraft.class_9139<net.minecraft.class_2540, Object> {

        @Override
        public Object decode(net.minecraft.class_2540 buf) {
            return unknown.decode(buf);
        }

        @Override
        public void encode(net.minecraft.class_2540 buf, Object payload) {
            if (payload instanceof Encodable e) {
                e.cb$write(buf);
            } else {
                unknown.encode(buf, payload);
            }
        }
    }
}
