package top.dext.centurybridge.shims.v26_2;

import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
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

    @Shadow @Final private java.util.List<net.minecraft.client.gui.components.Renderable> renderables;

    /**
     * 1.20.1 Screen.render drew the widgets and nothing else -- the mod called
     * renderBackground itself first. 26.2's extractRenderState draws the
     * background too, and the blur may only run once per frame, so a
     * super.render(context, ..) from an old override renders exactly what it
     * used to: the widget list.
     */
    public void render(GuiGraphics context, int mouseX, int mouseY, float delta) {
        for (net.minecraft.client.gui.components.Renderable r : renderables) {
            r.extractRenderState(context.cbExtractor(), mouseX, mouseY, delta);
        }
    }

    public void renderBackground(GuiGraphics context, int mouseX, int mouseY, float delta) {
        cbBackground(context, mouseX, mouseY, delta);
    }

    /** 1.20.1 renderBackground(DrawContext): the mouse/delta parameters arrived at 1.20.2 */
    public void renderBackground(GuiGraphics context) {
        cbBackground(context, 0, 0, 0.0f);
    }

    /**
     * 26.2 allows one background blur per frame and vanilla may already have
     * spent it before an old override calls renderBackground itself; a second
     * request is simply the same background, so it is idempotent here.
     */
    private void cbBackground(GuiGraphics context, int mouseX, int mouseY, float delta) {
        try {
            ((net.minecraft.client.gui.screens.Screen) (Object) this).extractBackground(context.cbExtractor(), mouseX, mouseY, delta);
        } catch (IllegalStateException alreadyBlurred) {
            // background already extracted this frame
        }
    }
}
