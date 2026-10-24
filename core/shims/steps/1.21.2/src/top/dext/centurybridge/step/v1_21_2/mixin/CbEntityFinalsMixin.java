package top.dext.centurybridge.step.v1_21_2.mixin;

import net.minecraft.class_1304;
import net.minecraft.class_1309;
import net.minecraft.class_1799;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import top.dext.centurybridge.step.v1_21_2.rt.CbEntityFinals;

/** 1.21.2's final getScale / getEquipmentSlotForItem answered by a mod class's renamed 1.21.1 override (CbEntityFinals). */
@Mixin(class_1309.class)
public abstract class CbEntityFinalsMixin {

    @Inject(method = "method_55693", at = @At("HEAD"), cancellable = true)
    private void cb$scale(CallbackInfoReturnable<Float> cir) {
        Float r = CbEntityFinals.scale((class_1309) (Object) this);
        if (r != null) {
            cir.setReturnValue(r);
        }
    }

    @Inject(method = "method_32326", at = @At("HEAD"), cancellable = true)
    private void cb$slot(class_1799 stack, CallbackInfoReturnable<class_1304> cir) {
        class_1304 r = CbEntityFinals.slot((class_1309) (Object) this, stack);
        if (r != null) {
            cir.setReturnValue(r);
        }
    }
}
