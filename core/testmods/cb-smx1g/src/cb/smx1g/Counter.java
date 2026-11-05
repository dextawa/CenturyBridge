package cb.smx1g;

/** Outside the mixin package: what the old mod did, reported through system properties for the native driver. */
public final class Counter {
    public static final int LIMIT = 64;
    private static int puts;
    private static int identityFails;

    private Counter() {
    }

    public static boolean full() {
        return puts >= LIMIT;
    }

    public static void put(boolean sameArray) {
        puts++;
        if (!sameArray) {
            identityFails++;
        }
        System.setProperty("cbsmx1g.puts", String.valueOf(puts));
        System.setProperty("cbsmx1g.identityFails", String.valueOf(identityFails));
    }
}
