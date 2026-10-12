package top.dext.centurybridge.step.v1_20_3.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import top.dext.centurybridge.step.v1_20_3.rt.CbGui;

/**
 * AbstractWidget's 1.20.2 contract on 1.20.3:
 * <ul>
 * <li>render(GuiGraphics, int, int, float) is final since 1.20.3. A 1.20.2 widget overriding it has its
 *     override renamed (overrideRenames) and marked with {@link CbGui.CbLegacyWidgetRender}; the final
 *     render hands the call to it, as vanilla called the override in 1.20.2. The widget's own
 *     super.render(..) comes back here and runs vanilla's body (the guard).</li>
 * <li>createTooltipPositioner() (protected in 1.20.2) is gone: 1.20.3 lets the Tooltip choose. It is
 *     given back with 1.20.2's choice, and a widget that overrides it (marked with
 *     {@link CbGui.CbLegacyTooltipPositioner}) has its tooltip placed by its own answer again.</li>
 * </ul>
 */
@Mixin(net.minecraft.class_339.class)
public abstract class CbAbstractWidgetMixin {

    @Inject(method = "method_25394", at = @At("HEAD"), cancellable = true)
    private void cb$legacyRender(net.minecraft.class_332 graphics, int mouseX, int mouseY, float partialTick, CallbackInfo ci) {
        if (this instanceof CbGui.CbLegacyWidgetRender legacy && CbGui.cbRenderEnter(this)) {
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

    /** was createTooltipPositioner() (method_47937): below or above the widget when focused by keyboard, else by the mouse */
    protected net.minecraft.class_8000 method_47937() {
        return CbGui.cbTooltipPositioner((net.minecraft.class_339) (Object) this);
    }
}
