package top.dext.centurybridge.shims.v1_20_2;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import top.dext.centurybridge.rt.v1_20_2.CbGuiImageWidgets;
import top.dext.centurybridge.rt.v1_20_2.CbLegacyImageWidget;

/**
 * The texture variant of 1.20.2's ImageWidget, which `new ImageWidget(..)` with a 1.20.1 constructor
 * builds (CbGuiImageWidgets): when the widget carries its 1.20.1 texture it draws as 1.20.1 did -- the
 * whole file stretched to the widget's current size, where 1.20.2's variant keeps the size it was made
 * with. Other texture ImageWidgets draw as 1.20.2 does.
 */
@Mixin(targets = "net.minecraft.class_8208$class_8660")
public abstract class CbGuiImageWidgetTextureBridge {

    @Inject(method = "method_48579", at = @At("HEAD"), cancellable = true)
    private void cb$legacyTexture(net.minecraft.class_332 graphics, int mouseX, int mouseY, float delta, CallbackInfo ci) {
        net.minecraft.class_2960 texture = ((CbLegacyImageWidget) this).cb$legacyTexture();
        if (texture != null) {
            CbGuiImageWidgets.cbRender((net.minecraft.class_339) (Object) this, texture, graphics);
            ci.cancel();
        }
    }
}
