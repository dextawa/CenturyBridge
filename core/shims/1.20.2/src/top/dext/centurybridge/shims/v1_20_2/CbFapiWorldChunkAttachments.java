package top.dext.centurybridge.shims.v1_20_2;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import top.dext.centurybridge.compat.fapi.impl.attachment.AttachmentTargetImpl;

/** A world chunk made from a proto chunk takes the proto chunk's attachments (1.20.1's WorldChunkMixin). */
@Mixin(net.minecraft.class_2818.class)
abstract class CbFapiWorldChunkAttachments {

    @Inject(method = "<init>(Lnet/minecraft/class_3218;Lnet/minecraft/class_2839;Lnet/minecraft/class_2818$class_6829;)V", at = @At("TAIL"))
    private void cb$transferProtoChunkAttachments(net.minecraft.class_3218 world, net.minecraft.class_2839 protoChunk,
                                                  net.minecraft.class_2818.class_6829 entityLoader, CallbackInfo ci) {
        AttachmentTargetImpl.transfer((AttachmentTargetImpl) protoChunk, (AttachmentTargetImpl) (Object) this, false);
    }
}
