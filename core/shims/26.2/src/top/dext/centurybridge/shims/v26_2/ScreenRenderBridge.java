package top.dext.centurybridge.shims.v26_2;

import org.spongepowered.asm.mixin.Mixin;
import top.dext.centurybridge.facade.v26_2.GuiGraphics;

/**
 * 26.2 turned Screen.render / renderBackground into extractRenderState /
 * extractBackground over a GuiGraphicsExtractor. A 1.20.x override reaches
 * this class through the overrideHooks forwarder with the facade; when it
 * calls super.render(context, ..) it lands here, and we hand the extractor
 * back to the real method. The forwarder's per-object guard makes that
 * re-entry fall through to vanilla instead of recursing.
 */
@Mixin(net.minecraft.client.gui.screens.Screen.class)
public abstract class ScreenRenderBridge {

    public void render(GuiGraphics context, int mouseX, int mouseY, float delta) {
        ((net.minecraft.client.gui.screens.Screen) (Object) this).extractRenderState(context.cbExtractor(), mouseX, mouseY, delta);
    }

    public void renderBackground(GuiGraphics context, int mouseX, int mouseY, float delta) {
        ((net.minecraft.client.gui.screens.Screen) (Object) this).extractBackground(context.cbExtractor(), mouseX, mouseY, delta);
    }

    /** 1.20.1 renderBackground(DrawContext): the mouse/delta parameters arrived at 1.20.2 */
    public void renderBackground(GuiGraphics context) {
        ((net.minecraft.client.gui.screens.Screen) (Object) this).extractBackground(context.cbExtractor(), 0, 0, 0.0f);
    }
}
