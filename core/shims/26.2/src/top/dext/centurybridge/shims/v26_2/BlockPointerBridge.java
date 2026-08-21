package top.dext.centurybridge.shims.v26_2;

import org.spongepowered.asm.mixin.Mixin;

/**
 * 1.20.1 -> 1.20.2: BlockPointer became a record; the old accessors map to
 * record components. Careful: old method_10120 returned the BLOCK STATE, not
 * the position. Work order: L3 x~20 each.
 */
@Mixin(net.minecraft.core.dispenser.BlockSource.class)
public abstract class BlockPointerBridge {

    private net.minecraft.core.dispenser.BlockSource self() {
        return (net.minecraft.core.dispenser.BlockSource) (Object) this;
    }

    public net.minecraft.core.BlockPos method_10122() {
        return self().pos();
    }

    public net.minecraft.world.level.block.state.BlockState method_10120() {
        return self().state();
    }

    @SuppressWarnings("unchecked")
    public <T extends net.minecraft.world.level.block.entity.BlockEntity> T method_10121() {
        return (T) (net.minecraft.world.level.block.entity.BlockEntity) self().blockEntity();
    }

    public net.minecraft.server.level.ServerLevel method_10207() {
        return self().level();
    }
}
