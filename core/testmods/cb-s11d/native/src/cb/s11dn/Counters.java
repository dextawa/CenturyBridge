package cb.s11dn;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;

/** what the native 1.21.11 mixins saw (each 1.21.11 call they hook, counted; the last values captured) */
public final class Counters {
    public static final Map<String, AtomicInteger> COUNT = new ConcurrentHashMap<>();
    public static volatile Object lightingType;
    public static volatile Object skybox;
    public static volatile int sunriseColor;
    public static volatile float fogEnd = Float.NaN;

    private Counters() {
    }

    public static void hit(String what) {
        COUNT.computeIfAbsent(what, k -> new AtomicInteger()).incrementAndGet();
    }

    public static int get(String what) {
        AtomicInteger a = COUNT.get(what);
        return a == null ? 0 : a.get();
    }
}
