package top.dext.centurybridge.step.v1_21_2.mixin;

import net.minecraft.class_1792;
import net.minecraft.class_1799;
import net.minecraft.class_1819;
import net.minecraft.class_3489;
import org.spongepowered.asm.mixin.Mixin;
import top.dext.centurybridge.step.v1_21_2.rt.CbItemMembers;
import top.dext.centurybridge.step.v1_21_2.rt.CbVanillaReads;

/**
 * ShieldItem's 1.21.1 isValidRepairItem(stack, ingredient) override, restored with 1.21.1's body so a mod's mixin on
 * it finds its method and anchors: planks, or what Item's body answers (the super call is Item's restored body,
 * CbItemMembers.super_method_7878: the compile stub's Item has no such method to call). The anvil's check goes through
 * it (CbItemStackMembersMixin) and follows it only where it answers otherwise than that body -- a mod's mixin
 * (CbItemMembers.vanilla_method_7878). Priority below every mod's (CbVanillaReads.PRIORITY).
 */
@Mixin(value = class_1819.class, priority = CbVanillaReads.PRIORITY)
public abstract class CbItemShieldRepairMixin {

    /** isValidRepairItem(stack, ingredient) */
    @CbVanillaReads("CbItemStackMembersMixin: ItemStack.isValidRepairItem (AnvilMenu's repair check) answers from it")
    public boolean method_7878(class_1799 stack, class_1799 ingredient) {
        return ingredient.method_31573(class_3489.field_15537)
            || CbItemMembers.super_method_7878((class_1792) (Object) this, stack, ingredient);
    }
}
