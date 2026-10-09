package top.dext.centurybridge.facade.v1_21_4;

import java.util.Optional;
import java.util.function.Function;
import java.util.function.Supplier;

/**
 * DFU 6's DataResult.PartialResult (gone in DFU 7+, replaced by the
 * DataResult.Error record). Old mods hold it from DataResult.get()'s Either
 * and DataResult.error(); the converter renames the class here and the
 * CbLibs redirects produce instances of it.
 */
public final class CbPartialResult<R> {
    private final Supplier<String> message;
    private final Optional<R> partialResult;

    public CbPartialResult(Supplier<String> message, Optional<R> partialResult) {
        this.message = message;
        this.partialResult = partialResult;
    }

    public <R2> CbPartialResult<R2> map(Function<? super R, ? extends R2> function) {
        return new CbPartialResult<>(message, partialResult.map(function));
    }

    public <R2> CbPartialResult<R2> flatMap(Function<R, CbPartialResult<R2>> function) {
        if (partialResult.isPresent()) {
            CbPartialResult<R2> result = function.apply(partialResult.get());
            return new CbPartialResult<>(() -> appendMessages(message.get(), result.message()), result.partialResult);
        }
        return new CbPartialResult<>(message, Optional.empty());
    }

    public String message() {
        return message.get();
    }

    public Optional<R> partialResult() {
        return partialResult;
    }

    private static String appendMessages(String first, String second) {
        return first + "; " + second;
    }

    @Override
    public String toString() {
        return "DynamicException[" + message() + ' ' + partialResult + ']';
    }
}
