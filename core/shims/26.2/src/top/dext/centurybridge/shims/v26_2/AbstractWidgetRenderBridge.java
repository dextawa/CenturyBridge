package top.dext.centurybridge.shims.v26_2;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import top.dext.centurybridge.facade.v26_2.GuiGraphics;

/** super.renderWidget(context, ..) from a 1.20.x widget override -> the 26.2 extract method. */
@Mixin(net.minecraft.client.gui.components.AbstractWidget.class)
public abstract class AbstractWidgetRenderBridge {

    @Shadow
    protected abstract void extractWidgetRenderState(net.minecraft.client.gui.GuiGraphicsExtractor ext, int mouseX, int mouseY, float delta);

    protected void renderWidget(GuiGraphics context, int mouseX, int mouseY, float delta) {
        this.extractWidgetRenderState(context.cbExtractor(), mouseX, mouseY, delta);
    }
}
