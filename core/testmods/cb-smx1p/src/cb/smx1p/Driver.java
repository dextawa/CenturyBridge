package cb.smx1p;

import java.util.UUID;
import net.minecraft.class_11560;
import net.minecraft.class_1937;
import net.minecraft.class_2165;
import net.minecraft.class_2168;
import net.minecraft.class_241;
import net.minecraft.class_243;
import net.minecraft.class_2561;
import net.minecraft.class_3218;
import net.minecraft.class_4969;
import net.minecraft.server.MinecraftServer;

/**
 * Runs once the world is loaded, with 1.21.10's int permission API: a CommandSourceStack built with level 2, its
 * hasPermission / withPermission / withMaximumPermission, the dedicated server's operator (op-permission-level, default 4)
 * and function (function-permission-level, default 2) levels, a non-operator profile's level (0), and
 * RespawnAnchorBlock.canSetSpawn(Level) (anchors work in the nether, not in the overworld). The expected values are what
 * 1.21.10's own bodies answer. "CB-SMX1P ok|FAIL case detail", then "CB-SMX1P summary ok=N fail=M".
 */
public final class Driver {
    private static int ok;
    private static int fail;

    private Driver() {
    }

    private static void check(String c, boolean cond, Object detail) {
        if (cond) {
            ok++;
        } else {
            fail++;
        }
        System.out.println("CB-SMX1P " + (cond ? "ok " : "FAIL ") + c + " " + detail);
    }

    private static void run(String c, Runnable r) {
        try {
            r.run();
        } catch (Throwable t) {
            check(c, false, t);
        }
    }

    public static void run(MinecraftServer server) {
        class_3218 overworld = server.method_30002();
        run("source", () -> {
            class_2168 src = new class_2168(class_2165.field_17395, class_243.field_1353, class_241.field_1340, overworld, 2,
                "cbsmx1p", class_2561.method_43470("cbsmx1p"), server, null);
            check("hasPermission(0)", src.method_9259(0), true);
            check("hasPermission(2)", src.method_9259(2), true);
            check("!hasPermission(3)", !src.method_9259(3), false);
            check("withPermission(4).has(4)", src.method_9206(4).method_9259(4), true);
            check("withPermission(0).!has(1)", !src.method_9206(0).method_9259(1), false);
            check("withMaximumPermission(3).has(3)", src.method_9230(3).method_9259(3), true);
            check("withMaximumPermission(1).has(2)", src.method_9230(1).method_9259(2), true);
            check("withMaximumPermission(1).!has(3)", !src.method_9230(1).method_9259(3), false);
        });
        run("server", () -> {
            int op = server.method_3798();
            check("operatorLevel==4", op == 4, op);
            int fn = server.method_21714();
            check("functionLevel==2", fn == 2, fn);
            int prof = server.method_3835(new class_11560(UUID.fromString("00000000-0000-0000-0000-0000cb5a1f01"), "cbsmx1p"));
            check("profileLevel(non-op)==0", prof == 0, prof);
        });
        run("anchor", () -> {
            check("!canSetSpawn(overworld)", !class_4969.method_27353(overworld), false);
            class_3218 nether = server.method_3847(class_1937.field_25180);
            check("canSetSpawn(nether)", nether != null && class_4969.method_27353(nether), nether);
        });
        System.out.println("CB-SMX1P summary ok=" + ok + " fail=" + fail);
    }
}
