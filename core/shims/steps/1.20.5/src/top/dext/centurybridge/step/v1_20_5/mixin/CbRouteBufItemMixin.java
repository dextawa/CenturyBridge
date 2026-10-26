package top.dext.centurybridge.step.v1_20_5.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import top.dext.centurybridge.step.v1_20_5.rt.CbRoutes;

/**
 * Route for 1.20.4's FriendlyByteBuf.writeItem / readItem (restored by CbNetFriendlyByteBufBridge, which writes and
 * reads through ItemStack.OPTIONAL_STREAM_CODEC): 1.20.4's packets wrote and read every item stack through them;
 * 1.20.5's use ItemStack.OPTIONAL_STREAM_CODEC (this anonymous codec) directly. With a converted mod mixing the
 * restored member, the codec asks it; the restored member's own codec call (CbRoutes.enterIo) then runs as 1.20.5
 * has it, so the bytes are 1.20.5's and native 1.20.5 mixins on the codec still run once. Unmixed it runs as 1.20.5
 * has it.
 */
@Mixin(targets = "net/minecraft/class_1799$1", priority = CbRoutes.ROUTE_PRIORITY)
public abstract class CbRouteBufItemMixin {

    @Inject(method = "method_56100", at = @At("HEAD"), cancellable = true)
    private void cbRoute$write(net.minecraft.class_9129 buf, net.minecraft.class_1799 stack, CallbackInfo ci) {
        if (CbRoutes.routeIo(CbRoutes.BUF_WRITE_ITEM)) {
            ((CbRoutes.LegacyBuf) buf).method_10793(stack);
            ci.cancel();
        }
    }

    @Inject(method = "method_56099", at = @At("HEAD"), cancellable = true)
    private void cbRoute$read(net.minecraft.class_9129 buf, CallbackInfoReturnable<net.minecraft.class_1799> cir) {
        if (CbRoutes.routeIo(CbRoutes.BUF_READ_ITEM)) {
            cir.setReturnValue(((CbRoutes.LegacyBuf) buf).method_10819());
        }
    }
}
