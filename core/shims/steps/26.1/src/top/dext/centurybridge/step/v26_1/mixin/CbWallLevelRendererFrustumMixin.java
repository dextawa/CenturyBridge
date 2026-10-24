package top.dext.centurybridge.step.v26_1.mixin;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.LevelRenderer;
import net.minecraft.client.renderer.culling.Frustum;
import org.spongepowered.asm.mixin.Mixin;

/**
 * 1.21.11's LevelRenderer.captureFrustum / killFrustum / getCapturedFrustum: 26.1 moved the captured culling frustum
 * (the debug "freeze frustum" state) to the camera; the level renderer's is the game renderer's main camera's.
 */
@Environment(EnvType.CLIENT)
@Mixin(LevelRenderer.class)
public abstract class CbWallLevelRendererFrustumMixin {

    public void captureFrustum() {
        Minecraft.getInstance().gameRenderer.getMainCamera().captureFrustum();
    }

    public void killFrustum() {
        Minecraft.getInstance().gameRenderer.getMainCamera().killFrustum();
    }

    public Frustum getCapturedFrustum() {
        return Minecraft.getInstance().gameRenderer.getMainCamera().getCapturedFrustum();
    }
}
