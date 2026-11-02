package top.dext.centurybridge.step.v1_21_11.mixin;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.class_332;
import net.minecraft.class_4264;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import top.dext.centurybridge.step.v1_21_11.rt.CbTextDrawing;

/**
 * AbstractButton.renderWidget(graphics, mouseX, mouseY, partialTick) (method_48579) is final in 1.21.11 (protected, not
 * final, in 1.21.10, and overridden public by SpriteIconButton's, ImageButton's, Checkbox's ... subclasses; javap both).
 * A 1.21.10 button overriding it has the override renamed cb$renderWidget (overrideRenames, adapter
 * CbTextDrawing.LegacyRenderWidget); the final renderWidget hands the call to it, as 1.21.10's AbstractWidget.render
 * called the override. The button's own super.renderWidget(..) comes back here as a re-entry and runs 1.21.11's body
 * (renderContents, then the focus outline). Default priority: native 1.21.11 injectors at the head keep their order.
 */
@Environment(EnvType.CLIENT)
@Mixin(class_4264.class)
public abstract class CbAbstractButtonRenderWidgetMixinHooks {

    @Inject(method = "method_48579", at = @At("HEAD"), cancellable = true)
    private void cb$v1_21_11$legacyRenderWidget(class_332 graphics, int mouseX, int mouseY, float partialTick, CallbackInfo ci) {
        if ((Object) this instanceof CbTextDrawing.LegacyRenderWidget legacy && CbTextDrawing.renderWidgetEnter(this)) {
            try {
                legacy.cb$renderWidget(graphics, mouseX, mouseY, partialTick);
            } finally {
                CbTextDrawing.renderWidgetExit(this);
            }
            ci.cancel();
        }
    }
}
