package cb.smx6c;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;

/** What the 1.20.6 FaceBakery mixin saw; read by the native 1.21 driver (cb-smx6n) through reflection. */
public final class Counters {
    public static final AtomicInteger CALLS = new AtomicInteger();
    public static final AtomicInteger NULLS = new AtomicInteger();
    public static final AtomicInteger REPLACED = new AtomicInteger();
    public static final Map<String, Integer> LOCATIONS = new ConcurrentHashMap<>();

    private Counters() {
    }
}
