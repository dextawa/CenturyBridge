package top.dext.centurybridge.step.v1_21_9.rt;

import com.mojang.blaze3d.systems.RenderSystem;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.class_10219;
import net.minecraft.class_289;
import net.minecraft.class_310;
import org.lwjgl.glfw.GLFW;

/**
 * RenderSystem.flipFrame(windowHandle, tracyCapture), which 1.21.9 replaced by flipFrame(window, tracyCapture) (javap
 * both): the same sequence, 1.21.9 reading the handle off the Window. 1.21.8's body over the handle it was given:
 * poll events, clear the Tesselator, swap the handle's buffers, end the Tracy frame, reset the dynamic uniforms, end
 * the level renderer's frame, poll events (pollEvents opened by the layer's access widener).
 */
@Environment(EnvType.CLIENT)
public final class CbFrameFlip {

    private CbFrameFlip() {
    }

    /** RenderSystem.flipFrame(windowHandle, tracyCapture) */
    public static void flipFrame(long window, class_10219 capture) {
        RenderSystem.pollEvents();
        class_289.method_1348().method_60828();
        GLFW.glfwSwapBuffers(window);
        if (capture != null) {
            capture.method_64233();
        }
        RenderSystem.getDynamicUniforms().method_71105();
        class_310.method_1551().field_1769.method_71118();
        RenderSystem.pollEvents();
    }
}
