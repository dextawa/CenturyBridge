package top.dext.centurybridge.step.v1_21.facade;

import java.util.Optional;

/**
 * 1.20.6's DamageEnchantment (net/minecraft/class_1882, gone in 1.21: sharpness, smite, bane of arthropods
 * and impaling are data there). 1.20.6's bodies: the bonus is 1 + 0.5 per level above the first without
 * target types, 2.5 per level against the targets; damage enchantments exclude each other; bane of
 * arthropods slows an arthropod it hits.
 */
public class class_1882 extends class_1887 {

    /** 1.20.6 DamageEnchantment.targets */
    public final Optional<net.minecraft.class_6862<net.minecraft.class_1299<?>>> field_48369;

    public class_1882(class_1887$class_9427 definition, Optional<net.minecraft.class_6862<net.minecraft.class_1299<?>>> targets) {
        super(definition);
        this.field_48369 = targets;
    }

    /** getDamageBonus(int, EntityType) */
    @Override
    public float method_8196(int level, net.minecraft.class_1299<?> type) {
        if (field_48369.isEmpty()) {
            return 1.0F + (float) Math.max(0, level - 1) * 0.5F;
        }
        return type != null && type.method_20210(field_48369.get()) ? (float) level * 2.5F : 0.0F;
    }

    /** checkCompatibility(Enchantment) */
    @Override
    public boolean method_8180(class_1887 other) {
        return !(other instanceof class_1882);
    }

    /** doPostAttack(LivingEntity, Entity, int): bane of arthropods' slowness on an arthropod */
    @Override
    public void method_8189(net.minecraft.class_1309 attacker, net.minecraft.class_1297 target, int level) {
        if (field_48369.isPresent() && target instanceof net.minecraft.class_1309 living
                && field_48369.get() == net.minecraft.class_3483.field_48285 && level > 0
                && living.method_5864().method_20210(field_48369.get())) {
            int duration = 20 + attacker.method_59922().method_43048(10 * level);
            living.method_6092(new net.minecraft.class_1293(net.minecraft.class_1294.field_5909, duration, 3));
        }
    }
}
