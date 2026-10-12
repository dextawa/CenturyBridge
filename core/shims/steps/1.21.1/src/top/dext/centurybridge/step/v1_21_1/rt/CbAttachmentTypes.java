package top.dext.centurybridge.step.v1_21_1.rt;

import net.fabricmc.fabric.api.attachment.v1.AttachmentType;

/**
 * 1.21.1's Fabric API gave AttachmentType an abstract isSynced(): "whether this attachment type can be
 * synchronized with clients ... as per its AttachmentSyncPredicate" (javadoc; Fabric API made it abstract,
 * no default). A type written against 1.21 has no sync codec and no sync predicate -- 1.21's attachment API
 * had no synchronization -- so it cannot be synchronized: false. It must be false, too: Fabric's sync code
 * (AttachmentTargetsMixin and the sync mixins of 1.21.1's fabric-data-attachment-api-v1) takes the codec and
 * predicate of every type that answers true off Fabric's own AttachmentTypeImpl, which a mod's type is not.
 */
public final class CbAttachmentTypes {

    private CbAttachmentTypes() {
    }

    public static boolean isSynced(AttachmentType<?> type) {
        return false;
    }
}
