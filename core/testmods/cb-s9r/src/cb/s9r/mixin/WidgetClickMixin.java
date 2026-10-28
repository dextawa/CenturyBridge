package cb.s9r.mixin;

import cb.s9r.Counters;
import net.minecraft.class_339;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

/** a 1.21.8 mod's mixin into AbstractWidget.mouseClicked(x, y, button): the "blocked" button never takes a click */
@Mixin(class_339.class)
public abstract class WidgetClickMixin {
    @Inject(method = "method_25402(DDI)Z", at = @At("HEAD"), cancellable = true)
    private void cbs9r$click(double x, double y, int button, CallbackInfoReturnable<Boolean> cir) {
        Counters.CLICK_MIXIN_CALLS.incrementAndGet();
        if ("blocked".equals(((class_339) (Object) this).method_25369().getString())) {
            cir.setReturnValue(false);
        }
    }
}
