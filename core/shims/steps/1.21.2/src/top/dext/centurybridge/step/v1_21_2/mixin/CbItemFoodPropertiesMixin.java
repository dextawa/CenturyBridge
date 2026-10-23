package top.dext.centurybridge.step.v1_21_2.mixin;

import net.minecraft.class_1792;
import net.minecraft.class_4174;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import top.dext.centurybridge.step.v1_21_2.rt.CbItemFood;

/**
 * Item.Properties.food(food) for a food 1.21.1 code gave an eat time, remainder or effects (CbItemFood): the
 * CONSUMABLE and USE_REMAINDER that eat it as 1.21.1 did. Every other food goes through 1.21.2's default.
 */
@Mixin(class_1792.class_1793.class)
public abstract class CbItemFoodPropertiesMixin {

    @Inject(method = "method_19265", at = @At("HEAD"), cancellable = true)
    private void cb$food(class_4174 food, CallbackInfoReturnable<class_1792.class_1793> cir) {
        CbItemFood.Extra extra = CbItemFood.extra(food);
        if (extra != null) {
            cir.setReturnValue(CbItemFood.food((class_1792.class_1793) (Object) this, food, extra));
        }
    }
}
