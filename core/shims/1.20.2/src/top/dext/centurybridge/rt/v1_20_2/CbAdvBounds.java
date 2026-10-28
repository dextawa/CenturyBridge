package top.dext.centurybridge.rt.v1_20_2;

import java.util.Optional;

/**
 * MinMaxBounds' 1.20.1 bounds were nullable boxed numbers (null = no bound); 1.20.2 holds the same values as Optionals.
 * The conversions the step's call reshapes use where a 1.20.1 caller (a mod's @Invoker / @Shadow) reaches one of the
 * private statics 1.20.2 kept under the same name with Optional parameters: null is absent, and back.
 */
public final class CbAdvBounds {

    private CbAdvBounds() {
    }

    public static Optional<Double> ofDouble(Double value) {
        return Optional.ofNullable(value);
    }

    public static Optional<Integer> ofInt(Integer value) {
        return Optional.ofNullable(value);
    }

    public static Double doubleOrNull(Optional<Double> value) {
        return value.orElse(null);
    }

    public static Long longOrNull(Optional<Long> value) {
        return value.orElse(null);
    }
}
