package top.dext.centurybridge.step.v1_20_3.rt;

import com.google.common.collect.Multimap;
import java.util.function.Supplier;
import net.minecraft.class_8942;

/**
 * Loot validation: 1.20.2's ValidationContext kept its own problems (a multimap keyed by context path, read back
 * with getProblems()); 1.20.3's reports them to a ProblemReporter it is given. Wired from s2world.json; the
 * mixin side is CbWorldValidationContextMixin.
 */
public final class CbWorldLoot {

    /**
     * ValidationContext(params, resolver) of 1.20.2 started an empty problem collection of its own: 1.20.3's
     * ProblemReporter.Collector, which its getProblems() reads back.
     */
    public static class_8942 newCollector() {
        return new class_8942.class_8943();
    }

    /** ValidationContext(problems, context, ..) of 1.20.2: a reporter into that multimap under that context path */
    public static class_8942 problems(Multimap<String, String> problems, Supplier<String> context) {
        return new CbWorldProblems(problems, context);
    }

    /**
     * 1.20.2's getProblems() of a context reporting to {@code reporter}: what a 1.20.2 constructor's collection
     * (or 1.20.3's own Collector) holds. Another reporter keeps its problems to itself; asking for them is refused.
     */
    public static Multimap<String, String> problemsOf(class_8942 reporter) {
        if (reporter instanceof class_8942.class_8943 collector) {
            return collector.method_54948();
        }
        if (reporter instanceof CbWorldProblems own) {
            return own.snapshot();
        }
        throw new UnsupportedOperationException("ValidationContext.getProblems(): the context reports to a "
            + reporter.getClass().getName() + ", which does not hand its problems back");
    }

    private CbWorldLoot() {
    }
}
