package top.dext.centurybridge.step.v1_20_5.mixin;

import java.util.Map;
import java.util.function.BiConsumer;
import java.util.function.Supplier;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import top.dext.centurybridge.step.v1_20_5.facade.AttributeModifierTemplate;
import top.dext.centurybridge.step.v1_20_5.rt.CbEffects;

/**
 * MobEffect: the 1.20.4 factor data factory a mod gave the effect, the attribute templates of a mod's own
 * kind it was given, and where 1.20.5 lists, applies and removes an effect's modifiers, those templates
 * too -- listed from a class's own 1.20.4 getAttributeModifiers() where 1.20.4 listed them (rt/CbEffects).
 */
@Mixin(net.minecraft.class_1291.class)
public abstract class CbMobEffectMixin implements CbEffects.EffectSlot {

    /** attributeModifiers */
    @Shadow
    @Final
    @SuppressWarnings("rawtypes")
    private Map field_5885;

    @Unique
    @SuppressWarnings("rawtypes")
    private Supplier cbS4$factorDataFactory;

    @Unique
    private Map<net.minecraft.class_1320, AttributeModifierTemplate> cbS4$legacyTemplates = CbEffects.newLegacyTemplates();

    @Override
    @SuppressWarnings("rawtypes")
    public Supplier cbS4$factorDataFactory() {
        return cbS4$factorDataFactory;
    }

    @Override
    @SuppressWarnings("rawtypes")
    public void cbS4$factorDataFactory(Supplier factory) {
        this.cbS4$factorDataFactory = factory;
    }

    @Override
    public Map<net.minecraft.class_1320, AttributeModifierTemplate> cbS4$legacyTemplates() {
        return cbS4$legacyTemplates;
    }

    @Override
    @SuppressWarnings("rawtypes")
    public Map cbS4$templates() {
        return field_5885;
    }

    /** createModifiers(amplifier, out): a class overriding 1.20.4's getAttributeModifiers() lists from it */
    @Inject(method = "method_55650", at = @At("HEAD"), cancellable = true)
    @SuppressWarnings("rawtypes")
    private void cbS4$listOwn(int amplifier, BiConsumer out, CallbackInfo ci) {
        if (CbEffects.listedOwn((net.minecraft.class_1291) (Object) this, amplifier, out)) {
            ci.cancel();
        }
    }

    /** createModifiers returning: a mod's own templates' modifiers too */
    @Inject(method = "method_55650", at = @At("RETURN"))
    @SuppressWarnings("rawtypes")
    private void cbS4$listLegacy(int amplifier, BiConsumer out, CallbackInfo ci) {
        CbEffects.listedLegacy((net.minecraft.class_1291) (Object) this, amplifier, out);
    }

    /** addAttributeModifiers(attributes, amplifier) returning */
    @Inject(method = "method_5555", at = @At("RETURN"))
    private void cbS4$addLegacy(net.minecraft.class_5131 attributes, int amplifier, CallbackInfo ci) {
        CbEffects.addedLegacy((net.minecraft.class_1291) (Object) this, attributes, amplifier);
    }

    /** removeAttributeModifiers(attributes) returning */
    @Inject(method = "method_5562", at = @At("RETURN"))
    private void cbS4$removeLegacy(net.minecraft.class_5131 attributes, CallbackInfo ci) {
        CbEffects.removedLegacy((net.minecraft.class_1291) (Object) this, attributes);
    }

    /**
     * addAttributeModifier(attribute, id, amount, operation) returning: the template it put is this effect's (1.20.4's
     * template held its effect; rt/CbEffectsGiveBack.field_45104). Reads only; 1.20.5's put is untouched.
     */
    @Inject(method = "method_5566", at = @At("RETURN"))
    @SuppressWarnings("rawtypes")
    private void cbS205$ownTemplate(net.minecraft.class_6880 attribute, String id, double amount,
            net.minecraft.class_1322.class_1323 operation, org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable<net.minecraft.class_1291> cir) {
        Object template = field_5885.get(attribute);
        if (template instanceof top.dext.centurybridge.step.v1_20_5.rt.CbEffectsGiveBack.TemplateOwner owned) {
            owned.cbS205$owner((net.minecraft.class_1291) (Object) this);
        }
    }
}
