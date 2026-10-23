package top.dext.centurybridge.step.v1_21_2.rt;

import net.minecraft.class_1766;
import net.minecraft.class_1802;
import net.minecraft.class_1829;
import net.minecraft.class_2246;
import top.dext.centurybridge.step.v1_21_2.facade.ElytraItem;
import top.dext.centurybridge.step.v1_21_2.facade.Equipable;
import top.dext.centurybridge.step.v1_21_2.facade.TieredItem;

/**
 * Type tests and casts against 1.21.1 types 1.21.2 removed, whose 1.21.1 instances include vanilla objects that are
 * not instances of the facade (instanceOfRedirects / checkCastRedirects; the type itself is renamed to the widest
 * type those instances have on 1.21.2, so the cast that follows the check always holds):
 * <ul>
 * <li>TieredItem (renamed to Item): the facade (a converted class extending it), and 1.21.2's SwordItem and
 *     DiggerItem (with Axe/Pickaxe/Shovel/HoeItem and the converted classes extending them) -- 1.21.1's two
 *     TieredItem subclasses;</li>
 * <li>ElytraItem (renamed to Item): the facade, and the elytra item, 1.21.1's one vanilla ElytraItem;</li>
 * <li>Equipable (renamed to Object): the facade's implementors (converted classes, the views of
 *     CbItemEquipable, and 1.21.1's vanilla Equipables that implement it again by mixin: ArmorItem, ShieldItem,
 *     AbstractSkullBlock, WoolCarpetBlock), and the two 1.21.1 vanilla Equipables 1.21.2 left without a class of
 *     their own: the elytra item and the carved pumpkin block (1.21.1's EquipableCarvedPumpkinBlock).</li>
 * </ul>
 * A cast that 1.21.1 would have refused throws ClassCastException, as it did; null passes.
 */
public final class CbItemTypes {

    private CbItemTypes() {
    }

    /** {@code o instanceof TieredItem} */
    public static boolean isTieredItem(Object o) {
        return o instanceof TieredItem || o instanceof class_1829 || o instanceof class_1766;
    }

    /** {@code (TieredItem) o} */
    public static Object castTieredItem(Object o) {
        return check(o, isTieredItem(o), "TieredItem");
    }

    /** {@code o instanceof ElytraItem} */
    public static boolean isElytraItem(Object o) {
        return o instanceof ElytraItem || o == class_1802.field_8833;
    }

    /** {@code (ElytraItem) o} */
    public static Object castElytraItem(Object o) {
        return check(o, isElytraItem(o), "ElytraItem");
    }

    /** {@code o instanceof Equipable} */
    public static boolean isEquipable(Object o) {
        return o instanceof Equipable || o == class_1802.field_8833 || o == class_2246.field_10147;
    }

    /** {@code (Equipable) o} */
    public static Object castEquipable(Object o) {
        return check(o, isEquipable(o), "Equipable");
    }

    private static Object check(Object o, boolean is, String type) {
        if (o == null || is) {
            return o;
        }
        throw new ClassCastException("class " + o.getClass().getName() + " cannot be cast to 1.21.1's " + type);
    }
}
