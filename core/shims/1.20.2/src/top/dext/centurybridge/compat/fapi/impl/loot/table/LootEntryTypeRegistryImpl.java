package top.dext.centurybridge.compat.fapi.impl.loot.table;

import top.dext.centurybridge.compat.fapi.loot.v1.LootEntryTypeRegistry;

/**
 * 1.20.1's LootEntryTypeRegistryImpl: Registry.register(LOOT_POOL_ENTRY_TYPE, id, new
 * LootPoolEntryType(serializer)). A 1.20.2 entry type is made from a codec; the serializer becomes
 * one through CbLootJson, the same conversion the converted mods' own LootPoolEntryType(serializer)
 * construction goes through.
 */
public final class LootEntryTypeRegistryImpl implements LootEntryTypeRegistry {

    @Override
    public void register(net.minecraft.class_2960 id, top.dext.centurybridge.facade.v1_20_2.class_5335<? extends net.minecraft.class_79> serializer) {
        net.minecraft.class_2378.method_10230(net.minecraft.class_7923.field_41133, id,
            new net.minecraft.class_5338(top.dext.centurybridge.rt.v1_20_2.CbLootJson.codec(serializer)));
    }
}
