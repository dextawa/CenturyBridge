package top.dext.centurybridge.rt.v1_20_2;

/**
 * Marks an effect class whose 1.20.1 attribute behaviour differs from the default: a converted
 * class overriding addAttributeModifiers, removeAttributeModifiers or getAttributeModifierValue
 * (an adapter hook adds this interface), or one of the 1.20.1 effect classes CenturyBridge ships
 * again (AttackDamageMobEffect, HealthBoostMobEffect facades). 1.20.2 applies such an effect
 * through the 1.20.1 hooks (CbLivingEffectAttributes) and its modifier templates answer
 * getAttributeModifierValue (CbEffectTemplateLegacyValue).
 */
public interface CbLegacyMobEffectOverride extends CbLegacyMobEffect {

    @Override
    void method_5555(net.minecraft.class_1309 entity, net.minecraft.class_5131 attributes, int amplifier);

    @Override
    void method_5562(net.minecraft.class_1309 entity, net.minecraft.class_5131 attributes, int amplifier);

    @Override
    double method_5563(int amplifier, net.minecraft.class_1322 modifier);
}
