package top.dext.centurybridge.compat.fapi;

import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.HitResult;

/**
 * fabric-block-api-v1's BlockPickInteractionAware (7 corpus mods, 36 block classes
 * implement it). Gone in 26.x: pick-block goes through the vanilla
 * getCloneItemStack. The interface keeps the block classes loadable; the vanilla
 * path does not consult it (documented loss: custom pick results).
 */
public interface CbBlockPickInteractionAware {
    ItemStack getPickedStack(BlockState state, BlockGetter level, BlockPos pos, Player player, HitResult result);
}
