package top.dext.centurybridge.step.v26_1.rt;

import com.mojang.blaze3d.pipeline.BlendFunction;
import com.mojang.blaze3d.pipeline.ColorTargetState;
import com.mojang.blaze3d.pipeline.DepthStencilState;
import com.mojang.blaze3d.pipeline.RenderPipeline;
import com.mojang.blaze3d.platform.CompareOp;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.textures.AddressMode;
import com.mojang.blaze3d.textures.FilterMode;
import com.mojang.blaze3d.textures.GpuSampler;
import com.mojang.blaze3d.vertex.DefaultVertexFormat;
import com.mojang.blaze3d.vertex.VertexFormat;
import java.util.function.BiFunction;
import java.util.function.Function;
import java.util.function.Supplier;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.renderer.RenderPipelines;
import net.minecraft.client.renderer.rendertype.OutputTarget;
import net.minecraft.client.renderer.rendertype.RenderSetup;
import net.minecraft.client.renderer.rendertype.RenderType;
import net.minecraft.client.renderer.rendertype.RenderTypes;
import net.minecraft.resources.Identifier;
import net.minecraft.util.Util;
import top.dext.centurybridge.step.v26_1.mixin.CbWallRenderPipelinesAccess;

/**
 * The render pipelines and render types 26.1 dropped from RenderPipelines / RenderTypes, rebuilt from 1.21.11's
 * definitions (its RenderPipelines and RenderTypes initializers) on 26.1's builders.
 * <ul>
 * <li>Tripwire and translucent moving blocks: 26.1 draws tripwire in the translucent layer (CbWallChunks) and moving
 *     translucent blocks with TRANSLUCENT_BLOCK, so 1.21.11's TRIPWIRE_BLOCK / TRIPWIRE_TERRAIN /
 *     TRANSLUCENT_MOVING_BLOCK pipelines and tripwireMovingBlock() are 26.1's translucent ones.</li>
 * <li>ENTITY_CUTOUT_NO_CULL_Z_OFFSET is 26.1's ENTITY_CUTOUT_Z_OFFSET (same shader defines, samplers and culling;
 *     26.1 dropped "no cull" from the name when its other z-offset variant went).</li>
 * <li>ENTITY_SMOOTH_CUTOUT and ENTITY_NO_OUTLINE are 26.1's entity shader with 1.21.11's defines.</li>
 * <li>ENTITY_DECAL, ITEM_ENTITY_TRANSLUCENT_CULL and DRAGON_EXPLOSION_ALPHA used shaders 26.1 deleted; they are
 *     CenturyBridge's copies (assets/centurybridge/shaders/core: the lightmap is sampled the 26.1 way).</li>
 * <li>A 1.21.11 builder left the depth test at LEQUAL with writes on unless told otherwise; 26.1's has no depth test
 *     unless given a DepthStencilState, so every pipeline here states its 1.21.11 depth state.</li>
 * </ul>
 */
@Environment(EnvType.CLIENT)
public final class CbWallRenderTypes {

    private CbWallRenderTypes() {
    }

    private static Identifier cb(String path) {
        return Identifier.fromNamespaceAndPath("centurybridge", path);
    }

    private static final DepthStencilState NO_DEPTH_WRITE = new DepthStencilState(CompareOp.LESS_THAN_OR_EQUAL, false);

    // ---- pipelines ----

    public static RenderPipeline tripwireBlock() {
        return RenderPipelines.TRANSLUCENT_BLOCK;
    }

    public static RenderPipeline tripwireTerrain() {
        return RenderPipelines.TRANSLUCENT_TERRAIN;
    }

    public static RenderPipeline translucentMovingBlock() {
        return RenderPipelines.TRANSLUCENT_BLOCK;
    }

    public static RenderPipeline entityCutoutNoCullZOffset() {
        return RenderPipelines.ENTITY_CUTOUT_Z_OFFSET;
    }

    private static final class Pipelines {
        static final RenderPipeline ENTITY_SMOOTH_CUTOUT = RenderPipeline.builder(CbWallRenderPipelinesAccess.cb$entity())
                .withLocation(cb("pipeline/entity_smooth_cutout"))
                .withShaderDefine("ALPHA_CUTOUT", 0.1F)
                .withSampler("Sampler1")
                .withCull(false)
                .build();

        static final RenderPipeline ENTITY_NO_OUTLINE = RenderPipeline.builder(CbWallRenderPipelinesAccess.cb$entity())
                .withLocation(cb("pipeline/entity_no_outline"))
                .withShaderDefine("NO_OVERLAY")
                .withShaderDefine("PER_FACE_LIGHTING")
                .withColorTargetState(new ColorTargetState(BlendFunction.TRANSLUCENT))
                .withCull(false)
                .withDepthStencilState(NO_DEPTH_WRITE)
                .build();

        static final RenderPipeline ENTITY_DECAL = RenderPipeline.builder(CbWallRenderPipelinesAccess.cb$matricesFogLightDir())
                .withLocation(cb("pipeline/entity_decal"))
                .withVertexShader(cb("core/entity_decal"))
                .withFragmentShader(cb("core/entity_decal"))
                .withSampler("Sampler0")
                .withSampler("Sampler1")
                .withSampler("Sampler2")
                .withDepthStencilState(new DepthStencilState(CompareOp.EQUAL, true))
                .withCull(false)
                .withVertexFormat(DefaultVertexFormat.ENTITY, VertexFormat.Mode.QUADS)
                .build();

