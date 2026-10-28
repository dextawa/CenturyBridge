package cb.smx6;

import java.util.List;
import net.minecraft.class_1297;
import net.minecraft.class_1299;
import net.minecraft.class_1430;
import net.minecraft.class_1531;
import net.minecraft.class_1538;
import net.minecraft.class_1685;
import net.minecraft.class_1799;
import net.minecraft.class_1802;
import net.minecraft.class_1893;
import net.minecraft.class_238;
import net.minecraft.class_2338;
import net.minecraft.class_2902;
import net.minecraft.class_3218;
import net.minecraft.class_3966;

/**
 * Runs once the server's world is loaded. Throws tridents into cows under open sky by calling 1.20.6's
 * ThrownTrident.onHitEntity, then counts the lightning bolts it struck: on 1.20.6 a Channeling trident strikes in a
 * thunderstorm unless isChanneling() says no, which TridentChannelingMixin makes it say while cancelChanneling is set.
 * Logs "CB-SMX6 ok|FAIL <case> <detail>" per case and "CB-SMX6 summary ok=N fail=M" at the end.
 */
public final class Driver {
    public static volatile boolean cancelChanneling;
    public static volatile int asked;
    public static volatile boolean forceMending;
    public static volatile int bookAsked;
    public static volatile net.minecraft.class_1887 lastPick;
    public static volatile int templateAsked;
    public static volatile String templateName;
    public static volatile int templateAmplifier;
    public static volatile boolean doubleEffect;
    public static volatile int slimesAsked;
    public static volatile int slimesCramming;
    public static volatile int slimesSpawnCount;
    public static volatile boolean noSlimes;
    public static volatile int matchesAsked;
    public static volatile int matchesSize;
    public static volatile boolean forceMatch;
    public static volatile int openAsked;
    public static volatile String openUrl;
    public static volatile int followCanAsked;
    public static volatile int followMoved;
    public static volatile int crashCommentAsked;
    public static volatile int profileCommentAsked;
    public static volatile boolean forceComment;
    public static volatile boolean blockFollowTeleport;
    public static volatile boolean blockRaid;
    public static volatile int badOmenAsked;
    public static volatile int badOmenRaids;
    public static volatile boolean blockLeashRestore;
    public static volatile int leashRestoreAsked;
    public static volatile int leashRestored;
    public static volatile boolean foldNested;
    public static volatile int nestedAsked;
    public static volatile int nestedVisits;
    private static net.minecraft.class_3222 followOwner;
    private static int ok;
    private static int fail;

    private Driver() {
    }

    private static void check(String c, boolean cond, String detail) {
        if (cond) {
            ok++;
        } else {
            fail++;
        }
        System.out.println("CB-SMX6 " + (cond ? "ok " : "FAIL ") + c + " " + detail);
    }

    /** a case waiting on a known engine gap: "ok" once it holds, else "known" (counted in neither ok nor fail) */
    private static void known(String c, boolean cond, String detail) {
        if (cond) {
            ok++;
        }
        System.out.println("CB-SMX6 " + (cond ? "ok " : "known ") + c + " " + detail);
    }

    /** one hit: a cow at the surface dx blocks east of spawn, a trident owned by an armor stand; returns the bolts struck */
    private static int hit(class_3218 level, int dx, boolean channeling) {
        class_2338 spawn = level.method_43126();
        class_2338 pos = level.method_8598(class_2902.class_2903.field_13197, spawn.method_10069(dx, 0, 0));
        class_1430 cow = class_1299.field_6085.method_5883(level);
        cow.method_5808(pos.method_10263() + 0.5, pos.method_10264(), pos.method_10260() + 0.5, 0.0F, 0.0F);
        level.method_8649(cow);
        class_1531 owner = class_1299.field_6131.method_5883(level);
        owner.method_5808(pos.method_10263() + 3.5, pos.method_10264(), pos.method_10260() + 0.5, 0.0F, 0.0F);
        level.method_8649(owner);
        class_1799 stack = new class_1799(class_1802.field_8547);
        if (channeling) {
            stack.method_7978(class_1893.field_9117, 1);
        }
        class_1685 trident = new class_1685(level, owner, stack);
        ((cb.smx6.mixin.TridentAccess) trident).cbsmx6$onHitEntity(new class_3966(cow));
        List<class_1538> bolts = level.method_18023(class_1299.field_6112, new class_238(pos).method_1014(4.0), e -> true);
        int n = bolts.size();
        for (class_1538 b : bolts) {
            b.method_31472();
        }
        cow.method_31472();
        owner.method_31472();
        trident.method_31472();
        return n;
    }

