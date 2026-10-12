package top.dext.centurybridge.step.v1_20_3.facade;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;

/**
 * Facade for 1.20.2's net/minecraft/class_7620$class_7621 (TaskChainer.DelayedTask), gone in 1.20.3: a task
 * a chain submits, with the chain's executor, when its turn comes; the chain goes on when the future it
 * answers completes. {@link top.dext.centurybridge.step.v1_20_3.rt.CbTasks} runs it on 1.20.3's chainers.
 */
@FunctionalInterface
public interface class_7620$class_7621 {

    CompletableFuture<?> submit(Executor executor);
}
