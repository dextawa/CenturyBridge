package cb.s26dn;

/**
 * Counters and the per-frame call order shared with the climbed 1.21.8 mod through system properties (no class link
 * between the two mods). Keys: "cbs26d.<who>.<what>" counts; "cbs26d.seq" the letters of the frame being drawn,
 * "cbs26d.lastseq" the last finished frame's.
 */
public final class Probe {
    private Probe() {
    }

    public static synchronized void bump(String key) {
        String k = "cbs26d." + key;
        System.setProperty(k, String.valueOf(Long.parseLong(System.getProperty(k, "0")) + 1));
    }

    public static long get(String key) {
        return Long.parseLong(System.getProperty("cbs26d." + key, "0"));
    }

    public static synchronized void seq(String letter) {
        System.setProperty("cbs26d.seq", System.getProperty("cbs26d.seq", "") + letter);
    }

    public static synchronized void endFrame() {
        System.setProperty("cbs26d.lastseq", System.getProperty("cbs26d.seq", ""));
        System.setProperty("cbs26d.seq", "");
    }
}
