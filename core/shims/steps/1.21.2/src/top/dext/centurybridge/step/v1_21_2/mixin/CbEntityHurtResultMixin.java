package top.dext.centurybridge.step.v1_21_2.mixin;

import net.minecraft.class_1282;
import net.minecraft.class_1309;
import net.minecraft.class_3218;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import top.dext.centurybridge.step.v1_21_2.rt.CbEntityDragon;

/**
 * LivingEntity.hurtServer's answer, recorded for the dragon whose 1.21.1 reallyHurt is being answered (1.21.2's
 * reallyHurt drops it; CbEntityDragon). Every other call only pays a thread-local read.
 */
@Mixin(class_1309.class)
public abstract class CbEntityHurtResultMixin {

    @Inject(method = "method_64397", at = @At("RETURN"))
    private void cb$hurtResult(class_3218 level, class_1282 source, float amount, CallbackInfoReturnable<Boolean> cir) {
        CbEntityDragon.hurtResult(this, cir.getReturnValueZ());
    }
}
