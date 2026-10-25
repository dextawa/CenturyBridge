package cb.smx4c.mixin;

import cb.smx4c.Counters;
import net.minecraft.class_339;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

/** 1.21.3's AbstractWidget.clicked(x, y): false for the one button the driver names, so it ignores clicks. */
@Mixin(class_339.class)
public abstract class WidgetClickedMixin {
    @Inject(method = "method_25361", at = @At("HEAD"), cancellable = true)
    private void cb$clicked(double x, double y, CallbackInfoReturnable<Boolean> cir) {
        if ((Object) this == Counters.blockedButton) {
            cir.setReturnValue(false);
        }
    }
}
