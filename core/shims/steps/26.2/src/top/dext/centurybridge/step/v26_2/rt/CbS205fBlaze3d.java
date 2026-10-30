package top.dext.centurybridge.step.v26_2.rt;

import com.mojang.blaze3d.GpuFormat;
import com.mojang.blaze3d.opengl.GlConst;
import com.mojang.blaze3d.opengl.GlProgram;
import com.mojang.blaze3d.opengl.GlShaderModule;
import com.mojang.blaze3d.pipeline.BindGroupLayout;
import com.mojang.blaze3d.pipeline.RenderPipeline;
import com.mojang.blaze3d.vertex.VertexFormat;
import com.mojang.blaze3d.vertex.VertexFormatElement;
import java.util.ArrayList;
import java.util.List;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.renderer.ShaderManager;
import top.dext.centurybridge.step.v26_2.facade.VertexFormatElement$Type;

/**
 * 26.1.2's GL program, vertex format and pipeline members that 26.2 replaced (javap -c -p of both client jars).
 * <ul>
 * <li>GlProgram.setupUniforms(uniforms, samplers): 26.2's setupBindGroupLayouts(layouts) runs the same body on
 *     flattenUniforms / flattenSamplers of the layouts (same block binding, Utb and sampler setup per entry), so one layout
 *     holding the uniforms and samplers in order is the same program setup.</li>
 * <li>GlProgram.link(vertex, fragment, format, label): 26.2 links over VertexFormat[] binding each element's name() at
 *     the next index (a null format skipped, a name equal to the previous one not bound again); 26.1.2 bound
 *     getElementAttributeNames() at their indices: the one-format array.</li>
 * <li>VertexFormat.getElementAttributeNames(): 26.1.2's names list, parallel to the elements; 26.2's elements carry
 *     their name.</li>
 * <li>VertexFormat.getOffset(element): 26.1.2 offsetsByElement[element.id()], -1 for an element not in the format;
 *     26.2's elements carry their offset.</li>
 * <li>RenderPipeline.getSamplers() / getUniforms(): 26.2 keeps them per bind group layout; flattened in order as 26.2's
 *     own GlProgram.setupBindGroupLayouts flattens them.</li>
 * <li>VertexFormatElement.count() / type() / normalized(), GlConst.toGl(Type): 26.2's vertex array setup
 *     (VertexArrayCache$Emulated.setupCombinedAttributes) derives them from the element's GpuFormat:
 *     glFormatChannelCount(toGlExternalId(format)), toGlType(format), isFormatNormalized(format). The type is 26.1.2's
 *     constant of that GL type (26.1.2 toGl: FLOAT 5126, UBYTE 5121, BYTE 5120, USHORT 5123, SHORT 5122, UINT 5125, INT
 *     5124); a format of a GL type 26.1.2 had no constant for (half float, packed) has no type (null).</li>
 * </ul>
 */
@Environment(EnvType.CLIENT)
public final class CbS205fBlaze3d {
    private static final int[] GL = {5126, 5121, 5120, 5123, 5122, 5125, 5124};

    private CbS205fBlaze3d() {
    }

    /** 26.1.2 GlProgram.setupUniforms(List&lt;UniformDescription&gt;, List&lt;String&gt;) */
    public static void setupUniforms(GlProgram program, List<BindGroupLayout.UniformDescription> uniforms, List<String> samplers) {
        BindGroupLayout.Builder builder = BindGroupLayout.builder();
        for (BindGroupLayout.UniformDescription u : uniforms) {
            if (u.gpuFormat() != null) {
                builder.withUniform(u.name(), u.type(), u.gpuFormat());
            } else {
                builder.withUniform(u.name(), u.type());
            }
        }
        for (String s : samplers) {
            builder.withSampler(s);
        }
        program.setupBindGroupLayouts(List.of(builder.build()));
    }

    /** 26.1.2 GlProgram.link(vertex, fragment, format, label) */
    public static GlProgram link(GlShaderModule vertex, GlShaderModule fragment, VertexFormat format, String label)
            throws ShaderManager.CompilationException {
        return GlProgram.link(vertex, fragment, new VertexFormat[] {format}, label);
    }

    /** 26.1.2 VertexFormat.getElementAttributeNames() */
    public static List<String> getElementAttributeNames(VertexFormat format) {
        List<VertexFormatElement> elements = format.getElements();
        List<String> names = new ArrayList<>(elements.size());
        for (VertexFormatElement e : elements) {
            names.add(e.name());
        }
        return names;
    }

    /** 26.1.2 VertexFormat.getOffset(element) */
    public static int getOffset(VertexFormat format, VertexFormatElement element) {
        return format.getElements().contains(element) ? element.offset() : -1;
    }

    /** 26.1.2 RenderPipeline.getSamplers() */
    public static List<String> getSamplers(RenderPipeline pipeline) {
        return BindGroupLayout.flattenSamplers(pipeline.getBindGroupLayouts());
    }

    /** 26.1.2 RenderPipeline.getUniforms() */
    public static List<BindGroupLayout.UniformDescription> getUniforms(RenderPipeline pipeline) {
        return BindGroupLayout.flattenUniforms(pipeline.getBindGroupLayouts());
    }

    /** 26.1.2 VertexFormatElement.count() */
    public static int count(VertexFormatElement element) {
        return GlConst.glFormatChannelCount(GlConst.toGlExternalId(element.format()));
    }

    /** 26.1.2 VertexFormatElement.normalized() */
    public static boolean normalized(VertexFormatElement element) {
        return GlConst.isFormatNormalized(element.format());
    }

    /** 26.1.2 VertexFormatElement.type() */
    public static VertexFormatElement$Type type(VertexFormatElement element) {
        GpuFormat format = element.format();
        int gl = GlConst.toGlType(format);
        for (VertexFormatElement$Type t : VertexFormatElement$Type.values()) {
            if (GL[t.ordinal()] == gl) {
                return t;
            }
        }
        return null;
    }

    /** 26.1.2 GlConst.toGl(VertexFormatElement.Type) */
    public static int toGl(VertexFormatElement$Type type) {
        return GL[type.ordinal()];
    }
}
