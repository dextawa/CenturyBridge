package top.dext.centurybridge.step.v1_21_2.facade;

import com.google.common.collect.ImmutableList;
import com.mojang.logging.LogUtils;
import it.unimi.dsi.fastutil.ints.Int2BooleanFunction;
import java.util.List;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.Executor;
import java.util.concurrent.RejectedExecutionException;
import java.util.concurrent.atomic.AtomicInteger;
import org.slf4j.Logger;
import top.dext.centurybridge.step.v1_21_2.rt.CbRestServer;

/**
 * 1.21.1's ProcessorMailbox (net/minecraft/class_3846, classRenames): a queue drained on an executor, one
 * task at a time, with 1.21.1's status bits and scheduling. 1.21.2's successor is ConsecutiveExecutor,
 * which takes only Runnables. 1.21.1's body; the mailbox registers in the metrics registry and charts its
 * queue size as 1.21.1 did, under 1.21.2's category for consecutive executors (MAIL_BOXES is gone).
 */
public class ProcessorMailbox<T> implements net.minecraft.class_5952, ProcessorHandle<T>, AutoCloseable, Runnable {
    private static final Logger LOGGER = LogUtils.getLogger();
    private static final int CLOSED_BIT = 1;
    private static final int SCHEDULED_BIT = 2;
    private final AtomicInteger status = new AtomicInteger(0);
    private final StrictQueue<? super T, ? extends Runnable> queue;
    private final Executor dispatcher;
    private final String name;

    /** create(executor, name) */
    public static ProcessorMailbox<Runnable> method_16902(Executor executor, String name) {
        return new ProcessorMailbox<>(new StrictQueue.QueueStrictQueue<>(new ConcurrentLinkedQueue<>()), executor, name);
    }

    public ProcessorMailbox(StrictQueue<? super T, ? extends Runnable> queue, Executor dispatcher, String name) {
        this.dispatcher = dispatcher;
        this.queue = queue;
        this.name = name;
        net.minecraft.class_5950.field_29555.method_34702(this);
    }

    private boolean setAsScheduled() {
        int s;
        do {
            s = this.status.get();
            if ((s & (CLOSED_BIT | SCHEDULED_BIT)) != 0) {
                return false;
            }
        } while (!this.status.compareAndSet(s, s | SCHEDULED_BIT));
        return true;
    }

    private void setAsIdle() {
        int s;
        do {
            s = this.status.get();
        } while (!this.status.compareAndSet(s, s & ~SCHEDULED_BIT));
    }

    private boolean canBeScheduled() {
        return (this.status.get() & CLOSED_BIT) == 0 && !this.queue.method_16911();
    }

    @Override
    public void close() {
        int s;
        do {
            s = this.status.get();
        } while (!this.status.compareAndSet(s, s | CLOSED_BIT));
    }

    private boolean shouldProcess() {
        return (this.status.get() & SCHEDULED_BIT) != 0;
    }

    private boolean pollTask() {
        if (!this.shouldProcess()) {
            return false;
        }
        Runnable task = this.queue.method_16909();
        if (task == null) {
            return false;
        }
        CbRestServer.wrapThreadWithTaskName(this.name, task).run();
        return true;
    }

    @Override
    public void run() {
        try {
            this.pollUntil(i -> i == 0);
        } finally {
            this.setAsIdle();
            this.registerForExecution();
        }
    }

    /** runAll() */
    public void method_37477() {
        try {
            this.pollUntil(i -> true);
        } finally {
            this.setAsIdle();
            this.registerForExecution();
        }
    }

    /** tell(message) */
    @SuppressWarnings("unchecked")
    @Override
    public void method_16901(T message) {
        ((StrictQueue<T, ?>) this.queue).method_16910(message);
        this.registerForExecution();
    }

    private void registerForExecution() {
        if (this.canBeScheduled() && this.setAsScheduled()) {
            try {
                this.dispatcher.execute(this);
            } catch (RejectedExecutionException e) {
                try {
                    this.dispatcher.execute(this);
                } catch (RejectedExecutionException e2) {
                    LOGGER.error("Cound not schedule mailbox", e2);
                }
            }
        }
    }

    private int pollUntil(Int2BooleanFunction condition) {
        int i = 0;
        while (condition.get(i) && this.pollTask()) {
            i++;
        }
        return i;
    }

    /** size() */
    public int method_34996() {
        return this.queue.method_34706();
    }

    /** hasWork() */
    public boolean method_40001() {
        return this.shouldProcess() && !this.queue.method_16911();
    }

    @Override
    public String toString() {
        return this.name + " " + this.status.get() + " " + this.queue.method_16911();
    }

    /** name() */
    @Override
    public String method_16898() {
        return this.name;
    }

    /** profiledMetrics() */
    @Override
    public List<net.minecraft.class_5965> method_34705() {
        return ImmutableList.of(net.minecraft.class_5965.method_34776(this.name + "-queue-size", CbRestServer.field_29552(), this::method_34996));
    }
}
