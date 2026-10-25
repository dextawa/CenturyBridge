package top.dext.centurybridge.step.v1_21_2.mixin;

import net.minecraft.class_1792;
import net.minecraft.class_1799;
import org.spongepowered.asm.mixin.Mixin;
import top.dext.centurybridge.step.v1_21_2.rt.CbItemMembers;
import top.dext.centurybridge.step.v1_21_2.rt.CbVanillaReads;

/**
 * 1.21.1's overridable Item members that 1.21.2 dropped, restored on Item (CbItemMembers): a 1.21.1 class's override
 * overrides them, and 1.21.2's reads go through them (each member's CbVanillaReads names the mixin). Priority below
 * every mod's so a mod's mixin may anchor anywhere in them (CbVanillaReads.PRIORITY).
 */
@Mixin(value = class_1792.class, priority = CbVanillaReads.PRIORITY)
public abstract class CbItemMembersRestoredMixin implements CbItemMembers.ItemEnchantmentValue, CbItemMembers.ItemRepair,
        CbItemMembers.ItemDescriptionId, CbItemMembers.ItemEnchantable {

    /**
     * 1.21.1's Item.getEnchantmentValue(), restored: what the item's built components say (0 without ENCHANTABLE).
     * A 1.21.1 class's own getEnchantmentValue overrides it; 1.21.2's EnchantmentHelper reads ENCHANTABLE through
     * it, so a mod's 1.21.1 mixin on it changes enchanting as it did on 1.21.1.
     */
    @CbVanillaReads("CbItemEnchantableReadsMixin: EnchantmentHelper.getEnchantmentCost / selectEnchantment take the "
        + "ENCHANTABLE they read from Item.getEnchantmentValue() unless the stack sets its own")
    @Override
    public int method_7837() {
        return CbItemMembers.super_method_7837((class_1792) (Object) this);
    }

    /** 1.21.1's Item.isValidRepairItem(stack, ingredient), restored: the REPAIRABLE the item was built with (CbItemMembers) */
    @CbVanillaReads("CbItemStackMembersMixin: ItemStack.isValidRepairItem (AnvilMenu's repair check) answers from it")
    @Override
    public boolean method_7878(class_1799 stack, class_1799 ingredient) {
        return CbItemMembers.super_method_7878((class_1792) (Object) this, stack, ingredient);
    }

    /** 1.21.1's Item.getDescriptionId(stack), restored: the item's description id */
    @CbVanillaReads("CbItemMembersMixin: Item.getName(stack) and (CbItemNameReadsMixin) the overrides of "
        + "CompassItem / PotionItem / TippedArrowItem / ShieldItem name the stack from it where it answers otherwise "
        + "than its body")
    @Override
    public String method_7866(class_1799 stack) {
        return CbItemMembers.super_method_7866((class_1792) (Object) this, stack);
    }

    /** 1.21.1's Item.isEnchantable(stack), restored with its body: a stack of one with MAX_DAMAGE */
    @CbVanillaReads("CbItemStackMembersMixin: ItemStack.isEnchantable() answers from it where it answers otherwise than its body")
    @Override
    public boolean method_7870(class_1799 stack) {
        return stack.method_7914() == 1 && stack.method_57826(net.minecraft.class_9334.field_50072);
    }
}
