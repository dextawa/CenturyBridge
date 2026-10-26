package cb.s6bn;

/** counters shared with the climbed 1.21.5 driver cb-s6b through system properties (no class link between the two mods) */
public final class Counter {
    private Counter() {
    }

    public static synchronized void bump(String key) {
        System.setProperty(key, String.valueOf(Long.parseLong(System.getProperty(key, "0")) + 1));
    }
}
