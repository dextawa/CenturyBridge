package top.dext.centurybridge.step.v1_21_9.rt;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;
import net.minecraft.class_3300;
import net.minecraft.class_3302;
import net.minecraft.class_4013;
import net.minecraft.class_4080;

/**
 * 1.21.9's PreparableReloadListener.reload(SharedState, backgroundExecutor, barrier, gameExecutor) for 1.21.8's
 * reload(barrier, resourceManager, backgroundExecutor, gameExecutor).
 *
 * 1.21.9's reload hands every listener one SharedState: the resource manager plus values listeners publish for
 * each other, each listener first given prepareSharedState. A 1.21.8 call reloads one listener with a manager: it
 * gets a state of that manager, prepared by the listener itself (what 1.21.9's SimpleReloadInstance does for each
 * listener before any reload).
 *
 * A 1.21.8 listener (its own reload, or a lambda of the old shape) is reached from 1.21.9's shape with the state's
 * resource manager -- all a 1.21.8 listener had -- and the same executors and barrier.
 */
public final class CbReload {

    private CbReload() {
    }

    // ------------------------------------------------------------------ 1.21.8 calls

    public static CompletableFuture<Void> method_25931(class_3302 listener, class_3302.class_4045 barrier, class_3300 manager,
            Executor background, Executor game) {
        class_3302.class_11558 state = new class_3302.class_11558(manager);
        listener.prepareSharedState(state);
        return listener.method_25931(state, background, barrier, game);
    }

    public static CompletableFuture<Void> method_25931(class_4013 listener, class_3302.class_4045 barrier, class_3300 manager,
            Executor background, Executor game) {
        return method_25931((class_3302) listener, barrier, manager, background, game);
    }

    @SuppressWarnings("rawtypes")
    public static CompletableFuture<Void> method_25931(class_4080 listener, class_3302.class_4045 barrier, class_3300 manager,
            Executor background, Executor game) {
        return method_25931((class_3302) listener, barrier, manager, background, game);
    }

    // ------------------------------------------------------------------ 1.21.8 listeners

    /** a 1.21.8 listener's own reload */
    public interface LegacyReload {
        CompletableFuture<Void> method_25931(class_3302.class_4045 barrier, class_3300 manager, Executor background, Executor game);
    }

    /** the override hook's bridge: 1.21.9's shape onto the old reload */
    public static CompletableFuture<Void> cbReload(LegacyReload self, class_3302.class_11558 state, Executor background,
            class_3302.class_4045 barrier, Executor game) {
        return self.method_25931(barrier, state.method_72361(), background, game);
    }

    /** a 1.21.8 lambda / method reference of PreparableReloadListener (lambdaAdapters) */
    public static class_3302 adapt(LegacyReload fn) {
        return new Lambda(fn);
    }

    static final class Lambda implements class_3302 {
        private final LegacyReload fn;

        Lambda(LegacyReload fn) {
            this.fn = fn;
        }

        @Override
        public CompletableFuture<Void> method_25931(class_3302.class_11558 state, Executor background, class_3302.class_4045 barrier,
                Executor game) {
            return fn.method_25931(barrier, state.method_72361(), background, game);
        }

        @Override
        public String method_22322() {
            return fn.getClass().getSimpleName();
        }
    }
}
