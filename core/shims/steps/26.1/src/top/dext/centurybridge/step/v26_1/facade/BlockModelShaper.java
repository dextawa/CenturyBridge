package top.dext.centurybridge.step.v26_1.facade;

import java.util.Map;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.renderer.block.dispatch.BlockStateModel;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.resources.model.ModelManager;
import net.minecraft.world.level.block.state.BlockState;
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
 */
@Environment(EnvType.CLIENT)
public class BlockModelShaper {

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
}
