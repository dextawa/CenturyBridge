package top.dext.centurybridge.step.v26_1.rt;

import com.mojang.logging.LogUtils;
import com.mojang.serialization.Dynamic;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.WeakHashMap;
import java.util.stream.Stream;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.NbtOps;
import net.minecraft.nbt.Tag;
import net.minecraft.world.level.timers.TimerCallback;
import net.minecraft.world.level.timers.TimerCallbacks;
import net.minecraft.world.level.timers.TimerQueue;
import org.slf4j.Logger;

/**
 * 1.21.11's TimerQueue on 26.1's. 1.21.11's queue was built with its TimerCallbacks (and optionally the saved
 * events as a stream of Dynamics) and wrote itself with store() into a ListTag of {Name, TriggerTime, Callback}
 * compounds. 26.1's queue is SavedData: built empty, with no callbacks of its own, and written by its codec through
 * pack(). The callbacks a 1.21.11 constructor gave are kept per queue here; a queue nobody gave callbacks to is
 * written with the callbacks 26.1 itself writes its queues with (TimerQueue.CODEC: SERVER_CALLBACKS).
 */
public final class CbWallTimers {

    private static final Logger LOGGER = LogUtils.getLogger();
    private static final Map<TimerQueue<?>, TimerCallbacks<?>> CALLBACKS = new WeakHashMap<>();

    private CbWallTimers() {
    }

    /** 1.21.11 TimerQueue(callbacks) */
    public static <T> TimerQueue<T> newQueue(TimerCallbacks<T> callbacks) {
        TimerQueue<T> queue = new TimerQueue<>();
        remember(queue, callbacks);
        return queue;
    }

    /** 1.21.11 TimerQueue(callbacks, events) */
    public static <T> TimerQueue<T> newQueue(TimerCallbacks<T> callbacks, Stream<? extends Dynamic<?>> events) {
        TimerQueue<T> queue = newQueue(callbacks);
        load(queue, callbacks, events);
        return queue;
    }

    /** post of a 1.21.11 subclass's super(callbacks) */
    public static void remember(TimerQueue<?> queue, TimerCallbacks<?> callbacks) {
        synchronized (CALLBACKS) {
            CALLBACKS.put(queue, callbacks);
        }
    }

    /**
     * post of a 1.21.11 subclass's super(callbacks, events): 1.21.11's body after this(callbacks) -- each event a
     * compound read with the queue's callbacks, anything else logged and skipped
     */
    @SuppressWarnings({"unchecked", "rawtypes"})
    public static void load(TimerQueue queue, TimerCallbacks callbacks, Stream<? extends Dynamic<?>> events) {
        remember(queue, callbacks);
        events.forEach(dynamic -> {
            Tag tag = (Tag) ((Dynamic) dynamic).convert(NbtOps.INSTANCE).getValue();
            if (tag instanceof CompoundTag compound) {
                TimerCallback callback = (TimerCallback) compound.read("Callback", callbacks.codec()).orElse(null);
                if (callback != null) {
                    queue.schedule(compound.getStringOr("Name", ""), compound.getLongOr("TriggerTime", 0L), callback);
                }
            } else {
                LOGGER.warn("Invalid format of events: {}", tag);
            }
        });
    }

    @SuppressWarnings("unchecked")
    private static <T> TimerCallbacks<T> callbacks(TimerQueue<T> queue) {
        TimerCallbacks<?> callbacks;
        synchronized (CALLBACKS) {
            callbacks = CALLBACKS.get(queue);
        }
        return callbacks != null ? (TimerCallbacks<T>) callbacks : (TimerCallbacks<T>) TimerCallbacks.SERVER_CALLBACKS;
    }

    /**
     * 1.21.11 TimerQueue.store() (a call, or a subclass's super call): the events in trigger order, each
     * {Name, TriggerTime, Callback}. 26.1's pack() is the same events in the same order.
     */
    public static <T> ListTag store(TimerQueue<T> queue) {
        TimerCallbacks<T> callbacks = callbacks(queue);
        ListTag list = new ListTag();
        for (TimerQueue.Event.Packed<T> event : queue.pack().events()) {
            CompoundTag tag = new CompoundTag();
            tag.putString("Name", event.id());
            tag.putLong("TriggerTime", event.triggerTime());
            tag.store("Callback", callbacks.codec(), event.callback());
            list.add(tag);
        }
        return list;
    }

    /**
     * a 1.21.11 subclass's super.store(): 1.21.11's own body, not the subclass's override -- pack() runs with the
     * queue marked as inside its hooked call, so the pack() a store() override gave the queue (the override hook)
     * answers vanilla's events rather than forwarding to the override again
     */
    public static <T> ListTag superStore(TimerQueue<T> queue) {
        boolean entered = Guard.cbEnter(queue);
        try {
            return store(queue);
        } finally {
            if (entered) {
                Guard.cbExit(queue);
            }
        }
    }

    /** a 1.21.11 TimerQueue subclass's store() override (the wall gives it its 1.21.11 name) */
    public interface LegacyStore {
        ListTag store();
    }

    /**
     * 26.1's pack() of a queue whose 1.21.11 class overrides store(): 26.1 writes the queue through pack(), so the
     * events are the ones the mod's store() answers, read back as 1.21.11's constructor read saved events (an
     * entry that is not a compound, or whose callback does not read, is skipped)
     */
    @SuppressWarnings({"unchecked", "rawtypes"})
    public static TimerQueue.Packed pack(LegacyStore store) {
        TimerCallbacks callbacks = callbacks((TimerQueue) store);
        List<TimerQueue.Event.Packed> events = new ArrayList<>();
        for (Tag tag : store.store()) {
            if (tag instanceof CompoundTag compound) {
                TimerCallback callback = (TimerCallback) compound.read("Callback", callbacks.codec()).orElse(null);
                if (callback != null) {
                    events.add(new TimerQueue.Event.Packed(compound.getLongOr("TriggerTime", 0L),
                        compound.getStringOr("Name", ""), callback));
                }
            } else {
                LOGGER.warn("Invalid format of events: {}", tag);
            }
        }
        return new TimerQueue.Packed(events);
    }
}
