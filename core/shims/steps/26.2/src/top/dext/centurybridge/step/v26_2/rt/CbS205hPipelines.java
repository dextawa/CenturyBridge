package top.dext.centurybridge.step.v26_2.rt;

import com.mojang.blaze3d.PrimitiveTopology;
import com.mojang.blaze3d.pipeline.BindGroupLayout;
import com.mojang.blaze3d.pipeline.RenderPipeline;
import com.mojang.blaze3d.shaders.UniformType;
import com.mojang.blaze3d.vertex.VertexFormat;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

/**
 * 26.1.2 RenderPipeline.Builder.withVertexFormat(VertexFormat, VertexFormat.Mode), gone at 26.2 (javap -c -p of both
 * builders). 26.1.2 stored one format and one mode (vertexFormat = Optional.of(format), vertexFormatMode =
 * Optional.of(mode)); 26.2 keeps a format per vertex buffer (vertexFormatPerBuffer[16], withVertexBinding(index, format))
 * and the mode as primitiveTopology (withPrimitiveTopology; VertexFormat.Mode is 26.2's PrimitiveTopology, a step class
 * rename). 26.2's own single-format pipelines (RenderPipelines.<clinit>) bind their format at buffer 0 then set the
 * topology, which is what this does; the snippet merge (withSnippet) carries both the same way.
 */
@Environment(EnvType.CLIENT)
public final class CbS205hPipelines {
    private CbS205hPipelines() {
    }

    /** 26.1.2 RenderPipeline.Builder.withVertexFormat(format, mode) */
    public static RenderPipeline.Builder withVertexFormat(RenderPipeline.Builder builder, VertexFormat format, PrimitiveTopology mode) {
        return builder.withVertexBinding(0, format).withPrimitiveTopology(mode);
    }

    /**
     * 26.1.2 RenderPipeline.Builder.withSampler(name), gone at 26.2: 26.1.2 appended the name to the builder's sampler
     * list; 26.2 declares samplers only through bind group layouts (BindGroupLayout.Builder.withSampler), and the
     * pipeline's samplers are BindGroupLayout.flattenSamplers of its layouts in the order added. 26.2's own
     * BindGroupLayouts.SAMPLER0 / SAMPLER1 / SAMPLER2 are exactly one-sampler layouts built this way, so each old call
     * adds one such layout and the flattened order stays the order the mod named them.
     */
    public static RenderPipeline.Builder withSampler(RenderPipeline.Builder builder, String name) {
        return builder.withBindGroupLayout(BindGroupLayout.builder().withSampler(name).build());
    }

    /**
     * 26.1.2 RenderPipeline.Builder.withUniform(name, type), gone at 26.2: 26.2 declares uniforms only through bind group
     * layouts (BindGroupLayout.Builder.withUniform), and the pipeline's uniforms are BindGroupLayout.flattenUniforms of its
     * layouts in the order added; each old call adds one one-uniform layout, as withSampler does for samplers.
     */
    public static RenderPipeline.Builder withUniform(RenderPipeline.Builder builder, String name, UniformType type) {
        return builder.withBindGroupLayout(BindGroupLayout.builder().withUniform(name, type).build());
    }
}
