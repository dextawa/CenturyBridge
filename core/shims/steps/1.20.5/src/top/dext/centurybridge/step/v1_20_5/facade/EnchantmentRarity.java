package top.dext.centurybridge.step.v1_20_5.facade;

/**
 * Enchantment.Rarity (gone in 1.20.5, which keeps a weight and an anvil cost in each enchantment's
 * definition): 1.20.4's four rarities with their selection weights, constants under 1.20.4's
 * intermediary names in 1.20.4's order.
 */
public enum EnchantmentRarity {
    /** COMMON */
    field_9087(10),
    /** UNCOMMON */
    field_9090(5),
    /** RARE */
    field_9088(2),
    /** VERY_RARE */
    field_9091(1);

    /** weight */
    public final int field_9089;

    EnchantmentRarity(int weight) {
        this.field_9089 = weight;
    }

    /** getWeight() */
    public int method_8197() {
        return field_9089;
    }
}