    /**
     * the librarian's level-1 enchanted-book offer for each seed (a fresh seeded random per call): "seed=key:level"
     * read off the book (getEnchantmentsForCrafting), with what the pick answered
     */
    private static String books(class_3218 level, long[] seeds) {
        net.minecraft.class_1646 villager = new net.minecraft.class_1646(class_1299.field_6077, level);
        net.minecraft.class_3853.class_1652[] trades = net.minecraft.class_3853.field_17067.get(net.minecraft.class_3852.field_17060).get(1);
        StringBuilder out = new StringBuilder();
        for (long seed : seeds) {
            for (net.minecraft.class_3853.class_1652 t : trades) {
                lastPick = null;
                net.minecraft.class_1914 offer = t.method_7246(villager, net.minecraft.class_5819.method_43049(seed));
                if (offer == null || offer.method_8250().method_7909() != class_1802.field_8598) {
                    continue;
                }
                net.minecraft.class_9304 e = net.minecraft.class_1890.method_57532(offer.method_8250());
                String key = lastPick == null ? "?" : String.valueOf(net.minecraft.class_7923.field_41176.method_10221(lastPick));
                int lvl = lastPick == null ? -1 : e.method_57536(lastPick);
                out.append(seed).append('=').append(key).append(':').append(lvl).append(' ');
            }
        }
        villager.method_31472();
        return out.toString().trim();
    }

    /** a pig's movement speed with Speed (amplifier 0) applied through LivingEntity.addEffect */
    private static double speed(class_3218 level) {
        net.minecraft.class_1452 pig = class_1299.field_6093.method_5883(level);
        class_2338 spawn = level.method_43126();
        pig.method_5808(spawn.method_10263() + 0.5, 100.0, spawn.method_10260() + 0.5, 0.0F, 0.0F);
        pig.method_6092(new net.minecraft.class_1293(net.minecraft.class_1294.field_5904, 200, 0));
        double v = pig.method_45325(net.minecraft.class_5134.field_23719);
        pig.method_31472();
        return v;
    }

    /** a zombie under Oozing removed as killed at the surface beside spawn: the slimes it left within 4 blocks */
    private static int oozing(net.minecraft.class_3218 level, int dx) {
        net.minecraft.class_2338 spawn = level.method_43126();
        net.minecraft.class_2338 pos = level.method_8598(net.minecraft.class_2902.class_2903.field_13197, spawn.method_10069(dx, 0, 24));
        net.minecraft.class_1642 zombie = net.minecraft.class_1299.field_6051.method_5883(level);
        zombie.method_5808(pos.method_10263() + 0.5, pos.method_10264(), pos.method_10260() + 0.5, 0.0F, 0.0F);
        level.method_8649(zombie);
        zombie.method_6092(new net.minecraft.class_1293(net.minecraft.class_1294.field_50120, 200, 0));
        zombie.method_5650(net.minecraft.class_1297.class_5529.field_26998);
        java.util.List<net.minecraft.class_1621> slimes = level.method_18023(net.minecraft.class_1299.field_6069, new net.minecraft.class_238(pos).method_1014(4.0), e -> true);
        int n = slimes.size();
        for (net.minecraft.class_1621 s : slimes) {
            s.method_31472();
        }
        return n;
    }

