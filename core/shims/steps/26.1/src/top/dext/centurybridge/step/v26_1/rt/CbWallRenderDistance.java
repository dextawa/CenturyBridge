package top.dext.centurybridge.step.v26_1.rt;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

/**
 * 1.21.11 GameRenderer.renderDistance (class_757.field_4025) as written by a mod, kept on GameRenderer by
 * mixin/CbWallGameRendererRenderDistanceHooks until the next frame resets it (NaN: nothing written this frame).
 */
@Environment(EnvType.CLIENT)
public interface CbWallRenderDistance {

    float cb$writtenRenderDistance();

    void cb$writeRenderDistance(float v);
}
