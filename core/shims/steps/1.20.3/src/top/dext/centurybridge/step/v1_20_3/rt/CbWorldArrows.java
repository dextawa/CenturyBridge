package top.dext.centurybridge.step.v1_20_3.rt;

import java.util.Set;
import net.minecraft.class_1293;
import net.minecraft.class_1309;
import net.minecraft.class_1667;
import net.minecraft.class_1799;
import net.minecraft.class_1802;
import net.minecraft.class_1842;
import net.minecraft.class_1844;
import net.minecraft.class_1847;
import net.minecraft.class_1937;

/**
 * Arrows: 1.20.3's arrow constructors take the item stack the arrow is picked up as (AbstractArrow keeps a
 * copy, and names the arrow after it when it has a custom name); 1.20.2's took none, and each arrow class
 * answered getPickupItem itself. Wired from s2world.json.
 */
public final class CbWorldArrows {

    /** NBT key of the 1.20.2 pickup mark, so an arrow keeps answering the 1.20.2 way after a reload */
    public static final String LEGACY_PICKUP_TAG = "centurybridge:legacy_pickup";

    /**
     * AbstractArrow(..) of a 1.20.2 subclass: no pickup stack. getPickupItem was abstract in 1.20.2, so the
     * subclass answers it itself; 1.20.3's AbstractArrow reads the stack only in that default, in saving it
     * and in getPickupItemStackOrigin (ThrownTrident's, about itself).
     */
    public static class_1799 noPickupItem() {
        return class_1799.field_8037;
    }

    /** Arrow(..) in a 1.20.2 subclass: the stack 1.20.3's own Arrow(EntityType, Level) passes (a plain arrow) */
    public static class_1799 arrowStack() {
        return new class_1799(class_1802.field_8107);
    }

    /** SpectralArrow(..): 1.20.2's SpectralArrow was always picked up as one spectral arrow */
    public static class_1799 spectralArrowStack() {
        return new class_1799(class_1802.field_8236);
    }

    /** new Arrow(level, shooter) of 1.20.2: the 1.20.3 arrow, marked to answer its pickup item the 1.20.2 way */
    public static class_1667 newArrow(class_1937 level, class_1309 shooter) {
        class_1667 arrow = new class_1667(level, shooter, arrowStack());
        ((CbWorldArrowAccess) arrow).cb$markLegacyPickup();
        return arrow;
    }

    /** new Arrow(level, x, y, z) of 1.20.2, as {@link #newArrow(class_1937, class_1309)} */
    public static class_1667 newArrow(class_1937 level, double x, double y, double z) {
        class_1667 arrow = new class_1667(level, x, y, z, arrowStack());
        ((CbWorldArrowAccess) arrow).cb$markLegacyPickup();
        return arrow;
    }

    /**
     * 1.20.2's Arrow.getPickupItem: a plain arrow when the arrow carries no potion and no effects, else a
     * tipped arrow with them (and the arrow's fixed colour). 1.20.3 instead puts them on a copy of the stack
     * the arrow was built with, which for an arrow a 1.20.2 constructor built is a plain arrow.
     */
    public static class_1799 legacyPickupItem(class_1842 potion, Set<class_1293> effects, boolean fixedColor, int color) {
        if (effects.isEmpty() && potion == class_1847.field_8984) {
            return new class_1799(class_1802.field_8107);
        }
        class_1799 stack = new class_1799(class_1802.field_8087);
        class_1844.method_8061(stack, potion);
        class_1844.method_8056(stack, effects);
        if (fixedColor) {
            stack.method_7948().method_10569("CustomPotionColor", color);
        }
        return stack;
    }

    private CbWorldArrows() {
    }
}
