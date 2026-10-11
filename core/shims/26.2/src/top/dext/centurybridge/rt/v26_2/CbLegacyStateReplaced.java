package top.dext.centurybridge.rt.v26_2;

/**
 * The 1.20.1 AbstractBlock.onStateReplaced(state, world, pos, newState, moved) shape,
 * added to every converted block class that overrides it (see ChunkStateReplacedBridge).
 */
public interface CbLegacyStateReplaced {

    /** onStateReplaced(state, world, pos, newState, moved) */
    void method_9536(net.minecraft.world.level.block.state.BlockState state, net.minecraft.world.level.Level world,
            net.minecraft.core.BlockPos pos, net.minecraft.world.level.block.state.BlockState newState, boolean moved);
}
