package top.dext.centurybridge.facade.v26_2;

import net.minecraft.world.level.block.HalfTransparentBlock;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.core.Direction;

/** Facade for 1.20.1 net/minecraft/class_4262 (AbstractGlassBlock), deleted on
 *  26.2; parent HalfTransparentBlock survives. Extends it so the mod glass block
 *  stays a genuine Block. The glass-specific overrides are given inert defaults
 *  so a subclass that calls super (or whose override 26.2 no longer invokes)
 *  still links. Member names are intermediary tokens. */
public abstract class class_4262 extends HalfTransparentBlock {
    protected class_4262(BlockBehaviour.Properties props) {
        super(props);
    }

    /** createBlockStateDefinition(builder) (method_9515) */
    protected void method_9515(StateDefinition.Builder<net.minecraft.world.level.block.Block, BlockState> builder) {
    }

    /** skipRendering(state, adjacent, dir) (method_9522) -> glass default true */
    protected boolean method_9522(BlockState state, BlockState adjacent, Direction dir) {
        return adjacent.getBlock() == this || super.skipRendering(state, adjacent, dir);
    }

    /** old use(state, level, pos, player, hand, hit) (method_9534) -> pass */
    protected net.minecraft.world.InteractionResult method_9534(BlockState state,
            net.minecraft.world.level.Level level, net.minecraft.core.BlockPos pos,
            net.minecraft.world.entity.player.Player player, net.minecraft.world.InteractionHand hand,
            net.minecraft.world.phys.BlockHitResult hit) {
        return net.minecraft.world.InteractionResult.PASS;
    }
}
