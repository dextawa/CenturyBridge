package top.dext.centurybridge.step.v1_21_2.mixin;

import net.minecraft.class_1799;
import net.minecraft.class_1890;
import net.minecraft.class_5819;
import net.minecraft.class_9889;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyVariable;
import top.dext.centurybridge.step.v1_21_2.rt.CbItemMembers;

/**
 * 1.21.1's EnchantmentHelper asked item.getEnchantmentValue() where 1.21.2's reads the stack's ENCHANTABLE
 * (getEnchantmentCost, selectEnchantment; javap of both): the value read is the item's restored
 * getEnchantmentValue() (CbItemMembersMixin) unless the stack sets an ENCHANTABLE of its own
 * (CbItemMembers.enchantableRead). Unmixed it is the same value; a 1.21.1 mod's mixin on Item.getEnchantmentValue
 * changes enchanting again.
 */
@Mixin(class_1890.class)
public abstract class CbItemEnchantableReadsMixin {

    /** getEnchantmentCost(random, slot, bookshelves, stack): its ENCHANTABLE local */
    @ModifyVariable(method = "method_8227", at = @At("STORE"), ordinal = 0)
    private static class_9889 cb$costEnchantable(class_9889 read, class_5819 random, int slot, int bookshelves,
                                                 class_1799 stack) {
        return CbItemMembers.enchantableRead(stack, read);
    }

    /** selectEnchantment(random, stack, level, enchantments): its ENCHANTABLE local */
    @ModifyVariable(method = "method_8230", at = @At("STORE"), ordinal = 0)
    private static class_9889 cb$selectEnchantable(class_9889 read, class_5819 random, class_1799 stack, int level,
                                                   java.util.stream.Stream<?> enchantments) {
        return CbItemMembers.enchantableRead(stack, read);
    }
}
