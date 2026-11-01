package top.dext.centurybridge.step.v1_21_5.facade;

import com.mojang.blaze3d.vertex.VertexFormat;
import net.minecraft.class_10149;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.class_2960;

/**
 * 1.21.4's net.minecraft.client.renderer.ShaderProgram (net/minecraft/class_10156, record (ResourceLocation configId,
 * VertexFormat vertexFormat, ShaderDefines defines)), gone in 1.21.5: a 1.21.5 program is described by a RenderPipeline
 * (vertex / fragment shader ids, defines, vertex format). Converted references name this class (classRenames); it is pure
 * data, as 1.21.4's was -- what compiles it is the caller's (RenderSystem.setShader in rt/CbGlRenderSystem, a 1.21.4
 * render type's pipeline in rt/CbRenderTypes). Members as 1.21.4's (javap -c -p class_10156): the canonical constructor,
 * the three accessors, ObjectMethods equals / hashCode, and toString = "configId (vertexFormat)", followed by
 * " with defines" when the defines are not empty (class_10149.method_62930).
 */
@Environment(EnvType.CLIENT)
public final class ShaderProgram {
    // 1.21.4's record fields (private there, reached by mods through their access wideners); public here because access
    // wideners never reach a CenturyBridge class. A final class, not a record, since a record's fields cannot be public.
    /** configId */
    public final class_2960 comp_3113;
    /** vertexFormat */
    public final VertexFormat comp_3114;
    /** defines */
    public final class_10149 comp_3115;

    public ShaderProgram(class_2960 comp_3113, VertexFormat comp_3114, class_10149 comp_3115) {
        this.comp_3113 = comp_3113;
        this.comp_3114 = comp_3114;
        this.comp_3115 = comp_3115;
    }

    public class_2960 comp_3113() {
        return this.comp_3113;
    }

    public VertexFormat comp_3114() {
        return this.comp_3114;
    }

    public class_10149 comp_3115() {
        return this.comp_3115;
    }

    /** the record's component-wise equality (ObjectMethods) */
    @Override
    public boolean equals(Object o) {
        return this == o || o instanceof ShaderProgram p && java.util.Objects.equals(this.comp_3113, p.comp_3113)
            && java.util.Objects.equals(this.comp_3114, p.comp_3114) && java.util.Objects.equals(this.comp_3115, p.comp_3115);
    }

    @Override
    public int hashCode() {
        return java.util.Objects.hash(this.comp_3113, this.comp_3114, this.comp_3115);
    }

    @Override
    public String toString() {
        String s = this.comp_3113 + " (" + this.comp_3114 + ")";
        return this.comp_3115.method_62930() ? s : s + " with " + this.comp_3115;
    }
}
