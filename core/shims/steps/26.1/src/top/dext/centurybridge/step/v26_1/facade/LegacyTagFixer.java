package top.dext.centurybridge.step.v26_1.facade;

import java.util.function.Supplier;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.level.ChunkPos;

/**
 * 1.21.11's LegacyTagFixer (net/minecraft/class_12242): a pre-datafixer pass a SimpleRegionStorage ran on each
 * chunk tag older than the current version before the data fixers (1.21.11's LegacyStructureDataHandler moved
 * pre-1.13 structure data into the chunk), told when a chunk was done, and naming the data version its output
 * stands at. 26.1 dropped it (that conversion is a world-upgrade file fix now); a 1.21.11 storage built with one
 * runs it as 1.21.11 did (rt/CbWallRegionStorage). 1.21.11's members, defaults and EMPTY.
 */
public interface LegacyTagFixer {

    /** 1.21.11 LegacyTagFixer.EMPTY: a fixer that changes nothing */
    Supplier<LegacyTagFixer> field_63959 = () -> tag -> tag;

    CompoundTag applyFix(CompoundTag tag);

    default void markChunkDone(ChunkPos pos) {
    }

    default int targetDataVersion() {
        return -1;
    }
}
