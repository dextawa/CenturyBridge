package top.dext.centurybridge.step.v1_21_4.mixin;

import net.minecraft.class_1799;
import net.minecraft.class_2338;
import net.minecraft.class_2680;
import net.minecraft.class_4538;
import net.minecraft.class_4970;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import top.dext.centurybridge.step.v1_21_4.rt.CbRestClone;

/**
 * BlockBehaviour.getCloneItemStack: within a 1.21.3-shaped call, a ShulkerBoxBlock's stack takes its block
 * entity as 1.21.3's ShulkerBoxBlock body did (1.21.4's ShulkerBoxBlock inherits this method; CbRestClone).
 */
@Mixin(class_4970.class)
public abstract class CbRestCloneBlocksMixin {

    @Inject(method = "method_9574", at = @At("RETURN"))
    private void cb$legacyShulker(class_4538 level, class_2338 pos, class_2680 state, boolean includeData,
            CallbackInfoReturnable<class_1799> cir) {
        CbRestClone.shulkerExtras((class_4970) (Object) this, level, pos, cir.getReturnValue());
    }
}
