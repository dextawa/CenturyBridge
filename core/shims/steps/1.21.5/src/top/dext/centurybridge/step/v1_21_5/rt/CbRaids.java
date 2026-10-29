package top.dext.centurybridge.step.v1_21_5.rt;

import com.mojang.serialization.DataResult;
import com.mojang.serialization.Dynamic;
import java.util.Map;
import java.util.OptionalInt;
import java.util.WeakHashMap;
import net.minecraft.class_2338;
import net.minecraft.class_2487;
import net.minecraft.class_2509;
import net.minecraft.class_2520;
import net.minecraft.class_3218;
import net.minecraft.class_3763;
import net.minecraft.class_3765;
import net.minecraft.server.MinecraftServer;

/**
 * 1.21.4's Raid members that 1.21.5 reshaped (javap -c -p 1.21.4 / 1.21.5 class_3765, class_3767). 1.21.5 took the level
 * and the id out of Raid: the ServerLevel is passed to the methods that need it (tick, joinRaid, addWaveMob,
 * removeFromRaid), the id is the key under which the level's Raids holds the raid (Raids.getId(raid)), and a raid is
 * saved by Raid.MAP_CODEC inside Raids' file instead of by Raid.save(tag).
 * <ul>
 * <li>A raid's level and id: those the raid was built with by the 1.21.4 constructors below; otherwise (a raid 1.21.5
 * built) the level whose Raids holds it and its id there, as 1.21.4's raid carried them. A raid no level holds and no
 * 1.21.4 constructor built has neither: getLevel answers null and getId -1.</li>
 * <li>new Raid(id, level, center): 1.21.5's Raid(center, level's difficulty) -- 1.21.4's constructor read the same
 * difficulty for the group count and kept the rest at their initial values, as 1.21.5's does.</li>
 * <li>save(tag) / new Raid(level, tag): 1.21.4's NBT form (Id, Started, Active, TicksActive, BadOmenLevel,
 * GroupsSpawned, PreRaidTicks, PostRaidTicks, TotalHealth, NumGroups, Status, CX / CY / CZ, HeroesOfTheVillage)
 * written from and read into 1.21.5's MAP_CODEC form (the same values under 1.21.5's keys: started, active,
 * ticks_active, raid_omen_level, groups_spawned, cooldown_ticks, post_raid_ticks, total_health, group_count, status,
 * center, heroes_of_the_village); a missing key reads as 1.21.4's getters answered it (0 / false / ONGOING).</li>
 * </ul>
 */
public final class CbRaids {

    private CbRaids() {
    }

    private static final Map<class_3765, class_3218> LEVELS = new WeakHashMap<>();
    private static final Map<class_3765, Integer> IDS = new WeakHashMap<>();

    /** 1.21.4 key, 1.21.5 key (the values each holds are of the same type and meaning) */
    private static final String[][] KEYS = {
        { "Started", "started" }, { "Active", "active" }, { "TicksActive", "ticks_active" }, { "BadOmenLevel", "raid_omen_level" },
        { "GroupsSpawned", "groups_spawned" }, { "PreRaidTicks", "cooldown_ticks" }, { "PostRaidTicks", "post_raid_ticks" },
        { "TotalHealth", "total_health" }, { "NumGroups", "group_count" }, { "Status", "status" },
        { "HeroesOfTheVillage", "heroes_of_the_village" } };

    private static synchronized void remember(class_3765 raid, class_3218 level, int id) {
        LEVELS.put(raid, level);
        IDS.put(raid, id);
    }

    /** the level holding the raid (and its id there), found and remembered */
    private static synchronized class_3218 find(class_3765 raid) {
        class_3218 level = LEVELS.get(raid);
        if (level != null) {
            return level;
        }
        MinecraftServer server = CbVariants.server();
        if (server == null) {
            return null;
        }
        for (class_3218 l : server.method_3738()) {
            OptionalInt id = l.method_19495().method_67378(raid);
            if (id.isPresent()) {
                remember(raid, l, id.getAsInt());
                return l;
            }
        }
        return null;
    }

    /** new Raid(int, ServerLevel, BlockPos) */
    public static class_3765 newRaid(int id, class_3218 level, class_2338 center) {
        class_3765 raid = new class_3765(center, level.method_8407());
        remember(raid, level, id);
        return raid;
    }

    /** new Raid(ServerLevel, CompoundTag) */
    public static class_3765 loadRaid(class_3218 level, class_2487 tag) {
        class_2487 w = raidMap(tag);
        DataResult<class_3765> r = class_3765.field_56439.codec().parse(new Dynamic<>(class_2509.field_11560, w));
        class_3765 raid = r.getOrThrow(msg -> new IllegalArgumentException("Raid NBT does not read on 1.21.5: " + msg));
        remember(raid, level, tag.method_10550("Id").orElse(0));
        return raid;
    }

