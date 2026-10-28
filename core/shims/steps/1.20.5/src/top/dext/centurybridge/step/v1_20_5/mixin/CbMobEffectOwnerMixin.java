package top.dext.centurybridge.step.v1_20_5.mixin;

import java.util.Map;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import top.dext.centurybridge.step.v1_20_5.rt.CbEffectsGiveBack;

/**
 * MobEffect.addAttributeModifier(attribute, id, amount, operation) returning: the template it put is this effect's
 * (1.20.4's template held its effect; rt/CbEffectsGiveBack.field_45104). Reads only; 1.20.5's put is untouched.
 * <p>
 * A mixin of its own, apart from CbMobEffectMixin: it names CbEffectsGiveBack.TemplateOwner, which only
 * CbAttributeTemplateMixin merges, and both stop at the release that reshapes AttributeTemplate (1.21: an id where
 * 1.20.5 had a UUID). Kept in CbMobEffectMixin, the climb would strip that mixin with them, and with it the
 * CbEffects.EffectSlot interface every MobEffectInstance constructor reads (a ClassCastException in Items' class init
 * on every climbed 1.21+ server).
 */
@Mixin(net.minecraft.class_1291.class)
public abstract class CbMobEffectOwnerMixin {

    /** attributeModifiers */
    @Shadow
    @Final
    @SuppressWarnings("rawtypes")
    private Map field_5885;

    @Inject(method = "method_5566", at = @At("RETURN"))
    @SuppressWarnings("rawtypes")
    private void cbS205$ownTemplate(net.minecraft.class_6880 attribute, String id, double amount,
            net.minecraft.class_1322.class_1323 operation, CallbackInfoReturnable<net.minecraft.class_1291> cir) {
        Object template = field_5885.get(attribute);
        if (template instanceof CbEffectsGiveBack.TemplateOwner owned) {
            owned.cbS205$owner((net.minecraft.class_1291) (Object) this);
        }
    }
}
