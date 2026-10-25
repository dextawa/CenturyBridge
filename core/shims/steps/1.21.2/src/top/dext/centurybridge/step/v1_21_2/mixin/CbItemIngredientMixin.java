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
 *
 * <p>1.21.1's test(stack) answered from getItems() (restored by CbItemIngredientStacksMixin); 1.21.2's reads the
 * holder list. test() consults the restored member: while it gives the holder list's items, 1.21.2's answer stands;
 * when a mod's mixin on getItems() makes it give something else, 1.21.1's answer from that
 * (CbItemIngredients.testRead).
 */
@Mixin(class_1856.class)
public abstract class CbItemIngredientMixin {

    @Inject(method = "method_8093", at = @At("HEAD"), cancellable = true)
    private void cb$emptyTest(class_1799 stack, CallbackInfoReturnable<Boolean> cir) {
        if (CbItemIngredients.isEmptyIngredient(this)) {
            cir.setReturnValue(stack.method_7960());
        }
    }

    @Inject(method = "method_8093", at = @At("RETURN"), cancellable = true)
    private void cb$testReadsItems(class_1799 stack, CallbackInfoReturnable<Boolean> cir) {
        boolean w = cir.getReturnValueZ();
        if (CbItemIngredients.testRead((class_1856) (Object) this, stack, w) != w) {
            cir.setReturnValue(!w);
        }
    }
}
