package top.dext.centurybridge.step.v26_1.mixin;

import com.mojang.blaze3d.pipeline.RenderPipeline;
import net.minecraft.client.renderer.RenderPipelines;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

/**
 * The 26.1 RenderPipelines snippets that have a 1.21.11 namesake, for their 1.21.11 depth and colour view
 * (rt/CbWallPipeline gives them their 1.21.11 view: 26.1 leaves the depth state out where 1.21.11 said
 * NO_DEPTH_TEST, and sets DepthStencilState.DEFAULT where 1.21.11 left LEQUAL to the builder's default).
 */
@Mixin(RenderPipelines.class)
public interface CbWallRenderPipelinesAccess {

    @Accessor("TEXT_SNIPPET")
    static RenderPipeline.Snippet cb$text() {
        throw new AssertionError();
    }

    @Accessor("CLOUDS_SNIPPET")
    static RenderPipeline.Snippet cb$clouds() {
        throw new AssertionError();
    }

    @Accessor("LINES_SNIPPET")
    static RenderPipeline.Snippet cb$lines() {
        throw new AssertionError();
    }

    @Accessor("DEBUG_FILLED_SNIPPET")
    static RenderPipeline.Snippet cb$debugFilled() {
        throw new AssertionError();
    }

    @Accessor("WEATHER_SNIPPET")
    static RenderPipeline.Snippet cb$weather() {
        throw new AssertionError();
    }

    @Accessor("GUI_SNIPPET")
    static RenderPipeline.Snippet cb$gui() {
        throw new AssertionError();
    }

    @Accessor("GUI_TEXTURED_SNIPPET")
    static RenderPipeline.Snippet cb$guiTextured() {
        throw new AssertionError();
    }

    @Accessor("GUI_TEXT_SNIPPET")
    static RenderPipeline.Snippet cb$guiText() {
        throw new AssertionError();
    }

    @Accessor("OUTLINE_SNIPPET")
    static RenderPipeline.Snippet cb$outline() {
        throw new AssertionError();
    }

    @Accessor("MATRICES_PROJECTION_SNIPPET")
    static RenderPipeline.Snippet cb$matricesProjection() {
        throw new AssertionError();
    }

    @Accessor("FOG_SNIPPET")
    static RenderPipeline.Snippet cb$fog() {
        throw new AssertionError();
    }

    @Accessor("GLOBALS_SNIPPET")
    static RenderPipeline.Snippet cb$globals() {
        throw new AssertionError();
    }

    @Accessor("MATRICES_FOG_SNIPPET")
    static RenderPipeline.Snippet cb$matricesFog() {
        throw new AssertionError();
    }

    @Accessor("MATRICES_FOG_LIGHT_DIR_SNIPPET")
    static RenderPipeline.Snippet cb$matricesFogLightDir() {
        throw new AssertionError();
    }

    @Accessor("GENERIC_BLOCKS_SNIPPET")
    static RenderPipeline.Snippet cb$genericBlocks() {
        throw new AssertionError();
    }

    @Accessor("TERRAIN_SNIPPET")
    static RenderPipeline.Snippet cb$terrain() {
        throw new AssertionError();
    }

    @Accessor("BLOCK_SNIPPET")
    static RenderPipeline.Snippet cb$block() {
        throw new AssertionError();
    }

    @Accessor("ENTITY_SNIPPET")
    static RenderPipeline.Snippet cb$entity() {
        throw new AssertionError();
    }

    @Accessor("ENTITY_EMISSIVE_SNIPPET")
    static RenderPipeline.Snippet cb$entityEmissive() {
        throw new AssertionError();
    }

    @Accessor("BEACON_BEAM_SNIPPET")
    static RenderPipeline.Snippet cb$beaconBeam() {
        throw new AssertionError();
    }

    @Accessor("END_PORTAL_SNIPPET")
    static RenderPipeline.Snippet cb$endPortal() {
        throw new AssertionError();
    }

    @Accessor("PARTICLE_SNIPPET")
    static RenderPipeline.Snippet cb$particle() {
        throw new AssertionError();
    }
}
