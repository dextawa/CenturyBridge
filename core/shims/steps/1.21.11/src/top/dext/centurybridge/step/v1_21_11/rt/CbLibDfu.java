package top.dext.centurybridge.step.v1_21_11.rt;

import com.mojang.datafixers.schemas.Schema;
import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.KeyDispatchCodec;
import it.unimi.dsi.fastutil.objects.Object2IntMap;
import java.lang.invoke.MethodHandles;
import java.lang.invoke.VarHandle;
import java.util.function.Function;
import java.util.function.Supplier;

/**
 * DFU 8.0.16 (1.21.10) -> 9.0.19 (1.21.11): the library front of this step (core/segments/damage-lib/1.21.10__1.21.11.tsv),
 * 8.0.16's semantics read off its bytecode.
 * <ul>
 * <li>Codec.unit(value) / unit(supplier) went; 8.0.16's were MapCodec.unit(supplier).codec() (the value one through a
 * supplier answering it), which 9.0.19 still has.</li>
 * <li>KeyDispatchCodec's constructors took the type key and its codec separately; 9.0.19's take the key's MapCodec --
 * the codec read from and written to that field: keyCodec.fieldOf(typeKey).</li>
 * <li>Schema's protected parent and RECURSIVE_TYPES fields became private (recursiveTypes); a 8.0.16 schema subclass read
 * them: the same objects, read through a private lookup (DFU is an unnamed-module library on the game class path).</li>
 * </ul>
 */
public final class CbLibDfu {
    private CbLibDfu() {
    }

    private static final VarHandle PARENT;
    private static final VarHandle RECURSIVE_TYPES;

    static {
        try {
            MethodHandles.Lookup lookup = MethodHandles.privateLookupIn(Schema.class, MethodHandles.lookup());
            PARENT = lookup.findVarHandle(Schema.class, "parent", Schema.class);
            RECURSIVE_TYPES = lookup.findVarHandle(Schema.class, "recursiveTypes", Object2IntMap.class);
        } catch (ReflectiveOperationException e) {
            throw new ExceptionInInitializerError(e);
        }
    }

    public static <A> Codec<A> unit(A value) {
        return unit(() -> value);
    }

    public static <A> Codec<A> unit(Supplier<A> value) {
        return MapCodec.unit(value).codec();
    }

    /** the type key's MapCodec of 8.0.16's (typeKey, keyCodec) constructor arguments */
    public static <K> MapCodec<K> keyField(String typeKey, Codec<K> keyCodec) {
        return keyCodec.fieldOf(typeKey);
    }

    /** new KeyDispatchCodec(typeKey, keyCodec, type, codec) */
    public static <K, V> KeyDispatchCodec<K, V> keyDispatch(String typeKey, Codec<K> keyCodec,
            Function<? super V, ? extends DataResult<? extends K>> type,
            Function<? super K, ? extends DataResult<? extends MapCodec<? extends V>>> codec) {
        return new KeyDispatchCodec<>(keyField(typeKey, keyCodec), type, codec);
    }

    /** Schema.parent (a subclass's read) */
    public static Schema parent(Schema schema) {
        return (Schema) PARENT.get(schema);
    }

    /** Schema.RECURSIVE_TYPES (a subclass's read) */
    @SuppressWarnings("unchecked")
    public static Object2IntMap<String> RECURSIVE_TYPES(Schema schema) {
        return (Object2IntMap<String>) RECURSIVE_TYPES.get(schema);
    }
}
