package top.dext.centurybridge.step.v1_20_5.rt;

import com.mojang.datafixers.util.Either;
import com.mojang.datafixers.util.Pair;
import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.Decoder;
import com.mojang.serialization.DynamicOps;
import com.mojang.serialization.Encoder;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.MapDecoder;
import com.mojang.serialization.MapEncoder;
import com.mojang.serialization.MapLike;
import com.mojang.serialization.RecordBuilder;
import com.mojang.serialization.codecs.KeyDispatchCodec;
import com.mojang.serialization.codecs.ListCodec;
import com.mojang.serialization.codecs.OptionalFieldCodec;
import java.util.Optional;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.stream.Stream;
import top.dext.centurybridge.step.v1_20_5.facade.DfuPartialResult;

/**
 * DFU 6.0.8 (1.20.4) members that DFU 7.0.14 (1.20.5) changed or removed, with DFU 6.0.8's semantics (read off its
 * bytecode): the library front of this step (Main lib-diff of the two releases' libraries).
 * <ul>
 * <li>DataResult became a sealed interface (the wiring lists it "interfaceized": every call site's opcode follows);
 * get(), getOrThrow(allowPartial, onError) and DataResult.PartialResult went; error() answers DataResult.Error
 * (message supplier, partial value), which carries everything PartialResult did (facade DfuPartialResult).</li>
 * <li>OptionalFieldCodec(name, codec) and Codec.optionalField(name, codec): DFU 6's optional field was lenient (a value
 * that fails to parse reads as absent); DFU 7 takes that as a flag.</li>
 * <li>ListCodec(codec): DFU 7 adds size bounds; DFU 6's list had none (0 .. Integer.MAX_VALUE, as DFU 7's Codec.list).</li>
 * <li>KeyDispatchCodec: DFU 6 dispatched to plain Decoders / Encoders (unsafe: the whole map handed to them -- assumeMap;
 * the public constructor: a MapCodecCodec's map codec, else the value under "value"); DFU 7 dispatches to MapDecoders /
 * MapEncoders, so the plain ones are wrapped to do what DFU 6 did with them.</li>
 * </ul>
 */
public final class CbFinDfu {

    private CbFinDfu() {
    }

    // ---- DataResult

    /** DataResult.getOrThrow(allowPartial, onError): the error goes to onError first, then the partial (if allowed and present) or a RuntimeException */
    public static Object getOrThrow(DataResult<?> result, boolean allowPartial, Consumer<String> onError) {
        Optional<? extends DataResult.Error<?>> error = result.error();
        if (error.isEmpty()) {
            return result.getOrThrow();
        }
        String message = error.get().message();
        onError.accept(message);
        if (allowPartial && error.get().partialValue().isPresent()) {
            return error.get().partialValue().get();
        }
        throw new RuntimeException(message);
    }

    /** DataResult.get(): the value on the left, or the error as a PartialResult on the right */
    public static Either<Object, DfuPartialResult<Object>> get(DataResult<?> result) {
        Optional<? extends DataResult.Error<?>> error = result.error();
        if (error.isEmpty()) {
            return Either.left(result.getOrThrow());
        }
        return Either.right(partial(error.get()));
    }

    /** DataResult.error(): the error side as DFU 6 typed it */
    public static Optional<DfuPartialResult<Object>> error(DataResult<?> result) {
        return result.error().map(CbFinDfu::partial);
    }

    @SuppressWarnings("unchecked")
    private static DfuPartialResult<Object> partial(DataResult.Error<?> error) {
        return new DfuPartialResult<>(error.messageSupplier(), (Optional<Object>) error.partialValue());
    }

    // ---- optional fields, lists

    /** new OptionalFieldCodec(name, codec): DFU 6's, lenient */
    public static <A> OptionalFieldCodec<A> optionalFieldCodec(String name, Codec<A> codec) {
        return new OptionalFieldCodec<>(name, codec, true);
    }

    /** the leniency a DFU 6 OptionalFieldCodec subclass's super(name, codec) passes */
    public static boolean lenient() {
        return true;
    }

    /** Codec.optionalField(name, codec): DFU 6's, lenient */
    public static <F> MapCodec<Optional<F>> optionalField(String name, Codec<F> codec) {
        return Codec.optionalField(name, codec, true);
    }

    /** new ListCodec(codec): DFU 6's list had no size bounds */
    public static <A> ListCodec<A> listCodec(Codec<A> codec) {
        return new ListCodec<>(codec, 0, Integer.MAX_VALUE);
    }

