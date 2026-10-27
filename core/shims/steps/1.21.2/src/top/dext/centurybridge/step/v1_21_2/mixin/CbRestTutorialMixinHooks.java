package top.dext.centurybridge.step.v1_21_2.mixin;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.class_1156;
import net.minecraft.class_372;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import top.dext.centurybridge.step.v1_21_2.rt.CbPrioritySplitClient;

/**
 * The injector into 1.21.2's own Tutorial.tick that goes with {@link CbRestTutorialMixin} (its restored 1.21.1 members
 * stay there, at {@code CbVanillaReads.PRIORITY}, so a mod's 1.21.1 injectors can anchor inside them): each timed toast's
 * progress fills over its ticks and it hides when they are over, first in the tick as 1.21.1's did.
 */
@Environment(EnvType.CLIENT)
@Mixin(class_1156.class)
public abstract class CbRestTutorialMixinHooks {

    @Inject(method = "method_4917", at = @At("HEAD"))
    private void cb$tickTimedToasts(CallbackInfo ci) {
        ((CbPrioritySplitClient.TutorialToasts) (Object) this).cb$v1_21_2$timedToasts().removeIf(t -> {
            class_372 toast = (class_372) t[0];
            int duration = (Integer) t[1];
            int[] timer = (int[]) t[2];
            timer[0]++;
            toast.method_1992(Math.min((float) timer[0] / (float) duration, 1.0F));
            if (timer[0] > duration) {
                toast.method_1993();
                return true;
            }
            return false;
        });
    }
}
