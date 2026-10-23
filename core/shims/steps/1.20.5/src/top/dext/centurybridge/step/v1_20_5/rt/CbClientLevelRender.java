package top.dext.centurybridge.step.v1_20_5.rt;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

/**
 * 1.20.4's level rendering entry points on 1.20.5 (s4client): 1.20.5 dropped the PoseStack these took. In
 * 1.20.4 GameRenderer.renderLevel rotated the pose stack by the camera and handed it to LevelRenderer, whose
 * prepareCullFrustum, renderLevel and renderSky read only its matrix (renderClouds pushed onto it); 1.20.5 hands
 * them that rotation as a Matrix4f (the frustum matrix) and gives renderClouds a fresh stack it multiplies by
 * it. So a 1.20.4 pose is 1.20.5's frustum matrix (its pose matrix), and back a stack holding that matrix.
 *
 * <p>A mod class overriding one of the old shapes keeps it (each has an adapter interface here): 1.20.5's
 * new shape reaches it with the matrix as a pose stack, a call to the old shape reaches it directly, and its
 * super call is 1.20.4's body over super's new shape.
 */
@Environment(EnvType.CLIENT)
public final class CbClientLevelRender {

    private CbClientLevelRender() {
    }

    /** a 1.20.4 pose as 1.20.5's frustum matrix */
    public static org.joml.Matrix4f poseMatrix(net.minecraft.class_4587 pose) {
        return new org.joml.Matrix4f(pose.method_23760().method_23761());
    }

    /** 1.20.5's frustum matrix as the 1.20.4 pose stack carrying it */
    public static net.minecraft.class_4587 matrixPose(org.joml.Matrix4f frustum) {
        net.minecraft.class_4587 pose = new net.minecraft.class_4587();
        pose.method_34425(frustum);
        return pose;
    }

    // ------------------------------------------------------------------ LevelRenderer.renderLevel

    public interface LegacyRenderLevel {
        void method_22710(net.minecraft.class_4587 pose, float partialTick, long nanos, boolean outline, net.minecraft.class_4184 camera,
                          net.minecraft.class_757 gameRenderer, net.minecraft.class_765 light, org.joml.Matrix4f projection);
    }

    public interface SuperRenderLevel {
        void cb$super$method_22710(float partialTick, long nanos, boolean outline, net.minecraft.class_4184 camera,
                                   net.minecraft.class_757 gameRenderer, net.minecraft.class_765 light, org.joml.Matrix4f frustum,
                                   org.joml.Matrix4f projection);
    }

    /** LevelRenderer.renderLevel(pose, partialTick, nanos, outline, camera, gameRenderer, light, projection) called on any renderer */
    public static void method_22710(net.minecraft.class_761 self, net.minecraft.class_4587 pose, float partialTick, long nanos, boolean outline,
                                    net.minecraft.class_4184 camera, net.minecraft.class_757 gameRenderer, net.minecraft.class_765 light,
                                    org.joml.Matrix4f projection) {
        if (self instanceof LegacyRenderLevel legacy) {
            legacy.method_22710(pose, partialTick, nanos, outline, camera, gameRenderer, light, projection);
        } else {
            self.method_22710(partialTick, nanos, outline, camera, gameRenderer, light, poseMatrix(pose), projection);
        }
    }

    public static void renderLevel(LegacyRenderLevel self, float partialTick, long nanos, boolean outline, net.minecraft.class_4184 camera,
                                   net.minecraft.class_757 gameRenderer, net.minecraft.class_765 light, org.joml.Matrix4f frustum,
                                   org.joml.Matrix4f projection) {
        self.method_22710(matrixPose(frustum), partialTick, nanos, outline, camera, gameRenderer, light, projection);
    }

    public static void superRenderLevel(SuperRenderLevel self, net.minecraft.class_4587 pose, float partialTick, long nanos, boolean outline,
                                        net.minecraft.class_4184 camera, net.minecraft.class_757 gameRenderer, net.minecraft.class_765 light,
                                        org.joml.Matrix4f projection) {
        self.cb$super$method_22710(partialTick, nanos, outline, camera, gameRenderer, light, poseMatrix(pose), projection);
    }

