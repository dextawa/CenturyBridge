package top.dext.centurybridge.facade.v26_2;

import java.util.List;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.ServerLevelAccessor;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureTemplate;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructurePlaceSettings;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureProcessorType;

/**
 * Facade for 1.20.1 net/minecraft/class_3491 (StructureProcessor), an abstract
 * class legacy worldgen mods extend to transform structure blocks as they place.
 * Deleted/reshaped on 26.2. Reached only via superRenames (a mod subclass has
 * its extends + super-calls pointed here; every other class_3491 reference stays
 * on the real type). The facade mirrors the full method surface so the subclass
 * loads and its own calls resolve.
 *
 * process/finalizeProcessing default to pass-through (return the block info /
 * list unchanged) -- a legacy processor that is never actually invoked by 26.2's
 * reshaped template system degrades to a no-op rather than crashing the load.
 * getType stays abstract (the subclass supplies it; its StructureProcessorType
 * registration is a separate, ledgered concern).
 */
public abstract class class_3491 {

    public class_3491() {
    }

    /** process(level, offset, pos, current, original, settings) -> pass-through */
    public StructureTemplate.StructureBlockInfo method_15110(
            LevelReader level, BlockPos offset, BlockPos pos,
            StructureTemplate.StructureBlockInfo current,
            StructureTemplate.StructureBlockInfo original,
            StructurePlaceSettings settings) {
        return current;
    }

    /** getType(): abstract in vanilla, but some mod subclasses are themselves
     *  abstract / leave it to a sibling and never implement it. Declaring it
     *  abstract here turns those concrete subclasses into AbstractMethodError
     *  risks (CLAIMED ABSTRACT) the moment they load. A default returning null
     *  keeps them loadable; a processor whose type is null simply fails to
     *  register into 26.2's reshaped template system -- the usual ledgered
     *  degrade, not a load crash. */
    protected StructureProcessorType method_16772() {
        return null;
    }

    /** finalizeProcessing(level, offset, pos, original, processed, settings) -> pass-through */
    public List<StructureTemplate.StructureBlockInfo> method_49887(
            ServerLevelAccessor level, BlockPos offset, BlockPos pos,
            List<StructureTemplate.StructureBlockInfo> original,
            List<StructureTemplate.StructureBlockInfo> processed,
            StructurePlaceSettings settings) {
        return processed;
    }
}
