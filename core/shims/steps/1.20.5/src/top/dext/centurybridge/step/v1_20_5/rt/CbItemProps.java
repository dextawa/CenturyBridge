package top.dext.centurybridge.step.v1_20_5.rt;

/**
 * 1.20.4's per-item properties that 1.20.5 moved into the item's default data components (durability)
 * or dropped. A mod class that overrode one of them is marked by an adapter interface (the converter
 * adds it), and a call answers with that override; otherwise with what 1.20.4's Item answered, read
 * from 1.20.5's components.
 */
public final class CbItemProps {

    /** a mod class declaring 1.20.4's Item.canBeDepleted() */
    public interface CanBeDepleted {
        boolean method_7846();
    }

    /** item.canBeDepleted(): the class's own 1.20.4 override, else 1.20.4's body (a durability above zero) */
    public static boolean canBeDepleted(net.minecraft.class_1792 item) {
        return item instanceof CanBeDepleted c ? c.method_7846() : maxDamage(item) > 0;
    }

    /** super.canBeDepleted() in a mod subclass: 1.20.4's Item body */
    public static boolean superCanBeDepleted(net.minecraft.class_1792 item) {
        return maxDamage(item) > 0;
    }

    /**
     * item.getMaxDamage() (final in 1.20.4, so never overridden): the durability the item's properties
     * gave it -- 1.20.5 keeps it as the item's default max damage component -- and 0 without one.
     */
    public static int maxDamage(net.minecraft.class_1792 item) {
        Integer max = item.method_57347().method_57829(net.minecraft.class_9334.field_50072);
        return max == null ? 0 : max;
    }

    private CbItemProps() {
    }
}
