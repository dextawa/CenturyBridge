package top.dext.centurybridge.step.v1_21_2.facade;

import net.minecraft.class_1792;
import net.minecraft.class_1799;
import top.dext.centurybridge.step.v1_21_2.rt.CbItemMembers;

/**
 * 1.21.1's BookItem (net/minecraft/class_1751, classRenames), gone in 1.21.2 (the book is a plain item with an
 * ENCHANTABLE component). isEnchantable(stack): a stack of one; getEnchantmentValue(): 1 -- answered where 1.21.2
 * reads enchantability through the Item members' Legacy interfaces (CbItemMembers).
 */
public class BookItem extends class_1792 implements CbItemMembers.LegacyEnchantable, CbItemMembers.LegacyEnchantmentValue {

    public BookItem(class_1792.class_1793 properties) {
        super(properties);
    }

    /** isEnchantable(stack): a single book */
    @Override
    public boolean method_7870(class_1799 stack) {
        return stack.method_7947() == 1;
    }

    /** getEnchantmentValue(): 1 */
    @Override
    public int method_7837() {
        return 1;
    }
}
