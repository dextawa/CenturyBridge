package top.dext.centurybridge.step.v1_21_2.mixin;

import net.minecraft.class_1702;
import net.minecraft.class_3222;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import top.dext.centurybridge.step.v1_21_2.rt.CbPrioritySplit;

/**
 * The injector into 1.21.2's own FoodData.tick that goes with {@link CbItemFoodDataMixin} (its restored 1.21.1 members
 * stay there, at {@code CbVanillaReads.PRIORITY}, so a mod's 1.21.1 injectors can anchor inside them).
 */
@Mixin(class_1702.class)
public abstract class CbItemFoodDataMixinHooks {

    @Shadow
    private int field_7756;

    @Inject(method = "method_7588(Lnet/minecraft/class_3222;)V", at = @At("HEAD"))
    private void cb$tick(class_3222 player, CallbackInfo ci) {
        ((CbPrioritySplit.FoodData) (Object) this).cb$v1_21_2$lastFoodLevel(this.field_7756);
    }
}
