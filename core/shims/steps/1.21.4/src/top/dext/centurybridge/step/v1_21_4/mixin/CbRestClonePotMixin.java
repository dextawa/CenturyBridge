package top.dext.centurybridge.step.v1_21_4.mixin;

import net.minecraft.class_1799;
import net.minecraft.class_2338;
import net.minecraft.class_2680;
import net.minecraft.class_4538;
import net.minecraft.class_8168;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import top.dext.centurybridge.step.v1_21_4.rt.CbRestClone;

/** DecoratedPotBlock.getCloneItemStack: within a 1.21.3-shaped call, 1.21.3's body (the block entity's getPotAsItem; CbRestClone). */
@Mixin(class_8168.class)
public abstract class CbRestClonePotMixin {

    @Inject(method = "method_9574", at = @At("HEAD"), cancellable = true)
    private void cb$legacyPot(class_4538 level, class_2338 pos, class_2680 state, boolean includeData,
            CallbackInfoReturnable<class_1799> cir) {
        class_1799 stack = CbRestClone.potLegacy(level, pos);
        if (stack != null) {
            cir.setReturnValue(stack);
        }
    }
}
