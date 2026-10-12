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
@Mixin(net.minecraft.class_8208.class)
public abstract class CbGuiImageWidgetBridge extends net.minecraft.class_339 implements CbLegacyImageWidget {

    @Unique
    private net.minecraft.class_2960 cb$texture;

    private CbGuiImageWidgetBridge() {
        super(0, 0, 0, 0, null);
    }

    @Override
    public net.minecraft.class_2960 cb$legacyTexture() {
        return this.cb$texture;
    }

    @Inject(method = "<init>(IIII)V", at = @At("RETURN"))
    private void cb$takeTexture(int x, int y, int width, int height, CallbackInfo ci) {
        this.cb$texture = CbGuiImageWidgets.cbTakePending();
    }

    /** 1.20.1 ImageWidget.renderWidget (public, as in 1.20.1), inherited by a mod subclass (the 1.20.2
     *  variants override it) */
    @Override
    public void method_48579(net.minecraft.class_332 graphics, int mouseX, int mouseY, float delta) {
        if (this.cb$texture != null) {
            CbGuiImageWidgets.cbRender(this, this.cb$texture, graphics);
        }
    }

    /** AbstractWidget.isActive, as 1.20.1's ImageWidget inherited it */
    @Inject(method = "method_37303", at = @At("HEAD"), cancellable = true)
    private void cb$active(CallbackInfoReturnable<Boolean> cir) {
        if (this.cb$texture != null) {
            cir.setReturnValue(this.field_22764 && this.field_22763);
        }
    }

    /** AbstractWidget.playDownSound, as 1.20.1's ImageWidget inherited it */
    @Inject(method = "method_25354", at = @At("HEAD"), cancellable = true)
    private void cb$clickSound(net.minecraft.class_1144 sounds, CallbackInfo ci) {
        if (this.cb$texture != null) {
            sounds.method_4873(net.minecraft.class_1109.method_47978(net.minecraft.class_3417.field_15015, 1.0F));
            ci.cancel();
        }
    }

    /** AbstractWidget.nextFocusPath, as 1.20.1's ImageWidget inherited it */
    @Inject(method = "method_48205", at = @At("HEAD"), cancellable = true)
    private void cb$focusable(net.minecraft.class_8023 navigation, CallbackInfoReturnable<net.minecraft.class_8016> cir) {
        if (this.cb$texture != null) {
            cir.setReturnValue(!this.field_22763 || !this.field_22764 ? null
                : !this.method_25370() ? net.minecraft.class_8016.method_48193(this) : null);
        }
    }
}
