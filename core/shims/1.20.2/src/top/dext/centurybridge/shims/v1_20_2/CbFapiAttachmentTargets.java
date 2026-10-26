package top.dext.centurybridge.shims.v1_20_2;

import java.util.IdentityHashMap;
import java.util.Map;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import top.dext.centurybridge.compat.fapi.attachment.v1.AttachmentType;
import top.dext.centurybridge.compat.fapi.impl.attachment.AttachmentSerializingImpl;
import top.dext.centurybridge.compat.fapi.impl.attachment.AttachmentTargetImpl;
import top.dext.centurybridge.compat.fapi.impl.attachment.CbFapiAttachments;
import top.dext.centurybridge.rt.v1_20_2.CbVanillaReads;

/**
 * Block entities, entities, worlds and chunks hold data attachments (Fabric API data attachment v1,
 * 1.20.1's AttachmentTargetsMixin): an identity map, created on the first attachment and dropped with
 * the last. Attaching to a block entity marks it dirty, to a chunk marks it for saving, as in 1.20.1.
 */
@Mixin(value = {net.minecraft.class_2586.class, net.minecraft.class_1297.class, net.minecraft.class_1937.class, net.minecraft.class_2791.class}, priority = CbVanillaReads.PRIORITY)
abstract class CbFapiAttachmentTargets implements AttachmentTargetImpl {

    @Unique
    private IdentityHashMap<AttachmentType<?>, Object> cb$dataAttachments = null;

    @Override
    @SuppressWarnings("unchecked")
    public <T> T getAttached(AttachmentType<T> type) {
        return cb$dataAttachments == null ? null : (T) cb$dataAttachments.get(type);
    }

    @Override
    @SuppressWarnings("unchecked")
    public <T> T setAttached(AttachmentType<T> type, T value) {
        Object thisObject = this;
        if (thisObject instanceof net.minecraft.class_2586 blockEntity) {
            blockEntity.method_5431();
        } else if (thisObject instanceof net.minecraft.class_2791 chunk) {
            chunk.method_12008(true);
            if (type.isPersistent() && chunk.method_12009().equals(net.minecraft.class_2806.field_12798)) {
                CbFapiAttachments.warnEmptyChunk(type);
            }
        }
        if (value == null) {
            if (cb$dataAttachments == null) {
                return null;
            }
            T removed = (T) cb$dataAttachments.remove(type);
            if (cb$dataAttachments.isEmpty()) {
                cb$dataAttachments = null;
            }
            return removed;
        }
        if (cb$dataAttachments == null) {
            cb$dataAttachments = new IdentityHashMap<>();
        }
        return (T) cb$dataAttachments.put(type, value);
    }

    @Override
    public boolean hasAttached(AttachmentType<?> type) {
        return cb$dataAttachments != null && cb$dataAttachments.containsKey(type);
    }

    @Override
    public void cb$writeAttachmentsToNbt(net.minecraft.class_2487 nbt) {
        AttachmentSerializingImpl.serializeAttachmentData(nbt, cb$dataAttachments);
    }

    @Override
    public void cb$readAttachmentsFromNbt(net.minecraft.class_2487 nbt) {
        cb$dataAttachments = AttachmentSerializingImpl.deserializeAttachmentData(nbt);
    }

    @Override
    public boolean cb$hasPersistentAttachments() {
        return AttachmentSerializingImpl.hasPersistentAttachments(cb$dataAttachments);
    }

    @Override
    public Map<AttachmentType<?>, ?> cb$getAttachments() {
        return cb$dataAttachments;
    }
}
