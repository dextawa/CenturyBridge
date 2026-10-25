package top.dext.centurybridge.shims.v1_20_2;

import org.spongepowered.asm.mixin.Mixin;
import top.dext.centurybridge.rt.v1_20_2.CbLegacyRenderTexture;

/**
 * AbstractWidget.renderTexture, removed in 1.20.2 with the texture-sheet widgets: the 1.20.1 body
 * (row v while active, v + vOffset while hovered or focused, v + 2 * vOffset while inactive, drawn
 * with the depth test on), over the 1.20.2 GuiGraphics blit that 1.20.1 called. The legacy image
 * buttons call it through {@link CbLegacyRenderTexture}, as 1.20.1's renderWidget did.
 */
@Mixin(value = net.minecraft.class_339.class, priority = top.dext.centurybridge.rt.v1_20_2.CbVanillaReads.PRIORITY)
public abstract class CbGuiAbstractWidgetBridge implements CbLegacyRenderTexture {

    @Override
    public void method_48588(net.minecraft.class_332 graphics, net.minecraft.class_2960 texture, int x, int y, int u, int v,
                             int vOffset, int width, int height, int textureWidth, int textureHeight) {
        net.minecraft.class_339 self = (net.minecraft.class_339) (Object) this;
        int row = v;
        if (!self.method_37303()) {
            row = v + vOffset * 2;
        } else if (self.method_25367()) {
            row = v + vOffset;
        }
        com.mojang.blaze3d.systems.RenderSystem.enableDepthTest();
        graphics.method_25290(texture, x, y, (float) u, (float) row, width, height, textureWidth, textureHeight);
    }
}