    /** the recipe a RecipeCache answers for a 3x3 grid holding one item in its first slot, by id, or "none" */
    private static String recipe(net.minecraft.class_8884 cache, class_3218 level, net.minecraft.class_1792 item) {
        net.minecraft.class_2371<class_1799> items = net.minecraft.class_2371.method_10213(9, class_1799.field_8037);
        items.set(0, new class_1799(item));
        net.minecraft.class_1715 grid = new net.minecraft.class_1715(null, 3, 3, items);
        java.util.Optional<net.minecraft.class_8786<net.minecraft.class_3955>> r = cache.method_54470(level, grid);
        return r.isPresent() ? String.valueOf(r.get().comp_1932()) : "none";
    }

    /** the enchantment on the book a trade offers (a fresh seeded random), by registry key and level, or "none" */
    private static String bookOf(class_3218 level, net.minecraft.class_3853.class_1652 trade, long seed) {
        net.minecraft.class_1646 villager = new net.minecraft.class_1646(class_1299.field_6077, level);
        try {
            net.minecraft.class_1914 offer = trade.method_7246(villager, net.minecraft.class_5819.method_43049(seed));
            if (offer == null || offer.method_8250().method_7909() != class_1802.field_8598) {
                return "none";
            }
            net.minecraft.class_9304 e = net.minecraft.class_1890.method_57532(offer.method_8250());
            StringBuilder out = new StringBuilder();
            for (net.minecraft.class_6880<net.minecraft.class_1887> h : e.method_57534()) {
                net.minecraft.class_1887 en = h.comp_349();
                out.append(net.minecraft.class_7923.field_41176.method_10221(en)).append(':').append(e.method_57536(en)).append(' ');
            }
            return out.toString().trim();
        } finally {
            villager.method_31472();
        }
    }

