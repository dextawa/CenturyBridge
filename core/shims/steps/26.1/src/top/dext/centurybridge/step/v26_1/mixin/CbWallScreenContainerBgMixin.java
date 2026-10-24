package top.dext.centurybridge.step.v26_1.mixin;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.screens.Screen;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import top.dext.centurybridge.step.v26_1.rt.CbWallContainerBackground;

/**
 * A 1.21.11 container screen extending a 26.1 one that draws its texture in extractBackground: its renderBg runs
 * right after the whole background (rt/CbWallContainerBackground).
 */
@Environment(EnvType.CLIENT)
@Mixin(Screen.class)
public abstract class CbWallScreenContainerBgMixin {

    @Inject(method = "extractRenderStateWithTooltipAndSubtitles", at = @At(value = "INVOKE",
            target = "Lnet/minecraft/client/gui/screens/Screen;extractBackground(Lnet/minecraft/client/gui/GuiGraphicsExtractor;IIF)V",
            shift = At.Shift.AFTER))
    private void cb$legacyRenderBg(GuiGraphicsExtractor graphics, int mouseX, int mouseY, float partialTick, CallbackInfo ci) {
        if (this instanceof CbWallContainerBackground.Screen screen && CbWallContainerBackground.overridesRenderBg(this)
                && CbWallContainerBackground.vanillaBackgroundBelow(this)) {
            screen.cb$renderBg(graphics, partialTick, mouseX, mouseY);
        }
    }
}
