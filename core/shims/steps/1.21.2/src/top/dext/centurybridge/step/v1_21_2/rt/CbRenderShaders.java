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

    /** 1.21.1 ShaderInstance.SHADER_CORE_PATH (fieldRedirects) */
    public static final String field_32778 = "shaders";

    /** what CbRenderShaderMixin needs from the ShaderManager */
    public interface Program {
        void cb$compiledFor(net.minecraft.class_10156 key);
    }

    /** a 1.21.2 core shader id (namespace:core/name) in 1.21.1's naming: a vanilla core shader's bare name, else namespace:name */
    public static String legacyName(net.minecraft.class_2960 id) {
        String path = id.method_12832().startsWith("core/") ? id.method_12832().substring("core/".length()) : id.method_12832();
        return id.method_12836().equals("minecraft") ? path : id.method_12836() + ":" + path;
    }

    /** 1.21.1 ShaderInstance.apply's sampler resolution: a RenderTarget's colour texture, a texture's id, an id; else -1 */
    public static int textureId(Object texture) {
        if (texture instanceof net.minecraft.class_276 target) {
            return target.method_30277();
        }
        if (texture instanceof net.minecraft.class_1044 t) {
            return t.method_4624();
        }
        if (texture instanceof Integer i) {
            return i;
        }
        return -1;
    }

    /**
     * 1.21.1 new ShaderInstance(provider, name, format) (ctorRedirects): 1.21.1 read shaders/core/&lt;name&gt;.json and its
     * programs through the provider; 1.21.2's ShaderManager compiles a program for a key (ShaderProgram: the config id
     * namespace:core/&lt;name&gt;, the format, no defines) from the shader resources it loaded at the last reload, which is
     * where the provider's files come from too. A compile failure is the IOException 1.21.1's constructor threw.
     * A mod shader's json must be in 1.21.2's format (vertex / fragment as namespace:core/... ids) to load: converting
     * 1.21.1-format shader resources is not done here.
     */
    public static net.minecraft.class_5944 create(net.minecraft.class_5912 provider, String name, net.minecraft.class_293 format) throws java.io.IOException {
        int colon = name.indexOf(':');
        net.minecraft.class_2960 id = colon < 0
            ? net.minecraft.class_2960.method_60655("minecraft", "core/" + name)
            : net.minecraft.class_2960.method_60655(name.substring(0, colon), "core/" + name.substring(colon + 1));
        try {
            return net.minecraft.class_310.method_1551().method_62887()
                .method_64062(new net.minecraft.class_10156(id, format, net.minecraft.class_10149.field_53930));
        } catch (net.minecraft.class_10151.class_10152 e) {
            throw new java.io.IOException("shader " + name + ": " + e.getMessage(), e);
        }
    }

    /** 1.21.1 ShaderInstance.CHUNK_OFFSET (instanceFieldGetters): the uniform 1.21.2 names ModelOffset, which its shaders use in its place */
    public static net.minecraft.class_284 field_29482(net.minecraft.class_5944 shader) {
        return shader.field_53139;
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
        if (key == null) {
            key = CbRenderTypes.byName(name);
        }
        return key == null ? null : program(key);
    }
}
