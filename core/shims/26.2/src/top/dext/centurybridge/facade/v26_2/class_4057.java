package top.dext.centurybridge.facade.v26_2;

import net.minecraft.world.item.Item;

/** Facade for 1.20.1 net/minecraft/class_4057 (dyeable armor variant), deleted
 *  on 26.2. Extends the ArmorItem facade (hence real Item) and implements the
 *  DyeableLeatherItem facade as the original did; old (ArmorMaterial, Type,
 *  Properties) constructor forwards to it. The material arg is the legacy
 *  class_1741 facade (converted to a 26.2 record inside class_1738). */
public class class_4057 extends class_1738 implements class_1768 {
    public class_4057(class_1741 material, class_8051 type, Item.Properties props) {
        super(material, type, props);
    }
}
