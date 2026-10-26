package top.dext.centurybridge.step.v1_21_6.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import top.dext.centurybridge.step.v1_21_6.rt.CbScissor;
import top.dext.centurybridge.step.v1_21_6.rt.CbVanillaReads;

/**
 * GuiGraphics.ScissorStack (class_332$class_8214) for a converted 1.21.5 mod's RenderSystem.enableScissor /
 * disableScissor (rt/CbScissor has the javap of both releases): GuiGraphics' elements record peek() (method_70863) as
 * their scissor, so while such a call stands, peek answers its rectangle (or no scissor after disableScissor), as
 * 1.21.5's GL scissor stood for GuiGraphics' draws. GuiGraphics' own enableScissor / disableScissor end it
 * (CbGuiScissorGraphicsMixin). peek runs whole, native 1.21.6 mixins on it included; with no converted call it answers
 * what 1.21.6 answers. Priority below every mod's.
 */
@net.fabricmc.api.Environment(net.fabricmc.api.EnvType.CLIENT)
@Mixin(targets = "net.minecraft.class_332$class_8214", priority = CbVanillaReads.PRIORITY)
public abstract class CbGuiScissorMixin {

    @Inject(method = "method_70863()Lnet/minecraft/class_8030;", at = @At("RETURN"), cancellable = true)
    private void cb$convertedScissor(CallbackInfoReturnable<net.minecraft.class_8030> cir) {
        if (CbScissor.guiOverride()) {
            cir.setReturnValue(CbScissor.guiRect());
        }
    }
}
