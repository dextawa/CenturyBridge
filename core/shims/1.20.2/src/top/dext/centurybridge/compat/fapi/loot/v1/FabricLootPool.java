package top.dext.centurybridge.compat.fapi.loot.v1;

import java.util.List;

/**
 * Fabric API loot tables v1's FabricLootPool (fabric-loot-tables-v1, removed with 1.20.2's Fabric API):
 * a loot pool's parts, read off the vanilla pool. Every LootPool implements it through the
 * CbFapiLootPoolV1 mixin, as 1.20.1's LootPoolMixin made it.
 */
public interface FabricLootPool {

    default net.minecraft.class_55 asVanilla() {
        return (net.minecraft.class_55) this;
    }

    List<net.minecraft.class_79> getEntries();

    List<net.minecraft.class_5341> getConditions();

    List<net.minecraft.class_117> getFunctions();

    net.minecraft.class_5658 getRolls();
}
