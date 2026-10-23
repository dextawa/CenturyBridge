package top.dext.centurybridge.shims.v1_20_2;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

/**
 * EntityTypePredicate.ANY of 1.20.1 is the sentinel CbAdvAny.field_9609 (an empty type set, told apart
 * by identity). 1.20.1's ANY matched every entity type; 1.20.2 code that meets the sentinel gets the
 * same answer.
 */
@Mixin(net.minecraft.class_2050.class)
public abstract class CbAdvEntityTypeAny {

    /** matches(type): 1.20.1's ANY answered true */
    @Inject(method = "method_8925(Lnet/minecraft/class_1299;)Z", at = @At("HEAD"), cancellable = true)
    private void cb$any(net.minecraft.class_1299<?> type, CallbackInfoReturnable<Boolean> cir) {
        if ((Object) this == top.dext.centurybridge.rt.v1_20_2.CbAdvAny.field_9609) {
            cir.setReturnValue(true);
        }
    }
}
