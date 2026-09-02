package top.dext.centurybridge.compat.fapi;

import net.minecraft.server.packs.resources.ResourceManager;
import top.dext.centurybridge.facade.v26_2.class_60;

/** was loot/v2 LootTableEvents.Loaded (SAM onLootTablesLoaded, 1.20.1 shape) */
@FunctionalInterface
public interface CbLootTableLoaded {
    void onLootTablesLoaded(ResourceManager resourceManager, class_60 lootManager);
}
