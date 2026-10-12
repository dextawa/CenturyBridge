package top.dext.centurybridge.facade.v1_20_2;

import net.minecraft.class_1291;
import net.minecraft.class_1309;
import net.minecraft.class_1322;
import net.minecraft.class_4081;
import net.minecraft.class_5131;
import top.dext.centurybridge.rt.v1_20_2.CbEffects;
import top.dext.centurybridge.rt.v1_20_2.CbLegacyMobEffectOverride;

/**
 * 1.20.1 HealthBoostMobEffect (class_1290), gone in 1.20.2 (health boost became a plain effect):
 * removing its modifiers also brings the entity's health down to its new maximum. Ported as it
 * was; as a CbLegacyMobEffectOverride it is applied through 1.20.1's attribute hooks (see
 * CbEffects).
 */
public class class_1290 extends class_1291 implements CbLegacyMobEffectOverride {

    public class_1290(class_4081 category, int color) {
        super(category, color);
    }

    /** removeAttributeModifiers(entity, attributes, amplifier) */
    @Override
    public void method_5562(class_1309 entity, class_5131 attributes, int amplifier) {
        CbEffects.removeAttributeModifiers(this, entity, attributes, amplifier);
        if (entity.method_6032() > entity.method_6063()) {
            entity.method_6033(entity.method_6063());
        }
    }

    /** addAttributeModifiers(entity, attributes, amplifier): MobEffect's 1.20.1 body */
    @Override
    public void method_5555(class_1309 entity, class_5131 attributes, int amplifier) {
        CbEffects.addAttributeModifiers(this, entity, attributes, amplifier);
    }

    /** getAttributeModifierValue(amplifier, modifier): MobEffect's 1.20.1 body */
    @Override
    public double method_5563(int amplifier, class_1322 modifier) {
        return CbEffects.attributeModifierValue(amplifier, modifier);
    }
}
