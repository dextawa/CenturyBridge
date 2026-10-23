package top.dext.centurybridge.step.v26_1.compat.fapi.loot.v2;

import net.fabricmc.fabric.api.event.Event;
import net.fabricmc.fabric.api.event.EventFactory;
import net.fabricmc.fabric.api.loot.v3.LootTableSource;
import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.world.level.storage.loot.LootTable;

/**
 * 1.21.11's net.fabricmc.fabric.api.loot.v2.LootTableEvents (fabric-loot-api-v2, which 26.1's Fabric API
 * dropped; its source enum is loot-api-v3's LootTableSource, a class rename). The three events and their
 * invokers are 1.21.11's bytecode; 1.21.11's LootInitializer fed them from loot-api-v3's events, minus the
 * registry lookup v2 never passed, and so does this class, once, when it is first used (no v2 listener
 * exists before that).
 */
public final class LootTableEvents {
    private LootTableEvents() {
    }

    /** the first listener's non-null table replaces the loaded one; null keeps it */
    public static final Event<Replace> REPLACE = EventFactory.createArrayBacked(Replace.class, listeners -> (key, original, source) -> {
        for (Replace listener : listeners) {
            LootTable replaced = listener.replaceLootTable(key, original, source);
            if (replaced != null) {
                return replaced;
            }
        }
        return null;
    });

    public static final Event<Modify> MODIFY = EventFactory.createArrayBacked(Modify.class, listeners -> (key, tableBuilder, source) -> {
        for (Modify listener : listeners) {
            listener.modifyLootTable(key, tableBuilder, source);
        }
    });

    public static final Event<Loaded> ALL_LOADED = EventFactory.createArrayBacked(Loaded.class, listeners -> (resourceManager, lootRegistry) -> {
        for (Loaded listener : listeners) {
            listener.onLootTablesLoaded(resourceManager, lootRegistry);
        }
    });

    static {
        net.fabricmc.fabric.api.loot.v3.LootTableEvents.REPLACE.register(
            (key, original, source, registries) -> REPLACE.invoker().replaceLootTable(key, original, source));
        net.fabricmc.fabric.api.loot.v3.LootTableEvents.MODIFY.register(
            (key, tableBuilder, source, registries) -> MODIFY.invoker().modifyLootTable(key, tableBuilder, source));
        net.fabricmc.fabric.api.loot.v3.LootTableEvents.ALL_LOADED.register(
            (resourceManager, lootRegistry) -> ALL_LOADED.invoker().onLootTablesLoaded(resourceManager, lootRegistry));
    }

    @FunctionalInterface
    public interface Replace {
        LootTable replaceLootTable(ResourceKey<LootTable> key, LootTable original, LootTableSource source);
    }

    @FunctionalInterface
    public interface Modify {
        void modifyLootTable(ResourceKey<LootTable> key, LootTable.Builder tableBuilder, LootTableSource source);
    }

    @FunctionalInterface
    public interface Loaded {
        void onLootTablesLoaded(ResourceManager resourceManager, Registry<LootTable> lootRegistry);
    }
}
