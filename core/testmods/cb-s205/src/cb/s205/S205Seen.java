package cb.s205;

/** what cb.s205.mixin.LoadEffectMixin saw of GameRenderer.loadEffect (outside the mixin package: Mixin transforms that one) */
public final class S205Seen {
    /** the location of each loadEffect call, in order (HEAD) */
    public static final java.util.List<String> LOADS = new java.util.ArrayList<>();
    /** loadEffect calls that reached the end of the method (TAIL) */
    public static int tails;

    private S205Seen() {
    }
}
