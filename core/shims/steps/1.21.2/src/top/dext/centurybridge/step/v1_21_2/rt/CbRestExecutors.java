package top.dext.centurybridge.step.v1_21_2.rt;

import java.util.concurrent.Executor;
import java.util.function.IntSupplier;
import top.dext.centurybridge.step.v1_21_2.facade.ChunkTaskPriorityQueueSorter;
import top.dext.centurybridge.step.v1_21_2.facade.ProcessorHandle;
import top.dext.centurybridge.step.v1_21_2.facade.ProcessorMailbox;

/**
 * 1.21.1's mailboxes handed to 1.21.2 APIs that take the executors replacing them:
 * new ThreadedLevelLightEngine(chunkGetter, chunkMap, skyLight, ProcessorMailbox taskMailbox,
 * ProcessorHandle&lt;Message&lt;Runnable&gt;&gt; sorterMailbox) is 1.21.2's
 * (..., ConsecutiveExecutor, ChunkTaskDispatcher). The light engine schedules its update on the first and
 * submits each light task, with its chunk and level, to the second.
 */
public final class CbRestExecutors {

    private CbRestExecutors() {
    }

    /**
     * The task mailbox as the 1.21.2 ConsecutiveExecutor the light engine schedules its update on: a
     * consecutive executor whose executor is the mailbox, so each scheduled task runs on the mailbox, one
     * at a time, as 1.21.1's tell(task) ran it.
     */
    public static net.minecraft.class_10176 lightTaskExecutor(ProcessorMailbox<Runnable> mailbox) {
        return new net.minecraft.class_10176(mailbox::method_16901, mailbox.method_16898());
    }

    /**
     * The sorter handle as the 1.21.2 ChunkTaskDispatcher the light engine submits to: 1.21.2's
     * submit(task, pos, level) becomes the 1.21.1 message the handle takes,
     * ChunkTaskPriorityQueueSorter.message(task, pos, level), so the task runs where 1.21.1's sorter puts
     * it. The dispatcher's own queue stays empty.
     */
    public static net.minecraft.class_10171 lightDispatcher(ProcessorHandle<ChunkTaskPriorityQueueSorter.Message<Runnable>> sorter) {
        return new LegacySorterDispatcher(sorter);
    }

    /**
     * WorldGenContext.mainThreadMailBox (1.21.1: a sorter handle taking ChunkTaskPriorityQueueSorter
     * messages; 1.21.2: mainThreadExecutor, an Executor) seen as 1.21.2's executor. 1.21.1's only user,
     * ChunkStatusTasks.full, ran each task through it as tell(message(task, pos, level)) -- at the chunk's
     * ticket level; an executor has no chunk to sort by, so a task runs as message(task, no chunk, level 0),
     * the order of submission at the front of the queue, which is the place 1.21.2 gives it (straight to
     * the main thread). Adaptive and the inverse of {@link #handle}: an executor this class made from a
     * handle comes back as that handle, null stays null.
     */
    public static Executor executor(ProcessorHandle<ChunkTaskPriorityQueueSorter.Message<Runnable>> handle) {
        if (handle == null) {
            return null;
        }
        if (handle instanceof ExecutorHandle eh) {
            return eh.executor;
        }
        return new HandleExecutor(handle);
    }

    /**
     * 1.21.2's main-thread executor as the 1.21.1 sorter handle WorldGenContext held: a message runs its
     * task on the executor, then releases the slot with the unit answer the sorter's messages expect
     * (ChunkTaskPriorityQueueSorter.message(runnable, ..) wraps the task so). The inverse of
     * {@link #executor}.
     */
    public static ProcessorHandle<ChunkTaskPriorityQueueSorter.Message<Runnable>> handle(Executor executor) {
        if (executor == null) {
            return null;
        }
        if (executor instanceof HandleExecutor he) {
            return he.handle;
        }
        return new ExecutorHandle(executor);
    }

    /** an executor over a 1.21.1 sorter handle (see {@link #executor}) */
    static final class HandleExecutor implements Executor {
        final ProcessorHandle<ChunkTaskPriorityQueueSorter.Message<Runnable>> handle;

        HandleExecutor(ProcessorHandle<ChunkTaskPriorityQueueSorter.Message<Runnable>> handle) {
            this.handle = handle;
        }

        @Override
        public void execute(Runnable task) {
            this.handle.method_16901(ChunkTaskPriorityQueueSorter.method_17626(task, net.minecraft.class_1923.field_17348, () -> 0));
        }
    }

    /** a 1.21.1 sorter handle over an executor (see {@link #handle}) */
    static final class ExecutorHandle implements ProcessorHandle<ChunkTaskPriorityQueueSorter.Message<Runnable>> {
        final Executor executor;

        ExecutorHandle(Executor executor) {
            this.executor = executor;
        }

        @Override
        public String method_16898() {
            return "main";
        }

        @Override
        public void method_16901(ChunkTaskPriorityQueueSorter.Message<Runnable> message) {
            Runnable task = message.field_17446.apply(ProcessorHandle.method_17344("main release", unit -> { }));
            this.executor.execute(task);
        }
    }

    /** a ChunkTaskDispatcher whose submit goes to a 1.21.1 sorter handle */
    static final class LegacySorterDispatcher extends net.minecraft.class_10171 {
        private final ProcessorHandle<ChunkTaskPriorityQueueSorter.Message<Runnable>> sorter;

        LegacySorterDispatcher(ProcessorHandle<ChunkTaskPriorityQueueSorter.Message<Runnable>> sorter) {
            super(net.minecraft.class_10178.method_63602(sorter.method_16898(), Runnable::run), Runnable::run);
            this.sorter = sorter;
        }

        @Override
        public void method_63553(Runnable task, long pos, IntSupplier level) {
            this.sorter.method_16901(ChunkTaskPriorityQueueSorter.method_17626(task, pos, level));
        }

        @Override
        public void close() {
            this.sorter.close();
        }
    }
}
