package top.dext.centurybridge.compat.fapi.impl.attachment;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import top.dext.centurybridge.compat.fapi.attachment.v1.AttachmentType;

/** The log line 1.20.1's attachment targets wrote (through AttachmentEntrypoint's logger), kept out of the mixin classes. */
public final class CbFapiAttachments {

    private static final Logger LOGGER = LoggerFactory.getLogger("fabric-data-attachment-api-v1");

    private CbFapiAttachments() {
    }

    public static void warnEmptyChunk(AttachmentType<?> type) {
        LOGGER.warn("Attaching persistent attachment {} to chunk with chunk status EMPTY. Attachment might be discarded.", type.identifier());
    }
}
