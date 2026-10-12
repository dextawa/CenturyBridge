package top.dext.centurybridge.rt.v1_20_2;

import java.util.UUID;
import net.minecraft.class_1291;
import net.minecraft.class_1322;
import net.minecraft.class_8630;

/**
 * A 1.20.1 effect modifier standing where 1.20.2 keeps an AttributeModifierTemplate (s1misc):
 * what 1.20.1 code put into an effect's getAttributeModifiers() map, or what a converted
 * override of getAttributeModifiers() answers (CbEffects' map views). 1.20.1 stored the
 * modifier itself and, applying the effect at an amplifier, added a modifier with its id, named
 * "description amplifier", worth getAttributeModifierValue(amplifier, modifier) -- which is what
 * {@link #method_52522} makes, so 1.20.2 applying the effect (and its potion tooltip) sees
 * 1.20.1's value. Without a known effect (a map no effect owns) the value is 1.20.1's default
 * getAttributeModifierValue and the name the modifier's own.
 */
public final class CbLegacyEffectTemplate implements class_8630 {

    final class_1291 effect;
    final class_1322 modifier;

    CbLegacyEffectTemplate(class_1291 effect, class_1322 modifier) {
        this.effect = effect;
        this.modifier = modifier;
    }

    /** getAttributeModifierId(): the 1.20.1 modifier's id */
    @Override
    public UUID method_52521() {
        return this.modifier.method_6189();
    }

    /** create(amplifier): the modifier 1.20.1 added at that amplifier */
    @Override
    public class_1322 method_52522(int amplifier) {
        if (this.effect != null) {
            return CbEffects.legacyModifier(this.effect, this.modifier, amplifier);
        }
        return new class_1322(this.modifier.method_6189(), this.modifier.method_6185() + " " + amplifier,
            CbEffects.attributeModifierValue(amplifier, this.modifier), this.modifier.method_6182());
    }
}
