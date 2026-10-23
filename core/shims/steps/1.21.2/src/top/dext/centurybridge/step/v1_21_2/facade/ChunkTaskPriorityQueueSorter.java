package top.dext.centurybridge.step.v1_21_2.facade;

import com.google.common.collect.Sets;
import com.mojang.datafixers.util.Either;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;
import java.util.function.Function;
import java.util.function.IntConsumer;
import java.util.function.IntSupplier;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * 1.21.1's ChunkTaskPriorityQueueSorter (net/minecraft/class_3900, classRenames) with its Message and
 * Release (class_3946 / class_3947): per processor, a priority queue of chunk tasks that a sorter mailbox
 * hands on in level order, at most maxTasks chunks acquired at once. 1.21.2 replaced it by
 * ChunkTaskDispatcher; this is 1.21.1's body on the 1.21.1 mailbox, handle and queue the layer gives back.
 * It is still a ChunkHolder.LevelChangeListener, as 1.21.1's was.
 */
public class ChunkTaskPriorityQueueSorter implements net.minecraft.class_3193.class_3896, AutoCloseable {
    private final Map<ProcessorHandle<?>, ChunkTaskPriorityQueue<? extends Function<ProcessorHandle<net.minecraft.class_3902>, ?>>> queues;
    private final Set<ProcessorHandle<?>> sleeping;
    private final ProcessorMailbox<StrictQueue.RunnableWithPriority> mailbox;

    public ChunkTaskPriorityQueueSorter(List<ProcessorHandle<?>> handles, Executor executor, int maxTasks) {
        this.queues = handles.stream().collect(Collectors.toMap(Function.identity(),
            h -> new ChunkTaskPriorityQueue<>(h.method_16898() + "_queue", maxTasks)));
        this.sleeping = Sets.newHashSet(handles);
        this.mailbox = new ProcessorMailbox<>(new StrictQueue.FixedPriorityQueue(4), executor, "sorter");
    }

    /** hasWork() */
    public boolean method_39994() {
        return this.mailbox.method_40001() || this.queues.values().stream().anyMatch(ChunkTaskPriorityQueue::method_39993);
    }

    /** message(task, pos, level) */
    public static <T> Message<T> method_34871(Function<ProcessorHandle<net.minecraft.class_3902>, T> task, long pos, IntSupplier level) {
        return new Message<>(task, pos, level);
    }

    /** message(runnable, pos, level): the task, then the unit answer that releases the slot */
    public static Message<Runnable> method_17626(Runnable task, long pos, IntSupplier level) {
        return new Message<>(handle -> () -> {
            task.run();
            handle.method_16901(net.minecraft.class_3902.field_17274);
        }, pos, level);
    }

    /** message(chunkHolder, runnable): at the holder's chunk and queue level */
    public static Message<Runnable> method_17629(net.minecraft.class_9761 holder, Runnable task) {
        return method_17626(task, holder.method_60473().method_8324(), holder::method_17208);
    }

    /** message(chunkHolder, task) */
    public static <T> Message<T> method_34870(net.minecraft.class_9761 holder, Function<ProcessorHandle<net.minecraft.class_3902>, T> task) {
        return method_34871(task, holder.method_60473().method_8324(), holder::method_17208);
    }

    /** release(task, pos, clearQueue) */
    public static Release method_17627(Runnable task, long pos, boolean clearQueue) {
        return new Release(task, pos, clearQueue);
    }

    /** getProcessor(target, flush): a handle taking messages for target, sorted by chunk level */
    public <T> ProcessorHandle<Message<T>> method_17622(ProcessorHandle<T> target, boolean flush) {
        return this.mailbox.<ProcessorHandle<Message<T>>>method_17345(reply -> new StrictQueue.RunnableWithPriority(0, () -> {
            this.getQueue(target);
            reply.method_16901(ProcessorHandle.method_17344("chunk priority sorter around " + target.method_16898(),
                (Message<T> m) -> this.submit(target, m.field_17446, m.field_17447, m.field_17448, flush)));
        })).join();
    }

    /** getReleaseProcessor(target) */
    public ProcessorHandle<Release> method_17614(ProcessorHandle<Runnable> target) {
        return this.mailbox.<ProcessorHandle<Release>>method_17345(reply -> new StrictQueue.RunnableWithPriority(0,
            () -> reply.method_16901(ProcessorHandle.method_17344("chunk priority sorter around " + target.method_16898(),
                (Release r) -> this.release(target, r.field_17450, r.field_17449, r.field_17451))))).join();
    }

