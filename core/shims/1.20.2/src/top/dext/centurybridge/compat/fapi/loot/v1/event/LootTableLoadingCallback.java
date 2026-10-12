package top.dext.centurybridge.compat.fapi.loot.v1.event;

import net.fabricmc.fabric.api.event.Event;
import top.dext.centurybridge.compat.fapi.loot.v1.FabricLootSupplierBuilder;

/**
 * Fabric API loot tables v1's LootTableLoadingCallback: every loot table as it loads, handed as a v1
 * builder holding the table, with a setter to replace it. Run from loot API v2's REPLACE / MODIFY
 * events exactly as 1.20.1's v1 module ran it (impl/loot/table/LootTablesV1Init).
 */
@FunctionalInterface
public interface LootTableLoadingCallback {

    Event<LootTableLoadingCallback> EVENT = top.dext.centurybridge.compat.fapi.impl.loot.table.LootTablesV1Init.event();

    void onLootTableLoading(net.minecraft.class_3300 resourceManager, net.minecraft.class_60 manager, net.minecraft.class_2960 id,
                            FabricLootSupplierBuilder supplier, LootTableSetter setter);

    @FunctionalInterface
    interface LootTableSetter {
        void set(net.minecraft.class_52 supplier);
    }
}
