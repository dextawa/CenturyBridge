package top.dext.centurybridge.step.v1_21_11.rt;

import java.util.IdentityHashMap;
import java.util.Map;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.class_10821;
import net.minecraft.class_4990;

/**
 * 1.21.10 ModelRotation's (x, y) Quadrants by OctahedralGroup. 1.21.10 built each of its 16 enum constants from two
 * Quadrants; its group came from class_4990.method_67987 (identity, composed with field_23318 y.field_57034 times, then
 * with field_23316 x.field_57034 times; compose is unchanged at 1.21.11). The 16 groups are distinct, so a group names
 * at most one constant. A 1.21.10 constructor injector moved onto 1.21.11's ModelRotation(OctahedralGroup)
 * (mixinTargets) gets its Quadrant arguments here. Reads only OctahedralGroup and Quadrant, never class_1086: it runs
 * inside class_1086's static initializer, so it must not start initialising anything that reads class_1086.
 */
@Environment(EnvType.CLIENT)
public final class CbModelRotationQuadrants {
    private CbModelRotationQuadrants() {
    }

    private static final Map<class_4990, class_10821[]> BY_GROUP = new IdentityHashMap<>();

    static {
        for (class_10821 x : class_10821.values()) {
            for (class_10821 y : class_10821.values()) {
                BY_GROUP.put(group(x, y), new class_10821[] {x, y});
            }
        }
    }

    /** 1.21.10 class_4990.method_67987's table entry for (x, y) */
    static class_4990 group(class_10821 x, class_10821 y) {
        class_4990 g = class_4990.field_23292;
        for (int i = 0; i < y.field_57034; i++) {
            g = g.method_26385(class_4990.field_23318);
        }
        for (int i = 0; i < x.field_57034; i++) {
            g = g.method_26385(class_4990.field_23316);
        }
        return g;
    }

    /** whether a 1.21.10 constant had group g (16 of 1.21.11's 48 groups) */
    public static boolean mapped(class_4990 g) {
        return BY_GROUP.containsKey(g);
    }

    /** the x Quadrant of the 1.21.10 constant with group g (ModelRotation.<init>(String, int, Quadrant x, Quadrant y)), else null */
    public static class_10821 xOf(class_4990 g) {
        class_10821[] q = BY_GROUP.get(g);
        return q == null ? null : q[0];
    }

    /** the y Quadrant of the 1.21.10 constant with group g, else null */
    public static class_10821 yOf(class_4990 g) {
        class_10821[] q = BY_GROUP.get(g);
        return q == null ? null : q[1];
    }
}
