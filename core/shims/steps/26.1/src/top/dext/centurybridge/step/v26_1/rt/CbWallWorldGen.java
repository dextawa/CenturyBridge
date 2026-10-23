package top.dext.centurybridge.step.v26_1.rt;

import net.minecraft.world.level.LevelSimulatedReader;
import net.minecraft.world.level.WorldGenLevel;

/**
 * The WorldGenLevel 26.1's tree placers take where 1.21.11's took a LevelSimulatedReader. 1.21.11's tree
 * feature always handed them its WorldGenLevel; a caller passing any other reader is named, since 26.1 has
 * no place for it.
 */
public final class CbWallWorldGen {

    private CbWallWorldGen() {
    }

    /** the argument as the WorldGenLevel 1.21.11's tree code always passed */
    public static WorldGenLevel worldGenLevel(LevelSimulatedReader reader) {
        if (reader == null || reader instanceof WorldGenLevel) {
            return (WorldGenLevel) reader;
        }
        throw new IllegalArgumentException("26.1 tree placement needs a WorldGenLevel; 1.21.11 code passed " + reader.getClass().getName());
    }
}
