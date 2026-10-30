package top.dext.centurybridge.step.v1_21_5.rt;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.mojang.blaze3d.pipeline.RenderPipeline;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

/**
 * The client half of {@link CbShaderJsons}: a 1.21.4 core shader json built from the 1.21.5 RenderPipeline of that program.
 *
 * <p>Which pipeline: 1.21.5 named its pipelines after 1.21.4's core shader names (rendertype_X -> pipeline/X, every other
 * name -> pipeline/name: lightmap, rendertype_entity_cutout, ...), so the pipeline at that location is taken; a name with
 * no pipeline of its own (position_tex_color, used by pipeline/gui_textured, end_sky, celestial, ...) takes the pipelines
 * whose vertex and fragment shader are both core/&lt;name&gt;, when they all describe the program alike. Checked against
 * the 56 1.21.4 jsons by simulating class_10799's &lt;clinit&gt; (javap -c): 41 identical; 7 differ only in sampler order
 * (1.21.5 lists Sampler2 before Sampler1; GL binds by name); breeze_wind, end_portal and end_gateway differ because
 * 1.21.5's own .vsh / .fsh, which are what gets compiled, changed (EndPortalLayers became the PORTAL_LAYERS define, fog
 * uniforms added) -- 1.21.5's description is the one that fits 1.21.5's sources. position_color_lightmap,
 * position_color_tex_lightmap, rendertype_armor_entity_glint, rendertype_entity_glint and rendertype_glint_translucent
 * have no 1.21.5 pipeline: no description (W's FileNotFoundException stays).
 *
 * <p>The json: vertex / fragment = the pipeline's shader ids, samplers = getSamplers(), defines = getShaderDefines() in
 * 1.21.4's codec form (ShaderDefines.CODEC), uniforms = getUniforms() with 1.21.4's type / count (UniformType: int, ivec3,
 * float, vec2..vec4, matrix4x4) and 1.21.4's values for that uniform name ({@link #defaultValues}); a uniform 1.21.4 gave
 * no single value for gives no description (logged) rather than a value made up here.
 */
@Environment(EnvType.CLIENT)
public final class CbShaderJsonsClient {
    private static final org.slf4j.Logger LOGGER = com.mojang.logging.LogUtils.getLogger();

    private CbShaderJsonsClient() {
    }

    /** the 1.21.4 json of core shader &lt;name&gt;, or null when no 1.21.5 pipeline describes it */
    static String describe(String name) {
        String location = name.startsWith("rendertype_") ? name.substring("rendertype_".length()) : name;
        net.minecraft.class_2960 own = net.minecraft.class_2960.method_60655("minecraft", "pipeline/" + location);
        net.minecraft.class_2960 core = net.minecraft.class_2960.method_60655("minecraft", "core/" + name);
        String byLocation = null;
        String byShader = null;
        boolean ambiguous = false;
        for (RenderPipeline p : net.minecraft.class_10799.method_67886()) {
            if (p.getLocation().equals(own)) {
                byLocation = json(name, p);
                if (byLocation == null) {
                    return null;
                }
            } else if (core.equals(p.getVertexShader()) && core.equals(p.getFragmentShader())) {
                String j = json(name, p);
                if (j == null) {
                    return null;
                }
                if (byShader != null && !byShader.equals(j)) {
                    ambiguous = true;
                }
                byShader = j;
            }
        }
        if (byLocation != null) {
            return byLocation;
        }
        if (ambiguous) {
            LOGGER.warn("Core shader {}: the 1.21.5 pipelines using core/{} describe it differently; no 1.21.4 json", name, name);
            return null;
        }
        return byShader;
    }

    private static String json(String name, RenderPipeline p) {
        JsonObject o = new JsonObject();
        o.addProperty("vertex", p.getVertexShader().toString());
        o.addProperty("fragment", p.getFragmentShader().toString());
        JsonArray samplers = new JsonArray();
        for (String s : p.getSamplers()) {
            JsonObject so = new JsonObject();
            so.addProperty("name", s);
            samplers.add(so);
        }
        o.add("samplers", samplers);
        JsonArray uniforms = new JsonArray();
        for (RenderPipeline.UniformDescription u : p.getUniforms()) {
            // UniformType by its enum constant name (1.21.5: INT, IVEC3, FLOAT, VEC2, VEC3, VEC4, MATRIX4X4; javap -c of
            // its <clinit>), not by field: 1.21.6 replaced these constants (UNIFORM_BUFFER / TEXEL_BUFFER), and a
            // type this layer cannot name gives no description instead of a dangling field once the layer climbs on
            String type;
            int count;
            net.minecraft.class_10789 t = u.type();
            switch (t == null ? "" : t.name()) {
                case "INT" -> { type = "int"; count = 1; }
                case "IVEC3" -> { type = "int"; count = 3; }
                case "FLOAT" -> { type = "float"; count = 1; }
                case "VEC2" -> { type = "float"; count = 2; }
                case "VEC3" -> { type = "float"; count = 3; }
                case "VEC4" -> { type = "float"; count = 4; }
                case "MATRIX4X4" -> { type = "matrix4x4"; count = 16; }
                default -> {
                    LOGGER.warn("Core shader {} (pipeline {}): uniform {} of type {} has no 1.21.4 form; no 1.21.4 json", name, p.getLocation(), u.name(), t);
                    return null;
                }
            }
            float[] values = defaultValues(u.name());
            if (values == null) {
                LOGGER.warn("Core shader {} (pipeline {}): uniform {} has no 1.21.4 default value; no 1.21.4 json", name, p.getLocation(), u.name());
                return null;
            }
            JsonObject uo = new JsonObject();
            uo.addProperty("name", u.name());
            uo.addProperty("type", type);
            uo.addProperty("count", count);
            JsonArray va = new JsonArray();
            for (float v : values) {
                va.add(v);
            }
            uo.add("values", va);
            uniforms.add(uo);
        }
        o.add("uniforms", uniforms);
        net.minecraft.class_10149 defines = p.getShaderDefines();
        if (!defines.method_62930()) {
            o.add("defines", net.minecraft.class_10149.field_53931.encodeStart(com.mojang.serialization.JsonOps.INSTANCE, defines).getOrThrow());
        }
        return o.toString();
    }

    private static final float[] IDENTITY = {1, 0, 0, 0, 0, 1, 0, 0, 0, 0, 1, 0, 0, 0, 0, 1};

    /**
     * The values 1.21.4's core shader jsons gave a uniform of that name: every json naming it gives the same values
     * (assets/minecraft/shaders/core/*.json of the 1.21.4 client jar, all 56 read). EndPortalLayers (15 in end_portal,
     * 16 in end_gateway) has no single value and 1.21.5's pipelines no longer have it.
     */
    static float[] defaultValues(String uniform) {
        return switch (uniform) {
            case "ModelViewMat", "ProjMat", "TextureMat" -> IDENTITY.clone();
            case "ColorModulator" -> new float[] {1, 1, 1, 1};
            case "FogColor" -> new float[] {0, 0, 0, 0};
            case "Light0_Direction", "Light1_Direction", "ModelOffset" -> new float[] {0, 0, 0};
            case "ScreenSize" -> new float[] {1, 1};
            case "SkyLightColor", "AmbientLightFactor", "BlockFactor", "BrightnessFactor", "FogEnd", "GlintAlpha", "LineWidth",
                 "SkyFactor" -> new float[] {1};
            case "DarkenWorldFactor", "DarknessScale", "FogShape", "FogStart", "GameTime", "NightVisionFactor",
                 "UseBrightLightmap" -> new float[] {0};
            default -> null;
        };
    }
}