    // ------------------------------------------------------------------ LevelRenderer.prepareCullFrustum

    public interface LegacyCullFrustum {
        void method_32133(net.minecraft.class_4587 pose, net.minecraft.class_243 cameraPos, org.joml.Matrix4f projection);
    }

    public interface SuperCullFrustum {
        void cb$super$method_32133(net.minecraft.class_243 cameraPos, org.joml.Matrix4f frustum, org.joml.Matrix4f projection);
    }

    /** LevelRenderer.prepareCullFrustum(pose, cameraPos, projection) called on any renderer */
    public static void method_32133(net.minecraft.class_761 self, net.minecraft.class_4587 pose, net.minecraft.class_243 cameraPos,
                                    org.joml.Matrix4f projection) {
        if (self instanceof LegacyCullFrustum legacy) {
            legacy.method_32133(pose, cameraPos, projection);
        } else {
            self.method_32133(cameraPos, poseMatrix(pose), projection);
        }
    }

    public static void cullFrustum(LegacyCullFrustum self, net.minecraft.class_243 cameraPos, org.joml.Matrix4f frustum, org.joml.Matrix4f projection) {
        self.method_32133(matrixPose(frustum), cameraPos, projection);
    }

    public static void superCullFrustum(SuperCullFrustum self, net.minecraft.class_4587 pose, net.minecraft.class_243 cameraPos,
                                        org.joml.Matrix4f projection) {
        self.cb$super$method_32133(cameraPos, poseMatrix(pose), projection);
    }

    // ------------------------------------------------------------------ LevelRenderer.renderSky

    public interface LegacySky {
        void method_3257(net.minecraft.class_4587 pose, org.joml.Matrix4f projection, float partialTick, net.minecraft.class_4184 camera,
                         boolean foggy, Runnable setupFog);
    }

    public interface SuperSky {
        void cb$super$method_3257(org.joml.Matrix4f frustum, org.joml.Matrix4f projection, float partialTick, net.minecraft.class_4184 camera,
                                  boolean foggy, Runnable setupFog);
    }

    /** LevelRenderer.renderSky(pose, projection, partialTick, camera, foggy, setupFog) called on any renderer */
    public static void method_3257(net.minecraft.class_761 self, net.minecraft.class_4587 pose, org.joml.Matrix4f projection, float partialTick,
                                   net.minecraft.class_4184 camera, boolean foggy, Runnable setupFog) {
        if (self instanceof LegacySky legacy) {
            legacy.method_3257(pose, projection, partialTick, camera, foggy, setupFog);
        } else {
            self.method_3257(poseMatrix(pose), projection, partialTick, camera, foggy, setupFog);
        }
    }

    public static void sky(LegacySky self, org.joml.Matrix4f frustum, org.joml.Matrix4f projection, float partialTick, net.minecraft.class_4184 camera,
                           boolean foggy, Runnable setupFog) {
        self.method_3257(matrixPose(frustum), projection, partialTick, camera, foggy, setupFog);
    }

    public static void superSky(SuperSky self, net.minecraft.class_4587 pose, org.joml.Matrix4f projection, float partialTick,
                                net.minecraft.class_4184 camera, boolean foggy, Runnable setupFog) {
        self.cb$super$method_3257(poseMatrix(pose), projection, partialTick, camera, foggy, setupFog);
    }

    // ------------------------------------------------------------------ LevelRenderer.renderClouds

    public interface LegacyClouds {
        void method_3259(net.minecraft.class_4587 pose, org.joml.Matrix4f projection, float partialTick, double camX, double camY, double camZ);
    }

    public interface SuperClouds {
        void cb$super$method_3259(net.minecraft.class_4587 pose, org.joml.Matrix4f frustum, org.joml.Matrix4f projection, float partialTick,
                                  double camX, double camY, double camZ);
    }