    public static void run(net.minecraft.server.MinecraftServer server) {
        System.out.println("CB-SMX6 start");
        class_3218 level = server.method_30002();
        try {
            // ---- report comments: CrashCommentMixin / ProfileCommentMixin (1.21: ReportType.getErrorComment routed through
            // the restored CrashReport.getErrorComment / FilledProfileResults.getComment)
            forceComment = false;
            crashCommentAsked = 0;
            String crash = new net.minecraft.class_128("cb-smx6 test", new RuntimeException("cb-smx6")).method_568();
            check("comment-passive", crashCommentAsked == 1 && !crash.contains("cb-smx6 witty"),
                "crash report comment asked " + crashCommentAsked + " time(s): " + commentLine(crash));
            forceComment = true;
            crashCommentAsked = 0;
            crash = new net.minecraft.class_128("cb-smx6 test", new RuntimeException("cb-smx6")).method_568();
            check("comment-forced", crashCommentAsked == 1 && crash.contains("\n// cb-smx6 witty crash\n"),
                "mod's comment: " + commentLine(crash));
            profileCommentAsked = 0;
            String profile = ((cb.smx6.mixin.ProfileReportInvoker) new net.minecraft.class_3692(java.util.Map.of(), 0L, 0, 1000000L, 1))
                .cbsmx6$report(1000000L, 1);
            forceComment = false;
            check("profile-forced", profileCommentAsked == 1 && profile.contains("\n// cb-smx6 witty profile\n"),
                "profiler report comment asked " + profileCommentAsked + " time(s): " + commentLine(profile));
            // ---- FollowOwnerGoal teleport helpers: FollowOwnerMixin (1.21: routed through the restored 1.20.6 members)
            blockFollowTeleport = false;
            followCanAsked = 0;
            followMoved = 0;
            System.out.println("CB-SMX6 follow-begin passive");
            double near = follow(server, level, -8);
            System.out.println("CB-SMX6 follow-end passive");
            check("follow-passive", near >= 0 && near < 5.0 && followCanAsked == 1 && followMoved == 1,
                "tamed wolf 20 blocks from its owner, one goal tick: distance " + near + ", canTeleportTo asked " + followCanAsked
                + " time(s), moved " + followMoved);
            blockFollowTeleport = true;
            followCanAsked = 0;
            followMoved = 0;
            System.out.println("CB-SMX6 follow-begin blocked");
            double far = follow(server, level, 8);
            System.out.println("CB-SMX6 follow-end blocked");
            blockFollowTeleport = false;
            check("follow-blocked", far > 15.0 && followCanAsked > 0 && followMoved == 0,
                "canTeleportTo refused by the mod: distance " + far + ", asked " + followCanAsked + " time(s), moved " + followMoved);
            {
                net.minecraft.class_1493 wolf = class_1299.field_6055.method_5883(level);
                cb.smx6.mixin.FollowOwnerAccess g0 = (cb.smx6.mixin.FollowOwnerAccess) new net.minecraft.class_1350(wolf, 1.0, 10.0F, 2.0F, false);
                cb.smx6.mixin.FollowOwnerAccess g1 = (cb.smx6.mixin.FollowOwnerAccess) new net.minecraft.class_1350(wolf, 1.0, 10.0F, 2.0F, true);
                check("follow-fields", g0.cb$level() == level && !g0.cb$canFly() && g1.cb$canFly()
                    && cb.smx6.mixin.FollowOwnerAccess.cb$maxHorizontal() == 3,
                    "level " + (g0.cb$level() == level) + ", canFly false/true goals " + g0.cb$canFly() + "/" + g1.cb$canFly()
                    + ", MAX_HORIZONTAL " + cb.smx6.mixin.FollowOwnerAccess.cb$maxHorizontal());
                wolf.method_31472();
            }
            // ---- Bad Omen raid start: BadOmenMixin (1.21: Raid Omen's createOrExtendRaid routed through the restored
            // BadOmenMobEffect.method_58613)
            blockRaid = false;
            badOmenAsked = 0;
            badOmenRaids = 0;
            boolean raided = raid(server, level, 1000);
            check("badomen-passive", raided && badOmenAsked == 1 && badOmenRaids == 1,
                "Raid Omen ending at a village block: raid " + raided + ", method_58613 asked " + badOmenAsked + " time(s), raids started "
                + badOmenRaids);
            blockRaid = true;
            badOmenAsked = 0;
            badOmenRaids = 0;
            raided = raid(server, level, 2000);
            blockRaid = false;
            check("badomen-blocked", !raided && badOmenAsked == 1 && badOmenRaids == 0,
                "raid refused by the mod: raid " + raided + ", asked " + badOmenAsked + " time(s), raids started " + badOmenRaids);
            // ---- Mob leash save state: LeashSaveMixin / LeashFieldAccessor (1.21: the fields are mirrors of LeashData, and
            // Leashable.tickLeash's restoreLeashFromSave routed through the restored Mob.restoreLeashFromSave)
            blockLeashRestore = false;
            leashRestoreAsked = 0;
            leashRestored = 0;
            String leashPassive = leash(level, 1200, false);
            check("leash-passive", leashPassive.startsWith("knot/null/1/"),
                "pig whose delayedLeashInfo (a fence position) was set through the accessor, one tick: "
                + leashPassive + " (holder/delayedLeashInfo after/asked/restored)");
            // the INVOKE anchor on setLeashedTo (1.21's renamed method_60964) inside the restored member: a known engine gap
            // (cases_smixin3/s121.md C8), reported apart from the pass / fail count
            known("leash-anchor", leashPassive.endsWith("/1"), "INVOKE setLeashedTo injector in restoreLeashFromSave fired "
                + leashPassive.substring(leashPassive.lastIndexOf('/') + 1) + " time(s) (C8: renamed anchors in restored members)");
            blockLeashRestore = true;
            leashRestoreAsked = 0;
            leashRestored = 0;
            String blocked = leash(level, 1300, true);
            blockLeashRestore = false;
            check("leash-blocked", "none/pos/1/0".equals(blocked),
                "restore refused by the mod: " + blocked + " (holder/delayedLeashInfo after/asked/restored)");
            // ---- TextComponentTagVisitor nested visits: TagVisitorMixin (1.21: visitList / visitCompound routed through the
            // restored method_59850)
            foldNested = false;
            nestedAsked = 0;
            nestedVisits = 0;
            String text = tagText();
            check("tagvisit-passive", text.equals("{a: {b: 1}}") && nestedAsked == 2 && nestedVisits == 2,
                "{a:{b:1}} as text: " + text + ", method_59850 asked " + nestedAsked + " time(s), nested visits " + nestedVisits);
            foldNested = true;
            nestedAsked = 0;
            nestedVisits = 0;
            text = tagText();
            foldNested = false;
            check("tagvisit-folded", text.equals("{a: <cb>}") && nestedAsked == 1 && nestedVisits == 0,
                "the mod answers the nested tag: " + text + ", asked " + nestedAsked + " time(s), nested visits " + nestedVisits);
            level.method_8519(1.0F);
            level.method_8496(1.0F);
            check("thunderstorm", level.method_8546(), "isThundering after rain/thunder level 1");

            cancelChanneling = false;
            asked = 0;
            int n = hit(level, 0, true);
            check("vanilla-strike", n == 1, "Channeling trident, mixin passive: " + n + " bolt(s), isChanneling asked " + asked + " time(s)");
            check("vanilla-asked-once", asked == 1, "isChanneling asked " + asked + " time(s) for one hit (1.20.6: once)");

            cancelChanneling = true;
            asked = 0;
            n = hit(level, 16, true);
            check("cancelled-no-strike", n == 0, "Channeling trident, isChanneling cancelled to false: " + n + " bolt(s), asked " + asked);

            cancelChanneling = false;
            asked = 0;
            n = hit(level, -16, false);
            check("plain-no-strike", n == 0, "trident without Channeling: " + n + " bolt(s)");

            level.method_8519(0.0F);
            level.method_8496(0.0F);
            asked = 0;
            n = hit(level, 32, true);
            check("clear-not-asked", n == 0 && asked == 0, "clear weather: " + n + " bolt(s), isChanneling asked " + asked + " time(s) (1.20.6: not asked)");
            // ---- the librarian's enchanted book: VillagerBookMixin on the 1.20.6 pick
            long[] seeds = {1L, 2L, 3L, 4L, 5L, 42L};
            forceMending = false;
            bookAsked = 0;
            String passive = books(level, seeds);
            check("book-passive", bookAsked == seeds.length && !passive.contains("?") && !passive.contains(":0") && !passive.contains(":-1"),
                "mixin passive, pick asked " + bookAsked + " time(s): " + passive);
            System.out.println("CB-SMX6 books " + passive);
            forceMending = true;
            bookAsked = 0;
            net.minecraft.class_1646 villager = new net.minecraft.class_1646(class_1299.field_6077, level);
            int mending = 0;
            int bookOffers = 0;
            for (net.minecraft.class_3853.class_1652 t : net.minecraft.class_3853.field_17067.get(net.minecraft.class_3852.field_17060).get(1)) {
                net.minecraft.class_1914 offer = t.method_7246(villager, net.minecraft.class_5819.method_43049(7L));
                if (offer != null && offer.method_8250().method_7909() == class_1802.field_8598) {
                    bookOffers++;
                    if (net.minecraft.class_1890.method_57532(offer.method_8250()).method_57536(class_1893.field_9101) > 0) {
                        mending++;
                    }
                }
            }
            villager.method_31472();
            forceMending = false;
            check("book-forced-mending", bookOffers == 1 && mending == 1, "pick forced to Mending: " + mending + " of " + bookOffers
                + " book offer(s) carry Mending, pick asked " + bookAsked);
            // ---- MobEffect.AttributeTemplate.create(name, amplifier): EffectTemplateMixin
            doubleEffect = false;
            templateAsked = 0;
            templateName = null;
            double plain = speed(level);
            check("effect-passive", templateAsked == 1 && "effect.minecraft.speed".equals(templateName) && templateAmplifier == 0
                && Math.abs(plain - 0.3) < 1e-6, "Speed I on a pig: speed " + plain + ", create asked " + templateAsked
                + " time(s) with name=" + templateName + " amplifier=" + templateAmplifier);
            doubleEffect = true;
            templateAsked = 0;
            double doubled = speed(level);
            doubleEffect = false;
            check("effect-doubled", templateAsked == 1 && Math.abs(doubled - 0.35) < 1e-6,
                "modifier amount doubled by the mod: speed " + doubled + " (passive " + plain + "), create asked " + templateAsked);
            // ---- OozingMobEffect.numberOfSlimesToSpawn: OozingMixin
            noSlimes = false;
            slimesAsked = 0;
            int slimes = oozing(level, 0);
            check("oozing-passive", slimes == 2 && slimesAsked == 1 && slimesCramming == 24 && slimesSpawnCount == 2,
                "Oozing zombie killed: " + slimes + " slime(s), asked " + slimesAsked + " time(s) with maxEntityCramming=" + slimesCramming
                + " spawnCount=" + slimesSpawnCount);
            noSlimes = true;
            slimesAsked = 0;
            slimes = oozing(level, 8);
            noSlimes = false;
            check("oozing-none", slimes == 0 && slimesAsked == 1, "mod answers 0: " + slimes + " slime(s), asked " + slimesAsked);
            // ---- RecipeCache.Entry.matches(List): RecipeCacheEntryMixin
            forceMatch = false;
            net.minecraft.class_8884 cache = new net.minecraft.class_8884(1);
            String first = recipe(cache, level, class_1802.field_8583);
            matchesAsked = 0;
            matchesSize = -1;
            String again = recipe(cache, level, class_1802.field_8583);
            int againAsked = matchesAsked;
            int againSize = matchesSize;
            String stone = recipe(cache, level, class_1802.field_20391);
            check("recipe-passive", "minecraft:oak_planks".equals(first) && "minecraft:oak_planks".equals(again) && againAsked == 1
                && "minecraft:stone_button".equals(stone), "log " + first + ", log again " + again + " (matches asked " + againAsked
                + ", list size " + againSize + "), stone " + stone);
            cache = new net.minecraft.class_8884(1);
            recipe(cache, level, class_1802.field_8583);
            forceMatch = true;
            matchesAsked = 0;
            String forced = recipe(cache, level, class_1802.field_20391);
            forceMatch = false;
            check("recipe-forced", "minecraft:oak_planks".equals(forced) && matchesAsked == 1,
                "matches forced true: stone answered " + forced + " from the cache, matches asked " + matchesAsked);
            // ---- Util.OS.getOpenUrlArguments(URL): UtilOsMixin (answers "true", so nothing opens)
            openAsked = 0;
            openUrl = null;
            net.minecraft.class_156.method_668().method_673(new java.net.URI("https://example.com/cb-smx6?q=1"));
            check("open-url", openAsked == 1 && "https://example.com/cb-smx6?q=1".equals(openUrl),
                "Util.getPlatform().openUri: getOpenUrlArguments asked " + openAsked + " time(s) with " + openUrl);
            // ---- EnchantBookForEmeralds constructors through a constructor invoker: EnchantBookInvoker
            String listed = bookOf(level, cb.smx6.mixin.EnchantBookInvoker.cbsmx6$newListed(1, class_1893.field_9099), 3L);
            check("trade-listed", listed.startsWith("minecraft:silk_touch:"), "(xp, silk_touch): book " + listed);
            String ranged = bookOf(level, cb.smx6.mixin.EnchantBookInvoker.cbsmx6$newRanged(1, 0, 1, class_1893.field_9101, class_1893.field_9099), 5L);
            check("trade-ranged", ranged.equals("minecraft:mending:1") || ranged.equals("minecraft:silk_touch:1"),
                "(xp, 0, 1, mending, silk_touch): book " + ranged);
            String any = bookOf(level, cb.smx6.mixin.EnchantBookInvoker.cbsmx6$new(1), 1L);
            check("trade-tradeable", !any.equals("none") && !any.isEmpty(), "(xp): book " + any);
        } catch (Throwable t) {
            check("exception", false, String.valueOf(t));
            t.printStackTrace();
        } finally {
            cancelChanneling = false;
            forceMending = false;
            doubleEffect = false;
            noSlimes = false;
            forceMatch = false;
            blockFollowTeleport = false;
            forceComment = false;
            foldNested = false;
        }
        System.out.println("CB-SMX6 summary ok=" + ok + " fail=" + fail);
    }

