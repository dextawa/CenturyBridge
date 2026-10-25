package top.dext.centurybridge.step.v26_1.mixin;

import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.Camera;
import net.minecraft.client.DeltaTracker;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.client.renderer.Lightmap;
import net.minecraft.client.renderer.LightmapRenderStateExtractor;
import net.minecraft.client.renderer.state.GameRenderState;
import net.minecraft.client.renderer.state.level.LevelRenderState;
import net.minecraft.util.profiling.Profiler;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.phys.EntityHitResult;
import org.joml.Matrix4f;
import org.joml.Vector3f;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import top.dext.centurybridge.step.v26_1.facade.LightTexture;
import top.dext.centurybridge.step.v26_1.facade.PanoramicScreenshotParameters;
import top.dext.centurybridge.step.v26_1.rt.CbVanillaReads;
import top.dext.centurybridge.step.v26_1.rt.CbWallPick;
import top.dext.centurybridge.step.v26_1.rt.CbWallDepthFar;

/**
 * 1.21.11 GameRenderer members 26.1 moved elsewhere, given back on GameRenderer itself (covers), so a
 * subclass's override and its super call stay what they were:
 * <ul>
 * <li>getRenderDistance() / getDepthFar() / getProjectionMatrix(fov): 1.21.11's formulas (render distance
 *     in blocks; the far plane the larger of four render distances and the cloud range; a perspective of
 *     the window's aspect from 0.05 to it). 26.1's Camera computes the same far plane; the projection
 *     takes the GPU device's depth range as 26.1's does. Camera.update takes its far plane from getDepthFar()
 *     (mixin/CbWallCameraDepthFarMixin), as 1.21.11's renderer did.</li>
 * <li>pick(partialTicks): 26.1 moved it to Minecraft (same body); Minecraft's calls go through this one
 * (CbWallMinecraftPickMixin).</li>
 * <li>updateCamera(deltaTracker): 26.1's Camera.update.</li>
 * <li>isPanoramicMode() and the panoramic screenshot parameters: kept by 26.1's Camera
 *     (enablePanoramicMode, panoramicForwards).</li>
 * <li>getLevelRenderState(): 26.1's GameRenderState.levelRenderState.</li>
 * <li>lightTexture(): the renderer's lightmap, its state extractor and state as 1.21.11's LightTexture.</li>
 * </ul>
 */
@Mixin(GameRenderer.class)
public abstract class CbWallGameRendererMixin implements CbWallDepthFar, CbWallPick {

    @Shadow
    @Final
    private Minecraft minecraft;

    @Shadow
    @Final
    private Camera mainCamera;

    @Shadow
    @Final
    private GameRenderState gameRenderState;

    @Shadow
    @Final
    private Lightmap lightmap;

    @Shadow
    @Final
    private LightmapRenderStateExtractor lightmapRenderStateExtractor;

    @Unique
    private LightTexture cb$lightTexture;

    @Unique
    private PanoramicScreenshotParameters cb$panoramic;

    @CbVanillaReads("CbWallCameraDepthFarMixin: Camera.update takes the far plane from getDepthFar(), which reads this")
    public float getRenderDistance() {
        return (float) (this.minecraft.options.getEffectiveRenderDistance() * 16);
    }

    @CbVanillaReads("CbWallCameraDepthFarMixin: Camera.update takes its far plane (depthFar: the projection's and the "
        + "culling frustum's far distance, the camera render state's depthFar) from this")
    @Override
    public float getDepthFar() {
        return Math.max(getRenderDistance() * 4.0F, (float) (this.minecraft.options.cloudRange().get() * 16));
    }

    public Matrix4f getProjectionMatrix(float fov) {
        return new Matrix4f().perspective(fov * ((float) Math.PI / 180.0F),
                (float) this.minecraft.getWindow().getWidth() / (float) this.minecraft.getWindow().getHeight(),
                0.05F, getDepthFar(), RenderSystem.getDevice().isZZeroToOne());
    }

    /** 1.21.11 GameRenderer.pick (class_757.method_3190), the body 26.1 moved to Minecraft.pick */
    @CbVanillaReads("CbWallMinecraftPickMixin: Minecraft.tick and renderFrame pick through this method")
    @Override
    public void pick(float partialTicks) {
        Entity camera = this.minecraft.getCameraEntity();
        if (camera == null) {
            return;
        }
        if (this.minecraft.level == null || this.minecraft.player == null) {
            return;
        }
        Profiler.get().push("pick");
        this.minecraft.hitResult = this.minecraft.player.raycastHitResult(partialTicks, camera);
        this.minecraft.crosshairPickEntity = this.minecraft.hitResult instanceof EntityHitResult hit ? hit.getEntity() : null;
        Profiler.get().pop();
    }

    public void updateCamera(DeltaTracker deltaTracker) {
        this.mainCamera.update(deltaTracker);
    }

    public boolean isPanoramicMode() {
        return this.mainCamera.isPanoramicMode();
    }

    public void setPanoramicScreenshotParameters(PanoramicScreenshotParameters parameters) {
        this.cb$panoramic = parameters;
        if (parameters == null) {
            this.mainCamera.disablePanoramicMode();
        } else {
            this.mainCamera.enablePanoramicMode();
            ((CbWallCameraAccess) this.mainCamera).cb$panoramicForwards().set(parameters.comp_5225());
        }
    }

    public PanoramicScreenshotParameters getPanoramicScreenshotParameters() {
        if (!this.mainCamera.isPanoramicMode()) {
            return null;
        }
        // the object set, while the camera still holds its vector (26.1 code may have re-entered the mode)
        PanoramicScreenshotParameters p = this.cb$panoramic;
        if (p != null && p.comp_5225().equals(this.mainCamera.panoramicForwards())) {
            return p;
        }
        return new PanoramicScreenshotParameters(new Vector3f(this.mainCamera.panoramicForwards()));
    }

    public LevelRenderState getLevelRenderState() {
        return this.gameRenderState.levelRenderState;
    }

    public LightTexture lightTexture() {
        if (this.cb$lightTexture == null) {
            this.cb$lightTexture = LightTexture.cb$of(this.lightmap, this.lightmapRenderStateExtractor,
                    this.gameRenderState.lightmapRenderState);
        }
        return this.cb$lightTexture;
    }
}
