package cb.sl6;

/** counters shared with the native control through the system properties (neither mod loads the other's classes) */
public final class Count {
    public static void hit(String what) {
        System.getProperties().merge("cb.sl6." + what, 1, (a, b) -> (Integer) a + (Integer) b);
    }

    public static int of(String what) {
        Object v = System.getProperties().get("cb.sl6." + what);
        return v instanceof Integer i ? i : 0;
    }

    private Count() {
    }
}
