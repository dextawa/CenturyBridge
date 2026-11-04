package top.dext.centurybridge.step.v1_21_4.mixin;

import net.minecraft.class_10319;
import net.minecraft.class_1937;
import net.minecraft.class_2338;
import net.minecraft.class_2680;
import net.minecraft.class_2689;
import net.minecraft.class_2248;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import top.dext.centurybridge.step.v1_21_4.rt.CbRestCreakingHeart;

/** Restores V's property in the real state definition and V's ACTIVE-only comparator gate. */
@Mixin(class_10319.class)
public abstract class CbRestCreakingHeartBlockMixin {

    @Inject(method = "method_9515", at = @At("RETURN"))
    private void cb$restoreCreakingProperty(class_2689.class_2690<class_2248, class_2680> builder,
            CallbackInfo ci) {
        CbRestCreakingHeart.appendProperty(builder);
    }

    @Inject(method = "method_9572", at = @At("HEAD"), cancellable = true)
    private void cb$dormantHasNoComparatorSignal(class_2680 state, class_1937 level, class_2338 pos,
            CallbackInfoReturnable<Integer> cir) {
        if (!CbRestCreakingHeart.emitsComparatorSignal(state)) {
            cir.setReturnValue(0);
        }
    }
}
