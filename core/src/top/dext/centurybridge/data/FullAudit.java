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
        write(out, null);
    }

    /**
     * As {@link #write(Path)}; past the namespace wall a fourth column carries each
     * symbol as the converted jar spells it (the same rename the jar went through),
     * so a check reading the jar can join its references to the audit.
     */
    public static synchronized void write(Path out, org.objectweb.asm.commons.Remapper wall) throws IOException {
        record Row(int count, String fate, String symbol) {}
        List<Row> rows = new ArrayList<>();
        for (var e : DATA.entrySet()) {
            for (var f : e.getValue().entrySet()) {
                rows.add(new Row(f.getValue(), f.getKey(), e.getKey()));
            }
        }
        rows.sort((a, b) -> b.count() - a.count());
        List<String> lines = new ArrayList<>();
        lines.add("count\tfate\tsymbol" + (wall != null ? "\toutSymbol" : ""));
        for (Row r : rows) {
            lines.add(r.count() + "\t" + r.fate() + "\t" + r.symbol() + (wall != null ? "\t" + outName(r.symbol(), wall) : ""));
        }
        Files.write(out, String.join("\n", lines).getBytes(StandardCharsets.UTF_8));
    }

    /** "owner.name(desc)" / "owner.name:desc" / a class name, through the wall rename */
    static String outName(String symbol, org.objectweb.asm.commons.Remapper wall) {
        if (symbol.startsWith("instanceof ")) {
            return "instanceof " + wall.map(symbol.substring(11));
        }
        int paren = symbol.indexOf('(');
        int colon = symbol.indexOf(':');
        if (paren < 0 && colon < 0) {
            return wall.map(symbol);
        }
        int dot = paren >= 0 ? symbol.lastIndexOf('.', paren) : symbol.lastIndexOf('.', colon);
        if (dot < 0) {
            return symbol;
        }
        String owner = symbol.substring(0, dot);
        if (paren >= 0) {
            String name = symbol.substring(dot + 1, paren);
            String desc = symbol.substring(paren);
            String n = name.startsWith("<") ? name : wall.mapMethodName(owner, name, desc);
            return wall.map(owner) + "." + n + wall.mapMethodDesc(desc);
        }
        String name = symbol.substring(dot + 1, colon);
        String desc = symbol.substring(colon + 1);
        return wall.map(owner) + "." + wall.mapFieldName(owner, name, desc) + ":" + wall.mapDesc(desc);
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