    /** onLevelChange(pos, oldLevel, newLevel, setLevel) */
    @Override
    public void method_17209(net.minecraft.class_1923 pos, IntSupplier oldLevel, int newLevel, IntConsumer setLevel) {
        this.mailbox.method_16901(new StrictQueue.RunnableWithPriority(0, () -> {
            int old = oldLevel.getAsInt();
            this.queues.values().forEach(q -> q.method_17272(old, pos, newLevel));
            setLevel.accept(newLevel);
        }));
    }

    private <T> void release(ProcessorHandle<T> target, long pos, Runnable task, boolean clearQueue) {
        this.mailbox.method_16901(new StrictQueue.RunnableWithPriority(1, () -> {
            ChunkTaskPriorityQueue<Function<ProcessorHandle<net.minecraft.class_3902>, T>> queue = this.getQueue(target);
            queue.method_17609(pos, clearQueue);
            if (this.sleeping.remove(target)) {
                this.pollTask(queue, target);
            }
            task.run();
        }));
    }

    private <T> void submit(ProcessorHandle<T> target, Function<ProcessorHandle<net.minecraft.class_3902>, T> task, long pos,
                            IntSupplier level, boolean flush) {
        this.mailbox.method_16901(new StrictQueue.RunnableWithPriority(2, () -> {
            ChunkTaskPriorityQueue<Function<ProcessorHandle<net.minecraft.class_3902>, T>> queue = this.getQueue(target);
            int l = level.getAsInt();
            queue.method_17274(Optional.of(task), pos, l);
            if (flush) {
                queue.method_17274(Optional.empty(), pos, l);
            }
            if (this.sleeping.remove(target)) {
                this.pollTask(queue, target);
            }
        }));
    }

    @SuppressWarnings({"unchecked", "rawtypes"})
    private <T> void pollTask(ChunkTaskPriorityQueue<Function<ProcessorHandle<net.minecraft.class_3902>, T>> queue, ProcessorHandle<T> target) {
        this.mailbox.method_16901(new StrictQueue.RunnableWithPriority(3, () -> {
            Stream<Either<Function<ProcessorHandle<net.minecraft.class_3902>, T>, Runnable>> tasks = queue.method_17606();
            if (tasks == null) {
                this.sleeping.add(target);
            } else {
                CompletableFuture.allOf(tasks.map(e -> e.map(target::method_17345, r -> {
                    r.run();
                    return CompletableFuture.completedFuture(net.minecraft.class_3902.field_17274);
                })).toArray(CompletableFuture[]::new)).thenAccept(v -> this.pollTask(queue, target));
            }
        }));
    }

    @SuppressWarnings("unchecked")
    private <T> ChunkTaskPriorityQueue<Function<ProcessorHandle<net.minecraft.class_3902>, T>> getQueue(ProcessorHandle<T> target) {
        ChunkTaskPriorityQueue<? extends Function<ProcessorHandle<net.minecraft.class_3902>, ?>> queue = this.queues.get(target);
        if (queue == null) {
            throw (IllegalArgumentException) net.minecraft.class_156.method_22320(new IllegalArgumentException("No queue for: " + target));
        }
        return (ChunkTaskPriorityQueue<Function<ProcessorHandle<net.minecraft.class_3902>, T>>) queue;
    }

    /** getDebugStatus() */
    public String method_21680() {
        return this.queues.entrySet().stream()
            .map(e -> e.getKey().method_16898() + "=["
                + e.getValue().method_21679().stream().map(p -> p + ":" + new net.minecraft.class_1923(p)).collect(Collectors.joining(","))
                + "]")
            .collect(Collectors.joining(","))
            + ", s=" + this.sleeping.size();
    }

    @Override
    public void close() {
        this.queues.keySet().forEach(ProcessorHandle::close);
    }

    /** ChunkTaskPriorityQueueSorter.Message (class_3946) */
    public static final class Message<T> {
        /** task (package-private in 1.21.1) */
        public final Function<ProcessorHandle<net.minecraft.class_3902>, T> field_17446;
        /** pos */
        public final long field_17447;
        /** level */
        public final IntSupplier field_17448;

        public Message(Function<ProcessorHandle<net.minecraft.class_3902>, T> task, long pos, IntSupplier level) {
            this.field_17446 = task;
            this.field_17447 = pos;
            this.field_17448 = level;
        }
    }

    /** ChunkTaskPriorityQueueSorter.Release (class_3947) */
    public static final class Release {
        /** task (package-private in 1.21.1) */
        public final Runnable field_17449;
        /** pos */
        public final long field_17450;
        /** clearQueue */
        public final boolean field_17451;

        public Release(Runnable task, long pos, boolean clearQueue) {
            this.field_17449 = task;
            this.field_17450 = pos;
            this.field_17451 = clearQueue;
        }
    }
}
