package top.dext.centurybridge.rt.v1_20_2;

import java.util.List;
import java.util.Map;
import net.minecraft.class_1291;
import net.minecraft.class_1309;
import net.minecraft.class_1320;
import net.minecraft.class_1322;
import net.minecraft.class_1324;
import net.minecraft.class_5131;
import net.minecraft.class_7917;
import net.minecraft.class_7923;
import net.minecraft.class_8630;
import top.dext.centurybridge.shims.v1_20_2.CbEffectTemplateAccess;

/**
 * 1.20.1 status effects on 1.20.2 (s1misc effect bridge).
 *
 * Attributes: 1.20.1 applied an effect's attribute modifiers through
 * addAttributeModifiers(entity, attributes, amplifier), each worth
 * getAttributeModifierValue(amplifier, modifier), and removed them through
 * removeAttributeModifiers(entity, attributes, amplifier) -- all three overridable, and
 * AttackDamageMobEffect / HealthBoostMobEffect / AbsorptionMobEffect did. 1.20.2 calls
 * add(attributes, amplifier) / remove(attributes) and lets each AttributeModifierTemplate
 * compute its value. The 1.20.1 methods are back on MobEffect with 1.20.1's bodies
 * (StatusEffectBridge, using {@link #addAttributeModifiers} / {@link #removeAttributeModifiers});
 * for an effect whose 1.20.1 behaviour differs ({@link CbLegacyMobEffectOverride}) the living
 * entity calls them again (CbLivingEffectAttributes) and the effect's templates answer with its
 * getAttributeModifierValue (CbEffectTemplateLegacyValue) wherever 1.20.2 asks them (the
 * potion tooltip included).
 *
 * Suspicious-stew holders: 1.20.1's one effect + duration, 1.20.2's list of entries.
 */
public final class CbEffects {

    // ---- 1.20.1 MobEffect statics (the registry's numeric ids)

    /** MobEffect.byId(id) */
    public static class_1291 method_5569(int id) {
        return class_7923.field_41174.method_10200(id);
    }

    /** MobEffect.getId(effect) */
    public static int method_5554(class_1291 effect) {
        return class_7923.field_41174.method_10206(effect);
    }

    /** MobEffect.getIdFromNullable(effect): the same registry lookup in 1.20.1 */
    public static int method_43257(class_1291 effect) {
        return class_7923.field_41174.method_10206(effect);
    }

    // ---- attributes

    /**
     * The modifier 1.20.1 stored for a template: its id, the effect's description id as its
     * name, the template's base amount and operation. A vanilla template is read field by field
     * (its create(amplifier) may itself be answering through getAttributeModifierValue); any
     * other template by what it creates at amplifier 0, which is its base amount.
     */
    public static class_1322 baseModifier(class_1291 effect, class_8630 template) {
        if (template instanceof CbEffectTemplateAccess vanilla) {
            return new class_1322(vanilla.cb$id(), effect::method_5567, vanilla.cb$amount(), vanilla.cb$operation());
        }
        class_1322 m = template.method_52522(0);
        return new class_1322(m.method_6189(), effect::method_5567, m.method_6186(), m.method_6182());
    }

    /** the modifier 1.20.1 added at an amplifier: named "description amplifier", worth getAttributeModifierValue */
    public static class_1322 legacyModifier(class_1291 effect, class_1322 base, int amplifier) {
        return new class_1322(base.method_6189(), effect.method_5567() + " " + amplifier,
            ((CbLegacyMobEffect) effect).method_5563(amplifier, base), base.method_6182());
    }

    /** 1.20.1 MobEffect.addAttributeModifiers(entity, attributes, amplifier) */
    public static void addAttributeModifiers(class_1291 effect, class_1309 entity, class_5131 attributes, int amplifier) {
        for (Map.Entry<class_1320, class_8630> e : effect.method_5565().entrySet()) {
            class_1324 instance = attributes.method_26842(e.getKey());
            if (instance != null) {
                class_1322 base = baseModifier(effect, e.getValue());
                instance.method_6200(base.method_6189());
                instance.method_26837(legacyModifier(effect, base, amplifier));
            }
        }
    }

    /** 1.20.1 MobEffect.removeAttributeModifiers(entity, attributes, amplifier) */
    public static void removeAttributeModifiers(class_1291 effect, class_1309 entity, class_5131 attributes, int amplifier) {
        for (Map.Entry<class_1320, class_8630> e : effect.method_5565().entrySet()) {
            class_1324 instance = attributes.method_26842(e.getKey());
            if (instance != null) {
                instance.method_6200(e.getValue().method_52521());
            }
        }
    }

    /** 1.20.1 MobEffect.getAttributeModifierValue(amplifier, modifier) */
    public static double attributeModifierValue(int amplifier, class_1322 modifier) {
        return modifier.method_6186() * (double) (amplifier + 1);
    }

    // ---- suspicious-stew holders

    /** 1.20.2 getSuspiciousEffects() of a converted 1.20.1 holder: its one effect */
    public static List<class_7917.class_8751> suspiciousEffects(CbLegacySuspiciousEffectHolder holder) {
        return List.of(new class_7917.class_8751(holder.method_10188(), holder.method_10187()));
    }

    /** SuspiciousEffectHolder.getSuspiciousEffect(): a 1.20.1 holder's own, else 1.20.2's first entry */
    public static class_1291 method_10188(class_7917 holder) {
        if (holder instanceof CbLegacySuspiciousEffectHolder legacy) {
            return legacy.method_10188();
        }
        List<class_7917.class_8751> entries = holder.method_53233();
        return entries.isEmpty() ? null : entries.get(0).comp_1838();
    }

    /** SuspiciousEffectHolder.getEffectDuration(): a 1.20.1 holder's own, else 1.20.2's first entry */
    public static int method_10187(class_7917 holder) {
        if (holder instanceof CbLegacySuspiciousEffectHolder legacy) {
            return legacy.method_10187();
        }
        List<class_7917.class_8751> entries = holder.method_53233();
        return entries.isEmpty() ? 0 : entries.get(0).comp_1839();
    }

    private CbEffects() {
    }
}
