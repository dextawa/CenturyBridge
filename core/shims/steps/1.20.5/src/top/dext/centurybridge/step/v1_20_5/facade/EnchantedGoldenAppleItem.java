package top.dext.centurybridge.step.v1_20_5.facade;

/**
 * 1.20.4's EnchantedGoldenAppleItem (class_1775; gone in 1.20.5, whose vanilla item of that kind is a plain Item with
 * enchantment_glint_override): an Item whose isFoil(stack) answers true, 1.20.4's body. 1.20.5's
 * ItemStack.hasFoil() asks the item's isFoil when the stack has no glint override, as 1.20.4's did.
 */
public class EnchantedGoldenAppleItem extends net.minecraft.class_1792 {

    public EnchantedGoldenAppleItem(net.minecraft.class_1792.class_1793 properties) {
        super(properties);
    }

    /** isFoil(stack): always */
    @Override
    public boolean method_7886(net.minecraft.class_1799 stack) {
        return true;
    }
}
