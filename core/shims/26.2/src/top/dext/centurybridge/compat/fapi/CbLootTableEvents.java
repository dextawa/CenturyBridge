package top.dext.centurybridge.compat.fapi;

import net.fabricmc.fabric.api.event.Event;
import net.fabricmc.fabric.api.event.EventFactory;
import net.fabricmc.fabric.api.loot.v3.LootTableEvents;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.world.level.storage.loot.LootTable;
import top.dext.centurybridge.facade.v26_2.class_60;
import top.dext.centurybridge.rt.v26_2.Trackers;

/**
 * fabric-loot-api-v2's LootTableEvents as 1.20.x mods use them (26 corpus
 * mods, almost all MODIFY). v3 (1.21+) changed every listener's shape: the
 * (ResourceManager, LootManager, Identifier, ...) prefix became
 * (ResourceKey&lt;LootTable&gt;, ...) plus a HolderLookup.Provider tail. The
 * old-shape events live here and one v3 listener each forwards into them:
 * the id is the key's location, the LootManager is the {@link class_60}
 * facade, the ResourceManager is the running server's. LootTableSource is a
 * pure rename (same enum).
 */
public final class CbLootTableEvents {

    public static final Event<CbLootTableReplace> REPLACE = EventFactory.createArrayBacked(CbLootTableReplace.class,
        listeners -> (manager, lootManager, id, original, source) -> {
            for (CbLootTableReplace l : listeners) {
                LootTable r = l.replaceLootTable(manager, lootManager, id, original, source);
                if (r != null) {
                    return r;
                }
            }
            return null;
        });

    public static final Event<CbLootTableModify> MODIFY = EventFactory.createArrayBacked(CbLootTableModify.class,
        listeners -> (manager, lootManager, id, builder, source) -> {
            for (CbLootTableModify l : listeners) {
                l.modifyLootTable(manager, lootManager, id, builder, source);
            }
        });

    public static final Event<CbLootTableLoaded> ALL_LOADED = EventFactory.createArrayBacked(CbLootTableLoaded.class,
        listeners -> (manager, lootManager) -> {
            for (CbLootTableLoaded l : listeners) {
                l.onLootTablesLoaded(manager, lootManager);
            }
        });

    static {
        LootTableEvents.REPLACE.register((key, original, source, registries) ->
            REPLACE.invoker().replaceLootTable(resources(), class_60.cbCurrent(), key.identifier(), original, source));
        LootTableEvents.MODIFY.register((key, builder, source, registries) ->
            MODIFY.invoker().modifyLootTable(resources(), class_60.cbCurrent(), key.identifier(), builder, source));
        LootTableEvents.ALL_LOADED.register((manager, registry) ->
            ALL_LOADED.invoker().onLootTablesLoaded(manager, new class_60(registry)));
    }

    private static ResourceManager resources() {
        MinecraftServer server = Trackers.SERVER;
        return server == null ? null : server.getResourceManager();
    }

    private CbLootTableEvents() {
    }
}
