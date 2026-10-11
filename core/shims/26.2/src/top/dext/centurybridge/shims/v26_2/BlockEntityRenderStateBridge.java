package top.dext.centurybridge.shims.v26_2;

import net.minecraft.client.renderer.blockentity.state.BlockEntityRenderState;
import net.minecraft.world.level.block.entity.BlockEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import top.dext.centurybridge.rt.v26_2.CbRenderStateBlockEntity;

/** Every block entity render state remembers its block entity and partial tick. */
@Mixin(BlockEntityRenderState.class)
public abstract class BlockEntityRenderStateBridge implements CbRenderStateBlockEntity {

    @Unique
    private BlockEntity centurybridge$blockEntity;
    @Unique
    private float centurybridge$partialTick;

    @Override
    public BlockEntity centurybridge$blockEntity() {
        return centurybridge$blockEntity;
    }

    @Override
    public float centurybridge$partialTick() {
        return centurybridge$partialTick;
    }

    @Override
    public void centurybridge$capture(BlockEntity blockEntity, float partialTick) {
        this.centurybridge$blockEntity = blockEntity;
        this.centurybridge$partialTick = partialTick;
    }
}
