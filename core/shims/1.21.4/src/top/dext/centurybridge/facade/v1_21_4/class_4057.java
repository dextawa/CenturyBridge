package top.dext.centurybridge.facade.v1_21_4;

/**
 * Facade for 1.20.1 net/minecraft/class_4057 (DyeableArmorItem), deleted at 1.20.5
 * when dyeable colour became a data component. Corpus armor mods subclass it (cook
 * armor set, IRModularArmor, VibraniumWeave, ...). Its ancestor class_1738 (ArmorItem)
 * is still a non-final public class on 1.21.4 whose constructor takes the exact same
 * (ArmorMaterial, ArmorType, Properties) triple the old class_4057 did. The old
 * class_1768 (Dyeable) interface was itself deleted at 1.20.5 (dye colour is a data
 * component now), so the facade only extends the real ArmorItem and forwards the
 * constructor; the dye behaviour that interface carried is a ledgered degrade. The
 * subclass registers as a genuine armor item. Reached via superRenames; member names
 * are intermediary tokens.
 */
public class class_4057 extends net.minecraft.class_1738 {

    public class_4057(net.minecraft.class_1741 material, net.minecraft.class_8051 type,
            net.minecraft.class_1792.class_1793 props) {
        super(material, type, props);
    }
}
