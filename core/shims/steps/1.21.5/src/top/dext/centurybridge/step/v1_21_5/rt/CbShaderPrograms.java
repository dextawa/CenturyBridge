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
 * config's values written into the program as 1.21.4's Uniform.setFromConfig and upload wrote them, by GL at the
 * uniform's location (no 1.21.5 Uniform: its setters are gone at 1.21.6, where Uniform became an interface).</li>
 * </ul>
 * 1.21.4's uniform types map onto 1.21.5's UniformType (class_10789): int x1 / x3, float x1 .. x4, matrix4x4. 1.21.5 has no
 * int x2 / x4, matrix2x2 or matrix3x3 uniform: such a uniform is not described to 1.21.5 (logged), as 1.21.5's
 * setupUniforms leaves out an active uniform of a GL type it does not know (method_68695 answers null); its 1.21.4 value
 * is still set.
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
                configs.add(c);
                net.minecraft.class_10789 type = type(c.comp_3123(), c.comp_3124());
                if (type == null) {
                    LOGGER.warn("Uniform {} ({} x{}) has no 1.21.5 uniform type; not described", c.comp_3122(), c.comp_3123(), c.comp_3124());
                    continue;
                }
                descriptions.add(new RenderPipeline.UniformDescription(c.comp_3122(), type));
            }
        }
        List<String> names = new ArrayList<>(samplers.size());
        for (Object s : samplers) {
            names.add(s instanceof ShaderProgramConfig$class_10158 c ? c.comp_3121() : (String) s);
        }
        describeBlocks(shader.method_1270(), descriptions);
        shader.method_62900(descriptions, names);
        if (configs.isEmpty()) {
            return;
        }
        // 1.21.4 gave each config uniform its values when the program was set up (Uniform.setFromConfig, uploaded with
        // the program bound): written here into the program by GL at the uniform's location, with the program bound and
        // the previous one restored -- no 1.21.5 Uniform object (1.21.6 made Uniform an interface of buffer bindings)
        int program = shader.method_1270();
        int previous = org.lwjgl.opengl.GL11.glGetInteger(org.lwjgl.opengl.GL20.GL_CURRENT_PROGRAM);
        com.mojang.blaze3d.opengl.GlStateManager._glUseProgram(program);
        try {
            for (ShaderProgramConfig$class_10159 c : configs) {
                set(program, c.comp_3122(), c.comp_3123(), c.comp_3125(), c.comp_3124());
            }
        } finally {
            com.mojang.blaze3d.opengl.GlStateManager._glUseProgram(previous);
        }
    }

    /**
     * The program's active uniform blocks, described as W's pipelines describe them (UniformType UNIFORM_BUFFER, 1.21.6+;
     * javap of GlProgram.setupUniforms: a described block gets its binding and a Uniform.Ubo, an undescribed one that is
     * not built in is left unbound). 1.21.4's json named no blocks (it had none); W's shader sources, which are what is
     * compiled, declare them. 1.21.5 has no UNIFORM_BUFFER type and its core shaders no blocks: nothing added there.
     */
    private static void describeBlocks(int program, List<RenderPipeline.UniformDescription> descriptions) {
        net.minecraft.class_10789 ubo = null;
        for (net.minecraft.class_10789 t : net.minecraft.class_10789.class.getEnumConstants()) {
            if (t.name().equals("UNIFORM_BUFFER")) {
                ubo = t;
            }
        }
        if (ubo == null) {
            return;
        }
        int blocks = org.lwjgl.opengl.GL20.glGetProgrami(program, org.lwjgl.opengl.GL31.GL_ACTIVE_UNIFORM_BLOCKS);
        for (int i = 0; i < blocks; i++) {
            String name = org.lwjgl.opengl.GL31.glGetActiveUniformBlockName(program, i);
            boolean described = false;
            for (RenderPipeline.UniformDescription d : descriptions) {
                described |= d.name().equals(name);
            }
            if (!described) {
                descriptions.add(new RenderPipeline.UniformDescription(name, ubo));
            }
        }
    }

    /** 1.21.4 Uniform.getTypeFromString(type) with the count, as 1.21.5's UniformType */
    static net.minecraft.class_10789 type(String type, int count) {
        String name = null;
        if ("int".equals(type)) {
            name = count == 1 ? "INT" : count == 3 ? "IVEC3" : null;
        } else if ("float".equals(type)) {
            name = switch (count) {
                case 1 -> "FLOAT";
                case 2 -> "VEC2";
                case 3 -> "VEC3";
                case 4 -> "VEC4";
                default -> null;
            };
        } else if (type != null && type.startsWith("matrix") && type.endsWith("4x4")) {
            name = "MATRIX4X4";
        }
        // by the enum constant's name (1.21.5's UniformType: INT, IVEC3, FLOAT, VEC2, VEC3, VEC4, MATRIX4X4; javap -c of
        // its <clinit>), not its field: 1.21.6 replaced these constants (UNIFORM_BUFFER / TEXEL_BUFFER), and a field
        // reference would dangle once the layer climbs on; there a type this method cannot name is null (left out)
        if (name == null) {
            return null;
        }
        for (net.minecraft.class_10789 t : net.minecraft.class_10789.class.getEnumConstants()) {
            if (t.name().equals(name)) {
                return t;
            }
        }
        return null;
    }

    /**
     * 1.21.4 Uniform.setFromConfig(values, count) and its upload (javap -c 1.21.4 class_284: method_65016, method_1299,
     * method_1300 / method_1303 / method_1301 / method_1296; class_5944.method_62898): max(count, 16) floats, all the
     * first value when one is given, else the values; the uniform's type is getTypeFromString(type) (int 0, float 4,
     * matrix2x2 8, matrix3x3 9, matrix4x4 10) plus count - 1 for a count of 2..4 below matrix, uploaded as int / float
     * vectors of that many components or as a matrix. A name the program has no location for (inactive, or a
     * uniform-block member) is left, as 1.21.4 left a uniform whose location was -1.
     */
    private static void set(int program, String name, String type, List<Float> values, int count) {
        int location = com.mojang.blaze3d.opengl.GlStateManager._glGetUniformLocation(program, name);
        if (location == -1) {
            return;
        }
        int base = "int".equals(type) ? 0 : "float".equals(type) ? 4 : type != null && type.startsWith("matrix")
            ? (type.endsWith("2x2") ? 8 : type.endsWith("3x3") ? 9 : type.endsWith("4x4") ? 10 : -1) : -1;
        if (base == -1) {
            LOGGER.warn("Uniform {}: type {} is no 1.21.4 uniform type; not set", name, type);
            return;
        }
        float[] f = new float[Math.max(count, 16)];
        if (values.size() == 1) {
            Arrays.fill(f, values.getFirst());
        } else {
            for (int i = 0; i < values.size() && i < f.length; i++) {
                f[i] = values.get(i);
            }
        }
        int components = count > 1 && count <= 4 && base < 8 ? count : 1;
        if (base == 0) {
            int[] n = new int[components];
            for (int i = 0; i < n.length; i++) {
                n[i] = (int) f[i];
            }
            switch (components) {
                case 1 -> org.lwjgl.opengl.GL20.glUniform1iv(location, n);
                case 2 -> org.lwjgl.opengl.GL20.glUniform2iv(location, n);
                case 3 -> org.lwjgl.opengl.GL20.glUniform3iv(location, n);
                default -> org.lwjgl.opengl.GL20.glUniform4iv(location, n);
            }
        } else if (base == 4) {
            float[] v = Arrays.copyOf(f, components);
            switch (components) {
                case 1 -> org.lwjgl.opengl.GL20.glUniform1fv(location, v);
                case 2 -> org.lwjgl.opengl.GL20.glUniform2fv(location, v);
                case 3 -> org.lwjgl.opengl.GL20.glUniform3fv(location, v);
                default -> org.lwjgl.opengl.GL20.glUniform4fv(location, v);
            }
        } else if (base == 8) {
            org.lwjgl.opengl.GL20.glUniformMatrix2fv(location, false, Arrays.copyOf(f, 4));
        } else if (base == 9) {
            org.lwjgl.opengl.GL20.glUniformMatrix3fv(location, false, Arrays.copyOf(f, 9));
        } else {
            org.lwjgl.opengl.GL20.glUniformMatrix4fv(location, false, Arrays.copyOf(f, 16));
        }
    }
}
