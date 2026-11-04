package top.dext.centurybridge.step.v1_21_4.mixin;

import net.minecraft.class_1937;
import net.minecraft.class_2338;
import net.minecraft.class_2680;
import net.minecraft.class_10323;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import top.dext.centurybridge.step.v1_21_4.rt.CbRestCreakingHeart;

/** Restores V's day/night enum transitions at the same point in the surviving native tick. */
@Mixin(class_10323.class)
public abstract class CbRestCreakingHeartEntityMixin {

    @Inject(method = "method_64804",
        at = @At(value = "INVOKE",
            target = "Lnet/minecraft/class_10319;method_65147(Lnet/minecraft/class_1937;)Z",
            ordinal = 1))
    private static void cb$restoreDayNightState(class_1937 level, class_2338 pos, class_2680 state,
            class_10323 heart, CallbackInfo ci) {
        CbRestCreakingHeart.updateDayNight(level, pos, state);
    }
}
