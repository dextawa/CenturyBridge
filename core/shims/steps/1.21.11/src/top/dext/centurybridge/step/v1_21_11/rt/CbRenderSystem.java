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

    /**
     * 1.21.10's twelve shader texture slots (RenderSystem.shaderTextures, javap: new GpuTextureView[12] in its clinit).
     * 1.21.11 removed them: a render type binds its textures from its RenderSetup, so no 1.21.11 draw reads a slot. They
     * are kept with 1.21.10's bodies for 1.21.10 code that sets and reads them back itself.
     */
    private static final com.mojang.blaze3d.textures.GpuTextureView[] shaderTextures =
        new com.mojang.blaze3d.textures.GpuTextureView[12];

    /** RenderSystem.setShaderTexture(int, GpuTextureView): out-of-range slots are ignored, as in 1.21.10 */
    public static void setShaderTexture(int slot, com.mojang.blaze3d.textures.GpuTextureView view) {
        RenderSystem.assertOnRenderThread();
        if (slot >= 0 && slot < shaderTextures.length) {
            shaderTextures[slot] = view;
        }
    }

    /** RenderSystem.getShaderTexture(int): null out of range, as in 1.21.10 */
    public static com.mojang.blaze3d.textures.GpuTextureView getShaderTexture(int slot) {
        RenderSystem.assertOnRenderThread();
        return slot >= 0 && slot < shaderTextures.length ? shaderTextures[slot] : null;
    }
}
