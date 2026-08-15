package top.dext.centurybridge.data;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

/**
 * Corpus-wide symbol accounting: every damaged reference the analysis pass
 * classifies -- including ones already handled by a cover/redirect/rename --
 * lands here with its fate and call-site count. The output table is the
 * authoritative kill-list map: bridging is driven from it wholesale, never
 * from crash reports one symbol at a time.
 */
public final class FullAudit {

    public static boolean enabled = false;

    // symbol -> fate -> call-site count
    private static final Map<String, Map<String, Integer>> DATA = new TreeMap<>();

    public static synchronized void record(String symbol, String fate) {
        if (!enabled) {
            return;
        }
        DATA.computeIfAbsent(symbol, k -> new TreeMap<>()).merge(fate, 1, Integer::sum);
    }

    public static synchronized void reset() {
        DATA.clear();
    }

    /** TSV sorted by total refcount descending: count, fate, symbol. */
    public static synchronized void write(Path out) throws IOException {
        record Row(int count, String fate, String symbol) {}
        List<Row> rows = new ArrayList<>();
        for (var e : DATA.entrySet()) {
            for (var f : e.getValue().entrySet()) {
                rows.add(new Row(f.getValue(), f.getKey(), e.getKey()));
            }
        }
        rows.sort((a, b) -> b.count() - a.count());
        List<String> lines = new ArrayList<>();
        lines.add("count\tfate\tsymbol");
        for (Row r : rows) {
            lines.add(r.count() + "\t" + r.fate() + "\t" + r.symbol());
        }
        Files.write(out, String.join("\n", lines).getBytes(StandardCharsets.UTF_8));
    }

    /** Fate-category rollup for the console: bridged vs accounted vs open. */
    public static synchronized Map<String, Integer> summary() {
        Map<String, Integer> sums = new TreeMap<>();
        for (var e : DATA.entrySet()) {
            for (var f : e.getValue().entrySet()) {
                String cat = f.getKey().split("[\\[@ :]")[0];
                sums.merge(cat, 1, Integer::sum); // distinct symbols per category
            }
        }
        return sums;
    }

    private FullAudit() {
    }
}
