package top.dext.centurybridge.step.v1_21_5.mixin;

import java.util.Optional;
import net.minecraft.class_2680;
import net.minecraft.class_2688;
import net.minecraft.class_2769;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import top.dext.centurybridge.step.v1_21_5.rt.CbCreakingHeartActive;

/**
 * StateHolder answers 1.21.4's creaking-heart ACTIVE property (rt/CbCreakingHeartActive) over 1.21.5's STATE: getValue,
 * getOptionalValue, getValueOrElse, hasProperty, setValue, trySetValue. Only the layer's own ACTIVE constant on a
 * creaking-heart state is answered (1.21.5 code never holds it); every other call runs 1.21.5's body untouched. A write
 * goes through 1.21.5's setValue of STATE, so W's own hooks on that call still fire. Merges nothing: default priority.
 */
@Mixin(class_2688.class)
public abstract class CbCreakingHeartActiveRoute {

    @Inject(method = "method_11654", at = @At("HEAD"), cancellable = true)
    private void cb$getActive(class_2769<?> property, CallbackInfoReturnable<Comparable<?>> cir) {
        if (CbCreakingHeartActive.applies(this, property)) {
            cir.setReturnValue(CbCreakingHeartActive.get((class_2680) (Object) this));
        }
    }

    @Inject(method = "method_28500", at = @At("HEAD"), cancellable = true)
    private void cb$optionalActive(class_2769<?> property, CallbackInfoReturnable<Optional<?>> cir) {
        if (CbCreakingHeartActive.applies(this, property)) {
            cir.setReturnValue(CbCreakingHeartActive.optional((class_2680) (Object) this));
        }
    }

    @Inject(method = "method_61767", at = @At("HEAD"), cancellable = true)
    private void cb$activeOrElse(class_2769<?> property, Comparable<?> fallback, CallbackInfoReturnable<Comparable<?>> cir) {
        if (CbCreakingHeartActive.applies(this, property)) {
            cir.setReturnValue(CbCreakingHeartActive.get((class_2680) (Object) this));
        }
    }

    @Inject(method = "method_28498", at = @At("HEAD"), cancellable = true)
    private void cb$hasActive(class_2769<?> property, CallbackInfoReturnable<Boolean> cir) {
        if (CbCreakingHeartActive.applies(this, property)) {
            cir.setReturnValue(true);
        }
    }

    @Inject(method = {"method_11657", "method_47968"}, at = @At("HEAD"), cancellable = true)
    private void cb$setActive(class_2769<?> property, Comparable<?> value, CallbackInfoReturnable<Object> cir) {
        if (CbCreakingHeartActive.applies(this, property)) {
            cir.setReturnValue(CbCreakingHeartActive.with((class_2680) (Object) this, value));
        }
    }
}
