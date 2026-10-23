package top.dext.centurybridge.step.v1_21.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import top.dext.centurybridge.step.v1_21.rt.CbItemFood;

/** Player.eat(level, stack, food): the stack being eaten stays at hand for a 1.20.6 FoodData.eat(stack) override (CbItemFood) */
@Mixin(net.minecraft.class_1657.class)
public abstract class CbItemFoodMixin {

    @Inject(method = "method_18866", at = @At("HEAD"))
    private void cbItem$eating(net.minecraft.class_1937 level, net.minecraft.class_1799 stack, net.minecraft.class_4174 food,
            CallbackInfoReturnable<net.minecraft.class_1799> cir) {
        CbItemFood.pushEating(stack);
    }

    @Inject(method = "method_18866", at = @At("RETURN"))
    private void cbItem$eaten(net.minecraft.class_1937 level, net.minecraft.class_1799 stack, net.minecraft.class_4174 food,
            CallbackInfoReturnable<net.minecraft.class_1799> cir) {
        CbItemFood.popEating();
    }
}
