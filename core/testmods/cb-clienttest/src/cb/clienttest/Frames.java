package cb.clienttest;

import java.util.LinkedHashMap;
import java.util.Map;

/**
 * Drawing steps run every frame. Each one counts the frames it ran clean and keeps its first exception;
 * when the phase ends, settle() gives the verdict: ok if it ran at least MIN_FRAMES frames and never
 * threw, else FAIL with the exception (or "ran n frames"). A throwing step does not stop the others, so
 * the screenshot still shows what works. A step that never ran is reported by Tally.finish.
 */
public final class Frames {
    static final int MIN_FRAMES = 3;

    private static final class State {
        int clean;
        int thrown;
        Throwable first;
        String detail;
    }

    private static final Map<String, State> STATES = new LinkedHashMap<>();

    private Frames() {
    }

    public interface Step {
        /** draws; answers a detail for the ok line (values the step measured), or null */
        String run() throws Throwable;
    }

    public static void step(String name, Step s) {
        State st = STATES.computeIfAbsent(name, k -> new State());
        try {
            String d = s.run();
            st.clean++;
            if (d != null) {
                st.detail = d;
            }
        } catch (Throwable t) {
            st.thrown++;
            if (st.first == null) {
                st.first = t;
                t.printStackTrace();
            }
        }
    }

    /** the verdicts of every step whose name starts with prefix */
    public static void settle(String prefix) {
        for (Map.Entry<String, State> e : STATES.entrySet()) {
            if (!e.getKey().startsWith(prefix)) {
                continue;
            }
            State st = e.getValue();
            String frames = st.clean + " clean frames, " + st.thrown + " thrown";
            if (st.first != null) {
                Tally.fail(e.getKey(), st.first.getClass().getName() + ": " + st.first.getMessage() + " (" + frames + ")");
            } else if (st.clean < MIN_FRAMES) {
                Tally.fail(e.getKey(), "ran " + frames);
            } else {
                Tally.ok(e.getKey(), (st.detail == null ? "" : st.detail + " ") + "(" + frames + ")");
            }
        }
    }
}
