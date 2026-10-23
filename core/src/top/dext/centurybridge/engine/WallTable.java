package top.dext.centurybridge.engine;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;

/**
 * Which namespace-wall rename table (intermediary -> mojang, C/M/F rows) a conversion uses: the one
 * spelling names the way the release it TARGETS declares them. The rule lives in one tracked file,
 * core/segments/wall-tables.tsv ("target TAB table under data/mappings"), read here by the engine and
 * by core/tools/wall_tables.py for the tools and scripts -- the climb's wall step (target 26.1) and
 * every 26.1.x target take the 26.1-named table; the old anchor jump to 26.2 keeps the 26.2-named one.
 *
 * {@code -Dcb.wallTable=<path>} (or the environment's CB_WALL_TABLE, which wall_tables.py and through
 * it build.sh and the scripts honour too) uses another table for this run: a candidate measured before
 * it is deployed under data/mappings.
 */
public final class WallTable {

    private WallTable() {
    }

    /** the rename table for a conversion to {@code target}; null when no wall lies before it */
    public static Path forTarget(Path segmentsDir, String target) throws IOException {
        String override = System.getProperty("cb.wallTable", System.getenv("CB_WALL_TABLE"));
        if (override != null && !override.isEmpty()) {
            return Path.of(override);
        }
        Path rules = segmentsDir.resolve("wall-tables.tsv");
        if (!Files.exists(rules)) {
            throw new IllegalStateException(rules + " is missing: it says which wall table a conversion to "
                + target + " renames with");
        }
        for (String line : Files.readAllLines(rules, StandardCharsets.UTF_8)) {
            if (line.isBlank() || line.startsWith("#")) {
                continue;
            }
            String[] f = line.split("\t");
            if (f.length >= 2 && f[0].equals(target)) {
                // a file name under data/mappings, or a path (absolute, or relative to the working directory)
                String t = f[1].trim();
                return t.contains("/") ? Path.of(t) : Path.of("data/mappings").resolve(t);
            }
        }
        throw new IllegalStateException(rules + " names no wall table for target " + target);
    }
}
