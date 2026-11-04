package top.dext.centurybridge.step.v26_1.rt;

import com.mojang.blaze3d.platform.Transparency;
import java.util.List;
import java.util.Map;
import java.util.WeakHashMap;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.model.geom.builders.UVPair;
import net.minecraft.client.renderer.block.dispatch.BlockStateModel;
import net.minecraft.client.renderer.block.dispatch.BlockStateModelPart;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.resources.model.ModelManager;
import net.minecraft.client.resources.model.geometry.BakedQuad;
import net.minecraft.client.resources.model.sprite.Material;
import net.minecraft.core.Direction;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.block.state.BlockState;
import org.joml.Vector3fc;
import top.dext.centurybridge.step.v26_1.facade.BlockModelShaper;

/**
 * 1.21.11's block model API on 26.1's.
 * <ul>
 * <li>BakedQuad: 26.1 moved tintIndex / sprite / shade / lightEmission into a MaterialInfo, which also
 *     carries the chunk section layer and item render type each quad now renders with (26.1 has no
 *     per-block render layer: ItemBlockRenderTypes is gone). A quad built with 1.21.11's constructor gets
 *     its MaterialInfo the way 26.1's FaceBakery makes one for a model face: the sprite's transparency
 *     over the area the quad's UVs cover, no forced translucency.</li>
 * <li>BlockStateModel / BlockModelPart: particleIcon() became particleMaterial() (the sprite plus a
 *     forceTranslucent flag 1.21.11 did not have), and both gained materialFlags(), which 26.1 computes
 *     from the quads (translucent / animated). Mod implementations get both through the adapters below.</li>
 * <li>ModelManager lost getMissingBlockStateModel() and getBlockModelShaper(); the models live in its
 *     BlockStateModelSet. The shaper it hands out is the live {@link BlockModelShaper} over that set.</li>
 * </ul>
 */
@Environment(EnvType.CLIENT)
public final class CbWallModels {

    private CbWallModels() {
    }

    // ---- BakedQuad ----

    /** 1.21.11 BakedQuad(position0..3, packedUV0..3, tintIndex, direction, sprite, shade, lightEmission) */
    public static BakedQuad bakedQuad(Vector3fc p0, Vector3fc p1, Vector3fc p2, Vector3fc p3,
                                      long uv0, long uv1, long uv2, long uv3,
                                      int tintIndex, Direction direction, TextureAtlasSprite sprite,
                                      boolean shade, int lightEmission) {
        Material.Baked material = new Material.Baked(sprite, false);
        BakedQuad.MaterialInfo info = BakedQuad.MaterialInfo.of(material,
            transparency(sprite, uv0, uv1, uv2, uv3), tintIndex, shade, lightEmission);
        return new BakedQuad(p0, p1, p2, p3, uv0, uv1, uv2, uv3, direction, info);
    }

    /**
     * FaceBakery.computeMaterialTransparency for a quad given by atlas UVs: the sprite's transparency over
     * the part of it the quad covers, in the sprite's own 0..1 coordinates.
     */
    private static Transparency transparency(TextureAtlasSprite sprite, long uv0, long uv1, long uv2, long uv3) {
        float minU = Math.min(Math.min(UVPair.unpackU(uv0), UVPair.unpackU(uv1)), Math.min(UVPair.unpackU(uv2), UVPair.unpackU(uv3)));
        float maxU = Math.max(Math.max(UVPair.unpackU(uv0), UVPair.unpackU(uv1)), Math.max(UVPair.unpackU(uv2), UVPair.unpackU(uv3)));
        float minV = Math.min(Math.min(UVPair.unpackV(uv0), UVPair.unpackV(uv1)), Math.min(UVPair.unpackV(uv2), UVPair.unpackV(uv3)));
        float maxV = Math.max(Math.max(UVPair.unpackV(uv0), UVPair.unpackV(uv1)), Math.max(UVPair.unpackV(uv2), UVPair.unpackV(uv3)));
        float du = sprite.getU1() - sprite.getU0();
        float dv = sprite.getV1() - sprite.getV0();
        if (du == 0.0F || dv == 0.0F) {
            return sprite.contents().computeTransparency(0.0F, 0.0F, 1.0F, 1.0F);
        }
        return sprite.contents().computeTransparency(
            local(minU, sprite.getU0(), du), local(minV, sprite.getV0(), dv),
            local(maxU, sprite.getU0(), du), local(maxV, sprite.getV0(), dv));
    }

    private static float local(float atlas, float origin, float size) {
        return Math.clamp((atlas - origin) / size, 0.0F, 1.0F);
    }

    /** 1.21.11 BakedQuad.tintIndex() */
    public static int tintIndex(BakedQuad q) {
        return q.materialInfo().tintIndex();
    }

    /** 1.21.11 BakedQuad.sprite() */
    public static TextureAtlasSprite sprite(BakedQuad q) {
        return q.materialInfo().sprite();
    }

    /** 1.21.11 BakedQuad.shade() */
    public static boolean shade(BakedQuad q) {
        return q.materialInfo().shade();
    }

    /** 1.21.11 BakedQuad.lightEmission() */
    public static int lightEmission(BakedQuad q) {
        return q.materialInfo().lightEmission();
    }

    /** 1.21.11 BakedQuad.isTinted(): tintIndex != -1, as MaterialInfo.isTinted() */
    public static boolean isTinted(BakedQuad q) {
        return q.materialInfo().isTinted();
    }

