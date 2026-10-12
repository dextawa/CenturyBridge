package top.dext.centurybridge.step.v1_20_5.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyVariable;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import top.dext.centurybridge.step.v1_20_5.rt.CbItemProps;
import top.dext.centurybridge.step.v1_20_5.rt.CbItemTags;

/**
 * ItemStack.OPTIONAL_STREAM_CODEC (ItemStack$1) reads the stack's component map field directly when it
 * writes a stack to the network: the 1.20.4 tag view's pending changes go to the components first. A
 * stack 1.20.4 sent without its tag -- an item whose class overrides shouldOverrideMultiplayerNbt to
 * false and cannot be depleted (rt/CbItemProps.sendsBare) -- goes as its bare item and count.
 */
@Mixin(targets = "net.minecraft.class_1799$1")
public abstract class CbItemStackStreamMixin {

    /** encode(RegistryFriendlyByteBuf, ItemStack) */
    @Inject(method = "method_56100", at = @At("HEAD"))
    private void cb$encode(net.minecraft.class_9129 buf, net.minecraft.class_1799 stack, CallbackInfo ci) {
        CbItemTags.flush(stack);
    }

    /** encode's stack, bare where 1.20.4 sent no tag */
    @ModifyVariable(method = "method_56100", at = @At("HEAD"), argsOnly = true)
    private net.minecraft.class_1799 cbS4$bare(net.minecraft.class_1799 stack) {
        if (!stack.method_7960() && CbItemProps.sendsBare(stack.method_7909())) {
            return new net.minecraft.class_1799(stack.method_41409(), stack.method_7947());
        }
        return stack;
    }
}
