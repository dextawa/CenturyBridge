package cb.smx6c;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;

/** What the 1.20.6 mixins saw; read by the native 1.21 driver (cb-smx6n) through reflection. */
public final class Counters {
    public static final AtomicInteger CALLS = new AtomicInteger();
    public static final AtomicInteger NULLS = new AtomicInteger();
    public static final AtomicInteger REPLACED = new AtomicInteger();
    public static final Map<String, Integer> LOCATIONS = new ConcurrentHashMap<>();
    /** ModelBakeryMoveMixin / MobRendererLeashMixin: calls of the members 1.21 moved (s121.json mixinMoves) */
    public static final AtomicInteger PREDICATE = new AtomicInteger();
    public static final AtomicInteger VALUE = new AtomicInteger();
    public static final AtomicInteger GROUP = new AtomicInteger();
    public static final AtomicInteger LEASH = new AtomicInteger();
    /** the same mixins with bare-name selectors ("method_4725"), as most mods write them */
    public static final AtomicInteger PREDICATE_BARE = new AtomicInteger();
    public static final AtomicInteger VALUE_BARE = new AtomicInteger();
    public static final AtomicInteger GROUP_BARE = new AtomicInteger();
    public static final AtomicInteger LEASH_BARE = new AtomicInteger();

    private Counters() {
    }
}
