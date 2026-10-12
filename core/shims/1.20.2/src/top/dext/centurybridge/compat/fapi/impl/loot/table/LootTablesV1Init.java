package top.dext.centurybridge.compat.fapi.impl.loot.table;

import java.util.HashMap;
import java.util.Map;
import net.fabricmc.fabric.api.event.Event;
import net.fabricmc.fabric.api.event.EventFactory;
import net.fabricmc.fabric.api.loot.v2.LootTableEvents;
import top.dext.centurybridge.compat.fapi.loot.v1.event.LootTableLoadingCallback;

/**
 * 1.20.1's LootTablesV1Init: the v1 loading event, run from loot API v2's events. REPLACE hands each
 * loaded table to the v1 callbacks as a buffering builder (and answers the table a callback set, if
 * any); MODIFY replays what they changed onto the table's builder. 1.20.1 registered these two
 * listeners at start-up; here they are registered when the v1 event is first touched -- until then no
 * v1 callback exists, and with none the listeners change no table.
 */
public final class LootTablesV1Init {

    private static final ThreadLocal<Map<net.minecraft.class_2960, BufferingLootTableBuilder>> BUFFERS =
        ThreadLocal.withInitial(HashMap::new);

    private static final Event<LootTableLoadingCallback> EVENT = EventFactory.createArrayBacked(LootTableLoadingCallback.class,
        listeners -> (resourceManager, manager, id, supplier, setter) -> {
            for (LootTableLoadingCallback callback : listeners) {
                callback.onLootTableLoading(resourceManager, manager, id, supplier, setter);
            }
        });

    static {
        LootTableEvents.REPLACE.register((resourceManager, lootManager, id, original, source) -> {
            BufferingLootTableBuilder builder = new BufferingLootTableBuilder();
            builder.init(original);
            BUFFERS.get().put(id, builder);
            net.minecraft.class_52[] replacement = new net.minecraft.class_52[1];
            EVENT.invoker().onLootTableLoading(resourceManager, lootManager, id, builder, table -> replacement[0] = table);
            return replacement[0];
        });
        LootTableEvents.MODIFY.register((resourceManager, lootManager, id, tableBuilder, source) -> {
            Map<net.minecraft.class_2960, BufferingLootTableBuilder> buffers = BUFFERS.get();
            if (buffers.containsKey(id)) {
                try {
                    buffers.get(id).applyTo(tableBuilder);
                } finally {
                    buffers.remove(id);
                    if (buffers.isEmpty()) {
                        BUFFERS.remove();
                    }
                }
            }
        });
    }

    private LootTablesV1Init() {
    }

    public static Event<LootTableLoadingCallback> event() {
        return EVENT;
    }
}
