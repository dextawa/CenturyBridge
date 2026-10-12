package top.dext.centurybridge.compat.fapi.impl.attachment;

import com.mojang.serialization.Codec;
import java.util.IdentityHashMap;
import java.util.Map;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import top.dext.centurybridge.compat.fapi.attachment.v1.AttachmentTarget;
import top.dext.centurybridge.compat.fapi.attachment.v1.AttachmentType;

/**
 * 1.20.1's AttachmentSerializingImpl: the persistent attachments of a target, under the
 * "fabric:attachments" compound of its NBT, each by its type's id through its codec. The same key and
 * layout as Fabric API's, so data saved by either reads back in the other.
 */
public final class AttachmentSerializingImpl {

    private static final Logger LOGGER = LoggerFactory.getLogger("fabric-data-attachment-api-v1");

    private AttachmentSerializingImpl() {
    }

    @SuppressWarnings("unchecked")
    public static void serializeAttachmentData(net.minecraft.class_2487 nbt, IdentityHashMap<AttachmentType<?>, ?> attachments) {
        if (attachments == null || attachments.isEmpty()) {
            return;
        }
        net.minecraft.class_2487 compound = new net.minecraft.class_2487();
        for (Map.Entry<AttachmentType<?>, ?> entry : attachments.entrySet()) {
            AttachmentType<?> type = entry.getKey();
            Codec<Object> codec = (Codec<Object>) type.persistenceCodec();
            if (codec != null) {
                codec.encodeStart(net.minecraft.class_2509.field_11560, entry.getValue())
                    .get()
                    .ifRight(partial -> {
                        LOGGER.warn("Couldn't serialize attachment " + type.identifier() + ", skipping. Error:");
                        LOGGER.warn(partial.message());
                    })
                    .ifLeft(serialized -> compound.method_10566(type.identifier().toString(), serialized));
            }
        }
        nbt.method_10566(AttachmentTarget.NBT_ATTACHMENT_KEY, compound);
    }

    public static IdentityHashMap<AttachmentType<?>, Object> deserializeAttachmentData(net.minecraft.class_2487 nbt) {
        if (nbt.method_10573(AttachmentTarget.NBT_ATTACHMENT_KEY, 10)) {
            IdentityHashMap<AttachmentType<?>, Object> attachments = new IdentityHashMap<>();
            net.minecraft.class_2487 compound = nbt.method_10562(AttachmentTarget.NBT_ATTACHMENT_KEY);
            for (String key : compound.method_10541()) {
                AttachmentType<?> type = AttachmentRegistryImpl.get(new net.minecraft.class_2960(key));
                if (type == null) {
                    LOGGER.warn("Unknown attachment type " + key + " found when deserializing, skipping");
                    continue;
                }
                Codec<?> codec = type.persistenceCodec();
                if (codec != null) {
                    codec.parse(net.minecraft.class_2509.field_11560, compound.method_10580(key))
                        .get()
                        .ifRight(partial -> {
                            LOGGER.warn("Couldn't deserialize attachment " + type.identifier() + ", skipping. Error:");
                            LOGGER.warn(partial.message());
                        })
                        .ifLeft(value -> attachments.put(type, value));
                }
            }
            if (attachments.isEmpty()) {
                return null;
            }
            return attachments;
        }
        return null;
    }

    public static boolean hasPersistentAttachments(IdentityHashMap<AttachmentType<?>, ?> map) {
        if (map == null) {
            return false;
        }
        for (AttachmentType<?> type : map.keySet()) {
            if (type.isPersistent()) {
                return true;
            }
        }
        return false;
    }
}
