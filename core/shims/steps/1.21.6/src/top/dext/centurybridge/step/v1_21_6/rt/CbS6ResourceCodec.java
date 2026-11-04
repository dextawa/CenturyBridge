package top.dext.centurybridge.step.v1_21_6.rt;

import com.mojang.datafixers.util.Pair;
import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.DynamicOps;
import net.minecraft.class_6880;

/** ResourceOrIdArgument's 1.21.5 Codec&lt;Holder&lt;T&gt;&gt; seen as 1.21.6's inline-value Codec&lt;T&gt;. */
public final class CbS6ResourceCodec {
    private CbS6ResourceCodec() {
    }

    /**
     * Adapt the codec, not its generic parameter. 1.21.6 has already split a registry id from an inline value before it
     * calls this codec, so encoding an inline value presents it to the old codec as a direct holder. Decode maps only the
     * successful value: DataResult's error, partial result and lifecycle continue through DFU's own result pipeline.
     */
    public static <T> Codec<T> valueCodec(Codec<class_6880<T>> legacy) {
        if (legacy instanceof ValueView) {
            @SuppressWarnings("unchecked")
            Codec<T> already = (Codec<T>) legacy;
            return already;
        }
        return new ValueView<>(legacy);
    }

    private static final class ValueView<T> implements Codec<T> {
        private final Codec<class_6880<T>> legacy;

        private ValueView(Codec<class_6880<T>> legacy) {
            this.legacy = legacy;
        }

        @Override
        public <O> DataResult<O> encode(T value, DynamicOps<O> ops, O prefix) {
            return legacy.encode(class_6880.method_40223(value), ops, prefix);
        }

        @Override
        public <O> DataResult<Pair<T, O>> decode(DynamicOps<O> ops, O input) {
            return legacy.decode(ops, input).map(pair -> pair.mapFirst(class_6880::comp_349));
        }

        @Override
        public String toString() {
            return "CbS6ResourceCodec[" + legacy + "]";
        }
    }
}
