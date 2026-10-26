package cb.smx4bn.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

/** 1.20.5 ItemStack.OPTIONAL_STREAM_CODEC (ItemStack$1) encode / decode */
@Mixin(targets = "net/minecraft/class_1799$1")
public abstract class NativeItemCodecMixin {
    @Inject(method = "method_56100(Lnet/minecraft/class_9129;Lnet/minecraft/class_1799;)V", at = @At("HEAD"), remap = false)
    private void cbsmx4bn$encode(net.minecraft.class_9129 buf, net.minecraft.class_1799 stack, CallbackInfo ci) {
        cb.smx4bn.Driver.itemEncode++;
    }

    @Inject(method = "method_56099(Lnet/minecraft/class_9129;)Lnet/minecraft/class_1799;", at = @At("HEAD"), remap = false)
    private void cbsmx4bn$decode(net.minecraft.class_9129 buf, CallbackInfoReturnable<net.minecraft.class_1799> cir) {
        cb.smx4bn.Driver.itemDecode++;
    }
}
