package top.dext.centurybridge.compat.fapi;

import net.fabricmc.fabric.api.loot.v3.LootTableSource;
import net.minecraft.resources.Identifier;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.world.level.storage.loot.LootTable;
import top.dext.centurybridge.facade.v26_2.class_60;

/** was loot/v2 LootTableEvents.Modify (SAM modifyLootTable, 1.20.1 shape) */
@FunctionalInterface
public interface CbLootTableModify {
    void modifyLootTable(ResourceManager resourceManager, class_60 lootManager, Identifier id, LootTable.Builder tableBuilder, LootTableSource source);
}
