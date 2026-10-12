package top.dext.centurybridge.step.v1_21.facade;

/**
 * 1.20.6's BreachEnchantment (net/minecraft/class_9467). 1.20.6 gated breach behind the update_1_21
 * experiment (FeatureFlags.UPDATE_1_21), which 1.21 made the base game: its required features are the
 * default set here, as for every 1.21 enchantment.
 */
public class class_9467 extends class_1887 {

    public class_9467() {
        super(class_1887.method_58674(net.minecraft.class_3489.field_50109, 2, 4, class_1887.method_58441(15, 9),
            class_1887.method_58441(65, 9), 4, net.minecraft.class_7701.field_40183, net.minecraft.class_1304.field_6173));
    }

    /** calculateArmorBreach(float level, float armorEffectiveness): 15% less armor effectiveness per level */
    public static float method_58672(float level, float armorEffectiveness) {
        return net.minecraft.class_3532.method_15363(armorEffectiveness - 0.15F * level, 0.0F, 1.0F);
    }
}
