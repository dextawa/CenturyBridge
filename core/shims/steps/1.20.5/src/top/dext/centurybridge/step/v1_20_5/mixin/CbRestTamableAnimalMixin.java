package top.dext.centurybridge.step.v1_20_5.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

/**
 * 1.20.4 TamableAnimal.reassessTameGoals(): a protected hook, empty on TamableAnimal, that every
 * setTame(tame) ran at its end -- Cat re-registered its avoid-players goal there, mods their own goals.
 * 1.20.5 removed it from TamableAnimal (Cat keeps its own and calls it from its setTame override) and
 * runs applyTamingSideEffects() instead, only when the caller asks for side effects (not when an entity
 * loads). The hook comes back with its 1.20.4 body, so a mod's override binds to it and a mod's
 * super.reassessTameGoals() reaches it, and 1.20.5's setTame(tame, sideEffects) runs it at its end as
 * 1.20.4's setTame did -- except on a cat, whose own setTame override already runs it.
 */
@Mixin(net.minecraft.class_1321.class)
public abstract class CbRestTamableAnimalMixin {

    /** was reassessTameGoals(): 1.20.4's body */
    protected void method_6175() {
    }

    @Inject(method = "method_6173(ZZ)V", at = @At("TAIL"))
    private void cbRest$reassessTameGoals(boolean tame, boolean applyTamingSideEffects, CallbackInfo ci) {
        if (!((Object) this instanceof net.minecraft.class_1451)) {
            this.method_6175();
        }
    }
}
