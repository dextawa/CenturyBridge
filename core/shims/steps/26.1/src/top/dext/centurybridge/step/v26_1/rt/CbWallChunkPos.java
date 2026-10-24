package top.dext.centurybridge.step.v26_1.rt;

import net.minecraft.core.BlockPos;
import net.minecraft.core.SectionPos;

/**
 * 1.21.11 ChunkPos's other constructors for a subclass's super(..) (26.1's record has only ChunkPos(x, z)): the
 * coordinates 1.21.11's bodies computed.
 */
public final class CbWallChunkPos {

    private CbWallChunkPos() {
    }

    /** 1.21.11 ChunkPos(BlockPos): x = SectionPos.blockToSectionCoord(pos.getX()) */
    public static int blockX(BlockPos pos) {
        return SectionPos.blockToSectionCoord(pos.getX());
    }

    /** 1.21.11 ChunkPos(BlockPos): z = SectionPos.blockToSectionCoord(pos.getZ()) */
    public static int blockZ(BlockPos pos) {
        return SectionPos.blockToSectionCoord(pos.getZ());
    }

    /** 1.21.11 ChunkPos(long): x = (int) packed */
    public static int packedX(long packed) {
        return (int) packed;
    }

    /** 1.21.11 ChunkPos(long): z = (int) (packed >> 32) */
    public static int packedZ(long packed) {
        return (int) (packed >> 32);
    }
}
