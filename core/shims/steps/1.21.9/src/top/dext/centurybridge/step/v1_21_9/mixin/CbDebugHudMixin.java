package top.dext.centurybridge.step.v1_21_9.mixin;

import net.minecraft.class_329;
import net.minecraft.class_332;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import top.dext.centurybridge.step.v1_21_9.rt.CbHud;

/**
 * Gui.renderDebugOverlay (1.21.9: the F3 overlay, drawn from GameRenderer and the debug options screen) draws the
 * restored VanillaHudElements.DEBUG root (rt/CbHud) once a converted mod attached an element to it or removed or
 * replaced it: the root's vanilla layer calls renderDebugOverlay again and runs it whole. Otherwise this returns at
 * once and the overlay is 1.21.9's.
 */
@Mixin(class_329.class)
public abstract class CbDebugHudMixin {

    @Inject(method = "method_70838(Lnet/minecraft/class_332;)V", at = @At("HEAD"), cancellable = true)
    private void cb$debugRoot(class_332 graphics, CallbackInfo ci) {
        if (CbHud.cbDrawRoot((class_329) (Object) this, graphics)) {
            ci.cancel();
        }
    }
}
