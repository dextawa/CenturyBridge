package top.dext.centurybridge.facade.v26_2;

import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.EnchantmentInstance;
import top.dext.centurybridge.rt.v26_2.Statics;

/**
 * Facade for 1.20.1 net/minecraft/class_1772 (EnchantedBookItem), removed in
 * 1.21.2: the enchanted book is a plain Item carrying STORED_ENCHANTMENTS.
 * Only the static helpers survive here (forwarded to the runtime bridge);
 * "x instanceof EnchantedBookItem" is answered by the converter's instanceof
 * redirect (Statics.cbIsEnchantedBook), not by this class.
 */
public class class_1772 {

    private class_1772() {
    }

    /** createForEnchantment(EnchantmentInstance) */
    public static ItemStack method_7808(EnchantmentInstance instance) {
        return Statics.method_7808(instance);
    }

    /** getEnchantments(ItemStack): the stored-enchantments list in the 1.20.1 NBT shape ({id, lvl}) */
    public static net.minecraft.nbt.ListTag method_7806(ItemStack stack) {
        return Statics.method_7806(stack);
    }

    /** addEnchantment(ItemStack, EnchantmentInstance) */
    public static void method_7807(ItemStack stack, EnchantmentInstance instance) {
        Statics.method_7807(stack, instance);
    }
}
