package top.dext.centurybridge.step.v1_21_5.facade;

import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import java.util.List;

/**
 * 1.21.4's ShaderProgramConfig.Uniform (net/minecraft/class_10157$class_10159, record (String name, String type, int count,
 * List&lt;Float&gt; values)), gone in 1.21.5 with ShaderProgramConfig: a 1.21.5 program takes its uniforms as
 * RenderPipeline.UniformDescription (name, UniformType) and sets no values from a config; rt/CbShaderPrograms turns these
 * into descriptions and sets the values as 1.21.4's Uniform.setFromConfig did. Members, CODEC and its count check as
 * 1.21.4's (javap -c -p: values must number count, or at most one); a plain class with record semantics, its members
 * public (access wideners never reach a CenturyBridge class).
 */
public final class ShaderProgramConfig$class_10159 {
    public static final Codec<ShaderProgramConfig$class_10159> field_53951 = RecordCodecBuilder.<ShaderProgramConfig$class_10159>create(i -> i.group(
        Codec.STRING.fieldOf("name").forGetter(ShaderProgramConfig$class_10159::comp_3122),
        Codec.STRING.fieldOf("type").forGetter(ShaderProgramConfig$class_10159::comp_3123),
        Codec.INT.fieldOf("count").forGetter(ShaderProgramConfig$class_10159::comp_3124),
        Codec.FLOAT.listOf().fieldOf("values").forGetter(ShaderProgramConfig$class_10159::comp_3125)
    ).apply(i, ShaderProgramConfig$class_10159::new)).validate(ShaderProgramConfig$class_10159::method_62962);

    public final String comp_3122;
    public final String comp_3123;
    public final int comp_3124;
    public final List<Float> comp_3125;

    public ShaderProgramConfig$class_10159(String name, String type, int count, List<Float> values) {
        this.comp_3122 = name;
        this.comp_3123 = type;
        this.comp_3124 = count;
        this.comp_3125 = values;
    }

    public String comp_3122() {
        return this.comp_3122;
    }

    public String comp_3123() {
        return this.comp_3123;
    }

    public int comp_3124() {
        return this.comp_3124;
    }

    public List<Float> comp_3125() {
        return this.comp_3125;
    }

    public static DataResult<ShaderProgramConfig$class_10159> method_62962(ShaderProgramConfig$class_10159 uniform) {
        int count = uniform.comp_3124;
        int size = uniform.comp_3125.size();
        return size != count && size > 1 ? DataResult.error(() -> method_62960(count, size)) : DataResult.success(uniform);
    }

    public static String method_62960(int count, int size) {
        return "Invalid amount of uniform values specified (expected " + count + ", found " + size + ")";
    }

    @Override
    public boolean equals(Object o) {
        return o instanceof ShaderProgramConfig$class_10159 u && this.comp_3124 == u.comp_3124 && java.util.Objects.equals(this.comp_3122, u.comp_3122)
            && java.util.Objects.equals(this.comp_3123, u.comp_3123) && java.util.Objects.equals(this.comp_3125, u.comp_3125);
    }

    @Override
    public int hashCode() {
        return java.util.Objects.hash(this.comp_3122, this.comp_3123, this.comp_3124, this.comp_3125);
    }

    @Override
    public String toString() {
        return "Uniform[name=" + this.comp_3122 + ", type=" + this.comp_3123 + ", count=" + this.comp_3124 + ", values=" + this.comp_3125 + "]";
    }
}
