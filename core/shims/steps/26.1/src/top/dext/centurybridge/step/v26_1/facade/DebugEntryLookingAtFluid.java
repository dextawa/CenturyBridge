package top.dext.centurybridge.step.v26_1.facade;

import java.util.List;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.core.BlockPos;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.material.FluidState;

/** 1.21.11's DebugEntryLookingAtFluid (net/minecraft/class_11617): see {@link DebugEntryLookingAt} */
@Environment(EnvType.CLIENT)
public class DebugEntryLookingAtFluid extends DebugEntryLookingAt {

    public DebugEntryLookingAtFluid() {
        super("looking_at_fluid", true, "Targeted Fluid: ");
    }

    @Override
    void describe(Level level, BlockPos pos, List<String> lines) {
        FluidState state = level.getFluidState(pos);
        lines.add(String.valueOf(BuiltInRegistries.FLUID.getKey(state.getType())));
        properties(state.getValues(), lines);
        state.getType().builtInRegistryHolder().tags().map(DebugEntryLookingAt::tag).forEach(lines::add);
    }
}
