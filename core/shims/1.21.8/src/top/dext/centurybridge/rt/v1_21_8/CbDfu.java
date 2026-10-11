package top.dext.centurybridge.rt.v1_21_8;

import com.mojang.datafixers.util.Either;
import com.mojang.serialization.DataResult;
import java.util.Optional;
import java.util.function.Consumer;
import top.dext.centurybridge.facade.v1_21_8.DfuPartialResult;

/**
 * DFU 6 (1.20.1) members the runtime's DFU no longer has, with DFU 6.0.8's semantics: the
 * converter routes a 1.20.1 mod's calls here (instance/static redirects). DataResult is a
 * sealed interface since DFU 7; its error side is DataResult.Error (message supplier,
 * partial value), which carries everything PartialResult did.
 */
public final class CbDfu {

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
        return result.error().map(CbDfu::partial);
    }

    @SuppressWarnings("unchecked")
    private static DfuPartialResult<Object> partial(DataResult.Error<?> error) {
        return new DfuPartialResult<>(error.messageSupplier(), (Optional<Object>) error.partialValue());
    }

    private CbDfu() {
    }
}
