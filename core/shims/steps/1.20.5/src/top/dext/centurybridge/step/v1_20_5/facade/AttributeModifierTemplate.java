package top.dext.centurybridge.step.v1_20_5.facade;

/**
 * 1.20.4's AttributeModifierTemplate: an effect's attribute modifier for a given amplifier. 1.20.5 keeps
 * an effect's modifiers as a fixed record (MobEffect.AttributeTemplate: id, amount, operation); a 1.20.4
 * template -- vanilla's, seen through rt/CbEffects, or a mod's own implementation -- is this interface.
 */
public interface AttributeModifierTemplate {

    /** getAttributeModifierId() */
    java.util.UUID method_52521();

    /** create(amplifier) */
    net.minecraft.class_1322 method_52522(int amplifier);
}
