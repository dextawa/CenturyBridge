package cb.clienttest;

import java.util.HashMap;
import java.util.Map;

/**
 * What the mod's mixins saw (outside the mixin package): HEAD/RETURN calls per hook and screen class --
 * AbstractContainerScreen.render, Screen.renderBackground, ChatComponent.render.
 */
public final class Hooks {
    public static final String CONTAINER_RENDER = "containerRender";
    public static final String BACKGROUND = "renderBackground";
    public static final String CHAT = "chatRender";

    private static final Map<String, int[]> COUNTS = new HashMap<>();

    private Hooks() {
    }

    public static synchronized void head(String hook, Object self) {
        counts(hook, self.getClass())[0]++;
    }

    public static synchronized void ret(String hook, Object self) {
        counts(hook, self.getClass())[1]++;
    }

    private static int[] counts(String hook, Class<?> c) {
        return COUNTS.computeIfAbsent(hook + ":" + c.getName(), k -> new int[2]);
    }

    /** {heads, returns} of hook on exactly class c since the last reset */
    static synchronized int[] get(String hook, Class<?> c) {
        int[] v = COUNTS.get(hook + ":" + c.getName());
        return v == null ? new int[2] : v.clone();
    }

    /** heads of hook on any class since the last reset */
    static synchronized int heads(String hook) {
        int n = 0;
        for (Map.Entry<String, int[]> e : COUNTS.entrySet()) {
            if (e.getKey().startsWith(hook + ":")) {
                n += e.getValue()[0];
            }
        }
        return n;
    }

    static synchronized void reset() {
        COUNTS.clear();
    }

    /** the verdict of a HEAD/RETURN pair on screen class c: heads ran and every head returned */
    static void verdict(String check, String hook, Class<?> c) {
        int[] v = get(hook, c);
        Tally.check(check, v[0] > 0 && v[1] == v[0], v[0] + " heads, " + v[1] + " returns on " + c.getSimpleName());
    }
}
