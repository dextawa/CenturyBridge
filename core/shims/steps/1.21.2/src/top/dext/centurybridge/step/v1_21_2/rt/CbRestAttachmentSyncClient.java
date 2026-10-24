package top.dext.centurybridge.step.v1_21_2.rt;

import io.netty.buffer.Unpooled;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.attachment.v1.AttachmentTarget;
import net.fabricmc.fabric.api.attachment.v1.AttachmentType;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.fabricmc.fabric.impl.attachment.AttachmentRegistryImpl;
import net.minecraft.class_1923;
import net.minecraft.class_2960;
import net.minecraft.class_310;
import net.minecraft.class_638;
import net.minecraft.class_9129;

/**
 * The client side of the 1.21.1 attachment synchronisation ({@link CbRestAttachmentSync}): each received
 * change is set on the client's own target -- the level, the entity of that id, the block entity at that
 * position, the loaded chunk at that position -- as 0.116's client applied it. A change for a target the
 * client does not have, or of an attachment type it does not know, is left out (logged), as 0.116 left out
 * what the client could not take.
 */
@Environment(EnvType.CLIENT)
public final class CbRestAttachmentSyncClient {

    private CbRestAttachmentSyncClient() {
    }

    private static final org.slf4j.Logger LOGGER = com.mojang.logging.LogUtils.getLogger();

    static void init() {
        ClientPlayNetworking.registerGlobalReceiver(CbRestAttachmentSync.Payload.TYPE, (payload, context) -> {
            class_638 level = class_310.method_1551().field_1687;
            if (level == null) {
                return;
            }
            for (byte[] entry : payload.entries()) {
                apply(level, entry);
            }
        });
    }

    @SuppressWarnings({"unchecked", "rawtypes"})
    private static void apply(class_638 level, byte[] bytes) {
        class_9129 buf = new class_9129(Unpooled.wrappedBuffer(bytes), level.method_30349());
        int kind = buf.method_10816();
        AttachmentTarget target = switch (kind) {
            case CbRestAttachmentSync.KIND_ENTITY -> (AttachmentTarget) (Object) level.method_8469(buf.method_10816());
            case CbRestAttachmentSync.KIND_BLOCK_ENTITY -> (AttachmentTarget) (Object) level.method_8321(buf.method_10811());
            case CbRestAttachmentSync.KIND_CHUNK -> {
                class_1923 pos = new class_1923(buf.readLong());
                yield (AttachmentTarget) (Object) level.method_2935().method_21730(pos.field_9181, pos.field_9180);
            }
            default -> (AttachmentTarget) (Object) level;
        };
        class_2960 id = buf.method_10810();
        AttachmentType type = AttachmentRegistryImpl.get(id);
        CbRestAttachmentSync.SyncInfo info = type == null ? null : CbRestAttachmentSync.info(type);
        if (target == null || info == null) {
            LOGGER.debug("Left out a synced attachment {}: {}", id, target == null ? "no such target on the client" : "unknown on the client");
            return;
        }
        boolean present = buf.readBoolean();
        target.setAttached(type, present ? info.codec().decode(buf) : null);
    }
}
