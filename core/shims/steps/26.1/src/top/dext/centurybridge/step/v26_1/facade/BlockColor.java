package top.dext.centurybridge.step.v26_1.facade;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.BlockAndLightGetter;
import net.minecraft.world.level.block.state.BlockState;

/**
 * 1.21.11's BlockColor (net/minecraft/class_322) on 26.1, which replaced it with a list of BlockTintSource per block
 * (one source per tint index). rt/CbFapiBlockColors converts between the two. The getter is 1.21.11's
 * BlockAndTintGetter as the wall step names it (BlockAndLightGetter); null when 1.21.11 asked without a world.
 */
@Environment(EnvType.CLIENT)
@FunctionalInterface
public interface BlockColor {
    int getColor(BlockState state, BlockAndLightGetter level, BlockPos pos, int tintIndex);
}
