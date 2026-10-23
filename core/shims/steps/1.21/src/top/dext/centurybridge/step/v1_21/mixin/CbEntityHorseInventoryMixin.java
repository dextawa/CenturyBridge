package top.dext.centurybridge.step.v1_21.mixin;

import net.minecraft.class_1496;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import top.dext.centurybridge.step.v1_21.rt.CbEntityAnimals;
import top.dext.centurybridge.step.v1_21.rt.Guard;

/**
 * AbstractHorse.getInventorySize(), final in 1.21: a 1.20.6 horse that overrides it answers with its own (renamed)
 * method, which sizes the inventory as in 1.20.6. The override's super call comes back here with the guard held
 * and runs 1.21's body.
 */
@Mixin(class_1496.class)
public abstract class CbEntityHorseInventoryMixin {

    @Inject(method = "method_6750()I", at = @At("HEAD"), cancellable = true)
    private void cb$legacyInventorySize(CallbackInfoReturnable<Integer> cir) {
        Object self = this;
        if (self instanceof CbEntityAnimals.LegacyInventorySize legacy && Guard.cbEnter(self)) {
            try {
                cir.setReturnValue(legacy.method_6750$cb1_21());
            } finally {
                Guard.cbExit(self);
            }
        }
    }
}
