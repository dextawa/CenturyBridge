package cb.s9.mixin;

import net.minecraft.class_1792;
import net.minecraft.class_1799;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

/** a 1.21.8 mod's mixin on Item.verifyComponentsAfterLoad(stack) HEAD: counts the stacks vanilla verifies (ItemVerifyTest) */
@Mixin(class_1792.class)
public abstract class ItemVerifyMixin {
    @Inject(method = "method_7860(Lnet/minecraft/class_1799;)V", at = @At("HEAD"))
    private void cbs9$verified(class_1799 stack, CallbackInfo ci) {
        if (stack.method_7909() == cb.s9.ItemVerifyTest.WATCHED) {
            cb.s9.ItemVerifyTest.CALLS.incrementAndGet();
        }
    }
}
