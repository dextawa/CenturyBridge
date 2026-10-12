package top.dext.centurybridge.shims.v1_20_2;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import top.dext.centurybridge.compat.fapi.impl.attachment.AttachmentTargetImpl;

/**
 * Entity attachments are saved and loaded with the entity (1.20.1's EntityMixin): read and written
 * just before the entity reads / writes its own custom data.
 */
@Mixin(net.minecraft.class_1297.class)
abstract class CbFapiEntityAttachments {

    @Inject(method = "method_5651", at = @At(value = "INVOKE", target = "Lnet/minecraft/class_1297;method_5749(Lnet/minecraft/class_2487;)V"))
    private void cb$readEntityAttachments(net.minecraft.class_2487 nbt, CallbackInfo ci) {
        ((AttachmentTargetImpl) (Object) this).cb$readAttachmentsFromNbt(nbt);
    }

    @Inject(method = "method_5647", at = @At(value = "INVOKE", target = "Lnet/minecraft/class_1297;method_5652(Lnet/minecraft/class_2487;)V"))
    private void cb$writeEntityAttachments(net.minecraft.class_2487 nbt, CallbackInfoReturnable<net.minecraft.class_2487> cir) {
        ((AttachmentTargetImpl) (Object) this).cb$writeAttachmentsToNbt(nbt);
    }
}
