package top.dext.centurybridge.step.v1_21_5.rt;

import java.nio.ByteBuffer;
import java.nio.FloatBuffer;
import java.nio.IntBuffer;
import java.util.function.Consumer;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import org.joml.Matrix4f;
import org.joml.Vector3f;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL14;
import org.lwjgl.opengl.GL15;
import org.lwjgl.opengl.GL20;
import org.lwjgl.opengl.GL30;

/**
 * The static members of 1.21.4's com/mojang/blaze3d/platform/GlStateManager that 1.21.5 dropped from
 * com/mojang/blaze3d/opengl/GlStateManager (the class itself moves there by classRenames; these are the
 * staticRedirects of the dropped ones). Each body is 1.21.4's (javap -c -p 1.21.4 GlStateManager), kept consistent
 * with what 1.21.5 tracks itself:
 * <ul>
 * <li>Thread checks: 1.21.4's assertOnRenderThreadOrInit / isOnRenderThreadOrInit are 1.21.5's assertOnRenderThread /
 * isOnRenderThread (1.21.5 dropped the OrInit forms; its render thread is the init thread).</li>
 * <li>State 1.21.5 caches goes through 1.21.5's own cached entry point, so its cache never disagrees with GL:
 * _blendFunc(src, dst) is _blendFuncSeparate(src, dst, src, dst) (glBlendFunc sets the alpha factors too, so GL ends
 * in the same state; 1.21.4 updated only its RGB cache), _genTextures / _deleteTextures go through 1.21.5's
 * _genTexture / _deleteTexture per texture (its texture counter and its per-unit binding cache, which 1.21.4's
 * _deleteTextures cleared the same way).</li>
 * <li>Stencil state: 1.21.5 neither caches nor sets it (no glStencil* call in its code jar), so 1.21.4's StencilState
 * cache lives here with 1.21.4's initial values (func ALWAYS / ref 0 / mask -1, write mask -1, ops KEEP); no other
 * writer can make it stale. _stencilFunc keeps 1.21.4's own comparison (the new func against each cached field).</li>
 * <li>Uncached GL state (clear values, blend equation, uniforms, renderbuffers, pixel transfer, float texture
 * parameters): the GL call 1.21.4 made. 1.21.5's command encoder sets its clear color and depth itself before each of
 * its clears, as 1.21.4's callers did.</li>
 * <li>Lighting: 1.21.4's setup*DiffuseLighting computed the two light directions and handed them to
 * RenderSystem.setShaderLights, which 1.21.5 keeps (its Lighting class calls it the same way).</li>
 * <li>upload: 1.21.4's body; NativeImage.Format's GL helpers 1.21.4 called (setUnpackPixelStoreState, glFormat) are
 * 1.21.5's _pixelStore(GL_UNPACK_ALIGNMENT, components()) and GlConst.toGl(format) (same values, javap). Off the render
 * thread 1.21.4 queued the upload with RenderSystem.recordRenderCall; 1.21.5 has no render-call queue, so it is queued
 * on Minecraft's task queue, which 1.21.5 drains on the render thread every frame (it runs at that point of the frame,
 * not at 1.21.4's replay point).</li>
 * </ul>
 */
@Environment(EnvType.CLIENT)
public final class CbGlStateManager {

    private CbGlStateManager() {
    }

    // 1.21.4 StencilState / StencilFunc (GlStateManager$class_1035 / $class_1034), initial values as 1.21.4's constructors
    private static int stencilFunc = 519;
    private static int stencilRef;
    private static int stencilFuncMask = -1;
    private static int stencilMask = -1;
    private static int stencilFail = 7680;
    private static int stencilDepthFail = 7680;
    private static int stencilPass = 7680;

    /** was GlStateManager.TEXTURE_COUNT (12, the texture units 1.21.4 cached); 1.21.5's TEXTURES cache has the same 12 units */
    public static int TEXTURE_COUNT() {
        return 12;
    }

    private static void assertOnRenderThread() {
        com.mojang.blaze3d.systems.RenderSystem.assertOnRenderThread();
    }

    public static int _glGetAttribLocation(int program, CharSequence name) {
        assertOnRenderThread();
        return GL20.glGetAttribLocation(program, name);
    }

    public static int getBoundFramebuffer() {
        assertOnRenderThread();
        return com.mojang.blaze3d.opengl.GlStateManager._getInteger(36006);
    }