    /** the lower bound a DFU 6 ListCodec subclass's super(codec) passes */
    public static int noMinimum() {
        return 0;
    }

    /** the upper bound a DFU 6 ListCodec subclass's super(codec) passes */
    public static int noMaximum() {
        return Integer.MAX_VALUE;
    }

    // ---- KeyDispatchCodec

    /** KeyDispatchCodec.unsafe(typeKey, keyCodec, type, decoder, encoder): DFU 6's, the whole map handed to the dispatched coders */
    @SuppressWarnings({"rawtypes", "unchecked"})
    public static KeyDispatchCodec unsafe(String typeKey, Codec keyCodec, Function type, Function decoder, Function encoder) {
        return new Unsafe(typeKey, keyCodec, type, wholeMapDecoders(decoder), wholeMapEncoders(encoder));
    }

    /**
     * the codec function of DFU 6's public KeyDispatchCodec(typeKey, keyCodec, type, codec): a key's codec answers a
     * map codec -- a MapCodecCodec's own map codec, else the codec under "value" (DFU 6 decoded and encoded a plain
     * codec's value under "value")
     */
    @SuppressWarnings({"rawtypes", "unchecked"})
    public static Function valueCodecs(Function codec) {
        if (codec == null) {
            return null;
        }
        return key -> ((DataResult) codec.apply(key)).map(c -> c instanceof MapCodec.MapCodecCodec m ? m.codec()
            : c instanceof MapCodec mc ? mc : ((Codec) c).fieldOf("value"));
    }

    /** new KeyDispatchCodec(typeKey, keyCodec, type, codec): DFU 6's codec function, its plain codecs read under "value" */
    @SuppressWarnings({"rawtypes", "unchecked"})
    public static KeyDispatchCodec keyDispatch(String typeKey, Codec keyCodec, Function type, Function codec) {
        return new KeyDispatchCodec(typeKey, keyCodec, type, valueCodecs(codec));
    }

    @SuppressWarnings({"rawtypes", "unchecked"})
    private static Function wholeMapDecoders(Function decoder) {
        return key -> ((DataResult) decoder.apply(key)).map(d -> d instanceof MapCodec.MapCodecCodec m ? m.codec()
            : d instanceof MapDecoder md ? md : new WholeMapDecoder((Decoder) d));
    }

    @SuppressWarnings({"rawtypes", "unchecked"})
    private static Function wholeMapEncoders(Function encoder) {
        return value -> ((DataResult) encoder.apply(value)).map(e -> e instanceof MapCodec.MapCodecCodec m ? m.codec()
            : e instanceof MapEncoder me ? me : new WholeMapEncoder((Encoder) e));
    }

    /** DFU 6 assumeMap decoding: the decoder reads the whole map */
    private static final class WholeMapDecoder<V> extends MapDecoder.Implementation<V> {
        private final Decoder<V> decoder;

        WholeMapDecoder(Decoder<V> decoder) {
            this.decoder = decoder;
        }

        @Override
        public <T> DataResult<V> decode(DynamicOps<T> ops, MapLike<T> input) {
            return this.decoder.decode(ops, ops.createMap(input.entries())).map(Pair::getFirst);
        }

        @Override
        public <T> Stream<T> keys(DynamicOps<T> ops) {
            return Stream.empty();
        }
    }

    /** DFU 6 assumeMap encoding: the encoder's map result merged into the record */
    private static final class WholeMapEncoder<V> extends MapEncoder.Implementation<V> {
        private final Encoder<V> encoder;

        WholeMapEncoder(Encoder<V> encoder) {
            this.encoder = encoder;
        }

        @Override
        public <T> RecordBuilder<T> encode(V input, DynamicOps<T> ops, RecordBuilder<T> prefix) {
            DataResult<T> encoded = this.encoder.encodeStart(ops, input);
            DataResult<MapLike<T>> map = encoded.flatMap(ops::getMap);
            return map.result().map(m -> {
                m.entries().forEach(p -> prefix.add(p.getFirst(), p.getSecond()));
                return prefix;
            }).orElseGet(() -> prefix.withErrorsFrom(map));
        }

        @Override
        public <T> Stream<T> keys(DynamicOps<T> ops) {
            return Stream.empty();
        }
    }

    @SuppressWarnings({"rawtypes", "unchecked"})
    private static final class Unsafe extends KeyDispatchCodec {
        Unsafe(String typeKey, Codec keyCodec, Function type, Function decoder, Function encoder) {
            super(typeKey, keyCodec, type, decoder, encoder);
        }
    }
}
