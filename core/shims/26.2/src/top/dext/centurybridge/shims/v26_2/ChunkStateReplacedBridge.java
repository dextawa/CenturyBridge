package top.dext.centurybridge.shims.v26_2;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.chunk.LevelChunk;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyVariable;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import top.dext.centurybridge.rt.v26_2.CbLegacyStateReplaced;

/**
 * 1.20.1 WorldChunk.setBlockState called the old block's onStateReplaced(state, world,
 * pos, newState, moved) on the server, once the new state was in the section and before
 * anything happened to the block entity: the override dropped the inventory, and its
 * super call removed the block entity. 1.21.5 split that into BlockEntity
 * preRemoveSideEffects (before removal) and affectNeighborsAfterRemoval (after, without
 * the new state), so an old override is never called and a container block takes its
 * items with it.
 *
 * For a converted block that overrides the old shape, the old call is made again at the
 * old point. The block entity is then gone when the block changed, as the old super call
 * left it: 1.20.1 block entities never dropped anything themselves, and the 26.2
 * preRemoveSideEffects a mod block entity inherits from a vanilla container would
 * otherwise drop the same items a second time.
 */
@Mixin(LevelChunk.class)
public abstract class ChunkStateReplacedBridge {

    @Shadow
    @Final
    Level level;

    @Unique
    private static final ThreadLocal<BlockState> CB_REPLACED = new ThreadLocal<>();

    /** the state being replaced (local 10, stored from LevelChunkSection.setBlockState) */
    @ModifyVariable(method = "setBlockState", at = @At("STORE"), index = 10)
    private BlockState cb$capture(BlockState old) {
        CB_REPLACED.set(old);
        return old;
    }

    @Inject(method = "setBlockState", at = @At(value = "INVOKE",
        target = "Lnet/minecraft/world/level/block/state/BlockState;is(Ljava/lang/Object;)Z", ordinal = 0))
    private void cb$legacyStateReplaced(BlockPos pos, BlockState state, int flags,
            CallbackInfoReturnable<BlockState> cir) {
        BlockState old = CB_REPLACED.get();
        CB_REPLACED.remove();
        if (old == null || this.level.isClientSide() || !(old.getBlock() instanceof CbLegacyStateReplaced legacy)) {
            return;
        }
        legacy.method_9536(old, this.level, pos, state, (flags & 64) != 0);
        if (!old.is(state.getBlock())) {
            ((LevelChunk) (Object) this).removeBlockEntity(pos);
        }
    }
}
