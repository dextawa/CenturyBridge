package top.dext.centurybridge.shims.v26_2;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

/**
 * 1.20.3 changed Block.onBreak to return the state. Two directions repaired:
 * - legacy CALL SITES: the old void overload is re-added (empty body -- the
 *   TAIL hook below routes the real behavior);
 * - legacy OVERRIDES in mod block subclasses: they override the re-added void
 *   method, and the new-shape method invokes it virtually at TAIL, so mod
 *   break logic fires again. The empty base body prevents recursion.
 */
@Mixin(net.minecraft.world.level.block.Block.class)
public abstract class BlockBridge {

    /** legacy onBreak shape; intentionally empty at the base (see class doc) */
    public void playerWillDestroy(net.minecraft.world.level.Level world, net.minecraft.core.BlockPos pos, net.minecraft.world.level.block.state.BlockState state, net.minecraft.world.entity.player.Player player) {
    }

    @Inject(method = "playerWillDestroy", at = @At("TAIL"))
    private void cb$fireLegacyOnBreak(net.minecraft.world.level.Level world, net.minecraft.core.BlockPos pos, net.minecraft.world.level.block.state.BlockState state,
                                      net.minecraft.world.entity.player.Player player, CallbackInfoReturnable<net.minecraft.world.level.block.state.BlockState> cir) {
        this.playerWillDestroy(world, pos, state, player);
    }

}
