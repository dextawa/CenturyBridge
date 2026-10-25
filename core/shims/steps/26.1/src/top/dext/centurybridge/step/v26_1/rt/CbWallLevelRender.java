package top.dext.centurybridge.step.v26_1.rt;

import com.mojang.blaze3d.buffers.GpuBufferSlice;
import com.mojang.blaze3d.resource.GraphicsResourceAllocator;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.Camera;
import net.minecraft.client.DeltaTracker;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.LevelRenderer;
import net.minecraft.client.renderer.culling.Frustum;
import net.minecraft.client.renderer.state.level.CameraRenderState;
import net.minecraft.client.renderer.state.level.LevelRenderState;
import net.minecraft.world.phys.Vec3;
import org.joml.Matrix4f;
import org.joml.Vector4f;

/**
 * 1.21.11's LevelRenderer.renderLevel(allocator, deltaTracker, renderOutline, camera, modelView, projection,
 * cullingProjection, fog, fogColor, renderSky) on 26.1.
 * <p>
 * 1.21.11 culled and drew the level from the given camera in that one call. 26.1 splits the frame: GameRenderer
 * first extracts -- the camera into a CameraRenderState (Camera.extractRenderState, its fluid, its fog), the level
 * into the level renderer's LevelRenderState (extractLevel(deltaTracker, camera, partialTick): visible sections and
 * their draws from the camera's cull frustum, entities, block entities, ...) -- and then renderLevel draws those
 * states. So a 1.21.11 call extracts the level for its camera and draws it: the camera state is the camera's own,
 * with the projection the caller passed (1.21.11 drew with it) and the main frame's fog data (the fog buffer and
 * colour are the caller's, passed on); the sections are the ones extractLevel prepared for that camera. Culling is
 * 1.21.11's: extractLevel culls with the camera's cull frustum, which for the call is the frustum 1.21.11 built --
 * new Frustum(modelView, cullingProjection) prepared at the camera's position, as Camera.prepareCullFrustum builds
 * 26.1's -- and the camera's own frustum is put back afterwards. The extraction goes into the level renderer's own level state, as 1.21.11's call
 * recomputed the renderer's visible sections for its camera.
 */
@Environment(EnvType.CLIENT)
public final class CbWallLevelRender {

    private CbWallLevelRender() {
    }

    public static void renderLevel(LevelRenderer renderer, GraphicsResourceAllocator allocator, DeltaTracker deltaTracker,
                                   boolean renderOutline, Camera camera, Matrix4f modelView, Matrix4f projection,
                                   Matrix4f cullingProjection, GpuBufferSlice fog, Vector4f fogColor, boolean renderSky) {
        float partialTick = camera.getCameraEntityPartialTicks(deltaTracker);
        CameraRenderState cameraState = new CameraRenderState();
        camera.extractRenderState(cameraState, partialTick);
        cameraState.fogType = camera.getFluidInCamera();
        cameraState.projectionMatrix = new Matrix4f(projection);
        CameraRenderState main = Minecraft.getInstance().gameRenderer.getGameRenderState().levelRenderState.cameraRenderState;
        cameraState.fogData = main == null ? null : main.fogData;
        Frustum own = camera.cullFrustum;
        Frustum culling = new Frustum(modelView, cullingProjection);
        Vec3 position = camera.position();
        culling.prepare(position.x(), position.y(), position.z());
        camera.cullFrustum = culling;
        try {
            renderer.extractLevel(deltaTracker, camera, partialTick);
        } finally {
            camera.cullFrustum = own;
        }
        LevelRenderState level = renderer.levelRenderState;
        renderer.renderLevel(allocator, deltaTracker, renderOutline, cameraState, modelView, fog, fogColor, renderSky,
                level.chunkSectionsToRender);
    }
}