    public static int glCheckFramebufferStatus(int target) {
        assertOnRenderThread();
        return GL30.glCheckFramebufferStatus(target);
    }

    public static int glGenRenderbuffers() {
        assertOnRenderThread();
        return GL30.glGenRenderbuffers();
    }

    public static ByteBuffer _glMapBuffer(int target, int access) {
        assertOnRenderThread();
        return GL15.glMapBuffer(target, access);
    }

    public static void _blendEquation(int mode) {
        assertOnRenderThread();
        GL14.glBlendEquation(mode);
    }

    /** glBlendFunc(src, dst) through 1.21.5's cached _blendFuncSeparate (glBlendFunc is glBlendFuncSeparate(src, dst, src, dst)) */
    public static void _blendFunc(int src, int dst) {
        assertOnRenderThread();
        com.mojang.blaze3d.opengl.GlStateManager._blendFuncSeparate(src, dst, src, dst);
    }

    public static void _clearColor(float r, float g, float b, float a) {
        assertOnRenderThread();
        GL11.glClearColor(r, g, b, a);
    }

    public static void _clearDepth(double depth) {
        assertOnRenderThread();
        GL11.glClearDepth(depth);
    }

    public static void _clearStencil(int stencil) {
        assertOnRenderThread();
        GL11.glClearStencil(stencil);
    }

    /** 1.21.4: clears the binding cache entries naming a deleted texture, deletes, counts down; 1.21.5's _deleteTexture does each */
    public static void _deleteTextures(int[] textures) {
        assertOnRenderThread();
        for (int texture : textures) {
            com.mojang.blaze3d.opengl.GlStateManager._deleteTexture(texture);
        }
    }

    public static void _disableVertexAttribArray(int index) {
        assertOnRenderThread();
        GL20.glDisableVertexAttribArray(index);
    }

    /** 1.21.4: counts up by the array's length, glGenTextures(array); 1.21.5's _genTexture counts and generates each */
    public static void _genTextures(int[] textures) {
        assertOnRenderThread();
        for (int i = 0; i < textures.length; i++) {
            textures[i] = com.mojang.blaze3d.opengl.GlStateManager._genTexture();
        }
    }

    public static void _getTexImage(int target, int level, int format, int type, long pixels) {
        assertOnRenderThread();
        GL11.glGetTexImage(target, level, format, type, pixels);
    }

    public static void _glBindRenderbuffer(int target, int renderbuffer) {
        assertOnRenderThread();
        GL30.glBindRenderbuffer(target, renderbuffer);
    }

    public static void _glCopyTexSubImage2D(int target, int level, int xOffset, int yOffset, int x, int y, int width, int height) {
        assertOnRenderThread();
        GL11.glCopyTexSubImage2D(target, level, xOffset, yOffset, x, y, width, height);
    }

    public static void _glDeleteRenderbuffers(int renderbuffer) {
        assertOnRenderThread();
        GL30.glDeleteRenderbuffers(renderbuffer);
    }

    public static void _glDeleteVertexArrays(int array) {
        assertOnRenderThread();
        GL30.glDeleteVertexArrays(array);
    }

    public static void _glDrawPixels(int width, int height, int format, int type, long pixels) {
        assertOnRenderThread();
        GL11.glDrawPixels(width, height, format, type, pixels);
    }

    public static void _glFramebufferRenderbuffer(int target, int attachment, int renderbufferTarget, int renderbuffer) {
        assertOnRenderThread();
        GL30.glFramebufferRenderbuffer(target, attachment, renderbufferTarget, renderbuffer);
    }

    public static void _glRenderbufferStorage(int target, int internalFormat, int width, int height) {
        assertOnRenderThread();
        GL30.glRenderbufferStorage(target, internalFormat, width, height);
    }

    public static void _glUniform2(int location, IntBuffer value) {
        assertOnRenderThread();
        GL20.glUniform2iv(location, value);
    }

    public static void _glUniform4(int location, IntBuffer value) {
        assertOnRenderThread();
        GL20.glUniform4iv(location, value);
    }

    public static void _glUniformMatrix2(int location, boolean transpose, FloatBuffer value) {
        assertOnRenderThread();
        GL20.glUniformMatrix2fv(location, transpose, value);
    }

