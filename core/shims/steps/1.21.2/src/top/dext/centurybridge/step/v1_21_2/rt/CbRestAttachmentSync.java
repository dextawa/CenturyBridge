package top.dext.centurybridge.step.v1_21_2.rt;

import io.netty.buffer.Unpooled;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.IdentityHashMap;
import java.util.List;
import java.util.Map;
import java.util.WeakHashMap;
import java.util.function.Consumer;
import java.util.function.Supplier;
import java.util.function.UnaryOperator;
import net.fabricmc.fabric.api.attachment.v1.AttachmentRegistry;
import net.fabricmc.fabric.api.attachment.v1.AttachmentTarget;
import net.fabricmc.fabric.api.attachment.v1.AttachmentType;
import net.fabricmc.fabric.api.entity.event.v1.ServerEntityWorldChangeEvents;
import net.fabricmc.fabric.api.networking.v1.PayloadTypeRegistry;
import net.fabricmc.fabric.api.networking.v1.PlayerLookup;
import net.fabricmc.fabric.api.networking.v1.ServerPlayConnectionEvents;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.fabricmc.fabric.impl.attachment.AttachmentTargetImpl;
import net.minecraft.class_1297;
import net.minecraft.class_2586;
import net.minecraft.class_2818;
import net.minecraft.class_2960;
import net.minecraft.class_3218;
import net.minecraft.class_3222;
import net.minecraft.class_5455;
import net.minecraft.class_8710;
import net.minecraft.class_9129;
import net.minecraft.class_9139;
import top.dext.centurybridge.step.v1_21_2.facade.fapi.AttachmentSyncPredicate;

/**
 * Attachment synchronisation of 1.21.1's Fabric API (0.116), which 1.21.2's Fabric API (0.106.1) predates:
 * AttachmentRegistry.Builder.syncWith(codec, predicate), AttachmentRegistry.create(id, builder),
 * AttachmentType.isSynced(), AttachmentSyncPredicate.
 *
 * As in 0.116, a server-side change of a synced attachment goes to the players that see the target -- those
 * tracking an entity (and the player itself), a block entity or chunk, every player of a level -- that the
 * type's predicate admits, encoded with the type's stream codec; a player receives a target's synced
 * attachments when it starts seeing it -- its level and itself on join, the new level on a level change, an
 * entity when the player starts tracking it (after the entity's spawn packets), a chunk and its block
 * entities after the chunk is sent (CbRestAttachmentSyncMixin). 0.116 hooked its own setAttached; 1.21.2's
 * Fabric API keeps the attachments in its own mixins, so the layer sees a change where a 1.21.1 mod makes it
 * -- the target's set / remove / modify / getAttachedOrCreate / getAttachedOrSet calls of a converted mod
 * (instanceRedirects); the types only exist in converted mods, which make their changes there. The client
 * side (CbRestAttachmentSyncClient) sets the received values on its own targets. 0.116 negotiated during
 * configuration which synced attachments the client knows; here the client leaves out the ones it does not
 * know, which comes to the same.
 */
public final class CbRestAttachmentSync {

    private CbRestAttachmentSync() {
    }

    private static final int BATCH_BYTES = 900_000;
    static final int KIND_LEVEL = 0;
    static final int KIND_ENTITY = 1;
    static final int KIND_BLOCK_ENTITY = 2;
    static final int KIND_CHUNK = 3;

    /** a synced type's stream codec and predicate */
    @SuppressWarnings("rawtypes")
    record SyncInfo(class_9139 codec, AttachmentSyncPredicate predicate) {
    }

    private static final Map<AttachmentType<?>, SyncInfo> SYNCED = Collections.synchronizedMap(new IdentityHashMap<>());
    private static final Map<AttachmentRegistry.Builder<?>, SyncInfo> PENDING = Collections.synchronizedMap(new WeakHashMap<>());

    static SyncInfo info(AttachmentType<?> type) {
        return SYNCED.get(type);
    }

    /** encoded changes of synced attachments */
    public record Payload(List<byte[]> entries) implements class_8710 {
        public static final class_8710.class_9154<Payload> TYPE =
            new class_8710.class_9154<>(class_2960.method_60655("centurybridge", "attachment_sync_1_21_1"));
        public static final class_9139<class_9129, Payload> CODEC = class_8710.method_56484(Payload::write, Payload::read);

        private void write(class_9129 buf) {
            buf.method_10804(entries.size());
            for (byte[] e : entries) {
                buf.method_10813(e);
            }
        }

        private static Payload read(class_9129 buf) {
            int n = buf.method_10816();
            List<byte[]> entries = new ArrayList<>(n);
            for (int i = 0; i < n; i++) {
                entries.add(buf.method_10795());
            }
            return new Payload(entries);
        }

