package top.dext.centurybridge.compat.fapi.impl.attachment;

import java.util.Map;
import top.dext.centurybridge.compat.fapi.attachment.v1.AttachmentTarget;
import top.dext.centurybridge.compat.fapi.attachment.v1.AttachmentType;

/**
 * What this layer's mixins make every entity, block entity, world and chunk: an attachment target
 * holding its attachments (1.20.1's AttachmentTargetImpl). The internal methods carry this layer's
 * prefix so they never meet the ones Fabric API's own module adds where it exists.
 */
public interface AttachmentTargetImpl extends AttachmentTarget {

    /** copies the attachments of one target to another; on a death, only the types that copy on death */
    @SuppressWarnings("unchecked")
    static void transfer(AttachmentTarget original, AttachmentTarget target, boolean isDeath) {
        Map<AttachmentType<?>, ?> attachments = ((AttachmentTargetImpl) original).cb$getAttachments();
        if (attachments == null) {
            return;
        }
        for (Map.Entry<AttachmentType<?>, ?> entry : attachments.entrySet()) {
            AttachmentType<Object> type = (AttachmentType<Object>) entry.getKey();
            if (!isDeath || type.copyOnDeath()) {
                target.setAttached(type, entry.getValue());
            }
        }
    }

    default Map<AttachmentType<?>, ?> cb$getAttachments() {
        throw new UnsupportedOperationException("Implemented via mixin");
    }

    default void cb$writeAttachmentsToNbt(net.minecraft.class_2487 nbt) {
        throw new UnsupportedOperationException("Implemented via mixin");
    }

    default void cb$readAttachmentsFromNbt(net.minecraft.class_2487 nbt) {
        throw new UnsupportedOperationException("Implemented via mixin");
    }

    default boolean cb$hasPersistentAttachments() {
        throw new UnsupportedOperationException("Implemented via mixin");
    }
}
