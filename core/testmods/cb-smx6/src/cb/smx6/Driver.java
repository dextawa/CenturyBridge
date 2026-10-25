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

    public static void run(net.minecraft.server.MinecraftServer server) {
        System.out.println("CB-SMX6 start");
        class_3218 level = server.method_30002();
        try {
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
        } catch (Throwable t) {
            check("exception", false, String.valueOf(t));
            t.printStackTrace();
        } finally {
            cancelChanneling = false;
            forceMending = false;
            doubleEffect = false;
            noSlimes = false;
            forceMatch = false;
        }
        System.out.println("CB-SMX6 summary ok=" + ok + " fail=" + fail);
    }
}
