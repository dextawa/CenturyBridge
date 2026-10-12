package top.dext.centurybridge.step.v1_20_5.mixin;

import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Mutable;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import top.dext.centurybridge.step.v1_20_5.rt.CbItemComponents;
import top.dext.centurybridge.step.v1_20_5.rt.CbItemProps;
import top.dext.centurybridge.step.v1_20_5.rt.CbTools;

/**
 * Item for a converted 1.20.4 one: a tool made by a 1.20.4 constructor keeps the attack modifiers it was
 * made with and answers getDefaultAttributeModifiers() with them (rt/CbTools); an item whose class
 * overrides 1.20.4's isEdible / getFoodProperties / isFireResistant gets its default components answered
 * by those overrides (rt/CbItemComponents); one overriding canBeDepleted is enchantable by 1.20.4's rule.
 */
@Mixin(net.minecraft.class_1792.class)
public abstract class CbItemMixin implements CbTools.Base {

    /** components */
    @Shadow
    @Final
    @Mutable
    private net.minecraft.class_9323 field_49263;

    @Unique
    private net.minecraft.class_9285 cbS4$base;

    @Override
    public net.minecraft.class_9285 cbS4$base() {
        return cbS4$base;
    }

    @Inject(method = "<init>", at = @At("RETURN"))
    private void cbS4$made(net.minecraft.class_1792.class_1793 properties, CallbackInfo ci) {
        cbS4$base = CbTools.claim(properties);
        Object self = this;
        if (self instanceof CbItemProps.Edible || self instanceof CbItemProps.Food || self instanceof CbItemProps.FireResistant) {
            field_49263 = new CbItemComponents((net.minecraft.class_1792) self, field_49263);
        }
    }

    /** getDefaultAttributeModifiers() */
    @Inject(method = "method_7844", at = @At("HEAD"), cancellable = true)
    private void cbS4$modifiers(CallbackInfoReturnable<net.minecraft.class_9285> cir) {
        if (cbS4$base != null) {
            cir.setReturnValue(cbS4$base);
        }
    }

    /** isEnchantable(stack): 1.20.4 asked canBeDepleted() */
    @Inject(method = "method_7870", at = @At("HEAD"), cancellable = true)
    private void cbS4$enchantable(net.minecraft.class_1799 stack, CallbackInfoReturnable<Boolean> cir) {
        if ((Object) this instanceof CbItemProps.CanBeDepleted) {
            cir.setReturnValue(CbItemProps.isEnchantable1204((net.minecraft.class_1792) (Object) this));
        }
    }
}
