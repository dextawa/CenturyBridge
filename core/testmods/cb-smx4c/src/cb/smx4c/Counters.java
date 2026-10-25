package cb.smx4c;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;

/** What the 1.21.3 mixins saw; the native 1.21.4 driver (cb-smx4n) reads LOADS and DONE through reflection. */
public final class Counters {
    /** TextureAtlas / DynamicTexture / FontTexture .load calls by the texture's runtime class */
    public static final Map<String, Integer> LOADS = new ConcurrentHashMap<>();
    /** the 1.21.3 getMaxScroll RETURN injection: calls and last value by list class */
    public static final Map<String, Integer> MAX_CALLS = new ConcurrentHashMap<>();
    public static final Map<String, Integer> MAX_VALUE = new ConcurrentHashMap<>();
    public static final AtomicInteger SCROLL_AMOUNT_OVERRIDE = new AtomicInteger();
    public static volatile Object blockedButton;
    public static volatile boolean done;

    private Counters() {
    }
}
