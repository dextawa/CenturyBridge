package top.dext.centurybridge.step.v1_20_3.rt;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Map;
import java.util.Optional;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

/**
 * 1.20.2's translucent-no-crumbling rendering on 1.20.3, which dropped it: the core shader
 * rendertype_translucent_no_crumbling (GameRenderer.getRendertypeTranslucentNoCrumblingShader), its shader
 * state (RenderStateShard.RENDERTYPE_TRANSLUCENT_NO_CRUMBLING_SHADER) and the render type
 * (RenderType.translucentNoCrumbling()); the shader state and the type are {@link CbNoCrumblingRenderType}'s,
 * which as a RenderStateShard subclass reaches the protected shard classes.
 * <p>
 * The shader is loaded with the core shaders on every resource reload (the GameRenderer mixin), from the
 * resource packs when one provides it -- as 1.20.2 loaded it -- and otherwise from the program below:
 * 1.20.2's, which 1.20.3's assets no longer carry -- position by the model-view and projection matrices,
 * colour = texture(Sampler0) x vertex colour x ColorModulator, over the block vertex format, no fog, no
 * lightmap sampling.
 */
@Environment(EnvType.CLIENT)
public final class CbRender {

    private static final String NAME = "rendertype_translucent_no_crumbling";

    private static final String JSON = """
        {
            "blend": { "func": "add", "srcrgb": "srcalpha", "dstrgb": "1-srcalpha" },
            "vertex": "rendertype_translucent_no_crumbling",
            "fragment": "rendertype_translucent_no_crumbling",
            "attributes": [ "Position", "Color", "UV0", "UV2", "Normal" ],
            "samplers": [ { "name": "Sampler0" } ],
            "uniforms": [
                { "name": "ModelViewMat", "type": "matrix4x4", "count": 16, "values": [ 1.0, 0.0, 0.0, 0.0, 0.0, 1.0, 0.0, 0.0, 0.0, 0.0, 1.0, 0.0, 0.0, 0.0, 0.0, 1.0 ] },
                { "name": "ProjMat", "type": "matrix4x4", "count": 16, "values": [ 1.0, 0.0, 0.0, 0.0, 0.0, 1.0, 0.0, 0.0, 0.0, 0.0, 1.0, 0.0, 0.0, 0.0, 0.0, 1.0 ] },
                { "name": "ColorModulator", "type": "float", "count": 4, "values": [ 1.0, 1.0, 1.0, 1.0 ] }
            ]
        }
        """;

    private static final String VERTEX = """
        #version 150

        in vec3 Position;
        in vec4 Color;
        in vec2 UV0;
        in vec2 UV2;
        in vec3 Normal;

        uniform mat4 ModelViewMat;
        uniform mat4 ProjMat;

        out vec4 vertexColor;
        out vec2 texCoord0;
        out vec2 texCoord2;
        out vec4 normal;

        void main() {
            gl_Position = ProjMat * ModelViewMat * vec4(Position, 1.0);
            vertexColor = Color;
            texCoord0 = UV0;
            texCoord2 = UV2;
            normal = ProjMat * ModelViewMat * vec4(Normal, 0.0);
        }
        """;

    private static final String FRAGMENT = """
        #version 150

        uniform sampler2D Sampler0;
        uniform vec4 ColorModulator;

        in vec4 vertexColor;
        in vec2 texCoord0;
        in vec2 texCoord2;
        in vec4 normal;

        out vec4 fragColor;

        void main() {
            vec4 color = texture(Sampler0, texCoord0) * vertexColor;
            fragColor = color * ColorModulator;
        }
        """;

    private static final Map<String, String> SOURCES = Map.of(
        "shaders/core/" + NAME + ".json", JSON,
        "shaders/core/" + NAME + ".vsh", VERTEX,
        "shaders/core/" + NAME + ".fsh", FRAGMENT);

    private static net.minecraft.class_5944 shader;

    private static net.minecraft.class_1921 renderType;

    /** was GameRenderer.getRendertypeTranslucentNoCrumblingShader() (method_34500) */
    public static net.minecraft.class_5944 method_34500() {
        return shader;
    }

    /** was RenderType.translucentNoCrumbling() (method_23585) */
    public static net.minecraft.class_1921 method_23585() {
        if (renderType == null) {
            renderType = new CbNoCrumblingRenderType();
        }
        return renderType;
    }

    /**
     * GameRenderer.reloadShaders(provider) has just loaded the core shaders (and closed every shader program,
     * this one's included): load this one again, from the packs first. A failure fails the reload, as a core
     * shader's failure did in 1.20.2.
     */
    public static void cbReloadShaders(net.minecraft.class_5912 provider) {
        net.minecraft.class_5912 withBuiltin = location -> provider.method_14486(location).or(() -> builtin(location));
        net.minecraft.class_5944 old = shader;
        try {
            shader = new net.minecraft.class_5944(withBuiltin, NAME, net.minecraft.class_290.field_1590);
        } catch (IOException e) {
            shader = null;
            throw new RuntimeException("could not reload shaders", e);
        } finally {
            if (old != null) {
                old.close();
            }
        }
    }

    private static Optional<net.minecraft.class_3298> builtin(net.minecraft.class_2960 location) {
        String text = location.method_12836().equals("minecraft") ? SOURCES.get(location.method_12832()) : null;
        if (text == null) {
            return Optional.empty();
        }
        byte[] bytes = text.getBytes(StandardCharsets.UTF_8);
        return Optional.of(new net.minecraft.class_3298(net.minecraft.class_310.method_1551().method_45573(),
            () -> new ByteArrayInputStream(bytes)));
    }

    private CbRender() {
    }
}
