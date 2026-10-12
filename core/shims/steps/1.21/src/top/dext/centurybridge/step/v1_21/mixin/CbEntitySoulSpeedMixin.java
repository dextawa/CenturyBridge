package top.dext.centurybridge.step.v1_21.mixin;

import net.minecraft.class_1309;
import net.minecraft.class_2338;
import net.minecraft.class_3218;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import top.dext.centurybridge.step.v1_21.rt.CbEntitySoulSpeed;

/**
 * LivingEntity and soul speed (CbEntitySoulSpeed): for a 1.20.6 subclass that overrides the soul speed methods,
 * 1.20.6's drive runs again after 1.21's location effects (onChangedBlock) and its particles after 1.21's base
 * tick, through the subclass's own methods. Any other entity is 1.21's alone.
 */
@Mixin(class_1309.class)
public abstract class CbEntitySoulSpeedMixin {

    @Inject(method = "method_6126(Lnet/minecraft/class_3218;Lnet/minecraft/class_2338;)V", at = @At("TAIL"))
    private void cb$legacySoulSpeed(class_3218 level, class_2338 pos, CallbackInfo ci) {
        class_1309 self = (class_1309) (Object) this;
        if (CbEntitySoulSpeed.drivesItself(self)) {
            CbEntitySoulSpeed.onChangedBlock(self);
        }
    }

    @Inject(method = "method_5670()V", at = @At("TAIL"))
    private void cb$legacySoulParticles(CallbackInfo ci) {
        CbEntitySoulSpeed.baseTick((class_1309) (Object) this);
    }
}
