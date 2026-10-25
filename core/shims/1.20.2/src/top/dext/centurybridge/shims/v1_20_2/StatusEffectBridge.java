package top.dext.centurybridge.shims.v1_20_2;

import net.minecraft.class_1291;
import net.minecraft.class_1309;
import net.minecraft.class_5131;
import org.spongepowered.asm.mixin.Mixin;
import top.dext.centurybridge.rt.v1_20_2.CbEffects;
import top.dext.centurybridge.rt.v1_20_2.CbLegacyMobEffect;

/**
 * 1.20.1 -> 1.20.2: StatusEffect attribute hooks dropped the entity/amplifier
 * parameters, and getAttributeModifierValue is gone. The 1.20.1 shapes are back with
 * 1.20.1's bodies (CbEffects): add puts each modifier in at getAttributeModifierValue, so an
 * override of that is honoured; remove takes them out by id; the value is the base amount
 * times (amplifier + 1). Vanilla 1.20.2 calls the new shapes; an effect whose 1.20.1
 * behaviour differs is applied through these again (s1misc: CbLivingEffectAttributes,
 * CbEffectTemplateLegacyValue).
 */
@Mixin(value = class_1291.class, priority = top.dext.centurybridge.rt.v1_20_2.CbVanillaReads.PRIORITY)
public abstract class StatusEffectBridge implements CbLegacyMobEffect {

    @Override
    public void method_5562(class_1309 entity, class_5131 attributes, int amplifier) {
        CbEffects.removeAttributeModifiers((class_1291) (Object) this, entity, attributes, amplifier);
    }

    @Override
    public void method_5555(class_1309 entity, class_5131 attributes, int amplifier) {
        CbEffects.addAttributeModifiers((class_1291) (Object) this, entity, attributes, amplifier);
    }

    /** removed in 1.20.2; re-implemented with the legacy default semantics */
    @Override
    public double method_5563(int amplifier, net.minecraft.class_1322 modifier) {
        return CbEffects.attributeModifierValue(amplifier, modifier);
    }
}
