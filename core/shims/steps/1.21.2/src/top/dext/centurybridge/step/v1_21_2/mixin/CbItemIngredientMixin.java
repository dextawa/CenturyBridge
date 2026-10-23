package top.dext.centurybridge.step.v1_21_2.mixin;

import net.minecraft.class_1799;
import net.minecraft.class_1856;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import top.dext.centurybridge.step.v1_21_2.rt.CbItemIngredients;

/**
 * Ingredient.test for 1.21.1's empty ingredient (CbItemIngredients.field_9017): it matches the empty stack, as
 * 1.21.1's EMPTY did.
 */
@Mixin(class_1856.class)
public abstract class CbItemIngredientMixin {

    @Inject(method = "method_8093", at = @At("HEAD"), cancellable = true)
    private void cb$emptyTest(class_1799 stack, CallbackInfoReturnable<Boolean> cir) {
        if (CbItemIngredients.isEmptyIngredient(this)) {
            cir.setReturnValue(stack.method_7960());
        }
    }
}
