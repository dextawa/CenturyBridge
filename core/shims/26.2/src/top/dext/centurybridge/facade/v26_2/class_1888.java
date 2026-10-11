package top.dext.centurybridge.facade.v26_2;

/**
 * Facade for 1.20.1 net/minecraft/class_1887$class_1888 (Enchantment.Rarity),
 * deleted in 26.2 (enchantments are fully data-driven; no Rarity enum). Legacy
 * mods pass a Rarity constant to the Enchantment super-constructor and read its
 * weight. Kept as a standalone enum so those classes load; the weight is the
 * vanilla 1.20.1 value. Constant/field names are the intermediary tokens the
 * mod bytecode still carries (classRename rewrites the owning type, not member
 * names).
 */
public enum class_1888 {
    field_9087(10),  // COMMON
    field_9090(5),   // UNCOMMON
    field_9088(2),   // RARE
    field_9091(1);   // VERY_RARE

    public final int field_9089;

    class_1888(int weight) {
        this.field_9089 = weight;
    }

    /** was getWeight() */
    public int method_8197() {
        return field_9089;
    }
}
