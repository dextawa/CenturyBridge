package cb.clienttest;

/** What ContainerRenderMixin saw: render HEAD/RETURN calls per screen class (outside the mixin package). */
public final class ContainerProbe {
    static int heads, rets;
    static Class<?> lastHead;

    private ContainerProbe() {
    }

    public static void head(Object screen) {
        heads++;
        lastHead = screen.getClass();
    }

    public static void ret(Object screen) {
        rets++;
    }

    static void reset() {
        heads = 0;
        rets = 0;
        lastHead = null;
    }
}
