package top.dext.centurybridge.step.v1_20_3.rt;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;
import java.util.function.Consumer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import top.dext.centurybridge.step.v1_20_3.facade.class_7620$class_7621;

/**
 * 1.20.2's TaskChainer on 1.20.3. 1.20.2 chained DelayedTasks -- append(task): when the chain reaches the
 * task it submits it with the chain's executor and waits for the future it answers; 1.20.3 chains
 * (preparation, handler) pairs instead and dropped DelayedTask. FutureChain keeps 1.20.2's append (its
 * mixin), immediate(executor) chainers made by 1.20.2 code answer both, and a 1.20.2 chainer of a mod's own
 * (or a lambda) gets 1.20.3's append from its own.
 */
public final class CbTasks {

    private static final Logger LOGGER = LoggerFactory.getLogger("TaskChainer");

    /** a chainer that takes 1.20.2's append(DelayedTask): a mod's own, FutureChain (its mixin), the immediate one below */
    @FunctionalInterface
    public interface CbLegacyTaskChainer {
        void append(class_7620$class_7621 task);
    }

    /**
     * TaskChainer.immediate(executor) called by 1.20.2 code: 1.20.3's immediate chainer (each preparation's
     * handler on the executor as it completes), which also takes 1.20.2's append -- the task submitted at
     * once with the executor, as 1.20.2's immediate chainer did
     */
    public static net.minecraft.class_7620 immediate(Executor executor) {
        return new Immediate(executor);
    }

    private static final class Immediate implements net.minecraft.class_7620, CbLegacyTaskChainer {
        private final Executor executor;

        Immediate(Executor executor) {
            this.executor = executor;
        }

        @Override
        public <T> void append(CompletableFuture<T> preparation, Consumer<T> handler) {
            preparation.thenAcceptAsync(handler, executor).exceptionally(t -> {
                LOGGER.error("Task failed", t);
                return null;
            });
        }

        @Override
        public void append(class_7620$class_7621 task) {
            task.submit(executor).exceptionally(t -> {
                LOGGER.error("Task failed", t);
                return null;
            });
        }
    }

    /**
     * was TaskChainer.append(DelayedTask): the chainer's 1.20.2 append where it has one; on a chainer 1.20.3
     * code made (TaskChainer.immediate, the only other kind) the task is submitted at once with an executor
     * that runs through the chainer -- on the chainer's executor, as the 1.20.2 immediate chainer ran it
     */
    public static void append(net.minecraft.class_7620 chainer, class_7620$class_7621 task) {
        if (chainer instanceof CbLegacyTaskChainer legacy) {
            legacy.append(task);
            return;
        }
        task.submit(chainer::append).exceptionally(t -> {
            LOGGER.error("Task failed", t);
            return null;
        });
    }

    /**
     * 1.20.3's append(preparation, handler) on a 1.20.2 chainer (adapterHooks): one DelayedTask that, when
     * the chain reaches it, hands the preparation's result to the handler on the chain's executor
     */
    public static <T> void cbAppend(CbLegacyTaskChainer chainer, CompletableFuture<T> preparation, Consumer<T> handler) {
        chainer.append(executor -> preparation.thenAcceptAsync(handler, executor));
    }

    /** a 1.20.2 TaskChainer lambda as the 1.20.3 interface (it takes 1.20.2's append too) */
    public static net.minecraft.class_7620 cbTaskChainer(CbLegacyTaskChainer legacy) {
        return new Adapted(legacy);
    }

    private static final class Adapted implements net.minecraft.class_7620, CbLegacyTaskChainer {
        private final CbLegacyTaskChainer legacy;

        Adapted(CbLegacyTaskChainer legacy) {
            this.legacy = legacy;
        }

        @Override
        public <T> void append(CompletableFuture<T> preparation, Consumer<T> handler) {
            cbAppend(legacy, preparation, handler);
        }

        @Override
        public void append(class_7620$class_7621 task) {
            legacy.append(task);
        }
    }

    private CbTasks() {
    }
}
