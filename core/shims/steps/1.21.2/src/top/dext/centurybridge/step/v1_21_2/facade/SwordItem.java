package top.dext.centurybridge.step.v1_21_2.facade;

import net.minecraft.class_1792;
import net.minecraft.class_1799;
import top.dext.centurybridge.step.v1_21_2.rt.CbItemMembers;
import top.dext.centurybridge.step.v1_21_2.rt.CbItemTiers;

/**
 * The superclass of a converted 1.21.1 SwordItem subclass (superRenames net/minecraft/class_1829): 1.21.2's SwordItem built
 * from the 1.21.1 tier (CbItemTiers.toW: durability, enchantability, repair and Tool as 1.21.1's constructor set
 * them, the caller's attribute modifiers kept) with 1.21.1's constructor, and the TieredItem members the
 * subclass's super calls reach here (1.21.1's bodies; the enchantment value and repair check are what the built
 * components hold for a legacy tier).
 */
public class SwordItem extends net.minecraft.class_1829 {

    public SwordItem(Tier tier, class_1792.class_1793 properties) {
        super(CbItemTiers.toW(tier), 0.0F, 0.0F, properties);
    }

    /** getTier() */
    public Tier method_8022() {
        return CbItemTiers.super_tier(this);
    }

    /** getEnchantmentValue() */
    public int method_7837() {
        return CbItemMembers.super_method_7837(this);
    }

    /** isValidRepairItem(stack, ingredient) */
    public boolean method_7878(class_1799 stack, class_1799 ingredient) {
        return CbItemMembers.super_method_7878(this, stack, ingredient);
    }
}
