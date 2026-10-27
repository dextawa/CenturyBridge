package top.dext.centurybridge.step.v1_21_9.rt;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import com.mojang.blaze3d.opengl.GlStateManager;
import com.mojang.blaze3d.systems.RenderSystem;
import java.nio.FloatBuffer;
import java.nio.IntBuffer;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL13;
import org.lwjgl.opengl.GL20;

/**
 * GlStateManager statics 1.21.9 dropped, with 1.21.8's bodies (javap -c -p 1.21.8 GlStateManager): each is the GL call
 * 1.21.8 made, on the render thread. _getActiveTexture reads 1.21.9's own active-texture cache (the same field 1.21.8
 * read; the layer's access widener opens it). glActiveTexture is 1.21.8's raw GL13 call, which, as on 1.21.8, leaves that
 * cache alone.
 */
@Environment(EnvType.CLIENT)
public final class CbGlStateManager {

    private CbGlStateManager() {
    }

    public static int _getActiveTexture() {
        return GlStateManager.activeTexture + 33984;
    }

    public static void glActiveTexture(int texture) {
        RenderSystem.assertOnRenderThread();
        GL13.glActiveTexture(texture);
    }

    public static void _glUniform1(int location, IntBuffer value) {
        RenderSystem.assertOnRenderThread();
        GL20.glUniform1iv(location, value);
    }

    public static void _glUniform1(int location, FloatBuffer value) {
        RenderSystem.assertOnRenderThread();
        GL20.glUniform1fv(location, value);
    }

    public static void _glUniform2(int location, FloatBuffer value) {
        RenderSystem.assertOnRenderThread();
        GL20.glUniform2fv(location, value);
    }

    public static void _glUniform3(int location, IntBuffer value) {
        RenderSystem.assertOnRenderThread();
        GL20.glUniform3iv(location, value);
    }

    public static void _glUniform3(int location, FloatBuffer value) {
        RenderSystem.assertOnRenderThread();
        GL20.glUniform3fv(location, value);
    }

    public static void _glUniform4(int location, FloatBuffer value) {
        RenderSystem.assertOnRenderThread();
        GL20.glUniform4fv(location, value);
    }

    public static void _glUniformMatrix4(int location, FloatBuffer value) {
        RenderSystem.assertOnRenderThread();
        GL20.glUniformMatrix4fv(location, false, value);
    }

    public static void _texImage2D(int target, int level, int internalFormat, int width, int height, int border, int format, int type,
                                   IntBuffer pixels) {
        RenderSystem.assertOnRenderThread();
        GL11.glTexImage2D(target, level, internalFormat, width, height, border, format, type, pixels);
    }

    public static void _texSubImage2D(int target, int level, int x, int y, int width, int height, int format, int type, IntBuffer pixels) {
        RenderSystem.assertOnRenderThread();
        GL11.glTexSubImage2D(target, level, x, y, width, height, format, type, pixels);
    }
}