    public static void _glUniformMatrix3(int location, boolean transpose, FloatBuffer value) {
        assertOnRenderThread();
        GL20.glUniformMatrix3fv(location, transpose, value);
    }

    public static void _glUniformMatrix4(int location, boolean transpose, FloatBuffer value) {
        assertOnRenderThread();
        GL20.glUniformMatrix4fv(location, transpose, value);
    }

    public static void _readPixels(int x, int y, int width, int height, int format, int type, ByteBuffer pixels) {
        assertOnRenderThread();
        GL11.glReadPixels(x, y, width, height, format, type, pixels);
    }

    /** 1.21.4's comparison as written there: the new func against each cached field (ref and mask included) */
    public static void _stencilFunc(int func, int ref, int mask) {
        assertOnRenderThread();
        if (func != stencilFunc || func != stencilRef || func != stencilFuncMask) {
            stencilFunc = func;
            stencilRef = ref;
            stencilFuncMask = mask;
            GL11.glStencilFunc(func, ref, mask);
        }
    }

    public static void _stencilMask(int mask) {
        assertOnRenderThread();
        if (mask != stencilMask) {
            stencilMask = mask;
            GL11.glStencilMask(mask);
        }
    }

    public static void _stencilOp(int fail, int depthFail, int pass) {
        assertOnRenderThread();
        if (fail != stencilFail || depthFail != stencilDepthFail || pass != stencilPass) {
            stencilFail = fail;
            stencilDepthFail = depthFail;
            stencilPass = pass;
            GL11.glStencilOp(fail, depthFail, pass);
        }
    }

    public static void _texParameter(int target, int name, float value) {
        assertOnRenderThread();
        GL11.glTexParameterf(target, name, value);
    }

    /** 1.21.4: the two light directions turned by the matrix (transformDirection), then RenderSystem.setShaderLights */
    public static void setupLevelDiffuseLighting(Vector3f light0, Vector3f light1, Matrix4f matrix) {
        assertOnRenderThread();
        Vector3f a = matrix.transformDirection(light0, new Vector3f());
        Vector3f b = matrix.transformDirection(light1, new Vector3f());
        com.mojang.blaze3d.systems.RenderSystem.setShaderLights(a, b);
    }

    public static void setupGuiFlatDiffuseLighting(Vector3f light0, Vector3f light1) {
        assertOnRenderThread();
        Matrix4f matrix = new Matrix4f().rotationY(-0.3926991F).rotateX(2.3561945F);
        setupLevelDiffuseLighting(light0, light1, matrix);
    }

    public static void setupGui3DDiffuseLighting(Vector3f light0, Vector3f light1) {
        assertOnRenderThread();
        Matrix4f matrix = new Matrix4f().scaling(1.0F, -1.0F, 1.0F).rotateYXZ(1.0821041F, 3.2375858F, 0.0F).rotateYXZ(-0.3926991F, 2.3561945F, 0.0F);
        setupLevelDiffuseLighting(light0, light1, matrix);
    }

    public static void upload(int level, int x, int y, int width, int height, net.minecraft.class_1011.class_1012 format, IntBuffer buffer,
            Consumer<IntBuffer> release) {
        if (!com.mojang.blaze3d.systems.RenderSystem.isOnRenderThread()) {
            net.minecraft.class_310.method_1551().execute(() -> doUpload(level, x, y, width, height, format, buffer, release));
        } else {
            doUpload(level, x, y, width, height, format, buffer, release);
        }
    }

    /** 1.21.4's private _upload */
    private static void doUpload(int level, int x, int y, int width, int height, net.minecraft.class_1011.class_1012 format, IntBuffer buffer,
            Consumer<IntBuffer> release) {
        try {
            assertOnRenderThread();
            com.mojang.blaze3d.opengl.GlStateManager._pixelStore(3314, width);
            com.mojang.blaze3d.opengl.GlStateManager._pixelStore(3316, 0);
            com.mojang.blaze3d.opengl.GlStateManager._pixelStore(3315, 0);
            com.mojang.blaze3d.opengl.GlStateManager._pixelStore(3317, format.method_4335());
            GL11.glTexSubImage2D(3553, level, x, y, width, height, com.mojang.blaze3d.opengl.GlConst.toGl(format), 5121, buffer);
        } finally {
            release.accept(buffer);
        }
    }
}
