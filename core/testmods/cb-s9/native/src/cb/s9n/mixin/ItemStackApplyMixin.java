package cb.s9n.mixin;

import net.minecraft.class_1799;
import net.minecraft.class_9326;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

/** A native 1.21.9 mixin on ItemStack.applyComponents(patch) RETURN: counts runs in cb.s9n.applies (cb-s9 ItemVerifyTest) */
@Mixin(class_1799.class)
public abstract class ItemStackApplyMixin {
    @Inject(method = "method_57366(Lnet/minecraft/class_9326;)V", at = @At("RETURN"))
    private void cbs9n$applied(class_9326 patch, CallbackInfo ci) {
        System.setProperty("cb.s9n.applies", String.valueOf(Integer.getInteger("cb.s9n.applies", 0) + 1));
    }
}
