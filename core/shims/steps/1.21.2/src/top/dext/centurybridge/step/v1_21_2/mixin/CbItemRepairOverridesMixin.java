package top.dext.centurybridge.step.v1_21_2.mixin;

import net.minecraft.class_1738;
import net.minecraft.class_1792;
import net.minecraft.class_1799;
import net.minecraft.class_1819;
import net.minecraft.class_9362;
import org.spongepowered.asm.mixin.Mixin;
import top.dext.centurybridge.step.v1_21_2.rt.CbItemMembers;
import top.dext.centurybridge.step.v1_21_2.rt.CbVanillaReads;

/**
 * 1.21.1's isValidRepairItem(stack, ingredient) overrides of ArmorItem (the material's repair ingredient), ShieldItem
 * (planks) and MaceItem (breeze rods), restored on those classes so a mod's mixin on them has its method: each
 * answers the REPAIRABLE the item was built with, what 1.21.1's override answered. 1.21.2's ItemStack.
 * isValidRepairItem (the anvil's check) goes through it (CbItemStackMembersMixin) and follows it only where it
 * answers otherwise -- a mod's mixin.
 */
@Mixin(value = {class_1738.class, class_1819.class, class_9362.class}, priority = CbVanillaReads.PRIORITY)
public abstract class CbItemRepairOverridesMixin {

    /** isValidRepairItem(stack, ingredient) */
    @CbVanillaReads("CbItemStackMembersMixin: ItemStack.isValidRepairItem (AnvilMenu's repair check) answers from it")
    public boolean method_7878(class_1799 stack, class_1799 ingredient) {
        return CbItemMembers.super_method_7878((class_1792) (Object) this, stack, ingredient);
    }
}
