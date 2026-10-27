package cb.s212n;

import net.minecraft.class_1799;
import net.minecraft.class_1802;
import net.minecraft.class_2246;
import net.minecraft.class_2338;
import net.minecraft.class_2363;
import net.minecraft.class_2609;
import net.minecraft.class_3218;

/**
 * 1.21.2-native driver: drives the same furnace paths through 1.21.2's API and checks that this mod's 1.21.2 mixins fire
 * and that vanilla's answers are 1.21.2's own (no converted mod asks for anything else here). Logs
 * "CB-S212N ok|FAIL <case> <detail>" and "CB-S212N summary ok=N fail=M".
 */
public final class Driver {
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
        System.out.println("CB-S212N " + (cond ? "ok " : "FAIL ") + c + " " + detail);
    }

    private static class_2609 furnace(class_3218 level, class_2338 pos) {
        level.method_8501(pos, class_2246.field_10181.method_9564());
        return (class_2609) level.method_8321(pos);
    }

    public static void run(net.minecraft.server.MinecraftServer server) {
        try {
            class_3218 level = server.method_30002();
            class_2338 pos = new class_2338(0, 210, 0);
            class_2609 be = furnace(level, pos);
            int c0 = Counts.cookTime;
            be.method_5447(0, new class_1799(class_1802.field_33400));
            check("native.cookTime", Counts.cookTime == c0 + 1, "fired " + (Counts.cookTime - c0));
            int total = be.method_38244(level.method_30349()).method_10550("CookTimeTotal");
            check("native.cookTime.vanilla", total == 200, "CookTimeTotal " + total);
            be.method_5447(1, new class_1799(class_1802.field_8713));
            int b0 = Counts.canBurn;
            class_2609.method_31651(level, pos, level.method_8320(pos), be);
            check("native.canBurn", Counts.canBurn > b0, "fired " + (Counts.canBurn - b0));
            check("native.canBurn.vanilla", level.method_8320(pos).method_11654(class_2363.field_11105), "lit " + level.method_8320(pos));
            net.minecraft.class_1642 zombie = net.minecraft.class_1299.field_6051.method_5883(level, net.minecraft.class_3730.field_16467);
            zombie.method_5814(40, 210, 20);
            level.method_8649(zombie);
            int s0 = Counts.seen;
            level.method_8437(null, 40, 210.5, 20, 3.0F, net.minecraft.class_1937.class_7867.field_40888);
            check("native.seen", Counts.seen > s0, "fired " + (Counts.seen - s0) + ", zombie health " + zombie.method_6032());
            zombie.method_31472();
            net.minecraft.class_1642 z2 = net.minecraft.class_1299.field_6051.method_5883(level, net.minecraft.class_3730.field_16467);
            z2.method_5814(0, 210, 40);
            level.method_8649(z2);
            int l0 = Counts.lookAt;
            server.method_3734().method_44252(server.method_3739(),
                "execute in minecraft:overworld run tp " + z2.method_5845() + " 0 210 40 facing 10 210 40");
            check("native.lookAt", Counts.lookAt == l0 + 1 && Math.abs(z2.method_36454() + 90.0F) < 0.5F,
                "fired " + (Counts.lookAt - l0) + ", yRot " + z2.method_36454());
            z2.method_31472();
        } catch (Throwable t) {
            fail++;
            System.out.println("CB-S212N FAIL threw " + t);
            t.printStackTrace(System.out);
        }
        System.out.println("CB-S212N summary ok=" + ok + " fail=" + fail);
    }
}
