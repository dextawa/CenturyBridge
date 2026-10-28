package top.dext.centurybridge.step.v1_20_5.rt;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

/**
 * 1.20.4's GameRenderer shader statics on 1.20.5 (s4client): the core shaders position_tex_color_normal and
 * position_tex_lightmap_color, which 1.20.5 no longer ships or loads, and the post-effect cycle (cycleEffect,
 * EFFECT_NONE). The two shaders are loaded with the game's others (mixin/CbClientGameRendererShadersMixin) from
 * their 1.20.4 definitions -- programs given to the shader loader here, with 1.20.4's fog functions written into
 * them -- with 1.20.4's vertex formats.
 */
@Environment(EnvType.CLIENT)
public final class CbClientShaders {

    private CbClientShaders() {
    }

    /** 1.20.4 GameRenderer's positionTexColorNormalShader */
    public static net.minecraft.class_5944 positionTexColorNormal;
    /** 1.20.4 GameRenderer's positionTexLightmapColorShader */
    public static net.minecraft.class_5944 positionTexLightmapColor;

    /** 1.20.4 GameRenderer.getPositionTexColorNormalShader() */
    public static net.minecraft.class_5944 method_34549() {
        return positionTexColorNormal;
    }

    /** 1.20.4 GameRenderer.getPositionTexLightmapColorShader() */
    public static net.minecraft.class_5944 method_35764() {
        return positionTexLightmapColor;
    }

    /**
     * 1.20.4 GameRenderer.SHADERS: the post effects cycleEffect() steps through. 1.20.5 ships only creeper,
     * spider, invert and blur of them; the others load from a resource pack that has them and otherwise fail
     * as any missing effect does (logged, no effect active), as 1.20.4's loadEffect handled a missing one.
     */
    public static final String[] SHADERS = {
        "notch", "fxaa", "art", "bumpy", "blobs2", "pencil", "color_convolve", "deconverge", "flip", "invert", "ntsc", "outline",
        "phosphor", "scan_pincushion", "sobel", "bits", "desaturate", "green", "blur", "wobble", "blobs", "antialias", "creeper", "spider"
    };

    /** 1.20.4 GameRenderer.EFFECT_NONE: SHADERS.length */
    public static int field_4010() {
        return SHADERS.length;
    }

    public static net.minecraft.class_2960 shader(int index) {
        return new net.minecraft.class_2960("shaders/post/" + SHADERS[index] + ".json");
    }

    // ------------------------------------------------------------------ the two core shaders

    private static final String BLEND = "\"blend\": { \"func\": \"add\", \"srcrgb\": \"srcalpha\", \"dstrgb\": \"1-srcalpha\" },";
    private static final String IDENTITY = "[ 1.0, 0.0, 0.0, 0.0, 0.0, 1.0, 0.0, 0.0, 0.0, 0.0, 1.0, 0.0, 0.0, 0.0, 0.0, 1.0 ]";

    /*
     * The two fog functions these 1.20.4 programs use, as 1.20.4's shaders/include/fog.glsl defined them, written into the
     * programs instead of imported. The game's fog.glsl is another file from 1.20.5 on (read from each client jar):
     * 1.20.5 - 1.21.5 declare fog_distance(vec3 pos, int shape), measuring the camera-relative position without the
     * model-view rotation, and no fog_distance(mat4, vec3, int), so the imported program failed to compile and the
     * shader reload threw; 1.21.6 on have neither fog_distance nor linear_fog (apply_fog over a Fog uniform block).
     * With 1.20.4's own bodies the programs compute 1.20.4's fog wherever they load.
     */
    /** 1.20.4 fog.glsl fog_distance(modelViewMat, pos, shape) */
    private static final String FOG_DISTANCE = String.join("\n",
        "float fog_distance(mat4 modelViewMat, vec3 pos, int shape) {",
        "    if (shape == 0) {",
        "        return length((modelViewMat * vec4(pos, 1.0)).xyz);",
        "    } else {",
        "        float distXZ = length((modelViewMat * vec4(pos.x, 0.0, pos.z, 1.0)).xyz);",
        "        float distY = length((modelViewMat * vec4(0.0, pos.y, 0.0, 1.0)).xyz);",
        "        return max(distXZ, distY);",
        "    }",
        "}");
    /** 1.20.4 fog.glsl linear_fog(inColor, vertexDistance, fogStart, fogEnd, fogColor) */
    private static final String LINEAR_FOG = String.join("\n",
        "vec4 linear_fog(vec4 inColor, float vertexDistance, float fogStart, float fogEnd, vec4 fogColor) {",
        "    if (vertexDistance <= fogStart) {",
        "        return inColor;",
        "    }",
        "    float fogValue = vertexDistance < fogEnd ? smoothstep(fogStart, fogEnd, vertexDistance) : 1.0;",
        "    return vec4(mix(inColor.rgb, fogColor.rgb, fogValue * fogColor.a), inColor.a);",
        "}");

