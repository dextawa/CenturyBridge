package top.dext.centurybridge.rt.v1_20_2;

/**
 * 1.20.1 AbstractWidget.renderTexture(GuiGraphics, texture, x, y, u, v, vOffset, width, height,
 * textureWidth, textureHeight), which 1.20.2 removed; CbGuiAbstractWidgetBridge puts it back on every
 * widget and declares this interface there, so bridge code (the legacy ImageButton / TextAndImageButton
 * rendering) calls it virtually, as 1.20.1's renderWidget did -- a mod widget's override is what runs.
 */
@net.fabricmc.api.Environment(net.fabricmc.api.EnvType.CLIENT)
public interface CbLegacyRenderTexture {

    void method_48588(net.minecraft.class_332 graphics, net.minecraft.class_2960 texture, int x, int y, int u, int v,
                      int vOffset, int width, int height, int textureWidth, int textureHeight);
}
