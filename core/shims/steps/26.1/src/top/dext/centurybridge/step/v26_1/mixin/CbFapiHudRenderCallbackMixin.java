package top.dext.centurybridge.step.v26_1.mixin;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.DeltaTracker;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import top.dext.centurybridge.step.v26_1.compat.fapi.client.rendering.v1.HudRenderCallback;

/** 1.21.11's HudRenderCallback, fired where 1.21.11's GuiMixin fired it: the tail of the HUD pass */
@Environment(EnvType.CLIENT)
@Mixin(Gui.class)
public abstract class CbFapiHudRenderCallbackMixin {

    @Inject(method = "extractRenderState", at = @At("TAIL"))
    private void cb$hudRenderCallback(GuiGraphicsExtractor graphics, DeltaTracker tickCounter, CallbackInfo ci) {
        HudRenderCallback.EVENT.invoker().onHudRender(graphics, tickCounter);
    }
}
