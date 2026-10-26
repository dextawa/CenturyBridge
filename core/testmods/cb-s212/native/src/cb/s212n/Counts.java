package cb.s212n;

/** how often this mod's 1.21.2 mixins fired (cb-s212's driver reads them by reflection) */
public final class Counts {
    public static volatile int cookTime;
    public static volatile int canBurn;
    public static volatile int seen;

    private Counts() {
    }
}
