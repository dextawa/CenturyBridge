package top.dext.centurybridge.facade.v26_2;

/**
 * Facade for 1.20.1 net/minecraft/class_1886 (EnchantmentTarget), deleted in
 * 26.2. Legacy mods pass a target constant to the Enchantment super-constructor
 * and call isAcceptableItem. Kept as a standalone enum so those classes load;
 * the acceptability test degrades to match-all (enchantment behaviour itself is
 * dead on 26.2's data-driven system -- this only keeps the class loadable).
 * Field names are the intermediary tokens the mod bytecode carries.
 */
public enum class_1886 {
    field_9068,  // ALL
    field_9079,  // ARMOR
    field_9076,  // ARMOR_FEET
    field_9071,  // ARMOR_LEGS
    field_9080,  // ARMOR_CHEST
    field_9074,  // ARMOR_HEAD
    field_9069,  // WEAPON
    field_9072,  // DIGGER
    field_9073,  // FISHING_ROD
    field_9082,  // TRIDENT
    field_9070,  // BREAKABLE
    field_9078,  // BOW
    field_9081,  // WEARABLE
    field_23747; // CROSSBOW

    /** was isAcceptableItem(Item): match-all (enchantment logic is dead on 26.2) */
    public boolean method_8177(net.minecraft.world.item.Item item) {
        return true;
    }
}
