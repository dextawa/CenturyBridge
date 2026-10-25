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
        } catch (Throwable t) {
            check("exception", false, String.valueOf(t));
            t.printStackTrace();
        } finally {
            cancelChanneling = false;
        }
        System.out.println("CB-SMX6 summary ok=" + ok + " fail=" + fail);
    }
}
