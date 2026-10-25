package top.dext.centurybridge.step.v26_1.rt;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

/**
 * The 1.21.11 GameRenderer.getDepthFar() the layer restores on GameRenderer (mixin/CbWallGameRendererMixin), as an
 * interface GameRenderer implements, so the camera's far plane can be read from it (mixin/CbWallCameraDepthFarMixin).
 */
@Environment(EnvType.CLIENT)
public interface CbWallDepthFar {

    float getDepthFar();
}