        @Override
        public class_8710.class_9154<? extends class_8710> method_56479() {
            return TYPE;
        }
    }

    static void init() {
        PayloadTypeRegistry.playS2C().register(Payload.TYPE, Payload.CODEC);
        ServerPlayConnectionEvents.JOIN.register((handler, sender, server) -> {
            class_3222 player = handler.field_14140;
            List<byte[]> changes = new ArrayList<>();
            initial((AttachmentTarget) (Object) player.method_51469(), player, changes);
            initial((AttachmentTarget) (Object) player, player, changes);
            send(player, changes);
        });
        ServerEntityWorldChangeEvents.AFTER_PLAYER_CHANGE_WORLD.register((player, origin, destination) -> {
            List<byte[]> changes = new ArrayList<>();
            initial((AttachmentTarget) (Object) destination, player, changes);
            send(player, changes);
        });
    }

    // ------------------------------------------------------------------ the 0.116 API

    /** Builder.syncWith(codec, predicate) of 0.116 */
    @SuppressWarnings("rawtypes")
    public static AttachmentRegistry.Builder syncWith(AttachmentRegistry.Builder builder, class_9139 codec, AttachmentSyncPredicate predicate) {
        PENDING.put(builder, new SyncInfo(codec, predicate));
        return builder;
    }

    /** Builder.buildAndRegister(id): the type, synced when its builder was told to sync */
    @SuppressWarnings("rawtypes")
    public static AttachmentType buildAndRegister(AttachmentRegistry.Builder builder, class_2960 id) {
        AttachmentType type = builder.buildAndRegister(id);
        SyncInfo info = PENDING.remove(builder);
        if (info != null) {
            SYNCED.put(type, info);
        }
        return type;
    }

    /** AttachmentRegistry.create(id, builder) of 0.116: a builder configured by the consumer, built and registered */
    @SuppressWarnings({"rawtypes", "unchecked"})
    public static AttachmentType create(class_2960 id, Consumer consumer) {
        AttachmentRegistry.Builder builder = AttachmentRegistry.builder();
        consumer.accept(builder);
        return buildAndRegister(builder, id);
    }

    /** AttachmentType.isSynced() of 0.116 */
    public static boolean isSynced(AttachmentType<?> type) {
        return SYNCED.containsKey(type);
    }

    // ------------------------------------------------------------------ changes made by 1.21.1 mods

    @SuppressWarnings({"rawtypes", "unchecked"})
    public static Object setAttached(AttachmentTarget target, AttachmentType type, Object value) {
        Object old = target.setAttached(type, value);
        changed(target, type);
        return old;
    }

    @SuppressWarnings({"rawtypes", "unchecked"})
    public static Object removeAttached(AttachmentTarget target, AttachmentType type) {
        Object old = target.removeAttached(type);
        changed(target, type);
        return old;
    }

    @SuppressWarnings({"rawtypes", "unchecked"})
    public static Object modifyAttached(AttachmentTarget target, AttachmentType type, UnaryOperator modifier) {
        Object value = target.modifyAttached(type, modifier);
        changed(target, type);
        return value;
    }

    @SuppressWarnings({"rawtypes", "unchecked"})
    public static Object getAttachedOrCreate(AttachmentTarget target, AttachmentType type) {
        boolean had = target.hasAttached(type);
        Object value = target.getAttachedOrCreate(type);
        if (!had) {
            changed(target, type);
        }
        return value;
    }

    @SuppressWarnings({"rawtypes", "unchecked"})
    public static Object getAttachedOrCreate(AttachmentTarget target, AttachmentType type, Supplier initializer) {
        boolean had = target.hasAttached(type);
        Object value = target.getAttachedOrCreate(type, initializer);
        if (!had) {
            changed(target, type);
        }
        return value;
    }

    @SuppressWarnings({"rawtypes", "unchecked"})
    public static Object getAttachedOrSet(AttachmentTarget target, AttachmentType type, Object defaultValue) {
        boolean had = target.hasAttached(type);
        Object value = target.getAttachedOrSet(type, defaultValue);
        if (!had) {
            changed(target, type);
        }
        return value;
    }

    /** a synced attachment of a server-side target changed: its new value to the players that see it and the predicate admits */
    private static void changed(AttachmentTarget target, AttachmentType<?> type) {
        SyncInfo info = SYNCED.get(type);
        if (info == null) {
            return;
        }
        Collection<class_3222> players = recipients(target);
        if (players.isEmpty()) {
            return;
        }
        byte[] entry = null;
        for (class_3222 player : players) {
            if (info.predicate().test(target, player)) {
                if (entry == null) {
                    entry = encode(target, type, info, access(target));
                    if (entry == null) {
                        return;
                    }
                }
                send(player, List.of(entry));
            }
        }
    }

