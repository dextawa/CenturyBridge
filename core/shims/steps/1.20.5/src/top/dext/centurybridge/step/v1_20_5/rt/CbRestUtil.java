package top.dext.centurybridge.step.v1_20_5.rt;

/**
 * 1.20.4 Util members 1.20.5 removed (DFU 7's DataResult answers getOrThrow itself; StringUtil took the
 * whitespace tests), with 1.20.4's bodies.
 */
public final class CbRestUtil {

    /** Util.getOrThrow(result, exception): the value, or the error's message as the exception */
    public static <T, E extends Throwable> T method_47526(com.mojang.serialization.DataResult<T> result,
                                                        java.util.function.Function<String, E> exception) throws E {
        java.util.Optional<com.mojang.serialization.DataResult.Error<T>> error = result.error();
        if (error.isPresent()) {
            throw exception.apply(error.get().message());
        }
        return result.result().orElseThrow();
    }

    /** Util.getPartialOrThrow(result, exception): the value, else a partial one, else the error's message as the exception */
    public static <T, E extends Throwable> T method_55334(com.mojang.serialization.DataResult<T> result,
                                                        java.util.function.Function<String, E> exception) throws E {
        java.util.Optional<com.mojang.serialization.DataResult.Error<T>> error = result.error();
        if (error.isPresent()) {
            java.util.Optional<T> partial = result.resultOrPartial(message -> {
            });
            if (partial.isPresent()) {
                return partial.get();
            }
            throw exception.apply(error.get().message());
        }
        return result.result().orElseThrow();
    }

    /** Util.isWhitespace(codepoint) */
    public static boolean method_51893(int codepoint) {
        return Character.isWhitespace(codepoint) || Character.isSpaceChar(codepoint);
    }

    /** Util.isBlank(string): null, empty, or whitespace only */
    public static boolean method_51894(String string) {
        if (string == null || string.length() == 0) {
            return true;
        }
        return string.chars().allMatch(CbRestUtil::method_51893);
    }

    private CbRestUtil() {
    }
}
