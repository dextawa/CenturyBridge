package top.dext.centurybridge.step.v1_21_2.mixin;

import net.minecraft.class_1792;
import net.minecraft.class_1799;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import top.dext.centurybridge.step.v1_21_2.rt.CbItemMembers;

/**
 * ItemStack for a converted 1.21.1 item class overriding isEnchantable(stack) / isValidRepairItem(stack,
 * ingredient) (CbItemMembers): 1.21.2's ItemStack.isEnchantable() and isValidRepairItem(ingredient) read the
 * ENCHANTABLE / REPAIRABLE components where 1.21.1's asked the item.
 */
@Mixin(class_1799.class)
public abstract class CbItemStackMembersMixin {

    @Shadow
    public abstract class_1792 method_7909();

    /** isEnchantable(): 1.21.1's -- the item's isEnchantable(stack), and an empty ENCHANTMENTS component */
    @Inject(method = "method_7923", at = @At("HEAD"), cancellable = true)
    private void cb$enchantable(CallbackInfoReturnable<Boolean> cir) {
        if (method_7909() instanceof CbItemMembers.LegacyEnchantable l) {
            class_1799 self = (class_1799) (Object) this;
            net.minecraft.class_9304 e = self.method_57824(net.minecraft.class_9334.field_49633);
            cir.setReturnValue(l.method_7870(self) && e != null && e.method_57543());
        }
    }

    /** isValidRepairItem(ingredient): the item's 1.21.1 isValidRepairItem(stack, ingredient) */
    @Inject(method = "method_61655", at = @At("HEAD"), cancellable = true)
    private void cb$repair(class_1799 ingredient, CallbackInfoReturnable<Boolean> cir) {
        if (method_7909() instanceof CbItemMembers.LegacyRepairItem l) {
            cir.setReturnValue(l.method_7878((class_1799) (Object) this, ingredient));
        }
    }
}
