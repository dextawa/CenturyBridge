package top.dext.centurybridge.step.v1_20_5.mixin;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

/**
 * AbstractSelectionList.setRenderBackground(boolean): 1.20.4's list drew its background (the darkened dirt
 * under the entries, and the top/bottom shadows) unless told not to. 1.20.5 always draws its own backdrop and
 * separators (renderListBackground / renderListSeparators); a list 1.20.4 code switched off skips them, as
 * 1.20.5's own list without a background does (SocialInteractionsPlayerList overrides both to nothing).
 */
@Environment(EnvType.CLIENT)
@Mixin(net.minecraft.class_350.class)
public abstract class CbClientSelectionListMixin {

    /** 1.20.4's renderBackground field, true by default */
    @Unique
    private boolean cb$renderBackground = true;

    /** 1.20.4 AbstractSelectionList.setRenderBackground(renderBackground) */
    public void method_31322(boolean renderBackground) {
        this.cb$renderBackground = renderBackground;
    }

    @Inject(method = "method_57715", at = @At("HEAD"), cancellable = true)
    private void cb$listBackground(net.minecraft.class_332 graphics, CallbackInfo ci) {
        if (!this.cb$renderBackground) {
            ci.cancel();
        }
    }

    @Inject(method = "method_57713", at = @At("HEAD"), cancellable = true)
    private void cb$listSeparators(net.minecraft.class_332 graphics, CallbackInfo ci) {
        if (!this.cb$renderBackground) {
            ci.cancel();
        }
    }
}