    /** a report's comment line ("// ..."), the first one */
    private static String commentLine(String report) {
        for (String line : report.split("\n")) {
            if (line.startsWith("// ")) {
                return line;
            }
        }
        return "(no comment line)";
    }

    /**
     * a server player (no connection) at a village block high above the terrain dx blocks east of spawn, whose Raid Omen
     * runs out there: RAID_OMEN's applyEffectTick with its Raid Omen position set at that block, as 1.21's Bad Omen sets it
     * on entering a village. The block is a village by a meeting-point POI added there (a bell's POI, added the way
     * ServerLevel adds it on the next server task) with a villager's claim on it. Answers whether a raid stands at the block
     * afterwards (it is stopped).
     */
    private static boolean raid(net.minecraft.server.MinecraftServer server, class_3218 level, int dx) {
        class_2338 spawn = level.method_43126();
        class_2338 at = new class_2338(spawn.method_10263() + dx, 300, spawn.method_10260());
        level.method_8501(at.method_10074(), net.minecraft.class_2246.field_10340.method_9564());
        level.method_19494().method_19115(at, net.minecraft.class_7923.field_41128.method_40290(net.minecraft.class_7477.field_39292));
        // a villager's claim (PoiManager.take): ServerLevel.isVillage counts occupied village POIs only
        level.method_19494().method_19126(h -> true, (h, p) -> p.equals(at), at, 1);
        if (followOwner == null) {
            followOwner = new net.minecraft.class_3222(server, level,
                new com.mojang.authlib.GameProfile(java.util.UUID.fromString("0c0b5121-0000-4000-8000-00000000c0b5"), "cbsmx6owner"),
                net.minecraft.class_8791.method_53821());
        }
        followOwner.method_5808(at.method_10263() + 0.5, at.method_10264(), at.method_10260() + 0.5, 0.0F, 0.0F);
        followOwner.method_58586(at);
        net.minecraft.class_1294.field_50117.comp_349().method_5572(followOwner, 0);
        net.minecraft.class_3765 r = level.method_19502(at);
        if (r != null) {
            r.method_16506();
        }
        return r != null;
    }

