package cb.smx4n.mixin;

import cb.smx4n.Driver;
import net.minecraft.class_339;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

/** Native 1.21.4: isMouseOver RETURNs counted while an AbstractWidget.mouseClicked runs (one per mouseClicked). */
@Mixin(class_339.class)
public abstract class WidgetMouseMixin {
    @Inject(method = "method_25402", at = @At("HEAD"))
    private void cb$clickHead(double x, double y, int button, CallbackInfoReturnable<Boolean> cir) {
        Driver.MC_CALLS.incrementAndGet();
        Driver.IN_CLICK.set(Driver.IN_CLICK.get() + 1);
    }

    @Inject(method = "method_25402", at = @At("RETURN"))
    private void cb$clickReturn(double x, double y, int button, CallbackInfoReturnable<Boolean> cir) {
        Driver.IN_CLICK.set(Driver.IN_CLICK.get() - 1);
    }

    @Inject(method = "method_25405", at = @At("RETURN"))
    private void cb$over(double x, double y, CallbackInfoReturnable<Boolean> cir) {
        if (Driver.IN_CLICK.get() > 0) {
            Driver.OVER_IN_CLICK.incrementAndGet();
        }
    }
}
