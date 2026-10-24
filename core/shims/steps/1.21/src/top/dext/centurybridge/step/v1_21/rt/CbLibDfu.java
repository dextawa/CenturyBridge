package top.dext.centurybridge.step.v1_21.rt;

import com.mojang.datafixers.DSL;
import com.mojang.datafixers.DataFixer;
import com.mojang.datafixers.DataFixerBuilder;
import java.util.Set;
import java.util.concurrent.Executor;

/**
 * DFU 7.0.14 (1.20.6) DataFixerBuilder members that DFU 8.0.16 (1.21) folded into build() -> Result: the library front
 * of this step (core/segments/damage-lib/1.20.6__1.21.tsv), 7.0.14's semantics read off its bytecode.
 * buildUnoptimized() was build() itself; buildOptimized(types, executor) built the fixer, started the same per-schema
 * type optimisation on the executor (failures logged, nothing awaited) and answered the fixer at once -- 8.0.16's
 * Result.optimize(types, executor) runs that loop.
 */
public final class CbLibDfu {
    private CbLibDfu() {
    }

    public static DataFixer buildUnoptimized(DataFixerBuilder builder) {
        return builder.build().fixer();
    }

    public static DataFixer buildOptimized(DataFixerBuilder builder, Set<DSL.TypeReference> types, Executor executor) {
        DataFixerBuilder.Result result = builder.build();
        result.optimize(types, executor);
        return result.fixer();
    }
}