    /** a 1.21.4 raid tag in 1.21.5's Raid.MAP_CODEC form (see the class javadoc) */
    private static class_2487 raidMap(class_2487 tag) {
        class_2487 w = new class_2487();
        for (String[] k : KEYS) {
            class_2520 v = tag.method_10580(k[0]);
            if (v != null) {
                w.method_10566(k[1], v.method_10707());
            }
        }
        w.method_10556("started", tag.method_10577("Started").orElse(false));
        w.method_10556("active", tag.method_10577("Active").orElse(false));
        w.method_10544("ticks_active", tag.method_10537("TicksActive").orElse(0L));
        w.method_10569("raid_omen_level", tag.method_10550("BadOmenLevel").orElse(0));
        w.method_10569("groups_spawned", tag.method_10550("GroupsSpawned").orElse(0));
        w.method_10569("cooldown_ticks", tag.method_10550("PreRaidTicks").orElse(0));
        w.method_10569("post_raid_ticks", tag.method_10550("PostRaidTicks").orElse(0));
        w.method_10548("total_health", tag.method_10583("TotalHealth").orElse(0.0F));
        w.method_10569("group_count", tag.method_10550("NumGroups").orElse(0));
        // 1.21.4 RaidStatus.getByName: the constant of that name ignoring case, ONGOING otherwise
        String status = tag.method_10558("Status").orElse("");
        // (1.21.5's RaidStatus is private; its serialized names are 1.21.4's constant names in lower case)
        String known = "ongoing";
        for (String s : new String[] { "ongoing", "victory", "loss", "stopped" }) {
            if (s.equalsIgnoreCase(status)) {
                known = s;
            }
        }
        w.method_10582("status", known);
        // 1.21.4 read HeroesOfTheVillage only when the tag had the list (none: no heroes); 1.21.5's MAP_CODEC requires it
        if (!(w.method_10580("heroes_of_the_village") instanceof net.minecraft.class_2499)) {
            w.method_10566("heroes_of_the_village", new net.minecraft.class_2499());
        }
        class_2338 center = new class_2338(tag.method_10550("CX").orElse(0), tag.method_10550("CY").orElse(0), tag.method_10550("CZ").orElse(0));
        w.method_10566("center", class_2338.field_25064.encodeStart(class_2509.field_11560, center).getOrThrow());
        return w;
    }

    /** Raid.save(CompoundTag) */
    public static class_2487 method_16502(class_3765 raid, class_2487 tag) {
        class_2520 encoded = class_3765.field_56439.codec().encodeStart(class_2509.field_11560, raid).getOrThrow();
        class_2487 w = (class_2487) encoded;
        tag.method_10569("Id", method_16494(raid));
        for (String[] k : KEYS) {
            class_2520 v = w.method_10580(k[1]);
            if (v != null) {
                tag.method_10566(k[0], v.method_10707());
            }
        }
        class_2520 c = w.method_10580("center");
        if (c != null) {
            class_2338 center = class_2338.field_25064.parse(class_2509.field_11560, c).getOrThrow();
            tag.method_10569("CX", center.method_10263());
            tag.method_10569("CY", center.method_10264());
            tag.method_10569("CZ", center.method_10260());
        }
        return tag;
    }

    /** Raid.getId() */
    public static int method_16494(class_3765 raid) {
        find(raid);
        synchronized (CbRaids.class) {
            Integer id = IDS.get(raid);
            return id == null ? -1 : id;
        }
    }

    /** Raid.getLevel() */
    public static net.minecraft.class_1937 method_16831(class_3765 raid) {
        return find(raid);
    }

    /** Raid.tick() */
    public static void method_16509(class_3765 raid) {
        class_3218 level = find(raid);
        if (level != null) {
            raid.method_16509(level);
        }
    }

    /** Raid.joinRaid(int, Raider, BlockPos, boolean) */
    public static void method_16516(class_3765 raid, int wave, class_3763 raider, class_2338 pos, boolean isRecruited) {
        raid.method_16516(level(raid, raider), wave, raider, pos, isRecruited);
    }

    /** Raid.removeFromRaid(Raider, boolean) */
    public static void method_16510(class_3765 raid, class_3763 raider, boolean wanderedOutOfRaid) {
        raid.method_16510(level(raid, raider), raider, wanderedOutOfRaid);
    }

    /** Raid.addWaveMob(int, Raider, boolean) */
    public static boolean method_16487(class_3765 raid, int wave, class_3763 raider, boolean isRecruited) {
        return raid.method_16487(level(raid, raider), wave, raider, isRecruited);
    }

