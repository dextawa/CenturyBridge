package top.dext.centurybridge.step.v26_1.rt;

import com.mojang.serialization.DataResult;
import org.apache.commons.lang3.math.Fraction;

/**
 * 1.21.11's BundleContents.getWeight(stack) answered a Fraction: a nested bundle's weight plus 1/16, 1 for an item
 * holding bees, else 1 / max stack size (javap class_9276.method_57423). 26.1's getWeight(ItemInstance) is the same
 * body answering a DataResult: success of the same value, except that a nested bundle whose own contents exceed the
 * total weight answers its error ("Excessive total bundle weight", BundleContents.computeContentWeight), which 26.1's
 * callers treat as "does not fit". 1.21.11 had no such state; the error is answered as a full weight (1), which no
 * bundle accepts either.
 */
public final class CbWallBundleWeights {
    private CbWallBundleWeights() {
    }

    /** 26.1's answer as 1.21.11's */
    public static Fraction toLegacy(DataResult<Fraction> weight) {
        return weight == null ? null : weight.result().orElse(Fraction.ONE);
    }

    /** 1.21.11's answer as 26.1's */
    public static DataResult<Fraction> toResult(Fraction weight) {
        return weight == null ? null : DataResult.success(weight);
    }
}
