package top.dext.centurybridge.step.v1_21_2.mixin;

import net.minecraft.class_1720;
import net.minecraft.class_1799;
import net.minecraft.class_1874;
import net.minecraft.class_1937;
import net.minecraft.class_3956;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import top.dext.centurybridge.step.v1_21_2.rt.CbItemRecipeMenus;

/**
 * 1.21.1's AbstractFurnaceMenu.canSmelt(stack) asked the level's recipe manager for a recipe of the menu's recipe type
 * matching the stack; 1.21.2 tests a recipe property set the menu is built with (furnace / blast furnace / smoker
 * input: the ingredients of those recipe types). A 1.21.1 mod furnace menu of its own recipe type has no such set
 * (its constructor's key is CbItemRecipeMenus.furnaceInputs), so for it canSmelt is 1.21.1's lookup.
 */
@Mixin(class_1720.class)
public abstract class CbItemFurnaceMenuMixin {

    @Shadow
    @Final
    private class_3956<? extends class_1874> field_17494;

    @Shadow
    @Final
    protected class_1937 field_7822;

    @Inject(method = "method_7640(Lnet/minecraft/class_1799;)Z", at = @At("HEAD"), cancellable = true)
    private void cb$legacyCanSmelt(class_1799 stack, CallbackInfoReturnable<Boolean> cir) {
        if (!CbItemRecipeMenus.hasInputSet(field_17494)) {
            cir.setReturnValue(CbItemRecipeMenus.legacyCanSmelt(field_17494, stack, field_7822));
        }
    }
}
