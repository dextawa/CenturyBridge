package top.dext.centurybridge.rt.v26_2;

import net.minecraft.world.level.block.entity.BlockEntity;

/**
 * The block entity a block entity render state was extracted from, and the partial tick it
 * was extracted at (BlockEntityRenderDispatcherCaptureBridge). 1.20.1 renderers took the
 * block entity itself.
 */
public interface CbRenderStateBlockEntity {

    BlockEntity centurybridge$blockEntity();

    float centurybridge$partialTick();

    void centurybridge$capture(BlockEntity blockEntity, float partialTick);
}
