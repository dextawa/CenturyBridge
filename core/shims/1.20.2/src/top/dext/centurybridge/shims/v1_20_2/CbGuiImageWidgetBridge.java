package top.dext.centurybridge.shims.v1_20_2;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import top.dext.centurybridge.rt.v1_20_2.CbGuiImageWidgets;
import top.dext.centurybridge.rt.v1_20_2.CbLegacyImageWidget;

/**
 * An ImageWidget made through the 1.20.1 API (see CbGuiImageWidgets) keeps the texture 1.20.1 gave
 * it and behaves as 1.20.1's ImageWidget did: a plain AbstractWidget that draws the whole texture --
 * active while visible, clicking plays the button sound, keyboard focus reaches it. 1.20.2's ImageWidget
 * is inactive, silent and unfocusable; widgets made through the 1.20.2 API never carry a 1.20.1 texture
 * and are untouched. The 1.20.1 renderWidget is also what a mod subclass of the now-abstract ImageWidget
 * inherits.
 */
@Mixin(value = net.minecraft.class_8208.class, priority = top.dext.centurybridge.rt.v1_20_2.CbVanillaReads.PRIORITY)
public abstract class CbGuiImageWidgetBridge extends net.minecraft.class_339 {

    private CbGuiImageWidgetBridge() {
        super(0, 0, 0, 0, null);
    }

    /** 1.20.1 ImageWidget.renderWidget (public, as in 1.20.1), inherited by a mod subclass (the 1.20.2
     *  variants override it) */
    @Override
    public void method_48579(net.minecraft.class_332 graphics, int mouseX, int mouseY, float delta) {
        net.minecraft.class_2960 texture = ((CbLegacyImageWidget) (Object) this).cb$legacyTexture();
        if (texture != null) {
            CbGuiImageWidgets.cbRender(this, texture, graphics);
        }
    }
}
