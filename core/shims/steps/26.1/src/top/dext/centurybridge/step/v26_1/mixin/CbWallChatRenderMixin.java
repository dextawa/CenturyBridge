package top.dext.centurybridge.step.v26_1.mixin;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.gui.ActiveTextCollector;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.components.ChatComponent;
import org.spongepowered.asm.mixin.Mixin;
import top.dext.centurybridge.step.v26_1.rt.CbVanillaReads;
import top.dext.centurybridge.step.v26_1.rt.CbWallLegacyCalls;
import top.dext.centurybridge.step.v26_1.rt.CbWallLegacySplit;

/**
 * 1.21.11's ChatComponent.render(graphics, font, tickCount, mouseX, mouseY, focused, insertionClicks) and
 * captureClickableText(collector, height, tickCount, focused). 26.1 replaced the focused flag by a DisplayMode:
 * BACKGROUND (the HUD's chat, 1.21.11's unfocused), FOREGROUND (the chat screen, 1.21.11's focused) and
 * FOREGROUND_RESTRICTED (the chat screen with 26.1's chat-restriction prompt, which 1.21.11 did not have). A 1.21.11
 * call maps its flag to BACKGROUND / FOREGROUND. A 1.21.11 subclass that overrides either method gets 26.1's calls
 * (mixin/CbWallChatRenderReadsMixin, rt/CbWallLegacySplit): the mode's foreground flag is its focused flag, and its
 * super call runs vanilla's.
 */
@Environment(EnvType.CLIENT)
@Mixin(value = ChatComponent.class, priority = CbVanillaReads.PRIORITY)
public abstract class CbWallChatRenderMixin implements CbWallLegacyCalls.ChatLegacy {

    private static ChatComponent.DisplayMode cb$mode(boolean focused) {
        return focused ? ChatComponent.DisplayMode.FOREGROUND : ChatComponent.DisplayMode.BACKGROUND;
    }

    @Override
    public void render(GuiGraphicsExtractor graphics, Font font, int tickCount, int mouseX, int mouseY, boolean focused,
                       boolean insertionClicks) {
        CbWallLegacySplit.enter(this);
        try {
            ((ChatComponent) (Object) this).extractRenderState(graphics, font, tickCount, mouseX, mouseY, cb$mode(focused), insertionClicks);
        } finally {
            CbWallLegacySplit.exit(this);
        }
    }

    @Override
    public void captureClickableText(ActiveTextCollector collector, int height, int tickCount, boolean focused) {
        CbWallLegacySplit.enter(this);
        try {
            ((ChatComponent) (Object) this).captureClickableText(collector, height, tickCount, cb$mode(focused));
        } finally {
            CbWallLegacySplit.exit(this);
        }
    }
}
