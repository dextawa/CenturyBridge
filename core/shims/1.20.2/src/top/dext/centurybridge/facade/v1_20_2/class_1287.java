package top.dext.centurybridge.facade.v1_20_2;

import net.minecraft.class_1291;
import net.minecraft.class_1309;
import net.minecraft.class_1322;
import net.minecraft.class_4081;
import net.minecraft.class_5131;
import top.dext.centurybridge.rt.v1_20_2.CbEffects;
import top.dext.centurybridge.rt.v1_20_2.CbLegacyMobEffectOverride;

/**
 * 1.20.1 AttackDamageMobEffect (class_1287), gone in 1.20.2 (strength and weakness became plain
 * effects whose templates carry the amount): an effect whose attribute modifiers are worth
 * multiplier * (amplifier + 1), whatever amount they were registered with. Ported as it was; as a
 * CbLegacyMobEffectOverride it is applied through 1.20.1's attribute hooks and its templates
 * answer with this value (see CbEffects). The constructor and the multiplier field were protected;
 * public here, as an access widener never reaches a CenturyBridge class.
 */
public class class_1287 extends class_1291 implements CbLegacyMobEffectOverride {

    public final double field_5881;

    public class_1287(class_4081 category, int color, double multiplier) {
        super(category, color);
        this.field_5881 = multiplier;
    }

    /** getAttributeModifierValue(amplifier, modifier) */
    @Override
    public double method_5563(int amplifier, class_1322 modifier) {
        return this.field_5881 * (double) (amplifier + 1);
    }

    /** addAttributeModifiers(entity, attributes, amplifier): MobEffect's 1.20.1 body */
    @Override
    public void method_5555(class_1309 entity, class_5131 attributes, int amplifier) {
        CbEffects.addAttributeModifiers(this, entity, attributes, amplifier);
    }

    /** removeAttributeModifiers(entity, attributes, amplifier): MobEffect's 1.20.1 body */
    @Override
    public void method_5562(class_1309 entity, class_5131 attributes, int amplifier) {
        CbEffects.removeAttributeModifiers(this, entity, attributes, amplifier);
    }
}
