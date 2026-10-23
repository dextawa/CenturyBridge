package top.dext.centurybridge.step.v1_21_2.facade;

import net.minecraft.class_1792;
import top.dext.centurybridge.step.v1_21_2.rt.CbItemConsumables;

/**
 * 1.21.1's EnchantedBookItem (net/minecraft/class_1772, classRenames), gone in 1.21.2 (the enchanted book is a
 * plain item with STORED_ENCHANTMENTS). isEnchantable(stack) answered false: so does an item without ENCHANTABLE,
 * which this one has unless its Properties give it (as 1.21.1's isEnchantable override ignored them, the class
 * answers false through the Item members' Legacy interface).
 */
public class EnchantedBookItem extends class_1792 implements top.dext.centurybridge.step.v1_21_2.rt.CbItemMembers.LegacyEnchantable {

    public EnchantedBookItem(class_1792.class_1793 properties) {
        super(properties);
    }

    /** isEnchantable(stack): false */
    @Override
    public boolean method_7870(net.minecraft.class_1799 stack) {
        return false;
    }

    /** createForEnchantment(instance): an enchanted book stack storing it */
    public static net.minecraft.class_1799 method_7808(net.minecraft.class_1889 instance) {
        return CbItemConsumables.enchantedBook(instance);
    }
}
