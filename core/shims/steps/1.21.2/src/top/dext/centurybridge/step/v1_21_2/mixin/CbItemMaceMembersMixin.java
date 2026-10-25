package top.dext.centurybridge.step.v1_21_2.mixin;

import net.minecraft.class_1799;
import net.minecraft.class_1802;
import net.minecraft.class_9362;
import org.spongepowered.asm.mixin.Mixin;
import top.dext.centurybridge.step.v1_21_2.rt.CbVanillaReads;

/**
 * MaceItem's 1.21.1 getEnchantmentValue() (15) and isValidRepairItem(stack, ingredient) (a breeze rod) overrides,
 * restored with 1.21.1's bodies so a mod's mixin on them finds its methods and anchors. Vanilla's enchanting reads
 * (CbItemEnchantableReadsMixin) and the anvil's check (CbItemStackMembersMixin) go through them and follow them only
 * where they answer otherwise than those bodies -- a mod's mixin (CbItemMembers.vanilla_method_7837 /
 * vanilla_method_7878). Priority below every mod's (CbVanillaReads.PRIORITY).
 */
@Mixin(value = class_9362.class, priority = CbVanillaReads.PRIORITY)
public abstract class CbItemMaceMembersMixin {

    /** getEnchantmentValue() */
    @CbVanillaReads("CbItemEnchantableReadsMixin: EnchantmentHelper.getEnchantmentCost / selectEnchantment take the ENCHANTABLE they read from it")
    public int method_7837() {
        return 15;
    }

    /** isValidRepairItem(stack, ingredient) */
    @CbVanillaReads("CbItemStackMembersMixin: ItemStack.isValidRepairItem (AnvilMenu's repair check) answers from it")
    public boolean method_7878(class_1799 stack, class_1799 ingredient) {
        return ingredient.method_31574(class_1802.field_49821);
    }
}
