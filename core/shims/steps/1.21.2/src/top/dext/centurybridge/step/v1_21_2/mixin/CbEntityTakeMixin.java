package top.dext.centurybridge.step.v1_21_2.mixin;

import net.minecraft.class_1309;
import net.minecraft.class_1799;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import top.dext.centurybridge.step.v1_21_2.rt.CbEntityTake;

/**
 * LivingEntity's final 1.21.2 canEquipWithDispenser: where 1.21.1's dispenser asked canTakeItem, a mod class's own
 * 1.21.1 override answers (CbEntityTake#dispenserEquip).
 */
@Mixin(class_1309.class)
public abstract class CbEntityTakeMixin {

    @Inject(method = "method_63625", at = @At("HEAD"), cancellable = true)
    private void cb$canTakeItem(class_1799 stack, CallbackInfoReturnable<Boolean> cir) {
        Boolean r = CbEntityTake.dispenserEquip((class_1309) (Object) this, stack);
        if (r != null) {
            cir.setReturnValue(r);
        }
    }
}
