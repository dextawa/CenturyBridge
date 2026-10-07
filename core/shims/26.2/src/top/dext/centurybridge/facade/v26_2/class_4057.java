package top.dext.centurybridge.facade.v26_2;

import net.minecraft.world.item.Item;
import net.minecraft.world.item.equipment.ArmorMaterial;

/** Facade for 1.20.1 net/minecraft/class_4057 (ArmorStandItem/dyeable armor
 *  variant), deleted on 26.2. Extends the ArmorItem facade (hence real Item)
 *  and implements the DyeableLeatherItem facade as the original did; old
 *  (ArmorMaterial, Type, Properties) constructor forwards to it. */
public class class_4057 extends class_1738 implements class_1768 {
    public class_4057(ArmorMaterial material, class_8051 type, Item.Properties props) {
        super(material, type, props);
    }
}
