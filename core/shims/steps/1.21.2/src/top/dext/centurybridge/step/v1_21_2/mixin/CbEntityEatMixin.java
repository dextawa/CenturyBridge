package top.dext.centurybridge.step.v1_21_2.mixin;

import net.minecraft.class_10124;
import net.minecraft.class_1309;
import net.minecraft.class_1799;
import net.minecraft.class_1937;
import net.minecraft.class_5819;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import top.dext.centurybridge.step.v1_21_2.rt.CbEntityEat;

/**
 * Consumable: where 1.21.1 asked the entity -- eat(level, stack, food) for a food finished, triggerItemUseEffects
 * (with getEatingSound / getDrinkingSound) while an item was used -- a mod class's own 1.21.1 overrides answer
 * (CbEntityEat).
 */
@Mixin(class_10124.class)
public abstract class CbEntityEatMixin {

    @Inject(method = "method_62843", at = @At("HEAD"), cancellable = true)
    private void cb$useEffects(class_5819 random, class_1309 entity, class_1799 stack, int count, CallbackInfo ci) {
        if (CbEntityEat.overridesUseEffects(entity)) {
            CbEntityEat.triggerItemUseEffects(entity, stack, count);
            ci.cancel();
        }
    }

    @Inject(method = "method_62847", at = @At("HEAD"), cancellable = true)
    private void cb$eat(class_1937 level, class_1309 entity, class_1799 stack, CallbackInfoReturnable<class_1799> cir) {
        class_1799 r = CbEntityEat.consume(level, entity, stack);
        if (r != null) {
            cir.setReturnValue(r);
        }
    }
}
