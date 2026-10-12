package top.dext.centurybridge.step.v1_21.facade;

/**
 * 1.20.6's DensityEnchantment (net/minecraft/class_9468). Gated behind update_1_21 in 1.20.6, base game in
 * 1.21 (see class_9467).
 */
public class class_9468 extends class_1887 {

    public class_9468() {
        super(class_1887.method_58674(net.minecraft.class_3489.field_50109, 10, 5, class_1887.method_58441(1, 11),
            class_1887.method_58441(21, 11), 1, net.minecraft.class_7701.field_40183, net.minecraft.class_1304.field_6173));
    }

    /** calculateDamageAddition(int level, float fallDistance) */
    public static float method_58673(int level, float fallDistance) {
        return fallDistance * (float) level;
    }
}
