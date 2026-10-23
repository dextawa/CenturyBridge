package top.dext.centurybridge.step.v1_21_2.rt;

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
