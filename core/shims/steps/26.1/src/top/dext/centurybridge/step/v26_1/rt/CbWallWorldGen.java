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

    /**
     * a mod's call of 1.21.11's BlockStateProvider.getState(random, pos): 26.1's with no level -- 1.21.11's providers
     * had none, and 26.1's providers that existed in 1.21.11 never read it (RandomizedIntStateProvider only hands it to
     * its source); a rule-based provider, new in 26.1 and testing blocks in the level, cannot answer without one
     */
    public static net.minecraft.world.level.block.state.BlockState getState(
        net.minecraft.world.level.levelgen.feature.stateproviders.BlockStateProvider provider, net.minecraft.util.RandomSource random,
        net.minecraft.core.BlockPos pos) {
        if (provider instanceof net.minecraft.world.level.levelgen.feature.stateproviders.RuleBasedStateProvider) {
            throw new IllegalStateException("1.21.11 BlockStateProvider.getState(random, pos) on 26.1's rule-based provider, which tests the level 1.21.11's call has none of");
        }
        return provider.getState(null, random, pos);
    }
}
