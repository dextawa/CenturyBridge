package top.dext.centurybridge.step.v1_21_6.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import top.dext.centurybridge.step.v1_21_6.rt.CbScissor;
import top.dext.centurybridge.step.v1_21_6.rt.CbVanillaReads;

/**
 * GuiGraphics.enableScissor (method_44379) / disableScissor (method_44380) end a converted 1.21.5 mod's RenderSystem
 * scissor for the frame's GUI elements (rt/CbScissor, CbGuiScissorMixin): 1.21.5's pushed / popped the stack and
 * applied its top (applyScissor), replacing whatever RenderSystem scissor stood; 1.21.6's push / pop the same stack
 * (javap; the only callers of ScissorStack.push / pop), whose top peek answers again. Additive at HEAD: both run whole.
 */
@net.fabricmc.api.Environment(net.fabricmc.api.EnvType.CLIENT)
@Mixin(value = net.minecraft.class_332.class, priority = CbVanillaReads.PRIORITY)
public abstract class CbGuiScissorGraphicsMixin {

    @Inject(method = "method_44379(IIII)V", at = @At("HEAD"))
    private void cb$enableScissor(int x0, int y0, int x1, int y1, CallbackInfo ci) {
        CbScissor.guiStackChanged();
    }

    @Inject(method = "method_44380()V", at = @At("HEAD"))
    private void cb$disableScissor(CallbackInfo ci) {
        CbScissor.guiStackChanged();
    }
}