    /**
     * LevelRenderer.renderClouds(pose, projection, partialTick, camX, camY, camZ) called on any renderer: 1.20.5's
     * body is 1.20.4's with the frustum matrix multiplied onto the given stack first; the 1.20.4 pose already
     * carries it, so the matrix multiplied in is the identity
     */
    public static void method_3259(net.minecraft.class_761 self, net.minecraft.class_4587 pose, org.joml.Matrix4f projection, float partialTick,
                                   double camX, double camY, double camZ) {
        if (self instanceof LegacyClouds legacy) {
            legacy.method_3259(pose, projection, partialTick, camX, camY, camZ);
        } else {
            self.method_3259(pose, new org.joml.Matrix4f(), projection, partialTick, camX, camY, camZ);
        }
    }

    /** 1.20.5's renderClouds(pose, frustum, ...) of a 1.20.4 override: the stack with the frustum matrix on it, as 1.20.4's caller handed it */
    public static void clouds(LegacyClouds self, net.minecraft.class_4587 pose, org.joml.Matrix4f frustum, org.joml.Matrix4f projection,
                              float partialTick, double camX, double camY, double camZ) {
        pose.method_22903();
        pose.method_34425(frustum);
        try {
            self.method_3259(pose, projection, partialTick, camX, camY, camZ);
        } finally {
            pose.method_22909();
        }
    }

    public static void superClouds(SuperClouds self, net.minecraft.class_4587 pose, org.joml.Matrix4f projection, float partialTick,
                                   double camX, double camY, double camZ) {
        self.cb$super$method_3259(pose, new org.joml.Matrix4f(), projection, partialTick, camX, camY, camZ);
    }

    // ------------------------------------------------------------------ GameRenderer.renderLevel

    public interface LegacyGameRenderLevel {
        void method_3188(float partialTick, long nanos, net.minecraft.class_4587 pose);
    }

    public interface SuperGameRenderLevel {
        void cb$super$method_3188(float partialTick, long nanos);
    }

    /**
     * GameRenderer.renderLevel(partialTick, nanos, pose) called on any game renderer. 1.20.4's GameRenderer.render
     * handed it a fresh stack and everything the level drew was placed by it (camera rotation and view bobbing
     * applied on top); 1.20.5 starts from the model-view stack. The pose goes onto the model-view stack around
     * 1.20.5's renderLevel -- the identity when, as 1.20.4's caller did, it is a fresh stack.
     */
    public static void method_3188(net.minecraft.class_757 self, float partialTick, long nanos, net.minecraft.class_4587 pose) {
        if (self instanceof LegacyGameRenderLevel legacy) {
            legacy.method_3188(partialTick, nanos, pose);
        } else {
            withModelView(pose, () -> self.method_3188(partialTick, nanos));
        }
    }

    /** 1.20.5's renderLevel(partialTick, nanos) of a 1.20.4 override: a fresh pose stack, as 1.20.4's GameRenderer.render handed it */
    public static void gameRenderLevel(LegacyGameRenderLevel self, float partialTick, long nanos) {
        self.method_3188(partialTick, nanos, new net.minecraft.class_4587());
    }

    public static void superGameRenderLevel(SuperGameRenderLevel self, float partialTick, long nanos, net.minecraft.class_4587 pose) {
        withModelView(pose, () -> self.cb$super$method_3188(partialTick, nanos));
    }

    private static void withModelView(net.minecraft.class_4587 pose, Runnable render) {
        org.joml.Matrix4fStack modelView = com.mojang.blaze3d.systems.RenderSystem.getModelViewStack();
        modelView.pushMatrix();
        modelView.mul(pose.method_23760().method_23761());
        com.mojang.blaze3d.systems.RenderSystem.applyModelViewMatrix();
        try {
            render.run();
        } finally {
            modelView.popMatrix();
            com.mojang.blaze3d.systems.RenderSystem.applyModelViewMatrix();
        }
    }
}
