package top.dext.centurybridge.shims.v1_20_2;

import java.util.Map;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import top.dext.centurybridge.compat.fapi.attachment.v1.AttachmentType;
import top.dext.centurybridge.compat.fapi.impl.attachment.AttachmentTargetImpl;
import top.dext.centurybridge.rt.v1_20_2.CbVanillaReads;

/**
 * A read-only wrapper around a world chunk is the world chunk as far as attachments go
 * (1.20.1's WrapperProtoChunkMixin): every attachment call goes to the wrapped chunk.
 */
@Mixin(value = net.minecraft.class_2821.class, priority = CbVanillaReads.PRIORITY)
abstract class CbFapiWrapperProtoChunkAttachments implements AttachmentTargetImpl {

    @Shadow @Final private net.minecraft.class_2818 field_12866;

    private AttachmentTargetImpl cb$wrapped() {
        return (AttachmentTargetImpl) field_12866;
    }

    @Override
    public <T> T getAttached(AttachmentType<T> type) {
        return cb$wrapped().getAttached(type);
    }

    @Override
    public <T> T setAttached(AttachmentType<T> type, T value) {
        return cb$wrapped().setAttached(type, value);
    }

    @Override
    public boolean hasAttached(AttachmentType<?> type) {
        return cb$wrapped().hasAttached(type);
    }

    @Override
    public void cb$writeAttachmentsToNbt(net.minecraft.class_2487 nbt) {
        cb$wrapped().cb$writeAttachmentsToNbt(nbt);
    }

    @Override
    public void cb$readAttachmentsFromNbt(net.minecraft.class_2487 nbt) {
        cb$wrapped().cb$readAttachmentsFromNbt(nbt);
    }

    @Override
    public boolean cb$hasPersistentAttachments() {
        return cb$wrapped().cb$hasPersistentAttachments();
    }

    @Override
    public Map<AttachmentType<?>, ?> cb$getAttachments() {
        return cb$wrapped().cb$getAttachments();
    }
}
