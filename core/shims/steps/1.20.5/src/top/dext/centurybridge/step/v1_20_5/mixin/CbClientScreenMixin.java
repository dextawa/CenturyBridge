package top.dext.centurybridge.step.v1_20_5.mixin;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import top.dext.centurybridge.step.v1_20_5.rt.CbClientGui;

/**
 * Screen.renderDirtBackground(graphics): 1.20.4's renderBackground drew it when there was no level (and the
 * transparent background over a level). 1.20.5 dropped it for the panorama, blur and menu background. A
 * 1.20.4 screen class's own renderDirtBackground (rt/CbClientGui.CbLegacyDirtBackground) is still what its
 * background is without a level, as 1.20.4 drew it.
 */
@Environment(EnvType.CLIENT)
@Mixin(net.minecraft.class_437.class)
public abstract class CbClientScreenMixin {

    @Shadow
    protected net.minecraft.class_310 field_22787;

    @Inject(method = "method_25420", at = @At("HEAD"), cancellable = true)
    private void cb$legacyDirtBackground(net.minecraft.class_332 graphics, int mouseX, int mouseY, float partialTick, CallbackInfo ci) {
        if (this instanceof CbClientGui.CbLegacyDirtBackground legacy && this.field_22787.field_1687 == null) {
            ci.cancel();
            legacy.method_25434(graphics);
        }
    }
}
