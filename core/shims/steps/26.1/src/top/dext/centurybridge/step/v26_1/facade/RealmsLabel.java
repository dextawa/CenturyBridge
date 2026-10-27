package top.dext.centurybridge.step.v26_1.facade;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.components.Renderable;
import net.minecraft.network.chat.Component;

/**
 * 1.21.11's RealmsLabel (class_4903) on 26.1, which dropped the class (its Realms screens lay their labels out
 * as widgets). The bodies are 1.21.11's (javap class_4903): a renderable drawing its text centred at (x, y) in
 * the game font. Members keep their intermediary names (a subclass overrides and calls them; 26.1's
 * extractRenderState goes through render), public.
 */
@Environment(EnvType.CLIENT)
public class RealmsLabel implements Renderable {
    /** text */
    public final Component field_22819;
    /** x */
    public final int field_22820;
    /** y */
    public final int field_22821;
    /** color */
    public final int field_22822;

    public RealmsLabel(Component text, int x, int y, int color) {
        this.field_22819 = text;
        this.field_22820 = x;
        this.field_22821 = y;
        this.field_22822 = color;
    }

    @Override
    public void extractRenderState(GuiGraphicsExtractor graphics, int mouseX, int mouseY, float partialTick) {
        this.method_25394(graphics, mouseX, mouseY, partialTick);
    }

    /** render(graphics, mouseX, mouseY, partialTick) */
    public void method_25394(GuiGraphicsExtractor graphics, int mouseX, int mouseY, float partialTick) {
        graphics.centeredText(Minecraft.getInstance().font, this.field_22819, this.field_22820, this.field_22821, this.field_22822);
    }

    /** getText() */
    public Component method_25487() {
        return this.field_22819;
    }
}
