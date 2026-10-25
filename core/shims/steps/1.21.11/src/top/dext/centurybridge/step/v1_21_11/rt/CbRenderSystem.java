package top.dext.centurybridge.step.v1_21_11.rt;

import com.mojang.blaze3d.systems.RenderSystem;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import org.joml.Matrix4f;

/**
 * 1.21.10's RenderSystem texture matrix and shader line width, which 1.21.11 removed: 1.21.10's draw took both from
 * here (the texturing / line shards set them around each draw); 1.21.11's draw takes the texture matrix from the
 * render type's TextureTransform and the line width from each vertex. The state is kept with 1.21.10's bodies, and
 * the render types 1.21.10's CompositeState builds read it through their shards (rt/CbRenderTypeCreate).
 */
@Environment(EnvType.CLIENT)
public final class CbRenderSystem {
    private static Matrix4f textureMatrix = new Matrix4f();
    private static float shaderLineWidth;

    private CbRenderSystem() {
    }

    /** RenderSystem.setTextureMatrix */
    public static void setTextureMatrix(Matrix4f matrix) {
        RenderSystem.assertOnRenderThread();
        textureMatrix = new Matrix4f(matrix);
    }

    /** RenderSystem.resetTextureMatrix */
    public static void resetTextureMatrix() {
        RenderSystem.assertOnRenderThread();
        textureMatrix.identity();
    }

    /** RenderSystem.getTextureMatrix */
    public static Matrix4f getTextureMatrix() {
        RenderSystem.assertOnRenderThread();
        return textureMatrix;
    }

    /** RenderSystem.lineWidth */
    public static void lineWidth(float width) {
        RenderSystem.assertOnRenderThread();
        shaderLineWidth = width;
    }

    /** RenderSystem.getShaderLineWidth */
    public static float getShaderLineWidth() {
        RenderSystem.assertOnRenderThread();
        return shaderLineWidth;
    }
}
