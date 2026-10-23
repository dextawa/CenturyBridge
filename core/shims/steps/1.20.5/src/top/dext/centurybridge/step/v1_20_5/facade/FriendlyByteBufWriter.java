package top.dext.centurybridge.step.v1_20_5.facade;

import java.util.Optional;
import java.util.function.BiConsumer;
import net.minecraft.class_2540;

/**
 * FriendlyByteBuf.Writer (1.20.4; class_2540$class_7462): a function writing a value into a buffer.
 * 1.20.5 has StreamEncoder in its place; see {@link FriendlyByteBufReader}.
 */
@FunctionalInterface
public interface FriendlyByteBufWriter<T> extends BiConsumer<class_2540, T> {

    /** was asOptional(): a writer of a boolean and then, when present, the value (FriendlyByteBuf.writeOptional) */
    default FriendlyByteBufWriter<Optional<T>> asOptional() {
        return (buf, value) -> buf.method_37435(value, this::accept);
    }
}
