package top.dext.centurybridge.step.v1_21_2.facade;

import com.google.common.collect.Queues;
import java.util.Locale;
import java.util.Queue;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * 1.21.1's StrictQueue&lt;T, F&gt; (net/minecraft/class_3847) with its FixedPriorityQueue, QueueStrictQueue and
 * RunnableWithPriority (classRenames): what 1.21.1's ProcessorMailbox drained. 1.21.2's StrictQueue takes
 * only Runnables (one type argument) and its RunnableWithPriority is a record; both are a different API,
 * kept for 1.21.2's own executors.
 */
public interface StrictQueue<T, F> {

    /** pop() */
    F method_16909();

    /** push(value) */
    boolean method_16910(T value);

    /** isEmpty() */
    boolean method_16911();

    /** size() */
    int method_34706();

    /** StrictQueue.FixedPriorityQueue (class_3847$class_3848) */
    final class FixedPriorityQueue implements StrictQueue<RunnableWithPriority, Runnable> {
        private final Queue<Runnable>[] queues;
        private final AtomicInteger size = new AtomicInteger();

        @SuppressWarnings("unchecked")
        public FixedPriorityQueue(int priorities) {
            this.queues = new Queue[priorities];
            for (int i = 0; i < priorities; i++) {
                this.queues[i] = Queues.newConcurrentLinkedQueue();
            }
        }

        /** pop() */
        @Override
        public Runnable method_16909() {
            return this.method_17346();
        }

        /** pop() as 1.21.1's own bridge-less signature (method_17346) */
        public Runnable method_17346() {
            for (Queue<Runnable> q : this.queues) {
                Runnable r = q.poll();
                if (r != null) {
                    this.size.decrementAndGet();
                    return r;
                }
            }
            return null;
        }

        /** push(task) */
        @Override
        public boolean method_16910(RunnableWithPriority task) {
            return this.method_16913(task);
        }

        /** push(task), 1.21.1's typed one (method_16913) */
        public boolean method_16913(RunnableWithPriority task) {
            int priority = task.field_17278;
            if (priority < this.queues.length && priority >= 0) {
                this.queues[priority].add(task);
                this.size.incrementAndGet();
                return true;
            }
            throw new IndexOutOfBoundsException(
                String.format(Locale.ROOT, "Priority %d not supported. Expected range [0-%d]", priority, this.queues.length - 1));
        }

        @Override
        public boolean method_16911() {
            return this.size.get() == 0;
        }

        @Override
        public int method_34706() {
            return this.size.get();
        }
    }

    /** StrictQueue.QueueStrictQueue (class_3847$class_3849) */
    final class QueueStrictQueue<T> implements StrictQueue<T, T> {
        private final Queue<T> queue;

        public QueueStrictQueue(Queue<T> queue) {
            this.queue = queue;
        }

        @Override
        public T method_16909() {
            return this.queue.poll();
        }

        @Override
        public boolean method_16910(T value) {
            return this.queue.add(value);
        }

        @Override
        public boolean method_16911() {
            return this.queue.isEmpty();
        }

        @Override
        public int method_34706() {
            return this.queue.size();
        }
    }

    /** StrictQueue.RunnableWithPriority (class_3847$class_3907) */
    final class RunnableWithPriority implements Runnable {
        /** priority (package-private in 1.21.1) */
        public final int field_17278;
        private final Runnable task;

        public RunnableWithPriority(int priority, Runnable task) {
            this.field_17278 = priority;
            this.task = task;
        }

        @Override
        public void run() {
            this.task.run();
        }

        /** getPriority() */
        public int method_17347() {
            return this.field_17278;
        }
    }
}
