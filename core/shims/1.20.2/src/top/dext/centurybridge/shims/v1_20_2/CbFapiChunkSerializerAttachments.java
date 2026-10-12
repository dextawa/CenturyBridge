package top.dext.centurybridge.shims.v1_20_2;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import top.dext.centurybridge.compat.fapi.impl.attachment.AttachmentTargetImpl;

/**
 * Chunk attachments are saved and loaded with the chunk (1.20.1's ChunkSerializerMixin). Written into
 * the NBT serialize answers. Read into the chunk deserialize answers -- a full chunk's read-only
 * wrapper hands them to the world chunk it wraps (CbFapiWrapperProtoChunkAttachments) -- where
 * 1.20.1's Fabric API read them into the chunk as it was constructed; the vanilla code between the
 * construction and the return does not touch attachments, so the chunk leaves deserialize holding the
 * same attachments either way.
 */
@Mixin(net.minecraft.class_2852.class)
abstract class CbFapiChunkSerializerAttachments {

    @Inject(method = "method_12395", at = @At("RETURN"))
    private static void cb$readChunkAttachments(net.minecraft.class_3218 world, net.minecraft.class_4153 poiStorage,
                                                net.minecraft.class_1923 chunkPos, net.minecraft.class_2487 nbt,
                                                CallbackInfoReturnable<net.minecraft.class_2839> cir) {
        ((AttachmentTargetImpl) cir.getReturnValue()).cb$readAttachmentsFromNbt(nbt);
    }

    @Inject(method = "method_12410", at = @At("RETURN"))
    private static void cb$writeChunkAttachments(net.minecraft.class_3218 world, net.minecraft.class_2791 chunk,
                                                 CallbackInfoReturnable<net.minecraft.class_2487> cir) {
        ((AttachmentTargetImpl) chunk).cb$writeAttachmentsToNbt(cir.getReturnValue());
    }
}
