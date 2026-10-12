package top.dext.centurybridge.compat.fapi.loot.v1;

/**
 * Fabric API loot tables v1's LootEntryTypeRegistry: registers a loot pool entry type from its JSON
 * serializer. 1.20.1's serializer type (JsonSerializer, gone with 1.20.2) is this layer's facade.
 */
public interface LootEntryTypeRegistry {

    LootEntryTypeRegistry INSTANCE = new top.dext.centurybridge.compat.fapi.impl.loot.table.LootEntryTypeRegistryImpl();

    void register(net.minecraft.class_2960 id, top.dext.centurybridge.facade.v1_20_2.class_5335<? extends net.minecraft.class_79> serializer);
}
