package top.dext.centurybridge.step.v26_2.rt;

import com.mojang.blaze3d.pipeline.RenderPipeline;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.renderer.BindGroupLayouts;

/**
 * 26.1.2 RenderPipelines.MATRICES_PROJECTION_SNIPPET / FOG_SNIPPET, gone at 26.2 (javap -c -p of both RenderPipelines:
 * the only two snippets 26.2 dropped). 26.1.2 built them as builder().withUniform("DynamicTransforms", UNIFORM_BUFFER)
 * .withUniform("Projection", UNIFORM_BUFFER).buildSnippet() and builder().withUniform("Fog", UNIFORM_BUFFER).buildSnippet();
 * 26.2 holds those uniform sets as the bind group layouts BindGroupLayouts.MATRICES_PROJECTION (DynamicTransforms,
 * Projection) and FOG (Fog), which its own snippets add with withBindGroupLayout (MATRICES_FOG_SNIPPET = GLOBALS +
 * MATRICES_PROJECTION + FOG). Read by the climbed 1.21.11 layer (CbRenderTypeCreate, field_60125) and 26.1 layer code.
 */
@Environment(EnvType.CLIENT)
public final class CbS205gSnippets {
    private static final RenderPipeline.Snippet MATRICES_PROJECTION =
        RenderPipeline.builder().withBindGroupLayout(BindGroupLayouts.MATRICES_PROJECTION).buildSnippet();
    private static final RenderPipeline.Snippet FOG = RenderPipeline.builder().withBindGroupLayout(BindGroupLayouts.FOG).buildSnippet();

    private CbS205gSnippets() {
    }

    /** 26.1.2 RenderPipelines.MATRICES_PROJECTION_SNIPPET */
    public static RenderPipeline.Snippet matricesProjection() {
        return MATRICES_PROJECTION;
    }

    /** 26.1.2 RenderPipelines.FOG_SNIPPET */
    public static RenderPipeline.Snippet fog() {
        return FOG;
    }
}
