package top.dext.centurybridge.step.v26_1.mixin;

import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.network.chat.Style;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;
import org.spongepowered.asm.mixin.gen.Invoker;

/**
 * GuiGraphicsExtractor's mouse position (the one 1.21.11's GuiGraphics kept as mouseX / mouseY and
 * its renderDeferredElements() read) and its hover-effect method (1.21.11's public
 * renderComponentHoverEffect, private componentHoverEffect in 26.1), for CbWallGui.
 */
@Mixin(GuiGraphicsExtractor.class)
public interface CbWallGuiAccess {

    @Accessor("mouseX")
    int cb$mouseX();

    @Accessor("mouseY")
    int cb$mouseY();

    @Invoker("componentHoverEffect")
    void cb$componentHoverEffect(Font font, Style style, int mouseX, int mouseY);
}
