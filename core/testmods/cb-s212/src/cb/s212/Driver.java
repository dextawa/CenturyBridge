package cb.s212;

import cb.s212.mixin.DataStorageInvoker;
import net.minecraft.class_1799;
import net.minecraft.class_1802;
import net.minecraft.class_2246;
import net.minecraft.class_2338;
import net.minecraft.class_2363;
import net.minecraft.class_2487;
import net.minecraft.class_2609;
import net.minecraft.class_3218;

/**
 * Runs once the server's world is loaded, on 1.21.1's API (converted 1.21.1 -> 1.21.2). Each case makes 1.21.2's vanilla
 * reach the method 1.21.1 had under another shape and checks that this mod's 1.21.1 mixin on it saw 1.21.1's arguments
 * and that what it answered is what vanilla used. With cb-s212n loaded, its 1.21.2 mixins on the same methods must have
 * fired for the same calls (read by reflection: cb.s212n.Counts). Logs "CB-S212 ok|FAIL <case> <detail>" and
 * "CB-S212 summary ok=N fail=M".
 */
public final class Driver {
    public static volatile int cookTimeAsked;
    public static volatile Object cookTimeLevel;
    public static volatile Object cookTimeBe;
    public static volatile int cookTimeVanilla = -1;
    public static volatile boolean cookTimeOverride;
    public static volatile int canBurnAsked;
    public static volatile Object canBurnRegistries;
    public static volatile Object canBurnRecipe;
    public static volatile java.util.List<class_1799> canBurnItems;
    public static volatile int canBurnMax = -1;
    public static volatile boolean refuseBurn;
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
        System.out.println("CB-S212 " + (cond ? "ok " : "FAIL ") + c + " " + detail);
    }

    private static void run(String c, Runnable r) {
        try {
            r.run();
        } catch (Throwable t) {
            fail++;
            System.out.println("CB-S212 FAIL " + c + " threw " + t);
            t.printStackTrace(System.out);
        }
    }

    /** cb-s212n's counter of that name, or -1 without cb-s212n */
    private static int nativeCount(String field) {
        try {
            return Class.forName("cb.s212n.Counts").getField(field).getInt(null);
        } catch (ReflectiveOperationException e) {
            return -1;
        }
    }

    private static void checkNative(String c, String field, int before) {
        int now = nativeCount(field);
        if (now >= 0) {
            check(c + ".native", now > before, "cb-s212n " + field + " " + before + " -> " + now + " (its 1.21.2 mixin fired too)");
        }
    }

    private static class_2609 furnace(class_3218 level, class_2338 pos) {
        level.method_8501(pos, class_2246.field_10181.method_9564());
        return (class_2609) level.method_8321(pos);
    }

    public static void run(net.minecraft.server.MinecraftServer server) {
        class_3218 level = server.method_30002();

        run("furnace.cookTime", () -> {
            // a plain call first: this mod's and cb-s212n's mixins both see it (neither cancels)
            class_2338 pos0 = new class_2338(-2, 200, 0);
            class_2609 be0 = furnace(level, pos0);
            int n0 = nativeCount("cookTime");
            cookTimeAsked = 0;
            be0.method_5447(0, new class_1799(class_1802.field_33400));
            check("furnace.cookTime.plain", cookTimeAsked == 1 && be0.method_38244(level.method_30349()).method_10550("CookTimeTotal") == 200,
                "asked " + cookTimeAsked + ", total " + be0.method_38244(level.method_30349()).method_10550("CookTimeTotal") + " (1.21.2's own 200)");
            checkNative("furnace.cookTime", "cookTime", n0);
            // then this mod answers 7 (setReturnValue cancels at RETURN: injectors after it there do not run, as on 1.21.1)
            class_2338 pos = new class_2338(0, 200, 0);
            class_2609 be = furnace(level, pos);
            cookTimeAsked = 0;
            cookTimeOverride = true;
            be.method_5447(0, new class_1799(class_1802.field_33400));
            cookTimeOverride = false;
            check("furnace.cookTime.asked", cookTimeAsked == 1, "asked " + cookTimeAsked + " (setItem(0, raw iron) asks getTotalCookTime once)");
            check("furnace.cookTime.args", cookTimeLevel == level && cookTimeBe == be,
                "level=" + cookTimeLevel + " be=" + (cookTimeBe == be));
            check("furnace.cookTime.vanilla", cookTimeVanilla == 200, "vanilla answer " + cookTimeVanilla + " (raw iron smelts in 200)");
            class_2487 tag = be.method_38244(level.method_30349());
            check("furnace.cookTime.used", tag.method_10550("CookTimeTotal") == 7, "saved " + tag);
        });

        run("furnace.canBurn", () -> {
            class_2338 pos = new class_2338(2, 200, 0);
            class_2609 be = furnace(level, pos);
            be.method_5447(0, new class_1799(class_1802.field_33400));
            be.method_5447(1, new class_1799(class_1802.field_8713));
            int n0 = nativeCount("canBurn");
            canBurnAsked = 0;
            class_2609.method_31651(level, pos, level.method_8320(pos), be);
            check("furnace.canBurn.asked", canBurnAsked >= 1, "asked " + canBurnAsked + " (serverTick with fuel and input)");
            check("furnace.canBurn.args", canBurnRegistries == level.method_30349() && canBurnRecipe != null && canBurnMax == 99
                    && canBurnItems != null && canBurnItems.get(0).method_31574(class_1802.field_33400),
                "registries=" + (canBurnRegistries == level.method_30349()) + " recipe=" + canBurnRecipe + " max=" + canBurnMax
                    + " input=" + (canBurnItems == null ? null : canBurnItems.get(0)));
            check("furnace.canBurn.lit", level.method_8320(pos).method_11654(class_2363.field_11105),
                "lit after a tick that may burn: " + level.method_8320(pos));
            checkNative("furnace.canBurn", "canBurn", n0);

            class_2338 pos2 = new class_2338(4, 200, 0);
            class_2609 be2 = furnace(level, pos2);
            be2.method_5447(0, new class_1799(class_1802.field_33400));
            be2.method_5447(1, new class_1799(class_1802.field_8713));
            refuseBurn = true;
            class_2609.method_31651(level, pos2, level.method_8320(pos2), be2);
            refuseBurn = false;
            check("furnace.canBurn.refused", !level.method_8320(pos2).method_11654(class_2363.field_11105),
                "not lit when the mixin answers false: " + level.method_8320(pos2));
        });

        run("data.file", () -> {
            java.io.File f = ((DataStorageInvoker) level.method_17983()).cbs212$dataFile("cbs212");
            String p = f == null ? null : f.getPath().replace('\\', '/');
            check("data.file", p != null && p.endsWith("/data/cbs212.dat"), "getDataFile(\"cbs212\") = " + p);
        });

        System.out.println("CB-S212 summary ok=" + ok + " fail=" + fail);
    }
}
