package top.dext.centurybridge.step.v1_20_5.facade;

import java.util.Optional;
import java.util.function.Function;
import net.minecraft.class_2540;

/**
 * FriendlyByteBuf.Reader (1.20.4; class_2540$class_7461): a function reading a value from a buffer.
 * 1.20.5 has StreamDecoder in its place; a 1.20.4 method reference or lambda built for this interface
 * stays one (the converter renames the dead type to this one), and the buffer methods taking it come
 * back with it (CbNetFriendlyByteBufBridge).
 */
@FunctionalInterface
public interface FriendlyByteBufReader<T> extends Function<class_2540, T> {

    /** was asOptional(): a reader of a boolean and then, when it was true, the value (FriendlyByteBuf.readOptional) */
    default FriendlyByteBufReader<Optional<T>> asOptional() {
        return buf -> buf.method_37436(this::apply);
    }
}
