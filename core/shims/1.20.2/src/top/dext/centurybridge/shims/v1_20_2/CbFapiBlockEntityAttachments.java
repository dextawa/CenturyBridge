package top.dext.centurybridge.shims.v1_20_2;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import top.dext.centurybridge.compat.fapi.impl.attachment.AttachmentTargetImpl;

/**
 * Block entity attachments are saved and loaded with the block entity (1.20.1's BlockEntityMixin): read
 * just before the block entity reads its own data when it is created from NBT, written into the NBT
 * createNbt answers.
 */
@Mixin(net.minecraft.class_2586.class)
abstract class CbFapiBlockEntityAttachments {

    @Inject(method = "method_17897", at = @At(value = "INVOKE", target = "Lnet/minecraft/class_2586;method_11014(Lnet/minecraft/class_2487;)V"))
    private static void cb$readBlockEntityAttachments(net.minecraft.class_2487 nbt, String id, net.minecraft.class_2586 blockEntity,
                                                      CallbackInfoReturnable<net.minecraft.class_2586> cir) {
        ((AttachmentTargetImpl) blockEntity).cb$readAttachmentsFromNbt(nbt);
    }

    @Inject(method = "method_38244", at = @At("RETURN"))
    private void cb$writeBlockEntityAttachments(CallbackInfoReturnable<net.minecraft.class_2487> cir) {
        ((AttachmentTargetImpl) (Object) this).cb$writeAttachmentsToNbt(cir.getReturnValue());
    }
}
