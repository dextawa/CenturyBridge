package top.dext.centurybridge.step.v1_20_5.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import top.dext.centurybridge.step.v1_20_5.rt.CbEffects;

/**
 * AbsorptionMobEffect, BadOmenMobEffect: the vanilla applyEffectTick bodies that can answer "the effect
 * goes". Reached as a 1.20.4 subclass's super call, that answer is the subclass's tick's (rt/CbEffects).
 */
@Mixin(targets = {"net.minecraft.class_1288", "net.minecraft.class_8631"})
public abstract class CbEffectTickMixin {

    /** applyEffectTick(entity, amplifier) */
    @Inject(method = "method_5572", at = @At("RETURN"))
    private void cbS4$answered(net.minecraft.class_1309 entity, int amplifier, CallbackInfoReturnable<Boolean> cir) {
        CbEffects.answered((net.minecraft.class_1291) (Object) this, entity, cir.getReturnValueZ());
    }
}
