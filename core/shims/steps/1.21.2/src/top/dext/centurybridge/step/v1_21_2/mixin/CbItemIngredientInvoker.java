package top.dext.centurybridge.step.v1_21_2.mixin;

import net.minecraft.class_1792;
import net.minecraft.class_1856;
import net.minecraft.class_6885;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Invoker;

/** Ingredient's private constructor, for 1.21.1's empty ingredient (CbItemIngredients) */
@Mixin(class_1856.class)
public interface CbItemIngredientInvoker {

    @Invoker("<init>")
    static class_1856 cb$create(class_6885<class_1792> items) {
        throw new AssertionError();
    }
}