    /**
     * a pig on a stone block high above the terrain dx blocks east of spawn, an oak fence beside it, whose saved leash
     * (delayedLeashInfo = the fence's position) is set through the mod's accessor, or its @Shadow field when viaShadow; one
     * tick (Mob.tick ticks the leash). Answers "holder/delayedLeashInfo/asked/restored": the holder as the accessor reads it
     * ("knot", "none", or "mismatch" when the accessor, the @Shadow field and getLeashHolder disagree), delayedLeashInfo as the
     * accessor reads it afterwards ("null", "pos", "other"), and the restore counters
     */
    private static String leash(class_3218 level, int dx, boolean viaShadow) {
        class_2338 spawn = level.method_43126();
        class_2338 at = new class_2338(spawn.method_10263() + dx, 300, spawn.method_10260());
        class_2338 fence = at.method_10069(2, 0, 0);
        level.method_8501(at.method_10074(), net.minecraft.class_2246.field_10340.method_9564());
        level.method_8501(fence, net.minecraft.class_2246.field_10620.method_9564());
        net.minecraft.class_1452 pig = class_1299.field_6093.method_5883(level);
        pig.method_5808(at.method_10263() + 0.5, at.method_10264(), at.method_10260() + 0.5, 0.0F, 0.0F);
        level.method_8649(pig);
        com.mojang.datafixers.util.Either<java.util.UUID, class_2338> saved = com.mojang.datafixers.util.Either.right(fence);
        if (viaShadow) {
            ((cb.smx6.LeashSaveAccess) pig).cbsmx6$shadowSetDelayed(saved);
        } else {
            ((cb.smx6.mixin.LeashFieldAccessor) pig).cbsmx6$setDelayed(saved);
        }
        boolean readBack = ((cb.smx6.mixin.LeashFieldAccessor) pig).cbsmx6$delayed() == saved
            && ((cb.smx6.LeashSaveAccess) pig).cbsmx6$shadowDelayed() == saved;
        pig.method_5773();
        net.minecraft.class_1297 holder = ((cb.smx6.mixin.LeashFieldAccessor) pig).cbsmx6$holder();
        String h = holder != ((cb.smx6.LeashSaveAccess) pig).cbsmx6$shadowHolder() || holder != pig.method_5933()
            || (holder != null) != pig.method_5934() ? "mismatch"
            : holder == null ? "none" : holder instanceof net.minecraft.class_1532 ? "knot" : holder.getClass().getSimpleName();
        com.mojang.datafixers.util.Either<java.util.UUID, class_2338> after = ((cb.smx6.mixin.LeashFieldAccessor) pig).cbsmx6$delayed();
        String d = after == null ? "null" : after.right().isPresent() && after.right().get().equals(fence) ? "pos" : "other";
        if (holder != null) {
            holder.method_31472();
        }
        pig.method_31472();
        return (readBack ? "" : "unread:") + h + "/" + d + "/" + leashRestoreAsked + "/" + leashRestored;
    }

