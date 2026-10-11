package top.dext.centurybridge.res;

import java.util.List;

/**
 * The releases the chain walks, oldest first. Data formats are upgraded by the
 * boundaries a resource crosses on its way to the target, so every rule names
 * the release whose format it produces.
 */
public final class Release {

    public static final List<String> ALL = List.of(
        "1.20.1", "1.20.2", "1.20.3", "1.20.4", "1.20.5", "1.20.6",
        "1.21", "1.21.1", "1.21.2", "1.21.3", "1.21.4", "1.21.5", "1.21.6",
        "1.21.7", "1.21.8", "1.21.9", "1.21.10", "1.21.11",
        // year-based era: ships unobfuscated, intermediary is dead -- the
        // 1.21.11 -> 26.1 segment is a whole-namespace wall (intermediary ->
        // mojang), not just a stub diff
        "26.1", "26.1.1", "26.1.2", "26.2");

    /** each release's data version (version.json world_version), the clock DataFixers count by */
    public static final List<Integer> DATA_VERSIONS = List.of(
        3465, 3578, 3698, 3700, 3837, 3839,
        3953, 3955, 4080, 4082, 4189, 4325, 4435,
        4438, 4440, 4554, 4556, 4671,
        4786, 4788, 4790, 4903);

    public static int dataVersion(String version) {
        return DATA_VERSIONS.get(index(version));
    }

    public static int index(String version) {
        int i = ALL.indexOf(version);
        if (i < 0) {
            throw new IllegalArgumentException("unknown release " + version);
        }
        return i;
    }

    /** whether a resource headed for {@code target} crosses into {@code release}'s format */
    public static boolean reaches(String target, String release) {
        return index(target) >= index(release);
    }

    /** whether a resource written for {@code source} predates {@code release}'s format */
    public static boolean before(String source, String release) {
        return index(source) < index(release);
    }

    private Release() {
    }
}
