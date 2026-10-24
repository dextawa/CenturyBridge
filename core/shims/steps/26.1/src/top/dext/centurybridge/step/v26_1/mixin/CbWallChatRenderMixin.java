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
import top.dext.centurybridge.step.v26_1.rt.CbWallLegacySplit;

/**
 * 1.21.11's ChatComponent.render(graphics, font, tickCount, mouseX, mouseY, focused, insertionClicks) and
 * captureClickableText(collector, height, tickCount, focused). 26.1 replaced the focused flag by a DisplayMode:
 * BACKGROUND (the HUD's chat, 1.21.11's unfocused), FOREGROUND (the chat screen, 1.21.11's focused) and
 * FOREGROUND_RESTRICTED (the chat screen with 26.1's chat-restriction prompt, which 1.21.11 did not have). A 1.21.11
 * call maps its flag to BACKGROUND / FOREGROUND. A 1.21.11 subclass that overrides either method gets 26.1's calls
 * (rt/CbWallLegacySplit): the mode's foreground flag is its focused flag, and its super call runs vanilla's.
 */
@Environment(EnvType.CLIENT)
@Mixin(ChatComponent.class)
public abstract class CbWallChatRenderMixin {

    private static ChatComponent.DisplayMode cb$mode(boolean focused) {
        return focused ? ChatComponent.DisplayMode.FOREGROUND : ChatComponent.DisplayMode.BACKGROUND;
    }

    public void render(GuiGraphicsExtractor graphics, Font font, int tickCount, int mouseX, int mouseY, boolean focused,
                       boolean insertionClicks) {
        CbWallLegacySplit.enter(this);
        try {
            ((ChatComponent) (Object) this).extractRenderState(graphics, font, tickCount, mouseX, mouseY, cb$mode(focused), insertionClicks);
        } finally {
            CbWallLegacySplit.exit(this);
        }
    }

    public void captureClickableText(ActiveTextCollector collector, int height, int tickCount, boolean focused) {
        CbWallLegacySplit.enter(this);
        try {
            ((ChatComponent) (Object) this).captureClickableText(collector, height, tickCount, cb$mode(focused));
        } finally {
            CbWallLegacySplit.exit(this);
        }
    }

    @Inject(method = "extractRenderState(Lnet/minecraft/client/gui/GuiGraphicsExtractor;Lnet/minecraft/client/gui/Font;IIILnet/minecraft/client/gui/components/ChatComponent$DisplayMode;Z)V",
            at = @At("HEAD"), cancellable = true)
    private void cb$legacyRender(GuiGraphicsExtractor graphics, Font font, int tickCount, int mouseX, int mouseY,
                                 ChatComponent.DisplayMode mode, boolean insertionClicks, CallbackInfo ci) {
        if (CbWallLegacySplit.legacy(this, ChatComponent.class, "render", GuiGraphicsExtractor.class, Font.class,
                int.class, int.class, int.class, boolean.class, boolean.class)) {
            render(graphics, font, tickCount, mouseX, mouseY, mode.foreground, insertionClicks);
            ci.cancel();
        }
    }

    @Inject(method = "captureClickableText(Lnet/minecraft/client/gui/ActiveTextCollector;IILnet/minecraft/client/gui/components/ChatComponent$DisplayMode;)V",
            at = @At("HEAD"), cancellable = true)
    private void cb$legacyCapture(ActiveTextCollector collector, int height, int tickCount, ChatComponent.DisplayMode mode,
                                  CallbackInfo ci) {
        if (CbWallLegacySplit.legacy(this, ChatComponent.class, "captureClickableText", ActiveTextCollector.class,
                int.class, int.class, boolean.class)) {
            captureClickableText(collector, height, tickCount, mode.foreground);
            ci.cancel();
        }
    }
}
