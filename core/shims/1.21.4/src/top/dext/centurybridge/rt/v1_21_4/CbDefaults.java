package top.dext.centurybridge.rt.v1_21_4;

import net.minecraft.class_1799;
import net.minecraft.class_1802;

/** Sentinel values the bridges hand to new-API constructors when the old API had no such argument. */
public final class CbDefaults {

    /**
     * 1.21.2 gave every ThrownItemEntity constructor an ItemStack; the 1.20.1 forms without one meant
     * "a stack of getDefaultItem()". Passing EMPTY would leave the projectile itemless (invisible, no
     * potion effect), so the factories/reshapes pass this sentinel and ThrownItemEntityBridge swaps in
     * the subclass default at the end of the constructor.
     */
    public static final class_1799 THROWN_DEFAULT_ITEM = new class_1799(class_1802.field_8162);

    private CbDefaults() {
    }
}
