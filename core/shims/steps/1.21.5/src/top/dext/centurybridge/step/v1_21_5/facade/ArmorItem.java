package top.dext.centurybridge.step.v1_21_5.facade;

import net.minecraft.class_1741;
import net.minecraft.class_1792;
import net.minecraft.class_8051;

/**
 * 1.21.4's ArmorItem (net/minecraft/class_1738), gone in 1.21.5 (armor pieces are Items built from
 * Item.Properties.humanoidArmor, 1.21.4's ArmorMaterial.humanoidProperties moved there unchanged). Superclass of a
 * converted subclass (superRenames) and what a 1.21.4 {@code new ArmorItem(..)} builds (ctorRedirects); other
 * references name Item (classRenames), and a type test asks CbItemArmor. 1.21.4's class had only this constructor.
 */
public class ArmorItem extends class_1792 {

    public ArmorItem(class_1741 material, class_8051 type, class_1792.class_1793 properties) {
        super(properties.method_66332(material, type));
    }
}
