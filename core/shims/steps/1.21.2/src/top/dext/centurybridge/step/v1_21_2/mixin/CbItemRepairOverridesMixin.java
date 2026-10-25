package top.dext.centurybridge.step.v1_21_2.mixin;

import net.minecraft.class_1738;
import net.minecraft.class_1792;
import net.minecraft.class_1799;
import org.spongepowered.asm.mixin.Mixin;
import top.dext.centurybridge.step.v1_21_2.rt.CbItemMembers;
import top.dext.centurybridge.step.v1_21_2.rt.CbVanillaReads;

/**
 * ArmorItem's 1.21.1 isValidRepairItem(stack, ingredient) override (the material's repair ingredient), restored so a
 * mod's mixin on it has its method: it answers the REPAIRABLE the item was built with, which 1.21.2 builds from the
 * same material (1.21.1's body read the material holder field 1.21.2's ArmorItem no longer has). 1.21.2's
 * ItemStack.isValidRepairItem (the anvil's check) goes through it (CbItemStackMembersMixin) and follows it only where
 * it answers otherwise than that body -- a mod's mixin. ShieldItem: CbItemShieldRepairMixin; MaceItem:
 * CbItemMaceMembersMixin.
 */
@Mixin(value = class_1738.class, priority = CbVanillaReads.PRIORITY)
public abstract class CbItemRepairOverridesMixin {

    /** isValidRepairItem(stack, ingredient) */
    @CbVanillaReads("CbItemStackMembersMixin: ItemStack.isValidRepairItem (AnvilMenu's repair check) answers from it")
    public boolean method_7878(class_1799 stack, class_1799 ingredient) {
        return CbItemMembers.super_method_7878((class_1792) (Object) this, stack, ingredient);
    }
}
