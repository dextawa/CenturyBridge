package top.dext.centurybridge.step.v1_21_2.facade;

import net.minecraft.class_1304;
import net.minecraft.class_1738;
import net.minecraft.class_1792;
import net.minecraft.class_1799;
import net.minecraft.class_3414;
import net.minecraft.class_6880;
import net.minecraft.class_8051;
import net.minecraft.class_9285;
import top.dext.centurybridge.step.v1_21_2.rt.CbItemArmor;
import top.dext.centurybridge.step.v1_21_2.rt.CbItemMembers;

/**
 * The superclass of a converted 1.21.1 ArmorItem subclass (superRenames net/minecraft/class_1738): 1.21.2's
 * ArmorItem with 1.21.1's constructor, which took a registered 1.21.1 material holder (CbItemArmor.toW makes
 * the 1.21.2 material it is built from), and 1.21.1's ArmorItem members, which the subclass's super calls reach
 * here (the converter leaves a super call into a superRenames facade to the facade). Every other reference stays
 * on 1.21.2's ArmorItem, whose 1.21.1 members are CbItemArmor's statics.
 *
 * <p>The bodies are 1.21.1's ArmorItem's: getType / getMaterial / getDefense / getToughness / getEquipmentSlot /
 * getEquipSound from the material and type (CbItemArmor); getEnchantmentValue, isValidRepairItem and
 * getDefaultAttributeModifiers from the material too, which for an item built from a 1.21.1 material are exactly
 * what its built components hold (ENCHANTABLE = the enchantment value, absent at 0; REPAIRABLE = the repair
 * ingredient's items, 1.21.1's ingredient test, and Item's false besides; ATTRIBUTE_MODIFIERS = 1.21.1's armor,
 * toughness and knockback modifiers), read as CbItemMembers reads them.
 */
public class ArmorItem extends class_1738 {

    public ArmorItem(class_6880<ArmorMaterial> material, class_8051 type, class_1792.class_1793 properties) {
        super(CbItemArmor.toW(material), type, properties);
    }

    /** getType() */
    public class_8051 method_48398() {
        return CbItemArmor.super_method_48398(this);
    }

    /** getMaterial() */
    public class_6880<ArmorMaterial> method_7686() {
        return CbItemArmor.super_method_7686(this);
    }

    /** getDefense() */
    public int method_7687() {
        return CbItemArmor.super_method_7687(this);
    }

    /** getToughness() */
    public float method_26353() {
        return CbItemArmor.super_method_26353(this);
    }

    /** getEquipmentSlot() */
    public class_1304 method_7685() {
        return CbItemArmor.super_method_7685(this);
    }

    /** getEquipSound(): through this class's getMaterial(), as 1.21.1's did */
    public class_6880<class_3414> method_31570() {
        return method_7686().comp_349().comp_2300();
    }

    /** getEnchantmentValue() */
    public int method_7837() {
        return CbItemMembers.super_method_7837(this);
    }

    /** isValidRepairItem(stack, ingredient) */
    public boolean method_7878(class_1799 stack, class_1799 ingredient) {
        return CbItemMembers.super_method_7878(this, stack, ingredient);
    }

    /** getDefaultAttributeModifiers() */
    public class_9285 method_7844() {
        return CbItemMembers.super_method_7844(this);
    }
}
