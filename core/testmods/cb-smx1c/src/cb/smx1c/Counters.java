package cb.smx1c;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;

/** call counts of the chunk-graph methods the mixins hook; prints "CB-SMX1C hit <name> <n>" at 1, 100 and 10000 */
public final class Counters {
    private static final Map<String, AtomicInteger> HITS = new ConcurrentHashMap<>();

    private Counters() {
    }

    public static void hit(String name, Object detail) {
        int n = HITS.computeIfAbsent(name, k -> new AtomicInteger()).incrementAndGet();
        if (n == 1 || n == 100 || n == 10000) {
            System.out.println("CB-SMX1C hit " + name + " " + n + " (" + detail + ")");
        }
    }
}
