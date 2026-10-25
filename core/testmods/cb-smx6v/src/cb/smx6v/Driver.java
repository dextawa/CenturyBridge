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
        } catch (Throwable t) {
            report(tag + " exception", false, String.valueOf(t));
            t.printStackTrace();
        }
        System.out.println("CB-SMX6V summary " + tag + " ok=" + ok + " fail=" + fail);
    }
}
