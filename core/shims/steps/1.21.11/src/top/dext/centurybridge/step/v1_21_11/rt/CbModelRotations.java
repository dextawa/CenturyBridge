package top.dext.centurybridge.step.v1_21_11.rt;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.class_10821;
import net.minecraft.class_1086;
import net.minecraft.class_4990;

/**
 * 1.21.10 ModelRotation (class_1086) on 1.21.11 (javap -c -p both code jars). 1.21.10's ModelRotation was an enum of 16
 * constants X{0,90,180,270}_Y{0,90,180,270} (X-major declaration order), each built from two Quadrants whose
 * OctahedralGroup came from class_4990.method_67987: identity, composed with field_23318 y.field_57034 times, then with
 * field_23316 x.field_57034 times. 1.21.11 made ModelRotation a class with one instance per OctahedralGroup
 * (method_75489(group), a map filled for every group), the group held in field_64679. compose (method_26385) has the same
 * body in both releases, and field_23292/23316/23318 and Quadrant.field_57034 are unchanged, so each constant is
 * 1.21.11's instance for the group 1.21.10 computed: the same rotation matrices and face transforms, and the same object
 * for every caller (vanilla 1.21.11's models included).
 */
@Environment(EnvType.CLIENT)
public final class CbModelRotations {
    private CbModelRotations() {
    }

    /** 1.21.10's constant names, in declaration order (Quadrant R0, R90, R180, R270 for x, then y) */
    private static final String[] NAMES = {
        "X0_Y0", "X0_Y90", "X0_Y180", "X0_Y270", "X90_Y0", "X90_Y90", "X90_Y180", "X90_Y270",
        "X180_Y0", "X180_Y90", "X180_Y180", "X180_Y270", "X270_Y0", "X270_Y90", "X270_Y180", "X270_Y270"};
    private static final class_1086[] VALUES = new class_1086[16];

    static {
        class_10821[] q = class_10821.values();
        for (int x = 0; x < 4; x++) {
            for (int y = 0; y < 4; y++) {
                VALUES[x * 4 + y] = method_68008(q[x], q[y]);
            }
        }
    }

    /** 1.21.10 class_4990.method_67987's table entry for (x, y) */
    private static class_4990 group(class_10821 x, class_10821 y) {
        class_4990 g = class_4990.field_23292;
        for (int i = 0; i < y.field_57034; i++) {
            g = g.method_26385(class_4990.field_23318);
        }
        for (int i = 0; i < x.field_57034; i++) {
            g = g.method_26385(class_4990.field_23316);
        }
        return g;
    }

    /** ModelRotation.by(Quadrant x, Quadrant y) */
    public static class_1086 method_68008(class_10821 x, class_10821 y) {
        return class_1086.method_75489(group(x, y));
    }

    /** ModelRotation.rotation() (the OctahedralGroup) */
    public static class_4990 method_35808(class_1086 r) {
        return r.field_64679;
    }

    /** ModelRotation.values(): a fresh array in 1.21.10's declaration order, as an enum's values() */
    public static class_1086[] values() {
        return VALUES.clone();
    }

    /** ModelRotation.valueOf(String): Enum.valueOf's contract (NPE on null, IllegalArgumentException on no match) */
    public static class_1086 valueOf(String name) {
        if (name == null) {
            throw new NullPointerException("Name is null");
        }
        for (int i = 0; i < 16; i++) {
            if (NAMES[i].equals(name)) {
                return VALUES[i];
            }
        }
        throw new IllegalArgumentException("No enum constant net.minecraft.class_1086." + name);
    }

    /**
     * ModelRotation.ordinal() / name() (java.lang.Enum's, called through class_1086): 1.21.10's index and constant name.
     * A 1.21.11 rotation outside the 16 (a group with a z turn, which 1.21.10 could not express) has no 1.21.10
     * constant: -1 and its 1.21.11 toString.
     */
    public static int ordinal(class_1086 r) {
        for (int i = 0; i < 16; i++) {
            if (VALUES[i] == r) {
                return i;
            }
        }
        return -1;
    }

    public static String name(class_1086 r) {
        int i = ordinal(r);
        return i < 0 ? r.toString() : NAMES[i];
    }

    public static class_1086 field_5350() { return VALUES[0]; }
    public static class_1086 field_5366() { return VALUES[1]; }
    public static class_1086 field_5355() { return VALUES[2]; }
    public static class_1086 field_5347() { return VALUES[3]; }
    public static class_1086 field_5351() { return VALUES[4]; }
    public static class_1086 field_5360() { return VALUES[5]; }
    public static class_1086 field_5367() { return VALUES[6]; }
    public static class_1086 field_5354() { return VALUES[7]; }
    public static class_1086 field_5358() { return VALUES[8]; }
    public static class_1086 field_5348() { return VALUES[9]; }
    public static class_1086 field_5356() { return VALUES[10]; }
    public static class_1086 field_5359() { return VALUES[11]; }
    public static class_1086 field_5353() { return VALUES[12]; }
    public static class_1086 field_5349() { return VALUES[13]; }
    public static class_1086 field_5361() { return VALUES[14]; }
    public static class_1086 field_5352() { return VALUES[15]; }
}
