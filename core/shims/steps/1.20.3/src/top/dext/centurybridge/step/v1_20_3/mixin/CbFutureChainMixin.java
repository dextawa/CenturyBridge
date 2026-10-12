package top.dext.centurybridge.step.v1_20_3.mixin;

import java.util.concurrent.CancellationException;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionException;
import java.util.concurrent.Executor;
import org.slf4j.Logger;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import top.dext.centurybridge.step.v1_20_3.facade.class_7620$class_7621;
import top.dext.centurybridge.step.v1_20_3.rt.CbTasks;

/**
 * FutureChain.append(DelayedTask), 1.20.2's body on 1.20.3's chain: after the chain's head, the task is
 * submitted on the chain's executor -- which 1.20.2 wrapped to run nothing once the chain is closed -- and
 * the chain waits for its future; a failed link is logged and the chain goes on, a cancellation ends it.
 */
@Mixin(net.minecraft.class_7619.class)
public abstract class CbFutureChainMixin implements CbTasks.CbLegacyTaskChainer {

    @Shadow
    @Final
    private static Logger field_39828;

    /** head */
    @Shadow
    private CompletableFuture<?> field_39829;

    /** executor */
    @Shadow
    @Final
    private Executor field_39830;

    /** closed */
    @Shadow
    private volatile boolean field_40112;

    @Override
    public void append(class_7620$class_7621 task) {
        Executor checked = runnable -> {
            if (!field_40112) {
                field_39830.execute(runnable);
            }
        };
        field_39829 = field_39829.thenComposeAsync(ignored -> task.submit(checked), checked).exceptionally(t -> {
            Throwable cause = t instanceof CompletionException completion ? completion.getCause() : t;
            if (cause instanceof CancellationException cancellation) {
                throw cancellation;
            }
            field_39828.error("Chain link failed, continuing to next one", cause);
            return null;
        });
    }
}
