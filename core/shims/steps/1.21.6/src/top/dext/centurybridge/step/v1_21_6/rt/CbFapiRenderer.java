package top.dext.centurybridge.step.v1_21_6.rt;

import net.fabricmc.fabric.api.renderer.v1.mesh.MutableQuadView;
import net.fabricmc.fabric.api.renderer.v1.mesh.QuadEmitter;
import net.fabricmc.fabric.api.renderer.v1.mesh.QuadView;
import net.fabricmc.fabric.api.renderer.v1.mesh.ShadeMode;
import net.fabricmc.fabric.api.renderer.v1.render.BlockVertexConsumerProvider;
import net.fabricmc.fabric.api.util.TriState;
import top.dext.centurybridge.step.v1_21_6.facade.fapi.renderer.BlendMode;
import top.dext.centurybridge.step.v1_21_6.facade.fapi.renderer.GlintMode;
import top.dext.centurybridge.step.v1_21_6.facade.fapi.renderer.MaterialFinder;
import top.dext.centurybridge.step.v1_21_6.facade.fapi.renderer.MaterialView;
import top.dext.centurybridge.step.v1_21_6.facade.fapi.renderer.RenderMaterial;

/**
 * Fabric API 1.21.5's renderer-v1 material system on 1.21.6's (renderer-api 6.1 -> 7.0, javap of both and of Indigo).
 * 1.21.5 put a quad's blend mode, emission, diffuse, ambient occlusion, glint and shade mode into a RenderMaterial
 * (MaterialFinder.find, canonical per value; Renderer.materialById / registerMaterial, a map that refuses an id twice;
 * the finder's defaults all DEFAULT / false, shade ENHANCED). 1.21.6 sets each on the quad itself
 * (QuadEmitter.renderLayer / emissive / diffuseShade / ambientOcclusion / glint / shadeMode, null layer and glint
 * meaning the default), and takes a BlockVertexConsumerProvider (getBuffer(ChunkSectionLayer)) where 1.21.5 took a
 * MultiBufferSource asked for 1.21.5's chunk render types.
 */
@net.fabricmc.api.Environment(net.fabricmc.api.EnvType.CLIENT)
public final class CbFapiRenderer {

    private CbFapiRenderer() {
    }

    // ---- materials ----

    private record Material(BlendMode blendMode, boolean emissive, boolean disableDiffuse, TriState ambientOcclusion,
                            GlintMode glintMode, ShadeMode shadeMode) implements RenderMaterial {
    }

    private static final java.util.Map<Material, Material> CANONICAL = new java.util.concurrent.ConcurrentHashMap<>();

    static RenderMaterial of(BlendMode blend, boolean emissive, boolean disableDiffuse, TriState ao, GlintMode glint, ShadeMode shade) {
        Material m = new Material(blend, emissive, disableDiffuse, ao, glint, shade);
        return CANONICAL.computeIfAbsent(m, k -> k);
    }

    private static final class Finder implements MaterialFinder {
        private BlendMode blend;
        private boolean emissive;
        private boolean disableDiffuse;
        private TriState ao;
        private GlintMode glint;
        private ShadeMode shade;

        Finder() {
            clear();
        }

        @Override public MaterialFinder blendMode(BlendMode mode) {
            this.blend = java.util.Objects.requireNonNull(mode, "BlendMode may not be null");
            return this;
        }

        @Override public MaterialFinder emissive(boolean emissive) {
            this.emissive = emissive;
            return this;
        }

        @Override public MaterialFinder disableDiffuse(boolean disable) {
            this.disableDiffuse = disable;
            return this;
        }

        @Override public MaterialFinder ambientOcclusion(TriState mode) {
            this.ao = java.util.Objects.requireNonNull(mode, "ambient occlusion TriState may not be null");
            return this;
        }

        @Override public MaterialFinder glintMode(GlintMode mode) {
            this.glint = java.util.Objects.requireNonNull(mode, "GlintMode may not be null");
            return this;
        }

        @Override public MaterialFinder shadeMode(ShadeMode mode) {
            this.shade = java.util.Objects.requireNonNull(mode, "ShadeMode may not be null");
            return this;
        }

        @Override public MaterialFinder copyFrom(MaterialView m) {
            blend = m.blendMode();
            emissive = m.emissive();
            disableDiffuse = m.disableDiffuse();
            ao = m.ambientOcclusion();
            glint = m.glintMode();
            shade = m.shadeMode();
            return this;
        }

        @Override public MaterialFinder clear() {
            blend = BlendMode.DEFAULT;
            emissive = false;
            disableDiffuse = false;
            ao = TriState.DEFAULT;
            glint = GlintMode.DEFAULT;
            shade = ShadeMode.ENHANCED;
            return this;
        }

        @Override public RenderMaterial find() {
            return of(blend, emissive, disableDiffuse, ao, glint, shade);
        }

        @Override public BlendMode blendMode() {
            return blend;
        }

        @Override public boolean emissive() {
            return emissive;
        }

        @Override public boolean disableDiffuse() {
            return disableDiffuse;
        }

        @Override public TriState ambientOcclusion() {
            return ao;
        }

        @Override public GlintMode glintMode() {
            return glint;
        }

        @Override public ShadeMode shadeMode() {
            return shade;
        }
    }

    private static final java.util.Map<net.minecraft.class_2960, RenderMaterial> BY_ID = new java.util.HashMap<>();

    static {
        BY_ID.put(RenderMaterial.STANDARD_ID, new Finder().find());
    }

