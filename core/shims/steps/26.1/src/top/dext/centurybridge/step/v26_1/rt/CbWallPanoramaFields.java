package top.dext.centurybridge.step.v26_1.rt;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.CubeMap;
import net.minecraft.client.renderer.Panorama;

/**
 * 1.21.11 PanoramaRenderer.minecraft / cubeMap (class_766.field_4139 / field_4141, opened by access wideners):
 * 1.21.11's constructor stored Minecraft.getInstance() and the cube map GameRenderer made for it. 26.1 (javap):
 * Panorama keeps neither; it asks Minecraft.getInstance() each frame, and the panorama cube map is
 * GameRenderer.guiRenderer.cubeMap (built in GuiRenderer's constructor, drawn there).
 */
@Environment(EnvType.CLIENT)
public final class CbWallPanoramaFields {
    private CbWallPanoramaFields() {
    }

    public static Minecraft minecraft(Panorama p) {
        return Minecraft.getInstance();
    }

    public static CubeMap cubeMap(Panorama p) {
        return Minecraft.getInstance().gameRenderer.guiRenderer.cubeMap;
    }
}
