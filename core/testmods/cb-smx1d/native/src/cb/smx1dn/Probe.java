package cb.smx1dn;

/**
 * Native counters shared with the climbed 1.21.10 mod through system properties ("cbsmx1d.n.<key>"). The per-vertex and
 * per-option counters count on the render thread only: the old mod's checks run there as one task, so nothing else on
 * that thread draws or reads options in between, while chunk-builder threads write vertices all the time.
 */
public final class Probe {
    private Probe() {
    }

    public static synchronized void bump(String key) {
        String k = "cbsmx1d.n." + key;
        System.setProperty(k, String.valueOf(Long.parseLong(System.getProperty(k, "0")) + 1));
    }

    public static void bumpRender(String key) {
        if ("Render thread".equals(Thread.currentThread().getName())) {
            bump(key);
        }
    }

    public static long get(String key) {
        return Long.parseLong(System.getProperty("cbsmx1d.n." + key, "0"));
    }
}
