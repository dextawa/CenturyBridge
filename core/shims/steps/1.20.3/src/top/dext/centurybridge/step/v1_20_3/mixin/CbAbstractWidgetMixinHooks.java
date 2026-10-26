package top.dext.centurybridge.step.v1_20_3.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import top.dext.centurybridge.step.v1_20_3.rt.CbGui;

/**
 * The injectors into 1.20.3's own AbstractWidget.render that go with {@link CbAbstractWidgetMixin} (its restored 1.20.2
 * createTooltipPositioner stays there, at {@code CbVanillaReads.PRIORITY}): the final render hands the call to a 1.20.2
 * widget's own render, and the tooltip is refreshed with the widget's 1.20.2 positioner.
 */
@Mixin(net.minecraft.class_339.class)
public abstract class CbAbstractWidgetMixinHooks {

    @Inject(method = "method_25394", at = @At("HEAD"), cancellable = true)
    private void cb$legacyRender(net.minecraft.class_332 graphics, int mouseX, int mouseY, float partialTick, CallbackInfo ci) {
        if ((Object) this instanceof CbGui.CbLegacyWidgetRender legacy && CbGui.cbRenderEnter(this)) {
            try {
                legacy.cb$render(graphics, mouseX, mouseY, partialTick);
            } finally {
                CbGui.cbRenderExit(this);
            }
            ci.cancel();
        }
    }

    @Redirect(method = "method_25394", at = @At(value = "INVOKE",
        target = "Lnet/minecraft/class_7919;method_54384(ZZLnet/minecraft/class_8030;)V"))
    private void cb$refreshTooltip(net.minecraft.class_7919 tooltip, boolean hovered, boolean focused, net.minecraft.class_8030 area) {
        CbGui.cbRefreshTooltip(tooltip, this, hovered, focused, area);
    }
}
