package top.dext.centurybridge.step.v26_1.mixin;

import java.util.function.Predicate;
import net.minecraft.core.BlockPos;
import net.minecraft.core.cauldron.CauldronInteraction;
import net.minecraft.core.cauldron.CauldronInteractions;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Invoker;

/** CauldronInteractions' non-public statics 1.21.11's CauldronInteraction had public (rt/CbWallCauldrons) */
@Mixin(CauldronInteractions.class)
public interface CbWallCauldronInteractionsAccess {

    @Invoker("newDispatcher")
    static CauldronInteraction.Dispatcher cb$newDispatcher(String name) {
        throw new AssertionError();
    }

    @Invoker("addDefaultInteractions")
    static void cb$addDefaultInteractions(CauldronInteraction.Dispatcher dispatcher) {
        throw new AssertionError();
    }

    @Invoker("fillBucket")
    static InteractionResult cb$fillBucket(BlockState state, Level level, BlockPos pos, Player player, InteractionHand hand, ItemStack emptyBucket,
        ItemStack filledBucket, Predicate<BlockState> canFill, SoundEvent sound) {
        throw new AssertionError();
    }

    @Invoker("emptyBucket")
    static InteractionResult cb$emptyBucket(Level level, BlockPos pos, Player player, InteractionHand hand, ItemStack filledBucket, BlockState state,
        SoundEvent sound) {
        throw new AssertionError();
    }
}
