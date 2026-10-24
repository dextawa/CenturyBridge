package cb.s7fapi;

import java.util.concurrent.atomic.AtomicInteger;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.attachment.v1.AttachmentRegistry;
import net.fabricmc.fabric.api.attachment.v1.AttachmentSyncPredicate;
import net.fabricmc.fabric.api.attachment.v1.AttachmentTarget;
import net.fabricmc.fabric.api.attachment.v1.AttachmentType;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerChunkEvents;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerLifecycleEvents;
import net.fabricmc.fabric.api.event.registry.FabricRegistry;
import net.fabricmc.fabric.api.event.registry.RegistryEntryAddedCallback;
import net.fabricmc.fabric.api.resource.ResourceReloadListenerKeys;
import net.minecraft.class_1802;
import net.minecraft.class_2960;
import net.minecraft.class_6862;
import net.minecraft.class_7923;
import net.minecraft.class_7924;

/**
 * A 1.21.1-era server mod compiled against 1.21.1's Fabric API (0.116) for s7rest's Fabric API give-backs on
 * 1.21.2 (whose Fabric API, 0.106.1, lacks them): ServerChunkEvents.CHUNK_GENERATE / CHUNK_LEVEL_TYPE_CHANGE
 * fire while the spawn area generates; an item tag file's "fabric:remove" (fabric-tag-api-v1, resources of
 * this mod) takes dirt out of a tag listing stone and dirt; FabricRegistry.addAlias makes an old id resolve to
 * stone; RegistryEntryAddedCallback.allEntries hands every existing item; ResourceReloadListenerKeys.TAGS is
 * 0.116's id. Every check prints "CB-FAPITEST ok|FAIL ..."; the last line is "CB-FAPITEST DONE passed/checks".
 */
public class S7FapiTestMod implements ModInitializer {

    static int checks;
    static int passed;

    static void check(boolean ok, String what) {
        checks++;
        if (ok) {
            passed++;
        }
        System.out.println("CB-FAPITEST " + (ok ? "ok   " : "FAIL ") + what);
    }

    @Override
    public void onInitialize() {
        AtomicInteger generated = new AtomicInteger();
        AtomicInteger levelChanges = new AtomicInteger();
        AtomicInteger toEntityTicking = new AtomicInteger();
        ServerChunkEvents.CHUNK_GENERATE.register((level, chunk) -> generated.incrementAndGet());
        ServerChunkEvents.CHUNK_LEVEL_TYPE_CHANGE.register((level, chunk, oldType, newType) -> {
            levelChanges.incrementAndGet();
            if (newType.ordinal() == oldType.ordinal() + 1 && newType.ordinal() == 3) {
                toEntityTicking.incrementAndGet();
            }
        });

        AtomicInteger items = new AtomicInteger();
        RegistryEntryAddedCallback.allEntries(class_7923.field_41178, holder -> items.incrementAndGet());
        check(items.get() > 1000, "allEntries handed the existing items (" + items.get() + ")");

        class_2960 old = class_2960.method_60655("cb_s7fapi", "old_stone");
        ((FabricRegistry) class_7923.field_41178).addAlias(old, class_2960.method_60655("minecraft", "stone"));
        check(class_7923.field_41178.method_10250(old), "an alias id is contained after addAlias");

        AttachmentType<Integer> synced = AttachmentRegistry.<Integer>create(class_2960.method_60655("cb_s7fapi", "synced"),
            builder -> builder.syncWith(net.minecraft.class_9135.field_49675.method_56430(), AttachmentSyncPredicate.all()));
        AttachmentType<Integer> plain = AttachmentRegistry.create(class_2960.method_60655("cb_s7fapi", "plain"));
        check(synced.isSynced() && !plain.isSynced(), "a type built with syncWith is synced, a plain one is not");

        check(class_2960.method_60656("tags").equals(ResourceReloadListenerKeys.TAGS), "ResourceReloadListenerKeys.TAGS is minecraft:tags");

        ServerLifecycleEvents.SERVER_STARTED.register(server -> {
            check(generated.get() > 0, "CHUNK_GENERATE fired for the spawn area (" + generated.get() + ")");
            check(levelChanges.get() > 0, "CHUNK_LEVEL_TYPE_CHANGE fired (" + levelChanges.get() + ", to entity ticking " + toEntityTicking.get() + ")");
            class_6862<net.minecraft.class_1792> tag = class_6862.method_40092(class_7924.field_41197, class_2960.method_60655("cb_s7fapi", "test"));
            check(class_1802.field_20391.method_40131().method_40220(tag), "the tag keeps stone");
            check(!class_1802.field_8831.method_40131().method_40220(tag), "fabric:remove took dirt out of the tag");
            AttachmentTarget world = (AttachmentTarget) server.method_30002();
            world.setAttached(synced, 5);
            world.modifyAttached(synced, v -> v + 2);
            check(Integer.valueOf(7).equals(world.getAttached(synced)), "set and modify a synced attachment on the overworld (" + world.getAttached(synced) + ")");
            System.out.println("CB-FAPITEST DONE " + passed + "/" + checks);
        });
    }
}
