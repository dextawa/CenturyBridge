package top.dext.centurybridge.shims.v1_20_2;

import java.util.Iterator;
import java.util.Map;
import net.minecraft.class_1291;
import net.minecraft.class_1309;
import net.minecraft.class_1320;
import net.minecraft.class_1322;
import net.minecraft.class_1324;
import net.minecraft.class_5131;
import net.minecraft.class_8630;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import top.dext.centurybridge.rt.v1_20_2.CbEffects;
import top.dext.centurybridge.rt.v1_20_2.CbLegacyMobEffect;
import top.dext.centurybridge.rt.v1_20_2.CbVanillaReads;

/**
 * 1.20.1 -> 1.20.2: MobEffect's attribute hooks dropped the entity / amplifier parameters, and
 * getAttributeModifierValue is gone. The 1.20.1 shapes are back with 1.20.1's bodies, inline so a 1.20.1 mod's
 * injector finds its anchors: add puts each of the effect's modifiers in at getAttributeModifierValue (an override
 * of that is honoured), remove takes them out by id, the value is the base amount times (amplifier + 1). 1.20.2
 * keeps its effect map as templates where 1.20.1 kept modifiers; each entry is read as the 1.20.1 modifier it
 * stands for (CbEffects.modifierOf). Two differences of 1.20.2's game the bodies cannot hide: 1.20.1's
 * AttributeInstance.removeModifier(AttributeModifier) is private on 1.20.2, so remove goes by the modifier's id
 * (removeModifier(UUID), what 1.20.1's did inside), and the map field is read through the mixin's shadow.
 *
 * Vanilla 1.20.2 keeps calling its own add(attributes, amplifier) / remove(attributes); the living entity consults
 * these members there (CbLivingEffectAttributes -> CbEffectAttributeRoute), and an effect whose 1.20.1 behaviour
 * differs (CbLegacyMobEffectOverride) is applied through them.
 */
@Mixin(value = class_1291.class, priority = top.dext.centurybridge.rt.v1_20_2.CbVanillaReads.PRIORITY)
public abstract class StatusEffectBridge implements CbLegacyMobEffect {

    @Shadow
    @Final
    private Map<class_1320, class_8630> field_5885;

    @Override
    @CbVanillaReads("CbLivingEffectAttributes: LivingEntity's effect removal consults it (CbEffectAttributeRoute, against its unmixed copy)")
    public void method_5562(class_1309 entity, class_5131 attributes, int amplifier) {
        Iterator<Map.Entry<class_1320, class_8630>> it = this.field_5885.entrySet().iterator();
        while (it.hasNext()) {
            Map.Entry<class_1320, class_8630> entry = it.next();
            class_1324 instance = attributes.method_26842(entry.getKey());
            if (instance != null) {
                instance.method_6200(CbEffects.modifierOf((class_1291) (Object) this, entry.getValue()).method_6189());
            }
        }
    }

    @Override
    @CbVanillaReads("CbLivingEffectAttributes: LivingEntity's effect application consults it (CbEffectAttributeRoute, against its unmixed copy)")
    public void method_5555(class_1309 entity, class_5131 attributes, int amplifier) {
        Iterator<Map.Entry<class_1320, class_8630>> it = this.field_5885.entrySet().iterator();
        while (it.hasNext()) {
            Map.Entry<class_1320, class_8630> entry = it.next();
            class_1324 instance = attributes.method_26842(entry.getKey());
            if (instance != null) {
                class_1322 modifier = CbEffects.modifierOf((class_1291) (Object) this, entry.getValue());
                instance.method_6200(modifier.method_6189());
                instance.method_26837(new class_1322(modifier.method_6189(), ((class_1291) (Object) this).method_5567() + " " + amplifier,
                    this.method_5563(amplifier, modifier), modifier.method_6182()));
            }
        }
    }

    /** 1.20.1 getAttributeModifierValue(amplifier, modifier): the base amount times (amplifier + 1) */
    @Override
    @CbVanillaReads("CbLivingEffectAttributes: read by addAttributeModifiers above, which LivingEntity's effect application consults")
    public double method_5563(int amplifier, class_1322 modifier) {
        return modifier.method_6186() * (double) (amplifier + 1);
    }
}
