package top.dext.centurybridge.step.v1_21.facade;

import top.dext.centurybridge.step.v1_21.rt.CbEnchVanilla;

/**
 * 1.20.6's FrostWalkerEnchantment (net/minecraft/class_1894): a treasure that excludes depth strider, and
 * the static that froze the water around a walking entity, with 1.20.6's body.
 */
public class class_1894 extends class_1887 {

    public class_1894(class_1887$class_9427 definition) {
        super(definition);
    }

    @Override
    public boolean method_8193() {
        return true;
    }

    /** onEntityMoved(LivingEntity, Level, BlockPos, int) */
    public static void method_8236(net.minecraft.class_1309 entity, net.minecraft.class_1937 level, net.minecraft.class_2338 pos, int enchLevel) {
        if (!entity.method_24828()) {
            return;
        }
        net.minecraft.class_2680 frostedIce = net.minecraft.class_2246.field_10110.method_9564();
        int radius = Math.min(16, 2 + enchLevel);
        net.minecraft.class_2338.class_2339 above = new net.minecraft.class_2338.class_2339();
        for (net.minecraft.class_2338 p : net.minecraft.class_2338.method_10097(pos.method_10069(-radius, -1, -radius), pos.method_10069(radius, -1, radius))) {
            if (!p.method_19769(entity.method_19538(), (double) radius)) {
                continue;
            }
            above.method_10103(p.method_10263(), p.method_10264() + 1, p.method_10260());
            if (!level.method_8320(above).method_26215()) {
                continue;
            }
            net.minecraft.class_2680 state = level.method_8320(p);
            if (state == net.minecraft.class_2386.method_51170() && frostedIce.method_26184(level, p)
                    && level.method_8628(frostedIce, p, net.minecraft.class_3726.method_16194())) {
                level.method_8501(p, frostedIce);
                level.method_39279(p, net.minecraft.class_2246.field_10110, net.minecraft.class_3532.method_15395(entity.method_59922(), 60, 120));
            }
        }
    }

    @Override
    public boolean method_8180(class_1887 other) {
        return super.method_8180(other) && other != CbEnchVanilla.field_9128();
    }
}
