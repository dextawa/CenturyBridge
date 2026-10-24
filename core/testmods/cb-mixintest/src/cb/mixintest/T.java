package cb.mixintest;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Bookkeeping of the mixin test: every case logs "CB-MIXINTEST ok|FAIL <case> <detail>", every
 * handler that runs logs "CB-MIXINTEST ran <case> <values>" and is counted; the driver ends with
 * "CB-MIXINTEST summary ok=N fail=M".
 */
public final class T {
    private static int ok;
    private static int fail;
    private static final Map<String, String> RAN = new ConcurrentHashMap<>();

    private T() {
    }

    public static void check(String c, boolean cond, String detail) {
        if (cond) {
            ok++;
            System.out.println("CB-MIXINTEST ok " + c + " " + detail);
        } else {
            fail++;
            System.out.println("CB-MIXINTEST FAIL " + c + " " + detail);
        }
    }

    public static void fail(String c, Throwable t) {
        fail++;
        System.out.println("CB-MIXINTEST FAIL " + c + " " + t);
        t.printStackTrace(System.out);
    }

    /** a handler ran, with what it saw */
    public static void ran(String c, String values) {
        RAN.put(c, values);
        System.out.println("CB-MIXINTEST ran " + c + " " + values);
    }

    /** what a handler last saw, or null when it never ran */
    public static String seen(String c) {
        return RAN.get(c);
    }

    public static void summary() {
        System.out.println("CB-MIXINTEST summary ok=" + ok + " fail=" + fail);
    }
}
