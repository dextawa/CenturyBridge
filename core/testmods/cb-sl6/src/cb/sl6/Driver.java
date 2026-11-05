package cb.sl6;

import java.lang.reflect.Field;
import java.util.Map;
import net.minecraft.class_1799;
import net.minecraft.class_1802;
import net.minecraft.class_2246;
import net.minecraft.class_2315;
import net.minecraft.class_2338;
import net.minecraft.class_2342;
import net.minecraft.class_2350;
import net.minecraft.class_2357;
import net.minecraft.class_2601;
import net.minecraft.class_3218;

/**
 * Places a dispenser with TNT north of it and runs the flint-and-steel dispense behaviour (1.20.4 DispenseItemBehavior$10,
 * 1.21 $17) once, then prints the handler counts. Shared by the converted fixture and the native control: whichever
 * driver runs first drives (system property cb.sl6.driven).
 */
public final class Driver {
    public static void run(class_3218 level, String who) {
        if (System.getProperties().putIfAbsent("cb.sl6.driven", who) != null) {
            return;
        }
        try {
            class_2338 pos = level.method_43126().method_10086(5);
            level.method_8501(pos, class_2246.field_10200.method_9564());
            level.method_8501(pos.method_10093(class_2350.field_11043), class_2246.field_10375.method_9564());
            class_2601 be = (class_2601) level.method_8321(pos);
            class_2342 src = new class_2342(level, pos, level.method_8320(pos), be);
            Field f = class_2315.class.getDeclaredField("field_10919");
            f.setAccessible(true);
            class_2357 b = (class_2357) ((Map<?, ?>) f.get(null)).get(class_1802.field_8884);
            b.dispense(src, new class_1799(class_1802.field_8884));
            System.out.println("CB-SL6 driven by " + who + " primed=" + Count.of("primed") + " bindImplicit=" + Count.of("bindImplicit")
                + " levelClass=" + System.getProperty("cb.sl6.levelClass") + " ordinal=" + Count.of("ordinal")
                + " native=" + Count.of("native"));
        } catch (Throwable t) {
            System.out.println("CB-SL6 driver FAIL " + t);
            t.printStackTrace(System.out);
        }
        System.out.println("CB-SL6 END");
    }

    private Driver() {
    }
}
