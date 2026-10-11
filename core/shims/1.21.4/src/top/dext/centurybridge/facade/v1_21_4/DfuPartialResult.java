package top.dext.centurybridge.facade.v1_21_4;

import java.util.Objects;
import java.util.Optional;
import java.util.function.Function;
import java.util.function.Supplier;

/**
 * DFU 6's DataResult.PartialResult -- the error side of a 1.20.1 DataResult (get() handed it
 * out as the right of an Either, error() as an Optional), which DFU 7 replaced with the
 * DataResult.Error record. Same members, same behaviour as DFU 6.0.8 (read off its bytecode):
 * flatMap joins the two messages with "; ", toString is "DynamicException[message partial]".
 */
public class DfuPartialResult<R> {

    private final Supplier<String> message;
    private final Optional<R> partialResult;

    public DfuPartialResult(Supplier<String> message, Optional<R> partialResult) {
        this.message = message;
        this.partialResult = partialResult;
    }

    public <R2> DfuPartialResult<R2> map(Function<? super R, ? extends R2> function) {
        return new DfuPartialResult<>(message, partialResult.map(function));
    }

    public <R2> DfuPartialResult<R2> flatMap(Function<R, DfuPartialResult<R2>> function) {
        if (partialResult.isPresent()) {
            DfuPartialResult<R2> result = function.apply(partialResult.get());
            return new DfuPartialResult<>(() -> message.get() + "; " + result.message.get(), result.partialResult);
        }
        return new DfuPartialResult<>(message, Optional.empty());
    }

    public String message() {
        return message.get();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        DfuPartialResult<?> that = (DfuPartialResult<?>) o;
        return Objects.equals(message, that.message) && Objects.equals(partialResult, that.partialResult);
    }

    @Override
    public int hashCode() {
        return 31 * message.hashCode() + partialResult.hashCode();
    }

    @Override
    public String toString() {
        return "DynamicException[" + message() + " " + partialResult + "]";
    }
}
