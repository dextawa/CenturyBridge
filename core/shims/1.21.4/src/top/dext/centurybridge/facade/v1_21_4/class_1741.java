package top.dext.centurybridge.facade.v1_21_4;

/**
 * Facade for 1.20.1 net/minecraft/class_1741 (ArmorMaterial), an INTERFACE mods
 * implement to define custom armor materials. On 1.21.4 it became a final record
 * (armor materials are registry/record driven), so a legacy `implements
 * class_1741` fails to load ("implements a class"). Reached via superRenames: a
 * mod material class has only its implements clause pointed at this interface;
 * every OTHER class_1741 reference (code reading a vanilla ArmorMaterial through
 * the instanceRedirect/ledger wiring) stays on the real final record -- the
 * type-split superRenames exists to avoid.
 *
 * The mod material class implements these methods as before; whether 1.21.4's
 * record-based armor consumes the material is a separate (ledgered) concern.
 * Member names are intermediary tokens; the slot param class_8051 (ArmorItem.Type)
 * survives as a top-level enum on 1.21.4 and is used directly.
 */
public interface class_1741 {
    /** getDurabilityForType(type) */
    int method_48402(net.minecraft.class_8051 type);

    /** getDefenseForType(type) */
    int method_48403(net.minecraft.class_8051 type);

    /** getEnchantmentValue() */
    int method_7699();

    /** getEquipSound() */
    net.minecraft.class_3414 method_7698();

    /** getRepairIngredient() */
    net.minecraft.class_1856 method_7695();

    /** getName() */
    String method_7694();

    /** getToughness() */
    float method_7700();

    /** getKnockbackResistance() */
    float method_24355();
}
