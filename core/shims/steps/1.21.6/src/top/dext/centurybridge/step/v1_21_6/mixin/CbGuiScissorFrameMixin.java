package top.dext.centurybridge.step.v1_21_6.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import top.dext.centurybridge.step.v1_21_6.rt.CbScissor;
import top.dext.centurybridge.step.v1_21_6.rt.CbVanillaReads;

/**
 * The start of the frame's GUI build for a converted 1.21.5 mod's RenderSystem scissor (rt/CbScissor): 1.21.6's
 * GameRenderer.render (method_3192) resets the frame's GuiRenderState (method_70926), builds a GuiGraphics over it,
 * draws the HUD, the screen, the overlay and the toasts into it, then hands it to GuiRenderer.render (javap). Right
 * after the reset the GUI's scissor is 1.21.6's own again: a RenderSystem scissor a converted mod set while the world
 * drew stays with the RenderType draws. Additive: render runs whole.
 */
@net.fabricmc.api.Environment(net.fabricmc.api.EnvType.CLIENT)
@Mixin(value = net.minecraft.class_757.class, priority = CbVanillaReads.PRIORITY)
public abstract class CbGuiScissorFrameMixin {

    @Inject(method = "method_3192(Lnet/minecraft/class_9779;Z)V",
        at = @At(value = "INVOKE", target = "Lnet/minecraft/class_11246;method_70926()V", shift = At.Shift.AFTER))
    private void cb$guiFrameStart(net.minecraft.class_9779 tracker, boolean tick, CallbackInfo ci) {
        CbScissor.guiFrameStart();
    }
}
