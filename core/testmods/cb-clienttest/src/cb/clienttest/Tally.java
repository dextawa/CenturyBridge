package cb.clienttest;

import java.util.LinkedHashMap;
import java.util.Map;

/**
 * The tally: every check is declared up front (so one that never runs counts as a failure, not as a
 * missing line), reported once as "CB-CLIENTTEST ok|FAIL <name> <detail>", and the run ends with
 * "CB-CLIENTTEST summary ok=N fail=M" and "CB-CLIENTTEST N/T".
 */
public final class Tally {
    private static final Map<String, String> RESULTS = new LinkedHashMap<>();
    private static final Map<String, Boolean> OK = new LinkedHashMap<>();

    private Tally() {
    }

    public static synchronized void declare(String... names) {
        for (String n : names) {
            OK.putIfAbsent(n, null);
        }
    }

    public static synchronized boolean reported(String name) {
        return OK.get(name) != null;
    }

    public static synchronized void ok(String name, String detail) {
        report(name, true, detail);
    }

    public static synchronized void fail(String name, String detail) {
        report(name, false, detail);
    }

    public static synchronized void check(String name, boolean pass, String detail) {
        report(name, pass, detail);
    }

    public static synchronized void error(String name, Throwable t) {
        report(name, false, t.getClass().getName() + ": " + t.getMessage());
        t.printStackTrace();
    }

    private static void report(String name, boolean pass, String detail) {
        if (OK.get(name) != null) {
            return; // first verdict stands
        }
        OK.put(name, pass);
        RESULTS.put(name, detail);
        log((pass ? "ok " : "FAIL ") + name + " " + detail);
    }

    public static void log(String line) {
        System.out.println("CB-CLIENTTEST " + line);
    }

    public static synchronized void finish() {
        int ok = 0, fail = 0;
        for (Map.Entry<String, Boolean> e : OK.entrySet()) {
            if (e.getValue() == null) {
                log("FAIL " + e.getKey() + " never ran");
                fail++;
            } else if (e.getValue()) {
                ok++;
            } else {
                fail++;
            }
        }
        log("summary ok=" + ok + " fail=" + fail);
        log(ok + "/" + (ok + fail));
    }
}
