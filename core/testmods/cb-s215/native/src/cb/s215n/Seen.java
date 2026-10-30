package cb.s215n;

/** counts of 1.21.5 calls seen by this native mod's own mixins (read by the converted cb-s215 through reflection) */
public final class Seen {
    public static volatile int equip;
    public static volatile int saddled;
    public static volatile int dispense;
    public static volatile int canUse;
    public static volatile int sound;

    private Seen() {
    }
}
