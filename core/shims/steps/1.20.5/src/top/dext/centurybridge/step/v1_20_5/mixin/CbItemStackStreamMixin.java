package top.dext.centurybridge.step.v1_20_5.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import top.dext.centurybridge.step.v1_20_5.rt.CbItemTags;

/**
 * ItemStack.OPTIONAL_STREAM_CODEC (ItemStack$1) reads the stack's component map field directly when it
 * writes a stack to the network: the 1.20.4 tag view's pending changes go to the components first.
 */
@Mixin(targets = "net.minecraft.class_1799$1")
public abstract class CbItemStackStreamMixin {

    /** encode(RegistryFriendlyByteBuf, ItemStack) */
    @Inject(method = "method_56100", at = @At("HEAD"))
    private void cb$encode(net.minecraft.class_9129 buf, net.minecraft.class_1799 stack, CallbackInfo ci) {
        CbItemTags.flush(stack);
    }
}
