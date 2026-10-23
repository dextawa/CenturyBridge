package top.dext.centurybridge.step.v1_20_5.compat.fapi.loot.v2;

import net.fabricmc.fabric.api.event.Event;
import net.fabricmc.fabric.api.event.EventFactory;
import net.fabricmc.fabric.api.loot.v2.LootTableSource;
import top.dext.centurybridge.step.v1_20_5.compat.fapi.impl.loot.CbLootEvents;
import top.dext.centurybridge.step.v1_20_5.facade.LootDataManager;

/**
 * Fabric API 0.97.3's (1.20.4) loot table events: listeners handed the resource manager, the loot manager
 * and the table's id. 1.20.5 kept the class and its three fields but changed every listener's shape (a
 * registry key, no managers), so a 1.20.4 listener cannot be registered on 1.20.5's events. A 1.20.4 mod's
 * listener types are these (classRenames of LootTableEvents$Replace / $Modify / $Loaded), and its reads of
 * LootTableEvents.REPLACE / MODIFY / ALL_LOADED reach these events (fieldGetters: {@link #REPLACE()} ...),
 * which fire as 1.20.4 fired them: after all loot data of a reload is read, every loot table through
 * REPLACE then MODIFY, then ALL_LOADED (impl/loot/CbLootEvents, CbFapiLootEventsMixin). Bodies of the
 * invokers as in 1.20.4 (Fabric API, Apache-2.0).
 */
public final class LootTableEvents {

    private LootTableEvents() {
    }

    public static final Event<Replace> REPLACE = EventFactory.createArrayBacked(Replace.class, listeners -> (resourceManager, lootManager, id, original, source) -> {
        for (Replace listener : listeners) {
            net.minecraft.class_52 replaced = listener.replaceLootTable(resourceManager, lootManager, id, original, source);
            if (replaced != null) {
                return replaced;
            }
        }
        return null;
    });

    public static final Event<Modify> MODIFY = EventFactory.createArrayBacked(Modify.class, listeners -> (resourceManager, lootManager, id, tableBuilder, source) -> {
        for (Modify listener : listeners) {
            listener.modifyLootTable(resourceManager, lootManager, id, tableBuilder, source);
        }
    });

    public static final Event<Loaded> ALL_LOADED = EventFactory.createArrayBacked(Loaded.class, listeners -> (resourceManager, lootManager) -> {
        for (Loaded listener : listeners) {
            listener.onLootTablesLoaded(resourceManager, lootManager);
        }
    });

    /** LootTableEvents.REPLACE as a 1.20.4 mod reads it (fieldGetters) */
    public static Event<Replace> REPLACE() {
        CbLootEvents.used();
        return REPLACE;
    }

    /** LootTableEvents.MODIFY as a 1.20.4 mod reads it (fieldGetters) */
    public static Event<Modify> MODIFY() {
        CbLootEvents.used();
        return MODIFY;
    }

    /** LootTableEvents.ALL_LOADED as a 1.20.4 mod reads it (fieldGetters) */
    public static Event<Loaded> ALL_LOADED() {
        CbLootEvents.used();
        return ALL_LOADED;
    }

    public interface Replace {
        net.minecraft.class_52 replaceLootTable(net.minecraft.class_3300 resourceManager, LootDataManager lootManager,
                                                net.minecraft.class_2960 id, net.minecraft.class_52 original, LootTableSource source);
    }

    public interface Modify {
        void modifyLootTable(net.minecraft.class_3300 resourceManager, LootDataManager lootManager, net.minecraft.class_2960 id,
                             net.minecraft.class_52.class_53 tableBuilder, LootTableSource source);
    }

    public interface Loaded {
        void onLootTablesLoaded(net.minecraft.class_3300 resourceManager, LootDataManager lootManager);
    }
}
