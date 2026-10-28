package cb.s9r;

import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;

/** what the 1.21.8 code saw; read by the native driver through reflection */
public final class Counters {
    public static final AtomicInteger IS_SELECTED_CALLS = new AtomicInteger();
    public static final Set<Integer> ASKED = ConcurrentHashMap.newKeySet();
    public static final AtomicInteger CLICK_MIXIN_CALLS = new AtomicInteger();
    public static final AtomicInteger KEEP_PRESSED = new AtomicInteger();
    public static final AtomicInteger BLOCKED_PRESSED = new AtomicInteger();

    private Counters() {
    }
}
