package cb.smx6c;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;

/** What the 1.20.6 mixins saw; read by the native 1.21 driver (cb-smx6n) through reflection. */
public final class Counters {
    /** FaceBakeryMixin.cb$countNoId: bakeQuad HEAD calls of a handler that does not read the id */
    /** DragonRaysMixin: 1.20.6 death-ray helper calls (centre, corners A / B / C) and impossible arguments */
    public static final AtomicInteger RAY_CENTRE = new AtomicInteger();
    public static final AtomicInteger RAY_A = new AtomicInteger();
    public static final AtomicInteger RAY_B = new AtomicInteger();
    public static final AtomicInteger RAY_C = new AtomicInteger();
    public static final AtomicInteger RAY_BAD = new AtomicInteger();
    public static final AtomicInteger CALLS_NO_ID = new AtomicInteger();
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

    /** BufferBuilderLifecycleMixin: 1.20.6 begin / end / endOrDiscardIfEmpty calls */
    public static final AtomicInteger BEGIN = new AtomicInteger();
    public static final AtomicInteger END = new AtomicInteger();
    public static final AtomicInteger END_OR_DISCARD = new AtomicInteger();

    private Counters() {
    }
}
