package cb.smx6v;

import java.util.List;
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
import net.minecraft.class_7924;

/**
 * cb-smx6's driver written against 1.21 (the same cows, armor-stand owners, positions and cases), with no mixin on
 * channeling: the bolts it counts are what 1.21 does, with or without the step layer installed.
 */
public final class Driver {
    private static int ok;
    private static int fail;

    private Driver() {
    }

    /** a report's comment ("// " stripped), the first comment line */
    private static String commentOf(String report) {
        for (String line : report.split("\n")) {
            if (line.startsWith("// ")) {
                return line.substring(3);
            }
        }
        return "(no comment line)";
    }

    private static void report(String c, boolean cond, String detail) {
        if (cond) {
            ok++;
        } else {
            fail++;
        }
        System.out.println("CB-SMX6V " + (cond ? "ok " : "FAIL ") + c + " " + detail);
    }

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
            stack.method_7978(level.method_30349().method_30530(class_7924.field_41265).method_40290(class_1893.field_9117), 1);
        }
        class_1685 trident = new class_1685(level, owner, stack);
        ((cb.smx6v.mixin.TridentAccess) trident).cbsmx6v$onHitEntity(new class_3966(cow));
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

    /** cb-smx6's books(): the librarian's level-1 enchanted-book offer per seed, "seed=key:level" off the book */
    private static String books(class_3218 level, long[] seeds) {
        net.minecraft.class_1646 villager = new net.minecraft.class_1646(class_1299.field_6077, level);
        net.minecraft.class_3853.class_1652[] trades = net.minecraft.class_3853.field_17067.get(net.minecraft.class_3852.field_17060).get(1);
        StringBuilder out = new StringBuilder();
        for (long seed : seeds) {
            for (net.minecraft.class_3853.class_1652 t : trades) {
                net.minecraft.class_1914 offer = t.method_7246(villager, net.minecraft.class_5819.method_43049(seed));
                if (offer == null || offer.method_8250().method_7909() != class_1802.field_8598) {
                    continue;
                }
                net.minecraft.class_9304 e = net.minecraft.class_1890.method_57532(offer.method_8250());
                for (net.minecraft.class_6880<net.minecraft.class_1887> h : e.method_57534()) {
                    out.append(seed).append('=').append(h.method_40230().get().method_29177()).append(':').append(e.method_57536(h)).append(' ');
                }
            }
        }
        villager.method_31472();
        return out.toString().trim();
    }

    /** cb-smx6's speed(): a pig's movement speed with Speed (amplifier 0) applied through LivingEntity.addEffect */
    private static double speed(class_3218 level) {
        net.minecraft.class_1452 pig = net.minecraft.class_1299.field_6093.method_5883(level);
        net.minecraft.class_2338 spawn = level.method_43126();
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

    /** cb-smx6's recipe(): the recipe a RecipeCache answers for a 3x3 grid holding one item in its first slot */
    private static String recipe(net.minecraft.class_8884 cache, class_3218 level, net.minecraft.class_1792 item) {
        java.util.List<class_1799> items = new java.util.ArrayList<>(java.util.Collections.nCopies(9, class_1799.field_8037));
        items.set(0, new class_1799(item));
        java.util.Optional<net.minecraft.class_8786<net.minecraft.class_3955>> r = cache.method_54470(level, net.minecraft.class_9694.method_59986(3, 3, items));
        return r.isPresent() ? String.valueOf(r.get().comp_1932()) : "none";
    }

    /** native 1.21 mixin TamableTeleportMixin: calls of TamableAnimal.tryToTeleportToOwner seen */
    public static volatile int nativeTeleports;
    public static volatile int nativeDimFired;
    public static volatile net.minecraft.class_3218 nativeDimOld;
    public static volatile net.minecraft.class_3218 nativeDimNew;
    /** native 1.21 mixin FollowOwnerWrapMixin: FollowOwnerGoal's tryToTeleportToOwner / unableToMoveToOwner calls wrapped */
    public static volatile int nativeTeleportWraps;
    public static volatile int nativeUnableWraps;
    /** native 1.21 mixin ReportCommentMixin: ReportType.getErrorComment answers seen, the last one */
    public static volatile int nativeComments;
    public static volatile String nativeLastComment;
    /** native 1.21 mixin RaidOmenWrapMixin: Raid Omen's createOrExtendRaid calls wrapped */
    public static volatile int nativeRaidWraps;
    /** native 1.21 mixin LeashRestoreWrapMixin: tickLeash's restoreLeashFromSave calls wrapped */
    public static volatile int nativeLeashRestoreWraps;
    /** native 1.21 mixin TagVisitorWrapMixin: visitCompound's nested visits wrapped */
    public static volatile int nativeNestedWraps;

    /**
     * a pig high above the terrain beside an oak fence, its leash saved as the fence's position (setLeashData with a saved
     * LeashData, as 1.21's readLeashData makes it), one tick: "knot" when it is leashed to a fence knot afterwards, else "none"
     */
    private static String leash(class_3218 level, int dx) {
        class_2338 spawn = level.method_43126();
        class_2338 at = new class_2338(spawn.method_10263() + dx, 300, spawn.method_10260() + 64);
        class_2338 fence = at.method_10069(2, 0, 0);
        level.method_8501(at.method_10074(), net.minecraft.class_2246.field_10340.method_9564());
        level.method_8501(fence, net.minecraft.class_2246.field_10620.method_9564());
        net.minecraft.class_1452 pig = net.minecraft.class_1299.field_6093.method_5883(level);
        pig.method_5808(at.method_10263() + 0.5, at.method_10264(), at.method_10260() + 0.5, 0.0F, 0.0F);
        level.method_8649(pig);
        pig.method_60960(cb.smx6v.mixin.LeashDataNew.cbsmx6v$saved(com.mojang.datafixers.util.Either.right(fence)));
        pig.method_5773();
        net.minecraft.class_1297 holder = pig.method_60952();
        if (holder != null) {
            holder.method_31472();
        }
        pig.method_31472();
        return holder instanceof net.minecraft.class_1532 ? "knot" : holder == null ? "none" : holder.getClass().getSimpleName();
    }
    private static net.minecraft.class_3222 followOwner;

    /**
     * cb-smx6's raid() on 1.21's API: a server player (no connection) at a village block (meeting-point POI) whose Raid Omen
     * runs out there; whether a raid stands at the block afterwards (it is stopped)
     */
    private static boolean raid(net.minecraft.server.MinecraftServer server, class_3218 level, int dx) {
        class_2338 spawn = level.method_43126();
        class_2338 at = new class_2338(spawn.method_10263() + dx, 300, spawn.method_10260() + 64);
        level.method_8501(at.method_10074(), net.minecraft.class_2246.field_10340.method_9564());
        level.method_19494().method_19115(at, net.minecraft.class_7923.field_41128.method_40290(net.minecraft.class_7477.field_39292));
        // a villager's claim (PoiManager.take): ServerLevel.isVillage counts occupied village POIs only
        level.method_19494().method_19126(h -> true, (h, p) -> p.equals(at), at, 1);
        if (followOwner == null) {
            followOwner = new net.minecraft.class_3222(server, level,
                new com.mojang.authlib.GameProfile(java.util.UUID.fromString("0c0b5121-0000-4000-8000-00000000c0b6"), "cbsmx6vowner"),
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
     * cb-smx6's follow() on 1.21's API: a wolf tamed by a server player (in the level's player list, no connection) 20 blocks east
     * of it, one canUse / start / tick of FollowOwnerGoal(wolf, 1.0, 10, 2); the wolf's distance to its owner afterwards
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
                new com.mojang.authlib.GameProfile(java.util.UUID.fromString("0c0b5121-0000-4000-8000-00000000c0b6"), "cbsmx6vowner"),
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
        wolf.method_59922().method_43052(1315L);
        net.minecraft.class_1350 goal = new net.minecraft.class_1350(wolf, 1.0, 10.0F, 2.0F);
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

    public static void run(net.minecraft.server.MinecraftServer server) {
        boolean layer = net.fabricmc.loader.api.FabricLoader.getInstance().isModLoaded("centurybridge")
            || net.fabricmc.loader.api.FabricLoader.getInstance().getAllMods().stream()
                .anyMatch(m -> m.getMetadata().getId().startsWith("cb-step") || m.getMetadata().getId().startsWith("cb_step")
                    || m.getMetadata().getId().startsWith("centurybridge"));
        String tag = layer ? "layer" : "nolayer";
        System.out.println("CB-SMX6V start " + tag);
        class_3218 level = server.method_30002();
        try {
            level.method_8519(1.0F);
            level.method_8496(1.0F);
            report(tag + " thunderstorm", level.method_8546(), "isThundering after rain/thunder level 1");
            int n = hit(level, 0, true);
            report(tag + " channeling-strike", n == 1, "Channeling trident: " + n + " bolt(s) per hit");
            n = hit(level, -16, false);
            report(tag + " plain-no-strike", n == 0, "trident without Channeling: " + n + " bolt(s)");
            level.method_8519(0.0F);
            level.method_8496(0.0F);
            n = hit(level, 32, true);
            report(tag + " clear-no-strike", n == 0, "clear weather: " + n + " bolt(s)");
            String books = books(level, new long[] {1L, 2L, 3L, 4L, 5L, 42L});
            report(tag + " books", !books.isEmpty(), books);
            System.out.println("CB-SMX6V books " + tag + " " + books);
            double sp = speed(level);
            report(tag + " effect-speed", Math.abs(sp - 0.3) < 1e-6, "Speed I on a pig: speed " + sp);
            System.out.println("CB-SMX6V speed " + tag + " " + sp);
            int slimes = oozing(level, 0);
            report(tag + " oozing", slimes == 2, "Oozing zombie killed: " + slimes + " slime(s)");
            System.out.println("CB-SMX6V oozing " + tag + " " + slimes);
            net.minecraft.class_8884 cache = new net.minecraft.class_8884(1);
            String recipes = recipe(cache, level, class_1802.field_8583) + " " + recipe(cache, level, class_1802.field_8583) + " "
                + recipe(cache, level, class_1802.field_20391);
            report(tag + " recipes", recipes.equals("minecraft:oak_planks minecraft:oak_planks minecraft:stone_button"), recipes);
            System.out.println("CB-SMX6V recipes " + tag + " " + recipes);
            nativeTeleports = 0;
            nativeTeleportWraps = 0;
            nativeUnableWraps = 0;
            double d = follow(server, level, 24);
            report(tag + " follow", d >= 0 && d < 5.0, "tamed wolf 20 blocks from its owner, one FollowOwnerGoal tick: distance " + d);
            report(tag + " follow-wraps", nativeTeleportWraps == 1 && nativeUnableWraps == 1,
                "native @WrapOperation on the goal's calls: teleport " + nativeTeleportWraps + ", unable " + nativeUnableWraps);
            System.out.println("CB-SMX6V follow " + tag + " distance=" + d + " nativeTryToTeleportToOwner=" + nativeTeleports
                + " nativeTeleportWraps=" + nativeTeleportWraps + " nativeUnableWraps=" + nativeUnableWraps);
            // report comments: 1.21's ReportType lists (with the layer's route installed, unmixed: untouched); the native
            // RETURN injector on ReportType.getErrorComment sees each answer
            nativeComments = 0;
            String crash = new net.minecraft.class_128("cb-smx6v test", new RuntimeException("cb-smx6v")).method_60920(net.minecraft.class_9813.field_52181);
            String cc = commentOf(crash);
            report(tag + " crash-comment", net.minecraft.class_9813.field_52181.comp_2857().contains(cc) && nativeComments == 1 && cc.equals(nativeLastComment),
                "crash report comment: " + cc + "; native RETURN injector saw " + nativeComments + " answer(s), last " + nativeLastComment);
            nativeComments = 0;
            String prof = net.minecraft.class_9813.field_52182.method_60927();
            report(tag + " profile-comment", net.minecraft.class_9813.field_52182.comp_2857().contains(prof) && nativeComments == 1 && prof.equals(nativeLastComment),
                "profile comment: " + prof + "; native RETURN injector saw " + nativeComments + " answer(s)");
            // Raid Omen running out at a village block: 1.21 starts the raid; the native wrap of its createOrExtendRaid call
            // runs once (with the layer, unmixed: 1.21's call; with cb-smx6 mixing method_58613: around the routed call)
            nativeRaidWraps = 0;
            boolean raided = raid(server, level, 3000);
            report(tag + " raid-omen", raided && nativeRaidWraps == 1,
                "Raid Omen ending at a village block: raid " + raided + ", native @WrapOperation of createOrExtendRaid ran " + nativeRaidWraps);
            // a pig's saved leash restored by tickLeash: 1.21's restoreLeashFromSave (with the layer, unmixed: untouched; with
            // cb-smx6 mixing Mob.restoreLeashFromSave: the routed restored body), the native wrap of the call runs once
            nativeLeashRestoreWraps = 0;
            String leashed = leash(level, 3200);
            report(tag + " leash-restore", leashed.equals("knot") && nativeLeashRestoreWraps == 1,
                "pig saved leashed to a fence, one tick: " + leashed + ", native @WrapOperation of restoreLeashFromSave ran " + nativeLeashRestoreWraps);
            // {a:{b:1}} as text: 1.21's in-place nested visits (with the layer, unmixed: untouched; with cb-smx6 mixing
            // method_59850: the routed restored visit); the native wrap of visitCompound's nested visit runs once per compound value
            nativeNestedWraps = 0;
            net.minecraft.class_2487 inner = new net.minecraft.class_2487();
            inner.method_10569("b", 1);
            net.minecraft.class_2487 outer = new net.minecraft.class_2487();
            outer.method_10566("a", inner);
            String text = new net.minecraft.class_5628("").method_32305(outer).getString();
            report(tag + " tagvisit", text.equals("{a: {b: 1}}") && nativeNestedWraps == 2,
                "{a:{b:1}} as text: " + text + ", native @WrapOperation of the nested visit ran " + nativeNestedWraps);
            // native code sees 1.21's own root: no minecraft:enchantment (a datapack registry), minecraft:item as ITEM
            net.minecraft.class_2960 enchId = net.minecraft.class_2960.method_60656("enchantment");
            report(tag + " root-native", net.minecraft.class_7923.field_41167.method_17966(enchId).isEmpty()
                    && !net.minecraft.class_7923.field_41167.method_10250(enchId)
                    && !net.minecraft.class_7923.field_41167.method_10235().contains(enchId)
                    && net.minecraft.class_7923.field_41167.method_17966(net.minecraft.class_2960.method_60656("painting_variant")).isEmpty()
                && net.minecraft.class_7923.field_41167.method_17966(net.minecraft.class_2960.method_60656("item")).orElse(null) == net.minecraft.class_7923.field_41178,
                "native REGISTRY minecraft:enchantment present " + net.minecraft.class_7923.field_41167.method_17966(enchId).isPresent()
                    + ", containsKey " + net.minecraft.class_7923.field_41167.method_10250(enchId));
            // a player's cross-level moves: native @Inject + @Local in changeDimension(transition) before sendAllPlayerInfo
            // fires once per move, teleportTo-driven or a direct (portal-type) changeDimension
            class_3218 end = server.method_3847(net.minecraft.class_1937.field_25181);
            com.mojang.authlib.GameProfile gp = new com.mojang.authlib.GameProfile(
                java.util.UUID.fromString("0c0b5121-0000-4000-8000-00000000e8a7"), "cbsmx6vtele");
            net.minecraft.class_3222 tp = new net.minecraft.class_3222(server, level, gp, net.minecraft.class_8791.method_53821());
            new net.minecraft.class_3244(server, new net.minecraft.class_2535(net.minecraft.class_2598.field_11941), tp,
                net.minecraft.class_8792.method_53824(gp, false));
            nativeDimFired = 0;
            nativeDimOld = null;
            nativeDimNew = null;
            tp.method_14251(end, 0.5, 100.0, 0.5, 0.0F, 0.0F);
            report(tag + " dim-local-teleport", nativeDimFired == 1 && nativeDimOld == level && nativeDimNew == end,
                "teleportTo overworld -> end: native changeDimension @Local injector fired " + nativeDimFired + ", old overworld "
                    + (nativeDimOld == level) + ", new end " + (nativeDimNew == end));
            nativeDimFired = 0;
            nativeDimOld = null;
            tp.method_5731(new net.minecraft.class_5454(level, new net.minecraft.class_243(0.5, 100.0, 0.5),
                net.minecraft.class_243.field_1353, 0.0F, 0.0F, net.minecraft.class_5454.field_52245));
            report(tag + " dim-local-portal", nativeDimFired == 1 && nativeDimOld == end,
                "changeDimension end -> overworld: native injector fired " + nativeDimFired + ", old end " + (nativeDimOld == end));
            if (!tp.method_31481()) {
                ((class_3218) tp.method_37908()).method_18770(tp, net.minecraft.class_1297.class_5529.field_26999);
            }
        } catch (Throwable t) {
            report(tag + " exception", false, String.valueOf(t));
            t.printStackTrace();
        }
        System.out.println("CB-SMX6V summary " + tag + " ok=" + ok + " fail=" + fail);
    }
}
