package top.dext.centurybridge.step.v1_20_3.rt;

import com.google.common.collect.ImmutableMultimap;
import com.google.common.collect.Multimap;
import java.util.function.Supplier;
import net.minecraft.class_8942;

/**
 * The problem collection of a ValidationContext built through 1.20.2's full constructor
 * ValidationContext(problems, context, params, resolver, visited): 1.20.2's context put every problem into the
 * multimap it was given, keyed by its context path, which it asked of the supplier once and kept; a child added
 * its name to the path. 1.20.3 moved exactly this into ProblemReporter.Collector, whose constructor that takes
 * an existing multimap is private: this is that reporter over the caller's multimap.
 */
final class CbWorldProblems implements class_8942 {

    private final Multimap<String, String> problems;
    private final Supplier<String> context;
    private String contextCache;

    CbWorldProblems(Multimap<String, String> problems, Supplier<String> context) {
        this.problems = problems;
        this.context = context;
    }

    private String context() {
        if (this.contextCache == null) {
            this.contextCache = this.context.get();
        }
        return this.contextCache;
    }

    @Override
    public class_8942 method_54946(String name) {
        return new CbWorldProblems(this.problems, () -> this.context() + name);
    }

    @Override
    public void method_54947(String problem) {
        this.problems.put(this.context(), problem);
    }

    /** 1.20.2's getProblems(): a copy of the problems so far */
    Multimap<String, String> snapshot() {
        return ImmutableMultimap.copyOf(this.problems);
    }
}
