package top.dext.centurybridge.compat.fapi.loot.v1;

import java.util.List;

/**
 * Fabric API loot tables v1's FabricLootSupplier: a loot table's pools and functions, read off the
 * vanilla table. Every LootTable implements it through the CbFapiLootTableV1 mixin, as 1.20.1's
 * LootTableMixin made it.
 */
public interface FabricLootSupplier {

    default net.minecraft.class_52 asVanilla() {
        return (net.minecraft.class_52) this;
    }

    List<net.minecraft.class_55> getPools();

    List<net.minecraft.class_117> getFunctions();

    default net.minecraft.class_176 getType() {
        return asVanilla().method_322();
    }
}
