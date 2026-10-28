package cb.s9rn.mixin;

import cb.s9rn.Driver;
import net.minecraft.class_11909;
import net.minecraft.class_339;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

/** a native 1.21.9 mixin on AbstractWidget.mouseClicked(MouseButtonEvent, doubleClick), where the layer's click route hooks */
@Mixin(class_339.class)
public abstract class WidgetMouseClickedMixin {
    @Inject(method = "method_25402(Lnet/minecraft/class_11909;Z)Z", at = @At("HEAD"))
    private void cbs9rn$click(class_11909 event, boolean doubleClick, CallbackInfoReturnable<Boolean> cir) {
        Driver.NATIVE_CLICKS.merge(((class_339) (Object) this).method_25369().getString(), 1, Integer::sum);
    }
}
