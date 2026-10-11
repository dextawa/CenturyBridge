package top.dext.centurybridge.res;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Vanilla ids across releases: the renames Mojang's DataFixers apply to world data (a
 * data pack is never fixed, so a mod's file still says minecraft:grass after 1.20.3),
 * and which vanilla tags each release ships (a tag file with a required reference to a
 * tag that is gone fails to load whole). Built from {@link IdRenameTable}, generated
 * out of the game itself.
 */
public final class VanillaIds {

    private record Rename(int dataVersion, String from, String to) {
    }

    private static final List<Rename> RENAMES = new ArrayList<>();
    private static final Map<String, String> TAGS = new HashMap<>();
    private static final Map<String, String> ENTRIES = new HashMap<>();

    static {
        for (String[] r : IdRenameTable.RENAMES) {
            RENAMES.add(new Rename(Integer.parseInt(r[0]), r[2], r[3]));
        }
        for (String[] t : IdRenameTable.TAG_PRESENCE) {
            TAGS.put(t[0], t[1]);
        }
        for (String[] t : IdRenameTable.ENTRY_PRESENCE) {
            ENTRIES.put(t[0], t[1]);
        }
    }

    /** AttributeIdPrefixFix (data version 4055, 1.21.2): attribute ids lost their category prefix */
    private static final int ATTRIBUTE_PREFIXES = 4055;
    private static final String[] PREFIXES = {"generic.", "horse.", "player.", "zombie."};

    /**
     * A vanilla id written for {@code source}, as {@code target} spells it: every rename
     * with a data version past the source's and up to the target's, in order. Ids that
     * were never renamed come back as given.
     */
    public static String renamed(String id, String source, String target) {
        int from = Release.dataVersion(source);
        int to = Release.dataVersion(target);
        String cur = id;
        for (Rename r : RENAMES) {
            if (r.dataVersion() > from && r.dataVersion() <= to && r.from().equals(cur)) {
                cur = r.to();
            }
        }
        return cur;
    }

    /** an attribute id ("minecraft:generic.max_health") as the target spells it */
    public static String attribute(String id, String source, String target) {
        String cur = renamed(id, source, target);
        if (Release.dataVersion(source) < ATTRIBUTE_PREFIXES && Release.dataVersion(target) >= ATTRIBUTE_PREFIXES) {
            String ns = cur.contains(":") ? cur.substring(0, cur.indexOf(':') + 1) : "minecraft:";
            String path = cur.contains(":") ? cur.substring(cur.indexOf(':') + 1) : cur;
            if (ns.equals("minecraft:")) {
                for (String p : PREFIXES) {
                    if (path.startsWith(p)) {
                        return ns + path.substring(p.length());
                    }
                }
            }
        }
        return cur;
    }

    /**
     * Whether vanilla release {@code release} ships tag {@code id} (namespaced, no '#') of
     * registry {@code registry} (singular: "block", "item", "worldgen/biome"). A tag the
     * table does not list is in every release, or is not vanilla at all.
     */
    public static boolean tagShipped(String registry, String id, String release) {
        String bits = TAGS.get(registry + ":" + id);
        return bits == null || bits.charAt(Release.index(release)) == '1';
    }

    /** a vanilla tag the source release shipped and the target does not */
    public static boolean tagGone(String registry, String id, String source, String target) {
        String bits = TAGS.get(registry + ":" + id);
        return bits != null && bits.charAt(Release.index(source)) == '1' && bits.charAt(Release.index(target)) == '0';
    }

    /**
     * Whether a minecraft-namespace data-driven entry (registry "worldgen/biome" or
     * "worldgen/structure") is one the release ships; null for other namespaces, whose
     * entries come from mods and cannot be judged here.
     */
    public static Boolean entryShipped(String registry, String id, String release) {
        if (!id.startsWith("minecraft:")) {
            return null;
        }
        String bits = ENTRIES.get(registry + ":" + id);
        return bits != null && bits.charAt(Release.index(release)) == '1';
    }

    private VanillaIds() {
    }
}
