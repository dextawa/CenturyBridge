package top.dext.centurybridge.step.v26_2.rt;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.components.DebugScreenOverlay;
import net.minecraft.client.renderer.state.level.CameraRenderState;

/**
 * 26.1.2 DebugScreenOverlay.render3dCrosshair(camera, guiScale): 26.2 moved the 3D debug crosshair into
 * DebugCrosshairRenderer.render(camera, guiScale), owned by the GameRenderer (field debugCrosshairRenderer, opened
 * by the layer's access widener). GameRenderer calls it where 26.1.2 called the overlay's (javap: the same
 * guiScale from the window render state at the same site); the crosshair lines, colours and rotation are the same,
 * 26.2 building its vertex buffer once instead of per call.
 */
@Environment(EnvType.CLIENT)
public final class CbS26DebugClient {
    private CbS26DebugClient() {}

    public static void render3dCrosshair(DebugScreenOverlay self, CameraRenderState camera, int guiScale) {
        Minecraft.getInstance().gameRenderer.debugCrosshairRenderer.render(camera, guiScale);
    }
}
