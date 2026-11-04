package top.dext.centurybridge.step.v1_21_4.mixin;

import net.minecraft.class_2680;
import net.minecraft.class_2688;
import net.minecraft.class_2769;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import top.dext.centurybridge.step.v1_21_4.rt.CbRestCreakingHeart;

/** Keeps the restored enum property and W's ACTIVE boolean coherent on every state write. */
@Mixin(class_2688.class)
public abstract class CbRestCreakingHeartStateMixin {

    @Inject(method = {"method_11657", "method_47968"}, at = @At("RETURN"), cancellable = true)
    private void cb$convertCreakingHeartState(class_2769<?> property, Comparable<?> value,
            CallbackInfoReturnable<Object> cir) {
        if (cir.getReturnValue() instanceof class_2680 state) {
            Object converted = CbRestCreakingHeart.convertState(state, property);
            if (converted != state) {
                cir.setReturnValue(converted);
            }
        }
    }
}
