package top.dext.centurybridge.compat.fapi.registry;

/**
 * Fabric API content registries v0's LootEntryTypeRegistry (deprecated, removed with 1.20.2's Fabric
 * API): registers a loot pool entry serializer. The serializer type (LootPoolEntry.Serializer, gone with
 * 1.20.2) is this layer's facade.
 */
public interface LootEntryTypeRegistry {

    LootEntryTypeRegistry INSTANCE = new top.dext.centurybridge.compat.fapi.impl.content.registry.LootEntryTypeRegistryImpl();

    void register(top.dext.centurybridge.facade.v1_20_2.class_79$class_5337<?> serializer);
}
