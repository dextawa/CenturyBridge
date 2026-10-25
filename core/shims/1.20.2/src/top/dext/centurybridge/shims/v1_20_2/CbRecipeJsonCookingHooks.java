package top.dext.centurybridge.shims.v1_20_2;

import net.minecraft.class_3957;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Coerce;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import top.dext.centurybridge.rt.v1_20_2.CbRecipeJson;

/** SimpleCookingSerializer(factory, defaultCookingTime): the time kept in 1.20.1's field (CbRecipeJsonCooking) */
@Mixin(class_3957.class)
public abstract class CbRecipeJsonCookingHooks {

    @Inject(method = "<init>", at = @At("RETURN"))
    private void cb$keepDefaultCookingTime(@Coerce Object factory, int defaultCookingTime, CallbackInfo ci) {
        ((CbRecipeJson.CookingTime) (Object) this).cb$setDefaultCookingTime(defaultCookingTime);
    }
}
