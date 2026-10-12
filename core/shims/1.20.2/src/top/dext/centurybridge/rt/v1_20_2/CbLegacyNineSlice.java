package top.dext.centurybridge.rt.v1_20_2;

/**
 * 1.20.1 GuiGraphics.blitNineSliced(texture, x, y, width, height, left, top, right, bottom, uWidth, vHeight,
 * u, v), restored on GuiGraphics by CbGuiGraphicsBridge, which declares this interface there: bridge code
 * that re-runs a 1.20.1 drawing path calls it on the GuiGraphics as 1.20.1 did, virtually.
 */
@net.fabricmc.api.Environment(net.fabricmc.api.EnvType.CLIENT)
public interface CbLegacyNineSlice {

    void method_48587(net.minecraft.class_2960 texture, int x, int y, int width, int height, int left, int top, int right,
                      int bottom, int uWidth, int vHeight, int u, int v);
}
