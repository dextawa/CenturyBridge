package top.dext.centurybridge.step.v1_20_5.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import top.dext.centurybridge.step.v1_20_5.facade.FactorData;
import top.dext.centurybridge.step.v1_20_5.rt.CbEffects;

/**
 * MobEffectInstance: the 1.20.4 factor data of an instance whose effect makes one -- made with the
 * instance, ticked with it, answering 1.20.5's blend factor, carried across a blend copy, at rest when
 * blending is skipped, saved and loaded under 1.20.4's key -- and 1.20.5's save() answered by a 1.20.4
 * class's own save(tag) (rt/CbEffects).
 */
@Mixin(net.minecraft.class_1293.class)
public abstract class CbEffectInstanceMixin implements CbEffects.InstanceSlot, CbEffects.HiddenField {

    /** hiddenEffect */
    @Shadow
    private net.minecraft.class_1293 field_21830;

    @Unique
    private FactorData cbS4$factorData;

    @Override
    public FactorData cbS4$factorData() {
        return cbS4$factorData;
    }

    @Override
    public void cbS4$factorData(FactorData data) {
        this.cbS4$factorData = data;
    }

    @Override
    public net.minecraft.class_1293 cbS4$hidden() {
        return field_21830;
    }

    /** the constructor every other but the copy one reaches */
    @Inject(method = "<init>(Lnet/minecraft/class_6880;IIZZZLnet/minecraft/class_1293;)V", at = @At("RETURN"))
    private void cbS4$made(CallbackInfo ci) {
        CbEffects.made((net.minecraft.class_1293) (Object) this);
    }

    /** the copy constructor */
    @Inject(method = "<init>(Lnet/minecraft/class_1293;)V", at = @At("RETURN"))
    private void cbS4$copied(CallbackInfo ci) {
        CbEffects.copied((net.minecraft.class_1293) (Object) this);
    }

    /** tick(entity, onExpire) */
    @Inject(method = "method_5585", at = @At("RETURN"))
    private void cbS4$ticked(net.minecraft.class_1309 entity, Runnable onExpire, CallbackInfoReturnable<Boolean> cir) {
        CbEffects.ticked((net.minecraft.class_1293) (Object) this);
    }

    /** getBlendFactor(entity, partialTick) */
    @Inject(method = "method_55653", at = @At("HEAD"), cancellable = true)
    private void cbS4$blendFactor(net.minecraft.class_1309 entity, float partialTick, CallbackInfoReturnable<Float> cir) {
        Float f = CbEffects.blendFactor((net.minecraft.class_1293) (Object) this, entity, partialTick);
        if (f != null) {
            cir.setReturnValue(f);
        }
    }

    /** skipBlending() */
    @Inject(method = "method_55657", at = @At("RETURN"))
    private void cbS4$skippedBlending(CallbackInfo ci) {
        CbEffects.skippedBlending((net.minecraft.class_1293) (Object) this);
    }

    /** copyBlendState(other) */
    @Inject(method = "method_55656", at = @At("RETURN"))
    private void cbS4$copiedBlendState(net.minecraft.class_1293 other, CallbackInfo ci) {
        CbEffects.copiedBlendState((net.minecraft.class_1293) (Object) this, other);
    }

    /** save(): a 1.20.4 class's own save(tag) answers, as 1.20.4 saved effects with it */
    @Inject(method = "method_5582()Lnet/minecraft/class_2520;", at = @At("HEAD"), cancellable = true)
    private void cbS4$saveOwn(CallbackInfoReturnable<net.minecraft.class_2520> cir) {
        if ((Object) this instanceof CbEffects.Saves s) {
            cir.setReturnValue(s.method_5582(new net.minecraft.class_2487()));
        }
    }

    /** save() returning: the factor data beside it */
    @Inject(method = "method_5582()Lnet/minecraft/class_2520;", at = @At("RETURN"), cancellable = true)
    private void cbS4$saved(CallbackInfoReturnable<net.minecraft.class_2520> cir) {
        cir.setReturnValue(CbEffects.saved((net.minecraft.class_1293) (Object) this, cir.getReturnValue()));
    }

    /** load(tag) */
    @Inject(method = "method_5583", at = @At("RETURN"))
    private static void cbS4$loaded(net.minecraft.class_2487 tag, CallbackInfoReturnable<net.minecraft.class_1293> cir) {
        CbEffects.loaded(cir.getReturnValue(), tag);
    }
}
