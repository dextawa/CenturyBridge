package top.dext.centurybridge.step.v1_20_5.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import top.dext.centurybridge.step.v1_20_5.rt.CbEffects;

/** BadOmenMobEffect's applyEffectTick answer, as CbEffectTickMixin's for AbsorptionMobEffect */
@Mixin(targets = "net.minecraft.class_8631")
public abstract class CbEffectTickBadOmenMixin {

    /** applyEffectTick(entity, amplifier) */
    @Inject(method = "method_5572", at = @At("RETURN"))
    private void cbS4$answered(net.minecraft.class_1309 entity, int amplifier, CallbackInfoReturnable<Boolean> cir) {
        CbEffects.answered((net.minecraft.class_1291) (Object) this, entity, cir.getReturnValueZ());
    }
}