        static final RenderPipeline ITEM_ENTITY_TRANSLUCENT_CULL = RenderPipeline.builder(CbWallRenderPipelinesAccess.cb$matricesFogLightDir())
                .withLocation(cb("pipeline/item_entity_translucent_cull"))
                .withVertexShader(cb("core/item_entity_translucent_cull"))
                .withFragmentShader(cb("core/item_entity_translucent_cull"))
                .withSampler("Sampler0")
                .withSampler("Sampler2")
                .withColorTargetState(new ColorTargetState(BlendFunction.TRANSLUCENT))
                .withDepthStencilState(DepthStencilState.DEFAULT)
                .withVertexFormat(DefaultVertexFormat.ENTITY, VertexFormat.Mode.QUADS)
                .build();

        static final RenderPipeline DRAGON_EXPLOSION_ALPHA = RenderPipeline.builder(CbWallRenderPipelinesAccess.cb$matricesProjection())
                .withLocation(cb("pipeline/dragon_explosion_alpha"))
                .withVertexShader(cb("core/entity_alpha"))
                .withFragmentShader(cb("core/entity_alpha"))
                .withSampler("Sampler0")
                .withCull(false)
                .withDepthStencilState(DepthStencilState.DEFAULT)
                .withVertexFormat(DefaultVertexFormat.ENTITY, VertexFormat.Mode.QUADS)
                .build();
    }

    public static RenderPipeline entitySmoothCutoutPipeline() {
        return Pipelines.ENTITY_SMOOTH_CUTOUT;
    }

    public static RenderPipeline entityNoOutlinePipeline() {
        return Pipelines.ENTITY_NO_OUTLINE;
    }

    public static RenderPipeline entityDecalPipeline() {
        return Pipelines.ENTITY_DECAL;
    }

    public static RenderPipeline itemEntityTranslucentCullPipeline() {
        return Pipelines.ITEM_ENTITY_TRANSLUCENT_CULL;
    }

    public static RenderPipeline dragonExplosionAlphaPipeline() {
        return Pipelines.DRAGON_EXPLOSION_ALPHA;
    }

    // ---- render types ----

    /** RenderTypes.MOVING_BLOCK_SAMPLER: the sampler 26.1's moving block setups use (createMovingBlockSetup) */
    public static final Supplier<GpuSampler> MOVING_BLOCK_SAMPLER = () -> RenderSystem.getSamplerCache()
            .getSampler(AddressMode.CLAMP_TO_EDGE, AddressMode.CLAMP_TO_EDGE, FilterMode.LINEAR, FilterMode.NEAREST, true);

    public static Supplier<GpuSampler> movingBlockSampler() {
        return MOVING_BLOCK_SAMPLER;
    }

    public static RenderType tripwireMovingBlock() {
        return RenderTypes.translucentMovingBlock();
    }

    private static final class Types {
        static final Function<Identifier, RenderType> ENTITY_SMOOTH_CUTOUT = Util.memoize(texture -> RenderType.create(
                "entity_smooth_cutout",
                RenderSetup.builder(Pipelines.ENTITY_SMOOTH_CUTOUT)
                        .withTexture("Sampler0", texture)
                        .useLightmap()
                        .useOverlay()
                        .setOutline(RenderSetup.OutlineProperty.AFFECTS_OUTLINE)
                        .createRenderSetup()));

        static final Function<Identifier, RenderType> ENTITY_DECAL = Util.memoize(texture -> RenderType.create(
                "entity_decal",
                RenderSetup.builder(Pipelines.ENTITY_DECAL)
                        .withTexture("Sampler0", texture)
                        .useLightmap()
                        .useOverlay()
                        .createRenderSetup()));

        static final Function<Identifier, RenderType> DRAGON_EXPLOSION_ALPHA = Util.memoize(texture -> RenderType.create(
                "entity_alpha",
                RenderSetup.builder(Pipelines.DRAGON_EXPLOSION_ALPHA)
                        .withTexture("Sampler0", texture)
                        .setOutline(RenderSetup.OutlineProperty.AFFECTS_OUTLINE)
                        .createRenderSetup()));

        static final BiFunction<Identifier, Boolean, RenderType> WEATHER = Util.memoize((texture, depthWrite) -> RenderType.create(
                "weather",
                RenderSetup.builder(depthWrite ? RenderPipelines.WEATHER_DEPTH_WRITE : RenderPipelines.WEATHER_NO_DEPTH_WRITE)
                        .withTexture("Sampler0", texture)
                        .setOutputTarget(OutputTarget.WEATHER_TARGET)
                        .useLightmap()
                        .createRenderSetup()));
    }

    public static RenderType entitySmoothCutout(Identifier texture) {
        return Types.ENTITY_SMOOTH_CUTOUT.apply(texture);
    }

    public static RenderType entityDecal(Identifier texture) {
        return Types.ENTITY_DECAL.apply(texture);
    }

    public static RenderType dragonExplosionAlpha(Identifier texture) {
        return Types.DRAGON_EXPLOSION_ALPHA.apply(texture);
    }

    /** RenderTypes.weather(texture, depthWrite): 26.1's WeatherEffectRenderer builds its own; this is 1.21.11's */
    public static RenderType weather(Identifier texture, boolean depthWrite) {
        return Types.WEATHER.apply(texture, depthWrite);
    }
}
