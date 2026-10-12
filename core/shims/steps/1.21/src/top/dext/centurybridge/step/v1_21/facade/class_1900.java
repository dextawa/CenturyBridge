package top.dext.centurybridge.step.v1_21.facade;

import top.dext.centurybridge.step.v1_21.rt.CbEnchVanilla;
import top.dext.centurybridge.step.v1_21.rt.CbEnchantmentHelper;

/**
 * 1.20.6's ProtectionEnchantment (net/minecraft/class_1900, gone in 1.21, where the five protections are
 * data). 1.20.6's bodies: the protection by type (all: the level; fire, explosion, projectile: twice the
 * level against their damage; fall: three times), nothing against damage that bypasses invulnerability;
 * two protections exclude each other unless one of them is feather falling; the fire and explosion
 * dampeners read the entity's fire / blast protection level as 1.20.6 did.
 */
public class class_1900 extends class_1887 {

    /** 1.20.6 ProtectionEnchantment.type */
    public final class_1900$class_1901 field_9133;

    public class_1900(class_1887$class_9427 definition, class_1900$class_1901 type) {
        super(definition);
        this.field_9133 = type;
    }

    /** getDamageProtection(int, DamageSource) */
    @Override
    public int method_8181(int level, net.minecraft.class_1282 source) {
        if (source.method_48789(net.minecraft.class_8103.field_42242)) {
            return 0;
        }
        if (field_9133 == class_1900$class_1901.field_9138) {
            return level;
        }
        if (field_9133 == class_1900$class_1901.field_9139 && source.method_48789(net.minecraft.class_8103.field_42246)) {
            return level * 2;
        }
        if (field_9133 == class_1900$class_1901.field_9140 && source.method_48789(net.minecraft.class_8103.field_42250)) {
            return level * 3;
        }
        if (field_9133 == class_1900$class_1901.field_9141 && source.method_48789(net.minecraft.class_8103.field_42249)) {
            return level * 2;
        }
        if (field_9133 == class_1900$class_1901.field_9142 && source.method_48789(net.minecraft.class_8103.field_42247)) {
            return level * 2;
        }
        return 0;
    }

    /** checkCompatibility(Enchantment) */
    @Override
    public boolean method_8180(class_1887 other) {
        if (other instanceof class_1900 protection) {
            if (this.field_9133 == protection.field_9133) {
                return false;
            }
            return this.field_9133 == class_1900$class_1901.field_9140 || protection.field_9133 == class_1900$class_1901.field_9140;
        }
        return super.method_8180(other);
    }

    /** getFireAfterDampener(LivingEntity, int): 15% less burning per level of fire protection */
    public static int method_8238(net.minecraft.class_1309 entity, int duration) {
        int level = CbEnchantmentHelper.method_8203(CbEnchVanilla.field_9095(), entity);
        if (level > 0) {
            duration -= net.minecraft.class_3532.method_15375((float) duration * (float) level * 0.15F);
        }
        return duration;
    }

    /** getExplosionKnockbackAfterDampener(LivingEntity, double): 15% less knockback per level of blast protection */
    public static double method_8237(net.minecraft.class_1309 entity, double knockback) {
        int level = CbEnchantmentHelper.method_8203(CbEnchVanilla.field_9107(), entity);
        if (level > 0) {
            knockback *= net.minecraft.class_3532.method_15350(1.0 - (double) level * 0.15, 0.0, 1.0);
        }
        return knockback;
    }
}
