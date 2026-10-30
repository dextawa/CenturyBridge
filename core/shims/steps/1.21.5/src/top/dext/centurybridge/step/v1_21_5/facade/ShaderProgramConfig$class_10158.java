package top.dext.centurybridge.step.v1_21_5.facade;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;

/**
 * 1.21.4's ShaderProgramConfig.Sampler (net/minecraft/class_10157$class_10158, record (String name)), gone in 1.21.5 with
 * ShaderProgramConfig: a 1.21.5 program takes its samplers as names (GlProgram.setupUniforms(List, List&lt;String&gt;),
 * class_5944.method_62900), which rt/CbShaderPrograms hands it from these. Members and CODEC as 1.21.4's (javap -c -p);
 * a plain class with record semantics, its members public (access wideners never reach a CenturyBridge class).
 */
public final class ShaderProgramConfig$class_10158 {
    public static final Codec<ShaderProgramConfig$class_10158> field_53950 = RecordCodecBuilder.create(i -> i.group(
        Codec.STRING.fieldOf("name").forGetter(ShaderProgramConfig$class_10158::comp_3121)
    ).apply(i, ShaderProgramConfig$class_10158::new));

    public final String comp_3121;

    public ShaderProgramConfig$class_10158(String name) {
        this.comp_3121 = name;
    }

    public String comp_3121() {
        return this.comp_3121;
    }

    @Override
    public boolean equals(Object o) {
        return o instanceof ShaderProgramConfig$class_10158 s && java.util.Objects.equals(this.comp_3121, s.comp_3121);
    }

    @Override
    public int hashCode() {
        return java.util.Objects.hashCode(this.comp_3121);
    }

    @Override
    public String toString() {
        return "Sampler[name=" + this.comp_3121 + "]";
    }
}
