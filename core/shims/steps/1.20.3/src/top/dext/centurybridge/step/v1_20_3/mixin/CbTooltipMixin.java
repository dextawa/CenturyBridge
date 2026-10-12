package top.dext.centurybridge.step.v1_20_3.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import top.dext.centurybridge.step.v1_20_3.rt.CbGui;

/**
 * Tooltip.createTooltipPositioner, 1.20.3's replacement for the widget's own: while a widget that
 * overrides 1.20.2's createTooltipPositioner refreshes its tooltip, its answer places the tooltip.
 */
@Mixin(net.minecraft.class_7919.class)
public abstract class CbTooltipMixin {

    @Inject(method = "method_54385", at = @At("HEAD"), cancellable = true)
    private void cb$widgetPositioner(boolean hovered, boolean focused, net.minecraft.class_8030 area,
                                     CallbackInfoReturnable<net.minecraft.class_8000> cir) {
        if (CbGui.cbTooltipWidget() instanceof CbGui.CbLegacyTooltipPositioner widget) {
            cir.setReturnValue(widget.method_47937());
        }
    }
}
