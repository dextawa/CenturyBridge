package top.dext.centurybridge.step.v1_20_5.facade;

import top.dext.centurybridge.step.v1_20_5.rt.CbFinItemArmor;

/**
 * 1.20.4's DyeableArmorItem (class_4057; classRenames): an ArmorItem that is a DyeableLeatherItem. 1.20.5's
 * leather armor is a plain ArmorItem in the minecraft:dyeable tag; this one is too (it joins the tag as tags
 * load).
 */
public class DyeableArmorItem extends net.minecraft.class_1738 implements DyeableLeatherItem {

    public DyeableArmorItem(ArmorMaterial material, net.minecraft.class_1738.class_8051 type, net.minecraft.class_1792.class_1793 properties) {
        super(CbFinItemArmor.holder(material), type, CbFinItemArmor.properties(material, type, properties));
    }
}