    private static Collection<class_3222> recipients(AttachmentTarget target) {
        if (target instanceof class_3222 player) {
            List<class_3222> all = new ArrayList<>(PlayerLookup.tracking(player));
            all.add(player);
            return all;
        }
        if (target instanceof class_1297 entity) {
            return entity.method_37908() instanceof class_3218 ? PlayerLookup.tracking(entity) : List.of();
        }
        if (target instanceof class_2586 blockEntity) {
            return blockEntity.method_10997() instanceof class_3218 ? PlayerLookup.tracking(blockEntity) : List.of();
        }
        if (target instanceof class_2818 chunk) {
            return chunk.method_12200() instanceof class_3218 level ? PlayerLookup.tracking(level, chunk.method_12004()) : List.of();
        }
        if (target instanceof class_3218 level) {
            return PlayerLookup.world(level);
        }
        return List.of();
    }

    private static class_5455 access(AttachmentTarget target) {
        if (target instanceof class_1297 entity) {
            return entity.method_37908().method_30349();
        }
        if (target instanceof class_2586 blockEntity) {
            return blockEntity.method_10997().method_30349();
        }
        if (target instanceof class_2818 chunk) {
            return chunk.method_12200().method_30349();
        }
        return ((class_3218) target).method_30349();
    }

    /** the synced attachments of a target the player starts seeing, as encoded changes */
    static void initial(AttachmentTarget target, class_3222 player, List<byte[]> out) {
        Map<AttachmentType<?>, ?> attachments = ((AttachmentTargetImpl) target).fabric_getAttachments();
        if (attachments == null || attachments.isEmpty()) {
            return;
        }
        class_5455 access = null;
        for (AttachmentType<?> type : new ArrayList<>(attachments.keySet())) {
            SyncInfo info = SYNCED.get(type);
            if (info != null && info.predicate().test(target, player)) {
                if (access == null) {
                    access = access(target);
                }
                byte[] entry = encode(target, type, info, access);
                if (entry != null) {
                    out.add(entry);
                }
            }
        }
    }

    /** a chunk the player was just sent, and its block entities (CbRestAttachmentSyncMixin) */
    public static void chunkSent(class_3222 player, class_2818 chunk) {
        List<byte[]> changes = new ArrayList<>();
        initial((AttachmentTarget) (Object) chunk, player, changes);
        for (class_2586 blockEntity : chunk.method_12214().values()) {
            initial((AttachmentTarget) (Object) blockEntity, player, changes);
        }
        send(player, changes);
    }

    /** an entity the player starts tracking, after its spawn packets (CbRestAttachmentSyncMixin) */
    public static void startedTracking(class_3222 player, class_1297 entity) {
        List<byte[]> changes = new ArrayList<>();
        initial((AttachmentTarget) (Object) entity, player, changes);
        send(player, changes);
    }

    static void send(class_3222 player, List<byte[]> changes) {
        if (changes.isEmpty() || player.field_13987 == null || !ServerPlayNetworking.canSend(player, Payload.TYPE)) {
            return;
        }
        List<byte[]> batch = new ArrayList<>();
        int size = 0;
        for (byte[] e : changes) {
            if (!batch.isEmpty() && size + e.length > BATCH_BYTES) {
                ServerPlayNetworking.send(player, new Payload(batch));
                batch = new ArrayList<>();
                size = 0;
            }
            batch.add(e);
            size += e.length + 5;
        }
        ServerPlayNetworking.send(player, new Payload(batch));
    }

    @SuppressWarnings("unchecked")
    private static byte[] encode(AttachmentTarget target, AttachmentType<?> type, SyncInfo info, class_5455 access) {
        class_9129 buf = new class_9129(Unpooled.buffer(), access);
        try {
            if (target instanceof class_1297 entity) {
                buf.method_10804(KIND_ENTITY);
                buf.method_10804(entity.method_5628());
            } else if (target instanceof class_2586 blockEntity) {
                buf.method_10804(KIND_BLOCK_ENTITY);
                buf.method_10807(blockEntity.method_11016());
            } else if (target instanceof class_2818 chunk) {
                buf.method_10804(KIND_CHUNK);
                buf.writeLong(chunk.method_12004().method_8324());
            } else {
                buf.method_10804(KIND_LEVEL);
            }
            buf.method_10812(type.identifier());
            Object value = target.getAttached(type);
            buf.method_52964(value != null);
            if (value != null) {
                info.codec().encode(buf, value);
            }
            byte[] out = new byte[buf.readableBytes()];
            buf.readBytes(out);
            return out;
        } finally {
            buf.release();
        }
    }
}
