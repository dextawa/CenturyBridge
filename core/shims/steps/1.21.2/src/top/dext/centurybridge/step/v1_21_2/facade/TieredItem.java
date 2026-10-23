package top.dext.centurybridge.step.v1_21_2.facade;

import net.minecraft.class_1792;
import net.minecraft.class_1799;
import top.dext.centurybridge.step.v1_21_2.rt.CbItemMembers;

/**
 * 1.21.1's TieredItem (net/minecraft/class_1831, classRenames), gone in 1.21.2: an item made of a Tier. 1.21.1's
 * body: the durability is the tier's uses, the enchantment value and repair check the tier's. 1.21.2 reads the
 * enchantment value from ENCHANTABLE and the repair check from REPAIRABLE: this class answers them through the
 * Item members' Legacy interfaces (CbItemMembers: the components view, the anvil). 1.21.2's own tools are not
 * TieredItems; their getTier() is CbItemTiers'.
 */
public class TieredItem extends class_1792 implements CbItemMembers.LegacyEnchantmentValue, CbItemMembers.LegacyRepairItem {

    /** tier (public: an access widener never reaches a CenturyBridge class) */
    public final Tier field_8921;

    public TieredItem(Tier tier, class_1792.class_1793 properties) {
        super(properties.method_7895(tier.method_8025()));
        this.field_8921 = tier;
    }

    /** getTier() */
    public Tier method_8022() {
        return field_8921;
    }

    /** getEnchantmentValue(): the tier's */
    @Override
    public int method_7837() {
        return field_8921.method_8026();
    }

    /** isValidRepairItem(stack, ingredient): the tier's repair ingredient, or Item's (false) */
    @Override
    public boolean method_7878(class_1799 stack, class_1799 ingredient) {
        return field_8921.method_8023().method_8093(ingredient) || CbItemMembers.super_method_7878(this, stack, ingredient);
    }
}
