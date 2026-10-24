package top.dext.centurybridge.step.v26_1.facade;

import java.util.Map;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.client.renderer.v1.model.FabricBlockStateModel;
import net.fabricmc.fabric.api.client.renderer.v1.model.FabricBlockStateModelSet;
import net.minecraft.client.renderer.block.BlockAndTintGetter;
import net.minecraft.client.renderer.block.dispatch.BlockStateModel;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.resources.model.ModelManager;
import net.minecraft.client.resources.model.sprite.Material;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.BlockAndLightGetter;
import net.minecraft.world.level.block.state.BlockState;
import top.dext.centurybridge.step.v26_1.rt.CbFapiModels;
import top.dext.centurybridge.step.v26_1.rt.CbWallModels;

/**
 * 1.21.11's BlockModelShaper (net/minecraft/class_773), gone in 26.1: its state -> model map is
 * ModelManager's BlockStateModelSet.
 *
 * Two kinds of instance, as in 1.21.11:
 * <ul>
 * <li>the live one ModelManager.getBlockModelShaper() / BlockRenderDispatcher.getBlockModelShaper()
 *     answered with ({@link #cb$live}): it reads the manager's current set, which is the map 1.21.11's
 *     ModelManager installed through replaceCache on every reload; its replaceCache replaces that set's
 *     map in place (CbWallBlockStateModelSetMixin), so every 26.1 renderer holding the set -- the section
 *     compiler, block feature renderer, particles -- sees the new models, as every 1.21.11 renderer
 *     reading through the shaper did;</li>
 * <li>one a mod constructs: its own map, empty until its replaceCache, answering the missing model for a
 *     state it lacks, exactly as 1.21.11's constructor and getBlockModel behaved.</li>
 * </ul>
 * Member names are 1.21.11's intermediary names: the converter renames the dead class here and leaves the
 * call sites' member names alone.
 *
 * 1.21.11's Fabric API injected FabricBlockModels into it; that is 26.1's FabricBlockStateModelSet, answered
 * from this shaper's own models (26.1's default reads a BlockStateModelSet, which this is not).
 */
@Environment(EnvType.CLIENT)
public class BlockModelShaper implements FabricBlockStateModelSet {

    private Map<BlockState, BlockStateModel> cb$models = Map.of();
    private final ModelManager cb$modelManager;
    private final boolean cb$live;

    /** 1.21.11 BlockModelShaper(ModelManager) */
    public BlockModelShaper(ModelManager modelManager) {
        this(modelManager, false);
    }

    private BlockModelShaper(ModelManager modelManager, boolean live) {
        this.cb$modelManager = modelManager;
        this.cb$live = live;
    }

    /** the shaper the model manager itself owned in 1.21.11 */
    public static BlockModelShaper cb$live(ModelManager modelManager) {
        return new BlockModelShaper(modelManager, true);
    }

    /** getParticleIcon(BlockState): the particle sprite of getBlockModel(state) */
    public TextureAtlasSprite method_3339(BlockState state) {
        return this.method_3335(state).particleMaterial().sprite();
    }

    /** getBlockModel(BlockState): the state's model, the missing model when it has none */
    public BlockStateModel method_3335(BlockState state) {
        if (this.cb$live) {
            return CbWallModels.liveModel(this.cb$modelManager, state);
        }
        BlockStateModel model = this.cb$models.get(state);
        return model != null ? model : CbWallModels.getMissingBlockStateModel(this.cb$modelManager);
    }

    /** getModelManager() */
    public ModelManager method_3333() {
        return this.cb$modelManager;
    }

    /** replaceCache(Map) */
    public void method_45784(Map<BlockState, BlockStateModel> models) {
        if (this.cb$live) {
            ((CbWallModels.ReplaceableModelSet) this.cb$modelManager.getBlockStateModelSet()).cb$replaceModels(models);
        } else {
            this.cb$models = models;
        }
    }

    /** FabricBlockModels.getModelParticleSprite's 26.1 shape: the particle material of getBlockModel(state) */
    @Override
    public Material.Baked getParticleMaterial(BlockState state, BlockAndTintGetter level, BlockPos pos) {
        return ((FabricBlockStateModel) (Object) this.method_3335(state)).particleMaterial(level, pos, state);
    }

    /** FabricBlockModels.getModelParticleSprite(state, view, pos), injected here by 1.21.11's Fabric API: getParticleMaterial's sprite */
    public TextureAtlasSprite getModelParticleSprite(BlockState state, BlockAndLightGetter view, BlockPos pos) {
        return CbFapiModels.getModelParticleSprite(this, state, view, pos);
    }
}
