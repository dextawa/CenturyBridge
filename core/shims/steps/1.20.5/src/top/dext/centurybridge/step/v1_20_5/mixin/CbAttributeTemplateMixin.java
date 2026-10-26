package top.dext.centurybridge.step.v1_20_5.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import top.dext.centurybridge.step.v1_20_5.facade.AttributeModifierTemplate;
import top.dext.centurybridge.step.v1_20_5.rt.CbEffects;
import top.dext.centurybridge.step.v1_20_5.rt.CbEffectsGiveBack;

/**
 * MobEffect.AttributeTemplate (a package-private record): its id and modifier, for 1.20.4's template view (rt/CbEffects).
 * <p>
 * 1.20.4's template (class_1291$class_8634) held the effect that made it and implemented AttributeModifierTemplate
 * (class_8630): getAttributeModifierId() its id, create(amplifier) the modifier named after that effect. 1.20.5's record
 * dropped both (javap). The effect comes back as the owner MobEffect.addAttributeModifier records (CbMobEffectMixin;
 * rt/CbEffectsGiveBack.field_45104), and with it the 1.20.4 interface, so code holding a vanilla template as a 1.20.4
 * template (a converted mod's or a climbing layer's cast to class_8630) reaches it as 1.20.4 did.
 */
@Mixin(targets = "net.minecraft.class_1291$class_8634")
public abstract class CbAttributeTemplateMixin implements CbEffects.TemplateView, CbEffectsGiveBack.TemplateOwner,
        AttributeModifierTemplate {

    /** id() */
    @Shadow
    public abstract java.util.UUID comp_2182();

    /** create(name, amplifier) */
    @Shadow
    public abstract net.minecraft.class_1322 method_52522(String name, int amplifier);

    /** the effect whose addAttributeModifier made this template (1.20.4's field_45104) */
    @Unique
    private net.minecraft.class_1291 cbS205$owner;

    @Override
    public java.util.UUID cbS4$id() {
        return comp_2182();
    }

    @Override
    public net.minecraft.class_1322 cbS4$create(String name, int amplifier) {
        return method_52522(name, amplifier);
    }

    @Override
    public net.minecraft.class_1291 cbS205$owner() {
        return cbS205$owner;
    }

    @Override
    public void cbS205$owner(net.minecraft.class_1291 effect) {
        this.cbS205$owner = effect;
    }

    /** 1.20.4 getAttributeModifierId(): the template's id */
    @Override
    public java.util.UUID method_52521() {
        return comp_2182();
    }

    /**
     * 1.20.4 create(amplifier): the modifier "effect.getDescriptionId() + ' ' + amplifier" worth amount * (amplifier + 1),
     * which is 1.20.5's create(name, amplifier) with the owner's description id as the name (javap -c both)
     */
    @Override
    public net.minecraft.class_1322 method_52522(int amplifier) {
        return method_52522(CbEffectsGiveBack.field_45104((net.minecraft.class_1291.class_8634) (Object) this).method_5567(), amplifier);
    }
}
