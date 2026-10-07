package top.dext.centurybridge.facade.v26_2;

import net.minecraft.world.entity.EquipmentSlot;

/**
 * Facade for 1.20.1 net/minecraft/class_1738$class_8051 (ArmorItem.Type), the
 * armor-slot enum deleted on 26.2 (armor is data-component driven). Legacy
 * armor items pass a Type constant to the ArmorItem super-constructor and read
 * its slot/name. Kept as a standalone enum so those classes load. Constant and
 * member names are intermediary tokens.
 */
public enum class_8051 {
    field_41934(EquipmentSlot.HEAD, "helmet"),     // HELMET
    field_41935(EquipmentSlot.CHEST, "chestplate"), // CHESTPLATE
    field_41936(EquipmentSlot.LEGS, "leggings"),    // LEGGINGS
    field_41937(EquipmentSlot.FEET, "boots");       // BOOTS

    private final EquipmentSlot field_41938;
    private final String field_41939;

    class_8051(EquipmentSlot slot, String name) {
        this.field_41938 = slot;
        this.field_41939 = name;
    }

    /** getSlot() */
    public EquipmentSlot method_48399() {
        return field_41938;
    }

    /** getName() */
    public String method_48400() {
        return field_41939;
    }
}