    /**
     * 26.1 ModelBaker.Interner.materialInfo(info), abstract on 26.1, on a 1.21.11 Interner implementation: 1.21.11's
     * Interner interned only vectors (vector(Vector3fc), javap of class_7775$class_12356) and 1.21.11 quads carried no
     * MaterialInfo, so a 1.21.11 interner shares none: the info as given (what 26.1's InternerImpl answers for an info
     * not seen before).
     */
    public static BakedQuad.MaterialInfo materialInfo(net.minecraft.client.resources.model.ModelBaker.Interner interner,
                                                      BakedQuad.MaterialInfo info) {
        return info;
    }

    // ---- BlockStateModel / BlockModelPart ----

    /** 1.21.11 BlockStateModel.particleIcon() */
    public static TextureAtlasSprite particleIcon(BlockStateModel model) {
        return model.particleMaterial().sprite();
    }

    /** 1.21.11 BlockModelPart.particleIcon() */
    public static TextureAtlasSprite partParticleIcon(BlockStateModelPart part) {
        return part.particleMaterial().sprite();
    }

    /**
     * 1.21.11 new SimpleModelWrapper(quads, useAmbientOcclusion, particleIcon): 26.1's record holds the particle as
     * a baked material; a 1.21.11 particle sprite carries no translucency override (26.1's default, false)
     */
    public static net.minecraft.client.resources.model.SimpleModelWrapper simpleModelWrapper(
            net.minecraft.client.resources.model.geometry.QuadCollection quads, boolean useAmbientOcclusion, TextureAtlasSprite particleIcon) {
        return new net.minecraft.client.resources.model.SimpleModelWrapper(quads, useAmbientOcclusion, new Material.Baked(particleIcon, false));
    }

    /** 1.21.11 BlockStateModel.collectParts(RandomSource), a default method 26.1 dropped: same body */
    public static List<BlockStateModelPart> collectParts(BlockStateModel model, RandomSource random) {
        List<BlockStateModelPart> parts = new ObjectArrayList<>();
        model.collectParts(random, parts);
        return parts;
    }

    /** A 1.21.11 BlockStateModel implementation: its own particleIcon() */
    public interface LegacyBlockStateModel extends BlockStateModel {
        TextureAtlasSprite particleIcon();
    }

    /** A 1.21.11 BlockModelPart implementation: its own particleIcon() */
    public interface LegacyBlockModelPart extends BlockStateModelPart {
        TextureAtlasSprite particleIcon();
    }

    /** particleMaterial() of a 1.21.11 model: its particle sprite, without forced translucency (1.21.11 had none) */
    public static Material.Baked modelParticleMaterial(LegacyBlockStateModel model) {
        return new Material.Baked(model.particleIcon(), false);
    }

    /** particleMaterial() of a 1.21.11 model part */
    public static Material.Baked partParticleMaterial(LegacyBlockModelPart part) {
        return new Material.Baked(part.particleIcon(), false);
    }

    /** materialFlags() of a 1.21.11 model part: the OR of its quads' flags, as 26.1's QuadCollection computes it */
    public static int partMaterialFlags(LegacyBlockModelPart part) {
        return quadFlags(part);
    }

    /**
     * materialFlags() of a 1.21.11 model: the OR of the flags of the parts it collects. 26.1's own models
     * OR every variant; a mod model's variants cannot be enumerated, so its parts are collected with the
     * fixed seed 42 (the seed 1.21.11's ModelBlockRenderer.renderModel collected parts with where no
     * position is known). Not cached: a mod model may change what it collects.
     */
    public static int modelMaterialFlags(LegacyBlockStateModel model) {
        List<BlockStateModelPart> parts = new ObjectArrayList<>();
        model.collectParts(RandomSource.create(42L), parts);
        int flags = 0;
        for (BlockStateModelPart part : parts) {
            flags |= part.materialFlags();
        }
        return flags;
    }

    private static int quadFlags(BlockStateModelPart part) {
        int flags = 0;
        for (Direction d : Direction.values()) {
            for (BakedQuad q : part.getQuads(d)) {
                flags |= q.materialInfo().flags();
            }
        }
        for (BakedQuad q : part.getQuads(null)) {
            flags |= q.materialInfo().flags();
        }
        return flags;
    }

    // ---- ModelManager ----

    private static final Map<ModelManager, BlockModelShaper> SHAPERS = new WeakHashMap<>();

    /** 1.21.11 ModelManager.getBlockModelShaper(): one live shaper per model manager, as 1.21.11 kept one */
    public static synchronized BlockModelShaper getBlockModelShaper(ModelManager manager) {
        return SHAPERS.computeIfAbsent(manager, BlockModelShaper::cb$live);
    }

    /** 1.21.11 ModelManager.getMissingBlockStateModel() */
    public static BlockStateModel getMissingBlockStateModel(ModelManager manager) {
        return manager.getBlockStateModelSet().missingModel();
    }

    /** the model a live shaper answers for a state: the manager's current set, as 1.21.11's shaper map was */
    public static BlockStateModel liveModel(ModelManager manager, BlockState state) {
        return manager.getBlockStateModelSet().get(state);
    }

    /** a BlockStateModelSet whose model map a live shaper's replaceCache replaces (CbWallBlockStateModelSetMixin) */
    public interface ReplaceableModelSet {
        void cb$replaceModels(Map<BlockState, BlockStateModel> models);
    }
}
