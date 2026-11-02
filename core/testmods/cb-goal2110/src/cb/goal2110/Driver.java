package cb.goal2110;

import java.lang.reflect.Method;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerLifecycleEvents;
import net.minecraft.server.MinecraftServer;
import net.minecraft.class_1533;
import net.minecraft.class_1937;
import net.minecraft.class_2338;
import net.minecraft.class_2350;

/**
 * Exercises the 1.21.9 ItemFrame bounding-box injection after conversion to 1.21.10.
 * The original method must fire its moved CONSTANT injector; 1.21.10's new method_74963,
 * which shares the extracted helper, must not. Reflection is deliberate: method_74963 does
 * not exist on 1.21.9 and this fixture is compiled against 1.21.9.
 */
public final class Driver implements ModInitializer {
    public static volatile int hits;
    private static int ok;
    private static int fail;

    private static void check(String name, boolean condition, String detail) {
        if (condition) ok++; else fail++;
        System.out.println("CB-GOAL2110 " + (condition ? "ok " : "FAIL ") + name + " " + detail);
    }

    @Override
    public void onInitialize() {
        ServerLifecycleEvents.SERVER_STARTED.register(Driver::run);
    }

    private static Object call(class_1533 frame, String name, Class<?>[] types, Object... args) throws Exception {
        Method m = class_1533.class.getDeclaredMethod(name, types);
        m.setAccessible(true);
        return m.invoke(frame, args);
    }

    private static void run(MinecraftServer server) {
        boolean control = Driver.class.getResource("/cb_goal2110_control") != null;
        try {
            class_1937 level = server.method_30002();
            class_1533 frame = new class_1533(level, new class_2338(0, 100, 0), class_2350.field_11043);
            Class<?>[] oldTypes = {class_2338.class, class_2350.class};
            Object[] oldArgs = {new class_2338(0, 100, 0), class_2350.field_11043};

            hits = 0;
            Object oldBox = call(frame, "method_59943", oldTypes, oldArgs);
            int want = control ? 0 : 1;
            check("legacy-path", oldBox != null && hits == want, "box=" + oldBox + " hits=" + hits + " want=" + want);

            hits = 0;
            Object otherBox = call(frame, "method_74963", new Class<?>[0]);
            check("shared-callee-other-caller", otherBox != null && hits == 0,
                "box=" + otherBox + " hits=" + hits + " want=0");

            hits = 0;
            Object againBox = call(frame, "method_59943", oldTypes, oldArgs);
            check("legacy-path-again", againBox != null && hits == want,
                "box=" + againBox + " hits=" + hits + " want=" + want);
        } catch (Throwable t) {
            check("driver", false, t.toString());
            t.printStackTrace();
        }
        System.out.println("CB-GOAL2110 summary ok=" + ok + " fail=" + fail + (control ? " (control)" : ""));
    }
}