    /** Renderer.materialFinder() */
    public static MaterialFinder materialFinder(net.fabricmc.fabric.api.renderer.v1.Renderer renderer) {
        return new Finder();
    }

    /** Renderer.materialById(id) */
    public static synchronized RenderMaterial materialById(net.fabricmc.fabric.api.renderer.v1.Renderer renderer, net.minecraft.class_2960 id) {
        return BY_ID.get(id);
    }

    /** Renderer.registerMaterial(id, material): false when the id is taken, as Indigo's */
    public static synchronized boolean registerMaterial(net.fabricmc.fabric.api.renderer.v1.Renderer renderer, net.minecraft.class_2960 id,
                                                        RenderMaterial material) {
        if (BY_ID.containsKey(id)) {
            return false;
        }
        BY_ID.put(id, material);
        return true;
    }

    // ---- quads ----

    private static void apply(MutableQuadView quad, RenderMaterial m) {
        quad.renderLayer(m.blendMode().cb$layer());
        quad.emissive(m.emissive());
        quad.diffuseShade(!m.disableDiffuse());
        quad.ambientOcclusion(m.ambientOcclusion());
        quad.glint(m.glintMode().glint);
        quad.shadeMode(m.shadeMode());
    }

    /** MutableQuadView.material(material) */
    public static MutableQuadView material(MutableQuadView quad, RenderMaterial material) {
        apply(quad, material);
        return quad;
    }

    /** QuadEmitter.material(material) */
    public static QuadEmitter material(QuadEmitter quad, RenderMaterial material) {
        apply(quad, material);
        return quad;
    }

    /** QuadView.material(): the quad's own properties as a 1.21.5 material */
    public static RenderMaterial material(QuadView quad) {
        return of(BlendMode.cb$of(quad.renderLayer()), quad.emissive(), !quad.diffuseShade(), quad.ambientOcclusion(),
            GlintMode.fromGlint(quad.glint()), quad.shadeMode());
    }

    /**
     * fromVanilla(quad, material, cullFace), Indigo 1.21.5's: the quad's data, nominal face, tint and light emission
     * (1.21.6's fromBakedQuad), then the cull face, the material (diffuse disabled too when the quad is unshaded), tag 0
     */
    private static void fill(MutableQuadView e, net.minecraft.class_777 quad, RenderMaterial material, net.minecraft.class_2350 cullFace) {
        e.fromBakedQuad(quad);
        e.cullFace(cullFace);
        apply(e, material);
        e.diffuseShade(quad.comp_3725() && !material.disableDiffuse());
        e.tag(0);
    }

    /** MutableQuadView.fromVanilla(quad, material, cullFace) */
    public static MutableQuadView fromVanilla(MutableQuadView e, net.minecraft.class_777 quad, RenderMaterial material, net.minecraft.class_2350 cullFace) {
        fill(e, quad, material, cullFace);
        return e;
    }

    /** QuadEmitter.fromVanilla(quad, material, cullFace) */
    public static QuadEmitter fromVanilla(QuadEmitter e, net.minecraft.class_777 quad, RenderMaterial material, net.minecraft.class_2350 cullFace) {
        fill(e, quad, material, cullFace);
        return e;
    }

    // ---- block rendering ----

    /** 1.21.5's chunk render type for a 1.21.6 chunk layer (RenderType.solid ... tripwire; translucent the rebuild) */
    static net.minecraft.class_1921 chunkType(net.minecraft.class_11515 layer) {
        for (BlendMode mode : BlendMode.values()) {
            if (mode != BlendMode.DEFAULT && mode.cb$layer() == layer) {
                return mode.blockRenderLayer;
            }
        }
        return net.minecraft.class_1921.method_29997();
    }

    /** a 1.21.5 MultiBufferSource, asked for 1.21.5's chunk render types, as 1.21.6's BlockVertexConsumerProvider */
    public static BlockVertexConsumerProvider provider(net.minecraft.class_4597 buffers) {
        return layer -> buffers.getBuffer(chunkType(layer));
    }

    /** Renderer.emitBlockModelPartQuads(part, emitter, cullTest): 1.21.6's FabricBlockModelPart.emitQuads */
    public static void emitBlockModelPartQuads(net.fabricmc.fabric.api.renderer.v1.Renderer renderer, net.minecraft.class_10889 part,
                                               QuadEmitter emitter, java.util.function.Predicate<net.minecraft.class_2350> cullTest) {
        ((net.fabricmc.fabric.api.renderer.v1.model.FabricBlockModelPart) part).emitQuads(emitter, cullTest);
    }

    // ---- RenderLayerHelper (1.21.5's bodies, javap) ----

    public static net.minecraft.class_1921 getMovingBlockLayer(net.minecraft.class_1921 layer) {
        return layer == CbChunkTypes.method_23583() ? net.minecraft.class_1921.method_29380() : layer;
    }

    public static net.minecraft.class_1921 getEntityBlockLayer(net.minecraft.class_1921 layer) {
        return layer == CbChunkTypes.method_23583() ? net.minecraft.class_4722.method_29382() : net.minecraft.class_4722.method_24074();
    }

    public static net.minecraft.class_4597 movingDelegate(net.minecraft.class_4597 buffers) {
        return type -> buffers.getBuffer(getMovingBlockLayer(type));
    }

    public static net.minecraft.class_4597 entityDelegate(net.minecraft.class_4597 buffers) {
        return type -> buffers.getBuffer(getEntityBlockLayer(type));
    }
}
