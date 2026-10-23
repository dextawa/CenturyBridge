package top.dext.centurybridge.step.v26_1.facade;

import com.mojang.serialization.Codec;
import net.minecraft.world.entity.ai.memory.MemorySlot;

/**
 * 1.21.11's ExpirableValue (net/minecraft/class_4831) on 26.1: a memory value with a time to live that
 * tick() counts down in place. 26.1 split it: the brain keeps each memory in a mutable MemorySlot (the
 * same value / time-to-live pair with the same tick, canExpire and hasExpired), and ExpirableValue is
 * an immutable record (value, optional time to live) used only to serialize memories.
 * <p>
 * An instance a mod makes holds its own pair. One handed out for a brain's memory ({@link #cb$of}) is
 * that memory as 1.21.11 held it: ticking it ticks the brain's slot, as ticking the brain's own
 * ExpirableValue did -- until the brain stores another value in the slot, when 1.21.11 would have put
 * a new ExpirableValue in its map: from then on this one keeps the value it had and counts down on its
 * own, from the time to live it last saw. Member names are 1.21.11's intermediary names.
 */
public class ExpirableValue<T> {

    /** MemorySlot / 1.21.11 ExpirableValue: a time to live of Long.MAX_VALUE never runs out */
    private static final long NEVER = Long.MAX_VALUE;

    private final T value;
    private long timeToLive;
    /** the brain's slot this value is (null: a value of its own) */
    private final MemorySlot<T> cb$slot;

    /** 1.21.11 ExpirableValue(value, timeToLive) */
    public ExpirableValue(T value, long timeToLive) {
        this.value = value;
        this.timeToLive = timeToLive;
        this.cb$slot = null;
    }

    private ExpirableValue(MemorySlot<T> slot) {
        this.value = slot.value();
        this.timeToLive = slot.timeToLive();
        this.cb$slot = slot;
    }

    /** a brain memory's slot as 1.21.11's ExpirableValue of it (null for an empty slot) */
    public static <T> ExpirableValue<T> cb$of(MemorySlot<T> slot) {
        return slot.hasValue() ? new ExpirableValue<>(slot) : null;
    }

    /** whether this still is its slot's memory (and refreshes the time to live seen) */
    private boolean cb$live() {
        if (cb$slot != null && cb$slot.hasValue() && cb$slot.value() == value) {
            timeToLive = cb$slot.timeToLive();
            return true;
        }
        return false;
    }

    /** tick() */
    public void method_24913() {
        if (cb$live()) {
            cb$slot.tick();
            timeToLive = cb$slot.timeToLive();
        } else if (method_24914()) {
            --timeToLive;
        }
    }

    /** of(value): never expires */
    public static <T> ExpirableValue<T> method_28355(T value) {
        return new ExpirableValue<>(value, NEVER);
    }

    /** of(value, timeToLive) */
    public static <T> ExpirableValue<T> method_24636(T value, long timeToLive) {
        return new ExpirableValue<>(value, timeToLive);
    }

    /** getTimeToLive() */
    public long method_35127() {
        cb$live();
        return timeToLive;
    }

    /** getValue() */
    public T method_24637() {
        return value;
    }

    /** hasExpired() */
    public boolean method_24634() {
        return method_35127() <= 0L;
    }

    /** canExpire() */
    public boolean method_24914() {
        return method_35127() != NEVER;
    }

    @Override
    public String toString() {
        return value + (method_24914() ? " (ttl: " + timeToLive + ")" : "");
    }

    /** the 26.1 record of this value (what 26.1 serializes) */
    public net.minecraft.world.entity.ai.memory.ExpirableValue<T> cb$record() {
        return method_24914() ? net.minecraft.world.entity.ai.memory.ExpirableValue.of(value, timeToLive)
                : net.minecraft.world.entity.ai.memory.ExpirableValue.of(value);
    }

    /** a 26.1 record as 1.21.11's value (a value of its own) */
    public static <T> ExpirableValue<T> cb$fromRecord(net.minecraft.world.entity.ai.memory.ExpirableValue<T> r) {
        return new ExpirableValue<>(r.value(), r.timeToLive().orElse(NEVER));
    }

    /** codec(Codec): 1.21.11's format ({"value": ..., "ttl": optional}) is 26.1's record codec's */
    public static <T> Codec<ExpirableValue<T>> method_28353(Codec<T> valueCodec) {
        return net.minecraft.world.entity.ai.memory.ExpirableValue.codec(valueCodec)
                .xmap(ExpirableValue::cb$fromRecord, ExpirableValue::cb$record);
    }
}
