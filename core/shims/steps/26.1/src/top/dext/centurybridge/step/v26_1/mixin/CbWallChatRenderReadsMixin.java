package top.dext.centurybridge.step.v26_1.mixin;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.gui.ActiveTextCollector;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.components.ChatComponent;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import top.dext.centurybridge.step.v26_1.rt.CbWallLegacyCalls;
import top.dext.centurybridge.step.v26_1.rt.CbWallLegacySplit;

/**
 * A 1.21.11 ChatComponent subclass overriding render(.., focused, ..) or captureClickableText(.., focused) (restored by
 * CbWallChatRenderMixin) gets 26.1's extractRenderState / captureClickableText calls, the mode's foreground flag as its
 * focused flag; a chat without such an override runs 26.1's bodies untouched. Default priority: injectors into 26.1
 * methods.
 */
@Environment(EnvType.CLIENT)
@Mixin(ChatComponent.class)
public abstract class CbWallChatRenderReadsMixin {

    @Inject(method = "extractRenderState(Lnet/minecraft/client/gui/GuiGraphicsExtractor;Lnet/minecraft/client/gui/Font;IIILnet/minecraft/client/gui/components/ChatComponent$DisplayMode;Z)V",
            at = @At("HEAD"), cancellable = true)
    private void cb$legacyRender(GuiGraphicsExtractor graphics, Font font, int tickCount, int mouseX, int mouseY,
                                 ChatComponent.DisplayMode mode, boolean insertionClicks, CallbackInfo ci) {
        if (CbWallLegacySplit.legacy(this, ChatComponent.class, "render", GuiGraphicsExtractor.class, Font.class,
                int.class, int.class, int.class, boolean.class, boolean.class)) {
            ((CbWallLegacyCalls.ChatLegacy) (Object) this).render(graphics, font, tickCount, mouseX, mouseY, mode.foreground, insertionClicks);
            ci.cancel();
        }
    }

    @Inject(method = "captureClickableText(Lnet/minecraft/client/gui/ActiveTextCollector;IILnet/minecraft/client/gui/components/ChatComponent$DisplayMode;)V",
            at = @At("HEAD"), cancellable = true)
    private void cb$legacyCapture(ActiveTextCollector collector, int height, int tickCount, ChatComponent.DisplayMode mode,
                                  CallbackInfo ci) {
        if (CbWallLegacySplit.legacy(this, ChatComponent.class, "captureClickableText", ActiveTextCollector.class,
                int.class, int.class, boolean.class)) {
            ((CbWallLegacyCalls.ChatLegacy) (Object) this).captureClickableText(collector, height, tickCount, mode.foreground);
            ci.cancel();
        }
    }
}
