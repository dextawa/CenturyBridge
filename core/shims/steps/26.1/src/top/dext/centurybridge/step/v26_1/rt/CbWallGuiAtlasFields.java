package top.dext.centurybridge.step.v26_1.rt;

import com.mojang.blaze3d.textures.GpuTexture;
import com.mojang.blaze3d.textures.GpuTextureView;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.gui.render.GuiItemAtlas;
import net.minecraft.client.gui.render.GuiRenderer;

/**
 * 1.21.11 GuiRenderer item-atlas fields (class_11228, opened by access wideners) that 26.1 moved into GuiItemAtlas
 * (javap of both):
 * <ul>
 * <li>MAXIMUM_ITEM_ATLAS_SIZE (field_59910): 1.21.11's clinit stores RenderSystem.getDevice().getMaxTextureSize();
 *     26.1's GuiItemAtlas.MAXIMUM_TEXTURE_SIZE is the same expression in its clinit.</li>
 * <li>itemsAtlas / itemsAtlasView / itemsAtlasDepth / itemsAtlasDepthView (field_59919 / field_60571 / field_59920 /
 *     field_60570): null until the first item atlas is made in 1.21.11; 26.1 keeps the atlas as GuiRenderer.itemAtlas
 *     (null until prepareItemAtlas) holding texture / textureView / depthTexture / depthTextureView. Read only: 26.1's
 *     fields are final and an atlas is only made by GuiItemAtlas's constructor, so the aw-put rows stay open.</li>
 * </ul>
 */
@Environment(EnvType.CLIENT)
public final class CbWallGuiAtlasFields {
    private CbWallGuiAtlasFields() {
    }

    public static int maximumItemAtlasSize() {
        return GuiItemAtlas.MAXIMUM_TEXTURE_SIZE;
    }

    public static GpuTexture itemsAtlas(GuiRenderer r) {
        GuiItemAtlas a = r.itemAtlas;
        return a == null ? null : a.texture;
    }

    public static GpuTextureView itemsAtlasView(GuiRenderer r) {
        GuiItemAtlas a = r.itemAtlas;
        return a == null ? null : a.textureView;
    }

    public static GpuTexture itemsAtlasDepth(GuiRenderer r) {
        GuiItemAtlas a = r.itemAtlas;
        return a == null ? null : a.depthTexture;
    }

    public static GpuTextureView itemsAtlasDepthView(GuiRenderer r) {
        GuiItemAtlas a = r.itemAtlas;
        return a == null ? null : a.depthTextureView;
    }
}
