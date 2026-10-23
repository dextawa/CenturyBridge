package top.dext.centurybridge.step.v1_21_2.rt;

import java.util.ArrayList;
import java.util.IdentityHashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.BiConsumer;
import net.minecraft.class_2378;
import net.minecraft.class_2960;
import net.minecraft.class_5321;

/**
 * Registry ids that 1.21.2 wants at construction, given to objects of a 1.21.1 mod that constructs
 * first and registers later.
 *
 * <p>1.21.1 derived what depends on an object's id lazily, from the registry, once it was registered:
 * Item.getDescriptionId (Item.getOrCreateDescriptionId: "item." + the registry id), the ITEM_NAME a
 * stack falls back to (Item.getName: that key), Block.getDescriptionId and getLootTable ("blocks/" + the
 * id, read on first use), EntityType's descriptionId and loot table. 1.21.2 bakes them into final fields
 * at construction from the key the Properties / builder carry (Item.Properties.setId, BlockBehaviour.
 * Properties.setId, EntityType.Builder.build(key)) and fails without one ("Item id not set"). A 1.21.1
 * mod's properties carry none.
 *
 * <p>Two phases, a registry at a time:
 * <ol>
 * <li>construction: the object gets a placeholder key of its own ({@link #placeholder}; the mixin on the
 *     constructor puts it on the properties for the reads the constructor makes, and takes it off again
 *     so that properties reused for another object -- one static Properties often builds several -- are
 *     not handed this one's identity); the object is remembered as pending, by identity;</li>
 * <li>registration: every registration ends in MappedRegistry.register(key, value, info)
 *     (Registry.register, registerForHolder, Fabric API's and vanilla's bootstrap alike;
 *     CbPendingIdsRegistryMixin hooks it at HEAD, before the value binds). A pending value of that
 *     registry gets the real key: each {@link #listen listener} for the registry re-derives what the
 *     constructor took from the placeholder, then the value stops being pending.</li>
 * </ol>
 * An object constructed with an id of its own (a mod calling 1.21.2's setId) is never pending and
 * never touched. An object never registered keeps the names of its placeholder, which no lang file or
 * loot table has -- as a 1.21.1 object read before registration had none either.
 */
public final class CbPendingIds {

    /** the namespace of every placeholder id */
    public static final String NS = "centurybridge_pending";

    private static final AtomicInteger N = new AtomicInteger();

    /** pending object -> its placeholder key */
    private static final Map<Object, class_5321<?>> PENDING = new IdentityHashMap<>();

    /** registry key -> its backfills, in order of {@link #listen} */
    private static final Map<class_5321<?>, List<BiConsumer<Object, class_5321<?>>>> LISTENERS = new java.util.HashMap<>();

    /**
     * A fresh placeholder key in {@code registry} ("centurybridge_pending:p&lt;n&gt;"), for an object about to
     * be constructed without an id; {@link #pending} marks the object once it exists.
     */
    public static <T> class_5321<T> placeholder(class_5321<? extends class_2378<T>> registry) {
        return class_5321.method_29179(registry, class_2960.method_60655(NS, "p" + N.getAndIncrement()));
    }

    /** whether a key is a placeholder of this mechanism (null counts as no key at all: false) */
    public static boolean isPlaceholder(class_5321<?> key) {
        return key != null && key.method_29177().method_12836().equals(NS);
    }

    /** remembers {@code object} as built under {@code placeholder}, to be backfilled when it registers */
    public static synchronized void pending(Object object, class_5321<?> placeholder) {
        PENDING.put(object, placeholder);
    }

    /** the placeholder {@code object} was built under, or null when it is not pending */
    public static synchronized class_5321<?> placeholderOf(Object object) {
        return PENDING.get(object);
    }

    /** whether {@code object} was built under a placeholder and has not registered yet */
    public static synchronized boolean isPending(Object object) {
        return PENDING.containsKey(object);
    }

    /**
     * Adds a backfill for values of {@code registry}: when a pending value registers there, it is called
     * with the value and its real key (in {@code registry}), before the value binds. Listeners run in the
     * order they were added; the value stops being pending after the last.
     */
    public static synchronized void listen(class_5321<? extends class_2378<?>> registry, BiConsumer<Object, class_5321<?>> backfill) {
        LISTENERS.computeIfAbsent(registry, k -> new ArrayList<>()).add(backfill);
    }

    /**
     * from the registry hook: the real key of a value being registered in the registry {@code registry}. A
     * value pending under a placeholder of another registry is left pending (it waits for its own).
     */
    public static void register(class_5321<? extends class_2378<?>> registry, class_5321<?> key, Object value) {
        List<BiConsumer<Object, class_5321<?>>> ls;
        synchronized (CbPendingIds.class) {
            class_5321<?> ph = PENDING.get(value);
            if (ph == null || !ph.method_41185().equals(registry.method_29177())) {
                return;
            }
            ls = new ArrayList<>(LISTENERS.getOrDefault(registry, List.of()));
        }
        for (BiConsumer<Object, class_5321<?>> l : ls) {
            l.accept(value, key);
        }
        synchronized (CbPendingIds.class) {
            PENDING.remove(value);
        }
    }

    private CbPendingIds() {
    }
}
