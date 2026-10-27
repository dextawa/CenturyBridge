package top.dext.centurybridge.step.v1_20_5.rt;

/**
 * Fields 1.20.5 reshaped under effects, given back for the code that still reads them as 1.20.4 had them --
 * mods' mixins and the climbing 1.20.2 layer's (CbEffectInstanceLegacy, CbEffectTemplateAccess,
 * CbEffectTemplateLegacyValue, CbFlowerBlockLegacy; instanceFieldGetters, MixinBridge carries their @Shadow /
 * @Accessor reads through these).
 */
public final class CbEffectsGiveBack {

    /**
     * MobEffectInstance.effect (class_1293.field_5896): 1.20.4 kept the MobEffect, 1.20.5 keeps its holder (javap: the
     * same final field, read by getEffect()); the effect is the holder's value.
     */
    public static net.minecraft.class_1291 field_5896(net.minecraft.class_1293 instance) {
        return instance.method_5579().comp_349();
    }

    /**
     * FlowerBlock.suspiciousStewEffects (class_2356.field_45775): 1.20.4 kept the entry list, 1.20.5 keeps it as
     * SuspiciousStewEffects (the same entries, javap: the constructor builds it from the same effect and duration).
     * The field itself, not the overridable getSuspiciousEffects().
     */
    @SuppressWarnings("rawtypes")
    public static java.util.List field_45775(net.minecraft.class_2356 flower) {
        return CbBlockSuspicious.entries(flower.field_45775);
    }

    /** a 1.20.5 MobEffect.AttributeTemplate with the effect that made it (CbAttributeTemplateMixin) */
    public interface TemplateOwner {
        net.minecraft.class_1291 cbS205$owner();

        void cbS205$owner(net.minecraft.class_1291 effect);
    }

    /**
     * MobEffect.AttributeTemplate.effect (class_1291$class_8634.field_45104): 1.20.4's template held the effect that
     * made it; 1.20.5's record holds only id, amount and operation (javap). Templates are made only by
     * MobEffect.addAttributeModifier (the record's constructor is package-private, its one caller that method), which
     * records itself as the owner of the template it put (CbMobEffectOwnerMixin), so this is the same effect.
     */
    public static net.minecraft.class_1291 field_45104(net.minecraft.class_1291.class_8634 template) {
        net.minecraft.class_1291 owner = ((TemplateOwner) (Object) template).cbS205$owner();
        if (owner == null) {
            throw new IllegalStateException("attribute template " + template + " was not made by MobEffect.addAttributeModifier");
        }
        return owner;
    }

    private CbEffectsGiveBack() {
    }
}
