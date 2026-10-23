package top.dext.centurybridge.step.v1_21_2.facade;

import com.google.common.collect.Lists;
import com.mojang.datafixers.util.Either;
import it.unimi.dsi.fastutil.longs.Long2ObjectLinkedOpenHashMap;
import it.unimi.dsi.fastutil.longs.LongOpenHashSet;
import it.unimi.dsi.fastutil.longs.LongSet;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import java.util.stream.Stream;

/**
 * 1.21.1's ChunkTaskPriorityQueue&lt;T&gt; (net/minecraft/class_3899, classRenames): per chunk and level, the
 * optional tasks and the acquired-chunk limit 1.21.1's sorter scheduled through. 1.21.2's class of that name
 * is another queue (Runnables, no limit) that 1.21.2's ChunkTaskDispatcher builds for itself; no 1.21.2 API
 * takes or hands one out. 1.21.1's body; the protected and package-private members are public here, as an access
 * widener never reaches a CenturyBridge class.
 */
public class ChunkTaskPriorityQueue<T> {
    /** PRIORITY_LEVEL_COUNT */
    public static final int field_17241 = net.minecraft.class_8563.field_44849 + 2;
    private final List<Long2ObjectLinkedOpenHashMap<List<Optional<T>>>> taskQueue = IntStream.range(0, field_17241)
        .mapToObj(i -> new Long2ObjectLinkedOpenHashMap<List<Optional<T>>>())
        .collect(Collectors.toList());
    private volatile int firstQueue = field_17241;
    private final String name;
    private final LongSet acquired = new LongOpenHashSet();
    private final int maxTasks;

    public ChunkTaskPriorityQueue(String name, int maxTasks) {
        this.name = name;
        this.maxTasks = maxTasks;
    }

    /** resortChunkTasks(oldLevel, pos, newLevel) */
    public void method_17272(int oldLevel, net.minecraft.class_1923 pos, int newLevel) {
        if (oldLevel < field_17241) {
            Long2ObjectLinkedOpenHashMap<List<Optional<T>>> queue = this.taskQueue.get(oldLevel);
            List<Optional<T>> tasks = queue.remove(pos.method_8324());
            if (oldLevel == this.firstQueue) {
                while (this.method_39993() && this.taskQueue.get(this.firstQueue).isEmpty()) {
                    this.firstQueue++;
                }
            }
            if (tasks != null && !tasks.isEmpty()) {
                this.taskQueue.get(newLevel).computeIfAbsent(pos.method_8324(), k -> Lists.newArrayList()).addAll(tasks);
                this.firstQueue = Math.min(this.firstQueue, newLevel);
            }
        }
    }

    /** submit(task, pos, level) */
    public void method_17274(Optional<T> task, long pos, int level) {
        this.taskQueue.get(level).computeIfAbsent(pos, k -> Lists.newArrayList()).add(task);
        this.firstQueue = Math.min(this.firstQueue, level);
    }

    /** release(pos, clearQueue) */
    public void method_17609(long pos, boolean clearQueue) {
        for (Long2ObjectLinkedOpenHashMap<List<Optional<T>>> queue : this.taskQueue) {
            List<Optional<T>> tasks = queue.get(pos);
            if (tasks != null) {
                if (clearQueue) {
                    tasks.clear();
                } else {
                    tasks.removeIf(Optional::isEmpty);
                }
                if (tasks.isEmpty()) {
                    queue.remove(pos);
                }
            }
        }
        while (this.method_39993() && this.taskQueue.get(this.firstQueue).isEmpty()) {
            this.firstQueue++;
        }
        this.acquired.remove(pos);
    }

    private Runnable acquire(long pos) {
        return () -> this.acquired.add(pos);
    }

    /** pop() */
    public Stream<Either<T, Runnable>> method_17606() {
        if (this.acquired.size() >= this.maxTasks) {
            return null;
        }
        if (!this.method_39993()) {
            return null;
        }
        int level = this.firstQueue;
        Long2ObjectLinkedOpenHashMap<List<Optional<T>>> queue = this.taskQueue.get(level);
        long pos = queue.firstLongKey();
        List<Optional<T>> tasks = queue.removeFirst();
        while (this.method_39993() && this.taskQueue.get(this.firstQueue).isEmpty()) {
            this.firstQueue++;
        }
        return tasks.stream().map(t -> t.<Either<T, Runnable>>map(Either::left).orElseGet(() -> Either.right(this.acquire(pos))));
    }

    /** hasWork() */
    public boolean method_39993() {
        return this.firstQueue < field_17241;
    }

    @Override
    public String toString() {
        return this.name + " " + this.firstQueue + "...";
    }

    /** getAcquired() */
    public LongSet method_21679() {
        return new LongOpenHashSet(this.acquired);
    }
}