    /** {a:{b:1}} through TextComponentTagVisitor("") as a string */
    private static String tagText() {
        net.minecraft.class_2487 inner = new net.minecraft.class_2487();
        inner.method_10569("b", 1);
        net.minecraft.class_2487 outer = new net.minecraft.class_2487();
        outer.method_10566("a", inner);
        return new net.minecraft.class_5628("").method_32305(outer).getString();
    }

    /**
     * a wolf tamed by a server player (in the level's player list, no connection) 20 blocks east of it at the surface, and one
     * canUse / start / tick of a 1.20.6-made FollowOwnerGoal(wolf, 1.0, 10, 2, false): the wolf's distance to its owner
     * afterwards (-1 when the goal did not start). 1.20.6 teleports a wolf 12 or more blocks away next to its owner.
     */
    private static double follow(net.minecraft.server.MinecraftServer server, class_3218 level, int dx) {
        class_2338 spawn = level.method_43126();
        // a stone floor high above the terrain (the world's seed is random): owner and wolf stand on it, and every spot
        // the teleport tries around the owner (3 blocks out, 1 up or down) is walkable floor or open air
        class_2338 at = new class_2338(spawn.method_10263() + dx, 300, spawn.method_10260() - 20);
        for (int x = -5; x <= 25; x++) {
            for (int z = -5; z <= 5; z++) {
                level.method_8501(at.method_10069(x, -1, z), net.minecraft.class_2246.field_10340.method_9564());
            }
        }
        if (followOwner == null) {
            followOwner = new net.minecraft.class_3222(server, level,
                new com.mojang.authlib.GameProfile(java.util.UUID.fromString("0c0b5121-0000-4000-8000-00000000c0b5"), "cbsmx6owner"),
                net.minecraft.class_8791.method_53821());
        }
        // in the level's player list only (getOwner looks the owner up there), not in its entity / chunk tracking:
        // a player without a connection must not be sent packets
        level.method_18456().add(followOwner);
        followOwner.method_5808(at.method_10263() + 0.5, at.method_10264(), at.method_10260() + 0.5, 0.0F, 0.0F);
        class_2338 wp = at.method_10069(20, 0, 0);
        net.minecraft.class_1493 wolf = class_1299.field_6055.method_5883(level);
        wolf.method_5808(wp.method_10263() + 0.5, wp.method_10264(), wp.method_10260() + 0.5, 0.0F, 0.0F);
        level.method_8649(wolf);
        wolf.method_6170(followOwner);
        wolf.method_24346(false);
        // deterministic teleport target: the wolf's random seeded so that the first try (1.20.6's draw order dx, dy, dz;
        // 1.21's dx, dz, dy) is a free floor spot 2-3 blocks from the owner, even if up to three draws happen before it
        // (Java LCG simulated for both orders, s121d). Owner, wolf and platform stand at fixed offsets on an isolated floor.
        wolf.method_6051().method_43052(1315L);
        net.minecraft.class_1350 goal = new net.minecraft.class_1350(wolf, 1.0, 10.0F, 2.0F, false);
        double d = -1.0;
        if (goal.method_6264()) {
            goal.method_6269();
            goal.method_6268();
            d = Math.sqrt(wolf.method_5858(followOwner));
        }
        wolf.method_31472();
        level.method_18456().remove(followOwner);
        return d;
    }
}
