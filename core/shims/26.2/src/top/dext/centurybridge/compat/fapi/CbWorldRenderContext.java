package top.dext.centurybridge.compat.fapi;

import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.Camera;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.client.renderer.LevelRenderer;
import net.minecraft.client.renderer.culling.Frustum;
import net.minecraft.client.renderer.state.level.CameraRenderState;
import net.minecraft.util.profiling.Profiler;
import net.minecraft.util.profiling.ProfilerFiller;
import net.fabricmc.fabric.api.client.rendering.v1.level.AbstractLevelRenderContext;
import net.fabricmc.fabric.api.client.rendering.v1.level.LevelRenderContext;
import org.joml.Matrix4f;

/**
 * fabric-rendering-v1's WorldRenderContext as 1.20.x listeners read it (15
 * corpus mods). Two 1.20.1 accessors cannot be expressed on 26.2 and are absent:
 * consumers() (MultiBufferSource -- immediate-mode vertex consumers are gone;
 * rendering goes through submit nodes) and lightmapTextureManager()
 * (LightTexture gone). A listener that reaches for them fails with
 * NoSuchMethodError inside the event dispatch, which CbWorldRenderEvents
 * catches: that listener is dropped with a log line and the game goes on.
 */
public interface CbWorldRenderContext {

    LevelRenderer worldRenderer();

    PoseStack matrixStack();

    float tickDelta();

    long limitTime();

    boolean blockOutlines();

    Camera camera();

    GameRenderer gameRenderer();

    Matrix4f projectionMatrix();

    ClientLevel world();

    ProfilerFiller profiler();

    boolean advancedTranslucency();

    Frustum frustum();

    /** what 26.2 offers at each hook, viewed through the 1.20.1 accessors */
    final class Impl implements CbWorldRenderContext {
        private final AbstractLevelRenderContext ctx;
        private final PoseStack poseStack;

        Impl(AbstractLevelRenderContext ctx) {
            this.ctx = ctx;
            this.poseStack = ctx instanceof LevelRenderContext r ? r.poseStack() : new PoseStack();
        }

        private CameraRenderState cam() {
            return ctx.levelState().cameraRenderState;
        }

        @Override
        public LevelRenderer worldRenderer() {
            return ctx.levelRenderer();
        }

        @Override
        public PoseStack matrixStack() {
            return poseStack;
        }

        @Override
        public float tickDelta() {
            return Minecraft.getInstance().getDeltaTracker().getGameTimeDeltaPartialTick(false);
        }

        @Override
        public long limitTime() {
            return 0L;
        }

        @Override
        public boolean blockOutlines() {
            return true;
        }

        @Override
        public Camera camera() {
            return Minecraft.getInstance().gameRenderer.mainCamera();
        }

        @Override
        public GameRenderer gameRenderer() {
            return ctx.gameRenderer();
        }

        @Override
        public Matrix4f projectionMatrix() {
            CameraRenderState c = cam();
            return c == null || c.projectionMatrix == null ? new Matrix4f() : c.projectionMatrix;
        }

        @Override
        public ClientLevel world() {
            return Minecraft.getInstance().level;
        }

        @Override
        public ProfilerFiller profiler() {
            return Profiler.get();
        }

        @Override
        public boolean advancedTranslucency() {
            return false;
        }

        @Override
        public Frustum frustum() {
            CameraRenderState c = cam();
            return c == null ? null : c.cullFrustum;
        }
    }
}
