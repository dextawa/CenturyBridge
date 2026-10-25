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
 * The injectors into 1.20.2's own methods that go with {@link CbGuiImageWidgetBridge} (its restored 1.20.1 members
 * stay there, at {@code CbVanillaReads.PRIORITY}, so a mod's 1.20.1 injectors can anchor inside them).
 */
@Mixin(net.minecraft.class_8208.class)
public abstract class CbGuiImageWidgetBridgeHooks extends net.minecraft.class_339 implements CbLegacyImageWidget {

    private CbGuiImageWidgetBridgeHooks() {
        super(0, 0, 0, 0, null);
    }

    @Unique
    private net.minecraft.class_2960 cb$texture;

    @Override
    public net.minecraft.class_2960 cb$legacyTexture() {
        return this.cb$texture;
    }

    @Inject(method = "<init>(IIII)V", at = @At("RETURN"))
    private void cb$takeTexture(int x, int y, int width, int height, CallbackInfo ci) {
        this.cb$texture = CbGuiImageWidgets.cbTakePending();
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
