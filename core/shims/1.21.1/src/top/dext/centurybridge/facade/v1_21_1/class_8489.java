package top.dext.centurybridge.facade.v1_21_1;

// class_8489 = 1.20.1 LootDataLookup, removed in 1.20.5 when loot tables moved into
// the dynamic registry. Its element-lookup API is typed on class_8488 (LootDataKey)
// and class_8490 (LootDataType), both dead in 1.21.1 with no facade, so the typed
// getElement/getElementOptional overloads cannot be expressed here -- any mod reaching
// them is already unresolved at the dead key/type classes. We keep the interface as the
// classRename target so class_8489 references still class-load; every lookup is a
// degraded UOE stub (the datapack-format loot data no longer exists in 1.21.1).
public interface class_8489 {

    default net.minecraft.class_52 getLootTable(net.minecraft.class_2960 id) {
        throw new UnsupportedOperationException("CenturyBridge: legacy loot-data lookup is unavailable in 1.21.1 (loot moved to the dynamic registry in 1.20.5)");
    }
}