    private static final java.util.Map<String, String> SOURCES = java.util.Map.of(
        "shaders/core/position_tex_color_normal.json", "{ " + BLEND
            + " \"vertex\": \"position_tex_color_normal\", \"fragment\": \"position_tex_color_normal\","
            + " \"attributes\": [ \"Position\", \"UV0\", \"Color\", \"Normal\" ],"
            + " \"samplers\": [ { \"name\": \"Sampler0\" } ],"
            + " \"uniforms\": ["
            + " { \"name\": \"ModelViewMat\", \"type\": \"matrix4x4\", \"count\": 16, \"values\": " + IDENTITY + " },"
            + " { \"name\": \"ProjMat\", \"type\": \"matrix4x4\", \"count\": 16, \"values\": " + IDENTITY + " },"
            + " { \"name\": \"ColorModulator\", \"type\": \"float\", \"count\": 4, \"values\": [ 1.0, 1.0, 1.0, 1.0 ] },"
            + " { \"name\": \"FogStart\", \"type\": \"float\", \"count\": 1, \"values\": [ 0.0 ] },"
            + " { \"name\": \"FogEnd\", \"type\": \"float\", \"count\": 1, \"values\": [ 1.0 ] },"
            + " { \"name\": \"FogColor\", \"type\": \"float\", \"count\": 4, \"values\": [ 0.0, 0.0, 0.0, 0.0 ] },"
            + " { \"name\": \"FogShape\", \"type\": \"int\", \"count\": 1, \"values\": [ 0 ] } ] }",
        "shaders/core/position_tex_color_normal.vsh", String.join("\n",
            "#version 150",
            FOG_DISTANCE,
            "in vec3 Position;", "in vec2 UV0;", "in vec4 Color;", "in vec3 Normal;",
            "uniform mat4 ModelViewMat;", "uniform mat4 ProjMat;", "uniform int FogShape;",
            "out vec2 texCoord0;", "out float vertexDistance;", "out vec4 vertexColor;", "out vec4 normal;",
            "void main() {",
            "    gl_Position = ProjMat * ModelViewMat * vec4(Position, 1.0);",
            "    texCoord0 = UV0;",
            "    vertexDistance = fog_distance(ModelViewMat, Position, FogShape);",
            "    vertexColor = Color;",
            "    normal = ProjMat * ModelViewMat * vec4(Normal, 0.0);",
            "}", ""),
        "shaders/core/position_tex_color_normal.fsh", String.join("\n",
            "#version 150",
            LINEAR_FOG,
            "uniform sampler2D Sampler0;", "uniform vec4 ColorModulator;", "uniform float FogStart;", "uniform float FogEnd;",
            "uniform vec4 FogColor;",
            "in vec2 texCoord0;", "in float vertexDistance;", "in vec4 vertexColor;", "in vec4 normal;",
            "out vec4 fragColor;",
            "void main() {",
            "    vec4 color = texture(Sampler0, texCoord0) * vertexColor * ColorModulator;",
            "    if (color.a < 0.1) {", "        discard;", "    }",
            "    fragColor = linear_fog(color, vertexDistance, FogStart, FogEnd, FogColor);",
            "}", ""),
        "shaders/core/position_tex_lightmap_color.json", "{ " + BLEND
            + " \"vertex\": \"position_tex_lightmap_color\", \"fragment\": \"position_tex_lightmap_color\","
            + " \"attributes\": [ \"Position\", \"UV0\", \"UV2\", \"Color\" ],"
            + " \"samplers\": [ { \"name\": \"Sampler0\" } ],"
            + " \"uniforms\": ["
            + " { \"name\": \"ModelViewMat\", \"type\": \"matrix4x4\", \"count\": 16, \"values\": " + IDENTITY + " },"
            + " { \"name\": \"ProjMat\", \"type\": \"matrix4x4\", \"count\": 16, \"values\": " + IDENTITY + " },"
            + " { \"name\": \"ColorModulator\", \"type\": \"float\", \"count\": 4, \"values\": [ 1.0, 1.0, 1.0, 1.0 ] } ] }",
        "shaders/core/position_tex_lightmap_color.vsh", String.join("\n",
            "#version 150",
            "in vec3 Position;", "in vec2 UV0;", "in vec2 UV2;", "in vec4 Color;",
            "uniform mat4 ModelViewMat;", "uniform mat4 ProjMat;",
            "out vec2 texCoord0;", "out vec2 texCoord2;", "out vec4 vertexColor;",
            "void main() {",
            "    gl_Position = ProjMat * ModelViewMat * vec4(Position, 1.0);",
            "    texCoord0 = UV0;", "    texCoord2 = UV2;", "    vertexColor = Color;",
            "}", ""),
        "shaders/core/position_tex_lightmap_color.fsh", String.join("\n",
            "#version 150",
            "uniform sampler2D Sampler0;", "uniform vec4 ColorModulator;",
            "in vec2 texCoord0;", "in vec2 texCoord2;", "in vec4 vertexColor;",
            "out vec4 fragColor;",
            "void main() {",
            "    vec4 color = texture(Sampler0, texCoord0) * vertexColor;",
            "    if (color.a < 0.1) {", "        discard;", "    }",
            "    fragColor = color * ColorModulator;",
            "}", ""));

