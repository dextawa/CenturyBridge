package top.dext.centurybridge.step.v1_20_5.compat.fapi.impl.loot;

import com.mojang.serialization.Lifecycle;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import net.fabricmc.fabric.api.loot.v2.FabricLootTableBuilder;
import net.fabricmc.fabric.api.loot.v2.LootTableSource;
import top.dext.centurybridge.step.v1_20_5.compat.fapi.loot.v2.LootTableEvents;
import top.dext.centurybridge.step.v1_20_5.facade.LootDataManager;
import top.dext.centurybridge.step.v1_20_5.rt.CbLootData;

/**
 * Fires Fabric API 1.20.4's loot table events (compat/fapi/loot/v2/LootTableEvents) the way 1.20.4 fired
 * them, on 1.20.5's loot loading. 1.20.4 ran them once a reload had read all loot data: for every loot
 * table but the empty one, REPLACE with the table and its source, then MODIFY on a builder copy of the
 * (maybe replaced) table, the results becoming the reload's tables; then ALL_LOADED. 1.20.5 reads loot data
 * as the server's reloadable registries; ReloadableRegistries.apply(registries, list) receives them all,
 * read and not yet frozen (CbFapiLootEventsMixin): the events run there and the loot table registry is
 * rebuilt from their results, in its order, before vanilla adds the empty table and freezes.
 * <ul>
 * <li>the loot manager handed to the listeners is 1.20.4's one per server: a LootDataManager (facade/,
 *     s4item's) answering from the reload's loot data -- as read during REPLACE and MODIFY, with their
 *     results at ALL_LOADED and after, as 1.20.4's manager answered once its map was replaced;</li>
 * <li>a table's source is the one 1.20.5's Fabric API determined for it as it read the table (its MODIFY
 *     event, which every table passes: REPLACED when a 1.20.5 listener replaced it), else DATA_PACK, 1.20.5's
 *     own default;</li>
 * <li>the resource manager is the reload's (ReloadableRegistries.reload).</li>
 * </ul>
 * Nothing runs unless a 1.20.4 mod reached these events (LootTableEvents.REPLACE() ... mark it). The events
 * run where 1.20.5 applies the reload, on a worker thread; 1.20.4 ran them on the thread finishing the loot
 * manager's reload, the server thread.
 */
public final class CbLootEvents {

    private static volatile boolean used;
    private static volatile net.minecraft.class_3300 resources;
    private static final Map<net.minecraft.class_2960, LootTableSource> SOURCES = new ConcurrentHashMap<>();

    /** what 1.20.4's single loot manager answers from: the latest reload's loot data */
    private static final class Current implements CbLootData.View {
        volatile CbLootData.View view = CbLootData.EMPTY;

        @Override
        public Object element(net.minecraft.class_8490<?> type, net.minecraft.class_2960 location) {
            return view.element(type, location);
        }

        @Override
        public Collection<net.minecraft.class_2960> keys(net.minecraft.class_8490<?> type) {
            return view.keys(type);
        }

        @Override
        public net.minecraft.class_7871.class_7872 lookup() {
            return view.lookup();
        }
    }

    private static final Current CURRENT = new Current();
    private static final LootDataManager MANAGER = new LootDataManager(CURRENT);

    private CbLootEvents() {
    }

    /** a 1.20.4 mod read one of the events */
    public static void used() {
        used = true;
    }

    /** CbFapiInit (fabric-loot-api-v2 loaded): 1.20.5's MODIFY hands each table's source as it is read */
    public static void init() {
        net.fabricmc.fabric.api.loot.v2.LootTableEvents.MODIFY.register((key, builder, source) -> SOURCES.put(key.method_29177(), source));
    }

    /** CbFapiLootEventsMixin: ReloadableRegistries.reload(registries, resources, executor) began */
    public static void reloadStarted(net.minecraft.class_3300 resourceManager) {
        resources = resourceManager;
    }

    /** CbFapiLootEventsMixin: ReloadableRegistries.apply's list of read loot registries, as the rest of apply is to see it */
    @SuppressWarnings({"rawtypes", "unchecked"})
    public static List apply(List registries) {
        if (!used) {
            SOURCES.clear();
            return registries;
        }
        int at = -1;
        for (int i = 0; i < registries.size(); i++) {
            if (((net.minecraft.class_2378) registries.get(i)).method_30517().equals(net.minecraft.class_7924.field_50079)) {
                at = i;
                break;
            }
        }
        if (at < 0) {
            SOURCES.clear();
            return registries;
        }
        net.minecraft.class_2378<net.minecraft.class_52> tables = (net.minecraft.class_2378<net.minecraft.class_52>) registries.get(at);
        net.minecraft.class_3300 resourceManager = resources;
        net.minecraft.class_2960 empty = net.minecraft.class_39.field_844.method_29177();
        CURRENT.view = CbLootData.manager(new net.minecraft.class_5455.class_6891(registries)).cbView();
        net.minecraft.class_2370<net.minecraft.class_52> rebuilt = new net.minecraft.class_2370<>(net.minecraft.class_7924.field_50079, Lifecycle.experimental());
        for (Object o : (Iterable) tables.method_40270()::iterator) {
            net.minecraft.class_6880.class_6883<net.minecraft.class_52> entry = (net.minecraft.class_6880.class_6883<net.minecraft.class_52>) o;
            net.minecraft.class_5321<net.minecraft.class_52> key = entry.method_40237();
            net.minecraft.class_2960 id = key.method_29177();
            net.minecraft.class_52 table = entry.comp_349();
            if (!id.equals(empty)) {
                LootTableSource source = SOURCES.getOrDefault(id, LootTableSource.DATA_PACK);
                net.minecraft.class_52 replacement = LootTableEvents.REPLACE.invoker().replaceLootTable(resourceManager, MANAGER, id, table, source);
                if (replacement != null) {
                    table = replacement;
                    source = LootTableSource.REPLACED;
                }
                net.minecraft.class_52.class_53 builder = FabricLootTableBuilder.copyOf(table);
                LootTableEvents.MODIFY.invoker().modifyLootTable(resourceManager, MANAGER, id, builder, source);
                table = builder.method_338();
            }
            Optional<net.minecraft.class_9248> info = tables.method_57058(key);
            rebuilt.method_10272(key, table, info.orElseGet(() -> new net.minecraft.class_9248(Optional.empty(), Lifecycle.experimental())));
        }
        List out = new ArrayList(registries);
        out.set(at, rebuilt);
        CURRENT.view = CbLootData.manager(new net.minecraft.class_5455.class_6891(out)).cbView();
        SOURCES.clear();
        LootTableEvents.ALL_LOADED.invoker().onLootTablesLoaded(resourceManager, MANAGER);
        return out;
    }
}
