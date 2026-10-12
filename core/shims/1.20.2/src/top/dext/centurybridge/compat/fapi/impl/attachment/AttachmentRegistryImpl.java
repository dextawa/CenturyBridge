package top.dext.centurybridge.compat.fapi.impl.attachment;

import com.mojang.serialization.Codec;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.function.Supplier;
import net.fabricmc.fabric.api.entity.event.v1.ServerEntityWorldChangeEvents;
import net.fabricmc.fabric.api.entity.event.v1.ServerPlayerEvents;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import top.dext.centurybridge.compat.fapi.attachment.v1.AttachmentRegistry;
import top.dext.centurybridge.compat.fapi.attachment.v1.AttachmentType;
import top.dext.centurybridge.compat.fapi.entity.event.v1.ServerLivingEntityEvents;

/**
 * 1.20.1's AttachmentRegistryImpl, and the attachment transfers 1.20.1's AttachmentEntrypoint
 * registered: a respawned player gets the old player's attachments (on a death, only those that copy
 * on death), an entity moved to another world and a converted mob get all of them.
 *
 * 1.20.1 registered the transfers at start-up. They are registered here when the first attachment
 * type is created or looked up: until then no type exists and no target can hold an attachment, so
 * no transfer would have moved anything.
 */
public final class AttachmentRegistryImpl {

    private static final Logger LOGGER = LoggerFactory.getLogger("fabric-data-attachment-api-v1");
    private static final Map<net.minecraft.class_2960, AttachmentType<?>> attachmentRegistry = new HashMap<>();

    static {
        ServerPlayerEvents.COPY_FROM.register((oldPlayer, newPlayer, alive) -> AttachmentTargetImpl.transfer(
            (AttachmentTargetImpl) oldPlayer, (AttachmentTargetImpl) newPlayer, !alive));
        ServerEntityWorldChangeEvents.AFTER_ENTITY_CHANGE_WORLD.register((originalEntity, newEntity, origin, destination) ->
            AttachmentTargetImpl.transfer((AttachmentTargetImpl) originalEntity, (AttachmentTargetImpl) newEntity, false));
        ServerLivingEntityEvents.MOB_CONVERSION.register((previous, converted, keepEquipment) -> AttachmentTargetImpl.transfer(
            (AttachmentTargetImpl) previous, (AttachmentTargetImpl) converted, true));
    }

    private AttachmentRegistryImpl() {
    }

    public static <A> void register(net.minecraft.class_2960 id, AttachmentType<A> type) {
        AttachmentType<?> existing = attachmentRegistry.put(id, type);
        if (existing != null) {
            LOGGER.warn("Encountered duplicate type registration for id " + id);
        }
    }

    public static AttachmentType<?> get(net.minecraft.class_2960 id) {
        return attachmentRegistry.get(id);
    }

    public static <A> AttachmentRegistry.Builder<A> builder() {
        return new BuilderImpl<>();
    }

    public static final class BuilderImpl<A> implements AttachmentRegistry.Builder<A> {

        private Supplier<A> defaultInitializer = null;
        private Codec<A> persistenceCodec = null;
        private boolean copyOnDeath = false;

        @Override
        public AttachmentRegistry.Builder<A> persistent(Codec<A> codec) {
            Objects.requireNonNull(codec, "codec cannot be null");
            this.persistenceCodec = codec;
            return this;
        }

        @Override
        public AttachmentRegistry.Builder<A> copyOnDeath() {
            this.copyOnDeath = true;
            return this;
        }

        @Override
        public AttachmentRegistry.Builder<A> initializer(Supplier<A> initializer) {
            Objects.requireNonNull(initializer, "initializer cannot be null");
            this.defaultInitializer = initializer;
            return this;
        }

        @Override
        public AttachmentType<A> buildAndRegister(net.minecraft.class_2960 id) {
            AttachmentType<A> attachment = new AttachmentTypeImpl<>(id, defaultInitializer, persistenceCodec, copyOnDeath);
            AttachmentRegistryImpl.register(id, attachment);
            return attachment;
        }
    }
}
