package top.dext.centurybridge.step.v1_21_2.facade;

import net.minecraft.class_1304;
import net.minecraft.class_1792;
import net.minecraft.class_1799;
import net.minecraft.class_2960;
import net.minecraft.class_3414;
import net.minecraft.class_4059;
import net.minecraft.class_6880;
import net.minecraft.class_8051;
import net.minecraft.class_9285;
import top.dext.centurybridge.step.v1_21_2.rt.CbItemArmor;
import top.dext.centurybridge.step.v1_21_2.rt.CbItemMembers;

/**
 * The superclass of a converted 1.21.1 AnimalArmorItem subclass (superRenames net/minecraft/class_4059): 1.21.2's
 * AnimalArmorItem with 1.21.1's constructor (a registered 1.21.1 material holder, the body type, whether it has a
 * dyed overlay texture) and 1.21.1's members, which the subclass's super calls reach here. 1.21.1's AnimalArmorItem
 * was an ArmorItem of type BODY: its ArmorItem members are here too, with 1.21.1's bodies (see facade ArmorItem;
 * isEnchantable(stack) answered false, and 1.21.2's animal armor has no ENCHANTABLE either).
 */
public class AnimalArmorItem extends class_4059 {

    public AnimalArmorItem(class_6880<ArmorMaterial> material, class_4059.class_9076 bodyType, boolean hasOverlay,
                           class_1792.class_1793 properties) {
        super(CbItemArmor.toW(material), bodyType, properties);
        ((CbItemArmor.AnimalFields) (Object) this).cb$setOverlay(hasOverlay);
    }

    /** getTexture() */
    public class_2960 method_18454() {
        return CbItemArmor.super_method_18454(this);
    }

    /** getOverlayTexture(): null without an overlay */
    public class_2960 method_57335() {
        return CbItemArmor.super_method_57335(this);
    }

    /** getBodyType() */
    public class_4059.class_9076 method_55756() {
        return CbItemArmor.super_method_55756(this);
    }

    /** isEnchantable(stack): false */
    public boolean method_7870(class_1799 stack) {
        return false;
    }

    /** getType(): BODY */
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

    /** getEquipmentSlot(): BODY */
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
