package top.dext.centurybridge.step.v1_21_2.mixin;

import java.util.List;
import net.minecraft.class_1792;
import net.minecraft.class_1799;
import net.minecraft.class_1856;
import net.minecraft.class_6880;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import top.dext.centurybridge.step.v1_21_2.rt.CbItemIngredients;
import top.dext.centurybridge.step.v1_21_2.rt.CbVanillaReads;

/**
 * Ingredient.test for 1.21.1's empty ingredient (CbItemIngredients.field_9017): it matches the empty stack, as
 * 1.21.1's EMPTY did.
 *
 * <p>1.21.1's getItems() (a stack of each item, cached) restored as a member of Ingredient, beside 1.21.2's
 * getItems() of the same name that returns the item holders. 1.21.1's test(stack) answered from getItems(); 1.21.2's
 * reads the holder list. test() consults the restored member: while it gives the holder list's items, 1.21.2's answer
 * stands; when a mod's mixin on getItems() makes it give something else, 1.21.1's answer from that
 * (CbItemIngredients.testRead).
 */
@Mixin(class_1856.class)
public abstract class CbItemIngredientMixin implements CbItemIngredients.IngredientStacks {

    @Unique
    private class_1799[] cb$stacks;

    @Unique
    private List<class_6880<class_1792>> cb$stacksOf;

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

    /** 1.21.1's getItems(): a stack of each of the ingredient's items, rebuilt when 1.21.2's item list is */
    @CbVanillaReads("CbItemIngredientMixin: test(stack) answers from it (cb$testReadsItems)")
    @Override
    public class_1799[] method_8105() {
        List<class_6880<class_1792>> items = ((class_1856) (Object) this).method_8105();
        if (this.cb$stacks == null || this.cb$stacksOf != items) {
            this.cb$stacks = CbItemIngredients.stacksOf(items);
            this.cb$stacksOf = items;
        }
        return this.cb$stacks;
    }
}