    /**
     * The game's resources with the two shaders' definitions added, unless a resource pack provides its own
     * (a pack's file wins, as a pack's shader overrode 1.20.4's built-in one)
     */
    private static net.minecraft.class_5912 withSources(net.minecraft.class_5912 game) {
        return location -> {
            java.util.Optional<net.minecraft.class_3298> found = game.method_14486(location);
            if (found.isPresent() || !"minecraft".equals(location.method_12836())) {
                return found;
            }
            String source = SOURCES.get(location.method_12832());
            if (source == null) {
                return found;
            }
            net.minecraft.class_3262 pack = game.method_14486(new net.minecraft.class_2960("shaders/include/fog.glsl"))
                .map(net.minecraft.class_3298::method_45304).orElse(null);
            byte[] bytes = source.getBytes(java.nio.charset.StandardCharsets.UTF_8);
            return java.util.Optional.of(new net.minecraft.class_3298(pack, () -> new java.io.ByteArrayInputStream(bytes)));
        };
    }

    /** after 1.20.5's reloadShaders(resources): the two loaded again, the previous ones closed (1.20.4 reloaded them with the rest) */
    public static void reload(net.minecraft.class_5912 resources) {
        net.minecraft.class_5912 sources = withSources(resources);
        net.minecraft.class_5944 normal = null;
        try {
            normal = new net.minecraft.class_5944(sources, "position_tex_color_normal", net.minecraft.class_290.field_1577);
            net.minecraft.class_5944 lightmap = new net.minecraft.class_5944(sources, "position_tex_lightmap_color", net.minecraft.class_290.field_1586);
            if (positionTexColorNormal != null) {
                positionTexColorNormal.close();
            }
            if (positionTexLightmapColor != null) {
                positionTexLightmapColor.close();
            }
            positionTexColorNormal = normal;
            positionTexLightmapColor = lightmap;
        } catch (java.io.IOException e) {
            if (normal != null) {
                normal.close();
            }
            throw new RuntimeException("could not reload shaders", e);
        }
    }
}
