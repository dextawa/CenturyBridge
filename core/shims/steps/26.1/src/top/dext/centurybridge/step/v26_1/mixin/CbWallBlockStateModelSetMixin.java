package top.dext.centurybridge.step.v26_1.mixin;

import java.util.Map;
import net.minecraft.client.renderer.block.BlockStateModelSet;
import net.minecraft.client.renderer.block.dispatch.BlockStateModel;
import net.minecraft.world.level.block.state.BlockState;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Mutable;
import org.spongepowered.asm.mixin.Shadow;
import top.dext.centurybridge.step.v26_1.rt.CbWallModels;

/**
 * 1.21.11's live BlockModelShaper.replaceCache(map) on 26.1: the model manager's BlockStateModelSet takes
 * the map in place of its own, so every holder of the set answers from it, as every 1.21.11 reader of the
 * shaper did. get(state) stays 26.1's getOrDefault(state, missingModel).
 */
@Mixin(BlockStateModelSet.class)
public abstract class CbWallBlockStateModelSetMixin implements CbWallModels.ReplaceableModelSet {

    @Shadow
    @Final
    @Mutable
    private Map<BlockState, BlockStateModel> modelByState;

    @Override
    public void cb$replaceModels(Map<BlockState, BlockStateModel> models) {
        this.modelByState = models;
    }
}
