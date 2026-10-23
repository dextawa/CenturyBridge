package top.dext.centurybridge.step.v1_21_2.rt;

import java.util.function.Supplier;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

/**
 * 1.21.1's core shaders over 1.21.2's ShaderManager.
 *
 * 1.21.1: GameRenderer loaded every core shader as a ShaderInstance at resource reload and answered them from static
 * getters (getPositionShader() ...) and by name (getShader(name)); RenderSystem.setShader took a supplier of one.
 * 1.21.2: a core shader is a CoreShaders key (ShaderProgram), compiled and cached by the ShaderManager
 * (Minecraft.getShaderManager().getProgram(key)), and the compiled program is the same class (class_5944); RenderSystem
 * takes the program or the key. The getters (generated, CbRenderShadersGen) answer the program of the key 1.21.2
 * registers under the 1.21.1 shader's name.
 */
@Environment(EnvType.CLIENT)
public final class CbRenderShaders {

    private CbRenderShaders() {
    }

    /** the compiled program of a core shader key (ShaderManager.getProgram: null when it failed to compile) */
    public static net.minecraft.class_5944 program(net.minecraft.class_10156 key) {
        return net.minecraft.class_310.method_1551().method_62887().method_62947(key);
    }

    /**
     * 1.21.1 RenderSystem.setShader(Supplier&lt;ShaderInstance&gt;) (staticRedirects): off the render thread 1.21.1 queued
     * the call (recordRenderCall) and asked the supplier when it ran; on it, asked the supplier and set the shader.
     */
    public static void setShader(Supplier<net.minecraft.class_5944> supplier) {
        if (!com.mojang.blaze3d.systems.RenderSystem.isOnRenderThread()) {
            com.mojang.blaze3d.systems.RenderSystem.recordRenderCall(() -> com.mojang.blaze3d.systems.RenderSystem.setShader(supplier.get()));
        } else {
            com.mojang.blaze3d.systems.RenderSystem.setShader(supplier.get());
        }
    }

    /** 1.21.1 GameRenderer.getShader(name) (instanceRedirects): the loaded core shader of that name, null for none */
    public static net.minecraft.class_5944 method_35767(net.minecraft.class_757 gameRenderer, String name) {
        if (name == null) {
            return null;
        }
        net.minecraft.class_10156 key = CbRenderShadersGen.byName(name);
        return key == null ? null : program(key);
    }
}
