package top.dext.centurybridge.facade.v26_2;

import net.minecraft.core.Registry;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.Identifier;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.MinecraftServer;
import net.minecraft.world.level.storage.loot.LootTable;
import top.dext.centurybridge.rt.v26_2.Trackers;

/**
 * LootManager (class_60) as the corpus names it: the 1.20.1 loot-table lookup
 * handed to Fabric's LootTableEvents listeners (14 corpus mods call
 * getLootTable(Identifier) on it). Since 1.21 loot tables are a reloadable
 * registry on the server; this facade answers from the registry it was created
 * for (ALL_LOADED) or from the running server's reloadable registries
 * (MODIFY/REPLACE, where 1.20.1 also only saw the previous load's tables).
 */
public class class_60 {

    private final Registry<LootTable> registry;

    public class_60(Registry<LootTable> registry) {
        this.registry = registry;
    }

    public static class_60 cbCurrent() {
        return new class_60(null);
    }

    /** was LootManager.getLootTable(Identifier): the table, or LootTable.EMPTY when unknown */
    public LootTable getLootTable(Identifier id) {
        ResourceKey<LootTable> key = ResourceKey.create(Registries.LOOT_TABLE, id);
        if (registry != null) {
            LootTable t = registry.getValue(key);
            return t == null ? LootTable.EMPTY : t;
        }
        MinecraftServer server = Trackers.SERVER;
        return server == null ? LootTable.EMPTY : server.reloadableRegistries().getLootTable(key);
    }
}
