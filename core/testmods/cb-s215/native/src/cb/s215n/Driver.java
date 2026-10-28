package cb.s215n;

import java.util.List;
import net.minecraft.class_1299;
import net.minecraft.class_1311;
import net.minecraft.class_1959;
import net.minecraft.class_2338;
import net.minecraft.class_2794;
import net.minecraft.class_3218;
import net.minecraft.class_5483;
import net.minecraft.class_5819;
import net.minecraft.class_6010;
import net.minecraft.class_6012;
import net.minecraft.class_6880;
import net.minecraft.class_7924;
import net.minecraft.server.MinecraftServer;

/**
 * cb-s215n: a 1.21.5 mod (not converted). Reads, through 1.21.5's own API, what the converted 1.21.4 mod cb-s215 built
 * (cb.s215.Driver.LIST / GEN, by reflection) and checks that 1.21.5 sees its own shapes: a WeightedList of
 * Weighted(SpawnerData, weight) with the weights the 1.21.4 mod gave, and its ChunkGenerator.getMobsAt override answering
 * such a list. Without cb-s215 it checks 1.21.5's own weighted lists only. Logs "CB-S215N ok|FAIL" and a summary.
 */
public final class Driver {
    private static int ok;
    private static int fail;
    private static boolean done;

    private Driver() {
    }

    private static void check(String c, boolean cond, String detail) {
        if (cond) {
            ok++;
        } else {
            fail++;
        }
        System.out.println("CB-S215N " + (cond ? "ok " : "FAIL ") + c + " " + detail);
    }

    private static void run(String c, Runnable r) {
        try {
            r.run();
        } catch (Throwable t) {
            fail++;
            System.out.println("CB-S215N FAIL " + c + " threw " + t);
            t.printStackTrace(System.out);
        }
    }

    private static Class<?> converted() {
        try {
            return Class.forName("cb.s215.Driver");
        } catch (ClassNotFoundException e) {
            return null;
        }
    }

    public static synchronized void run(MinecraftServer server) {
        if (done) {
            return;
        }
        done = true;
        Class<?> conv = converted();
        if (conv != null) {
            run("converted-driver", () -> {
                try {
                    conv.getMethod("runOnce", MinecraftServer.class).invoke(null, server);
                } catch (ReflectiveOperationException e) {
                    throw new RuntimeException(e);
                }
            });
        }
        class_5819 random = class_5819.method_43049(215L);
        class_3218 level = server.method_30002();

        run("native-list", () -> {
            class_6012<String> l = class_6012.<String>method_66215().method_34975("a", 3).method_34975("b", 7).method_34974();
            String v = l.method_34992(random).orElseThrow();
            check("native-list", l.method_34994().size() == 2 && l.method_34994().get(1).comp_2543() == 7 && ("a".equals(v) || "b".equals(v)),
                "entries=" + l.method_34994() + " value=" + v);
        });

        run("native-spawns", () -> {
            class_6880<class_1959> plains = server.method_30611().method_30530(class_7924.field_41236).method_46747(net.minecraft.class_1972.field_9451);
            int sheep = -1;
            for (class_6010<class_5483.class_1964> w : plains.comp_349().method_30966().method_31004(class_1311.field_6294).method_34994()) {
                if (w.comp_2542().comp_3488() == class_1299.field_6115) {
                    sheep = w.comp_2543();
                }
            }
            check("native-spawns", sheep == 12, "sheep weight=" + sheep);
        });

        if (conv == null) {
            System.out.println("CB-S215N summary ok=" + ok + " fail=" + fail + " (native only)");
            return;
        }
        run("converted-list", () -> {
            @SuppressWarnings("unchecked")
            class_6012<class_5483.class_1964> l = (class_6012<class_5483.class_1964>) field(conv, "LIST");
            List<class_6010<class_5483.class_1964>> e = l.method_34994();
            class_5483.class_1964 pick = l.method_34992(random).orElseThrow();
            check("converted-list", e.size() == 2 && e.get(0).comp_2543() == 42 && e.get(0).comp_2542().comp_3488() == class_1299.field_6051
                && e.get(1).comp_2543() == 8 && pick != null, "entries=" + e);
        });
        run("converted-mobs-at", () -> {
            class_2794 gen = (class_2794) field(conv, "GEN");
            class_2338 pos = new class_2338(0, 70, 0);
            class_6012<class_5483.class_1964> mobs = gen.method_12113(level.method_23753(pos), level.method_27056(), class_1311.field_6302, pos);
            List<class_6010<class_5483.class_1964>> e = mobs.method_34994();
            class_6010<class_5483.class_1964> last = e.get(e.size() - 1);
            class_6012<class_5483.class_1964> vanilla = level.method_23753(pos).comp_349().method_30966().method_31004(class_1311.field_6302);
            boolean same = e.size() == vanilla.method_34994().size() + 1;
            for (int i = 0; same && i < vanilla.method_34994().size(); i++) {
                same = vanilla.method_34994().get(i).comp_2543() == e.get(i).comp_2543() && vanilla.method_34994().get(i).comp_2542() == e.get(i).comp_2542();
            }
            check("converted-mobs-at", last.comp_2542().comp_3488() == class_1299.field_6099 && last.comp_2543() == 77 && same,
                "n=" + e.size() + " vanillaEntriesKept=" + same + " last=" + last);
        });
        run("converted-saved-data", () -> {
            Object saved = field(conv, "SAVED");
            Object mapIndex = field(conv, "MAP_INDEX");
            // the converted mod's data is a 1.21.5 SavedData; the MapIndex its factory() call got is vanilla's own
            check("converted-saved-data", saved instanceof net.minecraft.class_18
                && mapIndex == level.method_17983().method_17924(net.minecraft.class_3978.field_56475),
                "saved=" + saved + " mapIndex=" + mapIndex);
        });
        System.out.println("CB-S215N summary ok=" + ok + " fail=" + fail);
    }

    private static Object field(Class<?> c, String name) {
        try {
            return c.getField(name).get(null);
        } catch (ReflectiveOperationException e) {
            throw new RuntimeException(e);
        }
    }
}
