package top.dext.centurybridge.step.v1_21_2.mixin;

import net.minecraft.class_1273;
import net.minecraft.class_1799;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import top.dext.centurybridge.step.v1_21_2.rt.CbItemLocks;

/** LockCode.unlocksWith for a lock made from a 1.21.1 key: 1.21.1's test (CbItemLocks) */
@Mixin(class_1273.class)
public abstract class CbItemLockMixin {

    @Inject(method = "method_5472", at = @At("HEAD"), cancellable = true)
    private void cb$unlocks(class_1799 stack, CallbackInfoReturnable<Boolean> cir) {
        String key = CbItemLocks.keyOf((class_1273) (Object) this);
        if (key != null && !key.isEmpty()) {
            cir.setReturnValue(CbItemLocks.unlocks(key, stack));
        }
    }
}
