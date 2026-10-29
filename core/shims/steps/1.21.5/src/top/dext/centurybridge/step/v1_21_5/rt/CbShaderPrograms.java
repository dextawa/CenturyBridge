package top.dext.centurybridge.step.v1_21_5.rt;

import com.mojang.blaze3d.pipeline.RenderPipeline;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import top.dext.centurybridge.step.v1_21_5.facade.ShaderProgramConfig$class_10158;
import top.dext.centurybridge.step.v1_21_5.facade.ShaderProgramConfig$class_10159;

/**
 * 1.21.4's way of building a compiled shader program from a GL program id (javap -c -p 1.21.4 / 1.21.5 class_5944,
 * class_293 / VertexFormat, class_284), on 1.21.5, which builds one only through GlProgram.link(vertex, fragment, format,
 * label) (class_5944.method_62896). The climbed 1.21.2 layer's 1.21.1 ShaderInstance give-back (CbRenderShaderLoad) links
 * this way on every resource reload.
 * <ul>
 * <li>new CompiledShaderProgram(id) (ctorRedirects): 1.21.5's constructor takes a debug label too (used in its log lines
 * only); the label names the id, as 1.21.4 kept none.</li>
 * <li>VertexFormat.bindAttributes(program) (instanceRedirects): 1.21.4's body, which 1.21.5 inlined into link -- each
 * element's attribute name bound to its index.</li>
 * <li>CompiledShaderProgram.setupUniforms(uniforms, samplers) (instanceRedirects): same name and erased descriptor, but
 * 1.21.5 reads RenderPipeline.UniformDescription / String elements where 1.21.4 read ShaderProgramConfig.Uniform /
 * Sampler (facades): each element converted (one already in 1.21.5's form passes), 1.21.5's setupUniforms run, then each
 * config's values set as 1.21.4's Uniform.setFromConfig set them (max(count, 16) floats filled with the first value, then
 * the values; int uniforms take them truncated).</li>
 * </ul>
 * 1.21.4's uniform types map onto 1.21.5's UniformType (class_10789): int x1 / x3, float x1 .. x4, matrix4x4. 1.21.5 has no
 * int x2 / x4, matrix2x2 or matrix3x3 uniform: such a uniform is left out (logged), as 1.21.5's setupUniforms leaves out an
 * active uniform of a GL type it does not know (method_68695 answers null).
 */
@Environment(EnvType.CLIENT)
public final class CbShaderPrograms {
    private static final org.slf4j.Logger LOGGER = com.mojang.logging.LogUtils.getLogger();

    private CbShaderPrograms() {
    }

    /** 1.21.4 new CompiledShaderProgram(programId) */
    public static net.minecraft.class_5944 program(int id) {
        return new net.minecraft.class_5944(id, "centurybridge:program/" + id);
    }

    /** 1.21.4 VertexFormat.bindAttributes(program) */
    public static void method_62886(com.mojang.blaze3d.vertex.VertexFormat format, int program) {
        int i = 0;
        for (String name : format.getElementAttributeNames()) {
            com.mojang.blaze3d.opengl.GlStateManager._glBindAttribLocation(program, i, name);
            i++;
        }
    }

    /** 1.21.4 CompiledShaderProgram.setupUniforms(List&lt;ShaderProgramConfig.Uniform&gt;, List&lt;ShaderProgramConfig.Sampler&gt;) */
    public static void method_62900(net.minecraft.class_5944 shader, List<?> uniforms, List<?> samplers) {
        List<RenderPipeline.UniformDescription> descriptions = new ArrayList<>(uniforms.size());
        List<ShaderProgramConfig$class_10159> configs = new ArrayList<>();
        for (Object u : uniforms) {
            if (u instanceof RenderPipeline.UniformDescription d) {
                descriptions.add(d);
            } else {
                ShaderProgramConfig$class_10159 c = (ShaderProgramConfig$class_10159) u;
                net.minecraft.class_10789 type = type(c.comp_3123(), c.comp_3124());
                if (type == null) {
                    LOGGER.warn("Uniform {} ({} x{}) has no 1.21.5 uniform type; left out", c.comp_3122(), c.comp_3123(), c.comp_3124());
                    continue;
                }
                descriptions.add(new RenderPipeline.UniformDescription(c.comp_3122(), type));
                configs.add(c);
            }
        }
        List<String> names = new ArrayList<>(samplers.size());
        for (Object s : samplers) {
            names.add(s instanceof ShaderProgramConfig$class_10158 c ? c.comp_3121() : (String) s);
        }
        shader.method_62900(descriptions, names);
        for (ShaderProgramConfig$class_10159 c : configs) {
            net.minecraft.class_284 uniform = shader.method_34582(c.comp_3122());
            if (uniform != null) {
                set(uniform, c.comp_3125(), c.comp_3124());
            }
        }
    }

    /** 1.21.4 Uniform.getTypeFromString(type) with the count, as 1.21.5's UniformType */
    static net.minecraft.class_10789 type(String type, int count) {
        if ("int".equals(type)) {
            return count == 1 ? net.minecraft.class_10789.field_56741 : count == 3 ? net.minecraft.class_10789.field_56742 : null;
        }
        if ("float".equals(type)) {
            return switch (count) {
                case 1 -> net.minecraft.class_10789.field_56743;
                case 2 -> net.minecraft.class_10789.field_56744;
                case 3 -> net.minecraft.class_10789.field_56745;
                case 4 -> net.minecraft.class_10789.field_56746;
                default -> null;
            };
        }
        return type != null && type.startsWith("matrix") && type.endsWith("4x4") ? net.minecraft.class_10789.field_56747 : null;
    }

    /** 1.21.4 Uniform.setFromConfig(values, count) */
    private static void set(net.minecraft.class_284 uniform, List<Float> values, int count) {
        float[] f = new float[Math.max(count, 16)];
        if (!values.isEmpty()) {
            Arrays.fill(f, values.getFirst());
        }
        for (int i = 0; i < values.size() && i < f.length; i++) {
            f[i] = values.get(i);
        }
        // 1.21.4 set as many values as the uniform held; 1.21.5's Uniform.set(float[]) / set(int[]) put the whole
        // array into a buffer of exactly the type's size (javap: FloatBuffer.put(float[]) after a too-small check)
        net.minecraft.class_10789 type = uniform.method_35662();
        f = Arrays.copyOf(f, type.method_68449());
        if (type == net.minecraft.class_10789.field_56741 || type == net.minecraft.class_10789.field_56742) {
            int[] n = new int[f.length];
            for (int i = 0; i < n.length; i++) {
                n[i] = (int) f[i];
            }
            uniform.method_68344(n);
        } else {
            uniform.method_1253(f);
        }
    }
}
