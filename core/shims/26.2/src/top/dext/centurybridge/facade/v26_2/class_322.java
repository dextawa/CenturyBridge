package top.dext.centurybridge.facade.v26_2;

import net.minecraft.core.BlockPos;
import net.minecraft.client.renderer.block.BlockAndTintGetter;
import net.minecraft.world.level.block.state.BlockState;

/**
 * BlockColorProvider (class_322 / BlockColor) as the corpus implements it: the
 * per-tint-index callback vanilla dropped in 26.1 for BlockTintSource lists.
 * 7 corpus mods implement it (lambdas and WrappedBlockColor classes) and hand
 * it to Fabric's ColorProviderRegistry.BLOCK, which the compat layer turns into
 * a BlockTintsFactory (see CbColorProviderRegistry). The SAM name getColor is unobfuscated
 * in 1.20.1 (no intermediary member), so the corpus uses it verbatim.
 */
@FunctionalInterface
@net.fabricmc.api.Environment(net.fabricmc.api.EnvType.CLIENT)
public interface class_322 {
    /** was BlockColorProvider.getColor(state, world, pos, tintIndex) */
    int getColor(BlockState state, BlockAndTintGetter level, BlockPos pos, int tintIndex);
}
