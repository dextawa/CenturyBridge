package top.dext.centurybridge.step.v1_20_5.rt;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import java.util.IdentityHashMap;
import java.util.Map;

/**
 * 1.20.4's codecs for 1.20.5's map codecs.
 *
 * 1.20.5 (DataFixerUpper 7) turned every dispatched type's codec into a {@link MapCodec}: a
 * FloatProviderType, a FeatureSizeType, a ChunkGenerator, a loot function type... answers
 * {@code MapCodec} where 1.20.4 (DFU 6) answered {@code Codec}, and {@code Codec.dispatch} takes
 * {@code Function<K, MapCodec<V>>}. The two releases encode a dispatched value the same way, which
 * is what makes the conversion exact. DFU 6's {@code KeyDispatchCodec} (6.0.8, the 1.20.4 runtime)
 * treats the element codec it is handed in one of two ways (decode: {@code lambda$decode$3},
 * encode: {@code encode}, for maps that are not compressed):
 * <ul>
 * <li>a {@link MapCodec.MapCodecCodec} (what {@code RecordCodecBuilder.create},
 *     {@code Codec.unit} and {@code MapCodec.codec()} build) is inlined: its map codec reads and
 *     writes the same map that carries the "type" key;</li>
 * <li>any other codec reads and writes its value under the "value" key of that map.</li>
 * </ul>
 * DFU 7's {@code KeyDispatchCodec} (7.0.14, the 1.20.5 runtime) only ever inlines a map codec. So
 * the map codec that does exactly what DFU 6 did with a 1.20.4 codec is the wrapped map codec of a
 * {@code MapCodecCodec}, and {@code codec.fieldOf("value")} for any other codec.
 *
 * Both directions answer the same object for the same input (identity, not just equality), and
 * undo each other: dispatch looks a type's codec up in a registry by identity, so a codec a mod
 * registered and the codec its type answers later have to stay one object across the conversion,
 * and a 1.20.4 caller asking for a codec it handed over gets that codec back.
 */
public final class CbCodecs {

    /** guards both tables */
    private static final Object LOCK = new Object();
    /** 1.20.4 codec -> the 1.20.5 map codec standing for it */
    private static final Map<Codec<?>, MapCodec<?>> MAP_CODECS = new IdentityHashMap<>();
    /** 1.20.5 map codec -> the 1.20.4 codec standing for it */
    private static final Map<MapCodec<?>, Codec<?>> CODECS = new IdentityHashMap<>();

    /** the map codec 1.20.5 dispatches with for a codec 1.20.4 dispatched with (see the class comment) */
    @SuppressWarnings("unchecked")
    public static <A> MapCodec<A> toMapCodec(Codec<A> codec) {
        if (codec == null) {
            return null;
        }
        synchronized (LOCK) {
            MapCodec<?> known = MAP_CODECS.get(codec);
            if (known == null) {
                known = codec instanceof MapCodec.MapCodecCodec<A> inline ? inline.codec() : codec.fieldOf("value");
                MAP_CODECS.put(codec, known);
                CODECS.putIfAbsent(known, codec);
            }
            return (MapCodec<A>) known;
        }
    }

    /**
     * The codec 1.20.4 held where 1.20.5 holds a map codec: the codec the map codec was made from
     * when it came through {@link #toMapCodec}, otherwise {@code mapCodec.codec()} -- the
     * {@code MapCodecCodec} 1.20.4's {@code RecordCodecBuilder.create} / {@code Codec.unit} answered
     * for the same fields -- made once per map codec.
     */
    @SuppressWarnings("unchecked")
    public static <A> Codec<A> fromMapCodec(MapCodec<A> mapCodec) {
        if (mapCodec == null) {
            return null;
        }
        synchronized (LOCK) {
            Codec<?> known = CODECS.get(mapCodec);
            if (known == null) {
                known = mapCodec.codec();
                CODECS.put(mapCodec, known);
                MAP_CODECS.putIfAbsent(known, mapCodec);
            }
            return (Codec<A>) known;
        }
    }

    private CbCodecs() {
    }
}