    /** the raid's level; a raid no level holds acts in its raider's level (1.21.4's raiders were in the raid's level) */
    private static class_3218 level(class_3765 raid, class_3763 raider) {
        class_3218 level = find(raid);
        return level != null ? level : (class_3218) raider.method_37908();
    }

    // ---- Raids (class_3767): 1.21.5 took the level out of it as out of Raid (javap -c -p both). The level's Raids is
    // ---- still the one SavedData its storage holds (ServerLevel.getRaids, method_19495); a Raids 1.21.4's constructor
    // ---- or load built carries the level it was built for here, any other one the level whose getRaids answers it.

    private static final Map<net.minecraft.class_3767, class_3218> RAIDS_LEVELS = new WeakHashMap<>();

    private static synchronized class_3218 levelOf(net.minecraft.class_3767 raids) {
        class_3218 level = RAIDS_LEVELS.get(raids);
        if (level != null) {
            return level;
        }
        MinecraftServer server = CbVariants.server();
        if (server != null) {
            for (class_3218 l : server.method_3738()) {
                if (l.method_19495() == raids) {
                    RAIDS_LEVELS.put(raids, l);
                    return l;
                }
            }
        }
        return null;
    }

    /** new Raids(ServerLevel): 1.21.5's empty Raids (next id 1, tick 0, as 1.21.4's constructor set them) for that level */
    public static net.minecraft.class_3767 newRaids(class_3218 level) {
        net.minecraft.class_3767 raids = new net.minecraft.class_3767();
        synchronized (CbRaids.class) {
            RAIDS_LEVELS.put(raids, level);
        }
        return raids;
    }

    /** Raids.tick(): 1.21.5's tick(level) with the Raids' level; a Raids no level holds has nothing to tick in */
    public static void method_16539(net.minecraft.class_3767 raids) {
        class_3218 level = levelOf(raids);
        if (level != null) {
            raids.method_16539(level);
        }
    }

    /**
     * Raids.canJoinRaid(Raider, Raid): 1.21.4's body (javap): a raider and a raid with a level, the raider alive, able
     * to join (canJoinRaid), idle for at most 2400 ticks (getNoActionTime) and in a level of the raid's dimension type.
     * 1.21.5's canJoinRaid(raider) is the same without the raid.
     */
    public static boolean method_16838(class_3763 raider, class_3765 raid) {
        if (raider == null || raid == null) {
            return false;
        }
        net.minecraft.class_1937 level = method_16831(raid);
        if (level == null) {
            return false;
        }
        return raider.method_5805() && raider.method_16481() && raider.method_6131() <= 2400
            && raider.method_37908().method_8597() == level.method_8597();
    }

    /**
     * Raids.load(ServerLevel, CompoundTag): 1.21.4's file form (NextAvailableID, Tick, Raids: the raids' 1.21.4 tags,
     * each with its Id) read by 1.21.5's Raids.load(tag) after the same values are put in 1.21.5's form (next_id, tick,
     * raids: each raid's MAP_CODEC form with its id, javap class_3767 / class_3767$class_10734); each raid and the
     * Raids carry the level and id they were loaded with.
     */
    public static net.minecraft.class_3767 method_77(class_3218 level, class_2487 tag) {
        class_2487 w = new class_2487();
        w.method_10569("next_id", tag.method_10550("NextAvailableID").orElse(0));
        w.method_10569("tick", tag.method_10550("Tick").orElse(0));
        net.minecraft.class_2499 list = new net.minecraft.class_2499();
        java.util.List<Integer> ids = new java.util.ArrayList<>();
        for (class_2520 e : tag.method_68569("Raids")) {
            if (e instanceof class_2487 raidTag) {
                class_2487 r = raidMap(raidTag);
                int id = raidTag.method_10550("Id").orElse(0);
                r.method_10569("id", id);
                list.add(r);
                ids.add(id);
            }
        }
        w.method_10566("raids", list);
        net.minecraft.class_3767 raids = net.minecraft.class_3767.method_77(w);
        synchronized (CbRaids.class) {
            RAIDS_LEVELS.put(raids, level);
            for (int id : ids) {
                class_3765 raid = raids.method_16541(id);
                if (raid != null) {
                    remember(raid, level, id);
                }
            }
        }
        return raids;
    }

    /** Raids.getFileId(Holder&lt;DimensionType&gt;): 1.21.4's body, "raids_end" for the end's dimension type, else "raids" */
    public static String method_16533(net.minecraft.class_6880<net.minecraft.class_2874> type) {
        return type.method_40225(net.minecraft.class_7134.field_37668) ? "raids_end" : "raids";
    }
}
