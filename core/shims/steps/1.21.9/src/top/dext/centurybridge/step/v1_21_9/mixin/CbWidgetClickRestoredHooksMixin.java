package top.dext.centurybridge.step.v1_21_9.mixin;

import net.minecraft.class_11909;
import net.minecraft.class_339;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import top.dext.centurybridge.step.v1_21_9.rt.CbMixedMembers;
import top.dext.centurybridge.step.v1_21_9.rt.CbPrioritySplitClient;

/**
 * The route split off CbWidgetClickRestoredMixin (default priority, so it keeps 1.21.9's order among mods' injectors):
 * while a converted mod mixed 1.21.8's mouseClicked(x, y, button) (CbMixedMembers), 1.21.9's mouseClicked runs that
 * body in its place; unmixed, 1.21.9's mouseClicked runs untouched.
 */
@Mixin(class_339.class)
public abstract class CbWidgetClickRestoredHooksMixin {

    @Unique
    private static final String KEY = "net/minecraft/class_339.method_25402(DDI)Z";

    @Inject(method = "method_25402(Lnet/minecraft/class_11909;Z)Z", at = @At("HEAD"), cancellable = true)
    private void cb$routeClick(class_11909 event, boolean doubleClick, CallbackInfoReturnable<Boolean> cir) {
        if (CbMixedMembers.mixed(KEY)) {
            cir.setReturnValue(((CbPrioritySplitClient.WidgetClick) (Object) this).cb$v1_21_9$routeClick(event, doubleClick));
        }
    }
}
