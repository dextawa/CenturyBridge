package top.dext.centurybridge.shims.v26_2;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

/**
 * getPickStack narrowed its world parameter at 1.20.3 and went protected with
 * a trailing includeData flag at 26.2. The legacy overload lives here on
 * net.minecraft.world.level.block.state.BlockBehaviour -- where the modern method is DECLARED -- because @Shadow does
 * not walk the hierarchy; legacy call sites naming net.minecraft.world.level.block.Block still resolve
 * up to this overload. Legacy pick never carried block-entity data.
 */
@Mixin(net.minecraft.world.level.block.state.BlockBehaviour.class)
public abstract class AbstractBlockBridge {

    @Shadow
    protected abstract net.minecraft.world.item.ItemStack getCloneItemStack(net.minecraft.world.level.LevelReader world, net.minecraft.core.BlockPos pos, net.minecraft.world.level.block.state.BlockState state, boolean includeData);

    /** legacy getPickStack shape (world param narrowed at 1.20.3) */
    public net.minecraft.world.item.ItemStack getCloneItemStack(net.minecraft.world.level.BlockGetter world, net.minecraft.core.BlockPos pos, net.minecraft.world.level.block.state.BlockState state) {
        return this.getCloneItemStack((net.minecraft.world.level.LevelReader) world, pos, state, false);
    }
}
