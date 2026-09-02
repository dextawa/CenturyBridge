package top.dext.centurybridge.compat.fapi;

import net.fabricmc.fabric.api.client.screen.v1.ScreenEvents;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.screens.Screen;
import top.dext.centurybridge.facade.v26_2.GuiGraphics;

/** was ScreenEvents.AfterRender (SAM afterRender(screen, DrawContext, mouseX, mouseY, tickDelta)): a real AfterForeground listener that wraps the extractor in the DrawContext facade */
@FunctionalInterface
public interface CbScreenAfterRender extends ScreenEvents.AfterForeground {

    void afterRender(Screen screen, GuiGraphics drawContext, int mouseX, int mouseY, float tickDelta);

    @Override
    default void afterForeground(Screen screen, GuiGraphicsExtractor extractor, int mouseX, int mouseY, float tickDelta) {
        afterRender(screen, new GuiGraphics(extractor), mouseX, mouseY, tickDelta);
    }
}
