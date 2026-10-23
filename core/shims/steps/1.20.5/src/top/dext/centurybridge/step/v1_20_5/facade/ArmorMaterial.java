package top.dext.centurybridge.step.v1_20_5.facade;

/**
 * 1.20.4's ArmorMaterial interface (class_1741; classRenames). 1.20.5 reuses the name for a registry record
 * (durability moved to the item's properties, the equip sound and repair ingredient behind a holder and a
 * supplier, the textures to layers), and items take a Holder of it. 1.20.4 code implements this interface
 * and hands its materials to ArmorItem; rt/CbFinItemArmor registers the 1.20.5 record standing for each one
 * (its values asked from these methods) and maps holders back to 1.20.4 materials.
 */
public interface ArmorMaterial {

    /** getDurabilityForType(type) */
    int method_48402(net.minecraft.class_1738.class_8051 type);

    /** getDefenseForType(type) */
    int method_48403(net.minecraft.class_1738.class_8051 type);

    /** getEnchantmentValue() */
    int method_7699();

    /** getEquipSound() */
    net.minecraft.class_3414 method_7698();

    /** getRepairIngredient() */
    net.minecraft.class_1856 method_7695();

    /** getName(): the texture name, "name" or "namespace:name" */
    String method_7694();

    /** getToughness() */
    float method_7700();

    /** getKnockbackResistance() */
    float method_24355();
}
