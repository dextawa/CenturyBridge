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
        } catch (Throwable t) {
            report(tag + " exception", false, String.valueOf(t));
            t.printStackTrace();
        }
        System.out.println("CB-SMX6V summary " + tag + " ok=" + ok + " fail=" + fail);
    }
}
