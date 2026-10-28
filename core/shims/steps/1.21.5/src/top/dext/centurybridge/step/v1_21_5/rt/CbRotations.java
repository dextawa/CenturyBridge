package top.dext.centurybridge.step.v1_21_5.rt;

import net.minecraft.class_2379;
import net.minecraft.class_2494;
import net.minecraft.class_2499;
import net.minecraft.class_2520;
import net.minecraft.class_3532;

/**
 * 1.21.4's Rotations members on 1.21.5's Rotations record (x, y, z: comp_3776 / comp_3777 / comp_3778; the same
 * normalisation in the constructor). Bodies from javap -c -p 1.21.4 class_2379.
 */
public final class CbRotations {
    private CbRotations() {
    }

    /** new Rotations(ListTag): the floats at 0, 1, 2 (1.21.4's ListTag.getFloat: 0 unless that element is a FloatTag) */
    public static class_2379 fromList(class_2499 list) {
        return new class_2379(floatAt(list, 0), floatAt(list, 1), floatAt(list, 2));
    }

    private static float floatAt(class_2499 list, int i) {
        if (i >= 0 && i < list.size()) {
            class_2520 t = list.method_10534(i);
            if (t instanceof class_2494 f) {
                return f.method_10700();
            }
        }
        return 0.0F;
    }

    /** save(): a ListTag of the three FloatTags */
    public static class_2499 save(class_2379 r) {
        class_2499 list = new class_2499();
        list.add(class_2494.method_23244(r.comp_3776()));
        list.add(class_2494.method_23244(r.comp_3777()));
        list.add(class_2494.method_23244(r.comp_3778()));
        return list;
    }

    /** getWrappedX() */
    public static float wrappedX(class_2379 r) {
        return class_3532.method_15393(r.comp_3776());
    }

    /** getWrappedY() */
    public static float wrappedY(class_2379 r) {
        return class_3532.method_15393(r.comp_3777());
    }

    /** getWrappedZ() */
    public static float wrappedZ(class_2379 r) {
        return class_3532.method_15393(r.comp_3778());
    }

    /** field x */
    public static float field_11165(class_2379 r) {
        return r.comp_3776();
    }

    /** field y */
    public static float field_11164(class_2379 r) {
        return r.comp_3777();
    }

    /** field z */
    public static float field_11163(class_2379 r) {
        return r.comp_3778();
    }
}
