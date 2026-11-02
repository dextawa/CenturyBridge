package cb.slm;

/** per entity class counters shared with the native driver through the system properties (no class of either mod is the other's) */
public final class Count {
    public static void hit(String what, Object entity) {
        System.getProperties().merge("cb.slm." + what + "." + entity.getClass().getSimpleName(), 1, (a, b) -> (Integer) a + (Integer) b);
    }

    private Count() {
    }
}
