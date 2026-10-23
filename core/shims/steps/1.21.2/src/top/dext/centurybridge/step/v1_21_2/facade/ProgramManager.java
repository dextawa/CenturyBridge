package top.dext.centurybridge.step.v1_21_2.facade;

import com.mojang.blaze3d.platform.GlStateManager;
import com.mojang.blaze3d.systems.RenderSystem;
import java.io.IOException;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import org.slf4j.Logger;

/**
 * 1.21.1's ProgramManager (net/minecraft/class_285, classRenames), gone in 1.21.2: creating, linking, using and
 * releasing GL programs of Shaders. 1.21.1's bodies, on the GlStateManager calls 1.21.2 keeps.
 */
@Environment(EnvType.CLIENT)
public class ProgramManager {

    /** LOGGER */
    public static final Logger field_1551 = com.mojang.logging.LogUtils.getLogger();

    public ProgramManager() {
    }

    /** glUseProgram(program) */
    public static void method_22094(int program) {
        RenderSystem.assertOnRenderThread();
        GlStateManager._glUseProgram(program);
    }

    /** releaseProgram(shader): its two programs closed, the GL program deleted */
    public static void method_1304(Shader shader) {
        RenderSystem.assertOnRenderThread();
        shader.method_1278().method_1282();
        shader.method_1274().method_1282();
        GlStateManager.glDeleteProgram(shader.method_1270());
    }

    /** createProgram() */
    public static int method_1306() throws IOException {
        RenderSystem.assertOnRenderThread();
        int id = GlStateManager.glCreateProgram();
        if (id <= 0) {
            throw new IOException("Could not create shader program (returned program ID " + id + ")");
        }
        return id;
    }

    /** linkShader(shader): attach its programs, link, log a failure */
    public static void method_1307(Shader shader) {
        RenderSystem.assertOnRenderThread();
        shader.method_34418();
        GlStateManager.glLinkProgram(shader.method_1270());
        if (GlStateManager.glGetProgrami(shader.method_1270(), 35714) == 0) {
            field_1551.warn("Error encountered when linking program containing VS {} and FS {}. Log output:",
                shader.method_1274().method_1280(), shader.method_1278().method_1280());
            field_1551.warn(GlStateManager.glGetProgramInfoLog(shader.method_1270(), 32768));
        }
    }
}
