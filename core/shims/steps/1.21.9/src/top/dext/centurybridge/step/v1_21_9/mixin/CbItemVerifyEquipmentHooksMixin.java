package top.dext.centurybridge.step.v1_21_9.mixin;

import net.minecraft.class_10630;
import net.minecraft.class_1304;
import net.minecraft.class_1799;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import top.dext.centurybridge.step.v1_21_9.rt.CbPrioritySplit;

/**
 * 1.21.8's EntityEquipment.set(slot, stack) began with stack.getItem().verifyComponentsAfterLoad(stack) (javap -c);
 * 1.21.9 dropped the call. It comes back at HEAD (default priority), through CbItemRestoredMixin's restored member.
 */
@Mixin(class_10630.class)
public abstract class CbItemVerifyEquipmentHooksMixin {

    @Inject(method = "method_66660(Lnet/minecraft/class_1304;Lnet/minecraft/class_1799;)Lnet/minecraft/class_1799;", at = @At("HEAD"))
    private void cb$v1_21_9$verifyOnSet(class_1304 slot, class_1799 stack, CallbackInfoReturnable<class_1799> cir) {
        ((CbPrioritySplit.ItemVerify) stack.method_7909()).cb$v1_21_9$verifyComponentsAfterLoad(stack);
    }
}
