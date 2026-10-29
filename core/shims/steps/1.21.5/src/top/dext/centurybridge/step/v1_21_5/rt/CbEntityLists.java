package top.dext.centurybridge.step.v1_21_5.rt;

import net.minecraft.class_1297;
import net.minecraft.class_2489;
import net.minecraft.class_2494;
import net.minecraft.class_2499;

/**
 * 1.21.4's protected Entity.newDoubleList(double...) / newFloatList(float...) (method_5846 / method_5726), gone in
 * 1.21.5 (its save writes vectors through codecs). 1.21.4's bodies (javap -c -p 1.21.4 class_1297): a new ListTag with
 * DoubleTag.valueOf / FloatTag.valueOf of each value, in order. The entity is not read.
 */
public final class CbEntityLists {
    private CbEntityLists() {
    }

    /** newDoubleList(values) */
    public static class_2499 newDoubleList(class_1297 entity, double[] values) {
        class_2499 list = new class_2499();
        for (double v : values) {
            list.add(class_2489.method_23241(v));
        }
        return list;
    }

    /** newFloatList(values) */
    public static class_2499 newFloatList(class_1297 entity, float[] values) {
        class_2499 list = new class_2499();
        for (float v : values) {
            list.add(class_2494.method_23244(v));
        }
        return list;
    }
}
