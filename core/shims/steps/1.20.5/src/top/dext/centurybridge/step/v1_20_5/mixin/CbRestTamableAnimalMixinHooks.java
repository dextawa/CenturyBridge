package top.dext.centurybridge.step.v1_20_5.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import top.dext.centurybridge.step.v1_20_5.rt.CbPrioritySplit;

/**
 * The injector into 1.20.5's own TamableAnimal.setTame(tame, sideEffects) that goes with
 * {@link CbRestTamableAnimalMixin} (its restored 1.20.4 member stays there, at {@code CbVanillaReads.PRIORITY}):
 * 1.20.4's reassessTameGoals run at its end, except on a cat, whose own setTame override already runs it.
 */
@Mixin(net.minecraft.class_1321.class)
public abstract class CbRestTamableAnimalMixinHooks {

    @Inject(method = "method_6173(ZZ)V", at = @At("TAIL"))
    private void cbRest$reassessTameGoals(boolean tame, boolean applyTamingSideEffects, CallbackInfo ci) {
        if (!((Object) this instanceof net.minecraft.class_1451)) {
            ((CbPrioritySplit.TameGoals) (Object) this).cb$v1_20_5$reassessTameGoals();
        }
    }
}
