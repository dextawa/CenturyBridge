package top.dext.centurybridge.rt.v1_20_2;

/**
 * 1.20.1 MobEffect's attribute hooks, answered again by every effect (base mixin
 * StatusEffectBridge; s1misc made addAttributeModifiers 1.20.1's body). 1.20.2 dropped the
 * entity from add / remove, the amplifier from remove, and getAttributeModifierValue altogether:
 * its effects carry AttributeModifierTemplates that compute the value themselves. See
 * {@link CbEffects} for how a converted override is reconnected.
 */
public interface CbLegacyMobEffect {

    /** 1.20.1 addAttributeModifiers(entity, attributes, amplifier) */
    void method_5555(net.minecraft.class_1309 entity, net.minecraft.class_5131 attributes, int amplifier);

    /** 1.20.1 removeAttributeModifiers(entity, attributes, amplifier) */
    void method_5562(net.minecraft.class_1309 entity, net.minecraft.class_5131 attributes, int amplifier);

    /** 1.20.1 getAttributeModifierValue(amplifier, modifier) */
    double method_5563(int amplifier, net.minecraft.class_1322 modifier);
}
