package top.dext.centurybridge.step.v1_21_5.rt;

import net.minecraft.class_1743;
import net.minecraft.class_1794;
import net.minecraft.class_1802;
import net.minecraft.class_1821;
import top.dext.centurybridge.step.v1_21_5.facade.DiggerItem;
import top.dext.centurybridge.step.v1_21_5.facade.PickaxeItem;
import top.dext.centurybridge.step.v1_21_5.facade.SwordItem;

/**
 * Type tests and casts against 1.21.4's SwordItem, DiggerItem and PickaxeItem, which 1.21.5 removed
 * (instanceOfRedirects / checkCastRedirects; the types are renamed to Item, the widest type their 1.21.4 instances
 * have on 1.21.5, so the cast that follows a check always holds). A 1.21.4 instance is either a converted class
 * (under the facade) or one of 1.21.4's vanilla instances, which 1.21.5 builds as plain Items:
 * <ul>
 * <li>SwordItem: the six swords (wooden, stone, golden, iron, diamond, netherite -- 1.21.4's Items.&lt;clinit&gt;
 *     builds exactly these with {@code new SwordItem});</li>
 * <li>PickaxeItem: the six pickaxes (likewise {@code new PickaxeItem});</li>
 * <li>DiggerItem: PickaxeItem's, and 1.21.5's AxeItem, ShovelItem and HoeItem (1.21.4's other DiggerItem
 *     subclasses, which 1.21.5 keeps as Item subclasses; a converted subclass of them is one too).</li>
 * </ul>
 * A cast 1.21.4 would have refused throws ClassCastException, as it did; null passes.
 */
public final class CbItemToolTypes {

    private CbItemToolTypes() {
    }

    /** {@code o instanceof SwordItem} */
    public static boolean isSwordItem(Object o) {
        return o instanceof SwordItem || o == class_1802.field_8091 || o == class_1802.field_8528 || o == class_1802.field_8845
            || o == class_1802.field_8371 || o == class_1802.field_8802 || o == class_1802.field_22022;
    }

    /** {@code (SwordItem) o} */
    public static Object castSwordItem(Object o) {
        return check(o, isSwordItem(o), "SwordItem");
    }

    /** {@code o instanceof PickaxeItem} */
    public static boolean isPickaxeItem(Object o) {
        return o instanceof PickaxeItem || o == class_1802.field_8647 || o == class_1802.field_8387 || o == class_1802.field_8335
            || o == class_1802.field_8403 || o == class_1802.field_8377 || o == class_1802.field_22024;
    }

    /** {@code (PickaxeItem) o} */
    public static Object castPickaxeItem(Object o) {
        return check(o, isPickaxeItem(o), "PickaxeItem");
    }

    /** {@code o instanceof DiggerItem} */
    public static boolean isDiggerItem(Object o) {
        return o instanceof DiggerItem || o instanceof class_1743 || o instanceof class_1821 || o instanceof class_1794
            || isPickaxeItem(o);
    }

    /** {@code (DiggerItem) o} */
    public static Object castDiggerItem(Object o) {
        return check(o, isDiggerItem(o), "DiggerItem");
    }

    private static Object check(Object o, boolean is, String type) {
        if (o == null || is) {
            return o;
        }
        throw new ClassCastException("class " + o.getClass().getName() + " cannot be cast to 1.21.4's " + type);
    }
}
