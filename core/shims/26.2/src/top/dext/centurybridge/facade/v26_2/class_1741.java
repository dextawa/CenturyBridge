package top.dext.centurybridge.facade.v26_2;

/**
 * Facade for 1.20.1 net/minecraft/class_1741 (ArmorMaterial), an INTERFACE mods
 * implement to define custom armor materials. On 26.2 it became a final record
 * (armor is data-component driven), so a legacy `implements class_1741` fails to
 * load ("implements a class"). Reached via superRenames: a mod material class
 * has only its implements clause pointed at this interface; every OTHER
 * class_1741 reference (code reading a vanilla ArmorMaterial via the
 * instanceRedirects/ledger wiring) stays on the real final type -- that is the
 * type-split superRenames exists to avoid.
 *
 * The mod material class implements these methods as before; whether 26.2's
 * data-driven armor system consumes the material is a separate (ledgered)
 * concern. Member names are intermediary tokens; the slot param is the
 * class_8051 (ArmorItem.Type) facade.
 */
public interface class_1741 {
    /** getDurabilityForType(type) */
    int method_48402(class_8051 type);

    /** getDefenseForType(type) */
    int method_48403(class_8051 type);

    /** getEnchantmentValue() */
    int method_7699();

    /** getEquipSound() */
    net.minecraft.sounds.SoundEvent method_7698();

    /** getRepairIngredient() */
    net.minecraft.world.item.crafting.Ingredient method_7695();

    /** getName() */
    String method_7694();

    /** getToughness() */
    float method_7700();

    /** getKnockbackResistance() */
    float method_24355();
}
