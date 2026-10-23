package top.dext.centurybridge.step.v26_1.rt;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.client.model.loading.v1.wrapper.WrapperBlockStateModel;
import net.fabricmc.fabric.api.client.renderer.v1.model.FabricBlockStateModel;
import net.fabricmc.fabric.api.client.renderer.v1.model.FabricBlockStateModelSet;
import net.minecraft.client.renderer.block.BlockStateModelSet;
import net.minecraft.client.renderer.block.dispatch.BlockStateModel;
import net.minecraft.client.renderer.block.dispatch.BlockStateModelPart;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.resources.model.sprite.Material;
import net.minecraft.core.BlockPos;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.BlockAndLightGetter;
import net.minecraft.world.level.block.state.BlockState;
import top.dext.centurybridge.step.v26_1.facade.BlockModelShaper;

import java.lang.reflect.Field;
import java.util.List;

/**
 * 1.21.11's Fabric block-state model members on 26.1 (fabric-renderer-api-v1 FabricBlockStateModel and
 * FabricBlockModels, fabric-model-loading-api-v1 WrapperBlockStateModel): 26.1 renamed particleSprite to
 * particleMaterial (sprite + forceTranslucent) and takes the client BlockAndTintGetter where 1.21.11 took its
 * BlockAndTintGetter (26.1's BlockAndLightGetter, CbWallTintClient.toTint), and dropped WrapperBlockStateModel's
 * forwards of the vanilla members 26.1 removed (collectParts(random), particleIcon()).
 */
@Environment(EnvType.CLIENT)
public final class CbFapiModels {
    private static final Field WRAPPED;

    static {
        try {
            WRAPPED = WrapperBlockStateModel.class.getDeclaredField("wrapped");
            WRAPPED.setAccessible(true);
        } catch (NoSuchFieldException e) {
            throw new ExceptionInInitializerError(e);
        }
    }

    private CbFapiModels() {
    }

    /** WrapperBlockStateModel's protected wrapped model, which every 1.21.11 forward read */
    private static BlockStateModel wrapped(WrapperBlockStateModel model) {
        try {
            return (BlockStateModel) WRAPPED.get(model);
        } catch (IllegalAccessException e) {
            throw new IllegalStateException(e);
        }
    }

    private static FabricBlockStateModel fabric(BlockStateModel model) {
        return (FabricBlockStateModel) (Object) model;
    }

    // ---- particleSprite -> particleMaterial ----

    /** a 1.21.11 call of FabricBlockStateModel.particleSprite(view, pos, state): 26.1's particleMaterial's sprite */
    public static TextureAtlasSprite particleSprite(FabricBlockStateModel model, BlockAndLightGetter view, BlockPos pos, BlockState state) {
        return model.particleMaterial(CbWallTintClient.toTint(view), pos, state).sprite();
    }

    /** the same call naming WrapperBlockStateModel */
    public static TextureAtlasSprite particleSprite(WrapperBlockStateModel model, BlockAndLightGetter view, BlockPos pos, BlockState state) {
        return particleSprite(fabric(model), view, pos, state);
    }

    /** 1.21.11 FabricBlockStateModel.particleSprite's default body (a super call): the model's particleIcon() */
    public static TextureAtlasSprite particleSpriteBase(FabricBlockStateModel model, BlockAndLightGetter view, BlockPos pos, BlockState state) {
        return CbWallModels.particleIcon((BlockStateModel) (Object) model);
    }

    /** 1.21.11 WrapperBlockStateModel.particleSprite (a super call): the wrapped model's */
    public static TextureAtlasSprite particleSpriteBase(WrapperBlockStateModel model, BlockAndLightGetter view, BlockPos pos, BlockState state) {
        return particleSprite(fabric(wrapped(model)), view, pos, state);
    }

    /** a 1.21.11 particle sprite where 26.1 asks a particle material: no forced translucency (1.21.11 had none) */
    public static Material.Baked material(TextureAtlasSprite sprite) {
        return new Material.Baked(sprite, false);
    }

    // ---- WrapperBlockStateModel's forwards of members 26.1 removed ----

    /** a 1.21.11 call of WrapperBlockStateModel.collectParts(random): the vanilla member's body on the receiver */
    public static List<BlockStateModelPart> collectParts(WrapperBlockStateModel model, RandomSource random) {
        return CbWallModels.collectParts(model, random);
    }

    /** 1.21.11 WrapperBlockStateModel.collectParts(random) (a super call): the wrapped model's */
    public static List<BlockStateModelPart> collectPartsBase(WrapperBlockStateModel model, RandomSource random) {
        return CbWallModels.collectParts(wrapped(model), random);
    }

    /** a 1.21.11 call of WrapperBlockStateModel.particleIcon(): the vanilla member on the receiver */
    public static TextureAtlasSprite particleIcon(WrapperBlockStateModel model) {
        return CbWallModels.particleIcon(model);
    }

    /** 1.21.11 WrapperBlockStateModel.particleIcon() (a super call): the wrapped model's */
    public static TextureAtlasSprite particleIconBase(WrapperBlockStateModel model) {
        return CbWallModels.particleIcon(wrapped(model));
    }

    // ---- FabricBlockModels (26.1 FabricBlockStateModelSet) ----

    /** a 1.21.11 call of FabricBlockModels.getModelParticleSprite(state, view, pos): 26.1's getParticleMaterial's sprite */
    public static TextureAtlasSprite getModelParticleSprite(FabricBlockStateModelSet models, BlockState state, BlockAndLightGetter view, BlockPos pos) {
        return models.getParticleMaterial(state, CbWallTintClient.toTint(view), pos).sprite();
    }

    /**
     * 1.21.11 FabricBlockModels.getModelParticleSprite's default body (a super call): the particle sprite of the
     * state's model, from the shaper 1.21.11 injected it into (the facade, or 26.1's model set where a 26.1
     * BlockStateModelSet stands for it).
     */
    public static TextureAtlasSprite getModelParticleSpriteBase(FabricBlockStateModelSet models, BlockState state, BlockAndLightGetter view,
                                                               BlockPos pos) {
        BlockStateModel model = models instanceof BlockModelShaper shaper ? shaper.method_3335(state)
                : ((BlockStateModelSet) (Object) models).get(state);
        return particleSprite(fabric(model), view, pos, state);
    }
}
