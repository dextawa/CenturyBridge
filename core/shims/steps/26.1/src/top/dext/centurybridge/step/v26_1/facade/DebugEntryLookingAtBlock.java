package top.dext.centurybridge.step.v26_1.facade;

import java.util.List;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.core.BlockPos;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;

/** 1.21.11's DebugEntryLookingAtBlock (net/minecraft/class_11615): see {@link DebugEntryLookingAt} */
@Environment(EnvType.CLIENT)
public class DebugEntryLookingAtBlock extends DebugEntryLookingAt {

    public DebugEntryLookingAtBlock() {
        super("looking_at_block", false, "Targeted Block: ");
    }

    @Override
    void describe(Level level, BlockPos pos, List<String> lines) {
        BlockState state = level.getBlockState(pos);
        lines.add(String.valueOf(BuiltInRegistries.BLOCK.getKey(state.getBlock())));
        properties(state.getValues(), lines);
        state.getBlock().builtInRegistryHolder().tags().map(DebugEntryLookingAt::tag).forEach(lines::add);
    }
}
